package techguns.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Camo netting wall. The 1.12 "actual state" connection is a stored property updated in updateShape. Render type cutout (model).
 */
public class BlockTGCamoNet extends GenericBlockMetaEnumCamoChangeable<EnumCamoNetType> {

	public static final EnumProperty<EnumCamoNetType> CAMO_TYPE = EnumProperty.create("type", EnumCamoNetType.class);
	public static final EnumProperty<EnumConnectionType> CONNECTION = EnumProperty.create("connection", EnumConnectionType.class);

	public BlockTGCamoNet() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL).strength(2.0f).noOcclusion().ignitedByLava(), CAMO_TYPE);
		this.registerDefaultState(this.defaultBlockState().setValue(CONNECTION, EnumConnectionType.NONE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION);
	}

	protected BlockState getConnectedState(BlockState state, BlockGetter worldIn, BlockPos pos) {
		boolean n = canConnectTo(worldIn, pos, Direction.NORTH);
		boolean e = canConnectTo(worldIn, pos, Direction.EAST);
		boolean s = canConnectTo(worldIn, pos, Direction.SOUTH);
		boolean w = canConnectTo(worldIn, pos, Direction.WEST);
		return state.setValue(CONNECTION, EnumConnectionType.get(n, e, s, w));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return getConnectedState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (facing.getAxis().isHorizontal()) {
			return getConnectedState(state, level, currentPos);
		}
		return state;
	}

	protected static final VoxelShape COLLIDE_CENTER = Shapes.box(7 / 16d, 0, 7 / 16d, 9 / 16d, 1d, 9 / 16d);
	protected static final VoxelShape COLLIDE_SOUTH = Shapes.box(7 / 16d, 0, 9 / 16d, 9 / 16d, 1d, 1d);
	protected static final VoxelShape COLLIDE_NORTH = Shapes.box(7 / 16d, 0, 0, 9 / 16d, 1d, 7 / 16d);
	protected static final VoxelShape COLLIDE_EAST = Shapes.box(9 / 16d, 0, 7 / 16d, 1d, 1d, 9 / 16d);
	protected static final VoxelShape COLLIDE_WEST = Shapes.box(0, 0, 7 / 16d, 7 / 16d, 1d, 9 / 16d);

	protected static boolean hasN(EnumConnectionType c) {
		return (c.ordinal() & 8) != 0;
	}

	protected static boolean hasE(EnumConnectionType c) {
		return (c.ordinal() & 4) != 0;
	}

	protected static boolean hasS(EnumConnectionType c) {
		return (c.ordinal() & 2) != 0;
	}

	protected static boolean hasW(EnumConnectionType c) {
		return (c.ordinal() & 1) != 0;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		EnumConnectionType c = state.getValue(CONNECTION);
		VoxelShape shape = COLLIDE_CENTER;
		if (hasN(c)) shape = Shapes.or(shape, COLLIDE_NORTH);
		if (hasE(c)) shape = Shapes.or(shape, COLLIDE_EAST);
		if (hasS(c)) shape = Shapes.or(shape, COLLIDE_SOUTH);
		if (hasW(c)) shape = Shapes.or(shape, COLLIDE_WEST);
		return shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		double f = 0.4375D;
		double f1 = 0.5625D;
		double f2 = 0.4375D;
		double f3 = 0.5625D;
		EnumConnectionType c = state.getValue(CONNECTION);
		boolean north = hasN(c);
		boolean east = hasE(c);
		boolean south = hasS(c);
		boolean west = hasW(c);

		if (!(north || east || south || west)) {
			return Shapes.box(f, 0.0F, f2, f1, 1.0F, f3);
		}

		if ((!west || !east) && (west || east || north || south)) {
			if (west && !east) {
				f = 0.0F;
			} else if (!west && east) {
				f1 = 1.0F;
			}
		} else {
			f = 0.0F;
			f1 = 1.0F;
		}

		if ((!north || !south) && (west || east || north || south)) {
			if (north && !south) {
				f2 = 0.0F;
			} else if (!north && south) {
				f3 = 1.0F;
			}
		} else {
			f2 = 0.0F;
			f3 = 1.0F;
		}
		return Shapes.box(f, 0.0F, f2, f1, 1.0F, f3);
	}

	private boolean canConnectTo(BlockGetter world, BlockPos pos, Direction facing) {
		BlockPos other = pos.relative(facing);
		Block block = world.getBlockState(other).getBlock();
		return block instanceof BlockTGCamoNet;
	}
}
