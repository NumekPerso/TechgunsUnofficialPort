package techguns.entities.projectiles;

import javax.annotation.Nullable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import techguns.TGEntities;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;

public class AlienBlasterProjectile extends GenericProjectile {

	public static final int ENTITY_IGNITE_TIME = 3;

	public AlienBlasterProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
		if (worldIn.isClientSide) {
			Techguns.proxy.createFXOnEntity("AlienBlasterTrail", this);
		}
	}

	public AlienBlasterProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch,
			float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos firePos) {
		super(TGEntities.ALIEN_BLASTER_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
				blockdamage, firePos);
	}

	public AlienBlasterProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos firePos) {
		super(TGEntities.ALIEN_BLASTER_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
	}

	@Override
	protected void doImpactEffects(@Nullable BlockState state, HitResult rayTraceResult, @Nullable SoundType sound) {
		if (this.level().isClientSide) {
			Techguns.proxy.createFX("AlienExplosion", this.level(), rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z, 0, 0, 0);
		}
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResultIn) {
		ent.setSecondsOnFire(ENTITY_IGNITE_TIME);
	}

	@Override
	protected void hitBlock(BlockHitResult raytraceResultIn) {
		super.hitBlock(raytraceResultIn); //Does impact effect

		if (this.blockdamage) {
			burnBlocks(this.level(), raytraceResultIn, 0.35);
		}
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causeFireDamage(this, this.shooter, DeathType.LASER);
		src.armorPenetration = this.penetration;
		src.goreChance = 0.25f;
		return src;
	}

	public static class Factory implements IProjectileFactory<AlienBlasterProjectile> {

		@Override
		public AlienBlasterProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new AlienBlasterProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.FIRE;
		}
	}
}
