package techguns.entities.projectiles;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.packets.PacketSpawnParticleOnEntity;

public class FlamethrowerProjectile extends GenericProjectile {

	boolean piercing = false;

	float chanceToIgnite = 0.5f;
	int entityIgniteTime = 3;

	public FlamethrowerProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
		if (worldIn.isClientSide) {
			Techguns.proxy.createFXOnEntity("FlamethrowerTrail", this);
		}
	}

	public FlamethrowerProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage, float speed, int TTL, float spread, float dmgDropStart,
			float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity) {
		super(TGEntities.FLAMETHROWER_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.gravity = gravity;
	}

	public FlamethrowerProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity) {
		super(TGEntities.FLAMETHROWER_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.gravity = gravity;
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causeFireDamage(this, this.shooter, DeathType.DEFAULT);
		src.armorPenetration = this.penetration;
		src.setNoKnockback();
		return src;
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResult) {
		ent.setSecondsOnFire(this.entityIgniteTime);
	}

	@Override
	protected void hitBlock(BlockHitResult mop) {
		if (this.blockdamage) {
			burnBlocks(this.level(), mop, chanceToIgnite);
		}

		if (this.level().isClientSide) {
			Vec3 hitVec = mop.getLocation();
			Techguns.proxy.createFX("FlamethrowerImpact", this.level(), hitVec.x, hitVec.y, hitVec.z, 0f, 0f, 0f);
		}
	}

	@Override
	protected float inWaterUpdateBehaviour(float f1) {
		if (this.isInWater()) {
			for (int i = 0; i < 4; ++i) {
				this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - this.motionX * 0.25D,
						this.getY() - this.motionY * 0.25D, this.getZ() - this.motionZ * 0.25D, this.motionX, this.motionY,
						this.motionZ);
			}
			f1 = 0.85F;
		}

		//DIE IN WATER
		if (this.isInWaterOrRain()) {
			this.discard();
		}
		return f1;
	}

	public static class Factory implements IProjectileFactory<FlamethrowerProjectile> {

		@Override
		public FlamethrowerProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			//offset = -0.15 -0.05 0.5
			float offsetX = 0.0f;
			if (firePos == EnumBulletFirePos.RIGHT) offsetX = -0.15f;
			else if (firePos == EnumBulletFirePos.LEFT) offsetX = 0.15f;
			float offsetY = -0.05f;
			float offsetZ = 0.5f;
			if (!world.isClientSide) {
				TGPackets.network.sendToAllAround(new PacketSpawnParticleOnEntity("FlamethrowerFireFX", p, offsetX, offsetY, offsetZ, true), TGPackets.targetPointAroundEnt(p, 25.0f));
			}

			return new FlamethrowerProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, gravity);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.FIRE;
		}
	}
}
