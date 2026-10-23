package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import techguns.TGConfig;
import techguns.Techguns;
import techguns.deatheffects.EntityDeathUtils.DeathType;

public class PacketEntityDeathType extends TGPacket {
	int entityId;
	int deathtypeId;
	float motionX;
	float motionY;
	float motionZ;

	public PacketEntityDeathType(LivingEntity entity, DeathType deathtype) {
		this.entityId = entity.getId();
		this.deathtypeId = deathtype != null ? deathtype.getValue() : DeathType.DEFAULT.getValue();
		Vec3 motion = entity.getDeltaMovement();
		this.motionX = (float) motion.x;
		this.motionY = (float) motion.y;
		this.motionZ = (float) motion.z;
	}

	public PacketEntityDeathType(FriendlyByteBuf buf) {
		this.entityId = buf.readInt();
		this.deathtypeId = buf.readInt();
		this.motionX = buf.readFloat();
		this.motionY = buf.readFloat();
		this.motionZ = buf.readFloat();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeInt(this.deathtypeId);
		buf.writeFloat(this.motionX);
		buf.writeFloat(this.motionY);
		buf.writeFloat(this.motionZ);
	}

	@Override
	protected void handle(Player ply) {
		if (TGConfig.cl_enableDeathFX) {
			Entity ent = ply.level().getEntity(this.entityId);
			DeathType deathtype = DeathType.values()[this.deathtypeId];

			if (deathtype != DeathType.GORE || TGConfig.cl_enableDeathFX_Gore) {
				if (ent instanceof LivingEntity entity) {
					entity.setDeltaMovement(this.motionX, this.motionY, this.motionZ);
					Techguns.proxy.handleEntityDeathType(entity, deathtype, this.motionX, this.motionY, this.motionZ);
				}
			}
		}
	}
}
