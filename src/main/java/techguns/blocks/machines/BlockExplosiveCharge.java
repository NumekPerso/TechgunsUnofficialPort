package techguns.blocks.machines;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import techguns.api.machines.IMachineType;

/**
 * Explosive charge placed on a block side. FACING points into the block it is attached to. ARMED was computed from the tile
 * in 1.12 (getActualState), it is set by the tile entity in 1.20.
 */
public class BlockExplosiveCharge<T extends Enum<T> & IMachineType> extends BasicMachine<T> {

	public static final DirectionProperty FACING = DirectionalBlock.FACING;

	public static final BooleanProperty ARMED = BooleanProperty.create("armed");

	public BlockExplosiveCharge(T machineType, Supplier<? extends BlockEntityType<?>> tileType) {
		super(machineType, tileType);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ARMED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, ARMED);
	}

	protected static final VoxelShape[] boundingBoxes = {
			//D,U,N,S,W,E
			Block.box(2, 0, 2, 14, 5, 14),
			Block.box(2, 11, 2, 14, 16, 14),

			Block.box(2, 2, 0, 14, 14, 5),
			Block.box(2, 2, 11, 14, 14, 16),

			Block.box(0, 2, 2, 5, 14, 14),
			Block.box(11, 2, 2, 16, 14, 14)
	};

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return boundingBoxes[state.getValue(FACING).get3DDataValue()];
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		if (!worldIn.isClientSide && worldIn.isEmptyBlock(pos.relative(state.getValue(FACING)))) {
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}

	@Override
	public boolean rotateBlock(Level world, BlockPos pos, Direction axis) {
		if (axis == Direction.DOWN || axis == Direction.UP) {
			BlockState state = world.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			if (facing.getAxis().isHorizontal()) {
				world.setBlock(pos, state.setValue(FACING, facing.getClockWise()), 3);
				return true;
			}
		}
		return false;
	}
}
