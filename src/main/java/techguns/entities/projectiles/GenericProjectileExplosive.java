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
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.items.guns.ammo.DamageModifier;
import techguns.packets.PacketSpawnParticle;

public class GenericProjectileExplosive extends GenericProjectile {

	public GenericProjectileExplosive(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public GenericProjectileExplosive(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch,
			float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos firePos) {
		super(TGEntities.GENERIC_PROJECTILE_EXPLOSIVE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
				blockdamage, firePos);
	}

	public GenericProjectileExplosive(Level par2World, LivingEntity p, float damage, float speed, int TTL,
			float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos firePos) {
		super(TGEntities.GENERIC_PROJECTILE_EXPLOSIVE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causeExplosionDamage(this, this.shooter, DeathType.GORE);
		src.armorPenetration = this.penetration;
		src.goreChance = 1.0f;
		src.knockbackMultiplier = 3.0f;
		return src;
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult raytraceResultIn) {
		this.explode(raytraceResultIn.getLocation().x, raytraceResultIn.getLocation().y, raytraceResultIn.getLocation().z);
	}

	@Override
	protected void hitBlock(BlockHitResult raytraceResultIn) {
		this.explode(raytraceResultIn.getLocation().x, raytraceResultIn.getLocation().y, raytraceResultIn.getLocation().z);
	}

	protected void explode(double x, double y, double z) {
		if (!this.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("MiningChargeBlockExplosion", x, y, z), TGPackets.targetPointAroundEnt(this, 100.0f));

			//1.12: vanilla Explosion(world, this, x, y, z, 1.5f, false, blockdamage)
			this.level().explode(this, x, y, z, 1.5f, false, this.blockdamage ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);

		} else {
			Techguns.proxy.createLightPulse(x, y, z, 5, 15, 3.0f, 0.5f, 1f, 0.9f, 0.5f);
		}
		this.discard();
	}

	public static class Factory implements IProjectileFactory<GenericProjectileExplosive> {

		protected DamageModifier mod = new DamageModifier().setDmg(1.15f, 0f);

		@Override
		public DamageModifier getDamageModifier() {
			return mod;
		}

		@Override
		public GenericProjectileExplosive createProjectile(GenericGun gun, Level world, LivingEntity p,
				float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
				float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new GenericProjectileExplosive(world, p, mod.getDamage(damage), speed, TTL, spread, dmgDropStart, dmgDropEnd, mod.getDamage(dmgMin), penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.EXPLOSION;
		}
	}
}
