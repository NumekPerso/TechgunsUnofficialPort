package techguns.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import techguns.Techguns;

/**
 * Base class for all Techguns packets. Subclasses provide a constructor taking a FriendlyByteBuf (decoder),
 * encode() and handle(). handle() runs on the main thread of the receiving side, with the player of the
 * receiving side (sender on the server, the local player on the client).
 */
public abstract class TGPacket {

	public abstract void encode(FriendlyByteBuf buf);

	protected abstract void handle(Player ply);

	public final void handleMessage(Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			Player ply = ctx.getDirection().getReceptionSide().isServer() ? ctx.getSender() : Techguns.proxy.getPlayerClient();
			if (ply != null) {
				this.handle(ply);
			}
		});
		ctx.setPacketHandled(true);
	}
}
