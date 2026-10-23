package techguns.entities.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import techguns.TGEntities;
import techguns.api.damagesystem.DamageType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.items.guns.ammo.DamageModifier;

public class RocketProjectileHV extends RocketProjectile {

	public RocketProjectileHV(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public RocketProjectileHV(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch,
			float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun, float radius, double gravity) {
		super(TGEntities.ROCKET_PROJECTILE_HV, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
				blockdamage, leftGun, radius, gravity);
	}

	public RocketProjectileHV(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos leftGun, float radius, double gravity) {
		super(TGEntities.ROCKET_PROJECTILE_HV, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun,
				radius, gravity);
	}

	public static class Factory implements IProjectileFactory<RocketProjectileHV> {

		protected DamageModifier mod = new DamageModifier().setRadius(0.75f, 0f).setRange(0.75f, 0f).setVelocity(2.0f, 0f);

		@Override
		public DamageModifier getDamageModifier() {
			return mod;
		}

		@Override
		public RocketProjectileHV createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new RocketProjectileHV(world, p, mod.getDamage(damage), mod.getVelocity(speed), mod.getTTL(TTL), spread, mod.getRange(dmgDropStart), mod.getRange(dmgDropEnd), mod.getDamage(dmgMin), penetration, blockdamage, firePos, mod.getRadius(radius), gravity);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.EXPLOSION;
		}
	}
}
