package techguns.entities.projectiles;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import techguns.TGEntities;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.util.MathUtil;

/**
 * Nuclear death ray beam, follows the shooter every tick and traces again.
 */
public class NDRProjectile extends AbstractBeamProjectile {

	public static final int BEAM_LIFETIME = 10;

	public int shooterID;

	public NDRProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
		if (worldIn.isClientSide) {
			Techguns.proxy.createFXOnEntity("BeamGunMuzzleFX", this);
		}
	}

	public NDRProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun) {
		super(TGEntities.NDR_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		super.writeSpawnData(buffer);
		if (this.shooter != null)
			buffer.writeInt(this.shooter.getId());
		else
			buffer.writeInt(0);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		super.readSpawnData(additionalData);
		this.shooterID = additionalData.readInt();
	}

	@Override
	public void tick() {
		//Update position to shooter
		double dx, dy, dz;
		float f = 100.0F;
		Vec3 motion;
		if (this.shooter == null) {
			Entity e = this.level().getEntity(shooterID);
			if (e instanceof LivingEntity) this.shooter = (LivingEntity) e;
		}
		if (shooter != null) {
			dx = (double) (-Mth.sin(shooter.getYHeadRot() / 180.0F * (float) Math.PI) * Mth.cos(shooter.getXRot() / 180.0F * (float) Math.PI) * f);
			dz = (double) (Mth.cos(shooter.getYHeadRot() / 180.0F * (float) Math.PI) * Mth.cos(shooter.getXRot() / 180.0F * (float) Math.PI) * f);
			dy = (double) (-Mth.sin((shooter.getXRot()) / 180.0F * (float) Math.PI) * f);

			double posX = shooter.getX();
			double posY = shooter.getY();
			double posZ = shooter.getZ();

			motion = new Vec3(dx, dy, dz).normalize();
			this.motionX = motion.x * speed;
			this.motionY = motion.y * speed;
			this.motionZ = motion.z * speed;

			if (Techguns.proxy.isClientPlayerAndIn1stPerson(shooter)) {
				Vec3 offset = this.getFPOffset();
				offset = offset.xRot((float) (shooter.getXRot() * MathUtil.D2R)).yRot((float) ((-90.0 - shooter.getYHeadRot()) * MathUtil.D2R));
				posX += offset.x;
				posY += offset.y + shooter.getEyeHeight();
				posZ += offset.z;
			} else {
				posX -= (double) (Mth.cos(shooter.getYRot() / 180.0F * (float) Math.PI) * 0.16F);
				posY += shooter.getEyeHeight() - 0.10000000149011612D + this.get3PYOffset();
				posZ -= (double) (Mth.sin(shooter.getYRot() / 180.0F * (float) Math.PI) * 0.16F);
			}
			this.setPos(posX, posY, posZ);
			this.syncDeltaMovement();

			this.setXRot(shooter.getXRot());
			this.setYRot(shooter.getYHeadRot());
		}
		//---------
		trace();
		--this.ticksToLive;
		if (this.ticksToLive <= 0) {
			this.discard();
		}
	}

	/**
	 * Position updates from the server are ignored, the beam follows its shooter on both sides
	 */
	@Override
	public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport) {
		if (this.shooter == null) {
			super.lerpTo(x, y, z, yRot, xRot, steps, teleport);
		}
	}

	@Override
	protected void doImpactEffects(@Nullable BlockState state, HitResult rayTraceResult, @Nullable SoundType sound) {
		if (this.level().isClientSide) {
			Vec3 hitVec = rayTraceResult.getLocation();
			Techguns.proxy.createFX("BeamGunImpactFX", this.level(), hitVec.x, hitVec.y, hitVec.z, 0f, 0f, 0f);
		}
	}

	public Vec3 getFPOffset() {
		return new Vec3(0, -0.08, 0.12);
	}

	public float get3PYOffset() {
		return 0.0f;
	}

	public float getBeam3PYOffset() {
		return 0.0f;
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResultIn) {
		super.onHitEffect(ent, rayTraceResultIn);
		if (techguns.TGRadiationSystem.isEnabled()) {
			ent.addEffect(new net.minecraft.world.effect.MobEffectInstance(techguns.TGRadiationSystem.radiation_effect, 40, 4, false, true));
		}
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causeRadiationDamage(this, this.shooter, DeathType.LASER);
		src.armorPenetration = this.penetration;
		src.setNoKnockback();
		return src;
	}

	public static class Factory implements IProjectileFactory<NDRProjectile> {
		@Override
		public NDRProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed,
				int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
				boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new NDRProjectile(world, p, damage, speed, BEAM_LIFETIME, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.RADIATION;
		}
	}
}
