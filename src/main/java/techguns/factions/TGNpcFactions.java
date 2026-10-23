package techguns.factions;

import java.util.UUID;

import net.minecraft.world.entity.player.Player;
import techguns.api.npc.factions.TGNpcFaction;
import techguns.tileentities.BasicOwnedTileEnt;

/**
 * NPC faction hostility and player team checks. 1.12 used FTBLib teams when installed, the port has no team mod
 * integration (BasicOwnedTileEnt.FTBLIB_ENABLED is false): all other players are enemies of the owner for PvP settings.
 */
public class TGNpcFactions {

	protected static boolean[][] hostilityTable = new boolean[TGNpcFaction.values().length][TGNpcFaction.values().length];

	static {

		//init hostility table
		setHostile(TGNpcFaction.HOSTILE, false);
		setHostile(TGNpcFaction.TURRET, false);
		setHostile(TGNpcFaction.NEUTRAL, false);

		setHostile(TGNpcFaction.HOSTILE, TGNpcFaction.TURRET, true);
		setHostile(TGNpcFaction.HOSTILE, TGNpcFaction.NEUTRAL, true);
		setHostile(TGNpcFaction.TURRET, TGNpcFaction.NEUTRAL, false);

	}

	private static void setHostile(TGNpcFaction f1, TGNpcFaction f2, boolean hostile) {
		hostilityTable[f1.ordinal()][f2.ordinal()] = hostile;
		hostilityTable[f2.ordinal()][f1.ordinal()] = hostile;
	}

	private static void setHostile(TGNpcFaction f1, boolean hostile) {
		hostilityTable[f1.ordinal()][f1.ordinal()] = hostile;
	}

	public static boolean canAccess(UUID owner, UUID target, int security) {
		if (owner.equals(target)) return true;

		switch (security) {
		case 1:
			return isAlliedOrTeamMember(owner, target);
		case 2:
			return isTeamMember(owner, target);
		case 3:
			return owner.equals(target);
		case 0:
		default:
			return false;

		}

	}

	public static boolean shouldAttack(UUID owner, UUID target, int pvpsetting) {
		if (owner.equals(target)) return false;

		switch (pvpsetting) {
		case 1:
			return isEnemy(owner, target);
		case 2:
			return !isAlliedOrTeamMember(owner, target);
		case 3:
			return !isTeamMember(owner, target);
		case 4:
			return !owner.equals(target);
		case 0:
		default:
			return false;

		}

	}

	public static boolean isEnemy(UUID owner, UUID target) {
		return false;
	}

	public static boolean isAlliedOrTeamMember(UUID owner, UUID target) {
		return true;
	}

	public static boolean isAlliedNoTeamMember(UUID owner, UUID target) {
		return true;
	}

	public static boolean isTeamMember(UUID owner, UUID target) {
		return true;
	}

	public static boolean isHostile(Player ply1, Player ply2) {
		return isHostile(ply1.getGameProfile().getId(), ply2.getGameProfile().getId());
	}

	public static boolean isHostile(UUID ply1, UUID ply2) {
		if (BasicOwnedTileEnt.FTBLIB_ENABLED) {
			return false;
		}
		return !ply1.equals(ply2);
	}

	public static boolean isHostile(TGNpcFaction faction1, TGNpcFaction faction2) {
		return hostilityTable[faction1.ordinal()][faction2.ordinal()];
	}
}
