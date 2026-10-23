package techguns.entities.projectiles;

import javax.annotation.Nullable;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IChargedProjectileFactory;
import techguns.packets.PacketSpawnParticle;

public class PowerHammerProjectile extends GenericProjectile {

	public PowerHammerProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch,
			float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos firePos) {
		super(TGEntities.POWERHAMMER_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
				blockdamage, firePos);
	}

	public PowerHammerProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos firePos) {
		super(TGEntities.POWERHAMMER_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
	}

	public PowerHammerProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	protected void playImpact(HitResult rayTraceResult) {
		if (!this.level().isClientSide) {
			double x = rayTraceResult.getLocation().x;
			double y = rayTraceResult.getLocation().y;
			double z = rayTraceResult.getLocation().z;
			this.level().playSound((Player) null, x, y, z, TGSounds.POWERHAMMER_IMPACT, SoundSource.PLAYERS, 1.0f, 1.0f);
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("PowerhammerImpact", x, y, z), TGPackets.targetPointAroundEnt(this, 32.0f));
		}
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResult) {
		this.playImpact(rayTraceResult);
	}

	@Override
	protected void doImpactEffects(@Nullable BlockState state, HitResult rayTraceResult, @Nullable SoundType sound) {
		this.playImpact(rayTraceResult);
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = new TGDamageSource("powerhammer", this, this.shooter, DamageType.PHYSICAL, DeathType.GORE);
		src.goreChance = 1.0f;
		src.armorPenetration = this.penetration;
		return src;
	}

	public static class Factory implements IChargedProjectileFactory<PowerHammerProjectile> {

		@Override
		public PowerHammerProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new PowerHammerProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.PHYSICAL;
		}

		@Override
		public PowerHammerProjectile createChargedProjectile(Level world, LivingEntity p, float damage, float speed,
				int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
				boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity, float charge,
				int ammoConsumed) {
			return new PowerHammerProjectile(world, p, damage * ammoConsumed, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin * ammoConsumed, penetration, blockdamage, firePos);
		}
	}
}
