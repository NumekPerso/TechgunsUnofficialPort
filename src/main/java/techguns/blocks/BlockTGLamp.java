package techguns.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import techguns.Techguns;

/**
 * Lamps (attached to a face) and lanterns (connect to the surrounding blocks). The 1.12 "actual state" connections of the
 * lanterns are stored properties updated in updateShape, the 1.12 ItemBlockLamp placement checks are done in canSurvive.
 * The camo switches between yellow and white (lamp_type 0 <-> 1, 2 <-> 3).
 */
public class BlockTGLamp<T extends Enum<T> & StringRepresentable> extends GenericBlockMetaEnumCamoChangeable<T> {

	public static final DirectionProperty FACING_ALL = DirectionalBlock.FACING;

	public static final BooleanProperty CONNECT_DOWN = BooleanProperty.create("down");
	public static final BooleanProperty CONNECT_UP = BooleanProperty.create("up");
	public static final BooleanProperty CONNECT_N = BooleanProperty.create("north");
	public static final BooleanProperty CONNECT_S = BooleanProperty.create("south");
	public static final BooleanProperty CONNECT_W = BooleanProperty.create("west");
	public static final BooleanProperty CONNECT_E = BooleanProperty.create("east");

	public final EnumProperty<T> LAMP_TYPE;

	protected static final double lamp_side = 0.25D;
	protected static final double lamp_side_max = 1.0D - lamp_side;
	protected static final double lamp_h = 0.1875D;

	public static final VoxelShape LAMP_DOWN_AABB = Shapes.box(lamp_side, 0, lamp_side, lamp_side_max, lamp_h, lamp_side_max);
	public static final VoxelShape LAMP_UP_AABB = Shapes.box(lamp_side, 1.0D - lamp_h, lamp_side, lamp_side_max, 1.0D, lamp_side_max);
	public static final VoxelShape LAMP_NORTH_AABB = Shapes.box(lamp_side, lamp_side, 0.0D, lamp_side_max, lamp_side_max, lamp_h);
	public static final VoxelShape LAMP_SOUTH_AABB = Shapes.box(lamp_side, lamp_side, 1.0D - lamp_h, lamp_side_max, lamp_side_max, 1.0D);
	public static final VoxelShape LAMP_WEST_AABB = Shapes.box(0.0D, lamp_side, lamp_side, lamp_h, lamp_side_max, lamp_side_max);
	public static final VoxelShape LAMP_EAST_AABB = Shapes.box(1.0D - lamp_h, lamp_side, lamp_side, 1.0D, lamp_side_max, lamp_side_max);

	protected static final double lantern_side = 0.25D;
	protected static final double lantern_side_max = 1.0D - lamp_side;
	protected static final double lantern_h = 0.125D;
	public static final VoxelShape LANTERN_AABB = Shapes.box(lantern_side, lantern_h, lantern_side, lantern_side_max, 1.0D - lantern_h, lamp_side_max);

	public BlockTGLamp(Properties props, EnumProperty<T> type) {
		super(props, type);
		this.LAMP_TYPE = type;
		this.registerDefaultState(this.defaultBlockState().setValue(FACING_ALL, Direction.DOWN).setValue(CONNECT_DOWN, false).setValue(CONNECT_UP, false)
				.setValue(CONNECT_N, false).setValue(CONNECT_S, false).setValue(CONNECT_W, false).setValue(CONNECT_E, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING_ALL, CONNECT_DOWN, CONNECT_UP, CONNECT_N, CONNECT_S, CONNECT_W, CONNECT_E);
	}

	protected boolean isLantern(BlockState state) {
		return state.getValue(LAMP_TYPE).ordinal() >= 2;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (!isLantern(state)) {
			switch (state.getValue(FACING_ALL)) {
			case UP:
				return LAMP_UP_AABB;
			case EAST:
				return LAMP_EAST_AABB;
			case NORTH:
				return LAMP_NORTH_AABB;
			case SOUTH:
				return LAMP_SOUTH_AABB;
			case WEST:
				return LAMP_WEST_AABB;
			case DOWN:
			default:
				return LAMP_DOWN_AABB;
			}
		} else {
			return LANTERN_AABB;
		}
	}

	/**
	 * 1.12 getActualState
	 */
	protected BlockState getConnectedState(BlockState state, LevelReader worldIn, BlockPos pos) {
		if (!isLantern(state)) {
			return state.setValue(CONNECT_UP, false).setValue(CONNECT_DOWN, false).setValue(CONNECT_E, false)
					.setValue(CONNECT_W, false).setValue(CONNECT_N, false).setValue(CONNECT_S, false);
		} else {
			boolean u = this.canPlaceAt(worldIn, pos, Direction.UP);
			boolean d = this.canPlaceAt(worldIn, pos, Direction.DOWN);
			boolean n = this.canPlaceAt(worldIn, pos, Direction.NORTH);
			boolean s = this.canPlaceAt(worldIn, pos, Direction.SOUTH);
			boolean w = this.canPlaceAt(worldIn, pos, Direction.WEST);
			boolean e = this.canPlaceAt(worldIn, pos, Direction.EAST);
			return state.setValue(CONNECT_UP, u).setValue(CONNECT_DOWN, d).setValue(CONNECT_E, e)
					.setValue(CONNECT_W, w).setValue(CONNECT_N, n).setValue(CONNECT_S, s);
		}
	}

	/**
	 * The lamp is attached to the clicked face. The type comes from the stack's BlockStateTag, which vanilla applies after
	 * this, so the connections are recomputed in onPlace.
	 */
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = this.defaultBlockState().setValue(FACING_ALL, context.getClickedFace().getOpposite());
		T type = this.getTypeFromStack(context.getItemInHand());
		state = state.setValue(LAMP_TYPE, type);
		// 1.12 ItemBlockLamp.canPlaceBlockOnSide + checkForDrop
		if (!this.canPlaceAt(context.getLevel(), context.getClickedPos(), state.getValue(FACING_ALL))) {
			return null;
		}
		return getConnectedState(state, context.getLevel(), context.getClickedPos());
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return this.canPlaceAt(level, pos, state.getValue(FACING_ALL));
	}

	private boolean canPlaceAt(LevelReader worldIn, BlockPos pos, Direction facing) {
		BlockPos blockpos = pos.relative(facing);
		BlockState iblockstate = worldIn.getBlockState(blockpos);

		if ((facing.equals(Direction.UP) || facing.equals(Direction.DOWN)) && this.canPlaceOn(worldIn, blockpos, facing.getOpposite())) {
			return true;
		} else if (facing != Direction.UP && facing != Direction.DOWN) {
			return !isExceptBlockForAttachWithPiston(iblockstate) && iblockstate.isFaceSturdy(worldIn, blockpos, facing.getOpposite());
		} else {
			return false;
		}
	}

	protected static boolean isExceptBlockForAttachWithPiston(BlockState state) {
		Block b = state.getBlock();
		return Block.isExceptionForConnection(state) || b == Blocks.BARRIER;
	}

	/**
	 * 1.12 canPlaceTorchOnTop, for the given side of the block at pos
	 */
	private boolean canPlaceOn(LevelReader worldIn, BlockPos pos, Direction side) {
		return Block.canSupportCenter(worldIn, pos, side);
	}

	/**
	 * 1.12 neighborChanged / checkForDrop: the lamp drops when the block it is attached to is removed
	 */
	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (facing == state.getValue(FACING_ALL) && !this.canSurvive(state, level, currentPos)) {
			return Blocks.AIR.defaultBlockState();
		}
		return getConnectedState(state, level, currentPos);
	}

	@Override
	public void onPlace(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		BlockState connected = getConnectedState(state, level, pos);
		if (connected != state) {
			level.setBlock(pos, connected, 2);
		}
	}

	@Override
	public int getCamoCount() {
		return 2;
	}

	@Override
	public T switchCamo(T current, boolean back) {
		T[] values = clazz.getEnumConstants();
		int type = current.ordinal();
		if (type == 0) {
			return values[1];
		} else if (type == 1) {
			return values[0];
		} else if (type == 2) {
			return values[3];
		} else {
			return values[2];
		}
	}

	@Override
	public int getCurrentCamoIndex(T current) {
		int type = current.ordinal();
		if (type == 0 || type == 2) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public Component getCurrentCamoName(T current) {
		return Component.translatable(Techguns.MODID + ".lamp0.camoname." + this.getCurrentCamoIndex(current));
	}
}
