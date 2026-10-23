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
 * Flat camo netting (roof). Render type cutout (model).
 */
public class BlockTGCamoNetTop extends GenericBlockMetaEnumCamoChangeable<EnumCamoNetType> {

	public static final EnumProperty<EnumConnectionType> CONNECTION = BlockTGCamoNet.CONNECTION;

	public BlockTGCamoNetTop() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL).strength(2.0f).noOcclusion().ignitedByLava(), BlockTGCamoNet.CAMO_TYPE);
		this.registerDefaultState(this.defaultBlockState().setValue(CONNECTION, EnumConnectionType.NONE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION);
	}

	private static final double height = 1 / 16d;

	protected static final VoxelShape[] bounding_boxes = {
			//no connections
			Shapes.box(4 / 16d, 0, 4 / 16d, 12 / 16d, height, 12 / 16d),
			//w
			Shapes.box(0, 0, 4 / 16d, 9 / 16d, height, 12 / 16d),
			//s
			Shapes.box(4 / 16d, 0, 7 / 16d, 12 / 16d, height, 1d),
			//sw
			Shapes.box(0, 0, 7 / 16d, 9 / 16d, height, 1d),
			//e
			Shapes.box(7 / 16d, 0, 4 / 16d, 1d, height, 12 / 16d),
			//ew
			Shapes.box(0, 0, 4 / 16d, 1d, height, 12 / 16d),
			//es
			Shapes.box(7 / 16d, 0, 7 / 16d, 1d, height, 1d),
			//esw
			Shapes.box(0, 0, 7 / 16d, 1d, height, 1d),
			//n
			Shapes.box(4 / 16d, 0, 0, 12 / 16d, height, 9 / 16d),
			//nw
			Shapes.box(0, 0, 0, 9 / 16d, height, 9 / 16d),
			//ns
			Shapes.box(4 / 16d, 0, 0, 12 / 16d, height, 1d),
			//nsw
			Shapes.box(0, 0, 0, 9 / 16d, height, 1d),
			//ne
			Shapes.box(7 / 16d, 0, 0, 1d, height, 9 / 16d),
			//new
			Shapes.box(0, 0, 0, 1d, height, 9 / 16d),
			//nes
			Shapes.box(7 / 16d, 0, 0, 1d, height, 1d),
			//nesw
			Shapes.box(0, 0, 0, 1d, height, 1d) };

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return bounding_boxes[state.getValue(CONNECTION).ordinal()];
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

	private boolean canConnectTo(BlockGetter world, BlockPos pos, Direction facing) {
		BlockPos other = pos.relative(facing);
		Block block = world.getBlockState(other).getBlock();
		return block instanceof BlockTGCamoNetTop;
	}
}
