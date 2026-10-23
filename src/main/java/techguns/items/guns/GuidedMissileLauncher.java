package techguns.items.guns;

import java.util.List;
import java.util.Optional;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.capabilities.TGExtendedPlayer;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.GuidedMissileProjectile;
import techguns.util.SoundUtil;
import techguns.util.TGSoundCategory;

/**
 * Lock on launcher: holding the use key "charges" (traces the target on the client every tick), the gun fires with the attack key
 * while charging (canFireWhileCharging), the locked target is sent to the server with PacketShootGunTarget (see TGClientEvents).
 */
public class GuidedMissileLauncher extends GenericGunCharge {

	public static final float LOCK_RANGE = 150.0f;
	public static final float LOCK_ERROR_THRESHOLD = 0.5f;

	@SuppressWarnings("rawtypes")
	public GuidedMissileLauncher(String name, ChargedProjectileSelector projectile_selector, boolean semiAuto,
			int minFiretime, int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound,
			int TTL, float accuracy, float fullChargeTime, int ammoConsumedOnFullCharge) {
		super(name, projectile_selector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound, reloadsound, TTL,
				accuracy, fullChargeTime, ammoConsumedOnFullCharge);
	}

	@Override
	public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
		super.onUseTick(level, player, stack, count);

		if (player.level().isClientSide && player instanceof Player ply) {
			TGExtendedPlayer epc = TGExtendedPlayer.get(ply);
			if (epc == null) {
				return;
			}
			traceTarget(player);
			//Handle sounds
			if (epc.lockOnTicks >= this.lockOnTicks) {
				if (count % 4 == 0) SoundUtil.playSoundOnEntityGunPosition(player.level(), player, TGSounds.LOCKED_BEEP, SOUND_DISTANCE, 1.0F, false, false, TGSoundCategory.PLAYER_EFFECT);
			} else if (epc.lockOnTicks > 0) {
				if (count % 4 == 0) SoundUtil.playSoundOnEntityGunPosition(player.level(), player, TGSounds.LOCKON_BEEP, SOUND_DISTANCE, 1.0F, false, false, TGSoundCategory.PLAYER_EFFECT);
			} else {
				if (count % 12 == 0) SoundUtil.playSoundOnEntityGunPosition(player.level(), player, TGSounds.LOCKON_BEEP, SOUND_DISTANCE, 0.5F, false, false, TGSoundCategory.PLAYER_EFFECT);
			}
		}
	}

	@Override
	protected void startCharge(ItemStack item, Level world, Player player) {
		super.startCharge(item, world, player);
	}

	protected void traceTarget(LivingEntity shooter) {
		Vec3 vec3d1 = new Vec3(shooter.getX(), shooter.getY() + shooter.getEyeHeight(), shooter.getZ());
		Vec3 vec3d = vec3d1.add(shooter.getLookAngle().scale(LOCK_RANGE));

		HitResult raytraceresult = shooter.level().clip(new ClipContext(vec3d1, vec3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
		if (raytraceresult.getType() == HitResult.Type.MISS) {
			raytraceresult = null;
		}

		if (raytraceresult != null) {
			vec3d = raytraceresult.getLocation();
		}

		Entity entity = this.findEntityOnPath(shooter, vec3d1, vec3d);

		if (entity != null) {
			raytraceresult = new EntityHitResult(entity);
		}

		if (raytraceresult instanceof EntityHitResult ehr && ehr.getEntity() instanceof Player entityplayer) {
			if (shooter instanceof Player && !((Player) shooter).canHarmPlayer(entityplayer)) {
				raytraceresult = null;
			}
		}

		if (shooter instanceof Player) {
			TGExtendedPlayer epc = TGExtendedPlayer.get((Player) shooter);
			Entity hit = raytraceresult instanceof EntityHitResult ehr2 ? ehr2.getEntity() : null;
			if (hit != null) {
				if (epc.lockOnEntity != null && epc.lockOnEntity.isAlive()) {
					if (epc.lockOnEntity == hit) {
						epc.lockOnTicks++;
						if (epc.lockOnTicks >= this.lockOnTicks) {
							//LOCK COMPLETED!
							epc.lockOnTicks = this.lockOnTicks + this.lockOnPersistTicks;
						}
					} else {
						if (epc.lockOnTicks > 0) {
							epc.lockOnTicks--;
						} else {
							epc.lockOnEntity = null;
							epc.lockOnTicks = 0;
						}
					}
				} else {
					epc.lockOnEntity = hit;
					epc.lockOnTicks = 1;
				}
			} else {
				if (epc.lockOnEntity != null && !epc.lockOnEntity.isAlive()) {
					epc.lockOnEntity = null;
					epc.lockOnTicks = 0;
				} else {
					if (epc.lockOnTicks > 0) {
						epc.lockOnTicks--;
					} else {
						epc.lockOnEntity = null;
					}
				}
			}
		}
	}

	protected Entity findEntityOnPath(LivingEntity shooter, Vec3 start, Vec3 end) {
		Entity entity = null;
		Vec3 ray = shooter.getLookAngle().scale(LOCK_RANGE);
		List<Entity> list = shooter.level().getEntities(shooter, shooter.getBoundingBox().expandTowards(ray.x, ray.y, ray.z).inflate(1.0D), GenericProjectile.BULLET_TARGETS);
		double d0 = 0.0D;

		Entity prevTarget = null;
		if (shooter instanceof Player) {
			TGExtendedPlayer epc = TGExtendedPlayer.get((Player) shooter);
			if (epc != null) {
				prevTarget = epc.lockOnEntity;
			}
		}

		for (int i = 0; i < list.size(); ++i) {
			Entity entity1 = list.get(i);

			AABB axisalignedbb = entity1.getBoundingBox().inflate(LOCK_ERROR_THRESHOLD);
			Optional<Vec3> raytraceresult = axisalignedbb.clip(start, end);

			if (raytraceresult.isPresent()) {
				if (prevTarget == null || entity != prevTarget) {
					if (entity1 == prevTarget) {
						entity = prevTarget;
					} else {
						double d1 = start.distanceToSqr(raytraceresult.get());

						if (d1 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}
		}

		return entity;
	}

	@Override
	protected void spawnProjectile(Level world, LivingEntity player, ItemStack itemstack, float spread,
			float offset, float damagebonus, EnumBulletFirePos firePos, Entity target) {
		IProjectileFactory<GenericProjectile> projectile = this.projectile_selector.getFactoryForType(this.getCurrentAmmoVariantKey(itemstack));

		GenericProjectile proj = projectile.createProjectile(this, world, player, damage * damagebonus, speed, this.getScaledTTL(), spread, this.damageDropStart,
				this.damageDropEnd, this.damageMin * damagebonus, this.penetration, getDoBlockDamage(player), firePos, radius, gravity);

		float f = 1.0f;
		if (this.muzzelight) {
			Vec3 look = player.getLookAngle();
			Techguns.proxy.createLightPulse(proj.getX() + look.x * f, proj.getY() + look.y * f, proj.getZ() + look.z * f, this.light_lifetime, this.light_radius_start, this.light_radius_end, this.light_r, this.light_g, this.light_b);
		}
		if (silenced) {
			proj.setSilenced();
		}
		if (offset > 0.0f) {
			proj.shiftForward(offset / speed);
		}

		if (proj instanceof GuidedMissileProjectile missile) {
			missile.target = target;
		}

		world.addFreshEntity(proj);
	}

	@Override
	public int consumeAmmoCharge(ItemStack item, float f, boolean creative) {
		return 0;
	}
}
