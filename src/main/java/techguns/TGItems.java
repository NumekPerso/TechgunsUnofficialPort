package techguns;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegisterEvent;
import techguns.items.GenericItem;
import techguns.items.TGAmmoItem;

/**
 * Ammo items of the port. In 1.12 these were metadata variants of one shared item, now each is its own item with the
 * same name as its old variant. The fields keep the 1.12 names but are Items: use TGItems.newStack(item, count).
 */
public class TGItems {

	/**
	 * Every item of the mod is added here on construction and registered in TGRegistry
	 */
	public static final List<Item> ITEMLIST = new ArrayList<>();

	public static Item STONE_BULLETS;
	public static Item PISTOL_ROUNDS;
	public static Item SHOTGUN_ROUNDS;
	public static Item RIFLE_ROUNDS;
	public static Item SNIPER_ROUNDS;
	public static Item GRENADE_40MM;
	public static Item ADVANCED_ROUNDS;
	public static Item ROCKET;
	public static Item RIFLE_ROUNDS_STACK;

	public static Item SMG_MAGAZINE;
	public static Item SMG_MAGAZINE_EMPTY;
	public static Item PISTOL_MAGAZINE;
	public static Item PISTOL_MAGAZINE_EMPTY;
	public static Item ASSAULTRIFLE_MAGAZINE;
	public static Item ASSAULTRIFLE_MAGAZINE_EMPTY;
	public static Item LMG_MAGAZINE;
	public static Item LMG_MAGAZINE_EMPTY;
	public static Item MINIGUN_DRUM;
	public static Item MINIGUN_DRUM_EMPTY;
	public static Item AS50_MAGAZINE;
	public static Item AS50_MAGAZINE_EMPTY;
	public static Item ADVANCED_MAGAZINE;
	public static Item ADVANCED_MAGAZINE_EMPTY;

	public static Item COMPRESSED_AIR_TANK;
	public static Item COMPRESSED_AIR_TANK_EMPTY;
	public static Item BIO_TANK;
	public static Item BIO_TANK_EMPTY;
	public static Item FUEL_TANK;
	public static Item FUEL_TANK_EMPTY;
	public static Item ENERGY_CELL;
	public static Item ENERGY_CELL_EMPTY;
	public static Item NUCLEAR_POWERCELL;
	public static Item NUCLEAR_POWERCELL_EMPTY;

	public static Item NETHER_CHARGE;
	public static Item REDSTONE_BATTERY;
	public static Item REDSTONE_BATTERY_EMPTY;
	public static Item GAUSSRIFLE_SLUGS;

	public static Item SHOTGUN_ROUNDS_INCENDIARY;
	public static Item AS50_MAGAZINE_INCENDIARY;
	public static Item SNIPER_ROUNDS_INCENDIARY;
	public static Item PISTOL_ROUNDS_INCENDIARY;
	public static Item RIFLE_ROUNDS_INCENDIARY;
	public static Item RIFLE_ROUNDS_STACK_INCENDIARY;
	public static Item PISTOL_MAGAZINE_INCENDIARY;
	public static Item MINIGUN_DRUM_INCENDIARY;
	public static Item SMG_MAGAZINE_INCENDIARY;
	public static Item ASSAULTRIFLE_MAGAZINE_INCENDIARY;
	public static Item LMG_MAGAZINE_INCENDIARY;

	public static Item ROCKET_NUKE;
	public static Item SNIPER_ROUNDS_EXPLOSIVE;
	public static Item AS50_MAGAZINE_EXPLOSIVE;
	public static Item ROCKET_HIGH_VELOCITY;

	// ---- crafting materials (lot A) ----
	public static Item RECEIVER_IRON;
	public static Item RECEIVER_STEEL;
	public static Item RECEIVER_OBSIDIAN_STEEL;
	public static Item RECEIVER_CARBON;
	public static Item BARREL_STONE;
	public static Item BARREL_IRON;
	public static Item BARREL_OBSIDIAN_STEEL;
	public static Item BARREL_CARBON;
	public static Item BARREL_LASER;
	public static Item STOCK_WOOD;
	public static Item STOCK_PLASTIC;
	public static Item STOCK_CARBON;
	public static Item STEAMARMOR_PLATE;
	public static Item PLATE_IRON;
	public static Item PLATE_COPPER;
	public static Item PLATE_TIN;
	public static Item PLATE_BRONZE;
	public static Item PLATE_STEEL;
	public static Item PLATE_OBSIDIAN_STEEL;
	public static Item PLATE_LEAD;
	public static Item PLATE_CARBON;
	public static Item PLATE_TITANIUM;
	public static Item PLASTIC_SHEET;
	public static Item RUBBER_BAR;
	public static Item MECHANICAL_PARTS_IRON;
	public static Item MECHANICAL_PARTS_OBSIDIAN_STEEL;
	public static Item MECHANICAL_PARTS_CARBON;
	public static Item HEAVY_CLOTH;
	public static Item BIOMASS;
	public static Item WIRE_COPPER;
	public static Item WIRE_GOLD;
	public static Item CARBON_FIBERS;
	public static Item CIRCUIT_BOARD_BASIC;
	public static Item CIRCUIT_BOARD_ELITE;
	public static Item POWER_ARMOR_PLATING;
	public static Item COIL;
	public static Item CYBERNETIC_PARTS;
	public static Item ELECTRIC_ENGINE;
	public static Item LASER_FOCUS;
	public static Item PUMP_MECHANISM;
	public static Item RAD_EMITTER;
	public static Item SONIC_EMITTER;
	public static Item TGX;
	public static Item TREATED_LEATHER;
	public static Item ORE_TITANIUM;
	public static Item INGOT_COPPER;
	public static Item INGOT_TIN;
	public static Item INGOT_BRONZE;
	public static Item INGOT_LEAD;
	public static Item INGOT_STEEL;
	public static Item INGOT_OBSIDIAN_STEEL;
	public static Item INGOT_TITANIUM;
	public static Item NUGGET_COPPER;
	public static Item NUGGET_LEAD;
	public static Item NUGGET_STEEL;
	public static Item GAS_MASK_FILTER;
	public static Item GLIDER_BACKBACK;
	public static Item GLIDER_WING;
	public static Item ANTI_GRAV_CORE;
	public static Item OXYGEN_MASK;
	public static Item MACHINE_UPGRADE_STACK;
	public static Item RAW_RUBBER;
	public static Item RAW_PLASTIC;
	public static Item YELLOWCAKE;
	public static Item ENRICHED_URANIUM;
	public static Item TURRET_ARMOR_IRON;
	public static Item TURRET_ARMOR_STEEL;
	public static Item TURRET_ARMOR_OBSIDIAN_STEEL;
	public static Item TURRET_ARMOR_CARBON;
	public static Item QUARTZ_ROD;
	public static Item RC_HEAT_RAY;
	public static Item RC_UV_EMITTER;
	public static Item TACTICAL_NUKE_WARHEAD;
	public static Item MININGDRILLHEAD_OBSIDIAN;
	public static Item MININGDRILLHEAD_CARBON;
	public static Item POWERHAMMERHEAD_OBSIDIAN;
	public static Item POWERHAMMERHEAD_CARBON;
	public static Item CHAINSAWBLADES_OBSIDIAN;
	public static Item CHAINSAWBLADES_CARBON;
	public static Item BARREL_GAUSS;
	public static Item BARREL_TITANIUM;
	public static Item RECEIVER_TITANIUM;
	public static Item PLASMA_GENERATOR;
	public static Item WORKING_GLOVES;
	public static Item PROTECTIVE_FIBER;
	public static Item OREDRILLHEAD_STEEL;
	public static Item OREDRILLHEAD_OBSIDIANSTEEL;
	public static Item OREDRILLHEAD_CARBON;
	public static Item OREDRILLHEAD_MEDIUM_STEEL;
	public static Item OREDRILLHEAD_MEDIUM_OBSIDIANSTEEL;
	public static Item OREDRILLHEAD_MEDIUM_CARBON;
	public static Item OREDRILLHEAD_LARGE_STEEL;
	public static Item OREDRILLHEAD_LARGE_OBSIDIANSTEEL;
	public static Item OREDRILLHEAD_LARGE_CARBON;
	public static Item INFUSION_BAG;
	public static Item UPGRADE_PROTECTION_1;
	public static Item UPGRADE_PROJECTILE_PROTECTION_1;
	public static Item UPGRADE_BLAST_PROTECTION_1;
	public static Item UPGRADE_PROTECTION_2;
	public static Item UPGRADE_PROJECTILE_PROTECTION_2;
	public static Item UPGRADE_BLAST_PROTECTION_2;
	public static Item UPGRADE_PROTECTION_3;
	public static Item UPGRADE_PROJECTILE_PROTECTION_3;
	public static Item UPGRADE_BLAST_PROTECTION_3;

	//Tools/weapons
	public static Item COMBAT_KNIFE;
	public static Item CROWBAR;

	//TOOL Materials (1.12 EnumHelper.addToolMaterial("Steel", 2, 1000, 7.5f, 2.5f, 12), no repair item)
	public static final net.minecraft.world.item.Tier TG_STEEL = new net.minecraftforge.common.ForgeTier(2, 1000, 7.5f, 2.5f, 12,
			net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL, () -> net.minecraft.world.item.crafting.Ingredient.EMPTY);
	// ---- end crafting materials ----

	// ---- additional slot items (lot D) ----
	public static techguns.items.additionalslots.ItemGasMask GAS_MASK;
	public static techguns.items.additionalslots.ItemGlider GLIDER;
	public static techguns.items.additionalslots.ItemNightVisionGoggles NIGHTVISION_GOGGLES;
	public static techguns.items.additionalslots.ItemJumpPack JUMPPACK;
	public static techguns.items.additionalslots.ItemJetpack JETPACK;
	public static techguns.items.additionalslots.ItemScubaTanks SCUBA_TANKS;
	public static techguns.items.additionalslots.ItemTacticalMask TACTICAL_MASK;
	public static techguns.items.additionalslots.ItemAntiGravPack ANTI_GRAV_PACK;
	// ---- end additional slot items ----

	// ---- lot I (radiation) ----
	public static techguns.items.ItemRadAway RAD_AWAY;
	public static techguns.items.ItemRadpills RAD_PILLS;
	// ---- end lot I ----

	public static ItemStack newStack(ItemStack stack, int size) {
		ItemStack ret = stack.copy();
		ret.setCount(size);
		return ret;
	}

	public static ItemStack newStack(Item item, int size) {
		if (item == null) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(item, size);
	}

	/**
	 * Creates the ammo items, called from the item RegisterEvent before guns and armors are created.
	 */
	public static void createItems() {
		STONE_BULLETS = new TGAmmoItem("stonebullets");
		PISTOL_ROUNDS = new TGAmmoItem("pistolrounds");
		SHOTGUN_ROUNDS = new TGAmmoItem("shotgunrounds");
		RIFLE_ROUNDS = new TGAmmoItem("riflerounds");
		SNIPER_ROUNDS = new TGAmmoItem("sniperrounds");
		GRENADE_40MM = new TGAmmoItem("40mmgrenade");
		ADVANCED_ROUNDS = new TGAmmoItem("advancedrounds");
		ROCKET = new TGAmmoItem("rocket", true);
		RIFLE_ROUNDS_STACK = new TGAmmoItem("rifleroundsstack");

		SMG_MAGAZINE = new TGAmmoItem("smgmagazine");
		SMG_MAGAZINE_EMPTY = new TGAmmoItem("smgmagazineempty");
		PISTOL_MAGAZINE = new TGAmmoItem("pistolmagazine");
		PISTOL_MAGAZINE_EMPTY = new TGAmmoItem("pistolmagazineempty");
		ASSAULTRIFLE_MAGAZINE = new TGAmmoItem("assaultriflemagazine", true);
		ASSAULTRIFLE_MAGAZINE_EMPTY = new TGAmmoItem("assaultriflemagazineempty", true);
		LMG_MAGAZINE = new TGAmmoItem("lmgmagazine", true);
		LMG_MAGAZINE_EMPTY = new TGAmmoItem("lmgmagazineempty", true);
		MINIGUN_DRUM = new TGAmmoItem("minigundrum");
		MINIGUN_DRUM_EMPTY = new TGAmmoItem("minigundrumempty");
		AS50_MAGAZINE = new TGAmmoItem("as50magazine", true);
		AS50_MAGAZINE_EMPTY = new TGAmmoItem("as50magazineempty", true);

		ADVANCED_MAGAZINE = new TGAmmoItem("advancedmagazine");
		ADVANCED_MAGAZINE_EMPTY = new TGAmmoItem("advancedmagazineempty");

		COMPRESSED_AIR_TANK = new TGAmmoItem("compressedairtank");
		COMPRESSED_AIR_TANK_EMPTY = new TGAmmoItem("compressedairtankempty");
		BIO_TANK = new TGAmmoItem("biotank");
		BIO_TANK_EMPTY = new TGAmmoItem("biotankempty");
		FUEL_TANK = new TGAmmoItem("fueltank");
		FUEL_TANK_EMPTY = new TGAmmoItem("fueltankempty");
		ENERGY_CELL = new TGAmmoItem("energycell");
		ENERGY_CELL_EMPTY = new TGAmmoItem("energycellempty");
		NUCLEAR_POWERCELL = new TGAmmoItem("nuclearpowercell");
		NUCLEAR_POWERCELL_EMPTY = new TGAmmoItem("nuclearpowercelldepleted");

		NETHER_CHARGE = new TGAmmoItem("nethercharge");
		REDSTONE_BATTERY = new TGAmmoItem("redstone_battery");
		REDSTONE_BATTERY_EMPTY = new TGAmmoItem("redstone_battery_empty");
		GAUSSRIFLE_SLUGS = new TGAmmoItem("gaussrifleslugs");

		SHOTGUN_ROUNDS_INCENDIARY = new TGAmmoItem("shotgunrounds_incendiary");
		AS50_MAGAZINE_INCENDIARY = new TGAmmoItem("as50magazine_incendiary", true);
		SNIPER_ROUNDS_INCENDIARY = new TGAmmoItem("sniperrounds_incendiary");
		PISTOL_ROUNDS_INCENDIARY = new TGAmmoItem("pistolrounds_incendiary");
		RIFLE_ROUNDS_INCENDIARY = new TGAmmoItem("riflerounds_incendiary");
		RIFLE_ROUNDS_STACK_INCENDIARY = new TGAmmoItem("rifleroundsstack_incendiary");
		SMG_MAGAZINE_INCENDIARY = new TGAmmoItem("smgmagazine_incendiary");
		PISTOL_MAGAZINE_INCENDIARY = new TGAmmoItem("pistolmagazine_incendiary");
		MINIGUN_DRUM_INCENDIARY = new TGAmmoItem("minigundrum_incendiary");
		ASSAULTRIFLE_MAGAZINE_INCENDIARY = new TGAmmoItem("assaultriflemagazine_incendiary", true);
		LMG_MAGAZINE_INCENDIARY = new TGAmmoItem("lmgmagazine_incendiary", true);

		ROCKET_NUKE = new TGAmmoItem("rocket_nuke", true);

		AS50_MAGAZINE_EXPLOSIVE = new TGAmmoItem("as50magazine_explosive", true);
		SNIPER_ROUNDS_EXPLOSIVE = new TGAmmoItem("sniperrounds_explosive");

		ROCKET_HIGH_VELOCITY = new TGAmmoItem("rocket_high_velocity", true);

		// ---- crafting materials (lot A) ----
		RECEIVER_IRON = new techguns.items.TGSharedItem("ironreceiver");
		RECEIVER_STEEL = new techguns.items.TGSharedItem("steelreceiver");
		RECEIVER_OBSIDIAN_STEEL = new techguns.items.TGSharedItem("obsidiansteelreceiver");
		RECEIVER_CARBON = new techguns.items.TGSharedItem("carbonreceiver");
		BARREL_STONE = new techguns.items.TGSharedItem("stonebarrel");
		BARREL_IRON = new techguns.items.TGSharedItem("ironbarrel");
		BARREL_OBSIDIAN_STEEL = new techguns.items.TGSharedItem("obsidiansteelbarrel");
		BARREL_CARBON = new techguns.items.TGSharedItem("carbonbarrel");
		BARREL_LASER = new techguns.items.TGSharedItem("laserbarrel");
		STOCK_WOOD = new techguns.items.TGSharedItem("woodstock");
		STOCK_PLASTIC = new techguns.items.TGSharedItem("plasticstock");
		STOCK_CARBON = new techguns.items.TGSharedItem("carbonstock");
		STEAMARMOR_PLATE = new techguns.items.TGSharedItem("steamarmorplate");
		PLATE_IRON = new techguns.items.TGSharedItem("plateiron");
		PLATE_COPPER = new techguns.items.TGSharedItem("platecopper");
		PLATE_TIN = new techguns.items.TGSharedItem("platetin");
		PLATE_BRONZE = new techguns.items.TGSharedItem("platebronze");
		PLATE_STEEL = new techguns.items.TGSharedItem("platesteel");
		PLATE_OBSIDIAN_STEEL = new techguns.items.TGSharedItem("plateobsidiansteel");
		PLATE_LEAD = new techguns.items.TGSharedItem("platelead");
		PLATE_CARBON = new techguns.items.TGSharedItem("platecarbon");
		PLATE_TITANIUM = new techguns.items.TGSharedItem("platetitanium");
		PLASTIC_SHEET = new techguns.items.TGSharedItem("plasticsheet");
		RUBBER_BAR = new techguns.items.TGSharedItem("rubberbar");
		MECHANICAL_PARTS_IRON = new techguns.items.TGSharedItem("mechanicalpartsiron");
		MECHANICAL_PARTS_OBSIDIAN_STEEL = new techguns.items.TGSharedItem("mechanicalpartsobsidiansteel");
		MECHANICAL_PARTS_CARBON = new techguns.items.TGSharedItem("mechanicalpartscarbon");
		HEAVY_CLOTH = new techguns.items.TGSharedItem("heavycloth");
		BIOMASS = new techguns.items.TGSharedItem("biomass");
		WIRE_COPPER = new techguns.items.TGSharedItem("copperwire");
		WIRE_GOLD = new techguns.items.TGSharedItem("goldwire");
		CARBON_FIBERS = new techguns.items.TGSharedItem("carbonfibers");
		CIRCUIT_BOARD_BASIC = new techguns.items.TGSharedItem("circuitboard");
		CIRCUIT_BOARD_ELITE = new techguns.items.TGSharedItem("circuitboardelite");
		POWER_ARMOR_PLATING = new techguns.items.TGSharedItem("powerplating");
		COIL = new techguns.items.TGSharedItem("coil");
		CYBERNETIC_PARTS = new techguns.items.TGSharedItem("cyberneticparts");
		ELECTRIC_ENGINE = new techguns.items.TGSharedItem("electricengine");
		LASER_FOCUS = new techguns.items.TGSharedItem("laserfocus");
		PUMP_MECHANISM = new techguns.items.TGSharedItem("pumpmechanism");
		RAD_EMITTER = new techguns.items.TGSharedItem("rademitter");
		SONIC_EMITTER = new techguns.items.TGSharedItem("sonicemitter");
		TGX = new techguns.items.TGSharedItem("tgx");
		TREATED_LEATHER = new techguns.items.TGSharedItem("treatedleather");
		ORE_TITANIUM = new techguns.items.TGSharedItem("oretitanium");
		INGOT_COPPER = new techguns.items.TGSharedItem("ingotcopper");
		INGOT_TIN = new techguns.items.TGSharedItem("ingottin");
		INGOT_BRONZE = new techguns.items.TGSharedItem("ingotbronze");
		INGOT_LEAD = new techguns.items.TGSharedItem("ingotlead");
		INGOT_STEEL = new techguns.items.TGSharedItem("ingotsteel");
		INGOT_OBSIDIAN_STEEL = new techguns.items.TGSharedItem("ingotobsidiansteel");
		INGOT_TITANIUM = new techguns.items.TGSharedItem("ingottitanium");
		NUGGET_COPPER = new techguns.items.TGSharedItem("nuggetcopper");
		NUGGET_LEAD = new techguns.items.TGSharedItem("nuggetlead");
		NUGGET_STEEL = new techguns.items.TGSharedItem("nuggetsteel");
		GAS_MASK_FILTER = new techguns.items.TGSharedItem("gasmaskfilter");
		GLIDER_BACKBACK = new techguns.items.TGSharedItem("gliderbackpack");
		GLIDER_WING = new techguns.items.TGSharedItem("gliderwing");
		ANTI_GRAV_CORE = new techguns.items.TGSharedItem("antigravcore");
		OXYGEN_MASK = new techguns.items.TGSharedItem("oxygenmask", techguns.api.tginventory.TGSlotType.FACESLOT, 1);
		MACHINE_UPGRADE_STACK = new techguns.items.TGSharedItem("machinestackupgrade", techguns.api.tginventory.TGSlotType.NORMAL, 7);
		RAW_RUBBER = new techguns.items.TGSharedItem("rawrubber");
		RAW_PLASTIC = new techguns.items.TGSharedItem("rawplastic");
		YELLOWCAKE = new techguns.items.TGSharedItem("yellowcake");
		ENRICHED_URANIUM = new techguns.items.TGSharedItem("enricheduranium");
		TURRET_ARMOR_IRON = new techguns.items.TGSharedItem("turretarmoriron", techguns.api.tginventory.TGSlotType.TURRETARMOR);
		TURRET_ARMOR_STEEL = new techguns.items.TGSharedItem("turretarmorsteel", techguns.api.tginventory.TGSlotType.TURRETARMOR);
		TURRET_ARMOR_OBSIDIAN_STEEL = new techguns.items.TGSharedItem("turretarmorobsidiansteel", techguns.api.tginventory.TGSlotType.TURRETARMOR);
		TURRET_ARMOR_CARBON = new techguns.items.TGSharedItem("turretarmorcarbon", techguns.api.tginventory.TGSlotType.TURRETARMOR);
		QUARTZ_ROD = new techguns.items.TGSharedItem("quartzrod");
		RC_HEAT_RAY = new techguns.items.TGSharedItem("rcheatray", techguns.api.tginventory.TGSlotType.REACTION_CHAMBER_FOCUS, 1);
		RC_UV_EMITTER = new techguns.items.TGSharedItem("rcuvemitter", techguns.api.tginventory.TGSlotType.REACTION_CHAMBER_FOCUS, 1);
		TACTICAL_NUKE_WARHEAD = new techguns.items.TGSharedItem("tacticalnukewarhead");
		MININGDRILLHEAD_OBSIDIAN = new techguns.items.TGSharedItem("miningdrillhead_obsidian");
		MININGDRILLHEAD_CARBON = new techguns.items.TGSharedItem("miningdrillhead_carbon");
		POWERHAMMERHEAD_OBSIDIAN = new techguns.items.TGSharedItem("powerhammerhead_obsidian");
		POWERHAMMERHEAD_CARBON = new techguns.items.TGSharedItem("powerhammerhead_carbon");
		CHAINSAWBLADES_OBSIDIAN = new techguns.items.TGSharedItem("chainsawblades_obsidian");
		CHAINSAWBLADES_CARBON = new techguns.items.TGSharedItem("chainsawblades_carbon");
		BARREL_GAUSS = new techguns.items.TGSharedItem("gaussbarrel");
		BARREL_TITANIUM = new techguns.items.TGSharedItem("shieldedtitaniumbarrel");
		RECEIVER_TITANIUM = new techguns.items.TGSharedItem("titaniumreceiver");
		PLASMA_GENERATOR = new techguns.items.TGSharedItem("plasmagenerator");
		WORKING_GLOVES = new techguns.items.TGSharedItem("workinggloves", techguns.api.tginventory.TGSlotType.HANDSLOT, 1);
		PROTECTIVE_FIBER = new techguns.items.TGSharedItem("protectivefiber");
		OREDRILLHEAD_STEEL = new techguns.items.TGSharedItem("oredrillsmall_steel", techguns.api.tginventory.TGSlotType.DRILL_SMALL, 1).setOreDrillMiningLevel(1);
		OREDRILLHEAD_OBSIDIANSTEEL = new techguns.items.TGSharedItem("oredrillsmall_obsidiansteel", techguns.api.tginventory.TGSlotType.DRILL_SMALL, 1).setOreDrillMiningLevel(2);
		OREDRILLHEAD_CARBON = new techguns.items.TGSharedItem("oredrillsmall_carbon", techguns.api.tginventory.TGSlotType.DRILL_SMALL, 1).setOreDrillMiningLevel(3);
		OREDRILLHEAD_MEDIUM_STEEL = new techguns.items.TGSharedItem("oredrillmedium_steel", techguns.api.tginventory.TGSlotType.DRILL_MEDIUM, 1).setOreDrillMiningLevel(1);
		OREDRILLHEAD_MEDIUM_OBSIDIANSTEEL = new techguns.items.TGSharedItem("oredrillmedium_obsidiansteel", techguns.api.tginventory.TGSlotType.DRILL_MEDIUM, 1).setOreDrillMiningLevel(2);
		OREDRILLHEAD_MEDIUM_CARBON = new techguns.items.TGSharedItem("oredrillmedium_carbon", techguns.api.tginventory.TGSlotType.DRILL_MEDIUM, 1).setOreDrillMiningLevel(3);
		OREDRILLHEAD_LARGE_STEEL = new techguns.items.TGSharedItem("oredrilllarge_steel", techguns.api.tginventory.TGSlotType.DRILL_LARGE, 1).setOreDrillMiningLevel(1);
		OREDRILLHEAD_LARGE_OBSIDIANSTEEL = new techguns.items.TGSharedItem("oredrilllarge_obsidiansteel", techguns.api.tginventory.TGSlotType.DRILL_LARGE, 1).setOreDrillMiningLevel(2);
		OREDRILLHEAD_LARGE_CARBON = new techguns.items.TGSharedItem("oredrilllarge_carbon", techguns.api.tginventory.TGSlotType.DRILL_LARGE, 1).setOreDrillMiningLevel(3);
		INFUSION_BAG = new techguns.items.TGSharedItem("infusionbag");
		UPGRADE_PROTECTION_1 = new techguns.items.TGSharedItem("upgrade_protection_1", techguns.api.tginventory.TGSlotType.ARMOR_UPGRADE, 1);
		UPGRADE_PROJECTILE_PROTECTION_1 = new techguns.items.TGSharedItem("upgrade_projectile_protection_1", techguns.api.tginventory.TGSlotType.ARMOR_UPGRADE, 1);
		UPGRADE_BLAST_PROTECTION_1 = new techguns.items.TGSharedItem("upgrade_blast_protection_1", techguns.api.tginventory.TGSlotType.ARMOR_UPGRADE, 1);
		UPGRADE_PROTECTION_2 = new techguns.items.TGSharedItem("upgrade_protection_2", techguns.api.tginventory.TGSlotType.ARMOR_UPGRADE, 1);
		UPGRADE_PROJECTILE_PROTECTION_2 = new techguns.items.TGSharedItem("upgrade_projectile_protection_2", techguns.api.tginventory.TGSlotType.ARMOR_UPGRADE, 1);
		UPGRADE_BLAST_PROTECTION_2 = new techguns.items.TGSharedItem("upgrade_blast_protection_2", techguns.api.tginventory.TGSlotType.ARMOR_UPGRADE, 1);
		UPGRADE_PROTECTION_3 = new techguns.items.TGSharedItem("upgrade_protection_3", techguns.api.tginventory.TGSlotType.ARMOR_UPGRADE, 1);
		UPGRADE_PROJECTILE_PROTECTION_3 = new techguns.items.TGSharedItem("upgrade_projectile_protection_3", techguns.api.tginventory.TGSlotType.ARMOR_UPGRADE, 1);
		UPGRADE_BLAST_PROTECTION_3 = new techguns.items.TGSharedItem("upgrade_blast_protection_3", techguns.api.tginventory.TGSlotType.ARMOR_UPGRADE, 1);

		COMBAT_KNIFE = new techguns.items.tools.TGSword(TG_STEEL, "combatknife");
		CROWBAR = new techguns.items.tools.TGCrowbar(TG_STEEL, "crowbar");
		// ---- end crafting materials ----
	}

	/**
	 * Creates the additional slot items (jetpack, gas mask, ...), called after the armors are created
	 */
	public static void createAdditionalSlotItems() {
		// ---- lot I (radiation), created before the gas mask like in 1.12 ----
		RAD_AWAY = new techguns.items.ItemRadAway("radaway");
		RAD_PILLS = new techguns.items.ItemRadpills("radpills");
		// ---- end lot I ----
		// ---- additional slot items (lot D) ----
		GAS_MASK = new techguns.items.additionalslots.ItemGasMask("gasmask", 1, 300).setRadresist(1f);
		GLIDER = new techguns.items.additionalslots.ItemGlider("glider", 1, -1);
		JUMPPACK = new techguns.items.additionalslots.ItemJumpPack("jumppack", 4, 1000);
		SCUBA_TANKS = new techguns.items.additionalslots.ItemScubaTanks("scubatanks", 1, 600);
		NIGHTVISION_GOGGLES = new techguns.items.additionalslots.ItemNightVisionGoggles("nightvisiongoggles", 1, 24000);
		JETPACK = new techguns.items.additionalslots.ItemJetpack("jetpack", 4, 5000);
		TACTICAL_MASK = new techguns.items.additionalslots.ItemTacticalMask("tacticalmask", 4, 24000);
		ANTI_GRAV_PACK = new techguns.items.additionalslots.ItemAntiGravPack("antigravpack", 5, 12000);
		// ---- end additional slot items ----
	}

	/**
	 * Registers everything in ITEMLIST
	 */
	public static void registerItems(RegisterEvent event) {
		event.register(Registries.ITEM, helper -> {
			for (Item item : ITEMLIST) {
				if (item instanceof GenericItem gi) {
					helper.register(gi.getTGRegistryName(), item);
				} else if (item instanceof ITGRegistryNamed named) {
					helper.register(named.getTGRegistryName(), item);
				} else {
					throw new IllegalStateException("Item without Techguns registry name: " + item);
				}
			}
		});
	}

	/**
	 * Implemented by Techguns items that cannot extend GenericItem (armor, ...)
	 */
	public interface ITGRegistryNamed {
		net.minecraft.resources.ResourceLocation getTGRegistryName();
	}
}
