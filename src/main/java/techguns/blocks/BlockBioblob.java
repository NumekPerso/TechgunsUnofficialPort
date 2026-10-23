package techguns.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import techguns.TGBlocks;
import techguns.tileentities.BioBlobTileEnt;

/**
 * Bio blob placed by the bio gun projectiles. The size was read from the tile entity in 1.12 (getActualState),
 * it is now a block state property kept in sync by BioBlobTileEnt.
 */
public class BlockBioblob extends GenericBlock implements EntityBlock {

	public static final DirectionProperty FACING_ALL = DirectionalBlock.FACING;

	public static final int SIZE_2_W = 1;
	public static final int SIZE_2_H = 5;
	public static final int SIZE_1_W = 3;
	public static final int SIZE_1_H = 4;
	public static final int SIZE_0_W = 5;
	public static final int SIZE_0_H = 3;

	public static final double FSIZE = 0.0625f;

	public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 2);

	public BlockBioblob() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.SLIME_BLOCK).strength(2.0f).lightLevel(s -> 7).noOcclusion());
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING_ALL, Direction.DOWN).setValue(SIZE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING_ALL, SIZE);
	}

	protected VoxelShape getBB(BlockState state) {
		switch (state.getValue(SIZE)) {
		case 0:
			return getBBForRota(SIZE_0_W, SIZE_0_H, state.getValue(FACING_ALL));
		case 1:
			return getBBForRota(SIZE_1_W, SIZE_1_H, state.getValue(FACING_ALL));
		default:
			return getBBForRota(SIZE_2_W, SIZE_2_H, state.getValue(FACING_ALL));
		}
	}

	protected VoxelShape getBBForRota(int W, int H, Direction facing) {
		switch (facing) {
		case DOWN:
			return Shapes.box(FSIZE * W, 0, FSIZE * W, FSIZE * (16 - W), FSIZE * H, FSIZE * (16 - W));
		case WEST:
			return Shapes.box(0, FSIZE * W, FSIZE * W, FSIZE * H, FSIZE * (16 - W), FSIZE * (16 - W));
		case NORTH:
			return Shapes.box(FSIZE * W, FSIZE * W, 0, FSIZE * (16 - W), FSIZE * (16 - W), FSIZE * H);
		case SOUTH:
			return Shapes.box(FSIZE * W, FSIZE * W, FSIZE * (16 - H), FSIZE * (16 - W), FSIZE * (16 - W), 1);
		case UP:
			return Shapes.box(FSIZE * W, FSIZE * (16 - H), FSIZE * W, FSIZE * (16 - W), 1, FSIZE * (16 - W));
		case EAST:
			return Shapes.box(FSIZE * (16 - H), FSIZE * W, FSIZE * W, 1, FSIZE * (16 - W), FSIZE * (16 - W));
		}
		return Shapes.box(FSIZE * W, 0, FSIZE * W, FSIZE * (16 - W), FSIZE * H, FSIZE * (16 - W));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getBB(state);
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BioBlobTileEnt(pos, state);
	}

	@Override
	@Nullable
	public <E extends BlockEntity> BlockEntityTicker<E> getTicker(Level level, BlockState state, BlockEntityType<E> type) {
		if (level.isClientSide || type != TGBlocks.BIOBLOB_TILEENT) {
			return null;
		}
		return (lvl, pos, st, be) -> ((BioBlobTileEnt) be).update();
	}
}
