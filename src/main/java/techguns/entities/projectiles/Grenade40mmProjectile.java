package techguns.entities.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import techguns.TGEntities;
import techguns.api.damagesystem.DamageType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileBounceFactory;

public class Grenade40mmProjectile extends GrenadeProjectile {

	public Grenade40mmProjectile(GrenadeProjectile other, double x, double y, double z) {
		super(TGEntities.GRENADE_40MM_PROJECTILE, other, x, y, z);
	}

	public Grenade40mmProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
			boolean blockdamage, EnumBulletFirePos leftGun, double gravity, float radius, int bounces) {
		super(TGEntities.GRENADE_40MM_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, gravity, radius, bounces);
	}

	public Grenade40mmProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun,
			double gravity, float radius, int bounces) {
		super(TGEntities.GRENADE_40MM_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, gravity, radius, bounces);
	}

	public Grenade40mmProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected GrenadeProjectile createBounceProjectile(Vec3 hitVec) {
		return new Factory().createBounceProjectile(this, hitVec.x, hitVec.y, hitVec.z);
	}

	public static class Factory implements IProjectileBounceFactory<Grenade40mmProjectile> {

		@Override
		public DamageType getDamageType() {
			return DamageType.EXPLOSION;
		}

		@Override
		public Grenade40mmProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart,
				float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new Grenade40mmProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, gravity, radius, 2);
		}

		@Override
		public Grenade40mmProjectile createBounceProjectile(Grenade40mmProjectile proj, double bounceX, double bounceY, double bounceZ) {
			return new Grenade40mmProjectile(proj, bounceX, bounceY, bounceZ);
		}
	}
}
