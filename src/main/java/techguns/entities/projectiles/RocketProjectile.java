package techguns.entities.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.damagesystem.TGExplosion;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.packets.PacketSpawnParticle;

public class RocketProjectile extends GenericProjectile {

	public RocketProjectile(EntityType<? extends GenericProjectile> type, Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun, float radius, double gravity) {
		super(type, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.radius = radius;
		this.gravity = gravity;
	}

	public RocketProjectile(EntityType<? extends GenericProjectile> type, Level worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage, float speed, int TTL, float spread, float dmgDropStart,
			float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun, float radius, double gravity) {
		super(type, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.radius = radius;
		this.gravity = gravity;
	}

	public RocketProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun, float radius, double gravity) {
		this(TGEntities.ROCKET_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, radius, gravity);
	}

	public RocketProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
		if (worldIn.isClientSide) {
			this.createTrailFX();
		}
	}

	protected void createTrailFX() {
		Techguns.proxy.createFXOnEntity("RocketLauncherExhaust", this);
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResult) {
		this.explodeRocket();
	}

	@Override
	protected void hitBlock(BlockHitResult raytraceResultIn) {
		this.explodeRocket();
	}

	protected void explodeRocket() {
		if (!this.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("RocketExplosion", this.getX(), this.getY(), this.getZ()), TGPackets.targetPointAroundEnt(this, 50.0f));

			TGExplosion explosion = new TGExplosion(this.level(), this.shooter, this, getX(), getY(), getZ(), this.damage, this.damageMin, this.damageDropStart, this.damageDropEnd, this.blockdamage ? 0.5 : 0.0);

			explosion.doExplosion(true);
		} else {
			Techguns.proxy.createLightPulse(this.getX(), this.getY(), this.getZ(), 5, 15, 10.0f, 1.0f, 1f, 0.9f, 0.5f);
		}
		this.discard();
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource dmgsrc = TGDamageSource.causeExplosionDamage(this, this.shooter, DeathType.GORE);
		dmgsrc.goreChance = 1.0f;
		dmgsrc.knockbackMultiplier = 3.0f;
		return dmgsrc;
	}

	public static class Factory implements IProjectileFactory<RocketProjectile> {

		@Override
		public RocketProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new RocketProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, radius, gravity);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.EXPLOSION;
		}
	}
}
