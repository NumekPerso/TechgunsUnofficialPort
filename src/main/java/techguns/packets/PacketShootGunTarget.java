package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.api.guns.IGenericGun;

/**
 * Tells the server that the player who sent this message wants to shoot with his current gun at a locked target
 */
public class PacketShootGunTarget extends TGPacket {
	public boolean isZooming = false;
	public boolean offHand = false;
	public int entityId = -1;

	public PacketShootGunTarget(boolean isZooming, InteractionHand hand, Entity target) {
		this.isZooming = isZooming;
		this.offHand = hand == InteractionHand.OFF_HAND;
		this.entityId = target.getId();
	}

	public PacketShootGunTarget(FriendlyByteBuf buf) {
		this.isZooming = buf.readBoolean();
		this.offHand = buf.readBoolean();
		this.entityId = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(isZooming);
		buf.writeBoolean(offHand);
		buf.writeInt(entityId);
	}

	public InteractionHand getHand() {
		return offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}

	@Override
	protected void handle(Player ply) {
		ItemStack stack = ply.getItemInHand(this.getHand());
		Entity target = ply.level().getEntity(this.entityId);
		if (!stack.isEmpty() && stack.getItem() instanceof IGenericGun) {
			((IGenericGun) stack.getItem()).shootGunPrimary(stack, ply.level(), ply, this.isZooming, this.getHand(), target);
		}
	}
}
