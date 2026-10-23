package techguns.entities.ai;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;

/**
 * Port of the 1.12 vanilla EntityAIFindEntityNearestPlayer (removed in 1.14): target goal for mobs without path finding
 * (flying mobs), targets the nearest visible player in follow range (+-4 blocks vertical search range).
 */
public class EntityAIFindEntityNearestPlayer extends Goal {
	private final Mob entityLiving;
	private final Predicate<Player> predicate;
	private LivingEntity entityTarget;

	public EntityAIFindEntityNearestPlayer(Mob entityLivingIn) {
		this.entityLiving = entityLivingIn;
		this.setFlags(java.util.EnumSet.of(Goal.Flag.TARGET));
		this.predicate = (Player p) -> {
			if (p.getAbilities().invulnerable) {
				return false;
			} else {
				double d0 = EntityAIFindEntityNearestPlayer.this.maxTargetRange();

				if (p.isShiftKeyDown()) {
					d0 *= 0.800000011920929D;
				}

				if (p.isInvisible()) {
					float f = p.getArmorCoverPercentage();

					if (f < 0.1F) {
						f = 0.1F;
					}

					d0 *= (double) (0.7F * f);
				}

				return (double) p.distanceTo(EntityAIFindEntityNearestPlayer.this.entityLiving) > d0 ? false : isSuitableTarget(EntityAIFindEntityNearestPlayer.this.entityLiving, p);
			}
		};
	}

	/**
	 * 1.12 EntityAITarget.isSuitableTarget(attacker, target, false, true)
	 */
	protected static boolean isSuitableTarget(Mob attacker, LivingEntity target) {
		if (target == null || target == attacker || !target.isAlive() || !attacker.canAttackType(target.getType())) {
			return false;
		} else if (attacker.isAlliedTo(target)) {
			return false;
		} else if (target instanceof Player && (((Player) target).isCreative() || ((Player) target).isSpectator())) {
			return false;
		} else {
			return attacker.getSensing().hasLineOfSight(target);
		}
	}

	@Override
	public boolean canUse() {
		double d0 = this.maxTargetRange();
		List<Player> list = this.entityLiving.level().getEntitiesOfClass(Player.class, this.entityLiving.getBoundingBox().inflate(d0, 4.0D, d0), this.predicate);
		list.sort(Comparator.comparingDouble(p -> p.distanceToSqr(this.entityLiving)));

		if (list.isEmpty()) {
			return false;
		} else {
			this.entityTarget = list.get(0);
			return true;
		}
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity entitylivingbase = this.entityLiving.getTarget();

		if (entitylivingbase == null) {
			return false;
		} else if (!entitylivingbase.isAlive()) {
			return false;
		} else if (entitylivingbase instanceof Player && ((Player) entitylivingbase).getAbilities().invulnerable) {
			return false;
		} else {
			Team team = this.entityLiving.getTeam();
			Team team1 = entitylivingbase.getTeam();

			if (team != null && team1 == team) {
				return false;
			} else {
				double d0 = this.maxTargetRange();

				if (this.entityLiving.distanceToSqr(entitylivingbase) > d0 * d0) {
					return false;
				} else {
					return !(entitylivingbase instanceof Player) || !((Player) entitylivingbase).isCreative();
				}
			}
		}
	}

	@Override
	public void start() {
		this.entityLiving.setTarget(this.entityTarget);
		super.start();
	}

	@Override
	public void stop() {
		this.entityLiving.setTarget(null);
		super.stop();
	}

	protected double maxTargetRange() {
		AttributeInstance iattributeinstance = this.entityLiving.getAttribute(Attributes.FOLLOW_RANGE);
		return iattributeinstance == null ? 16.0D : iattributeinstance.getValue();
	}
}
