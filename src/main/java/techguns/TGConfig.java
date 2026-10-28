package techguns;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Config values are mirrored into static fields so the ported code can keep reading TGConfig.xyz directly.
 */
public class TGConfig {

	public static boolean debug = false;
	public static boolean limitUnsafeModeToOP = false;
	public static boolean disableAutofeeder = false;
	public static boolean machinesNeedNoPower = false;
	// lot E: upgrade bench
	public static int upgrade_xp_cost = 20;

	public static float damagePvP = 0.5f;
	public static float damageTurretToPlayer = 0.5f;
	public static float damageFactorNPC = 1.0f;

	public static boolean cl_enableDeathFX = true;
	public static boolean cl_enableDeathFX_Gore = true;
	public static boolean cl_lockSpeedFov = true;
	public static float cl_fixedSprintFov = 1.15f;
	public static float cl_effectsQuality = 1.0f;

	// lot I: radiation, disabled by default like in 1.12
	public static boolean WIP_disableRadiationSystem = true;

	// worldgen (lot B: ores)
	public static boolean doOreGenCopper = true;
	public static boolean doOreGenTin = true;
	public static boolean doOreGenLead = true;
	public static boolean doOreGenUranium = true;
	public static boolean doOreGenTitanium = true;

	// lot G: explosive charges, ore drills, ore cluster values (1.12 defaults)
	public static float explosiveChargeMaxBlockHardness = 30.0f;
	public static float explosiveChargeAdvancedMaxBlockHardness = 100.0f;
	public static float oreDrillMultiplierOres = 1.0f;
	public static float oreDrillMultiplierPower = 1.0f;
	public static float oreDrillMultiplierFuel = 1000f;
	public static float oreDrillFuelValueFuel = 100f;
	public static int mininglevel_coal = 0;
	public static int mininglevel_common_metal = 0;
	public static int mininglevel_rare_metal = 1;
	public static int mininglevel_shiny_metal = 2;
	public static int mininglevel_uranium = 3;
	public static int mininglevel_common_gem = 1;
	public static int mininglevel_shiny_gem = 3;
	public static int mininglevel_nether_crystal = 2;
	public static int mininglevel_oil = 2;
	public static double oremult_coal = 10;
	public static double oremult_common_metal = 5;
	public static double oremult_rare_metal = 2.5;
	public static double oremult_shiny_metal = 1;
	public static double oremult_uranium = 0.5;
	public static double oremult_common_gem = 5;
	public static double oremult_shiny_gem = 0.2;
	public static double oremult_nether_crystal = 4;
	public static double oremult_oil = 4;
	public static double powermult_coal = 0.1;
	public static double powermult_common_metal = 0.2;
	public static double powermult_rare_metal = 0.4;
	public static double powermult_shiny_metal = 1.0;
	public static double powermult_uranium = 1.0;
	public static double powermult_common_gem = 0.2;
	public static double powermult_shiny_gem = 1.0;
	public static double powermult_nether_crystal = 0.5;
	public static double powermult_oil = 1.0;
	private static ForgeConfigSpec.DoubleValue EXPLOSIVE_CHARGE_HARDNESS;
	private static ForgeConfigSpec.DoubleValue EXPLOSIVE_CHARGE_ADV_HARDNESS;
	private static ForgeConfigSpec.DoubleValue ORE_DRILL_MULT_ORES;
	private static ForgeConfigSpec.DoubleValue ORE_DRILL_MULT_POWER;
	private static ForgeConfigSpec.DoubleValue ORE_DRILL_MULT_FUEL;
	private static ForgeConfigSpec.DoubleValue ORE_DRILL_FUEL_VALUE;
	private static final String[] CLUSTER_NAMES = { "coal", "common_metal", "rare_metal", "shiny_metal", "uranium", "common_gem", "shiny_gem", "nether_crystal", "oil" };
	private static final ForgeConfigSpec.IntValue[] CLUSTER_MININGLEVEL = new ForgeConfigSpec.IntValue[CLUSTER_NAMES.length];
	private static final ForgeConfigSpec.DoubleValue[] CLUSTER_OREMULT = new ForgeConfigSpec.DoubleValue[CLUSTER_NAMES.length];
	private static final ForgeConfigSpec.DoubleValue[] CLUSTER_POWERMULT = new ForgeConfigSpec.DoubleValue[CLUSTER_NAMES.length];

	private static void buildLotG(ForgeConfigSpec.Builder b) {
		b.push("explosive_charges");
		EXPLOSIVE_CHARGE_HARDNESS = b.comment("Highest blockHardness normal explosive charges can break, obsidian is 50.0)").defineInRange("ExplosiveChargeMaxHardness", 30.0, 0.0, Float.MAX_VALUE);
		EXPLOSIVE_CHARGE_ADV_HARDNESS = b.comment("Highest blockHardness advanced explosive charges can break, obsidian is 50.0)").defineInRange("ExplosiveChargeAdvancedMaxHardness", 100.0, 0.0, Float.MAX_VALUE);
		b.pop();
		b.push("ore_drills");
		ORE_DRILL_MULT_ORES = b.comment("Multiplier to default rate on how many ores an ore drill produces").defineInRange("oreDrillMultiplierOre", 1.0, 0.001, 1000.0);
		ORE_DRILL_MULT_POWER = b.comment("Multiplier to default rate on how much power an ore drill requires").defineInRange("oreDrillMultiplierPower", 1.0, 0.0, 1000.0);
		ORE_DRILL_MULT_FUEL = b.comment("Multiplier to calculate value of furnace fuel burn time = RF for ore Drill. burnTime* <THIS_VALUE> = RF. Only for internal use of the ore Drill, no real RF generation.").defineInRange("oreDrillFuelMultiplier", 1000.0, 1.0, 100000.0);
		ORE_DRILL_FUEL_VALUE = b.comment("Fuel value for Liquid Fuel (fluid tag techguns:fuel) for use in ore Drills, this is per Millibucket, not Bucket, so 1/1000 of bucket value").defineInRange("oreDrillFuelValueFuel", 100.0, 1.0, 100000.0);
		int[] ml = { mininglevel_coal, mininglevel_common_metal, mininglevel_rare_metal, mininglevel_shiny_metal, mininglevel_uranium, mininglevel_common_gem, mininglevel_shiny_gem, mininglevel_nether_crystal, mininglevel_oil };
		double[] om = { oremult_coal, oremult_common_metal, oremult_rare_metal, oremult_shiny_metal, oremult_uranium, oremult_common_gem, oremult_shiny_gem, oremult_nether_crystal, oremult_oil };
		double[] pm = { powermult_coal, powermult_common_metal, powermult_rare_metal, powermult_shiny_metal, powermult_uranium, powermult_common_gem, powermult_shiny_gem, powermult_nether_crystal, powermult_oil };
		for (int i = 0; i < CLUSTER_NAMES.length; i++) {
			String n = CLUSTER_NAMES[i];
			CLUSTER_MININGLEVEL[i] = b.comment("Mining Level for " + n.replace('_', ' ') + " ore clusters").defineInRange("cluster_mininglevel_" + n, ml[i], 0, 10);
			CLUSTER_OREMULT[i] = b.comment("Ore Multiplier for " + n.replace('_', ' ') + " ore clusters").defineInRange("cluster_oremult_" + n, om[i], 0.0001, 1000.0);
			CLUSTER_POWERMULT[i] = b.comment("Power Multiplier for " + n.replace('_', ' ') + " ore clusters").defineInRange("cluster_powermult_" + n, pm[i], 0.0001, 1000.0);
		}
		b.pop();
	}

	private static void loadLotG() {
		explosiveChargeMaxBlockHardness = EXPLOSIVE_CHARGE_HARDNESS.get().floatValue();
		explosiveChargeAdvancedMaxBlockHardness = EXPLOSIVE_CHARGE_ADV_HARDNESS.get().floatValue();
		oreDrillMultiplierOres = ORE_DRILL_MULT_ORES.get().floatValue();
		oreDrillMultiplierPower = ORE_DRILL_MULT_POWER.get().floatValue();
		oreDrillMultiplierFuel = ORE_DRILL_MULT_FUEL.get().floatValue();
		oreDrillFuelValueFuel = ORE_DRILL_FUEL_VALUE.get().floatValue();
		mininglevel_coal = CLUSTER_MININGLEVEL[0].get();
		mininglevel_common_metal = CLUSTER_MININGLEVEL[1].get();
		mininglevel_rare_metal = CLUSTER_MININGLEVEL[2].get();
		mininglevel_shiny_metal = CLUSTER_MININGLEVEL[3].get();
		mininglevel_uranium = CLUSTER_MININGLEVEL[4].get();
		mininglevel_common_gem = CLUSTER_MININGLEVEL[5].get();
		mininglevel_shiny_gem = CLUSTER_MININGLEVEL[6].get();
		mininglevel_nether_crystal = CLUSTER_MININGLEVEL[7].get();
		mininglevel_oil = CLUSTER_MININGLEVEL[8].get();
		oremult_coal = CLUSTER_OREMULT[0].get();
		oremult_common_metal = CLUSTER_OREMULT[1].get();
		oremult_rare_metal = CLUSTER_OREMULT[2].get();
		oremult_shiny_metal = CLUSTER_OREMULT[3].get();
		oremult_uranium = CLUSTER_OREMULT[4].get();
		oremult_common_gem = CLUSTER_OREMULT[5].get();
		oremult_shiny_gem = CLUSTER_OREMULT[6].get();
		oremult_nether_crystal = CLUSTER_OREMULT[7].get();
		oremult_oil = CLUSTER_OREMULT[8].get();
		powermult_coal = CLUSTER_POWERMULT[0].get();
		powermult_common_metal = CLUSTER_POWERMULT[1].get();
		powermult_rare_metal = CLUSTER_POWERMULT[2].get();
		powermult_shiny_metal = CLUSTER_POWERMULT[3].get();
		powermult_uranium = CLUSTER_POWERMULT[4].get();
		powermult_common_gem = CLUSTER_POWERMULT[5].get();
		powermult_shiny_gem = CLUSTER_POWERMULT[6].get();
		powermult_nether_crystal = CLUSTER_POWERMULT[7].get();
		powermult_oil = CLUSTER_POWERMULT[8].get();
		TGOreClusters.orecluster.reloadConfig();
	}
	// end lot G

	// lot K: structure generation (1.12 World Generation category)
	public static boolean doWorldspawn = true;
	public static int spawnWeightTGStructureBig = 64;
	public static int spawnWeightTGStructureSmall = 16;
	public static int spawnWeightTGStructureMedium = 32;
	public static boolean spawnOreClusterStructures = true;
	private static ForgeConfigSpec.BooleanValue DO_WORLDSPAWN;
	private static ForgeConfigSpec.IntValue SPAWN_WEIGHT_BIG;
	private static ForgeConfigSpec.IntValue SPAWN_WEIGHT_SMALL;
	private static ForgeConfigSpec.IntValue SPAWN_WEIGHT_MEDIUM;
	private static ForgeConfigSpec.BooleanValue SPAWN_ORECLUSTER_STRUCTURES;

	private static void buildLotK(ForgeConfigSpec.Builder b) {
		b.push("structures");
		DO_WORLDSPAWN = b.comment("Should Structures (military bases) be spawned in the world?").define("SpawnStructures", true);
		SPAWN_WEIGHT_BIG = b.comment("Every X chunks it's tried to spawn a Big building (one try in each X*X chunks region). Changes need a restart.").defineInRange("StructureSpawnWeightBig", 64, 16, 100000);
		SPAWN_WEIGHT_SMALL = b.comment("Every X chunks it's tried to spawn a Small building (one try in each X*X chunks region). Changes need a restart.").defineInRange("StructureSpawnWeightSmall", 16, 4, 100000);
		SPAWN_WEIGHT_MEDIUM = b.comment("Every X chunks it's tried to spawn a Medium building (one try in each X*X chunks region). Changes need a restart.").defineInRange("StructureSpawnWeightMedium", 32, 8, 100000);
		SPAWN_ORECLUSTER_STRUCTURES = b.comment("When worldgen is enabled, include structure spawns that contain ore clusters.").define("SpawnOreClusterStructures", true);
		b.pop();
	}

	private static void loadLotK() {
		doWorldspawn = DO_WORLDSPAWN.get();
		spawnWeightTGStructureBig = SPAWN_WEIGHT_BIG.get();
		spawnWeightTGStructureSmall = SPAWN_WEIGHT_SMALL.get();
		spawnWeightTGStructureMedium = SPAWN_WEIGHT_MEDIUM.get();
		spawnOreClusterStructures = SPAWN_ORECLUSTER_STRUCTURES.get();
	}
	// end lot K
	// lot J: NPC spawning (1.12 "NPC Spawn" category, same defaults). The overall spawn weights of the 1.12 config
	// ("Techguns Spawnweight Overworld/Nether", 600/300) are the weights of the biome modifiers data/techguns/forge/biome_modifier/npc_*.json
	public static int distanceSpawnLevel0 = 500;
	public static int distanceSpawnLevel1 = 1000;
	public static int distanceSpawnLevel2 = 2500;
	public static int spawnWeightZombieSoldier = 100;
	public static int spawnWeightZombieFarmer = 200;
	public static int spawnWeightZombieMiner = 200;
	public static int spawnWeightZombiePigmanSoldier = 100;
	public static int spawnWeightCyberDemon = 30;
	public static int spawnWeightSkeletonSoldier = 100;
	public static int spawnWeightBandit = 50;
	public static int spawnWeightPsychoSteve = 3;
	public static java.util.List<? extends String> biomeBlacklist = java.util.List.of();
	private static ForgeConfigSpec.IntValue[] NPC_SPAWN_VALUES;
	private static ForgeConfigSpec.ConfigValue<java.util.List<? extends String>> BIOME_BLACKLIST;

	private static void buildLotJ(ForgeConfigSpec.Builder b) {
		b.push("npc_spawn");
		NPC_SPAWN_VALUES = new ForgeConfigSpec.IntValue[] {
				b.comment("Up to which distance to worldspawn only mobs with danger level up to 0 will spawn").defineInRange("DistanceSpawnLevel0", 500, 0, Integer.MAX_VALUE),
				b.comment("Up to which distance to worldspawn only mobs with danger level up to 1 will spawn").defineInRange("DistanceSpawnLevel1", 1000, 0, Integer.MAX_VALUE),
				b.comment("Up to which distance to worldspawn only mobs with danger level up to 2 will spawn").defineInRange("DistanceSpawnLevel2", 2500, 0, Integer.MAX_VALUE),
				b.comment("Spawn weight for spawning Zombie Soldiers, at 0 spawn will not be registered").defineInRange("SpawnWeightZombieSoldier", 100, 0, 10000),
				b.comment("Spawn weight for spawning Zombie Farmers, at 0 spawn will not be registered").defineInRange("SpawnWeightZombieFarmer", 200, 0, 10000),
				b.comment("Spawn weight for spawning Zombie Miners, at 0 spawn will not be registered").defineInRange("SpawnWeightZombieMiner", 200, 0, 10000),
				b.comment("Spawn weight for spawning Zombie Pigman Soldiers (Nether only), at 0 spawn will not be registered").defineInRange("SpawnWeightZombiePigmanSoldier", 100, 0, 10000),
				b.comment("Spawn weight for spawning Cyber Demons (Nether only), at 0 spawn will not be registered").defineInRange("SpawnWeightCyberDemon", 30, 0, 10000),
				b.comment("Spawn weight for spawning Skeleton Soldiers, at 0 spawn will not be registered").defineInRange("SpawnWeightSkeletonSoldier", 100, 0, 10000),
				b.comment("Spawn weight for spawning Bandit groups, at 0 spawn will not be registered").defineInRange("SpawnWeightBandit", 50, 0, 10000),
				b.comment("Spawn weight for spawning Psycho Steve, early game boss, don't set to high value, at 0 spawn will not be registered").defineInRange("SpawnWeightPsychoSteve", 3, 0, 10000) };
		BIOME_BLACKLIST = b.comment("Biome Registry names (e.g: minecraft:mushroom_fields) that are excluded from Techguns monster spawning").defineListAllowEmpty(java.util.List.of("BiomeBlacklist"), java.util.List::of, o -> o instanceof String);
		b.pop();
	}

	private static void loadLotJ() {
		distanceSpawnLevel0 = NPC_SPAWN_VALUES[0].get();
		distanceSpawnLevel1 = NPC_SPAWN_VALUES[1].get();
		distanceSpawnLevel2 = NPC_SPAWN_VALUES[2].get();
		spawnWeightZombieSoldier = NPC_SPAWN_VALUES[3].get();
		spawnWeightZombieFarmer = NPC_SPAWN_VALUES[4].get();
		spawnWeightZombieMiner = NPC_SPAWN_VALUES[5].get();
		spawnWeightZombiePigmanSoldier = NPC_SPAWN_VALUES[6].get();
		spawnWeightCyberDemon = NPC_SPAWN_VALUES[7].get();
		spawnWeightSkeletonSoldier = NPC_SPAWN_VALUES[8].get();
		spawnWeightBandit = NPC_SPAWN_VALUES[9].get();
		spawnWeightPsychoSteve = NPC_SPAWN_VALUES[10].get();
		biomeBlacklist = BIOME_BLACKLIST.get();
	}
	// end lot J

	private static final ForgeConfigSpec COMMON_SPEC;
	private static final ForgeConfigSpec CLIENT_SPEC;

	private static final ForgeConfigSpec.BooleanValue DEBUG;
	private static final ForgeConfigSpec.BooleanValue LIMIT_UNSAFE_MODE;
	private static final ForgeConfigSpec.BooleanValue DISABLE_AUTOFEEDER;
	private static final ForgeConfigSpec.BooleanValue MACHINES_NEED_NO_POWER;
	private static final ForgeConfigSpec.IntValue UPGRADE_XP_COST;
	private static final ForgeConfigSpec.DoubleValue DAMAGE_PVP;
	private static final ForgeConfigSpec.DoubleValue DAMAGE_TURRET;
	private static final ForgeConfigSpec.DoubleValue DAMAGE_NPC;

	private static final ForgeConfigSpec.BooleanValue DO_ORE_GEN_COPPER;
	private static final ForgeConfigSpec.BooleanValue DO_ORE_GEN_TIN;
	private static final ForgeConfigSpec.BooleanValue DO_ORE_GEN_LEAD;
	private static final ForgeConfigSpec.BooleanValue DO_ORE_GEN_URANIUM;
	private static final ForgeConfigSpec.BooleanValue DO_ORE_GEN_TITANIUM;

	private static final ForgeConfigSpec.BooleanValue DISABLE_RADIATION;

	private static final ForgeConfigSpec.BooleanValue DEATH_FX;
	private static final ForgeConfigSpec.BooleanValue DEATH_FX_GORE;
	private static final ForgeConfigSpec.BooleanValue LOCK_SPEED_FOV;
	private static final ForgeConfigSpec.DoubleValue SPRINT_FOV;
	private static final ForgeConfigSpec.DoubleValue EFFECTS_QUALITY;

	static {
		ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
		b.push("general");
		DEBUG = b.comment("Enable debug options and unfinished stuff, disable this for playing.").define("debug", false);
		LIMIT_UNSAFE_MODE = b.comment("Only opped players can use the unsafe mode of guns.").define("RestrictUnsafeModeToOP", false);
		DISABLE_AUTOFEEDER = b.comment("Disable automatic feeding of Food in the Techguns tab. Disable autofeeding if you think it breaks the balance").define("disableAutofeeder", false);
		MACHINES_NEED_NO_POWER = b.comment("Machines don't need power, activate this if you don't want to install a mod with generators and still be able to use the machines").define("machinesNeedNoPower", false);
		UPGRADE_XP_COST = b.comment("Base XP value for Upgrade Bench recipes (enchants)").defineInRange("UpgradeXPCost", 20, 0, 10000);
		//1.12 key and default: configs of 1.0.0 had "disableRadiationSystem" = false, the new key gives them the new default
		DISABLE_RADIATION = b.comment("Disable the radiation system (radiation sickness, radioactive items, RadAway/Rad pills still usable). Set to false to enable radiation.").define("WIP_disableRadiationSystem", true);
		b.pop();
		b.push("damage_factors");
		DAMAGE_PVP = b.comment("Damage factor Techguns weapons deal when fired from players against other players, is zero when PvP is disabled").defineInRange("DamagePvP", 0.5, 0.0, 100.0);
		DAMAGE_TURRET = b.comment("Damage factor Techguns Turrets deal when hitting players").defineInRange("DamageTurretToPlayer", 0.5, 0.0, 100.0);
		DAMAGE_NPC = b.comment("Damage factor for all NPCs").defineInRange("DamageFactorNPC", 1.0, 0.0, 100.0);
		b.pop();
		b.push("worldgen");
		DO_ORE_GEN_COPPER = b.comment("Generate Copper Ore, disable if other mod does").define("doOreGenCopper", true);
		DO_ORE_GEN_TIN = b.comment("Generate Tin Ore, disable if other mod does").define("doOreGenTin", true);
		DO_ORE_GEN_LEAD = b.comment("Generate Lead Ore, disable if other mod does").define("doOreGenLead", true);
		DO_ORE_GEN_TITANIUM = b.comment("Generate Titanium, not generated by most mods mods, leave it on in most cases").define("doOreGenTitanium", true);
		DO_ORE_GEN_URANIUM = b.comment("Generate Uranium, disable if other mod already adds it and you want only 1 type. Tag: forge:ores/uranium").define("doOreGenUranium", true);
		b.pop();
		buildLotG(b);
		buildLotK(b);
		buildLotJ(b);
		COMMON_SPEC = b.build();

		ForgeConfigSpec.Builder c = new ForgeConfigSpec.Builder();
		c.push("clientside");
		DEATH_FX = c.comment("Enable Death Effects, pure clientside check.").define("EnableDeathEffects", true);
		DEATH_FX_GORE = c.comment("Enable the gore Death Effect, requires DeathEffects to be enabled, pure clientside check.").translation("techguns.config.enablegoredeatheffect").define("EnableGoreDeathEffect", true);
		LOCK_SPEED_FOV = c.comment("Counters the speed dependant FOV change. This also stops FOV changes while sprinting.").define("LockSpeedDependantFov", true);
		SPRINT_FOV = c.comment("Multiply the FOV while sprinting by this value independent from the actual speed, has no effect when LockSpeedDependantFov is false.").defineInRange("FixedSprintFovMultiplier", 1.15, 1.0, 10.0);
		EFFECTS_QUALITY = c.comment("Effects quality: multiplier of the number of Techguns particles (smoke, fire, sparks, impacts...), e.g. 0.25, 0.5 or 1.0. 0 disables them. Trails, muzzle flashes, beams and gibs are not affected. Pure clientside setting.")
				.translation("techguns.config.effectsquality").defineInRange("EffectsQuality", 1.0, 0.0, 1.0);
		c.pop();
		CLIENT_SPEC = c.build();
	}

	public static void register() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
	}

	public static void onConfigLoad(ModConfigEvent event) {
		Object spec = event.getConfig().getSpec();
		if (spec == COMMON_SPEC) {
			debug = DEBUG.get();
			limitUnsafeModeToOP = LIMIT_UNSAFE_MODE.get();
			disableAutofeeder = DISABLE_AUTOFEEDER.get();
			machinesNeedNoPower = MACHINES_NEED_NO_POWER.get();
			upgrade_xp_cost = UPGRADE_XP_COST.get();
			damagePvP = DAMAGE_PVP.get().floatValue();
			damageTurretToPlayer = DAMAGE_TURRET.get().floatValue();
			damageFactorNPC = DAMAGE_NPC.get().floatValue();
			doOreGenCopper = DO_ORE_GEN_COPPER.get();
			doOreGenTin = DO_ORE_GEN_TIN.get();
			doOreGenLead = DO_ORE_GEN_LEAD.get();
			doOreGenUranium = DO_ORE_GEN_URANIUM.get();
			doOreGenTitanium = DO_ORE_GEN_TITANIUM.get();
			WIP_disableRadiationSystem = DISABLE_RADIATION.get();
			loadLotG();
			loadLotK();
			loadLotJ();
		} else if (spec == CLIENT_SPEC) {
			cl_enableDeathFX = DEATH_FX.get();
			cl_enableDeathFX_Gore = DEATH_FX_GORE.get();
			cl_lockSpeedFov = LOCK_SPEED_FOV.get();
			cl_fixedSprintFov = SPRINT_FOV.get().floatValue();
			cl_effectsQuality = EFFECTS_QUALITY.get().floatValue();
		}
	}
}
