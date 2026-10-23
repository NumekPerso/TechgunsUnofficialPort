package techguns.blocks.machines.multiblocks;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGMachines;
import techguns.blocks.machines.MultiBlockMachine;
import techguns.packets.PacketMultiBlockFormInvalidBlockMessage;
import techguns.tileentities.FabricatorTileEntMaster;
import techguns.tileentities.MultiBlockMachineTileEntMaster;

/**
 * Fabricator: controller + 2 housing blocks on the bottom row (2x2), 4 glass blocks on top.
 */
public class FabricatorDefinition extends MultiBlockMachineSchematic {

	public FabricatorDefinition() {
		super(FabricatorTileEntMaster.class);
	}

	private ArrayList<BlockPos> getBottomRow(BlockPos masterPos, Direction direction) {
		masterPos = masterPos.immutable();
		ArrayList<BlockPos> bottomRow = new ArrayList<>();

		BlockPos behind = masterPos.relative(direction.getOpposite());
		bottomRow.add(behind);

		Direction left = direction.getClockWise();
		BlockPos leftFront = masterPos.relative(left, 1);
		BlockPos leftBehind = behind.relative(left, 1);
		bottomRow.add(leftFront);
		bottomRow.add(leftBehind);
		return bottomRow;
	}

	private ArrayList<BlockPos> getTopRow(BlockPos masterPos, Direction direction) {
		masterPos = masterPos.immutable();

		BlockPos behind = masterPos.relative(direction.getOpposite());
		Direction left = direction.getClockWise();
		BlockPos leftFront = masterPos.relative(left, 1);
		BlockPos leftBehind = behind.relative(left, 1);

		ArrayList<BlockPos> topRow = new ArrayList<>();
		topRow.add(masterPos.relative(Direction.UP, 1));
		topRow.add(behind.relative(Direction.UP, 1));
		topRow.add(leftFront.relative(Direction.UP, 1));
		topRow.add(leftBehind.relative(Direction.UP, 1));

		return topRow;
	}

	/**
	 * 1.12 compared with the unformed default block state, in 1.20 the type is the block: same block and not formed
	 */
	public static boolean allBlocksMatchUnformed(Level w, Player player, ArrayList<BlockPos> positions, Block block, boolean message) {
		boolean valid = true;
		for (BlockPos p : positions) {
			BlockState bs = w.getBlockState(p);
			if (!bs.is(block) || !bs.hasProperty(MultiBlockMachine.FORMED) || bs.getValue(MultiBlockMachine.FORMED)) {
				sendErrorPing(w, p, player, 0, message);
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public boolean checkForm(Level w, Player ply, BlockPos masterPos, Direction direction) {
		if (this.canFormFromSide(direction) &&
				allBlocksMatchUnformed(w, ply, getBottomRow(masterPos, direction), TGMachines.FABRICATOR_HOUSING, false)
				&& allBlocksMatchUnformed(w, ply, getTopRow(masterPos, direction), TGMachines.FABRICATOR_GLASS, false)) {
			return true;
		} else {
			sendErrorMSG(w, masterPos, ply, PacketMultiBlockFormInvalidBlockMessage.MSG_TYPE_MULTIBLOCK_ERROR);
			return false;
		}
	}

	@Override
	public boolean canFormFromSide(Direction side) {
		return side != null && side != Direction.UP && side != Direction.DOWN;
	}

	@Override
	public boolean form(Level w, Player player, BlockPos masterPos, Direction direction) {
		BlockEntity tile = w.getBlockEntity(masterPos);
		if (tile instanceof MultiBlockMachineTileEntMaster) {

			MultiBlockMachineTileEntMaster master = (MultiBlockMachineTileEntMaster) tile;
			master.form(direction);

			ArrayList<BlockPos> glassBlocks = this.getTopRow(masterPos, direction);
			ArrayList<BlockPos> housingBlocks = this.getBottomRow(masterPos, direction);

			glassBlocks.forEach(b -> linkSlave(w, player, b, 1, masterPos));
			housingBlocks.forEach(b -> linkSlave(w, player, b, 2, masterPos));

			return true;
		}
		return false;
	}

	@Override
	public void unform(Level w, MultiBlockMachineTileEntMaster master) {
		BlockPos masterPos = master.getBlockPos();
		Direction direction = master.getMultiblockDirection();

		master.unform();

		if (direction == null) {
			return;
		}

		ArrayList<BlockPos> glassBlocks = this.getTopRow(masterPos, direction);
		ArrayList<BlockPos> housingBlocks = this.getBottomRow(masterPos, direction);

		glassBlocks.forEach(b -> unlinkSlave(w, b));
		housingBlocks.forEach(b -> unlinkSlave(w, b));
	}
}
