package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.api.guns.IGenericGun;

/**
 * Tells the server that the player who sent this message wants to shoot with his current gun
 */
public class PacketShootGun extends TGPacket {
	public boolean isZooming = false;
	public boolean offHand = false;

	public PacketShootGun(boolean isZooming, InteractionHand hand) {
		this.isZooming = isZooming;
		this.offHand = hand == InteractionHand.OFF_HAND;
	}

	public PacketShootGun(FriendlyByteBuf buf) {
		this.isZooming = buf.readBoolean();
		this.offHand = buf.readBoolean();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(isZooming);
		buf.writeBoolean(offHand);
	}

	public InteractionHand getHand() {
		return offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}

	@Override
	protected void handle(Player ply) {
		ItemStack stack = ply.getItemInHand(this.getHand());
		if (!stack.isEmpty() && stack.getItem() instanceof IGenericGun) {
			((IGenericGun) stack.getItem()).shootGunPrimary(stack, ply.level(), ply, this.isZooming, this.getHand(), null);
		}
	}
}
