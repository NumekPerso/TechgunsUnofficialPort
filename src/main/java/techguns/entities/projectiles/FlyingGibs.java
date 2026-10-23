package techguns.entities.projectiles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Body part flying around after a gore death. Client side only entity (spawned by the client DeathEffect), never saved.
 *
 * 1.12 stored the client GoreData and the trail particle system in this class. To keep this common class loadable on
 * a dedicated server (the EntityType is registered on both sides), the client data lives in the client subclass
 * techguns.client.particle.FlyingGibsClient, which implements onHitGround().
 */
public class FlyingGibs extends Entity{

	/**
	 * Registered in TGEntities ("techguns:flyinggibs")
	 */
	public static EntityType<FlyingGibs> TYPE;

	public int maxTimeToLive = 200;
	public int timeToLive = 200;

	public double gravity = 0.029999999329447746D;
	public float size;
	public Vec3 rotationAxis = new Vec3(0, 1, 0);
	public int bodypart;
	public int hitGroundTTL = 0;

	//the exploding entity
	public LivingEntity entity;

	public FlyingGibs(Level world, LivingEntity entity, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float size, int bodypart) {
		this(TYPE, world);
		this.setPos(posX, posY, posZ);
		this.xo = posX;
		this.yo = posY;
		this.zo = posZ;
		this.setDeltaMovement(motionX, motionY, motionZ);
		this.size = size;
		this.maxTimeToLive = 75 + this.random.nextInt(50);
		this.timeToLive = maxTimeToLive;
		this.entity = entity;
		this.bodypart = bodypart;
		this.rotationAxis = new Vec3(random.nextDouble(), random.nextDouble(), random.nextDouble());
	}

	public FlyingGibs(EntityType<? extends FlyingGibs> type, Level world) {
		super(type, world);
	}

	/**
	 * Called once when the gib hits the ground the first time (1.12: trail_system.setExpired())
	 */
	protected void onHitGround() {
	}

	@Override
	public void tick()
	{
		super.tick();

		if (this.timeToLive > 0)
		{
			--timeToLive;
		}else {
			this.discard();
			return;
		}

		this.xo = this.getX();
		this.yo = this.getY();
		this.zo = this.getZ();
		Vec3 motion = this.getDeltaMovement();
		double motionX = motion.x;
		double motionY = motion.y - gravity;
		double motionZ = motion.z;

		if (this.level().getFluidState(this.blockPosition()).is(FluidTags.LAVA))
		{
			motionY = 0.20000000298023224D;
			motionX = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
			motionZ = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
			this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
		}

		this.setDeltaMovement(motionX, motionY, motionZ);
		this.move(MoverType.SELF, this.getDeltaMovement());
		motion = this.getDeltaMovement();
		motionX = motion.x;
		motionY = motion.y;
		motionZ = motion.z;
		float f = 0.98F;

		if (this.onGround())
		{
			if (hitGroundTTL == 0) {
				hitGroundTTL = timeToLive;
				this.onHitGround();
			}
			f = (float) (this.level().getBlockState(this.blockPosition()).getBlock().getFriction() * 0.98);

		}

		motionX *= (double)f;
		motionY *= 0.9800000190734863D;
		motionZ *= (double)f;

		if (this.onGround())
		{
			motionY *= -0.8999999761581421D;
		}
		this.setDeltaMovement(motionX, motionY, motionZ);
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return super.getBoundingBoxForCulling().inflate(Math.max(1.0f, this.size));
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
