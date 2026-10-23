package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import techguns.Techguns;
import techguns.util.EntityCondition;

public class PacketSpawnParticleOnEntity extends TGPacket {
	String name;
	int entityID;
	float offsetX = 0.0f;
	float offsetY = 0.0f;
	float offsetZ = 0.0f;
	boolean attachToHead = false;
	EntityCondition condition;

	public PacketSpawnParticleOnEntity(String name, Entity ent) {
		this(name, ent, 0.0f, 0.0f, 0.0f, false, EntityCondition.NONE);
	}

	public PacketSpawnParticleOnEntity(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead) {
		this(name, ent, offsetX, offsetY, offsetZ, attachToHead, EntityCondition.NONE);
	}

	public PacketSpawnParticleOnEntity(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead, EntityCondition condition) {
		this.name = name;
		this.entityID = ent.getId();
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.attachToHead = attachToHead;
		this.condition = condition;
	}

	public PacketSpawnParticleOnEntity(FriendlyByteBuf buf) {
		this.name = buf.readUtf();
		this.entityID = buf.readInt();
		this.offsetX = buf.readFloat();
		this.offsetY = buf.readFloat();
		this.offsetZ = buf.readFloat();
		this.attachToHead = buf.readBoolean();
		this.condition = EntityCondition.fromByte(buf.readByte());
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(name);
		buf.writeInt(entityID);
		buf.writeFloat(this.offsetX);
		buf.writeFloat(this.offsetY);
		buf.writeFloat(this.offsetZ);
		buf.writeBoolean(this.attachToHead);
		buf.writeByte(condition == null ? EntityCondition.NONE.id : condition.id);
	}

	@Override
	protected void handle(Player ply) {
		Entity ent = ply.level().getEntity(this.entityID);
		if (ent != null) {
			Techguns.proxy.createFXOnEntityWithOffset(this.name, ent, this.offsetX, this.offsetY, this.offsetZ, this.attachToHead, this.condition);
		} else {
			Techguns.LOGGER.warn("Got Packet for FX " + this.name + " on Entity, but ent was null");
		}
	}
}
