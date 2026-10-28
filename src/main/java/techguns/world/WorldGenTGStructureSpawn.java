package techguns.world;

import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.common.Tags;
import techguns.TGConfig;
import techguns.world.structures.WorldgenStructure;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

/**
 * 1.12 WorldGenTGStructureSpawn (IWorldGenerator spawning the Techguns structures) is now a vanilla Structure type
 * "techguns:tg_structure": each Techguns structure is a datapack structure (data/techguns/worldgen/structure) with the
 * name of its TGStructureSpawnRegister entry. findGenerationPoint runs the 1.12 spawn checks (findSpawn), the single
 * TGStructurePiece generates the structure chunk by chunk.
 */
public class WorldGenTGStructureSpawn extends Structure {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final Codec<WorldGenTGStructureSpawn> CODEC = RecordCodecBuilder.create(i -> i.group(settingsCodec(i),
			Codec.STRING.fieldOf("structure").forGetter(s -> s.structureName)).apply(i, WorldGenTGStructureSpawn::new));

	protected final String structureName;

	public WorldGenTGStructureSpawn(StructureSettings settings, String structureName) {
		super(settings);
		this.structureName = structureName;
	}

	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
		if (!TGConfig.doWorldspawn) {
			return Optional.empty();
		}
		TGStructureSpawn spawn = TGStructureSpawnRegister.get(structureName);
		if (spawn == null || !spawn.isEnabled()) {
			return Optional.empty();
		}
		int cx = ctx.chunkPos().getMiddleBlockX();
		int cz = ctx.chunkPos().getMiddleBlockZ();
		int testY = spawn.nether ? 64 : ctx.chunkGenerator().getSeaLevel();
		Holder<Biome> biome = ctx.biomeSource().getNoiseBiome(QuartPos.fromBlock(cx), QuartPos.fromBlock(testY), QuartPos.fromBlock(cz), ctx.randomState().sampler());
		if (!ctx.validBiome().test(biome)) {
			return Optional.empty();
		}

		Optional<GenerationStub> stub = findSpawnPoint(ctx, spawn);
		if (stub.isPresent() && !NESTED.get() && !isSelectedInSet(ctx, spawn)) {
			return Optional.empty();
		}
		return stub;
	}

	/**
	 * Set while checking the other structures of the set (isSelectedInSet)
	 */
	private static final ThreadLocal<Boolean> NESTED = ThreadLocal.withInitial(() -> false);

	/**
	 * The structure sets contain several structures with overlapping biomes. Vanilla picks one of them by weight (and
	 * tries the next one when it can't generate), but /locate (StructureCheck) only checks if this structure could
	 * generate. To get correct /locate results, the vanilla choice of ChunkGenerator.createStructures is done here too.
	 */
	protected boolean isSelectedInSet(GenerationContext ctx, TGStructureSpawn spawn) {
		if (!spawn.inSet) {
			return true;
		}
		TGStructurePlacement placement = TGStructurePlacement.forSize(spawn.size);
		if (!placement.getPotentialStructureChunk(ctx.seed(), ctx.chunkPos().x, ctx.chunkPos().z).equals(ctx.chunkPos())) {
			// not a chunk of the structure set: /place
			return true;
		}
		java.util.List<TGStructureSpawn> list = TGStructureSpawnRegister.getSet(spawn.size);
		WorldgenRandom rand = new WorldgenRandom(new LegacyRandomSource(0L));
		rand.setLargeFeatureSeed(ctx.seed(), ctx.chunkPos().x, ctx.chunkPos().z);
		int total = 0;
		for (TGStructureSpawn s : list) {
			total += s.spawnWeight;
		}
		net.minecraft.core.Registry<Structure> registry = ctx.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE);
		while (!list.isEmpty()) {
			int j = rand.nextInt(total);
			int k = 0;
			for (TGStructureSpawn s : list) {
				j -= s.spawnWeight;
				if (j < 0) {
					break;
				}
				k++;
			}
			TGStructureSpawn entry = list.get(k);
			if (entry == spawn) {
				return true;
			}
			Structure other = registry.get(new net.minecraft.resources.ResourceLocation(techguns.Techguns.MODID, entry.name));
			if (other != null) {
				GenerationContext c2 = new GenerationContext(ctx.registryAccess(), ctx.chunkGenerator(), ctx.biomeSource(), ctx.randomState(), ctx.structureTemplateManager(),
						ctx.seed(), ctx.chunkPos(), ctx.heightAccessor(), other.biomes()::contains);
				boolean nested = NESTED.get();
				NESTED.set(true);
				try {
					if (other.findValidGenerationPoint(c2).isPresent()) {
						return false;
					}
				} finally {
					NESTED.set(nested);
				}
			}
			list.remove(k);
			total -= entry.spawnWeight;
		}
		return false;
	}

	protected Optional<GenerationStub> findSpawnPoint(GenerationContext ctx, TGStructureSpawn spawn) {
		try {
			WorldgenStructure s = spawn.create();
			// own random (not ctx.random(), the other structures of the set may be checked with the same context)
			WorldgenRandom wr = new WorldgenRandom(new LegacyRandomSource(0L));
			wr.setLargeFeatureSeed(ctx.seed(), ctx.chunkPos().x, ctx.chunkPos().z);
			Random rnd = new Random(wr.nextLong() ^ structureName.hashCode());
			int sizeX = s.getSizeX(rnd);
			int sizeZ = s.getSizeZ(rnd);
			int sizeY = s.getSizeY(rnd);

			TGSpawnContext sctx = new TGSpawnContext(ctx.chunkGenerator(), ctx.heightAccessor(), ctx.randomState());
			TGSpawnData data;
			if (spawn.nether) {
				data = s.findSpawnCave(sctx, ctx.chunkPos().x, ctx.chunkPos().z, sizeX, sizeY, sizeZ, rnd);
			} else {
				data = s.findSpawn(sctx, ctx.chunkPos().x, ctx.chunkPos().z, sizeX, sizeY, sizeZ, rnd);
			}
			if (data == null) {
				return Optional.empty();
			}
			data.seed = rnd.nextLong();

			BlockPos center = new BlockPos(data.x + data.sizeX / 2, data.y + 1, data.z + data.sizeZ / 2);
			Holder<Biome> b = ctx.biomeSource().getNoiseBiome(QuartPos.fromBlock(center.getX()), QuartPos.fromBlock(center.getY()), QuartPos.fromBlock(center.getZ()),
					ctx.randomState().sampler());
			if (!ctx.validBiome().test(b)) {
				return Optional.empty();
			}
			data.color = getBiomeColorTypeFromBiome(b);

			BoundingBox box = s.getBoundingBox(data);
			boolean heights = !spawn.nether && s.needsHeightField();
			// the pieces (and the height field) are only created when the structure is really generated, not for
			// the structure presence checks of /locate
			return Optional.of(new GenerationStub(center, builder -> {
				if (heights) {
					data.heights = sctx.createHeightField(box);
				}
				builder.addPiece(new TGStructurePiece(structureName, data, box));
			}));
		} catch (Exception e) {
			LOGGER.error("Techguns: error while placing structure {}", structureName, e);
			return Optional.empty();
		}
	}

	public static BiomeColorType getBiomeColorTypeFromBiome(Holder<Biome> biome) {
		//1.12 also used the cold biomes (taiga, windswept hills...): structures built of snow blocks without snow around
		if (biome.is(Tags.Biomes.IS_SNOWY)) {
			return BiomeColorType.SNOW;
		} else if (biome.is(Tags.Biomes.IS_SANDY) || biome.is(BiomeTags.IS_BEACH) || biome.is(BiomeTags.IS_BADLANDS)) {
			return BiomeColorType.DESERT;
		} else if (biome.is(BiomeTags.IS_NETHER)) {
			return BiomeColorType.NETHER;
		}
		return BiomeColorType.WOODLAND;
	}

	@Override
	public StructureType<?> type() {
		return TGWorldgen.TG_STRUCTURE_TYPE;
	}
}
