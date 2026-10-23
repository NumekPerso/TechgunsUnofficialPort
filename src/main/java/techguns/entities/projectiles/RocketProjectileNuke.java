package techguns.entities.projectiles;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.damagesystem.TGExplosion;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.items.guns.ammo.DamageModifier;
import techguns.packets.PacketSpawnParticle;

public class RocketProjectileNuke extends RocketProjectile {

	public RocketProjectileNuke(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos leftGun, float radius, double gravity) {
		super(TGEntities.ROCKET_PROJECTILE_NUKE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun,
				radius, gravity);
	}

	public RocketProjectileNuke(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch,
			float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun, float radius, double gravity) {
		super(TGEntities.ROCKET_PROJECTILE_NUKE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
				blockdamage, leftGun, radius, gravity);
	}

	public RocketProjectileNuke(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void explodeRocket() {
		if (!this.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("NukeExplosion", this.getX(), this.getY(), this.getZ()), TGPackets.targetPointAroundEnt(this, 150.0f));
			TGExplosion explosion = new TGExplosion(this.level(), this.shooter, this, getX(), getY(), getZ(), this.damage, this.damageMin, this.damageDropStart, this.damageDropEnd, this.blockdamage ? 0.5 : 0.0);
			explosion.blockDropChance = 0.05f;
			explosion.setDmgSrc(getProjectileDamageSource());

			explosion.doExplosion(false);
			this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), TGSounds.NUKE_EXPLOSION, SoundSource.BLOCKS, 4.0F, 1.0F);

			/** DO RADIATION**/
			if (techguns.TGRadiationSystem.isEnabled()) {
				techguns.entities.special.EntityRadiation rad = new techguns.entities.special.EntityRadiation(this, 20 * 60, this.damageDropStart, 9, this.damageDropEnd, 2);
				this.level().addFreshEntity(rad);
			}

		} else {
			Techguns.proxy.createLightPulse(this.getX(), this.getY(), this.getZ(), 5, 80, 80.0f, 1.0f, 1f, 0.9f, 0.5f);
		}
		this.discard();
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource dmgsrc = TGDamageSource.causeExplosionDamage(this, this.shooter, DeathType.LASER);
		dmgsrc.goreChance = 1.0f;
		dmgsrc.knockbackMultiplier = 3.0f;
		return dmgsrc;
	}

	public static class Factory implements IProjectileFactory<RocketProjectileNuke> {

		protected DamageModifier mod = new DamageModifier().setDmg(5f, 0f).setRadius(5f, 0f).setRange(5f, 0f);

		@Override
		public DamageModifier getDamageModifier() {
			return mod;
		}

		@Override
		public RocketProjectileNuke createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new RocketProjectileNuke(world, p, mod.getDamage(damage), speed, TTL, spread, mod.getRange(dmgDropStart), mod.getRange(dmgDropEnd), mod.getDamage(dmgMin), penetration, blockdamage, firePos, mod.getRadius(radius), gravity);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.EXPLOSION;
		}
	}
}
