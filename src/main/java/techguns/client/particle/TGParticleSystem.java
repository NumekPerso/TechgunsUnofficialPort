package techguns.client.particle;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import techguns.TGConfig;
import techguns.client.particle.TGParticleSystemType.DirResult;
import techguns.util.EntityCondition;
import techguns.util.MathUtil;

/**
 * A particle system which spawns particles
 */
public class TGParticleSystem extends Particle implements ITGParticle {

	protected static final Random rand = new Random();

	public TGParticleSystemType type;

	public EntityCondition condition = EntityCondition.NONE;

	int systemLifetime;
	int spawnDelay = 0;

	public float scale =1.0f; //global scale
	public float rotationYaw;
	public float rotationPitch;

	public float prevRotationYaw;
	public float prevRotationPitch;

	public int initialDelay;
	public int ticksExisted = 0;

	public float startSizeRate;
	public float startSizeRateDamping;
	public float startSize = 0.0f;

	protected TGParticleStreak prevParticle = null;

	Entity entity; //parent entity (if attached to an entity)
	public boolean attachToHead = false;
	public Vec3 entityOffset = null;
	TGParticle parent; //parent particle (if attached to a particle)

	protected boolean itemAttached=false;

	public TGParticleSystem(Level worldIn, TGParticleSystemType type, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		super(clientLevel(worldIn), xCoordIn + type.offset.x, yCoordIn+type.offset.y, zCoordIn+type.offset.z);
		this.xd = xSpeedIn;
		this.yd = ySpeedIn;
		this.zd = zSpeedIn;
		this.type = type;
		this.init();
	}

	public TGParticleSystem(Entity entity, TGParticleSystemType type) {
		this(entity.level(), type, entity.getX(),entity.getY(),entity.getZ(),0,0,0);
		this.entity = entity;
	}


	public TGParticleSystem(Level worldIn,TGParticle part, TGParticleSystemType type) {
		this(worldIn, type, part.posX(), part.posY(), part.posZ(),0,0,0);
		this.parent = part;
	}

	/**
	 * Particles need a ClientLevel, FX are only created on the client
	 */
	protected static ClientLevel clientLevel(Level world) {
		if (world instanceof ClientLevel) {
			return (ClientLevel) world;
		}
		return Minecraft.getInstance().level;
	}

	public void renderParticle(PoseStack poseStack, MultiBufferSource buffers, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		//This is a ParticleSystem which spawns the actual particles. Don't render anything
	}

	protected void init() {
		this.systemLifetime = MathUtil.randomInt(rand, type.systemLifetimeMin, type.systemLifetimeMax);
		this.initialDelay = MathUtil.randomInt(rand, type.initialDelayMin, type.initialDelayMax);
		this.startSizeRate = MathUtil.randomFloat(rand, type.startSizeRateMin, type.startSizeRateMax) * this.scale;
		this.startSizeRateDamping = MathUtil.randomFloat(rand, type.startSizeRateDampingMin, type.startSizeRateDampingMax);
	}

	public void onUpdate() {
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (type == null) {
			this.remove();
			return;
		}

		if (this.entity!=null && !this.itemAttached){
			if (!condition.evaluate(entity)) {
				this.remove();
				return;
			}

			if (this.attachToHead && entity instanceof LivingEntity) {
				LivingEntity elb = (LivingEntity) entity;

				this.rotationPitch=elb.getXRot();
				this.rotationYaw=elb.yHeadRot;

				Vec3 offset = type.offset;
				if (this.entityOffset != null) offset = offset.add(this.entityOffset);

				offset = offset.xRot((float) (-elb.getXRot()*MathUtil.D2R));
				offset = offset.yRot((float) ((-elb.yHeadRot)*MathUtil.D2R));

				this.x = elb.xo + offset.x;
				this.y = elb.yo + elb.getEyeHeight() + offset.y;
				this.z = elb.zo + offset.z;
			}else {
				this.rotationPitch=entity.getXRot();
				this.rotationYaw=entity.getYRot();

				Vec3 offset = type.offset;
				if (this.entityOffset != null) offset = offset.add(this.entityOffset);

				offset = offset.xRot((float) (-entity.getXRot()*MathUtil.D2R));
				offset = offset.yRot((float) ((-entity.getYRot())*MathUtil.D2R));

				this.x = entity.xo + offset.x;
				this.y = entity.yo + offset.y;
				this.z = entity.zo + offset.z;
			}
			Vec3 motion = entity.getDeltaMovement();
			this.xd = motion.x;
			this.yd = motion.y;
			this.zd = motion.z;

		} else if (this.parent != null) {
			this.x=parent.posX() + type.offset.x;
			this.y=parent.posY() + type.offset.y;
			this.z=parent.posZ() + type.offset.z;
		} else {
			this.x+=xd;
			this.y+=yd;
			this.z+=zd;
		}

		if (initialDelay-- > 0) {
			return;
		}

		this.startSize = this.startSize+this.startSizeRate;
		this.startSizeRate = (this.startSizeRate * this.startSizeRateDamping);


		if (spawnDelay-- <= 0) {
			int count = MathUtil.randomInt(rand, type.particleCountMin, type.particleCountMax);
			if (!this.type.streak && !this.itemAttached) {
				count = applyEffectsQuality(count);
			}

			for (int i = 0; i < count; i++) {
				//Get position and motion data
				DirResult dir = this.type.new DirResult();
				Vec3 position = type.volumeType.getPosition(this, dir, i, count);
				Vec3 motion = type.velocityType.getVelocity(this, dir.values);

				motion = motion.scale(this.scale);
				position = position.scale(this.scale);

				//apply ParticleSystem's entity rotation
				motion = motion.xRot((float) (-this.rotationPitch*MathUtil.D2R));
				motion = motion.yRot((float) ((-this.rotationYaw)*MathUtil.D2R));
				if (type.volumeType != TGParticleSystemType.VOL_TRAIL) {
					position = position.xRot((float) (-this.rotationPitch*MathUtil.D2R));
					position = position.yRot((float) ((-this.rotationYaw)*MathUtil.D2R));
				}

				//Spawn particle
				double mf = 0.05D; //Per Second instead of Per Tick
				TGParticle particle=null;
				if (this.type.streak) {
					TGParticleStreak particleStreak = new TGParticleStreak(this.level, this.x+position.x, this.y+position.y, this.z+position.z, motion.x*mf, motion.y*mf, motion.z*mf, this);
					if (prevParticle != null) {
						particleStreak.setPrev(prevParticle);
						prevParticle.setNext(particleStreak);
					}
					addEffect(particleStreak);
					prevParticle = particleStreak;
					particle = particleStreak;
				}else {
					if(this.itemAttached) {
						particle = new TGParticleItemAttached(this.level, this.x+position.x, this.y+position.y, this.z+position.z, motion.x*mf, motion.y*mf, motion.z*mf, this, this.entity);
					} else {
						particle = new TGParticle(this.level, this.x+position.x, this.y+position.y, this.z+position.z, motion.x*mf, motion.y*mf, motion.z*mf, this);
					}
					addEffect(particle);
				}
				if (this.type.attachedSystem != null && !this.type.attachedSystem.equals("")) {
					List<TGParticleSystem> systems = TGFX.createFXOnParticle(this.level, particle, this.type.attachedSystem);
					if (systems!=null) {
						for (TGParticleSystem s : systems) {
							s.scale = this.scale;
							addEffect(s);
						}
					}
				}
			}
			spawnDelay = MathUtil.randomInt(rand, type.spawnDelayMin, type.spawnDelayMax);
		}

		if (this.entity!=null){
			if (this.entity.isRemoved()){
				this.remove();
			} else if ( this.systemLifetime>0 && ticksExisted>= this.systemLifetime){
				this.remove();
			}

		} else if (this.parent != null) {
			if (!this.parent.isAlive()) {
				this.remove();
			}
		}else {
			if (ticksExisted >= this.systemLifetime) {
				this.remove();
			}
		}
		ticksExisted++;


	}

	/**
	 * Scales a particle count by the client "effects quality" setting, the fractional part is spawned randomly so that
	 * systems spawning one particle at a time keep spawning (fewer) particles
	 */
	public static int applyEffectsQuality(int count) {
		float q = TGConfig.cl_effectsQuality;
		if (q >= 1.0f || count <= 0) {
			return count;
		}
		float c = count * q;
		int n = (int) c;
		if (rand.nextFloat() < c - n) {
			n++;
		}
		return n;
	}

	@Override
	public void tick() {
		this.onUpdate();
	}

	protected void addEffect(ITGParticle s) {
		TGFXHandler.particleManager.addEffect(s);
	}

	public double posX() {
		return x;
	}

	public double posY() {
		return y;
	}

	public double posZ() {
		return z;
	}

	public double motionX() {
		return xd;
	}

	public double motionY() {
		return yd;
	}

	public double motionZ() {
		return zd;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public Vec3 getPos() {
		return new Vec3(this.x, this.y, this.z);
	}

	@Override
	public boolean shouldRemove() {
		return !this.isAlive();
	}

	@Override
	public void updateTick() {
		this.onUpdate();
	}

	@Override
	public void doRender(PoseStack poseStack, MultiBufferSource buffers, Entity entityIn, float partialTickTime, float rotX, float rotZ,
			float rotYZ, float rotXY, float rotXZ) {
		this.renderParticle(poseStack, buffers, entityIn, partialTickTime, rotX, rotZ, rotYZ, rotXY, rotXZ);
	}

	@Override
	public AABB getRenderBoundingBox(float partialTickTime, Entity viewEntity) {
		//DOESN'T MATTER, SYSTEM DOES NOT RENDER STUFF ANYWAY
		float s = 0.5f;
		return new AABB(this.x-s, this.y-s, this.z-s, this.x+s, this.y+s, this.z+s);
	}

	@Override
	public double getDepth() {
		return 0;
	}

	@Override
	public void setDepth(double depth) {
	}

	@Override
	public void setItemAttached() {
	}

	@Override
	public void remove() {
		super.remove();
		this.entity=null;
	}

	/**
	 * Not used, systems are not added to the vanilla particle engine
	 */
	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.NO_RENDER;
	}
}
