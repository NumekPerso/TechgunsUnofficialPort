package techguns.entities.spawn;

import java.util.ArrayList;

import techguns.Techguns;
import techguns.util.MathUtil;

public class TGNpcSpawnTable {
	protected final int maxDanger;

	protected final ArrayList<ArrayList<TGNpcSpawn>> spawnlist;

	public TGNpcSpawnTable() {
		this(5);
	}

	public TGNpcSpawnTable(int maxDanger) {
		super();
		this.maxDanger = maxDanger;
		spawnlist = new ArrayList<>();
		for (int i = 0; i <= maxDanger; i++) {
			spawnlist.add(new ArrayList<TGNpcSpawn>());
		}
	}

	public ArrayList<TGNpcSpawn> get(int dangerlevel) {
		dangerlevel = MathUtil.clamp(dangerlevel, 0, maxDanger);
		return spawnlist.get(dangerlevel);
	}

	/**
	 * Spawns with weight &lt;= 0 never spawn (1.12: not registered; the weight is checked when spawning so the config can change)
	 */
	public void registerSpawn(TGNpcSpawn spawn, int danger) {
		if (danger < 0 || danger > maxDanger) {
			Techguns.LOGGER.warn("Tried to register NPC spawn for dangerlevel outside of [0-" + maxDanger + "]: " + danger);
			return;
		}
		ArrayList<TGNpcSpawn> list = spawnlist.get(danger);
		list.add(spawn);
	}

	public void clear() {
		this.spawnlist.forEach(ArrayList::clear);
	}
}
