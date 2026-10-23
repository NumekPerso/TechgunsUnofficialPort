package techguns.entities.npcs;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.npc.INPCTechgunsShooter;
import techguns.api.npc.INpcTGDamageSystem;
import techguns.damagesystem.DamageSystem;
import techguns.damagesystem.TGDamageSource;
import techguns.entities.projectiles.AlienBlasterProjectile;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.packets.PacketPlaySound;
import techguns.util.TGSoundCategory;

/**
 * Fire immunity: EntityType builder (fireImmune)
 */
public class Ghastling extends Monster implements INpcTGDamageSystem, INPCTechgunsShooter, ITGSpawnerNPC {

	private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(Ghastling.class, EntityDataSerializers.BOOLEAN);

	protected static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/ghastling");

	protected boolean tryLink = true;
	protected TGSpawnerNPCData spawnerData = new TGSpawnerNPCData();

	public Ghastling(EntityType<? extends Ghastling> type, Level worldIn) {
		super(type, worldIn);
		this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
		this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
		this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
		this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
		this.xpReward = 10;
	}

	@Override
	@Nullable
	protected ResourceLocation getDefaultLootTable() {
		return LOOT;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(4, new AIFireballAttack(this));
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
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
		return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.ARMOR, 10.0).add(Attributes.ARMOR_TOUGHNESS, 0);
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.GHAST_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.GHAST_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.GHAST_DEATH;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	@Override
	protected float getSoundVolume() {
		return 10.0F;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
		return 1.5f;
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
	public float getTotalArmorAgainstType(TGDamageSource dmgsrc) {
		return 0;
	}

	@Override
	public float getPenetrationResistance(TGDamageSource dmgsrc) {
		return 0;
	}

	@Override
	protected float getDamageAfterArmorAbsorb(DamageSource source, float amount) {
		return DamageSystem.getDamageAfterArmorAbsorbNPC(this, source, amount);
	}

	/**
	 * Unused in 1.12 (not added to the tasks)
	 */
	protected static class AIGhastlingAttack extends Goal {
		private final Ghastling parentEntity;
		public int attackTimer;

		public AIGhastlingAttack(Ghastling mob) {
			this.parentEntity = mob;
		}

		@Override
		public boolean canUse() {
			return this.parentEntity.getTarget() != null;
		}

		@Override
		public void start() {
			this.attackTimer = 0;
		}

		@Override
		public void stop() {
			this.parentEntity.setAttacking(false);
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity entitylivingbase = this.parentEntity.getTarget();
			if (entitylivingbase == null) {
				return;
			}

			if (entitylivingbase.distanceToSqr(this.parentEntity) < 4096.0D && this.parentEntity.hasLineOfSight(entitylivingbase)) {
				Level world = this.parentEntity.level();
				++this.attackTimer;

				if (attackTimer == 15) {
					TGPackets.network.sendToAllAround(new PacketPlaySound(TGSounds.NETHERBLASTER_FIRE, this.parentEntity, 1.0f, 1.0f, false, false, TGSoundCategory.GUN_FIRE), TGPackets.targetPointAroundEnt(parentEntity, 100.0f));
					AlienBlasterProjectile rocket = new AlienBlasterProjectile(world, parentEntity, 10, 1.5f, 200, 0.05f, 200, 200, 10, 0, false, EnumBulletFirePos.CENTER);
					world.addFreshEntity(rocket);

				} else if (attackTimer > 30) {
					this.attackTimer = 0;
				}
			} else if (this.attackTimer > 0) {
				--this.attackTimer;
			}

			this.parentEntity.setAttacking(this.attackTimer > 10);
		}
	}

	protected static class AIFireballAttack extends Goal {
		private final Ghastling parentEntity;
		private int attackStep;
		private int attackTime;

		public AIFireballAttack(Ghastling mob) {
			this.parentEntity = mob;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean canUse() {
			LivingEntity entitylivingbase = this.parentEntity.getTarget();
			return entitylivingbase != null && entitylivingbase.isAlive();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void start() {
			this.attackStep = 0;
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
			--this.attackTime;
			LivingEntity entitylivingbase = this.parentEntity.getTarget();
			if (entitylivingbase == null) {
				return;
			}
			double d0 = this.parentEntity.distanceToSqr(entitylivingbase);

			if (d0 < 4.0D) {
				if (this.attackTime <= 0) {
					this.attackTime = 20;
					this.parentEntity.doHurtTarget(entitylivingbase);
				}

				this.parentEntity.getMoveControl().setWantedPosition(entitylivingbase.getX(), entitylivingbase.getY(), entitylivingbase.getZ(), 1.0D);
			} else if (d0 < this.getFollowDistance() * this.getFollowDistance()) {

				if (this.attackTime <= 0) {
					++this.attackStep;

					if (this.attackStep == 1) {
						this.attackTime = 30;
						this.parentEntity.setAttacking(true);
					} else if (this.attackStep <= 4) {
						this.attackTime = 6;
					} else {
						this.attackTime = 50;
						this.attackStep = 0;
						this.parentEntity.setAttacking(false);
					}

					if (this.attackStep > 1) {
						this.parentEntity.level().levelEvent((Player) null, 1018, BlockPos.containing(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ()), 0);

						AlienBlasterProjectile rocket = new AlienBlasterProjectile(this.parentEntity.level(), parentEntity, 6, 1.5f, 200, 0.05f, 200, 200, 6, 0, false, EnumBulletFirePos.CENTER);

						this.parentEntity.level().addFreshEntity(rocket);
					}
				}

				this.parentEntity.getLookControl().setLookAt(entitylivingbase, 10.0F, 10.0F);
			} else {
				this.parentEntity.getNavigation().stop();
				this.parentEntity.getMoveControl().setWantedPosition(entitylivingbase.getX(), entitylivingbase.getY(), entitylivingbase.getZ(), 1.0D);
			}
		}

		private double getFollowDistance() {
			AttributeInstance iattributeinstance = this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE);
			return iattributeinstance == null ? 16.0D : iattributeinstance.getValue();
		}
	}

	/***
	 * Spawner linking behavior
	 */
	@Override
	public boolean getTryLink() {
		return tryLink;
	}

	@Override
	public void setTryLink(boolean value) {
		this.tryLink = value;
	}

	@Override
	public TGSpawnerNPCData getSpawnerData() {
		return this.spawnerData;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.spawnerData.readFromEntityNBT(tag);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		this.spawnerData.writeToEntityNBT(tag);
	}

	@Override
	public void checkDespawn() {
		super.checkDespawn();
		this.despawnEntitySpawner(this.level(), this.isRemoved());
	}

	@Override
	public void die(DamageSource cause) {
		super.die(cause);
		this.onDeathSpawner(this.level(), this.dead);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide && this.level().getDifficulty() == Difficulty.PEACEFUL) {
			this.discard();
		}

		this.onUpdateSpawner(this.level());
	}
}
