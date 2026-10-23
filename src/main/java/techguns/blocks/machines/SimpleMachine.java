package techguns.blocks.machines;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import techguns.api.machines.IMachineType;

/**
 * A Machine that has 4 rotation types as block property
 */
public class SimpleMachine<T extends Enum<T> & IMachineType> extends BasicMachine<T> {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public SimpleMachine(T machineType, Supplier<? extends BlockEntityType<?>> tileType) {
		this(machineType, tileType, defaultProperties(machineType));
	}

	public SimpleMachine(T machineType, Supplier<? extends BlockEntityType<?>> tileType, BlockBehaviour.Properties properties) {
		super(machineType, tileType, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public boolean rotateBlock(Level world, BlockPos pos, Direction axis) {
		if (axis == Direction.DOWN || axis == Direction.UP) {
			BlockState state = world.getBlockState(pos);
			BlockState statenew = state.setValue(FACING, state.getValue(FACING).getClockWise());
			world.setBlock(pos, statenew, 3);
			return true;
		}
		return false;
	}

}
