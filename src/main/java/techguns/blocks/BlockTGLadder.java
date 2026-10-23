package techguns.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Metal ladders (camo changeable type). Also in the minecraft:climbable tag.
 */
public class BlockTGLadder<T extends Enum<T> & StringRepresentable> extends GenericBlockMetaEnumCamoChangeable<T> {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	protected static final double LADDER_SIZE = 0.125D;
	protected static final VoxelShape LADDER_EAST_AABB = Shapes.box(0.0D, 0.0D, 0.0D, LADDER_SIZE, 1.0D, 1.0D);
	protected static final VoxelShape LADDER_WEST_AABB = Shapes.box(1.0D - LADDER_SIZE, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final VoxelShape LADDER_SOUTH_AABB = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, LADDER_SIZE);
	protected static final VoxelShape LADDER_NORTH_AABB = Shapes.box(0.0D, 0.0D, 1.0D - LADDER_SIZE, 1.0D, 1.0D, 1.0D);

	public BlockTGLadder(Properties props, EnumProperty<T> type) {
		super(props, type);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		switch (state.getValue(FACING)) {
		case NORTH:
			return LADDER_NORTH_AABB;
		case SOUTH:
			return LADDER_SOUTH_AABB;
		case WEST:
			return LADDER_WEST_AABB;
		case EAST:
		default:
			return LADDER_EAST_AABB;
		}
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState basetype = this.defaultBlockState();
		Direction facing = context.getClickedFace();
		Level worldIn = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (facing.getAxis().isHorizontal() /*&& this.canAttachTo(worldIn, pos.offset(facing.getOpposite()), facing)*/) {
			return basetype.setValue(FACING, facing);
		} else {
			if (worldIn.getBlockState(pos.below()).getBlock() instanceof BlockTGLadder) {
				BlockState statedown = worldIn.getBlockState(pos.below());
				return basetype.setValue(FACING, statedown.getValue(FACING));
			} else if (worldIn.getBlockState(pos.above()).getBlock() instanceof BlockTGLadder) {
				BlockState stateup = worldIn.getBlockState(pos.above());
				return basetype.setValue(FACING, stateup.getValue(FACING));
			}
			return basetype.setValue(FACING, context.getHorizontalDirection().getOpposite());
		}
	}

	@Override
	public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return true;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}

	/**
	 * 1.12 used "techguns.ladder0.camoname.<index>"
	 */
	@Override
	public net.minecraft.network.chat.Component getCurrentCamoName(T current) {
		return net.minecraft.network.chat.Component.translatable("techguns.ladder0.camoname." + getCurrentCamoIndex(current));
	}
}
