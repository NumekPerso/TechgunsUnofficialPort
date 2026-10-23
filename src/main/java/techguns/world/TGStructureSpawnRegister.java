package techguns.world;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import techguns.TGConfig;
import techguns.TGFluids;
import techguns.world.structures.*;
import techguns.world.structures.WorldgenStructure;

/**
 * The Techguns structures (1.12 TGStructureSpawnRegister). The 1.12 weights/biomes/dimensions are in the datapack
 * (see TGStructureSpawn), this keeps the 1.12 table as documentation and maps the structure names of the datapack to
 * the structure classes.
 */
public class TGStructureSpawnRegister {

	protected static final Map<String, TGStructureSpawn> SPAWNS = new LinkedHashMap<>();

	protected static final BooleanSupplier ALWAYS = () -> true;
	protected static final BooleanSupplier ORECLUSTERS = () -> TGConfig.spawnOreClusterStructures;

	protected static void add(String name, Supplier<WorldgenStructure> factory, int weight, boolean nether, StructureLandType type, StructureSize size, BooleanSupplier enabled) {
		SPAWNS.put(name, new TGStructureSpawn(name, factory, weight, nether, type, size, enabled));
	}

	protected static final BooleanSupplier OIL = () -> TGConfig.spawnOreClusterStructures && TGFluids.OIL_WORLDSPAWN != null;

	static {
		// OVERWORLD, small
		add("factory_house_small", () -> new FactoryHouseSmall(8, 0, 7, 9, 5, 10).setXZSize(11, 10), 10, false, StructureLandType.LAND, StructureSize.SMALL, ALWAYS);
		add("small_trainstation", () -> new SmallTrainstation(0, 0, 0, 0, 0, 0).setXZSize(11, 12), 10, false, StructureLandType.LAND, StructureSize.SMALL, ALWAYS);
		add("small_mine", () -> new SmallMine().setXZSize(17, 11), 10, false, StructureLandType.LAND, StructureSize.SMALL, ALWAYS);
		add("gas_station", GasStation::new, 10, false, StructureLandType.LAND, StructureSize.SMALL, ALWAYS);

		// OVERWORLD, medium
		add("alien_bug_nest", () -> new AlienBugNestStructure().setXZSize(4, 4), 20, false, StructureLandType.LAND, StructureSize.MEDIUM, ALWAYS);
		add("police_station", PoliceStation::new, 10, false, StructureLandType.LAND, StructureSize.MEDIUM, ALWAYS);
		add("survivor_hideout", SurvivorHideout::new, 10, false, StructureLandType.LAND, StructureSize.MEDIUM, ALWAYS);
		add("orecluster_spike", () -> new OreClusterSpike().setXZSize(8, 8), 10, false, StructureLandType.LAND, StructureSize.MEDIUM, ORECLUSTERS);
		add("orecluster_meteor_basis", () -> new OreClusterMeteorBasis().setXZSize(17, 17), 5, false, StructureLandType.LAND, StructureSize.MEDIUM, ORECLUSTERS);
		add("desert_oil_cluster", () -> new DesertOilCluster().setXZSize(11, 11), 15, false, StructureLandType.LAND, StructureSize.MEDIUM, OIL);

		// OVERWORLD, big
		add("military_base", () -> new MilitaryBaseStructure(0, 0, 0, 0, 0, 0), 1, false, StructureLandType.LAND, StructureSize.BIG, ALWAYS);
		add("castle", CastleStructure::new, 1, false, StructureLandType.LAND, StructureSize.BIG, ALWAYS);
		add("aircraft_carrier", () -> new AircraftCarrier(54, 24, 21, 54, 24, 21).setXZSize(54, 21), 1, false, StructureLandType.WATER, StructureSize.BIG, ALWAYS);
		// not spawned in 1.12 (only /place): tech fortress (1.12 city: unfinished and not spawned, not ported)
		add("tech_fortress", TechFortressStructure::new, 0, false, StructureLandType.LAND, StructureSize.BIG, ALWAYS);

		// NETHER, small
		add("nether_altar_small", () -> new NetherAltarSmall().setXZSize(11, 11), 10, true, StructureLandType.LAND, StructureSize.SMALL, ALWAYS);
		add("nether_soul_platform", () -> new NetherSoulPlatform().setXZSize(11, 11), 10, true, StructureLandType.LAND, StructureSize.SMALL, ALWAYS);
		add("nether_loot_01", () -> new NetherLoot01().setXZSize(6, 6), 10, true, StructureLandType.LAND, StructureSize.SMALL, ALWAYS);
		add("nether_acid_hole", () -> new NetherAcidHole().setXZSize(9, 9), 10, true, StructureLandType.LAND, StructureSize.SMALL, ALWAYS);
		add("nether_orecluster_small", () -> new NetherOreClusterSmall().setXZSize(3, 3), 10, true, StructureLandType.LAND, StructureSize.SMALL, ORECLUSTERS);

		// NETHER, medium
		add("nether_altar_medium", () -> new NetherAltarMedium().setXZSize(16, 16), 10, true, StructureLandType.LAND, StructureSize.MEDIUM, ALWAYS);
		add("nether_ghast_spawner", () -> new NetherGhastSpawner().setXZSize(10, 10), 10, true, StructureLandType.LAND, StructureSize.MEDIUM, ALWAYS);
		add("nether_orecluster_castle", () -> new NetherOreClusterCastle().setXZSize(11, 11), 1000, true, StructureLandType.LAND, StructureSize.MEDIUM, ORECLUSTERS);
		// 1.12: TODO, not spawned (only /place)
		add("nether_dungeon", NetherDungeonStructure::new, 0, true, StructureLandType.LAND, StructureSize.BIG, ALWAYS);
	}

	static {
		// 1.12: only with an oil fluid of another mod (TGFluids.OIL_WORLDSPAWN), not in the structure set
		SPAWNS.get("desert_oil_cluster").inSet = false;
	}

	/**
	 * The structures of a structure set, in the order of the datapack set
	 */
	public static java.util.List<TGStructureSpawn> getSet(StructureSize size) {
		java.util.List<TGStructureSpawn> list = new java.util.ArrayList<>();
		for (TGStructureSpawn s : SPAWNS.values()) {
			if (s.size == size && s.inSet) {
				list.add(s);
			}
		}
		return list;
	}

	public static TGStructureSpawn get(String name) {
		return SPAWNS.get(name);
	}

	public static Map<String, TGStructureSpawn> getSpawns() {
		return SPAWNS;
	}
}
