package techguns.world.structures;

import java.util.Random;

import techguns.world.dungeon.presets.IDungeonPreset;

public class TechFortressStructure extends DungeonStructure {

	public TechFortressStructure() {
		this.heightdiffLimit = 10;
	}

	@Override
	protected IDungeonPreset getPreset() {
		return IDungeonPreset.Presets.techFortress();
	}

	@Override
	public int getSizeX(Random rnd) {
		return 32 + rnd.nextInt(16);
	}

	@Override
	public int getSizeZ(Random rnd) {
		return 32 + rnd.nextInt(16);
	}

	@Override
	public int getSizeY(Random rnd) {
		return 24 + rnd.nextInt(16);
	}
}
