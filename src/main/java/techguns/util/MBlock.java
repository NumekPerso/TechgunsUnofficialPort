package techguns.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import techguns.world.BlockData;
import techguns.world.BlockRotator;
import techguns.world.EnumLootType;
import techguns.world.LegacyBlocks;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

/**
 * Block used by the structure generation (1.12 Block + metadata). 1.20: the 1.12 registry name + meta constructors
 * convert to flattened block states with LegacyBlocks, the other constructors take the 1.20 state.
 * Not serializable anymore (dungeon templates are text files, see DungeonTemplate).
 */
public class MBlock {

	public Block block;

	protected BlockState state;

	protected boolean hasTileEntity = false;

	protected int layer = 0;

	public MBlock(MBlock other) {
		this.block = other.block;
		this.state = other.state;
		this.hasTileEntity = other.hasTileEntity;
		this.layer = other.layer;
	}

	public MBlock(BlockState state) {
		this(state, false);
	}

	public MBlock(BlockState state, boolean hasTileEnt) {
		this.block = state.getBlock();
		this.state = state;
		this.hasTileEntity = hasTileEnt;
		this.layer = BlockData.getBlockLayer(this);
	}

	/**
	 * 1.12 block registry name (e.g. "minecraft:stonebrick", "techguns:lamp0") and metadata
	 */
	public MBlock(String registryname, int meta) {
		this(LegacyBlocks.get(registryname, meta), false);
	}

	public MBlock(String registryname, int meta, boolean hasTileEnt) {
		this(LegacyBlocks.get(registryname, meta), hasTileEnt);
	}

	/**
	 * 1.20 block state string (e.g. "minecraft:oak_stairs[facing=east]"), used by the BuildingScanTool output
	 */
	public static MBlock of(String stateString) {
		try {
			return new MBlock(net.minecraft.commands.arguments.blocks.BlockStateParser.parseForBlock(net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(), stateString, false).blockState());
		} catch (Exception e) {
			return new MBlock(Blocks.AIR.defaultBlockState());
		}
	}

	public MBlock(Block block) {
		this(block.defaultBlockState());
	}

	public BlockState getState() {
		return state;
	}

	public int getPass() {
		return MathUtil.clamp(layer - 1, 0, 1);
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof MBlock) && ((MBlock) other).state == this.state;
	}

	@Override
	public int hashCode() {
		return state.hashCode();
	}

	public void tileEntityPostPlacementAction(LevelAccessor w, BlockState state, BlockPos p, int rotation) {

	}

	public boolean hasTileEntity() {
		return hasTileEntity;
	}

	protected int getPlacementFlags() {
		return 2;
	}

	public void setBlock(LevelAccessor w, MutableBlockPos pos, int rotation) {
		setBlock(w, pos, rotation, null, BiomeColorType.WOODLAND);
	}

	public void setBlock(LevelAccessor w, MutableBlockPos pos, int rotation, EnumLootType loottype, BiomeColorType biome) {
		if (pos.getY() > w.getMinBuildHeight()) {
			BlockState targetState = BlockRotator.getRotatedHorizontal(state, rotation);
			placeState(w, pos, targetState, getPlacementFlags());
			if (this.hasTileEntity) {
				this.tileEntityPostPlacementAction(w, targetState, pos, rotation);
			}
		}
	}

	public void setBlockReplaceableOnly(LevelAccessor w, MutableBlockPos pos, int rotation, EnumLootType loottype, BiomeColorType biome) {
		BlockState bs = w.getBlockState(pos);
		if (bs.canBeReplaced()) {
			setBlock(w, pos, rotation, loottype, biome);
		}
	}

	/**
	 * 1.20: sets the state, door halves are made consistent (1.12 doors stored the facing in the lower half and the
	 * hinge in the upper half)
	 */
	public static void placeState(LevelAccessor w, BlockPos pos, BlockState targetState, int flags) {
		if (targetState.getBlock() instanceof DoorBlock) {
			if (targetState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
				BlockState below = w.getBlockState(pos.below());
				if (below.getBlock() == targetState.getBlock() && below.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
					targetState = targetState.setValue(DoorBlock.FACING, below.getValue(DoorBlock.FACING)).setValue(DoorBlock.OPEN, below.getValue(DoorBlock.OPEN));
					w.setBlock(pos.below(), below.setValue(DoorBlock.HINGE, targetState.getValue(DoorBlock.HINGE)), flags);
				}
			} else {
				BlockState above = w.getBlockState(pos.above());
				if (above.getBlock() == targetState.getBlock() && above.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
					targetState = targetState.setValue(DoorBlock.HINGE, above.getValue(DoorBlock.HINGE));
					w.setBlock(pos.above(), above.setValue(DoorBlock.FACING, targetState.getValue(DoorBlock.FACING)).setValue(DoorBlock.OPEN, targetState.getValue(DoorBlock.OPEN)), flags);
				}
			}
		} else if (targetState.hasProperty(BlockStateProperties.WATERLOGGED) && targetState.getValue(BlockStateProperties.WATERLOGGED)) {
			targetState = targetState.setValue(BlockStateProperties.WATERLOGGED, false);
		}
		w.setBlock(pos, targetState, flags);
	}

	public static BlockState air() {
		return Blocks.AIR.defaultBlockState();
	}
}
