package techguns.entities.projectiles;

import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import techguns.TGEntities;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.util.MathUtil;

public class TeslaProjectile extends AbstractBeamProjectile {

	public static final int TTL = 10;
	public static final float CHAIN_RANGE = 8.0f;
	public static final int CHAIN_TARGETS = 4;
	public static final float CHAIN_DAMAGE_FACTOR = 0.75f;

	/**
	 * Random seed of the lightning bolt shape (not synced, like in 1.12)
	 */
	public long seed = 0;

	protected int chainTargets = CHAIN_TARGETS;
	protected Entity prevTarget = null;

	public TeslaProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
		this.seed = worldIn.random.nextLong();
	}

	public TeslaProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun, int chainTargets) {
		super(TGEntities.TESLA_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.seed = par2World.random.nextLong();
		this.chainTargets = chainTargets;
		trace();
	}

	/**
	 * Chain lightning from source to target
	 */
	public TeslaProjectile(Level world, LivingEntity shooter, Entity source, LivingEntity target, int chainTargets, float damage, float speed, int TTL, float dmgDropStart,
			float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage) {
		super(TGEntities.TESLA_PROJECTILE, world, shooter, damage, speed, TTL, 0, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, EnumBulletFirePos.CENTER);
		this.seed = world.random.nextLong();
		maxTicks = (short) this.ticksToLive;
		this.chainTargets = chainTargets;
		this.prevTarget = target;

		this.setPos(source.getX(), source.getY() + source.getEyeHeight() * 0.5f, source.getZ());
		Vec3 src = new Vec3(this.getX(), this.getY(), this.getZ());
		Vec3 tgt = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5f, target.getZ());
		Vec3 dir = tgt.subtract(src).normalize();

		this.distance = src.distanceTo(tgt);
		this.laserPitch = (float) (Math.asin(-dir.y) * MathUtil.R2D);
		this.laserYaw = (float) (Math.atan2(dir.x, dir.z) * MathUtil.R2D);

		this.setXRot(laserPitch);
		this.setYRot(laserYaw);

		this.motionX = dir.x * this.speed;
		this.motionY = dir.y * this.speed;
		this.motionZ = dir.z * this.speed;
		this.syncDeltaMovement();

		if (distance <= 0) {
			distance = this.speed;
		}

		HitResult raytraceresult = new EntityHitResult(target);
		this.onHitKeepAlive(raytraceresult);
	}

	@Override
	protected void onHit(HitResult raytraceResultIn) {
		super.onHit(raytraceResultIn);
		if (!this.level().isClientSide) {
			Entity lastTarget = raytraceResultIn instanceof EntityHitResult ehr ? ehr.getEntity() : null;
			if (lastTarget != null && this.chainTargets > 0 && this.shooter != null) {
				LivingEntity nextTarget = findNextTarget(lastTarget);
				if (nextTarget != null) {
					TeslaProjectile proj = new TeslaProjectile(this.level(), this.shooter, lastTarget, nextTarget, chainTargets - 1, this.damage * CHAIN_DAMAGE_FACTOR, this.speed, TTL, this.damageDropStart, this.damageDropEnd, this.damageMin, this.penetration, this.blockdamage);
					this.level().addFreshEntity(proj);
				}
			}
		}
	}

	private LivingEntity findNextTarget(Entity lastTarget) {
		List<Entity> list = this.level().getEntities(lastTarget, new AABB(lastTarget.getX() - CHAIN_RANGE, lastTarget.getY() - CHAIN_RANGE, lastTarget.getZ() - CHAIN_RANGE,
				lastTarget.getX() + CHAIN_RANGE, lastTarget.getY() + CHAIN_RANGE, lastTarget.getZ() + CHAIN_RANGE), BULLET_TARGETS);

		for (int i1 = 0; i1 < list.size(); ++i1) {
			Entity e = list.get(i1);
			if (e instanceof LivingEntity entity) {
				double distance = Math.sqrt(entity.distanceToSqr(lastTarget.getX(), lastTarget.getY() + lastTarget.getEyeHeight() * 0.5f, lastTarget.getZ()));

				if (distance < CHAIN_RANGE && entity.isAlive() && entity != lastTarget) {
					if (!entity.equals(shooter) && !entity.equals(prevTarget)) {
						Vec3 vec3d1 = new Vec3(lastTarget.getX(), lastTarget.getY() + lastTarget.getEyeHeight() * 0.5f, lastTarget.getZ());
						Vec3 vec3d2 = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight() * 0.5f, entity.getZ());
						HitResult raytraceresult = this.level().clip(new ClipContext(vec3d1, vec3d2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

						if (raytraceresult.getType() == HitResult.Type.MISS)
							return entity;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causeLightningDamage(this, this.shooter, DeathType.LASER);
		src.armorPenetration = this.penetration;
		src.setNoKnockback();
		src.goreChance = 0.5f;
		return src;
	}

	public long getSeed() {
		return seed;
	}

	public static class Factory implements IProjectileFactory<TeslaProjectile> {
		@Override
		public TeslaProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed,
				int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
				boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new TeslaProjectile(world, p, damage, speed, TeslaProjectile.TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, CHAIN_TARGETS);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.LIGHTNING;
		}
	}
}
