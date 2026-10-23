package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import techguns.capabilities.TGExtendedPlayer;

public class PacketSwapWeapon extends TGPacket {
	int plyId;

	public PacketSwapWeapon(Player ply) {
		this.plyId = ply.getId();
	}

	public PacketSwapWeapon(FriendlyByteBuf buf) {
		this.plyId = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(plyId);
	}

	@Override
	protected void handle(Player ply) {
		Entity ent = ply.level().getEntity(this.plyId);
		if (ent instanceof Player p) {
			TGExtendedPlayer caps = TGExtendedPlayer.get(p);
			if (caps != null) {
				caps.swapAttackTimes();
			}
		}
	}
}
