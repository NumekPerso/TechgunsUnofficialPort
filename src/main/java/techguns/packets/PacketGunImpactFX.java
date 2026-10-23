package techguns.packets;

import java.util.BitSet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.util.DataUtil;

public class PacketGunImpactFX extends TGPacket {
	short soundType;
	double loc_x;
	double loc_y;
	double loc_z;
	float pitch;
	float yaw;
	boolean incendiary = false;

	public PacketGunImpactFX(short soundType, double loc_x, double loc_y, double loc_z, float pitch, float yaw) {
		this.soundType = soundType;
		this.loc_x = loc_x;
		this.loc_y = loc_y;
		this.loc_z = loc_z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public PacketGunImpactFX(short soundType, double loc_x, double loc_y, double loc_z, float pitch, float yaw, boolean inc) {
		this(soundType, loc_x, loc_y, loc_z, pitch, yaw);
		this.incendiary = inc;
	}

	public PacketGunImpactFX(FriendlyByteBuf buf) {
		this.soundType = buf.readShort();
		this.loc_x = buf.readDouble();
		this.loc_y = buf.readDouble();
		this.loc_z = buf.readDouble();
		this.pitch = buf.readFloat();
		this.yaw = buf.readFloat();
		BitSet bs = DataUtil.uncompress(buf.readByte());
		this.incendiary = bs.get(0);
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeShort(soundType);
		buf.writeDouble(loc_x);
		buf.writeDouble(loc_y);
		buf.writeDouble(loc_z);
		buf.writeFloat(pitch);
		buf.writeFloat(yaw);
		buf.writeByte(DataUtil.compress(incendiary));
	}

	@Override
	protected void handle(Player ply) {
		double x = this.loc_x;
		double y = this.loc_y;
		double z = this.loc_z;
		Level world = ply.level();
		boolean distdelay = true;

		if (this.incendiary) {
			Techguns.proxy.createFX("Impact_IncendiaryBullet", world, x, y, z, 0.0D, 0.0D, 0.0D, this.pitch, this.yaw);
		}

		if (this.soundType == 0) { //stone
			world.playLocalSound(x, y, z, TGSounds.BULLET_IMPACT_STONE, SoundSource.AMBIENT, 1.0f, 1.0f, distdelay);
			Techguns.proxy.createFX("Impact_BulletRock", world, x, y, z, 0.0D, 0.0D, 0.0D, this.pitch, this.yaw);
		} else if (this.soundType == 1) { //wood
			world.playLocalSound(x, y, z, TGSounds.BULLET_IMPACT_WOOD, SoundSource.AMBIENT, 1.0f, 1.0f, distdelay);
			Techguns.proxy.createFX("Impact_BulletWood", world, x, y, z, 0.0D, 0.0D, 0.0D, this.pitch, this.yaw);
		} else if (this.soundType == 2) { //glass
			world.playLocalSound(x, y, z, TGSounds.BULLET_IMPACT_GLASS, SoundSource.AMBIENT, 1.0f, 1.0f, distdelay);
			Techguns.proxy.createFX("Impact_BulletGlass", world, x, y, z, 0.0D, 0.0D, 0.0D, this.pitch, this.yaw);
		} else if (this.soundType == 3) { //metal
			world.playLocalSound(x, y, z, TGSounds.BULLET_IMPACT_METAL, SoundSource.AMBIENT, 1.0f, 1.0f, distdelay);
			Techguns.proxy.createFX("Impact_BulletMetal", world, x, y, z, 0.0D, 0.0D, 0.0D, this.pitch, this.yaw);
		} else if (this.soundType == 4) { //dirt
			world.playLocalSound(x, y, z, TGSounds.BULLET_IMPACT_DIRT, SoundSource.AMBIENT, 1.0f, 1.0f, distdelay);
			Techguns.proxy.createFX("Impact_BulletDirt", world, x, y, z, 0.0D, 0.0D, 0.0D, this.pitch, this.yaw);
		} else if (this.soundType == 5) { //dirt, only sound
			world.playLocalSound(x, y, z, TGSounds.BULLET_IMPACT_DIRT, SoundSource.AMBIENT, 1.0f, 1.0f, distdelay);
		} else if (this.soundType == -1) { //default
			world.playLocalSound(x, y, z, TGSounds.BULLET_IMPACT_DIRT, SoundSource.AMBIENT, 1.0f, 1.0f, distdelay);
			Techguns.proxy.createFX("Impact_BulletDefault", world, x, y, z, 0.0D, 0.0D, 0.0D, this.pitch, this.yaw);
		}
	}
}
