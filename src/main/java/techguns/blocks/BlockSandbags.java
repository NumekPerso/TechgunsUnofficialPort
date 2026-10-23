package techguns.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Fence like sandbags. The 1.12 "actual state" connections are stored block state properties, updated in updateShape.
 */
public class BlockSandbags extends GenericBlock {
	/** Whether this block connects in the northern direction */
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	/** Whether this block connects in the eastern direction */
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	/** Whether this block connects in the southern direction */
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	/** Whether this block connects in the western direction */
	public static final BooleanProperty WEST = BooleanProperty.create("west");

	public static final BooleanProperty CORNER_NE = BooleanProperty.create("corner_ne");
	public static final BooleanProperty CORNER_ES = BooleanProperty.create("corner_es");
	public static final BooleanProperty CORNER_SW = BooleanProperty.create("corner_sw");
	public static final BooleanProperty CORNER_WN = BooleanProperty.create("corner_wn");

	public static final double size_1 = 0.25D;
	public static final double size_2 = 0.75D;
	public static final double h = 1.0D;

	protected static final VoxelShape[] BOUNDING_BOXES = new VoxelShape[] { Shapes.box(size_1, 0.0D, size_1, size_2, 1.0D, size_2), Shapes.box(size_1, 0.0D, size_1, size_2, 1.0D, 1.0D),
			Shapes.box(0.0D, 0.0D, size_1, size_2, 1.0D, size_2), Shapes.box(0.0D, 0.0D, size_1, size_2, 1.0D, 1.0D), Shapes.box(size_1, 0.0D, 0.0D, size_2, 1.0D, size_2),
			Shapes.box(size_1, 0.0D, 0.0D, size_2, 1.0D, 1.0D), Shapes.box(0.0D, 0.0D, 0.0D, size_2, 1.0D, size_2), Shapes.box(0.0D, 0.0D, 0.0D, size_2, 1.0D, 1.0D),
			Shapes.box(size_1, 0.0D, size_1, 1.0D, 1.0D, size_2), Shapes.box(size_1, 0.0D, size_1, 1.0D, 1.0D, 1.0D), Shapes.box(0.0D, 0.0D, size_1, 1.0D, 1.0D, size_2),
			Shapes.box(0.0D, 0.0D, size_1, 1.0D, 1.0D, 1.0D), Shapes.box(size_1, 0.0D, 0.0D, 1.0D, 1.0D, size_2), Shapes.box(size_1, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D),
			Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, size_2), Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D) };

	public static final VoxelShape PILLAR_AABB = Shapes.box(size_1, 0.0D, size_1, size_2, h, size_2);
	public static final VoxelShape SOUTH_AABB = Shapes.box(size_1, 0.0D, size_2, size_2, h, 1.0D);
	public static final VoxelShape WEST_AABB = Shapes.box(0.0D, 0.0D, size_1, size_1, h, size_2);
	public static final VoxelShape NORTH_AABB = Shapes.box(size_1, 0.0D, 0.0D, size_2, h, size_1);
	public static final VoxelShape EAST_AABB = Shapes.box(size_2, 0.0D, size_1, 1.0D, h, size_2);

	public BlockSandbags() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).strength(3.0f, 15.0f).noOcclusion());
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false)
				.setValue(CORNER_NE, false).setValue(CORNER_ES, false).setValue(CORNER_SW, false).setValue(CORNER_WN, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, CORNER_NE, CORNER_ES, CORNER_SW, CORNER_WN);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape = PILLAR_AABB;
		if (state.getValue(NORTH)) {
			shape = Shapes.or(shape, NORTH_AABB);
		}
		if (state.getValue(EAST)) {
			shape = Shapes.or(shape, EAST_AABB);
		}
		if (state.getValue(SOUTH)) {
			shape = Shapes.or(shape, SOUTH_AABB);
		}
		if (state.getValue(WEST)) {
			shape = Shapes.or(shape, WEST_AABB);
		}
		return shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return BOUNDING_BOXES[getBoundingBoxIdx(state)];
	}

	/**
	 * Returns the correct index into boundingBoxes, based on what the fence is connected to.
	 */
	private static int getBoundingBoxIdx(BlockState state) {
		int i = 0;
		if (state.getValue(NORTH)) {
			i |= 1 << Direction.NORTH.get2DDataValue();
		}
		if (state.getValue(EAST)) {
			i |= 1 << Direction.EAST.get2DDataValue();
		}
		if (state.getValue(SOUTH)) {
			i |= 1 << Direction.SOUTH.get2DDataValue();
		}
		if (state.getValue(WEST)) {
			i |= 1 << Direction.WEST.get2DDataValue();
		}
		return i;
	}

	/**
	 * Determines if an entity can path through this block
	 */
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	public boolean canConnectTo(BlockGetter worldIn, BlockPos pos, Direction facing) {
		BlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		boolean flag = block instanceof FenceGateBlock;
		return !isExcepBlockForAttachWithPiston(iblockstate) && iblockstate.isFaceSturdy(worldIn, pos, facing) || flag;
	}

	protected static boolean isExcepBlockForAttachWithPiston(BlockState state) {
		Block b = state.getBlock();
		return Block.isExceptionForConnection(state) || b == Blocks.BARRIER || b == Blocks.MELON || b == Blocks.PUMPKIN || b == Blocks.CARVED_PUMPKIN || b == Blocks.JACK_O_LANTERN;
	}

	/**
	 * 1.12 canBeConnectedTo: fences and other sandbags
	 */
	public static boolean canBeConnectedTo(BlockState other) {
		Block connector = other.getBlock();
		return connector instanceof FenceBlock || connector instanceof BlockSandbags;
	}

	private boolean canFenceConnectTo(BlockGetter world, BlockPos pos, Direction facing) {
		BlockPos other = pos.relative(facing);
		return canBeConnectedTo(world.getBlockState(other)) || canConnectTo(world, other, facing.getOpposite());
	}

	private boolean canConnectCorner(BlockGetter world, BlockPos pos, Direction f1, Direction f2) {
		return canFenceConnectTo(world, pos, f1) && canFenceConnectTo(world, pos, f2) && (canFenceConnectTo(world, pos.relative(f1), f2) || canFenceConnectTo(world, pos.relative(f2), f1));
	}

	/**
	 * 1.12 getActualState
	 */
	protected BlockState getConnectedState(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return state.setValue(NORTH, canFenceConnectTo(worldIn, pos, Direction.NORTH))
				.setValue(EAST, canFenceConnectTo(worldIn, pos, Direction.EAST))
				.setValue(SOUTH, canFenceConnectTo(worldIn, pos, Direction.SOUTH))
				.setValue(WEST, canFenceConnectTo(worldIn, pos, Direction.WEST))
				.setValue(CORNER_NE, canConnectCorner(worldIn, pos, Direction.NORTH, Direction.EAST))
				.setValue(CORNER_ES, canConnectCorner(worldIn, pos, Direction.EAST, Direction.SOUTH))
				.setValue(CORNER_SW, canConnectCorner(worldIn, pos, Direction.SOUTH, Direction.WEST))
				.setValue(CORNER_WN, canConnectCorner(worldIn, pos, Direction.WEST, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return getConnectedState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
	}

	/**
	 * Corners depend on diagonal neighbours, recompute everything on any update
	 */
	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return getConnectedState(state, level, currentPos);
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
		return false;
	}
}
