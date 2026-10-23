package techguns.entities.projectiles;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.api.damagesystem.DamageType;
import techguns.api.npc.INPCTechgunsShooter;
import techguns.damagesystem.DamageSystem;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.packets.PacketGunImpactFX;

/**
 * Port of the 1.12 GenericProjectile. Movement is computed manually from the motionX/Y/Z fields like in 1.12,
 * the vanilla delta movement is kept in sync for rendering/interpolation.
 */
public class GenericProjectile extends Entity implements IEntityAdditionalSpawnData {

	public static final Predicate<Entity> BULLET_TARGETS = EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.ENTITY_STILL_ALIVE).and(Entity::isPickable);

	protected float damage;
	public float speed = 1.0f; // speed in blocks per tick

	protected int ticksToLive = 100;
	protected int lifetime = 100; // TTL but not reduced per tick

	// DMG drop stats
	protected float damageDropStart;
	protected float damageDropEnd;
	protected float damageMin;
	protected LivingEntity shooter;

	protected float penetration = 0.0f;

	protected boolean silenced = false;
	protected boolean blockdamage = false;

	protected boolean posInitialized = false;
	protected double startX;
	protected double startY;
	protected double startZ;

	protected float radius = 0.0f;
	protected double gravity = 0.0f;

	public double motionX;
	public double motionY;
	public double motionZ;

	public GenericProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
		this.setNoGravity(true);
	}

	public GenericProjectile(Level worldIn) {
		this(TGEntities.GENERIC_PROJECTILE, worldIn);
	}

	public GenericProjectile(EntityType<? extends GenericProjectile> type, Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos) {
		this(type, par2World);
		this.shooter = p;
		this.initProjectile(p.getX(), p.getY() + p.getEyeHeight(), p.getZ(), p.getYHeadRot(), p.getXRot(), damage, speed,
				TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
	}

	public GenericProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos) {
		this(TGEntities.GENERIC_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
	}

	public GenericProjectile(EntityType<? extends GenericProjectile> type, Level worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage,
			float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
			boolean blockdamage, EnumBulletFirePos firePos) {
		this(type, worldIn);
		this.shooter = null;
		this.initProjectile(posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
	}

	private void initProjectile(double posX, double posY, double posZ, float yaw, float pitch, float damage,
			float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
			boolean blockdamage, EnumBulletFirePos firePos) {

		float offsetSide = 0.16F;
		float offsetHeight = 0f;
		if (this.shooter != null && shooter instanceof INPCTechgunsShooter) {
			INPCTechgunsShooter tgshooter = (INPCTechgunsShooter) this.shooter;
			offsetSide += tgshooter.getBulletOffsetSide();
			offsetHeight += tgshooter.getBulletOffsetHeight();
		}

		float yRot = yaw + (float) (spread - (2 * Math.random() * spread)) * 40.0f;
		float xRot = pitch + (float) (spread - (2 * Math.random() * spread)) * 40.0f;

		double x = posX;
		double y = posY;
		double z = posZ;
		if (firePos == EnumBulletFirePos.RIGHT) {
			x -= (double) (Mth.cos(yRot / 180.0F * (float) Math.PI) * offsetSide);
			z -= (double) (Mth.sin(yRot / 180.0F * (float) Math.PI) * offsetSide);
		} else if (firePos == EnumBulletFirePos.LEFT) {
			x += (double) (Mth.cos(yRot / 180.0F * (float) Math.PI) * offsetSide);
			z += (double) (Mth.sin(yRot / 180.0F * (float) Math.PI) * offsetSide);
		}
		y += (-0.10000000149011612D + offsetHeight);

		this.moveTo(x, y, z, yRot, xRot);

		float f = 0.4F;
		this.motionX = (double) (-Mth.sin(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(this.getXRot() / 180.0F * (float) Math.PI) * f);
		this.motionZ = (double) (Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(this.getXRot() / 180.0F * (float) Math.PI) * f);
		this.motionY = (double) (-Mth.sin((this.getXRot()) / 180.0F * (float) Math.PI) * f);
		this.shoot(this.motionX, this.motionY, this.motionZ, 1.5f, 1.0F);

		this.motionX *= speed;
		this.motionY *= speed;
		this.motionZ *= speed;
		this.damage = damage;
		this.speed = speed;
		this.ticksToLive = TTL;
		this.lifetime = TTL;
		this.penetration = penetration;

		this.damageMin = dmgMin;
		this.damageDropStart = dmgDropStart;
		this.damageDropEnd = dmgDropEnd;
		this.blockdamage = blockdamage;
		this.syncDeltaMovement();
	}

	/**
	 * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
	 */
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		float f = Mth.sqrt((float) (x * x + y * y + z * z));
		x = x / (double) f;
		y = y / (double) f;
		z = z / (double) f;
		x = x + this.random.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		y = y + this.random.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		z = z + this.random.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		x = x * (double) velocity;
		y = y * (double) velocity;
		z = z * (double) velocity;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float f1 = Mth.sqrt((float) (x * x + z * z));
		this.setYRot((float) (Mth.atan2(x, z) * (180D / Math.PI)));
		this.setXRot((float) (Mth.atan2(y, (double) f1) * (180D / Math.PI)));
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
		//NOTE: 1.12 called motion.normalize() afterwards but discarded the result, so the 1.5 velocity factor stays (motion = dir*1.5*speed)
	}

	protected void syncDeltaMovement() {
		this.setDeltaMovement(this.motionX, this.motionY, this.motionZ);
	}

	@Override
	public void tick() {
		if (!this.posInitialized && !this.level().isClientSide) {
			this.initStartPos();
		}
		this.xo = this.getX();
		this.yo = this.getY();
		this.zo = this.getZ();
		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();

		super.tick();

		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			float f = Mth.sqrt((float) (this.motionX * this.motionX + this.motionZ * this.motionZ));
			this.setYRot((float) (Mth.atan2(this.motionX, this.motionZ) * (180D / Math.PI)));
			this.setXRot((float) (Mth.atan2(this.motionY, (double) f) * (180D / Math.PI)));
			this.yRotO = this.getYRot();
			this.xRotO = this.getXRot();
		}

		--this.ticksToLive;

		Vec3 vec3d1 = new Vec3(this.getX(), this.getY(), this.getZ());
		Vec3 vec3d = new Vec3(this.getX() + this.motionX, this.getY() + this.motionY, this.getZ() + this.motionZ);
		HitResult raytraceresult = this.level().clip(new ClipContext(vec3d1, vec3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if (raytraceresult.getType() == HitResult.Type.MISS) {
			raytraceresult = null;
		}

		if (raytraceresult != null) {
			vec3d = raytraceresult.getLocation();
		}

		HitResult raytraceresultEntity = this.findEntityOnPath(vec3d1, vec3d);
		if (raytraceresultEntity != null) {
			raytraceresult = raytraceresultEntity;
		}

		if (raytraceresult instanceof EntityHitResult ehr && ehr.getEntity() instanceof Player entityplayer) {
			if (this.shooter instanceof Player && !((Player) this.shooter).canHarmPlayer(entityplayer)) {
				raytraceresult = null;
			}
		}

		if (raytraceresult != null) {
			this.onHit(raytraceresult);
		}

		double newX = this.getX() + this.motionX;
		double newY = this.getY() + this.motionY;
		double newZ = this.getZ() + this.motionZ;
		float f4 = Mth.sqrt((float) (this.motionX * this.motionX + this.motionZ * this.motionZ));
		this.setYRot((float) (Mth.atan2(this.motionX, this.motionZ) * (180D / Math.PI)));

		this.setXRot((float) (Mth.atan2(this.motionY, (double) f4) * (180D / Math.PI)));
		if (this.getXRot() - this.xRotO < -180.0F) {
			this.xRotO -= 360.0F;
		}
		if (this.getXRot() - this.xRotO >= 180.0F) {
			this.xRotO += 360.0F;
		}
		if (this.getYRot() - this.yRotO < -180.0F) {
			this.yRotO -= 360.0F;
		}
		if (this.getYRot() - this.yRotO >= 180.0F) {
			this.yRotO += 360.0F;
		}

		this.setXRot(this.xRotO + (this.getXRot() - this.xRotO) * 0.2F);
		this.setYRot(this.yRotO + (this.getYRot() - this.yRotO) * 0.2F);
		float f1 = 0.99F;

		f1 = this.inWaterUpdateBehaviour(f1);

		this.motionX *= (double) f1;
		this.motionY *= (double) f1;
		this.motionZ *= (double) f1;

		if (this.gravity != 0) {
			this.motionY -= this.gravity;
		}

		this.setPos(newX, newY, newZ);
		this.syncDeltaMovement();
		this.checkInsideBlocks();

		if (this.ticksToLive <= 0) {
			this.discard();
		}
	}

	protected float inWaterUpdateBehaviour(float f1) {
		if (this.isInWater()) {
			for (int i = 0; i < 4; ++i) {
				this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - this.motionX * 0.25D,
						this.getY() - this.motionY * 0.25D, this.getZ() - this.motionZ * 0.25D, this.motionX, this.motionY,
						this.motionZ);
			}
			f1 = 0.85F;
		}

		if (this.isInWaterOrRain()) {
			this.clearFire();
		}
		return f1;
	}

	protected void initStartPos() {
		this.startX = this.getX();
		this.startY = this.getY();
		this.startZ = this.getZ();
		this.posInitialized = true;
	}

	@Nullable
	protected EntityHitResult findEntityOnPath(Vec3 start, Vec3 end) {
		Entity entity = null;
		List<Entity> list = this.level().getEntities(this, this.getBoundingBox().expandTowards(this.motionX, this.motionY, this.motionZ).inflate(1.0D), BULLET_TARGETS);
		double d0 = 0.0D;

		Vec3 hitPos = null;

		for (int i = 0; i < list.size(); ++i) {
			Entity entity1 = list.get(i);

			if (entity1 != this.shooter) {
				AABB axisalignedbb = entity1.getBoundingBox().inflate(0.30000001192092896D);
				Optional<Vec3> raytraceresult = axisalignedbb.clip(start, end);

				if (raytraceresult.isPresent()) {
					double d1 = start.distanceToSqr(raytraceresult.get());

					if (d1 < d0 || d0 == 0.0D) {
						entity = entity1;
						d0 = d1;
						hitPos = raytraceresult.get();
					}
				}
			}
		}

		if (entity != null) {
			return new EntityHitResult(entity, hitPos);
		} else {
			return null;
		}
	}

	/**
	 * Called when the projectile hits a block or an entity
	 */
	protected void onHit(HitResult raytraceResultIn) {

		if (raytraceResultIn instanceof EntityHitResult entityHit && !this.level().isClientSide) {

			TGDamageSource src = getProjectileDamageSource();

			if (entityHit.getEntity() instanceof LivingEntity ent) {

				float dmg = DamageSystem.getDamageFactor(this.shooter, ent) * this.getDamage();

				if (dmg > 0.0f) {

					if (src.knockbackMultiplier == 0.0f) {
						TGDamageSource knockback = TGDamageSource.getKnockbackDummyDmgSrc(this, this.shooter);
						ent.hurt(knockback, 0.01f);
					}

					ent.hurt(src, dmg);
					if (src.wasSuccessful()) {

						if (ent instanceof Mob) {
							this.setAIRevengeTarget(((Mob) ent));
						}

						this.onHitEffect(ent, raytraceResultIn);
					}
				}

			} else {
				entityHit.getEntity().hurt(src, this.getDamage());
			}

			this.discard();
		}

		if (raytraceResultIn instanceof BlockHitResult blockHit && raytraceResultIn.getType() == HitResult.Type.BLOCK) {
			this.hitBlock(blockHit);
			this.discard();
		}
	}

	protected void setAIRevengeTarget(Mob ent) {
		boolean shootBack = false;
		if (this.shooter != null) {
			if (!(shooter instanceof Player)) {
				shootBack = true;
			}
		}

		if (shootBack) {
			ent.setLastHurtByMob(shooter);
		}
	}

	public void shiftForward(float factor) {
		this.setPos(this.getX() + this.motionX * factor, this.getY() + this.motionY * factor, this.getZ() + this.motionZ * factor);
	}

	/**
	 * Override in subclass for entity hit effect (potion effects etc)
	 */
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResultIn) {
	}

	/**
	 * Override for block hit effect
	 */
	protected void hitBlock(BlockHitResult raytraceResultIn) {
		BlockPos targetPos = raytraceResultIn.getBlockPos();
		BlockState target = this.level().getBlockState(targetPos);
		SoundType sound = target.getSoundType(this.level(), targetPos, this);
		this.doImpactEffects(target, raytraceResultIn, sound);
	}

	protected static boolean isSound(SoundType sound, SoundType... types) {
		for (SoundType t : types) {
			if (sound == t) return true;
		}
		return false;
	}

	/**
	 * @param state may be null
	 * @param sound may be null
	 */
	protected void doImpactEffects(@Nullable BlockState state, HitResult rayTraceResult, @Nullable SoundType sound) {
		double x = rayTraceResult.getLocation().x;
		double y = rayTraceResult.getLocation().y;
		double z = rayTraceResult.getLocation().z;

		float[] angles = this.getImpactAngles(rayTraceResult);
		float pitch = angles[0];
		float yaw = angles[1];

		this.sendImpactFX(x, y, z, pitch, yaw, getImpactTypeForSound(sound));
	}

	/**
	 * Pitch and yaw of the impact effect, from the hit side (blocks) or the projectile rotation (entities)
	 *
	 * @return {pitch, yaw}
	 */
	protected float[] getImpactAngles(HitResult rayTraceResult) {
		float pitch = 0.0f;
		float yaw = 0.0f;
		if (rayTraceResult instanceof BlockHitResult bhr && rayTraceResult.getType() == HitResult.Type.BLOCK) {
			switch (bhr.getDirection()) {
			case UP:
				pitch = -90.0f;
				break;
			case DOWN:
				pitch = 90.0f;
				break;
			default:
				yaw = bhr.getDirection().toYRot();
				break;
			}
		} else {
			pitch = -this.getXRot();
			yaw = -this.getYRot();
		}
		return new float[] { pitch, yaw };
	}

	/**
	 * Impact type of the 1.12 sound type check: 0=stone, 1=wood, 2=glass, 3=metal, 4=dirt/sand, -1=default
	 */
	public static int getImpactTypeForSound(@Nullable SoundType sound) {
		int type = -1;
		if (sound != null) {
			if (isSound(sound, SoundType.STONE, SoundType.DEEPSLATE, SoundType.DEEPSLATE_BRICKS, SoundType.DEEPSLATE_TILES, SoundType.POLISHED_DEEPSLATE,
					SoundType.TUFF, SoundType.CALCITE, SoundType.NETHER_BRICKS, SoundType.BASALT, SoundType.NETHERRACK, SoundType.STONE)) {
				type = 0;
			} else if (isSound(sound, SoundType.WOOD, SoundType.LADDER, SoundType.BAMBOO_WOOD, SoundType.NETHER_WOOD, SoundType.CHERRY_WOOD, SoundType.SCAFFOLDING)) {
				type = 1;
			} else if (isSound(sound, SoundType.GLASS, SoundType.AMETHYST)) {
				type = 2;
			} else if (isSound(sound, SoundType.METAL, SoundType.ANVIL, SoundType.NETHERITE_BLOCK, SoundType.COPPER, SoundType.CHAIN, SoundType.LANTERN)) {
				type = 3;
			} else if (isSound(sound, SoundType.GRAVEL, SoundType.SAND, SoundType.ROOTED_DIRT, SoundType.MUD, SoundType.SOUL_SAND, SoundType.SOUL_SOIL)) {
				type = 4;
			}
		}
		return type;
	}

	protected void sendImpactFX(double x, double y, double z, float pitch, float yaw, int type) {
		sendImpactFX(x, y, z, pitch, yaw, type, false);
	}

	protected void sendImpactFX(double x, double y, double z, float pitch, float yaw, int type, boolean incendiary) {
		if (!this.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketGunImpactFX((short) type, x, y, z, pitch, yaw, incendiary), TGPackets.targetPoint(this.level(), x, y, z, TGEntities.bulletTrackRange));
		}
	}

	/**
	 * Server velocity updates are ignored like in 1.12, projectiles are simulated on the client from their spawn data.
	 */
	@Override
	public void lerpMotion(double x, double y, double z) {
	}

	/**
	 * Override to change damage type etc
	 */
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causeBulletDamage(this, this.shooter, DeathType.GORE);
		src.armorPenetration = this.penetration;
		src.setNoKnockback();
		return src;
	}

	/**
	 * Return the travelled distance
	 */
	protected double getDistanceTravelled() {
		Vec3 start = new Vec3(this.startX, this.startY, this.startZ);
		Vec3 end = new Vec3(this.getX(), this.getY(), this.getZ());
		return start.distanceTo(end);
	}

	public GenericProjectile setSilenced() {
		this.silenced = true;
		return this;
	}

	/**
	 * Get Damage for distance
	 */
	protected float getDamage() {

		if (this.damageDropEnd == 0f) { //not having damage drop
			return this.damage;
		}

		double distance = this.getDistanceTravelled();

		if (distance <= this.damageDropStart) {
			return this.damage;
		} else if (distance > this.damageDropEnd) {
			return this.damageMin;
		} else {
			float factor = 1.0f - (float) ((distance - this.damageDropStart) / (this.damageDropEnd - this.damageDropStart));
			return (this.damageMin + (this.damage - this.damageMin) * factor);
		}
	}

	public LivingEntity getShooter() {
		return shooter;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tags) {
		this.damage = tags.getFloat("damage");
		this.speed = tags.getFloat("speed");

		this.ticksToLive = tags.getShort("ticksToLive");
		this.lifetime = tags.getShort("lifetime");

		this.damageDropStart = tags.getShort("damageDropStart");
		this.damageDropEnd = tags.getShort("damageDropEnd");
		this.damageMin = tags.getFloat("damageMin");

		this.gravity = tags.getFloat("gravity");

		this.motionX = tags.getDouble("motionX");
		this.motionY = tags.getDouble("motionY");
		this.motionZ = tags.getDouble("motionZ");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tags) {
		tags.putFloat("damage", this.damage);
		tags.putFloat("speed", this.speed);

		tags.putShort("ticksToLive", (short) this.ticksToLive);
		tags.putShort("lifetime", (short) this.lifetime);

		tags.putShort("damageDropStart", (short) this.damageDropStart);
		tags.putShort("damageDropEnd", (short) this.damageDropEnd);
		tags.putFloat("damageMin", this.damageMin);

		if (this.gravity != 0d) {
			tags.putFloat("gravity", (float) this.gravity);
		}
		tags.putDouble("motionX", this.motionX);
		tags.putDouble("motionY", this.motionY);
		tags.putDouble("motionZ", this.motionZ);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeFloat(this.speed);
		buffer.writeDouble(this.motionX);
		buffer.writeDouble(this.motionY);
		buffer.writeDouble(this.motionZ);
		buffer.writeDouble(this.gravity); //not synced in 1.12, needed for the client side simulation of gravity projectiles
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		this.speed = additionalData.readFloat();
		this.motionX = additionalData.readDouble();
		this.motionY = additionalData.readDouble();
		this.motionZ = additionalData.readDouble();
		this.gravity = additionalData.readDouble();
		this.syncDeltaMovement();
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d0 = this.getBoundingBox().getSize() * 10.0D;
		if (Double.isNaN(d0)) {
			d0 = 1.0D;
		}
		d0 *= 64.0D * getViewScale();
		return distance < d0 * d0;
	}

	public float getSpeed() {
		return speed;
	}

	public double getGravity() {
		return gravity;
	}

	public float getRadius() {
		return radius;
	}

	public int getTicksToLive() {
		return ticksToLive;
	}

	public int getLifetime() {
		return lifetime;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean fireImmune() {
		return true;
	}

	public static void burnBlocks(Level world, BlockHitResult mop, double chanceToIgnite) {
		if (world.isClientSide) return;

		if (Math.random() <= chanceToIgnite) {
			BlockPos hit = mop.getBlockPos();

			switch (mop.getDirection()) {
			case DOWN:
				if (world.isEmptyBlock(hit.below())) world.setBlockAndUpdate(hit.below(), Blocks.FIRE.defaultBlockState());
				break;
			case UP:
				if (world.isEmptyBlock(hit.above())) {
					if (world.getBlockState(hit).is(Blocks.FARMLAND)) world.setBlockAndUpdate(hit, Blocks.DIRT.defaultBlockState());
					world.setBlockAndUpdate(hit.above(), Blocks.FIRE.defaultBlockState());
				}
				break;
			default:
				BlockPos p = hit.relative(mop.getDirection());
				if (world.isEmptyBlock(p)) {
					world.setBlockAndUpdate(p, Blocks.FIRE.defaultBlockState());
				}
			}
		}
	}

	public static class Factory implements IProjectileFactory<GenericProjectile> {

		@Override
		public GenericProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new GenericProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.PROJECTILE;
		}
	}
}
