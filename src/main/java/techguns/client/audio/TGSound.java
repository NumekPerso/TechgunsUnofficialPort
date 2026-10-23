package techguns.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import techguns.util.EntityCondition;
import techguns.util.MathUtil;
import techguns.util.TGSoundCategory;

public class TGSound extends AbstractTickableSoundInstance {

	Entity entity;
	boolean gunPosition;
	boolean moving;
	TGSoundCategory tgcategory = null;

	EntityCondition condition = EntityCondition.NONE;

	public TGSound(SoundEvent soundname, Entity entity, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, TGSoundCategory category, EntityCondition condition) {
		this(soundname, entity, volume, pitch, repeat, moving, gunPosition, category);
		if (condition != null) {
			this.condition = condition;
		}
	}

	public TGSound(SoundEvent soundname, Entity entity, float volume, float pitch, boolean repeat, boolean moving, boolean gunPosition, TGSoundCategory category) {
		this(soundname, entity, volume, pitch, repeat, moving, category);
		this.gunPosition = gunPosition;
		if (gunPosition) {
			if (entity instanceof LivingEntity) {
				this.applyGunPosition();
			}
		}
	}

	public TGSound(SoundEvent soundname, Entity entity, float volume, float pitch, boolean repeat, boolean moving, TGSoundCategory category) {
		this(soundname, entity, volume, pitch, repeat, category);
		this.moving = moving;
		if (entity != null) {
			this.x = (float) entity.getX();
			this.y = (float) entity.getY();
			this.z = (float) entity.getZ();
		}
	}

	public TGSound(SoundEvent soundname, Entity entity, float volume, float pitch, boolean repeat, TGSoundCategory category) {
		super(soundname, category.getVanillaCategory(), RandomSource.create());
		this.tgcategory = category;
		this.volume = volume;
		this.pitch = pitch;
		this.looping = repeat;
		this.entity = entity;
	}

	public TGSound(SoundEvent soundname, float posx, float posy, float posz, float volume, float pitch, boolean repeat, TGSoundCategory category) {
		this(soundname, null, volume, pitch, repeat, category);
		this.x = posx;
		this.y = posy;
		this.z = posz;
	}

	protected void applyGunPosition() {
		Vec2 pos = MathUtil.polarOffsetXZ((float) x, (float) z, 1.0f, (float) (((LivingEntity) entity).getYHeadRot() * Math.PI / 180.0f));
		this.x = pos.x;
		this.z = pos.y;
		this.y = this.y + (entity.getBbHeight() * 0.5f);
	}

	public void setDonePlaying() {
		this.looping = false;
		this.delay = 0;
		this.stop();
	}

	@Override
	public void tick() {
		if (entity != null && !condition.evaluate(entity)) {
			this.setDonePlaying();
			return;
		}

		if (moving && entity != null) {
			this.x = (float) entity.getX();
			this.y = (float) entity.getY();
			this.z = (float) entity.getZ();
			if (gunPosition) {
				if (entity instanceof LivingEntity) {
					this.applyGunPosition();
				}
			}
		} else if (moving) {
			this.setDonePlaying();
		}
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setRepeatDelay(int delay) {
		this.delay = delay;
	}
}
