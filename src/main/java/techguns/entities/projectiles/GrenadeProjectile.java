package techguns.entities.projectiles;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGExplosion;
import techguns.items.guns.IGrenadeProjectileFactory;
import techguns.packets.PacketSpawnParticle;

/**
 * Bouncing grenade: on a block hit a new projectile is spawned with the reflected motion (bounces-1), the old one dies.
 */
public class GrenadeProjectile extends GenericProjectile {

	int bounces = 3;

	protected boolean bounced = false;

	public GrenadeProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public GrenadeProjectile(EntityType<? extends GenericProjectile> type, Level worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage, float speed, int TTL, float spread, float damageDropStart, float damageDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity, float radius, int bounces) {
		super(type, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, damageDropStart, damageDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.bounces = bounces;
		this.gravity = gravity;
		this.radius = radius;
	}

	public GrenadeProjectile(EntityType<? extends GenericProjectile> type, Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity, float radius, int bounces) {
		super(type, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.bounces = bounces;
		this.gravity = gravity;
		this.radius = radius;
	}

	public GrenadeProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity, float radius, int bounces) {
		this(TGEntities.GRENADE_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, gravity, radius, bounces);
	}

	/**
	 * Copy on bounce constructor
	 */
	public GrenadeProjectile(EntityType<? extends GenericProjectile> type, GrenadeProjectile other, double x, double y, double z) {
		super(type, other.level(), x, y, z, other.getYRot(), other.getXRot(), other.damage, other.speed, other.ticksToLive, 1.0f, other.damageDropStart, other.damageDropEnd, other.damageMin, other.penetration, other.blockdamage, EnumBulletFirePos.CENTER);
		this.shooter = other.shooter;
		this.bounces = other.bounces - 1;
		this.gravity = other.gravity;
		this.radius = other.radius;
	}

	public GrenadeProjectile(GrenadeProjectile other, double x, double y, double z) {
		this(TGEntities.GRENADE_PROJECTILE, other, x, y, z);
	}

	@Override
	protected void hitBlock(BlockHitResult raytraceResultIn) {
		if (this.bounces > 0 && !bounced) {

			Direction sideHit = raytraceResultIn.getDirection();

			float len = this.getBounceSpeedFactor();

			Vec3 refl;
			switch (sideHit) {
			case EAST:
			case WEST:
				refl = new Vec3(-this.motionX, this.motionY, this.motionZ);
				break;
			case NORTH:
			case SOUTH:
				refl = new Vec3(this.motionX, this.motionY, -this.motionZ);
				break;
			case DOWN:
			case UP:
			default:
				refl = new Vec3(this.motionX, -this.motionY, this.motionZ);
				break;
			}

			this.playBounceSound();

			if (!this.level().isClientSide) {
				GrenadeProjectile gren = createBounceProjectile(raytraceResultIn.getLocation());

				gren.motionX = refl.x * len;
				gren.motionY = refl.y * len;
				gren.motionZ = refl.z * len;
				gren.setPos(gren.getX() + gren.motionX, gren.getY() + gren.motionY, gren.getZ() + gren.motionZ);
				gren.syncDeltaMovement();

				this.level().addFreshEntity(gren);

				createProjectileTrail(gren);
			}
			this.bounced = true;
		} else if (bounced) {
			this.discard();
		} else {
			this.explode();
		}
	}

	protected GrenadeProjectile createBounceProjectile(Vec3 hitvec) {
		return new Factory().createBounceProjectile(this, hitvec.x, hitvec.y, hitvec.z);
	}

	protected void playBounceSound() {
		this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS, 1.0f, 1.0f, false);
	}

	protected void createProjectileTrail(GrenadeProjectile gren) {
	}

	protected float getBounceSpeedFactor() {
		return 0.5f;
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResult) {
		this.explode();
	}

	protected void explode() {
		if (!this.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("RocketExplosion", this.getX(), this.getY(), this.getZ()), TGPackets.targetPointAroundEnt(this, 50.0f));

			TGExplosion exp = new TGExplosion(this.level(), shooter, this, getX(), getY(), getZ(), this.damage, this.damageMin, this.damageDropStart, this.damageDropEnd, 0);
			exp.doExplosion(true);
		}
		this.discard();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.bounces = tags.getByte("bounces");
		this.bounced = tags.getBoolean("bounced");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putByte("bounces", (byte) this.bounces);
		tags.putBoolean("bounced", this.bounced);
	}

	public int getBounces() {
		return bounces;
	}

	public static class Factory implements IGrenadeProjectileFactory<GrenadeProjectile> {

		@Override
		public GrenadeProjectile createProjectile(Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
				EnumBulletFirePos leftGun, float radius, double gravity, float charge, int bounces) {
			return new GrenadeProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, gravity, radius, bounces);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.EXPLOSION;
		}

		@Override
		public GrenadeProjectile createBounceProjectile(GrenadeProjectile proj, double bounceX, double bounceY, double bounceZ) {
			return new GrenadeProjectile(proj, bounceX, bounceY, bounceZ);
		}
	}
}
