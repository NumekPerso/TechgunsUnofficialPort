package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import techguns.client.ShooterValues;

public class ReloadStartedMessage extends TGPacket {
	protected int entityID;
	protected int time;
	protected byte attackType;
	protected boolean offHand;

	public ReloadStartedMessage(LivingEntity shooter, InteractionHand hand, int firetime, int attackType) {
		entityID = shooter.getId();
		time = firetime;
		this.attackType = (byte) attackType;
		this.offHand = hand == InteractionHand.OFF_HAND;
	}

	public ReloadStartedMessage(FriendlyByteBuf buf) {
		entityID = buf.readInt();
		time = buf.readInt();
		attackType = buf.readByte();
		this.offHand = buf.readBoolean();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeInt(time);
		buf.writeByte(attackType);
		buf.writeBoolean(offHand);
	}

	@Override
	protected void handle(Player ply) {
		Entity ent = ply.level().getEntity(this.entityID);
		if (ent instanceof LivingEntity shooter) {
			//the local player already started the reload clientside
			if (shooter != ply) {
				ShooterValues.setReloadtime(shooter, this.offHand, System.currentTimeMillis() + this.time, this.time, this.attackType);
			}
		}
	}
}
