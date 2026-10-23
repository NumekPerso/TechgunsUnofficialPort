package techguns.blocks;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGMachines;
import techguns.blocks.machines.BlockOreDrill;
import techguns.blocks.machines.EnumOreDrillType;
import techguns.blocks.machines.MachineItemBlock;
import techguns.blocks.machines.MultiBlockMachine;
import techguns.blocks.machines.multiblocks.OreDrillDefinition;

/**
 * Item of the ore drill blocks: rods must be placed next to an ore cluster (that has no rod yet) or another rod,
 * controllers can't be placed next to each other (1.12 canPlaceBlockOnSide).
 */
public class ItemBlockOreDrill extends MachineItemBlock {
	protected final BlockOreDrill block;

	public ItemBlockOreDrill(BlockOreDrill block) {
		super(block);
		this.block = block;
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		if (!this.canPlaceBlockOnSide(context.getLevel(), context.getClickedPos(), context.getPlayer())) {
			return InteractionResult.FAIL;
		}
		return super.place(context);
	}

	/**
	 * @param pos position where the block will be placed
	 */
	public boolean canPlaceBlockOnSide(Level world, BlockPos pos, Player player) {
		EnumOreDrillType type = block.getMachineType();

		if (type == EnumOreDrillType.ROD) {

			BlockPos clusterPos = checkForOreClusterInNeighbourhood(world, pos);
			if (clusterPos != null) {

				BlockState bs = world.getBlockState(clusterPos);

				//Check if cluster has a connected rod already
				if (getHasConnectedDrillRod(world, clusterPos, new ArrayList<BlockPos>(), bs)) {
					if (world.isClientSide && player != null) {
						player.displayClientMessage(Component.translatable("techguns.msg.error.alreadyrod"), true);
					}
					return false;
				} else {
					return true;
				}

			} else {
				// Check for other rod neighbour
				if (checkForRodInNeighbourhood(world, pos)) {
					return true;
				} else {
					if (world.isClientSide && player != null) {
						player.displayClientMessage(Component.translatable("techguns.msg.error.rodplacement"), true);
					}
					return false;
				}
			}
		} else if (type == EnumOreDrillType.CONTROLLER) {

			if (checkForControllerInNeighbourhood(world, pos)) {
				if (world.isClientSide && player != null) {
					player.displayClientMessage(Component.translatable("techguns.msg.error.oredrill.controllernext"), true);
				}
				return false;
			} else {
				return true;
			}

		} else {
			return true;
		}
	}

	public static BlockPos checkForOreClusterInNeighbourhood(Level w, BlockPos p) {
		for (Direction f : Direction.values()) {
			if (OreDrillDefinition.isOreCluster(w, p.relative(f))) {
				return p.relative(f);
			}
		}
		return null;
	}

	protected static boolean isUnformedRod(BlockState bs) {
		return bs.is(TGMachines.OREDRILL_ROD) && !bs.getValue(MultiBlockMachine.FORMED);
	}

	public boolean checkForRodInNeighbourhood(Level w, BlockPos p) {
		for (Direction f : Direction.values()) {
			if (isUnformedRod(w.getBlockState(p.relative(f)))) {
				return true;
			}
		}
		return false;
	}

	public boolean checkForControllerInNeighbourhood(Level w, BlockPos p) {
		for (Direction f : Direction.values()) {
			BlockState bs = w.getBlockState(p.relative(f));
			//1.12 returned on the first ore drill block found
			if (bs.getBlock() instanceof BlockOreDrill) {
				return bs.is(TGMachines.OREDRILL_CONTROLLER);
			}
		}
		return false;
	}

	public boolean getHasConnectedDrillRod(Level world, BlockPos p, ArrayList<BlockPos> visited, BlockState clusterblock) {
		BlockState target = world.getBlockState(p);
		if (!visited.contains(p)) {
			if (target == clusterblock) {
				visited.add(p);

				boolean b1 = getHasConnectedDrillRod(world, p.relative(Direction.DOWN), visited, clusterblock);
				boolean b2 = getHasConnectedDrillRod(world, p.relative(Direction.UP), visited, clusterblock);
				boolean b3 = getHasConnectedDrillRod(world, p.relative(Direction.NORTH), visited, clusterblock);
				boolean b4 = getHasConnectedDrillRod(world, p.relative(Direction.SOUTH), visited, clusterblock);
				boolean b5 = getHasConnectedDrillRod(world, p.relative(Direction.WEST), visited, clusterblock);
				boolean b6 = getHasConnectedDrillRod(world, p.relative(Direction.EAST), visited, clusterblock);

				return b1 || b2 || b3 || b4 || b5 || b6;
			} else if (isUnformedRod(target)) {

				return true;

			} else {
				//stop recursion, no cluster
				return false;
			}
		} else {
			return false;
		}
	}
}
