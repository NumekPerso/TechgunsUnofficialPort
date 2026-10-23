package techguns.blocks.machines;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import techguns.api.machines.IMachineType;
import techguns.tileentities.MultiBlockMachineTileEntSlave;

/**
 * Block of a multiblock machine (housing, glass, controller). 1.12 computed MULTIBLOCK_DIRECTION in getActualState from
 * the master tile, in 1.20 it is a real block state property set by the master when it forms (see MultiBlockMachineTileEntMaster.form).
 */
public class MultiBlockMachine<T extends Enum<T> & IMachineType> extends BasicMachine<T> {
	public static final BooleanProperty FORMED = BooleanProperty.create("formed");

	public static final DirectionProperty MULTIBLOCK_DIRECTION = HorizontalDirectionalBlock.FACING;

	public MultiBlockMachine(T machineType, Supplier<? extends BlockEntityType<?>> tileType) {
		this(machineType, tileType, defaultProperties(machineType).noOcclusion());
	}

	public MultiBlockMachine(T machineType, Supplier<? extends BlockEntityType<?>> tileType, BlockBehaviour.Properties properties) {
		super(machineType, tileType, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FORMED, false).setValue(MULTIBLOCK_DIRECTION, Direction.SOUTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FORMED, MULTIBLOCK_DIRECTION);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FORMED, false);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		if (this.machineType.hideInCreative()) {
			return ItemStack.EMPTY;
		}
		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(FORMED)) {
			BlockEntity tile = worldIn.getBlockEntity(pos);
			if (tile instanceof MultiBlockMachineTileEntSlave) {
				return ((MultiBlockMachineTileEntSlave) tile).getFormedCollisionBoundingBox();
			}
		}
		return Shapes.block();
	}

	@Override
	public boolean rotateBlock(Level world, BlockPos pos, Direction axis) {
		//NO ROTATING ON MULTIBLOCK MACHINES
		return false;
	}

	/**
	 * Formed multiblocks don't support blocks on their sides (1.12 BlockFaceShape.UNDEFINED)
	 */
	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
		return !state.getValue(FORMED) && super.canSustainPlant(state, world, pos, facing, plantable);
	}

	public boolean isFormed(LevelReader level, BlockPos pos) {
		BlockState bs = level.getBlockState(pos);
		return bs.getBlock() == this && bs.getValue(FORMED);
	}
}
