package techguns.entities.ai;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import techguns.api.npc.factions.ITGNpcTeam;
import techguns.entities.npcs.NPCTurret;
import techguns.factions.TGNpcFactions;

/**
 * Retaliation target goal that respects turret owners and NPC factions
 */
public class EntityAIHurtByTargetTGFactions extends HurtByTargetGoal {

	public EntityAIHurtByTargetTGFactions(PathfinderMob ent, boolean callsForHelp) {
		super(ent);
		if (callsForHelp) {
			this.setAlertOthers();
		}
	}

	@Override
	protected boolean canAttack(@Nullable LivingEntity elb, TargetingConditions conditions) {

		if (this.mob instanceof NPCTurret && elb instanceof Player) {
			NPCTurret turret = (NPCTurret) this.mob;
			if (turret.mountedTileEnt != null) {
				UUID owner = turret.mountedTileEnt.getOwner();
				if (owner != null) {
					return TGNpcFactions.isHostile(owner, ((Player) elb).getGameProfile().getId()) && super.canAttack(elb, conditions);
				}
			}
		}
		if (this.mob instanceof ITGNpcTeam && elb instanceof ITGNpcTeam) {
			ITGNpcTeam owner = (ITGNpcTeam) this.mob;
			ITGNpcTeam target = (ITGNpcTeam) elb;

			return TGNpcFactions.isHostile(owner.getTGFaction(), target.getTGFaction());

		}

		return super.canAttack(elb, conditions);
	}

}
