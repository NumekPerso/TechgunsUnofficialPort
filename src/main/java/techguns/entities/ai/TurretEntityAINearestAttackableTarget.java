package techguns.entities.ai;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;

/**
 * Nearest attackable target with a configurable vertical search range
 */
public class TurretEntityAINearestAttackableTarget<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

	protected double yOffset;

	public TurretEntityAINearestAttackableTarget(Mob creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable Predicate<LivingEntity> targetSelector, double yOffset) {
		super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
		this.yOffset = yOffset;
	}

	@Override
	protected AABB getTargetSearchArea(double targetDistance) {
		return this.mob.getBoundingBox().inflate(targetDistance, yOffset, targetDistance);
	}

}
