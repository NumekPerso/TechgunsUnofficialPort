package techguns.world.structures;

import java.util.Random;

import techguns.world.dungeon.presets.IDungeonPreset;

/**
 * Not spawned in 1.12 (TODO there), available with /place structure techguns:nether_dungeon
 */
public class NetherDungeonStructure extends DungeonStructure {

	public NetherDungeonStructure() {
		this.heightdiffLimit = 255;
	}

	@Override
	protected IDungeonPreset getPreset() {
		return IDungeonPreset.Presets.nether();
	}

	@Override
	public int getSizeX(Random rnd) {
		return 24 + rnd.nextInt(16);
	}

	@Override
	public int getSizeZ(Random rnd) {
		return 24 + rnd.nextInt(16);
	}

	@Override
	public int getSizeY(Random rnd) {
		return 16 + rnd.nextInt(16);
	}
}
