package techguns.blocks.machines.multiblocks;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGPackets;
import techguns.blocks.machines.MultiBlockMachine;
import techguns.packets.PacketMultiBlockFormInvalidBlockMessage;
import techguns.packets.PacketSpawnParticle;
import techguns.tileentities.MultiBlockMachineTileEntMaster;
import techguns.tileentities.MultiBlockMachineTileEntSlave;

public abstract class MultiBlockMachineSchematic {

	protected Class<? extends MultiBlockMachineTileEntMaster> mastertileClazz;

	public MultiBlockMachineSchematic(Class<? extends MultiBlockMachineTileEntMaster> mastertileClazz) {
		super();
		this.mastertileClazz = mastertileClazz;
	}

	public abstract boolean checkForm(Level w, Player player, BlockPos masterPos, Direction direction);

	public abstract boolean form(Level w, Player player, BlockPos masterPos, Direction direction);

	public abstract void unform(Level w, MultiBlockMachineTileEntMaster master);

	public abstract boolean canFormFromSide(Direction side);

	protected static boolean allBlocksMatch(Level w, Player player, ArrayList<BlockPos> positions, BlockState blockstate) {
		return allBlocksMatch(w, player, positions, blockstate, false);
	}

	protected static boolean allBlocksMatch(Level w, Player player, ArrayList<BlockPos> positions, BlockState blockstate, boolean message) {
		boolean valid = true;
		for (int i = 0; i < positions.size(); i++) {
			BlockPos p = positions.get(i);

			if (w.getBlockState(p) != blockstate) {
				sendErrorPing(w, p, player, 0, message);
				valid = false;
			}
		}
		return valid;
	}

	protected static void sendErrorPing(Level w, BlockPos p, Player player, int type, boolean message) {
		if (!w.isClientSide && player instanceof ServerPlayer sply) {
			TGPackets.network.sendTo(new PacketSpawnParticle("MultiblockInvalidPing", p.getX() + 0.5d, p.getY() + 0.5d, p.getZ() + 0.5d), sply);
			if (message) TGPackets.network.sendTo(new PacketMultiBlockFormInvalidBlockMessage(p, type), sply);
		}
	}

	protected static void sendErrorMSG(Level w, BlockPos p, Player player, int msgID) {
		if (!w.isClientSide && player instanceof ServerPlayer sply) {
			TGPackets.network.sendTo(new PacketMultiBlockFormInvalidBlockMessage(p, msgID), sply);
		}
	}

	protected void linkSlave(Level w, Player p, BlockPos pos, int type, BlockPos masterPos) {
		BlockEntity tile = w.getBlockEntity(pos);
		if (tile instanceof MultiBlockMachineTileEntSlave) {
			MultiBlockMachineTileEntSlave slave = (MultiBlockMachineTileEntSlave) tile;
			slave.form(masterPos, (byte) type);

			if (!w.isClientSide) {
				updateBlockStateForm(w, pos, type);
				slave.needUpdate();
			}
		}
	}

	protected void updateBlockStateForm(Level w, BlockPos pos, int type) {
		w.setBlock(pos, w.getBlockState(pos).setValue(MultiBlockMachine.FORMED, true), 3);
	}

	protected void updateBlockStateUnform(Level w, BlockPos pos, BlockState bs, int type) {
		w.setBlock(pos, bs.setValue(MultiBlockMachine.FORMED, false), 3);
	}

	protected void unlinkSlave(Level w, BlockPos pos) {
		BlockEntity tile = w.getBlockEntity(pos);
		if (tile instanceof MultiBlockMachineTileEntSlave) {
			MultiBlockMachineTileEntSlave slave = (MultiBlockMachineTileEntSlave) tile;
			int type = slave.getSlaveType();
			slave.unform();
			if (!w.isClientSide) {
				BlockState bs = w.getBlockState(pos);
				if (bs.getBlock() instanceof MultiBlockMachine) {
					updateBlockStateUnform(w, pos, bs, type);
					slave.needUpdate();
				}
			}
		}
	}
}
