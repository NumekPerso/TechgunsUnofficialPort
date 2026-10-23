package techguns;

import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import techguns.capabilities.TGExtendedPlayer;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.util.EntityCondition;
import techguns.util.TGSoundCategory;

/**
 * Side abstraction like in 1.12: common code calls Techguns.proxy, the client implementation does sounds, effects and rendering state.
 * All methods are no-ops / defaults on the dedicated server.
 */
public abstract class CommonProxy {

	public void registerModBusListeners(IEventBus modBus) {
	}

	public TGExtendedPlayer createExtendedPlayer(Player ply) {
		return new TGExtendedPlayer(ply);
	}

	/**
	 * Return player on client, null on server
	 */
	public Player getPlayerClient() {
		return null;
	}

	public RegistryAccess getClientRegistryAccess() {
		throw new IllegalStateException("No registry access available on the server without a running server");
	}

	public float getPartialTickTime() {
		return 1.0f;
	}

	public float getPlayerZoom() {
		return 1.0f;
	}

	public void setPlayerZoom(float zoom) {
	}

	public void randomizeMuzzleFlashJitter(RandomSource rand) {
	}

	public boolean isShiftKeyDown() {
		return false;
	}

	/**
	 * Client: state of the vanilla jump key (jetpack)
	 */
	public boolean isJumpKeyDown() {
		return false;
	}

	public void handleSoundEvent(Player ply, int entityId, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, boolean playOnOwnPlayer, TGSoundCategory soundCategory, EntityCondition condition) {
	}

	public void playSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, TGSoundCategory category) {
	}

	public void playSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, TGSoundCategory category, EntityCondition condition) {
	}

	public void playSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, boolean playForOwnPlayer, TGSoundCategory category) {
	}

	public void playSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, boolean playForOwnPlayer, TGSoundCategory category, EntityCondition condition) {
	}

	/**
	 * Client only: plays the reload sound of the own player, at most every 500ms
	 */
	public void playReloadSoundOnEntity(Entity ent, SoundEvent soundname, float volume, float pitch, boolean repeat, boolean moving, TGSoundCategory category) {
	}

	public void playSoundOnPosition(SoundEvent soundname, float posx, float posy, float posz, float volume, float pitch, boolean repeat, TGSoundCategory soundCategory) {
	}

	public void createFX(String name, Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
	}

	public void createFX(String name, Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float pitch, float yaw) {
	}

	public void createFX(String name, Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float scale) {
	}

	public void createFXOnEntity(String name, Entity ent) {
	}

	public void createFXOnEntity(String name, Entity ent, float scale) {
	}

	public void createFXOnEntityWithOffset(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead, EntityCondition condition) {
	}

	public void createLightPulse(double x, double y, double z, int lifetime, float rad_start, float rad_end, float r, float g, float b) {
	}

	public void createLightPulse(double x, double y, double z, int fadeIn, int fadeOut, float rad_large, float rad_small, float r, float g, float b) {
	}

	/**
	 * Client: start the death effect of an entity
	 */
	public void handleEntityDeathType(LivingEntity entity, DeathType deathtype, float motionX, float motionY, float motionZ) {
	}

	/**
	 * Client: (re)start the ambient particle effect of the item held in hand
	 */
	public void handleAmbientEffectChange(LivingEntity elb, InteractionHand hand) {
	}

	public void clearItemParticleSystemsHand(LivingEntity entity, InteractionHand hand) {
	}

	public void setHasStepassist(boolean value) {
	}

	public void setHasNightvision(boolean value) {
	}

	public void setFlySpeed(float value) {
	}

	public boolean getHasStepassist() {
		return false;
	}

	public boolean getHasNightvision() {
		return false;
	}

	public boolean isClientPlayerAndIn1stPerson(LivingEntity ent) {
		return false;
	}

	public boolean clientInRangeSquared(double posX, double posZ, double distSq) {
		return false;
	}

	public void handlePlayerGliding(Player player) {
	}
}
