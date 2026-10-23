package techguns.client;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import techguns.TGSounds;
import techguns.client.audio.TGSound;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.particle.ITGParticle;
import techguns.client.particle.ITGParticleAttachments;
import techguns.client.particle.TGParticleSystem;
import techguns.util.EntityCondition;
import techguns.util.SoundUtil;
import techguns.util.TGSoundCategory;

/**
 * Client side version of the player capability, holds client only state (attached particle systems, sounds).
 */
public class TGExtendedPlayerClient extends TGExtendedPlayer implements ITGParticleAttachments {

	// Client only fields
	public TGSound gliderLoop = null;

	public TGSound jetPackLoop = null;

	protected List<ITGParticle> entityParticles = null;
	protected List<TGParticleSystem> particleSysMH=null;
	protected List<TGParticleSystem> particleSysOH=null;
	protected List<ITGParticle> entityParticlesMH = null;
	protected List<ITGParticle> entityParticlesOH = null;

	//used to count down from 20 since last tick when radiation was applied
	public int radiationticks = 0;

	public TGExtendedPlayerClient(Player entity) {
		super(entity);
	}

	public static TGExtendedPlayerClient get(Player ply) {
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props instanceof TGExtendedPlayerClient) {
			return (TGExtendedPlayerClient) props;
		}
		return null;
	}

	@Override
	public void copyFrom(TGExtendedPlayer other) {
		super.copyFrom(other);
		if (other instanceof TGExtendedPlayerClient other_cl) {
			this.gliderLoop = other_cl.gliderLoop;
			this.jetPackLoop = other_cl.jetPackLoop;
		}
	}

	@Override
	public void setJumpkeyPressed(boolean isJumpkeyPressed) {
		if (this.entity.level().isClientSide) {
			if (isJumpkeyPressed && jetPackLoop == null) {
				jetPackLoop = new TGSound(TGSounds.JETPACK_LOOP, this.entity, 0.75f, 1.0f, true, true, false, TGSoundCategory.PLAYER_EFFECT, EntityCondition.ENTITY_ALIVE);
				SoundUtil.playSoundAtEntityPos(this.entity.level(), this.entity, TGSounds.JETPACK_START, 0.75f, 1.0f, false, TGSoundCategory.PLAYER_EFFECT);
				Minecraft.getInstance().getSoundManager().play(jetPackLoop);
			}

			if (!isJumpkeyPressed && this.isJumpkeyPressed) {
				if (jetPackLoop != null) {
					jetPackLoop.setDonePlaying();
					jetPackLoop = null;
					//1.12 also played TGSounds.JETPACK_END here, that sound does not exist in the 1.12 assets either
				}
			}
		}
		this.isJumpkeyPressed = isJumpkeyPressed;
	}

	/**
	 * 1.12 ClientProxy.handlePlayerGliding
	 */
	@Override
	public void handlePlayerGliding() {
		if (this.isGliding) {
			if (this.gliderLoop == null) {
				this.gliderLoop = new TGSound(TGSounds.GLIDER_LOOP, this.entity, 0.75f, 1.0f, true, true, false, TGSoundCategory.PLAYER_EFFECT, EntityCondition.ENTITY_ALIVE);
				Minecraft.getInstance().getSoundManager().play(this.gliderLoop);
			}
		} else {
			if (this.gliderLoop != null) {
				this.gliderLoop.setDonePlaying();
				this.gliderLoop = null;
			}
		}
	}

	@Override
	public List<ITGParticle> getEntityParticles() {
		return this.entityParticles;
	}

	@Override
	public List<TGParticleSystem> getParticleSysMainhand() {
		return this.particleSysMH;
	}

	@Override
	public List<TGParticleSystem> getParticleSysOffhand() {
		return this.particleSysOH;
	}

	@Override
	public List<ITGParticle> getOrInitEntityParticles() {
		if (this.entityParticles==null) this.entityParticles = new LinkedList<ITGParticle>();
		return this.entityParticles;
	}

	@Override
	public List<TGParticleSystem> getOrInitParticleSysMainhand() {
		if(this.particleSysMH==null) this.particleSysMH = new LinkedList<>();
		return this.particleSysMH;
	}

	@Override
	public List<TGParticleSystem> getOrInitParticleSysOffhand() {
		if(this.particleSysOH==null) this.particleSysOH = new LinkedList<>();
		return this.particleSysOH;
	}

	@Override
	public List<ITGParticle> getEntityParticlesMH() {
		return this.entityParticlesMH;
	}

	@Override
	public List<ITGParticle> getEntityParticlesOH() {
		return this.entityParticlesOH;
	}

	@Override
	public List<ITGParticle> getOrInitEntityParticlesOH() {
		if(this.entityParticlesOH==null) this.entityParticlesOH = new LinkedList<ITGParticle>();
		return this.entityParticlesOH;
	}

	@Override
	public List<ITGParticle> getOrInitEntityParticlesMH() {
		if(this.entityParticlesMH==null) this.entityParticlesMH = new LinkedList<ITGParticle>();
		return this.entityParticlesMH;
	}
}
