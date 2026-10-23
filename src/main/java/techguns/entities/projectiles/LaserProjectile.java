package techguns.entities.projectiles;

import javax.annotation.Nullable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.packets.PacketSpawnParticle;

public class LaserProjectile extends AbstractBeamProjectile {

	public LaserProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage,
			float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
			boolean blockdamage, EnumBulletFirePos leftGun) {
		super(TGEntities.LASER_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
				blockdamage, leftGun);
		trace();
	}

	public LaserProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun) {
		super(TGEntities.LASER_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		trace();
	}

	public LaserProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void doImpactEffects(@Nullable BlockState state, HitResult rayTraceResult, @Nullable SoundType sound) {
		if (!this.level().isClientSide) {
			Vec3 hitVec = rayTraceResult.getLocation();
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("LaserGunImpact", hitVec.x, hitVec.y, hitVec.z), TGPackets.targetPointAroundEnt(this, 35.0f));
		}
	}

	@Override
	public void tick() {
		--this.ticksToLive;
		if (this.ticksToLive <= 0) {
			this.discard();
		}
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causeEnergyDamage(this, this.shooter, DeathType.LASER);
		src.goreChance = 1.0f;
		src.armorPenetration = this.penetration;
		src.setNoKnockback();
		return src;
	}

	public static class Factory implements IProjectileFactory<LaserProjectile> {
		@Override
		public LaserProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed,
				int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
				boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new LaserProjectile(world, p, damage, speed, 7, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.ENERGY;
		}
	}
}
