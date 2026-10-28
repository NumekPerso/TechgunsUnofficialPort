package techguns.entities.npcs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.damagesystem.TGDamageSource;
import techguns.entities.ai.EntityAIFindEntityNearestPlayer;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.RocketProjectile;
import techguns.packets.PacketPlaySound;
import techguns.packets.PacketSpawnParticleOnEntity;
import techguns.util.EntityCondition;
import techguns.util.TGSoundCategory;

/**
 * Fire immunity: EntityType builder (fireImmune). 1.12 getMaxSpawnedInChunk = 1 (not naturally spawned).
 */
public class AttackHelicopter extends GenericFlyingMob {
	private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(AttackHelicopter.class, EntityDataSerializers.BOOLEAN);

	private int ambientSoundTick = 0;

	protected static final int TARGET_FLY_HEIGHT = 24;

	public static final int MAX_DEATH_TIME = 100;

	protected static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/attackhelicopter");

	public AttackHelicopter(EntityType<? extends AttackHelicopter> type, Level worldIn) {
		super(type, worldIn);
		this.xpReward = 5;
		this.moveControl = new GenericFlyingMob.FlyingMobMoveHelper(this);
	}

	@Override
	@Nullable
	protected ResourceLocation getDefaultLootTable() {
		return LOOT;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(5, new GenericFlyingMob.AIRandomFly(this));
		this.goalSelector.addGoal(7, new GenericFlyingMob.AILookAround(this));
		this.goalSelector.addGoal(7, new AttackHelicopter.AIHelicopterAttack(this));
		//1.12 searched +-4 blocks vertically: flying TARGET_FLY_HEIGHT blocks above the ground it never found the player
		this.targetSelector.addGoal(1, new EntityAIFindEntityNearestPlayer(this, TARGET_FLY_HEIGHT * 2));
	}

	public boolean isAttacking() {
		return this.entityData.get(ATTACKING).booleanValue();
	}

	public void setAttacking(boolean attacking) {
		this.entityData.set(ATTACKING, Boolean.valueOf(attacking));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ATTACKING, Boolean.valueOf(false));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.FOLLOW_RANGE, 128.0D).add(Attributes.ARMOR, 16.0).add(Attributes.ARMOR_TOUGHNESS, 5);
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return TGSounds.HELICOPTER_HIT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return TGSounds.HELICOPTER_HIT;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	@Override
	protected float getSoundVolume() {
		return 10.0F;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 1;
	}

	@Override
	public void baseTick() {
		super.baseTick();

		if (--ambientSoundTick <= 0 && this.deathTime == 0) {
			this.playSound(TGSounds.HELICOPTER_ROTOR, 6.0f, 1.0f);
			ambientSoundTick = 61;
		}
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
		return 0.5f;
	}

	/**
	 * 1.12 onDeathUpdate: falls and explodes after MAX_DEATH_TIME ticks (the experience is dropped on death in 1.20)
	 */
	@Override
	protected void tickDeath() {
		if (this.deathTime == 0) {
			this.playSound(TGSounds.HELICOPTER_DEATH, 6.0f, 1.0f);
		}
		++this.deathTime;

		if (this.deathTime == MAX_DEATH_TIME) {

			this.playSound(TGSounds.HELICOPTER_EXPLODE, 6.0f, 1.0f);

			Techguns.proxy.createFX("HelicopterDeathExplosion", this.level(), this.getX(), this.getY() - 8D, this.getZ(), 0, 0, 0);

			if (!this.level().isClientSide && this.deathLootSource != null) {
				this.dropDebrisLoot();
			}
		} else if (this.deathTime == MAX_DEATH_TIME + 1 && !this.level().isClientSide) {
			//one tick later: the client knows the item entities now
			for (ItemEntity item : this.debrisDrops) {
				if (item.isAlive()) {
					TGPackets.network.sendToAllAround(new PacketSpawnParticleOnEntity("RocketLauncherTrail", item, 0f, 0f, 0f, false, EntityCondition.AIRBORNE), TGPackets.targetPointAroundEnt(item, 150.0f));
				}
			}
			this.debrisDrops.clear();
		}
		//removed a few ticks later than 1.12 so the client reaches MAX_DEATH_TIME (explosion FX) before the removal packet,
		//the renderer doesn't render it anymore after MAX_DEATH_TIME
		if (this.deathTime >= MAX_DEATH_TIME + 3 && !this.level().isClientSide) {
			this.remove(Entity.RemovalReason.KILLED);
		}
	}

	/**
	 * The loot (and experience) of the death is kept until the explosion: dropped on death it fell straight down from
	 * where the helicopter was hit, it now flies off with the debris of the explosion
	 */
	@Nullable
	protected DamageSource deathLootSource = null;
	protected int deathLootPlayerTime = 0;
	@Nullable
	protected Player deathLootPlayer = null;
	protected boolean droppingDebrisLoot = false;
	protected final List<ItemEntity> debrisDrops = new ArrayList<>();

	@Override
	protected void dropAllDeathLoot(DamageSource source) {
		if (this.droppingDebrisLoot) {
			super.dropAllDeathLoot(source);
		} else {
			this.deathLootSource = source;
			this.deathLootPlayerTime = this.lastHurtByPlayerTime;
			this.deathLootPlayer = this.lastHurtByPlayer;
		}
	}

	protected void dropDebrisLoot() {
		//the "killed by player" loot conditions and the experience need the last player hit (reset while dying)
		this.lastHurtByPlayerTime = this.deathLootPlayerTime;
		this.lastHurtByPlayer = this.deathLootPlayer;
		this.droppingDebrisLoot = true;
		try {
			this.dropAllDeathLoot(this.deathLootSource);
		} finally {
			this.droppingDebrisLoot = false;
			this.deathLootSource = null;
		}
	}

	@Override
	@Nullable
	public ItemEntity spawnAtLocation(ItemStack stack, float offsetY) {
		ItemEntity item = super.spawnAtLocation(stack, offsetY);
		if (item != null && this.droppingDebrisLoot) {
			//center of the explosion FX, thrown outwards like the debris parts
			item.setPos(this.getX(), this.getY() - 8D, this.getZ());
			double angle = this.random.nextDouble() * Math.PI * 2.0;
			double v = 0.35 + this.random.nextDouble() * 0.35;
			item.setDeltaMovement(Math.cos(angle) * v, 0.4 + this.random.nextDouble() * 0.4, Math.sin(angle) * v);
			this.debrisDrops.add(item);
		}
		return item;
	}

	static class AIHelicopterAttack extends Goal {
		private final AttackHelicopter parentEntity;
		public int attackTimer;

		public AIHelicopterAttack(AttackHelicopter heli) {
			this.parentEntity = heli;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean canUse() {
			return this.parentEntity.getTarget() != null;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void start() {
			this.attackTimer = 0;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		@Override
		public void stop() {
			this.parentEntity.setAttacking(false);
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		@Override
		public void tick() {
			LivingEntity entitylivingbase = this.parentEntity.getTarget();
			if (entitylivingbase == null) {
				return;
			}

			if (entitylivingbase.distanceToSqr(this.parentEntity) < 4096.0D && this.parentEntity.hasLineOfSight(entitylivingbase)) {
				Level world = this.parentEntity.level();
				++this.attackTimer;

				if (this.attackTimer >= 14 && (this.attackTimer < 24) && (this.attackTimer % 2 == 0)) {
					if (this.attackTimer == 14) {
						TGPackets.network.sendToAllAround(new PacketPlaySound(TGSounds.HELICOPTER_BURST, this.parentEntity, 8.0f, 1.0f, false, false, TGSoundCategory.GUN_FIRE), TGPackets.targetPointAroundEnt(parentEntity, 100.0f));
					}

					GenericProjectile bullet = new GenericProjectile(world, this.parentEntity, 12.0f, 1.0f, 100, 0.05f, 30, 40, 8.0f, 0.25f, false, EnumBulletFirePos.CENTER);
					aimAt(bullet, entitylivingbase, 1.0f, 8.0f);
					world.addFreshEntity(bullet);

				} else if (attackTimer == 35) {

					TGPackets.network.sendToAllAround(new PacketPlaySound(TGSounds.ROCKET_FIRE, this.parentEntity, 8.0f, 1.0f, false, false, TGSoundCategory.GUN_FIRE), TGPackets.targetPointAroundEnt(parentEntity, 100.0f));
					RocketProjectile rocket = new RocketProjectile(world, this.parentEntity, 12.0f, 1.0f, 100, 0.05f, 30, 40, 8.0f, 0.25f, false, this.parentEntity.random.nextBoolean() ? EnumBulletFirePos.LEFT : EnumBulletFirePos.RIGHT, 4.0f, 0.0f);
					aimAt(rocket, entitylivingbase, 1.0f, 4.0f);
					world.addFreshEntity(rocket);

				} else if (attackTimer > 35) {
					this.attackTimer = -30;
				}
			} else if (this.attackTimer > 0) {
				--this.attackTimer;
			}

			this.parentEntity.setAttacking(this.attackTimer > 10);
		}

		/**
		 * The projectiles were fired along the helicopter pitch, which only follows the target loosely (the model
		 * tilts with it): aim them at the middle of the target instead, same velocity as GenericProjectile (1.5 * speed)
		 */
		protected static void aimAt(GenericProjectile proj, LivingEntity target, float speed, float inaccuracy) {
			double dx = target.getX() - proj.getX();
			double dy = target.getY() + target.getBbHeight() * 0.5 - proj.getY();
			double dz = target.getZ() - proj.getZ();
			proj.shoot(dx, dy, dz, 1.5f * speed, inaccuracy);
			proj.setDeltaMovement(proj.motionX, proj.motionY, proj.motionZ);
		}
	}

	@Override
	public float getTotalArmorAgainstType(TGDamageSource dmgsrc) {
		switch (dmgsrc.damageType) {
		case EXPLOSION:
			return 0.0f;
		case LIGHTNING:
		case ENERGY:
			return 10.0f;
		case FIRE:
		case ICE:
		case PHYSICAL:
		case PROJECTILE:
		case POISON:
		case RADIATION:
			return 20.0f;
		case UNRESISTABLE:
		default:
			return 0.0f;
		}
	}

	@Override
	public float getPenetrationResistance(TGDamageSource dmgsrc) {
		switch (dmgsrc.damageType) {
		case PROJECTILE:
			return 0.5f;
		default:
			return 0.0f;
		}
	}

	@Override
	public float getWeaponPosX() {
		return 0;
	}

	@Override
	public float getWeaponPosY() {
		return 0;
	}

	@Override
	public float getWeaponPosZ() {
		return 0;
	}

	@Override
	public float getBulletOffsetSide() {
		return 2.0f;
	}

	@Override
	protected int getTargetFlyHeight() {
		return TARGET_FLY_HEIGHT;
	}
}
