package techguns.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import techguns.CommonProxy;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.audio.TGSound;
import techguns.client.particle.TGFXHandler;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.util.EntityCondition;
import techguns.util.TGSoundCategory;

public class ClientProxy extends CommonProxy {

	public boolean keyFirePressedMainhand;
	public boolean keyFirePressedOffhand;

	public float player_zoom = 1.0f;

	public long lastReloadsoundPlayed = 0L;

	public float PARTIAL_TICK_TIME;

	public float muzzleFlashJitterX = 0; //-1.0 to 1.0
	public float muzzleFlashJitterY = 0; //-1.0 to 1.0
	public float muzzleFlashJitterAngle = 0; //-1.0 to 1.0
	public float muzzleFlashJitterScale = 0; //-1.0 to 1.0

	public boolean hasStepassist = false;
	public boolean hasNightvision = false;

	public static ClientProxy get() {
		return (ClientProxy) techguns.Techguns.proxy;
	}

	@Override
	public void registerModBusListeners(IEventBus modBus) {
		TGClientSetup.registerModBusListeners(modBus);
	}

	@Override
	public TGExtendedPlayer createExtendedPlayer(Player ply) {
		if (ply.level().isClientSide) {
			return new TGExtendedPlayerClient(ply);
		}
		return new TGExtendedPlayer(ply);
	}

	@Override
	public Player getPlayerClient() {
		return Minecraft.getInstance().player;
	}

	@Override
	public RegistryAccess getClientRegistryAccess() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null) {
			return mc.level.registryAccess();
		}
		return super.getClientRegistryAccess();
	}

	@Override
	public float getPartialTickTime() {
		return PARTIAL_TICK_TIME;
	}

	@Override
	public float getPlayerZoom() {
		return player_zoom;
	}

	@Override
	public void setPlayerZoom(float zoom) {
		this.player_zoom = zoom;
	}

	@Override
	public void randomizeMuzzleFlashJitter(RandomSource rand) {
		this.muzzleFlashJitterX = 1.0f - (rand.nextFloat() * 2.0f);
		this.muzzleFlashJitterY = 1.0f - (rand.nextFloat() * 2.0f);
		this.muzzleFlashJitterScale = 1.0f - (rand.nextFloat() * 2.0f);
		this.muzzleFlashJitterAngle = 1.0f - (rand.nextFloat() * 2.0f);
	}

	/**
	 * x, y, scale, angle
	 */
	public float[] getMuzzleFlashJitter() {
		return new float[] { muzzleFlashJitterX, muzzleFlashJitterY, muzzleFlashJitterScale, muzzleFlashJitterAngle };
	}

	@Override
	public boolean isShiftKeyDown() {
		return Screen.hasShiftDown();
	}

	@Override
	public boolean isJumpKeyDown() {
		return Minecraft.getInstance().options.keyJump.isDown();
	}

	@Override
	public void handleSoundEvent(Player ply, int entityId, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, boolean playOnOwnPlayer, TGSoundCategory soundCategory, EntityCondition condition) {
		Entity entity = null;
		if (entityId != -1) {
			entity = ply.level().getEntity(entityId);
		}

		if (entity != null) {
			if (entity != ply || playOnOwnPlayer) {
				Minecraft.getInstance().getSoundManager().play(new TGSound(soundname, entity, volume, pitch, repeat, moving, gunPosition, soundCategory, condition));
			}
		}
	}

	@Override
	public void playSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, boolean playForOwnPlayer, TGSoundCategory category) {
		if (playForOwnPlayer || ent != this.getPlayerClient()) {
			this.playSoundOnEntity(ent, soundname, volume, pitch, repeat, moving, gunPosition, category);
		}
	}

	@Override
	public void playSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, boolean playForOwnPlayer, TGSoundCategory category, EntityCondition condition) {
		if (playForOwnPlayer || ent != this.getPlayerClient()) {
			this.playSoundOnEntity(ent, soundname, volume, pitch, repeat, moving, gunPosition, category, condition);
		}
	}

	@Override
	public void playSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, TGSoundCategory category) {
		Minecraft.getInstance().getSoundManager().play(new TGSound(soundname, ent, volume, pitch, repeat, moving, gunPosition, category));
	}

	@Override
	public void playSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, TGSoundCategory category, EntityCondition condition) {
		Minecraft.getInstance().getSoundManager().play(new TGSound(soundname, ent, volume, pitch, repeat, moving, gunPosition, category, condition));
	}

	@Override
	public void playReloadSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, TGSoundCategory category) {
		if (this.lastReloadsoundPlayed - System.currentTimeMillis() < -500) {
			this.lastReloadsoundPlayed = System.currentTimeMillis();
			this.playSoundOnEntity(ent, soundname, volume, pitch, repeat, moving, true, category);
		}
	}

	@Override
	public void playSoundOnPosition(SoundEvent soundname, float posx, float posy, float posz, float volume, float pitch, boolean repeat, TGSoundCategory soundCategory) {
		Minecraft.getInstance().getSoundManager().play(new TGSound(soundname, posx, posy, posz, volume, pitch, repeat, soundCategory));
	}

	@Override
	public void createFX(String name, Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		TGFXHandler.createFX(name, world, posX, posY, posZ, motionX, motionY, motionZ);
	}

	@Override
	public void createFX(String name, Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float pitch, float yaw) {
		TGFXHandler.createFX(name, world, posX, posY, posZ, motionX, motionY, motionZ, pitch, yaw);
	}

	@Override
	public void createFX(String name, Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float scale) {
		TGFXHandler.createFX(name, world, posX, posY, posZ, motionX, motionY, motionZ, scale);
	}

	@Override
	public void createFXOnEntity(String name, Entity ent) {
		TGFXHandler.createFXOnEntity(name, ent);
	}

	@Override
	public void createFXOnEntity(String name, Entity ent, float scale) {
		TGFXHandler.createFXOnEntity(name, ent, scale);
	}

	@Override
	public void createFXOnEntityWithOffset(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead, EntityCondition condition) {
		TGFXHandler.createFXOnEntityWithOffset(name, ent, offsetX, offsetY, offsetZ, attachToHead, condition);
	}

	@Override
	public void createLightPulse(double x, double y, double z, int lifetime, float rad_start, float rad_end, float r, float g, float b) {
		TGFXHandler.createLightPulse(x, y, z, lifetime, rad_start, rad_end, r, g, b);
	}

	@Override
	public void createLightPulse(double x, double y, double z, int fadeIn, int fadeOut, float rad_large, float rad_small, float r, float g, float b) {
		TGFXHandler.createLightPulse(x, y, z, fadeIn, fadeOut, rad_large, rad_small, r, g, b);
	}

	@Override
	public void handleEntityDeathType(LivingEntity entity, DeathType deathtype, float motionX, float motionY, float motionZ) {
		TGFXHandler.handleEntityDeathType(entity, deathtype, motionX, motionY, motionZ);
	}

	@Override
	public void handleAmbientEffectChange(LivingEntity elb, InteractionHand hand) {
		TGFXHandler.handleAmbientEffectChange(elb, hand);
	}

	@Override
	public void clearItemParticleSystemsHand(LivingEntity entity, InteractionHand hand) {
		TGFXHandler.clearItemParticleSystemsHand(entity, hand);
	}

	@Override
	public void setHasStepassist(boolean value) {
		this.hasStepassist = value;
	}

	@Override
	public void setHasNightvision(boolean value) {
		this.hasNightvision = value;
	}

	@Override
	public void setFlySpeed(float value) {
		Player ply = this.getPlayerClient();
		if (ply != null) {
			ply.getAbilities().setFlyingSpeed(value);
		}
	}

	@Override
	public boolean getHasStepassist() {
		return this.hasStepassist;
	}

	@Override
	public boolean getHasNightvision() {
		return this.hasNightvision;
	}

	@Override
	public boolean isClientPlayerAndIn1stPerson(LivingEntity ent) {
		return ent == this.getPlayerClient() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
	}

	@Override
	public boolean clientInRangeSquared(double posX, double posZ, double distSq) {
		Player ply = this.getPlayerClient();
		if (ply != null) {
			double dx = ply.getX() - posX;
			double dz = ply.getZ() - posZ;
			return (dx * dx + dz * dz) <= distSq;
		}
		return false;
	}
}
