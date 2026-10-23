package techguns.entities.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import techguns.TGEntities;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;

public class BlasterProjectile extends GenericProjectile {

	public BlasterProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public BlasterProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage, float speed, int TTL, float spread, float dmgDropStart,
			float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun) {
		super(TGEntities.BLASTER_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
	}

	public BlasterProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun) {
		super(TGEntities.BLASTER_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causeEnergyDamage(this, this.shooter, DeathType.LASER);
		src.armorPenetration = this.penetration;
		src.setNoKnockback();
		return src;
	}

	public static class Factory implements IProjectileFactory<BlasterProjectile> {

		@Override
		public BlasterProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new BlasterProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.ENERGY;
		}
	}

	@Override
	protected void hitBlock(BlockHitResult rayTraceResult) {
		if (this.level().isClientSide) {
			Techguns.proxy.createFX("LaserGunImpact", this.level(), rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z, 0, 0, 0);
		}
	}

	@Override
	protected float inWaterUpdateBehaviour(float f1) {
		//no slowdown/bubbles in water
		if (this.isInWaterOrRain()) {
			this.clearFire();
		}
		return f1;
	}
}
