package techguns.world.dungeon.presets;

import java.util.Random;

import techguns.tileentities.TGSpawnerTileEnt;
import techguns.world.dungeon.DungeonSegment;
import techguns.world.dungeon.DungeonTemplate;
import techguns.world.dungeon.IDungeonPath;
import techguns.world.dungeon.MazeDungeonPath;
import techguns.world.dungeon.MazeDungeonPath.PathSegment;
import techguns.world.dungeon.TemplateSegment.SegmentType;

public interface IDungeonPreset {

	/**
	 * 1.20: the 1.12 PRESET_TECHFORTRESS / PRESET_CASTLE / PRESET_NETHER constants are created on first use (they need
	 * the dungeon templates, which need the registered blocks)
	 */
	public static class Presets {
		private static IDungeonPreset techfortress;
		private static IDungeonPreset castle;
		private static IDungeonPreset nether;

		public static synchronized IDungeonPreset techFortress() {
			if (techfortress == null) {
				DungeonTemplate.init();
				techfortress = new PresetTechFortress();
			}
			return techfortress;
		}

		public static synchronized IDungeonPreset castle() {
			if (castle == null) {
				DungeonTemplate.init();
				castle = new PresetCastle();
			}
			return castle;
		}

		public static synchronized IDungeonPreset nether() {
			if (nether == null) {
				DungeonTemplate.init();
				nether = new PresetNetherDungeonUnderground();
			}
			return nether;
		}

		public static IDungeonPreset get(String name) {
			switch (name) {
			case "castle":
				return castle();
			case "nether":
				return nether();
			case "techfortress":
			default:
				return techFortress();
			}
		}
	}

	public DungeonSegment getSegment(SegmentType type, int y, int yMin, int yMax, boolean isSegmentAbove, boolean isSegmentBelow, int seed);

	public int getSizeXZ();

	public int getSizeY();

	//NPC stuff

	/**
	 * NPC spawners per number of dungeon segments.
	 */
	public default float getSpawnDensity() {
		return 0.1f;
	}

	/**
	 * The floor height of the dungeon segments
	 */
	public default int getSpawnYOffset(PathSegment seg) {
		return 1;
	}

	public default IDungeonPath getDungeonPath(int sX, int sY, int sZ, Random rand) {
		return new MazeDungeonPath(sX, sY, sZ, rand);
	}

	public void initDungeonPath(IDungeonPath path);

	public void initSpawner(TGSpawnerTileEnt spawner);
}
