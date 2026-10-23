package techguns.packets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import techguns.Techguns;
import techguns.util.EntityCondition;
import techguns.util.TGSoundCategory;

public class PacketPlaySound extends TGPacket {
	String soundname;
	int entityId;
	float volume;
	float pitch;
	boolean repeat;
	boolean moving;
	boolean gunPosition = false;
	boolean playOnOwnPlayer = false;
	int soundx = 0;
	short soundy = 0;
	int soundz = 0;
	TGSoundCategory category;
	EntityCondition condition;

	public PacketPlaySound(SoundEvent soundname, Entity entity, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, boolean playOnOwnPlayer, TGSoundCategory category) {
		this(soundname, entity, volume, pitch, repeat, moving, gunPosition, category);
		this.playOnOwnPlayer = playOnOwnPlayer;
	}

	public PacketPlaySound(SoundEvent soundname, Entity entity, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, boolean playOnOwnPlayer, TGSoundCategory category, EntityCondition condition) {
		this(soundname, entity, volume, pitch, repeat, moving, gunPosition, category);
		this.playOnOwnPlayer = playOnOwnPlayer;
		this.condition = condition;
	}

	public PacketPlaySound(SoundEvent soundname, Entity entity, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, TGSoundCategory category) {
		this(soundname, entity, volume, pitch, repeat, moving, category);
		this.gunPosition = gunPosition;
	}

	public PacketPlaySound(SoundEvent soundname, Entity entity, float volume, float pitch, boolean repeat, boolean moving, TGSoundCategory category) {
		if (entity == null) {
			this.entityId = -1;
		} else {
			this.entityId = entity.getId();
		}
		this.soundname = soundname.getLocation().toString();
		this.volume = volume;
		this.pitch = pitch;
		this.repeat = repeat;
		this.moving = moving;
		this.category = category;
	}

	public PacketPlaySound(SoundEvent soundname, int posx, int posy, int posz, float volume, float pitch, boolean repeat, TGSoundCategory category) {
		this(soundname, null, volume, pitch, repeat, false, false, category);
		this.soundx = posx;
		this.soundy = (short) posy;
		this.soundz = posz;
	}

	public PacketPlaySound(FriendlyByteBuf buf) {
		this.entityId = buf.readInt();
		this.volume = buf.readFloat();
		this.pitch = buf.readFloat();
		this.repeat = buf.readBoolean();
		this.moving = buf.readBoolean();
		this.gunPosition = buf.readBoolean();
		this.playOnOwnPlayer = buf.readBoolean();
		this.soundx = buf.readInt();
		this.soundy = buf.readShort();
		this.soundz = buf.readInt();
		this.category = TGSoundCategory.get(buf.readByte());
		this.condition = EntityCondition.fromByte(buf.readByte());
		this.soundname = buf.readUtf();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeFloat(volume);
		buf.writeFloat(pitch);
		buf.writeBoolean(repeat);
		buf.writeBoolean(moving);
		buf.writeBoolean(gunPosition);
		buf.writeBoolean(playOnOwnPlayer);
		buf.writeInt(soundx);
		buf.writeShort(soundy);
		buf.writeInt(soundz);
		buf.writeByte(category.getId());
		buf.writeByte(condition == null ? EntityCondition.NONE.id : condition.id);
		buf.writeUtf(soundname);
	}

	@Override
	protected void handle(Player ply) {
		SoundEvent event = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(this.soundname));
		if (event == null) {
			return;
		}
		if (this.entityId != -1) {
			Techguns.proxy.handleSoundEvent(ply, this.entityId, event, this.volume, this.pitch, this.repeat, this.moving, this.gunPosition, this.playOnOwnPlayer, this.category, this.condition);
		} else {
			Techguns.proxy.playSoundOnPosition(event, this.soundx, this.soundy, this.soundz, this.volume, this.pitch, this.repeat, this.category);
		}
	}
}
