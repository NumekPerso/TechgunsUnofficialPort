package techguns.entities.projectiles;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.capabilities.TGExtendedPlayer;
import techguns.damagesystem.TGExplosion;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IChargedProjectileFactory;
import techguns.packets.PacketSpawnParticle;
import techguns.util.MathUtil;

public class GuidedMissileProjectile extends RocketProjectile {

	public static final double MAX_TURN_ANGLE = 9.0 * MathUtil.D2R; //= 180 degrees per second

	/**
	 * Tracked target, synced in the spawn data
	 */
	public Entity target;

	public GuidedMissileProjectile(EntityType<? extends GenericProjectile> type, Level par2World, LivingEntity p, float damage, float speed, int TTL,
			float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos leftGun, float radius, double gravity) {
		super(type, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, radius, gravity);
	}

	public GuidedMissileProjectile(EntityType<? extends GenericProjectile> type, Level par2World, LivingEntity p, float damage, float speed, int TTL,
			float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos leftGun, float radius, Entity target) {
		super(type, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, radius, 0.0f);
		this.target = target;
	}

	public GuidedMissileProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL,
			float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos leftGun, float radius, double gravity) {
		this(TGEntities.GUIDED_MISSILE_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, radius, gravity);
	}

	public GuidedMissileProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL,
			float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos leftGun, float radius, Entity target) {
		this(TGEntities.GUIDED_MISSILE_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun, radius, target);
	}

	public GuidedMissileProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void createTrailFX() {
		Techguns.proxy.createFXOnEntity("GuidedMissileExhaust", this);
	}

	@Override
	protected void explodeRocket() {
		if (!this.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("GuidedMissileExplosion", this.getX(), this.getY(), this.getZ()), TGPackets.targetPointAroundEnt(this, 50.0f));
			TGExplosion explosion = new TGExplosion(this.level(), this.shooter, this, getX(), getY(), getZ(), this.damage, this.damageMin, this.damageDropStart, this.damageDropEnd, this.blockdamage ? 0.25 : 0.0);

			explosion.doExplosion(true);
		} else {
			Techguns.proxy.createLightPulse(this.getX(), this.getY(), this.getZ(), 5, 15, 10.0f, 1.0f, 1f, 0.9f, 0.5f);
		}
		this.discard();
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		super.readSpawnData(additionalData);
		int entityID = additionalData.readInt();
		if (entityID != -1)
			this.target = this.level().getEntity(entityID);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		super.writeSpawnData(buffer);
		if (target != null)
			buffer.writeInt(target.getId());
		else
			buffer.writeInt(-1);
	}

	@Override
	public void tick() {
		//Update Motion
		if (this.target != null) {
			Vec3 motion = new Vec3(motionX, motionY, motionZ);
			double speed = motion.length();

			Vec3 v2 = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.5f, target.getZ()).subtract(new Vec3(this.getX(), this.getY(), this.getZ())).normalize();
			Vec3 v1 = motion.normalize();

			double angle = Math.acos(v1.dot(v2));
			Vec3 axis = v1.cross(v2).normalize();

			if (angle < MAX_TURN_ANGLE) {
				motion = v2.scale(speed);
			} else {
				motion = MathUtil.rotateVector(v1, axis, MAX_TURN_ANGLE).scale(speed);
			}

			this.motionX = motion.x;
			this.motionY = motion.y;
			this.motionZ = motion.z;
		}
		super.tick();
	}

	public Entity getTarget() {
		return target;
	}

	/**
	 * Lock on target of the shooter, if the lock is complete
	 */
	protected static Entity getLockedTarget(GenericGun gun, LivingEntity p) {
		Entity target = null;
		if (p instanceof Player) {
			TGExtendedPlayer epc = TGExtendedPlayer.get((Player) p);
			GenericGun g = gun;
			if (g == null && !p.getUseItem().isEmpty() && p.getUseItem().getItem() instanceof GenericGun) {
				g = (GenericGun) p.getUseItem().getItem();
			}
			if (epc != null && g != null && epc.lockOnEntity != null && epc.lockOnTicks >= g.getLockOnTicks()) {
				target = epc.lockOnEntity;
			}
		}
		return target;
	}

	public static class Factory implements IChargedProjectileFactory<GuidedMissileProjectile> {

		@Override
		public GuidedMissileProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {

			Entity target = getLockedTarget(gun, p);

			if (target != null) {
				return new GuidedMissileProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, radius, target);
			} else {
				return new GuidedMissileProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, radius, 0.01f);
			}
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.EXPLOSION;
		}

		@Override
		public GuidedMissileProjectile createChargedProjectile(Level world, LivingEntity p, float damage,
				float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
				boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity, float charge, int ammoConsumed) {
			return null;
		}
	}
}
