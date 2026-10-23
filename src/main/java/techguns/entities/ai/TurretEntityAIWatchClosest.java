package techguns.entities.ai;

import java.util.EnumSet;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntitySelector;

/**
 * Copy of LookAtPlayerGoal (1.12 EntityAIWatchClosest) with a different Y range for targeting
 */
public class TurretEntityAIWatchClosest extends Goal {
	protected Mob entity;
	/** The closest entity which is being watched by this one. */
	protected Entity closestEntity;
	/** This is the Maximum distance that the AI will look for the Entity */
	protected float maxDistanceForPlayer;
	private int lookTime;
	private final float chance;
	protected double yOffset;
	protected Class<? extends LivingEntity> watchedClass;

	public TurretEntityAIWatchClosest(Mob entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, double yOffset) {
		this(entityIn, watchTargetClass, maxDistance, 0.02F, yOffset);
	}

	public TurretEntityAIWatchClosest(Mob entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, float chanceIn, double yOffset) {
		this.entity = entityIn;
		this.watchedClass = watchTargetClass;
		this.maxDistanceForPlayer = maxDistance;
		this.chance = chanceIn;
		this.setFlags(EnumSet.of(Goal.Flag.LOOK));
		this.yOffset = yOffset;
	}

	@Override
	public boolean canUse() {
		if (this.entity.getRandom().nextFloat() >= this.chance) {
			return false;
		} else {
			if (this.entity.getTarget() != null) {
				this.closestEntity = this.entity.getTarget();
			}

			TargetingConditions cond = TargetingConditions.forNonCombat().range(this.maxDistanceForPlayer).selector(e -> EntitySelector.NO_SPECTATORS.test(e) && !e.isPassengerOfSameVehicle(this.entity));
			if (this.watchedClass == Player.class) {
				this.closestEntity = this.entity.level().getNearestPlayer(cond, this.entity, this.entity.getX(), this.entity.getEyeY(), this.entity.getZ());
			} else {
				this.closestEntity = this.entity.level().getNearestEntity(this.entity.level().getEntitiesOfClass(this.watchedClass, this.entity.getBoundingBox().inflate((double) this.maxDistanceForPlayer, this.yOffset, (double) this.maxDistanceForPlayer), e -> true), cond, this.entity, this.entity.getX(), this.entity.getEyeY(), this.entity.getZ());
			}

			return this.closestEntity != null;
		}
	}

	@Override
	public boolean canContinueToUse() {
		if (!this.closestEntity.isAlive()) {
			return false;
		} else if (this.entity.distanceToSqr(this.closestEntity) > (double) (this.maxDistanceForPlayer * this.maxDistanceForPlayer)) {
			return false;
		} else {
			return this.lookTime > 0;
		}
	}

	@Override
	public void start() {
		this.lookTime = this.adjustedTickDelay(40 + this.entity.getRandom().nextInt(40));
	}

	@Override
	public void stop() {
		this.closestEntity = null;
	}

	@Override
	public void tick() {
		if (this.closestEntity != null) {
			this.entity.getLookControl().setLookAt(this.closestEntity.getX(), this.closestEntity.getEyeY(), this.closestEntity.getZ());
		}
		--this.lookTime;
	}
}
