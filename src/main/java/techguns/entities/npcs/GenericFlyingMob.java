package techguns.entities.npcs;

import java.util.EnumSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import techguns.api.npc.INPCTechgunsShooter;
import techguns.api.npc.INpcTGDamageSystem;
import techguns.damagesystem.DamageSystem;
import techguns.util.MathUtil;

public abstract class GenericFlyingMob extends FlyingMob implements Enemy, INpcTGDamageSystem, INPCTechgunsShooter, ITGSpawnerNPC {

	protected boolean tryLink = true;
	protected TGSpawnerNPCData spawnerData = new TGSpawnerNPCData();

	public GenericFlyingMob(EntityType<? extends GenericFlyingMob> type, Level worldIn) {
		super(type, worldIn);
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

	@Override
	protected float getDamageAfterArmorAbsorb(DamageSource source, float amount) {
		return DamageSystem.getDamageAfterArmorAbsorbNPC(this, source, amount);
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

	protected abstract int getTargetFlyHeight();

	protected static class AILookAround extends Goal {
		private final GenericFlyingMob parentEntity;

		public AILookAround(GenericFlyingMob mob) {
			this.parentEntity = mob;
			this.setFlags(EnumSet.of(Goal.Flag.LOOK));
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean canUse() {
			return true;
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
			if (this.parentEntity.getTarget() == null) {
				Vec3 motion = this.parentEntity.getDeltaMovement();
				this.parentEntity.setYRot(-((float) Mth.atan2(motion.x, motion.z)) * (180F / (float) Math.PI));
				this.parentEntity.yBodyRot = this.parentEntity.getYRot();
				this.parentEntity.setXRot(0f);
			} else {
				LivingEntity target = this.parentEntity.getTarget();

				if (target.distanceToSqr(this.parentEntity) < (96.0 * 96.0)) {
					double d1 = target.getX() - this.parentEntity.getX();
					double d2 = target.getZ() - this.parentEntity.getZ();
					this.parentEntity.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
					this.parentEntity.yBodyRot = this.parentEntity.getYRot();

					//Calculate the pitch
					Vec3 pos = new Vec3(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ());
					Vec3 target_pos = new Vec3(target.getX(), target.getY(), target.getZ());

					Vec3 groundPoint = new Vec3(pos.x, target_pos.y, pos.z);

					if (pos.y > groundPoint.y) {

						Vec3 toTarget = target_pos.subtract(pos).normalize();
						Vec3 toGround = groundPoint.subtract(pos).normalize();

						float acos = (float) (Math.acos(toTarget.dot(toGround)) * MathUtil.R2D);
						float angle = -MathUtil.clamp(90.0f - acos, 0f, 90f);
						this.parentEntity.getLookControl().setLookAt(target, 45.0f, angle);
					}
				}
			}
		}
	}

	protected static class AIRandomFly extends Goal {
		private final GenericFlyingMob parentEntity;

		public AIRandomFly(GenericFlyingMob mob) {
			this.parentEntity = mob;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean canUse() {
			MoveControl entitymovehelper = this.parentEntity.getMoveControl();

			if (!entitymovehelper.hasWanted()) {
				return true;
			} else {
				double d0 = entitymovehelper.getWantedX() - this.parentEntity.getX();
				double d1 = entitymovehelper.getWantedY() - this.parentEntity.getY();
				double d2 = entitymovehelper.getWantedZ() - this.parentEntity.getZ();
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				return d3 < 1.0D || d3 > 3600.0D;
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean canContinueToUse() {
			return false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void start() {
			RandomSource random = this.parentEntity.getRandom();
			double d0 = this.parentEntity.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double d2 = this.parentEntity.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			this.parentEntity.getMoveControl().setWantedPosition(d0, getGroundLevel(this.parentEntity) + this.parentEntity.getTargetFlyHeight(), d2, 1.0D);
		}

		private int getGroundLevel(GenericFlyingMob entity) {
			return entity.level().getHeight(Heightmap.Types.MOTION_BLOCKING, (int) entity.getX(), (int) entity.getZ());
		}

	}

	protected static class FlyingMobMoveHelper extends MoveControl {
		private final GenericFlyingMob parentEntity;
		private int courseChangeCooldown = 0;

		public FlyingMobMoveHelper(GenericFlyingMob mob) {
			super(mob);
			this.parentEntity = mob;
		}

		@Override
		public void tick() {
			if (this.operation == MoveControl.Operation.MOVE_TO) {
				double d0 = this.wantedX - this.parentEntity.getX();
				double d1 = this.wantedY - this.parentEntity.getY();
				double d2 = this.wantedZ - this.parentEntity.getZ();
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;

				if (this.courseChangeCooldown-- <= 0) {
					this.courseChangeCooldown += this.parentEntity.getRandom().nextInt(5) + 2;
					d3 = (double) Math.sqrt(d3);

					if (this.isNotColliding(this.wantedX, this.wantedY, this.wantedZ, d3)) {
						this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(d0 / d3 * 0.1D, d1 / d3 * 0.1D, d2 / d3 * 0.1D));
					} else {
						this.operation = MoveControl.Operation.WAIT;
					}
				}
			}
		}

		/**
		 * Checks if entity bounding box is not colliding with terrain
		 */
		private boolean isNotColliding(double x, double y, double z, double p_179926_7_) {
			double d0 = (x - this.parentEntity.getX()) / p_179926_7_;
			double d1 = (y - this.parentEntity.getY()) / p_179926_7_;
			double d2 = (z - this.parentEntity.getZ()) / p_179926_7_;
			AABB axisalignedbb = this.parentEntity.getBoundingBox();

			for (int i = 1; (double) i < p_179926_7_; ++i) {
				axisalignedbb = axisalignedbb.move(d0, d1, d2);

				if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) {
					return false;
				}
			}

			return true;
		}
	}

}
