package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import techguns.Techguns;

public class PacketSpawnParticle extends TGPacket {
	String name;
	double posX;
	double posY;
	double posZ;
	double motionX = 0.0D;
	double motionY = 0.0D;
	double motionZ = 0.0D;
	float scale = 1.0f;

	public PacketSpawnParticle(String name, double posX, double posY, double posZ) {
		this.name = name;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	public PacketSpawnParticle(String name, double posX, double posY, double posZ, float scale) {
		this(name, posX, posY, posZ);
		this.scale = scale;
	}

	public PacketSpawnParticle(String name, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		this(name, posX, posY, posZ);
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
	}

	public PacketSpawnParticle(String name, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float scale) {
		this(name, posX, posY, posZ, motionX, motionY, motionZ);
		this.scale = scale;
	}

	public PacketSpawnParticle(FriendlyByteBuf buf) {
		this.name = buf.readUtf();
		this.posX = buf.readDouble();
		this.posY = buf.readDouble();
		this.posZ = buf.readDouble();
		this.motionX = buf.readDouble();
		this.motionY = buf.readDouble();
		this.motionZ = buf.readDouble();
		this.scale = buf.readFloat();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(name);
		buf.writeDouble(posX);
		buf.writeDouble(posY);
		buf.writeDouble(posZ);
		buf.writeDouble(motionX);
		buf.writeDouble(motionY);
		buf.writeDouble(motionZ);
		buf.writeFloat(scale);
	}

	@Override
	protected void handle(Player ply) {
		Techguns.proxy.createFX(this.name, ply.level(), this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, this.scale);
	}
}
