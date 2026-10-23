package techguns.world;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import techguns.blocks.BlockSandbags;
import techguns.blocks.BlockTGCamoNet;
import techguns.blocks.BlockTGCamoNetTop;

/**
 * 1.20: the level a Techguns structure is generated in during world generation.
 * <p>
 * The 1.12 structures were generated at once by an IWorldGenerator. In 1.20 a structure piece is generated chunk by
 * chunk (StructurePiece.postProcess), so the whole 1.12 generation code is run again for every chunk the structure
 * touches, with the same random seed, and this level only writes the blocks inside the current chunk (writeBox).
 * <p>
 * To get the same result in every chunk, the code must see the same world everywhere: all writes of the current run
 * are kept in an overlay (reads see them, also outside of the current chunk), the terrain heights come from the
 * HeightField computed from the generator noise at structure start (not the real world, which differs depending on
 * the order chunks are generated in), blocks above the original surface read as air (trees, plants: the junk removal
 * uses getRealBlockState), blocks below the surface are the real blocks inside the current chunk and the default
 * block/fluid of the dimension outside. Random block choices use posRandom (seeded by structure seed + position).
 */
public class TGStructureLevel implements WorldGenLevel {

	protected final WorldGenLevel level;
	protected final BoundingBox writeBox;
	protected final long seed;
	@Nullable
	protected final HeightField heights;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;

	protected final Long2ObjectOpenHashMap<BlockState> overlay = new Long2ObjectOpenHashMap<>();
	protected final Long2IntOpenHashMap tops = new Long2IntOpenHashMap();

	protected final RandomSource random;

	public TGStructureLevel(WorldGenLevel level, BoundingBox writeBox, long seed, @Nullable HeightField heights, BlockState defaultBlock, BlockState defaultFluid) {
		this.level = level;
		this.writeBox = writeBox;
		this.seed = seed;
		this.heights = heights;
		this.defaultBlock = defaultBlock;
		this.defaultFluid = defaultFluid;
		this.random = RandomSource.create(seed);
	}

	public long getStructureSeed() {
		return seed;
	}

	public WorldGenLevel getWrappedLevel() {
		return level;
	}

	/**
	 * Random seeded with the structure seed and the position: same value in every chunk generation run
	 */
	public static Random posRandom(LevelAccessor w, BlockPos pos) {
		long s = w instanceof TGStructureLevel t ? t.seed : w.getRandom().nextLong();
		return new Random(s ^ (pos.asLong() * 0x5DEECE66DL) ^ 0x2545F4914F6CDD1DL);
	}

	public static RandomSource posRandomSource(LevelAccessor w, BlockPos pos) {
		return RandomSource.create(posRandom(w, pos).nextLong());
	}

	public boolean isInWriteBox(int x, int z) {
		return x >= writeBox.minX() && x <= writeBox.maxX() && z >= writeBox.minZ() && z <= writeBox.maxZ();
	}

	public boolean isInWriteBox(BlockPos pos) {
		return isInWriteBox(pos.getX(), pos.getZ());
	}

	protected boolean canReadReal(int x, int z) {
		return isInWriteBox(x, z) || level.hasChunk(x >> 4, z >> 4);
	}

	/**
	 * The real block (inside the current chunk), used to remove junk (trees, plants) above the ground
	 */
	public BlockState getRealBlockState(BlockPos pos) {
		if (isInWriteBox(pos)) {
			return level.getBlockState(pos);
		}
		return getBlockState(pos);
	}

	/**
	 * Real height (WORLD_SURFACE) inside the current chunk, virtual height elsewhere
	 */
	public int getRealHeight(int x, int z) {
		if (isInWriteBox(x, z)) {
			return Math.max(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), getVirtualTop(x, z));
		}
		return getVirtualTop(x, z);
	}

	protected BlockState getBaseState(BlockPos pos) {
		int x = pos.getX();
		int z = pos.getZ();
		int y = pos.getY();
		if (heights != null && heights.contains(x, z)) {
			int surface = heights.surface(x, z);
			if (y >= surface) {
				return Blocks.AIR.defaultBlockState();
			}
			if (isInWriteBox(x, z)) {
				return level.getBlockState(pos);
			}
			return y >= heights.floor(x, z) ? defaultFluid : defaultBlock;
		}
		if (canReadReal(x, z)) {
			return level.getBlockState(pos);
		}
		return Blocks.AIR.defaultBlockState();
	}

	protected int getBaseTop(int x, int z) {
		if (heights != null && heights.contains(x, z)) {
			return heights.surface(x, z);
		}
		if (canReadReal(x, z)) {
			return level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
		}
		return level.getSeaLevel();
	}

	public int getVirtualTop(int x, int z) {
		long key = ChunkPos.asLong(x, z);
		if (tops.containsKey(key)) {
			return tops.get(key);
		}
		return getBaseTop(x, z);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (this.isOutsideBuildHeight(pos)) {
			return Blocks.VOID_AIR.defaultBlockState();
		}
		BlockState s = overlay.get(pos.asLong());
		if (s != null) {
			return s;
		}
		return getBaseState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return getBlockState(pos).getFluidState();
	}

	@Override
	public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
		if (this.isOutsideBuildHeight(pos)) {
			return false;
		}
		overlay.put(pos.asLong(), state);
		updateTop(pos, state);
		if (writeBox.isInside(pos)) {
			boolean r = level.setBlock(pos, state, flags & ~1, recursionLeft);
			if (needsShapeUpdate(state)) {
				level.getChunk(pos).markPosForPostprocessing(pos);
			}
			return r;
		}
		return true;
	}

	protected static boolean needsShapeUpdate(BlockState state) {
		Block b = state.getBlock();
		return b instanceof CrossCollisionBlock || b instanceof WallBlock || b instanceof StairBlock || b instanceof RedStoneWireBlock
				|| b instanceof BlockSandbags || b instanceof BlockTGCamoNet || b instanceof BlockTGCamoNetTop;
	}

	protected void updateTop(BlockPos pos, BlockState state) {
		int x = pos.getX();
		int z = pos.getZ();
		int y = pos.getY();
		int top = getVirtualTop(x, z);
		long key = ChunkPos.asLong(x, z);
		if (!state.isAir()) {
			if (y >= top) {
				tops.put(key, y + 1);
			}
		} else if (y == top - 1) {
			BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, y - 1, z);
			int limit = 0;
			while (p.getY() > getMinBuildHeight() && limit++ < 128 && getBlockState(p).isAir()) {
				p.move(Direction.DOWN);
			}
			tops.put(key, p.getY() + 1);
		}
	}

	@Override
	public int getHeight(Heightmap.Types type, int x, int z) {
		return getVirtualTop(x, z);
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean isMoving) {
		return setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
	}

	@Override
	public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft) {
		return setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
	}

	@Override
	public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> predicate) {
		return predicate.test(getBlockState(pos));
	}

	@Override
	public boolean isFluidAtPosition(BlockPos pos, Predicate<FluidState> predicate) {
		return predicate.test(getFluidState(pos));
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		if (writeBox.isInside(pos)) {
			return level.getBlockEntity(pos);
		}
		return null;
	}

	@Override
	public boolean addFreshEntity(Entity entity) {
		if (isInWriteBox(entity.blockPosition())) {
			return level.addFreshEntity(entity);
		}
		return false;
	}

	// ---- delegates ----

	@Override
	public long getSeed() {
		return level.getSeed();
	}

	@Override
	public ServerLevel getLevel() {
		return level.getLevel();
	}

	@Override
	public long nextSubTickCount() {
		return level.nextSubTickCount();
	}

	@Override
	public LevelTickAccess<Block> getBlockTicks() {
		return level.getBlockTicks();
	}

	@Override
	public LevelTickAccess<Fluid> getFluidTicks() {
		return level.getFluidTicks();
	}

	@Override
	public LevelData getLevelData() {
		return level.getLevelData();
	}

	@Override
	public DifficultyInstance getCurrentDifficultyAt(BlockPos pos) {
		return level.getCurrentDifficultyAt(pos);
	}

	@Nullable
	@Override
	public MinecraftServer getServer() {
		return level.getServer();
	}

	@Override
	public ChunkSource getChunkSource() {
		return level.getChunkSource();
	}

	@Override
	public RandomSource getRandom() {
		return random;
	}

	@Override
	public void playSound(@Nullable Player player, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
	}

	@Override
	public void addParticle(ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
	}

	@Override
	public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
	}

	@Override
	public void gameEvent(GameEvent event, Vec3 pos, GameEvent.Context context) {
	}

	@Override
	public float getShade(Direction direction, boolean shade) {
		return level.getShade(direction, shade);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return level.getLightEngine();
	}

	@Override
	public WorldBorder getWorldBorder() {
		return level.getWorldBorder();
	}

	@Override
	public List<Entity> getEntities(@Nullable Entity entity, AABB area, Predicate<? super Entity> predicate) {
		return level.getEntities(entity, area, predicate);
	}

	@Override
	public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> test, AABB area, Predicate<? super T> predicate) {
		return level.getEntities(test, area, predicate);
	}

	@Override
	public List<? extends Player> players() {
		return level.players();
	}

	@Nullable
	@Override
	public ChunkAccess getChunk(int x, int z, ChunkStatus status, boolean require) {
		return level.getChunk(x, z, status, require);
	}

	@Override
	public boolean hasChunk(int x, int z) {
		return level.hasChunk(x, z);
	}

	@Override
	public int getSkyDarken() {
		return level.getSkyDarken();
	}

	@Override
	public BiomeManager getBiomeManager() {
		return level.getBiomeManager();
	}

	@Override
	public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
		return level.getUncachedNoiseBiome(x, y, z);
	}

	@Override
	public boolean isClientSide() {
		return false;
	}

	@Override
	public int getSeaLevel() {
		return level.getSeaLevel();
	}

	@Override
	public DimensionType dimensionType() {
		return level.dimensionType();
	}

	@Override
	public RegistryAccess registryAccess() {
		return level.registryAccess();
	}

	@Override
	public FeatureFlagSet enabledFeatures() {
		return level.enabledFeatures();
	}

}
