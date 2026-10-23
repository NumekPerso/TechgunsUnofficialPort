package techguns.entities.projectiles;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Beams are traced instantly on creation (or every tick for the NDR), the length / direction of the beam is sent to the client
 * in the spawn data (distance, laserPitch, laserYaw, maxTicks).
 */
public abstract class AbstractBeamProjectile extends GenericProjectile {

	public double distance = -1d;
	public float laserPitch = 0.0f;
	public float laserYaw = 0.0f;
	public short maxTicks = 0;

	/**
	 * 1.12 reset isDead after onHit during the trace, the beam must survive the hit. Removal by discard() is ignored while tracing.
	 */
	protected boolean tracing = false;

	public AbstractBeamProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public AbstractBeamProjectile(EntityType<? extends GenericProjectile> type, Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun) {
		super(type, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		maxTicks = (short) this.ticksToLive;
	}

	public AbstractBeamProjectile(EntityType<? extends GenericProjectile> type, Level worldIn, double posX, double posY, double posZ, float yaw, float pitch,
			float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun) {
		super(type, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		maxTicks = (short) this.ticksToLive;
	}

	protected void trace() {
		Vec3 vec3d1 = new Vec3(this.getX(), this.getY(), this.getZ());
		Vec3 vec3d = new Vec3(this.getX() + this.motionX, this.getY() + this.motionY, this.getZ() + this.motionZ);
		HitResult raytraceresult = this.level().clip(new ClipContext(vec3d1, vec3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if (raytraceresult.getType() == HitResult.Type.MISS) {
			raytraceresult = null;
		}

		if (raytraceresult != null) {
			vec3d = raytraceresult.getLocation();
		}

		HitResult rayTraceResultEntity = this.findEntityOnPath(vec3d1, vec3d);
		if (rayTraceResultEntity != null) {
			raytraceresult = rayTraceResultEntity;
		}

		if (raytraceresult instanceof EntityHitResult ehr && ehr.getEntity() instanceof Player entityplayer) {
			if (this.shooter instanceof Player && !((Player) this.shooter).canHarmPlayer(entityplayer)) {
				raytraceresult = null;
			}
		}

		if (raytraceresult != null) {
			this.onHitKeepAlive(raytraceresult);
			Vec3 hitVec = raytraceresult.getLocation();
			distance = vec3d1.distanceTo(hitVec);
		}
		laserPitch = this.getXRot();
		laserYaw = this.getYRot();
		if (distance <= 0) {
			distance = this.speed;
		}
	}

	/**
	 * onHit without removing the beam (1.12: onHit(); isDead=false;)
	 */
	protected void onHitKeepAlive(HitResult hit) {
		this.tracing = true;
		try {
			this.onHit(hit);
		} finally {
			this.tracing = false;
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		if (this.tracing && reason == RemovalReason.DISCARDED) {
			return;
		}
		super.remove(reason);
	}

	@Override
	public void tick() {
		--this.ticksToLive;
		if (this.ticksToLive <= 0) {
			this.discard();
		}
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		super.writeSpawnData(buffer);
		buffer.writeDouble(this.distance);
		buffer.writeFloat(this.laserPitch);
		buffer.writeFloat(this.laserYaw);
		buffer.writeShort(this.maxTicks);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		super.readSpawnData(additionalData);
		this.distance = additionalData.readDouble();
		this.laserPitch = additionalData.readFloat();
		this.laserYaw = additionalData.readFloat();
		this.maxTicks = additionalData.readShort();
	}

	/**
	 * 1.12 getRenderBoundingBox (the 1.12 version used pos2.z for the x coordinate by mistake)
	 */
	@Override
	public AABB getBoundingBoxForCulling() {
		if (this.distance <= 0) {
			return super.getBoundingBoxForCulling();
		}
		Vec3 pos2 = new Vec3(this.motionX, this.motionY, this.motionZ).normalize().scale(distance);
		return new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + pos2.x, this.getY() + pos2.y, this.getZ() + pos2.z).inflate(0.5d);
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return true;
	}

	public double getDistance() {
		return distance;
	}

	public float getLaserPitch() {
		return laserPitch;
	}

	public float getLaserYaw() {
		return laserYaw;
	}

	public short getMaxTicks() {
		return maxTicks;
	}
}
