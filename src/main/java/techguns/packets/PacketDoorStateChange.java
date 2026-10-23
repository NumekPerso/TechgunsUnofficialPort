package techguns.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import techguns.tileentities.Door3x3TileEntity;

/**
 * Tells the clients a 3x3 door changed its state (start time of the opening/closing animation)
 */
public class PacketDoorStateChange extends TGPacket {
	protected BlockPos pos;

	public PacketDoorStateChange(BlockEntity tile) {
		this.pos = tile.getBlockPos();
	}

	public PacketDoorStateChange(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	protected void handle(Player ply) {
		BlockEntity tile = ply.level().getBlockEntity(this.pos);
		if (tile instanceof Door3x3TileEntity door) {
			door.setLastStateChangeTime(System.currentTimeMillis());
		}
	}
}
