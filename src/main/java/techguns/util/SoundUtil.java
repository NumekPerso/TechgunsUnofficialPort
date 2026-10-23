package techguns.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.packets.PacketPlaySound;

public class SoundUtil {

	/**
	 * Plays a moving Sound on the specified Entity, in front of the entity.
	 */
	public static void playSoundOnEntityGunPosition(Level world, Entity entity, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean playOnOwnPlayer, TGSoundCategory category, EntityCondition condition) {
		if (!world.isClientSide) {
			TGPackets.network.sendToAllAround(new PacketPlaySound(soundname, entity, volume, pitch, repeat, moving, true, playOnOwnPlayer, category, condition), TGPackets.targetPointAroundEnt(entity, 100.0f));
		} else {
			Techguns.proxy.playSoundOnEntity(entity, soundname, volume, pitch, repeat, moving, true, true, category, condition);
		}
	}

	/**
	 * Plays a moving Sound on the specified Entity, in front of the entity.
	 */
	public static void playSoundOnEntityGunPosition(Level world, Entity entity, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean playOnOwnPlayer, TGSoundCategory category) {
		if (!world.isClientSide) {
			TGPackets.network.sendToAllAround(new PacketPlaySound(soundname, entity, volume, pitch, repeat, moving, true, playOnOwnPlayer, category), TGPackets.targetPointAroundEnt(entity, 100.0f));
		} else {
			Techguns.proxy.playSoundOnEntity(entity, soundname, volume, pitch, repeat, moving, true, true, category);
		}
	}

	/**
	 * Plays a moving Sound on the specified Entity, in front of the entity.
	 */
	public static void playSoundOnEntityGunPosition(Level world, Entity entity, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, TGSoundCategory category) {
		playSoundOnEntityGunPosition(world, entity, soundname, volume, pitch, repeat, moving, false, category);
	}

	/**
	 * Like playSoundOnEntityGunPosition() but does a check for last played own reload.
	 */
	public static void playReloadSoundOnEntity(Level world, Entity entity, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, TGSoundCategory category) {
		if (!world.isClientSide) {
			TGPackets.network.sendToAllAround(new PacketPlaySound(soundname, entity, volume, pitch, repeat, moving, true, category), TGPackets.targetPointAroundEnt(entity, 100.0f));
		} else {
			Techguns.proxy.playReloadSoundOnEntity(entity, soundname, volume, pitch, repeat, moving, category);
		}
	}

	/**
	 * Plays a moving Sound on the specified Entity
	 */
	public static void playSoundOnEntity(Level world, Entity entity, SoundEvent soundname, float volume, float pitch, boolean repeat, TGSoundCategory category) {
		if (!world.isClientSide) {
			TGPackets.network.sendToAllAround(new PacketPlaySound(soundname, entity, volume, pitch, repeat, true, category), TGPackets.targetPointAroundEnt(entity, 100.0f));
		} else {
			Techguns.proxy.playSoundOnEntity(entity, soundname, volume, pitch, repeat, true, false, category);
		}
	}

	/**
	 * Plays a non-moving Sound at the specified Entity's position
	 */
	public static void playSoundAtEntityPos(Level world, Entity entity, SoundEvent soundname, float volume, float pitch, boolean repeat, TGSoundCategory category) {
		if (!world.isClientSide) {
			TGPackets.network.sendToAllAround(new PacketPlaySound(soundname, entity, volume, pitch, repeat, false, category), TGPackets.targetPointAroundEnt(entity, 100.0f));
		} else {
			Techguns.proxy.playSoundOnEntity(entity, soundname, volume, pitch, repeat, false, false, category);
		}
	}
}
