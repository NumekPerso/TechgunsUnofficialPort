package techguns.entities.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import techguns.TGEntities;
import techguns.api.damagesystem.DamageType;
import techguns.items.guns.IGrenadeProjectileFactory;

public class FragGrenadeProjectile extends GrenadeProjectile {

	public FragGrenadeProjectile(GrenadeProjectile other, double x, double y, double z) {
		super(TGEntities.FRAG_GRENADE_PROJECTILE, other, x, y, z);
	}

	public FragGrenadeProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch,
			float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos leftGun, double gravity, float radius, int bounces) {
		super(TGEntities.FRAG_GRENADE_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
				blockdamage, leftGun, gravity, radius, bounces);
		init();
	}

	public FragGrenadeProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity, float radius,
			int bounces) {
		super(TGEntities.FRAG_GRENADE_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, gravity, radius, bounces);
		init();
	}

	public FragGrenadeProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
		init();
	}

	private void init() {
		this.bounces = 2;
	}

	@Override
	protected GrenadeProjectile createBounceProjectile(Vec3 hitvec) {
		return new Factory().createBounceProjectile(this, hitvec.x, hitvec.y, hitvec.z);
	}

	public static class Factory implements IGrenadeProjectileFactory<FragGrenadeProjectile> {

		@Override
		public FragGrenadeProjectile createProjectile(Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
				EnumBulletFirePos leftGun, float radius, double gravity, float charge, int bounces) {
			return new FragGrenadeProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, gravity, radius, bounces);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.EXPLOSION;
		}

		@Override
		public FragGrenadeProjectile createBounceProjectile(FragGrenadeProjectile proj, double bounceX, double bounceY, double bounceZ) {
			return new FragGrenadeProjectile(proj, bounceX, bounceY, bounceZ);
		}
	}
}
