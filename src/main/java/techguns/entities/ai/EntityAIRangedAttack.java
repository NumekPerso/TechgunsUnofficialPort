package techguns.entities.ai;

import java.util.EnumSet;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

/**
 * Ranged attack goal with burst fire (1.12 EntityAIRangedAttack)
 */
public class EntityAIRangedAttack extends Goal {
	/** The entity the AI instance has been applied to */
	private final Mob entityHost;
	/** The entity (as a RangedAttackMob) the AI instance has been applied to. */
	private final RangedAttackMob rangedAttackEntityHost;
	private LivingEntity attackTarget;
	/**
	 * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
	 * maxRangedAttackTime.
	 */
	private int rangedAttackTime;
	private double entityMoveSpeed;
	private int ticksTargetSeen;
	private int attackTimeVariance;
	/** The maximum time the AI has to wait before peforming another ranged attack. */
	private int maxRangedAttackTime;
	private float attackRange;
	private float attackRange_2;

	//GUN HANDLING:
	private int maxBurstCount; //Total number of shots in burst.
	private int burstCount; //shots left in current burst.
	private int shotDelay; //delay between shots in burst.

	public EntityAIRangedAttack(RangedAttackMob shooter, double moveSpeed, int attackTimeVariance, int attackTime, float attackRange, int maxBurstCount, int shotDelay) {
		this.rangedAttackTime = -1;

		if (!(shooter instanceof Mob)) {
			throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
		} else {
			this.rangedAttackEntityHost = shooter;
			this.entityHost = (Mob) shooter;
			this.entityMoveSpeed = moveSpeed;
			this.attackTimeVariance = attackTimeVariance;
			this.maxRangedAttackTime = attackTime;
			this.attackRange = attackRange;
			this.attackRange_2 = attackRange * attackRange;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));

			this.maxBurstCount = maxBurstCount;
			this.burstCount = maxBurstCount;
			this.shotDelay = shotDelay;
		}
	}

	@Override
	public boolean canUse() {
		LivingEntity entitylivingbase = this.entityHost.getTarget();

		if (entitylivingbase == null || !entitylivingbase.isAlive()) {
			return false;
		} else {
			this.attackTarget = entitylivingbase;
			return true;
		}
	}

	@Override
	public boolean canContinueToUse() {
		return this.canUse() || !this.entityHost.getNavigation().isDone();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void stop() {
		this.attackTarget = null;
		this.ticksTargetSeen = 0;
		this.rangedAttackTime = -1;
	}

	@Override
	public void tick() {
		if (this.attackTarget == null) {
			return;
		}
		double d0 = this.entityHost.distanceToSqr(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
		boolean targetInSight = this.entityHost.getSensing().hasLineOfSight(this.attackTarget);

		if (targetInSight) {
			++this.ticksTargetSeen;
		} else {
			this.ticksTargetSeen = 0;
		}

		if (d0 <= (double) this.attackRange_2 && this.ticksTargetSeen >= 20) {
			this.entityHost.getNavigation().stop();
		} else {
			this.entityHost.getNavigation().moveTo(this.attackTarget, this.entityMoveSpeed);
		}

		this.entityHost.getLookControl().setLookAt(this.attackTarget, 30.0F, 55.0F);
		float f;

		if (--this.rangedAttackTime == 0) {
			if (d0 > (double) this.attackRange_2 || !targetInSight) {
				return;
			}

			f = Mth.sqrt((float) d0) / this.attackRange;

			float f1 = f;

			if (f < 0.1F) {
				f1 = 0.1F;
			}

			if (f1 > 1.0F) {
				f1 = 1.0F;
			}

			this.rangedAttackEntityHost.performRangedAttack(this.attackTarget, f1);

			if (maxBurstCount > 0) burstCount--;
			if (burstCount > 0) {
				this.rangedAttackTime = shotDelay;
			} else {
				burstCount = maxBurstCount;
				this.rangedAttackTime = Mth.floor(f * (float) (this.maxRangedAttackTime - this.attackTimeVariance) + (float) this.attackTimeVariance);
			}
		} else if (this.rangedAttackTime < 0) {
			f = Mth.sqrt((float) d0) / this.attackRange;
			this.rangedAttackTime = Mth.floor(f * (float) (this.maxRangedAttackTime - this.attackTimeVariance) + (float) this.attackTimeVariance);
		}
	}
}
