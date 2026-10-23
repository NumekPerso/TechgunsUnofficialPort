package techguns.radiation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import techguns.capabilities.TGExtendedPlayer;
import techguns.packets.TGPacket;

/**
 * Server -> client: RAD level of the local player (1.12 computed it on both sides with the radiation effect)
 */
public class PacketRadiationSync extends TGPacket {

	public int radlevel;

	public PacketRadiationSync(int radlevel) {
		this.radlevel = radlevel;
	}

	public PacketRadiationSync(FriendlyByteBuf buf) {
		this.radlevel = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.radlevel);
	}

	@Override
	protected void handle(Player ply) {
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props != null) {
			props.radlevel = this.radlevel;
		}
	}
}
