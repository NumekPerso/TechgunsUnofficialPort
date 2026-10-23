package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import techguns.Techguns;

public class PacketNotifyAmbientEffectChange extends TGPacket {
	int entityId;
	InteractionHand hand;

	public PacketNotifyAmbientEffectChange(LivingEntity entity, InteractionHand hand) {
		this.entityId = entity.getId();
		this.hand = hand;
	}

	public PacketNotifyAmbientEffectChange(FriendlyByteBuf buf) {
		this.hand = buf.readBoolean() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		this.entityId = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(hand == InteractionHand.OFF_HAND);
		buf.writeInt(entityId);
	}

	@Override
	protected void handle(Player ply) {
		Entity ent = ply.level().getEntity(this.entityId);
		if (ent instanceof LivingEntity elb) {
			Techguns.proxy.handleAmbientEffectChange(elb, this.hand);
		}
	}
}
