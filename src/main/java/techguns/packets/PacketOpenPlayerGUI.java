package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import techguns.gui.player.TGPlayerInventoryContainer;

/**
 * Client asks the server to open a player gui, guiID 0 = Techguns player inventory (the only player gui of 1.12)
 */
public class PacketOpenPlayerGUI extends TGPacket {
	private short guiID;

	public PacketOpenPlayerGUI(short guiID) {
		this.guiID = guiID;
	}

	public PacketOpenPlayerGUI() {
		this((short) 0);
	}

	public PacketOpenPlayerGUI(FriendlyByteBuf buf) {
		this.guiID = buf.readShort();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeShort(this.guiID);
	}

	@Override
	protected void handle(Player ply) {
		if (this.guiID == 0 && ply instanceof ServerPlayer sply && !sply.isSpectator()) {
			TGPlayerInventoryContainer.openForPlayer(sply);
		}
	}
}
