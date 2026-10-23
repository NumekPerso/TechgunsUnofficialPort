package techguns.entities.projectiles;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
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
import techguns.items.guns.ammo.DamageModifier;

public class GenericProjectileIncendiary extends GenericProjectile {

	protected boolean showFireTrail = false;

	public GenericProjectileIncendiary(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public GenericProjectileIncendiary(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch,
			float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos firePos, boolean fireTrail) {
		super(TGEntities.GENERIC_PROJECTILE_INCENDIARY, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
				blockdamage, firePos);
		this.showFireTrail = fireTrail;
	}

	public GenericProjectileIncendiary(Level par2World, LivingEntity p, float damage, float speed, int TTL,
			float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos firePos, boolean fireTrail) {
		super(TGEntities.GENERIC_PROJECTILE_INCENDIARY, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		this.showFireTrail = fireTrail;
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causeFireDamage(this, this.shooter, DeathType.DEFAULT);
		src.armorPenetration = this.penetration;
		src.setNoKnockback();
		return src;
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResultIn) {
		super.onHitEffect(ent, rayTraceResultIn);
		if (!ent.fireImmune()) {
			ent.setSecondsOnFire(3);
		}
	}

	@Override
	protected void hitBlock(BlockHitResult raytraceResultIn) {
		if (this.blockdamage) {
			burnBlocks(this.level(), raytraceResultIn, damage / 40f);
		}
		super.hitBlock(raytraceResultIn);
	}

	@Override
	protected void doImpactEffects(@Nullable BlockState state, HitResult rayTraceResult, @Nullable SoundType sound) {
		double x = rayTraceResult.getLocation().x;
		double y = rayTraceResult.getLocation().y;
		double z = rayTraceResult.getLocation().z;

		float[] angles = this.getImpactAngles(rayTraceResult);
		float pitch = angles[0];
		float yaw = angles[1];

		if (this.showFireTrail) {
			this.sendImpactFX(x, y, z, pitch, yaw, 5, true);
		} else {
			this.sendImpactFX(x, y, z, pitch, yaw, getImpactTypeForSound(sound), true);
		}
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		super.writeSpawnData(buffer);
		buffer.writeBoolean(showFireTrail);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		super.readSpawnData(additionalData);
		this.showFireTrail = additionalData.readBoolean();
		if (showFireTrail) {
			Techguns.proxy.createFXOnEntity("IncendiaryShotgunTrail", this);
		}
	}

	public boolean isShowFireTrail() {
		return showFireTrail;
	}

	public static class Factory implements IProjectileFactory<GenericProjectile> {

		protected DamageModifier mod = new DamageModifier().setDmg(1.1f, 0f);

		protected boolean fireTrail;

		public Factory(boolean fireTrail) {
			this.fireTrail = fireTrail;
		}

		@Override
		public DamageModifier getDamageModifier() {
			return mod;
		}

		@Override
		public GenericProjectileIncendiary createProjectile(GenericGun gun, Level world, LivingEntity p,
				float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
				float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new GenericProjectileIncendiary(world, p, mod.getDamage(damage), speed, TTL, spread, dmgDropStart, dmgDropEnd, mod.getDamage(dmgMin), penetration, blockdamage, firePos, this.fireTrail);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.FIRE;
		}
	}
}
