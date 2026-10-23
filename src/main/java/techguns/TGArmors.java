package techguns;

import java.util.ArrayList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import techguns.damagesystem.ShieldStats;
import techguns.items.armors.ArmorPowerType;
import techguns.items.armors.GenericArmor;
import techguns.items.armors.GenericArmorMultiCamo;
import techguns.items.armors.GenericShield;
import techguns.items.armors.PoweredArmor;
import techguns.items.armors.TGArmorMaterial;

/**
 * Port of the 1.12 TGArmors: armor materials, armor items and shields. createItems() is called from Techguns.onRegister after
 * the ammo items (batteries) and guns are created. The items add themselves to TGItems.ITEMLIST.
 *
 * Repair materials are given as registry names (vanilla items or the Techguns crafting materials of TGItems).
 */
public class TGArmors {
	public static final ArrayList<GenericArmor> armors = new ArrayList<>();
	public static final ArrayList<GenericShield> shields = new ArrayList<>();

	public static final ResourceLocation ARMORMODEL_STEAM_ARMOR_0 = new ResourceLocation(Techguns.MODID, "steam_armor_0");
	public static final ResourceLocation ARMORMODEL_STEAM_ARMOR_1 = new ResourceLocation(Techguns.MODID, "steam_armor_1");
	public static final ResourceLocation ARMORMODEL_POWER_ARMOR_0 = new ResourceLocation(Techguns.MODID, "power_armor_0");
	public static final ResourceLocation ARMORMODEL_POWER_ARMOR_1 = new ResourceLocation(Techguns.MODID, "power_armor_1");
	public static final ResourceLocation ARMORMODEL_EXO_SUIT_0 = new ResourceLocation(Techguns.MODID, "exo_suit_0");
	public static final ResourceLocation ARMORMODEL_EXO_SUIT_1 = new ResourceLocation(Techguns.MODID, "exo_suit_1");
	public static final ResourceLocation ARMORMODEL_EXO_SUIT_2 = new ResourceLocation(Techguns.MODID, "exo_suit_2");
	public static final ResourceLocation ARMORMODEL_BERET_0 = new ResourceLocation(Techguns.MODID, "beret_0");
	public static final ResourceLocation ARMORMODEL_COAT_0 = new ResourceLocation(Techguns.MODID, "coat_0");
	public static final ResourceLocation ARMORMODEL_COAT_1 = new ResourceLocation(Techguns.MODID, "coat_1");
	public static final ResourceLocation ARMORMODEL_COAT_2 = new ResourceLocation(Techguns.MODID, "coat_2");
	public static final ResourceLocation ARMORMODEL_COAT_3 = new ResourceLocation(Techguns.MODID, "coat_3");
	public static final ResourceLocation ARMORMODEL_STEAM_ARMOR_2 = new ResourceLocation(Techguns.MODID, "steam_armor_2");
	public static final ResourceLocation ARMORMODEL_POWER_ARMOR_2 = new ResourceLocation(Techguns.MODID, "power_armor_2");
	public static final ResourceLocation ARMORMODEL_POWER_ARMOR_MK2_0 = new ResourceLocation(Techguns.MODID, "power_armor_mk2_0");
	public static final ResourceLocation ARMORMODEL_POWER_ARMOR_MK2_1 = new ResourceLocation(Techguns.MODID, "power_armor_mk2_1");
	public static final ResourceLocation ARMORMODEL_POWER_ARMOR_MK2_2 = new ResourceLocation(Techguns.MODID, "power_armor_mk2_2");

	/*
	 * Repair materials (1.12 TGItems shared item variants)
	 */
	protected static final ResourceLocation IRON_INGOT = new ResourceLocation("minecraft", "iron_ingot");
	protected static final ResourceLocation HEAVY_CLOTH = tg("heavycloth");
	protected static final ResourceLocation STEAMARMOR_PLATE = tg("steamarmorplate");
	protected static final ResourceLocation PROTECTIVE_FIBER = tg("protectivefiber");
	protected static final ResourceLocation INGOT_OBSIDIAN_STEEL = tg("ingotobsidiansteel");
	protected static final ResourceLocation RUBBER_BAR = tg("rubberbar");
	protected static final ResourceLocation TREATED_LEATHER = tg("treatedleather");
	protected static final ResourceLocation PLATE_CARBON = tg("platecarbon");
	protected static final ResourceLocation CARBON_FIBERS = tg("carbonfibers");
	protected static final ResourceLocation POWER_ARMOR_PLATING = tg("powerplating");
	protected static final ResourceLocation WIRE_GOLD = tg("goldwire");
	protected static final ResourceLocation CIRCUIT_BOARD_ELITE = tg("circuitboardelite");

	private static ResourceLocation tg(String name) {
		return new ResourceLocation(Techguns.MODID, name);
	}

	public static TGArmorMaterial T1_COMBAT = new TGArmorMaterial("T1_COMBAT", 60, 0, 15.0f, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5f);
	public static TGArmorMaterial T1_SCOUT = new TGArmorMaterial("T1_SCOUT", 60, 0, 13.0f, SoundEvents.ARMOR_EQUIP_LEATHER, 0f);
	public static TGArmorMaterial T1_MINER = new TGArmorMaterial("T1_MINER", 60, 0, 13.0f, SoundEvents.ARMOR_EQUIP_LEATHER, 0f);
	public static TGArmorMaterial T1_STEAM = new TGArmorMaterial("T1_STEAM", 200, 0, 19.0f, SoundEvents.ARMOR_EQUIP_IRON, 1f).setArmorElemental(17.0f).setArmorExplosion(18.0f).setArmorPoison(10.0f).setArmorRadiation(6.0f).setPenetrationResistance(0.1f);

	public static TGArmorMaterial T2_HAZMAT = new TGArmorMaterial("T2_HAZMAT", 80, 0, 10.0f, SoundEvents.ARMOR_EQUIP_LEATHER, 0f).setArmorElemental(16.0f).setArmorExplosion(10.0f).setArmorPoison(20.0f).setArmorRadiation(20.0f);

	public static TGArmorMaterial T2_COMBAT = new TGArmorMaterial("T2_COMBAT", 72, 0, 18.0f, SoundEvents.ARMOR_EQUIP_LEATHER, 1f);
	public static TGArmorMaterial T2_COMMANDO = new TGArmorMaterial("T2_COMMANDO", 72, 0, 18.0f, SoundEvents.ARMOR_EQUIP_LEATHER, 1f).setArmorElemental(16.0f).setArmorExplosion(16.0f).setArmorPoison(10.0f).setArmorRadiation(5.0f);

	public static TGArmorMaterial T2_RIOT = new TGArmorMaterial("T2_RIOT", 96, 0, 18.5f, SoundEvents.ARMOR_EQUIP_LEATHER, 1.5f).setArmorFire(18.0f).setArmorExplosion(18.0f).setArmorEnergy(18.0f).setArmorIce(17.0f).setArmorLightning(17.0f).setArmorPoison(16.0f).setArmorRadiation(16.0f).setPenetrationResistance(0.1f);

	public static TGArmorMaterial T3_COMBAT = new TGArmorMaterial("T3_COMBAT", 240, 0, 21.0f, SoundEvents.ARMOR_EQUIP_CHAIN, 2.0f).setArmorFire(18.0f).setArmorExplosion(18.0f).setArmorEnergy(19.0f).setArmorIce(18.0f).setArmorLightning(18.0f).setArmorPoison(10.0f).setArmorRadiation(12.0f).setPenetrationResistance(0.15f);

	public static TGArmorMaterial T3_POWER = new TGArmorMaterial("T3_POWER", 360, 0, 22.0f, SoundEvents.ARMOR_EQUIP_IRON, 2.5f).setArmorFire(19.0f).setArmorExplosion(21.0f).setArmorEnergy(20.0f).setArmorIce(19.0f).setArmorLightning(19.0f).setArmorPoison(15.0f).setArmorRadiation(17.0f).setPenetrationResistance(0.25f);

	public static TGArmorMaterial T3_MINER = new TGArmorMaterial("T3_MINER", 300, 0, 20.0f, SoundEvents.ARMOR_EQUIP_IRON, 2.0f).setArmorFire(20.0f).setArmorExplosion(20.0f).setArmorEnergy(20.0f).setArmorIce(20.0f).setArmorLightning(20.0f).setArmorPoison(20.0f).setArmorRadiation(20.0f).setPenetrationResistance(0.15f);

	//Same as T3_Combat
	public static TGArmorMaterial T3_EXO = new TGArmorMaterial("T3_EXO", 300, 0, 21.0f, SoundEvents.ARMOR_EQUIP_CHAIN, 2.0f).setArmorFire(18.5f).setArmorExplosion(18.5f).setArmorEnergy(19.0f).setArmorIce(18.5f).setArmorLightning(18.5f).setArmorPoison(10.0f).setArmorRadiation(12.0f).setPenetrationResistance(0.15f);

	public static TGArmorMaterial T2_BERET = new TGArmorMaterial("T2_BERET", 60, 0, 8.0f, SoundEvents.ARMOR_EQUIP_LEATHER, 0);

	public static TGArmorMaterial T4_PRAETOR = new TGArmorMaterial("T4_PRAETOR", 360, 0, 22.0f, SoundEvents.ARMOR_EQUIP_IRON, 3.0f).setArmorFire(21.0f).setArmorExplosion(21.0f).setArmorEnergy(21.0f).setArmorIce(21.0f).setArmorLightning(21.0f).setArmorPoison(21.0f).setArmorRadiation(20.0f).setPenetrationResistance(0.25f);

	public static TGArmorMaterial T4_POWER = new TGArmorMaterial("T4_POWER", 460, 0, 23.0f, SoundEvents.ARMOR_EQUIP_IRON, 3.5f).setArmorFire(21.0f).setArmorExplosion(22.0f).setArmorEnergy(21.0f).setArmorIce(21.0f).setArmorLightning(21.0f).setArmorPoison(19.0f).setArmorRadiation(20.0f).setPenetrationResistance(0.3f);

	/**
	 * ARMORS
	 */
	public static GenericArmor t1_combat_Helmet;
	public static GenericArmor t1_combat_Chestplate;
	public static GenericArmor t1_combat_Leggings;
	public static GenericArmor t1_combat_Boots;

	public static GenericArmor t1_scout_Helmet;
	public static GenericArmor t1_scout_Chestplate;
	public static GenericArmor t1_scout_Leggings;
	public static GenericArmor t1_scout_Boots;

	public static GenericArmor t1_miner_Helmet;
	public static GenericArmor t1_miner_Chestplate;
	public static GenericArmor t1_miner_Leggings;
	public static GenericArmor t1_miner_Boots;

	public static GenericArmor steam_Helmet;
	public static GenericArmor steam_Chestplate;
	public static GenericArmor steam_Leggings;
	public static GenericArmor steam_Boots;

	public static GenericArmor hazmat_Helmet;
	public static GenericArmor hazmat_Chestplate;
	public static GenericArmor hazmat_Leggings;
	public static GenericArmor hazmat_Boots;

	public static GenericArmor t2_combat_Helmet;
	public static GenericArmor t2_combat_Chestplate;
	public static GenericArmor t2_combat_Leggings;
	public static GenericArmor t2_combat_Boots;

	public static GenericArmor t2_commando_Helmet;
	public static GenericArmor t2_commando_Chestplate;
	public static GenericArmor t2_commando_Leggings;
	public static GenericArmor t2_commando_Boots;

	public static GenericArmor t2_riot_Helmet;
	public static GenericArmor t2_riot_Chestplate;
	public static GenericArmor t2_riot_Leggings;
	public static GenericArmor t2_riot_Boots;

	public static GenericArmor t3_combat_Helmet;
	public static GenericArmor t3_combat_Chestplate;
	public static GenericArmor t3_combat_Leggings;
	public static GenericArmor t3_combat_Boots;

	public static GenericArmor t3_power_Helmet;
	public static GenericArmor t3_power_Chestplate;
	public static GenericArmor t3_power_Leggings;
	public static GenericArmor t3_power_Boots;

	public static GenericArmor t3_miner_Helmet;
	public static GenericArmor t3_miner_Chestplate;
	public static GenericArmor t3_miner_Leggings;
	public static GenericArmor t3_miner_Boots;

	public static GenericArmor t3_exo_Helmet;
	public static GenericArmor t3_exo_Chestplate;
	public static GenericArmor t3_exo_Leggings;
	public static GenericArmor t3_exo_Boots;

	public static GenericArmor t4_praetor_Helmet;
	public static GenericArmor t4_praetor_Chestplate;
	public static GenericArmor t4_praetor_Leggings;
	public static GenericArmor t4_praetor_Boots;

	public static GenericArmor t4_power_Helmet;
	public static GenericArmor t4_power_Chestplate;
	public static GenericArmor t4_power_Leggings;
	public static GenericArmor t4_power_Boots;

	public static GenericArmor t2_beret;

	/**
	 * SHIELDS
	 */
	public static GenericShield riot_shield;
	public static GenericShield ballistic_shield;
	public static GenericShield advanced_shield;

	public static void createItems() {
		t1_combat_Helmet = new GenericArmor("t1_combat_helmet", T1_COMBAT, "t1_combat", EquipmentSlot.HEAD).setRepairMats(IRON_INGOT, HEAVY_CLOTH, 0.5f, 2).setKnockbackResistance(0.05f); //0 for helmet
		t1_combat_Chestplate = new GenericArmor("t1_combat_chestplate", T1_COMBAT, "t1_combat", EquipmentSlot.CHEST).setRepairMats(IRON_INGOT, HEAVY_CLOTH, 0.5f, 4).setKnockbackResistance(0.20f); // 1 for chestplate
		t1_combat_Leggings = new GenericArmor("t1_combat_leggings", T1_COMBAT, "t1_combat", EquipmentSlot.LEGS).setRepairMats(IRON_INGOT, HEAVY_CLOTH, 1.0f / 3.0f, 3).setKnockbackResistance(0.10f); // 2 for leggings
		t1_combat_Boots = new GenericArmor("t1_combat_boots", T1_COMBAT, "t1_combat", EquipmentSlot.FEET).setRepairMats(IRON_INGOT, HEAVY_CLOTH, 0.5f, 2).setKnockbackResistance(0.05f); // 3 for boots

		String[] t1_scout_textures = { "t1_scout", "t1_scout_forest", "t1_scout_snow", "t1_scout_black" };
		t1_scout_Helmet = new GenericArmorMultiCamo("t1_scout_helmet", T1_SCOUT, t1_scout_textures, EquipmentSlot.HEAD).setSpeedBoni(0.125f, 0.02f).setRepairMats(null, HEAVY_CLOTH, 0f, 2); //0 for helmet
		t1_scout_Chestplate = new GenericArmorMultiCamo("t1_scout_chestplate", T1_SCOUT, t1_scout_textures, EquipmentSlot.CHEST).setSpeedBoni(0.125f, 0.02f).setRepairMats(null, HEAVY_CLOTH, 0f, 4); // 1 for chestplate
		t1_scout_Leggings = new GenericArmorMultiCamo("t1_scout_leggings", T1_SCOUT, t1_scout_textures, EquipmentSlot.LEGS).setSpeedBoni(0.125f, 0.02f).setRepairMats(null, HEAVY_CLOTH, 0f, 3); // 2 for leggings
		t1_scout_Boots = new GenericArmorMultiCamo("t1_scout_boots", T1_SCOUT, t1_scout_textures, EquipmentSlot.FEET).setSpeedBoni(0.125f, 0.1f).setFallProtection(0.2f, 1.0f).setRepairMats(null, HEAVY_CLOTH, 0f, 2); // 3 for boots

		//T1 Bob
		String[] t1_miner_textures = { "t1_miner", "t1_miner_red", "t1_miner_green", "t1_miner_black" };
		t1_miner_Helmet = new GenericArmorMultiCamo("t1_miner_helmet", T1_MINER, t1_miner_textures, EquipmentSlot.HEAD).setCamoNameSuffix("helmet").setSpeedBoni(0.08f, 0.00f).setMiningBoni(0.05f).setRepairMats(IRON_INGOT, HEAVY_CLOTH, 0.5f, 2); //0 for helmet
		t1_miner_Chestplate = new GenericArmorMultiCamo("t1_miner_chestplate", T1_MINER, t1_miner_textures, EquipmentSlot.CHEST).setSpeedBoni(0.08f, 0.0f).setMiningBoni(0.05f).setRepairMats(IRON_INGOT, HEAVY_CLOTH, 0.5f, 4); // 1 for chestplate
		t1_miner_Leggings = new GenericArmorMultiCamo("t1_miner_leggings", T1_MINER, t1_miner_textures, EquipmentSlot.LEGS).setSpeedBoni(0.08f, 0.0f).setMiningBoni(0.05f).setRepairMats(IRON_INGOT, HEAVY_CLOTH, 1.0f / 3.0f, 2); // 2 for leggings
		t1_miner_Boots = new GenericArmorMultiCamo("t1_miner_boots", T1_MINER, t1_miner_textures, EquipmentSlot.FEET).setSpeedBoni(0.08f, 0.1f).setMiningBoni(0.05f).setFallProtection(0.2f, 1.0f).setRepairMats(IRON_INGOT, HEAVY_CLOTH, 0.5f, 2); // 3 for boots

		String[] steam_armor_textures = { "steam_armor", "steam_armor_rusty", "steam_armor_iron", "steam_armor_hazard" };
		ResourceLocation steamPlate = new ResourceLocation(Techguns.MODID, "platecopper");
		steam_Helmet = ((PoweredArmor) new PoweredArmor("steam_helmet", T1_STEAM, steam_armor_textures, EquipmentSlot.HEAD, ArmorPowerType.STEAM, 10).setMiningBoni(0.05f, 0.0f).setHealthBonus(1, 0).setSpeedBoni(0.05f, 0.03f, 0.0f, 0.0f)).setRADResistance(0.5f).setRepairMats(STEAMARMOR_PLATE, STEAMARMOR_PLATE, 1.0f, 2).setArmorModel(ARMORMODEL_STEAM_ARMOR_2, false, steamPlate).setHideFaceslot(true).setKnockbackResistance(0.15f).setUseRenderHack();
		steam_Chestplate = ((PoweredArmor) new PoweredArmor("steam_chestplate", T1_STEAM, steam_armor_textures, EquipmentSlot.CHEST, ArmorPowerType.STEAM, 3600).setSpeedBoni(0.05f, 0.03f, 0.0f, 0.0f).setMiningBoni(0.05f, 0.0f).setHealthBonus(2, 0).setBattery(TGItems.COMPRESSED_AIR_TANK).setEmptyBattery(TGItems.COMPRESSED_AIR_TANK_EMPTY)).setRADResistance(0.5f).setRepairMats(STEAMARMOR_PLATE, STEAMARMOR_PLATE, 1.0f, 4).setArmorModel(ARMORMODEL_STEAM_ARMOR_0, false, steamPlate).setHideBackslot(true).setHideGloveslot(true).setKnockbackResistance(0.30f).setUseRenderHack(); // 1 for chestplate
		steam_Leggings = ((PoweredArmor) new PoweredArmor("steam_leggings", T1_STEAM, steam_armor_textures, EquipmentSlot.LEGS, ArmorPowerType.STEAM, 10).setSpeedBoni(0.05f, 0.03f, 0f, 0f).setMiningBoni(0.05f, 0f).setHealthBonus(1, 0)).setRADResistance(0.5f).setRepairMats(STEAMARMOR_PLATE, STEAMARMOR_PLATE, 1.0f, 3).setArmorModel(ARMORMODEL_STEAM_ARMOR_1, false, steamPlate).setKnockbackResistance(0.20f).setUseRenderHack(); // 2 for leggings
		steam_Boots = ((PoweredArmor) new PoweredArmor("steam_boots", T1_STEAM, steam_armor_textures, EquipmentSlot.FEET, ArmorPowerType.STEAM, 10).setSpeedBoni(0.05f, 0.15f, 0, 0).setMiningBoni(0.05f, 0).setFallProtection(0.2f, 1.0f, 0, 0).setHealthBonus(1, 0).setStepAssist(1.0f, 0)).setRADResistance(0.5f).setRepairMats(STEAMARMOR_PLATE, STEAMARMOR_PLATE, 1.0f, 2).setArmorModel(ARMORMODEL_STEAM_ARMOR_0, false, steamPlate).setKnockbackResistance(0.15f).setUseRenderHack(); // 3 for boots

		String[] hazmat_textures = { "hazmatsuit", "hazmatsuit_grey", "hazmatsuit_orange", "hazmatsuit_blue" };
		hazmat_Helmet = new GenericArmorMultiCamo("hazmat_helmet", T2_HAZMAT, hazmat_textures, EquipmentSlot.HEAD).setSpeedBoni(0.0f, 0.00f).setMiningBoni(0.0f).setRADResistance(1f).setRepairMats(null, PROTECTIVE_FIBER, 0f, 2); //0 for helmet
		hazmat_Chestplate = new GenericArmorMultiCamo("hazmat_chestplate", T2_HAZMAT, hazmat_textures, EquipmentSlot.CHEST).setSpeedBoni(0.0f, 0.0f).setMiningBoni(0.0f).setRADResistance(1f).setRepairMats(null, PROTECTIVE_FIBER, 0f, 4); // 1 for chestplate
		hazmat_Leggings = new GenericArmorMultiCamo("hazmat_leggings", T2_HAZMAT, hazmat_textures, EquipmentSlot.LEGS).setSpeedBoni(0.0f, 0.0f).setMiningBoni(0.0f).setRADResistance(1f).setRepairMats(null, PROTECTIVE_FIBER, 0f, 3); // 2 for leggings
		hazmat_Boots = new GenericArmorMultiCamo("hazmat_boots", T2_HAZMAT, hazmat_textures, EquipmentSlot.FEET).setSpeedBoni(0.0f, 0.0f).setMiningBoni(0.0f).setRADResistance(1f).setFallProtection(0.1f, 0.5f).setRepairMats(null, PROTECTIVE_FIBER, 0f, 2); // 3 for boots

		//T2 Combat Armor
		String[] t2_combat_textures = { "t2_combat", "t2_combat_wood", "t2_combat_desert", "t2_combat_arctic", "t2_combat_swat", "t2_combat_security" };
		t2_combat_Helmet = new GenericArmorMultiCamo("t2_combat_helmet", T2_COMBAT, t2_combat_textures, EquipmentSlot.HEAD).setSpeedBoni(0.1f, 0.0f).setRepairMats(INGOT_OBSIDIAN_STEEL, HEAVY_CLOTH, 0.5f, 2).setKnockbackResistance(0.10f); //0 for helmet
		t2_combat_Chestplate = new GenericArmorMultiCamo("t2_combat_chestplate", T2_COMBAT, t2_combat_textures, EquipmentSlot.CHEST).setSpeedBoni(0.1f, 0.0f).setRepairMats(INGOT_OBSIDIAN_STEEL, HEAVY_CLOTH, 0.5f, 4).setKnockbackResistance(0.25f); // 1 for chestplate
		t2_combat_Leggings = new GenericArmorMultiCamo("t2_combat_leggings", T2_COMBAT, t2_combat_textures, EquipmentSlot.LEGS).setSpeedBoni(0.1f, 0.0f).setRepairMats(INGOT_OBSIDIAN_STEEL, HEAVY_CLOTH, 1.0f / 3.0f, 3).setKnockbackResistance(0.15f); // 2 for leggings
		t2_combat_Boots = new GenericArmorMultiCamo("t2_combat_boots", T2_COMBAT, t2_combat_textures, EquipmentSlot.FEET).setSpeedBoni(0.1f, 0.1f).setRepairMats(INGOT_OBSIDIAN_STEEL, HEAVY_CLOTH, 0.5f, 2).setKnockbackResistance(0.10f); // 3 for boots

		//T2 Commando Armor
		t2_commando_Helmet = new GenericArmor("t2_commando_helmet", T2_COMMANDO, "t2_commando", EquipmentSlot.HEAD).setSpeedBoni(0.10f, 0.f).setRepairMats(INGOT_OBSIDIAN_STEEL, RUBBER_BAR, 0.5f, 2).setRADResistance(0.25f).setMiningBoniWater(1.25f).setGunBonus(0.05f).setKnockbackResistance(0.05f).setOxygenGear(1.0f); //0 for helmet
		t2_commando_Chestplate = new GenericArmor("t2_commando_chestplate", T2_COMMANDO, "t2_commando", EquipmentSlot.CHEST).setSpeedBoni(0.10f, 0.f).setRepairMats(INGOT_OBSIDIAN_STEEL, RUBBER_BAR, 0.5f, 4).setRADResistance(0.25f).setMiningBoniWater(1.25f).setGunBonus(0.05f).setKnockbackResistance(0.20f); // 1 for chestplate
		t2_commando_Leggings = new GenericArmor("t2_commando_leggings", T2_COMMANDO, "t2_commando", EquipmentSlot.LEGS).setSpeedBoni(0.10f, 0.f).setRepairMats(INGOT_OBSIDIAN_STEEL, RUBBER_BAR, 1.0f / 3.0f, 3).setRADResistance(0.25f).setMiningBoniWater(1.25f).setGunBonus(0.05f).setKnockbackResistance(0.10f); // 2 for leggings
		t2_commando_Boots = new GenericArmor("t2_commando_boots", T2_COMMANDO, "t2_commando", EquipmentSlot.FEET).setSpeedBoni(0.10f, 0.1f).setRepairMats(INGOT_OBSIDIAN_STEEL, RUBBER_BAR, 0.5f, 2).setRADResistance(0.25f).setMiningBoniWater(1.25f).setGunBonus(0.05f).setKnockbackResistance(0.05f).setFallProtection(0.2f, 1.0f); // 3 for boots

		//T2.5 Riot Gear Armor
		String[] t2_riot_textures = { "t2_riot" };
		t2_riot_Helmet = new GenericArmorMultiCamo("t2_riot_helmet", T2_RIOT, t2_riot_textures, EquipmentSlot.HEAD).setSpeedBoni(0.12f, 0.f).setRepairMats(INGOT_OBSIDIAN_STEEL, TREATED_LEATHER, 0.5f, 2).setRADResistance(0.5f).setKnockbackResistance(0.125f).setArmorModel(TGArmors.ARMORMODEL_COAT_0, true); //0 for helmet
		t2_riot_Chestplate = new GenericArmorMultiCamo("t2_riot_chestplate", T2_RIOT, t2_riot_textures, EquipmentSlot.CHEST).setSpeedBoni(0.12f, 0.f).setRepairMats(INGOT_OBSIDIAN_STEEL, TREATED_LEATHER, 0.5f, 4).setRADResistance(0.5f).setKnockbackResistance(0.275f).setArmorModel(TGArmors.ARMORMODEL_COAT_1, true); // 1 for chestplate
		t2_riot_Leggings = new GenericArmorMultiCamo("t2_riot_leggings", T2_RIOT, t2_riot_textures, EquipmentSlot.LEGS).setSpeedBoni(0.13f, 0.f).setRepairMats(INGOT_OBSIDIAN_STEEL, TREATED_LEATHER, 1.0f / 3.0f, 3).setRADResistance(0.5f).setKnockbackResistance(0.175f).setArmorModel(TGArmors.ARMORMODEL_COAT_2, true); // 2 for leggings
		t2_riot_Boots = new GenericArmorMultiCamo("t2_riot_boots", T2_RIOT, t2_riot_textures, EquipmentSlot.FEET).setSpeedBoni(0.13f, 0.15f).setRepairMats(INGOT_OBSIDIAN_STEEL, TREATED_LEATHER, 0.5f, 2).setRADResistance(0.5f).setKnockbackResistance(0.125f).setArmorModel(TGArmors.ARMORMODEL_COAT_3, true); // 3 for boots

		//T3 Combat Armor
		String[] t3_combat_textures = { "t3_combat", "t3_combat_silver", "t3_combat_green" };
		t3_combat_Helmet = new GenericArmorMultiCamo("t3_combat_helmet", T3_COMBAT, t3_combat_textures, EquipmentSlot.HEAD).setSpeedBoni(0.1f, 0.0f).setRepairMats(PLATE_CARBON, CARBON_FIBERS, 0.5f, 2).setRADResistance(0.25f).setKnockbackResistance(0.15f); //0 for helmet
		t3_combat_Chestplate = new GenericArmorMultiCamo("t3_combat_chestplate", T3_COMBAT, t3_combat_textures, EquipmentSlot.CHEST).setSpeedBoni(0.1f, 0.0f).setRepairMats(PLATE_CARBON, CARBON_FIBERS, 0.5f, 4).setRADResistance(0.25f).setKnockbackResistance(0.30f); // 1 for chestplate
		t3_combat_Leggings = new GenericArmorMultiCamo("t3_combat_leggings", T3_COMBAT, t3_combat_textures, EquipmentSlot.LEGS).setSpeedBoni(0.1f, 0.0f).setRepairMats(PLATE_CARBON, CARBON_FIBERS, 1.0f / 3.0f, 3).setRADResistance(0.25f).setKnockbackResistance(0.20f); // 2 for leggings
		t3_combat_Boots = new GenericArmorMultiCamo("t3_combat_boots", T3_COMBAT, t3_combat_textures, EquipmentSlot.FEET).setSpeedBoni(0.1f, 0.1f).setRepairMats(PLATE_CARBON, CARBON_FIBERS, 0.5f, 2).setRADResistance(0.25f).setKnockbackResistance(0.15f); // 3 for boots

		//T3 power armor
		String[] t3_power_armor_textures = { "powerarmor", "powerarmor_dark", "powerarmor_dark2" };
		ResourceLocation pwrplate = new ResourceLocation(Techguns.MODID, "platesteel");
		t3_power_Helmet = ((PoweredArmor) new PoweredArmor("t3_power_helmet", T3_POWER, t3_power_armor_textures, EquipmentSlot.HEAD, ArmorPowerType.RF, 10).setMiningBoni(0.05f, 0.0f).setHealthBonus(1, 0).setSpeedBoni(0.05f, 0.03f, 0.0f, 0.0f)).setRADResistance(0.75f).setRepairMats(POWER_ARMOR_PLATING, WIRE_GOLD, 0.5f, 2).setArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_2, false, pwrplate).setHideFaceslot(true).setKnockbackResistance(0.20f).setUseRenderHack(); //0 for helmet
		t3_power_Chestplate = ((PoweredArmor) new PoweredArmor("t3_power_chestplate", T3_POWER, t3_power_armor_textures, EquipmentSlot.CHEST, ArmorPowerType.RF, 3600).setSpeedBoni(0.05f, 0.03f, 0.0f, 0.0f).setMiningBoni(0.05f, 0.0f).setHealthBonus(2, 0).setBattery(TGItems.ENERGY_CELL).setEmptyBattery(TGItems.ENERGY_CELL_EMPTY)).setRADResistance(0.75f).setRepairMats(POWER_ARMOR_PLATING, WIRE_GOLD, 0.5f, 4).setArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_0, false, pwrplate).setHideBackslot(false).setHideGloveslot(true).setKnockbackResistance(0.35f).setUseRenderHack(); // 1 for chestplate
		t3_power_Leggings = ((PoweredArmor) new PoweredArmor("t3_power_leggings", T3_POWER, t3_power_armor_textures, EquipmentSlot.LEGS, ArmorPowerType.RF, 10).setSpeedBoni(0.05f, 0.03f, 0f, 0f).setMiningBoni(0.05f, 0f).setHealthBonus(1, 0)).setRADResistance(0.75f).setRepairMats(POWER_ARMOR_PLATING, WIRE_GOLD, 1.0f / 3.0f, 3).setArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_1, false, pwrplate).setKnockbackResistance(0.25f).setUseRenderHack(); // 2 for leggings
		t3_power_Boots = ((PoweredArmor) new PoweredArmor("t3_power_boots", T3_POWER, t3_power_armor_textures, EquipmentSlot.FEET, ArmorPowerType.RF, 10).setSpeedBoni(0.05f, 0.15f, 0, 0).setMiningBoni(0.05f, 0).setFallProtection(0.2f, 1.0f, 0, 0).setHealthBonus(1, 0).setStepAssist(1.0f, 0)).setRADResistance(0.75f).setRepairMats(POWER_ARMOR_PLATING, WIRE_GOLD, 0.5f, 2).setArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_0, false, pwrplate).setKnockbackResistance(0.20f).setUseRenderHack(); // 3 for boots

		//T3 Mining Armor
		String[] t3_miner_textures = { "hevsuit", "hevsuit_silver", "hevsuit_black" };
		t3_miner_Helmet = ((PoweredArmor) new PoweredArmor("t3_miner_helmet", T3_MINER, t3_miner_textures, EquipmentSlot.HEAD, ArmorPowerType.RF, 10).setSpeedBoni(0.1f, 0.3f, 0, 0).setWaterspeedBonus(1.25f, 0).setMiningBoni(0.1f, 0).setMiningBoniWater(1.25f, 0).setOxygenGear(1.0f, 0.0f)).setHideFaceslot(true).setRADResistance(1f).setRepairMats(PLATE_CARBON, CIRCUIT_BOARD_ELITE, 0.5f, 2).setKnockbackResistance(0.10f); //0 for helmet
		t3_miner_Chestplate = ((PoweredArmor) new PoweredArmor("t3_miner_chestplate", T3_MINER, t3_miner_textures, EquipmentSlot.CHEST, ArmorPowerType.RF, 10000).setBattery(TGItems.ENERGY_CELL).setEmptyBattery(TGItems.ENERGY_CELL_EMPTY).setSpeedBoni(0.1f, 0.03f, 0, 0).setWaterspeedBonus(1.25f, 0).setMiningBoni(0.1f, 0).setMiningBoniWater(1.25f, 0).setCoolingSystem(1.0f, 0)).setRADResistance(1f).setRepairMats(PLATE_CARBON, CIRCUIT_BOARD_ELITE, 0.5f, 4).setHideGloveslot(true).setKnockbackResistance(0.25f); // 1 for chestplate
		t3_miner_Leggings = ((PoweredArmor) new PoweredArmor("t3_miner_leggings", T3_MINER, t3_miner_textures, EquipmentSlot.LEGS, ArmorPowerType.RF, 10).setSpeedBoni(0.1f, 0.03f, 0, 0).setWaterspeedBonus(1.25f, 0).setMiningBoni(0.1f, 0).setMiningBoniWater(1.25f, 0)).setRADResistance(1f).setRepairMats(PLATE_CARBON, CIRCUIT_BOARD_ELITE, 1.0f / 3.0f, 3).setKnockbackResistance(0.15f); // 2 for leggings
		t3_miner_Boots = ((PoweredArmor) new PoweredArmor("t3_miner_boots", T3_MINER, t3_miner_textures, EquipmentSlot.FEET, ArmorPowerType.RF, 10).setSpeedBoni(0.1f, 0.15f, 0, 0).setWaterspeedBonus(1.25f, 0).setMiningBoni(0.1f, 0).setMiningBoniWater(1.25f, 0).setStepAssist(1.0f, 0).setFallProtection(0.5f, 3.0f, 0, 0)).setRADResistance(1f).setRepairMats(PLATE_CARBON, CIRCUIT_BOARD_ELITE, 0.5f, 2).setKnockbackResistance(0.10f); // 3 for boots

		String[] exo_armor_textures = { "t3_exo", "t3_exo_silver", "t3_exo_green" };
		t3_exo_Helmet = ((PoweredArmor) new PoweredArmor("t3_exo_helmet", T3_EXO, exo_armor_textures, EquipmentSlot.HEAD, ArmorPowerType.RF, 10).setSpeedBoni(0.2f, 0.1f, 0.0f, 0.0f).setMiningBoni(0.075f, 0.0f)).setRADResistance(0.5f).setRepairMats(PLATE_CARBON, CARBON_FIBERS, 0.5f, 2).setArmorModel(TGArmors.ARMORMODEL_EXO_SUIT_0, true).setHideFaceslot(false).setKnockbackResistance(0.15f); //0 for helmet
		t3_exo_Chestplate = ((PoweredArmor) new PoweredArmor("t3_exo_chestplate", T3_EXO, exo_armor_textures, EquipmentSlot.CHEST, ArmorPowerType.RF, 7200).setSpeedBoni(0.2f, 0.1f, 0.0f, 0.0f).setMiningBoni(0.075f, 0.0f).setBattery(TGItems.ENERGY_CELL).setEmptyBattery(TGItems.ENERGY_CELL_EMPTY)).setRADResistance(0.5f).setRepairMats(PLATE_CARBON, CARBON_FIBERS, 0.5f, 4).setArmorModel(TGArmors.ARMORMODEL_EXO_SUIT_2, true).setHideBackslot(false).setKnockbackResistance(0.30f); // 1 for chestplate
		t3_exo_Leggings = ((PoweredArmor) new PoweredArmor("t3_exo_leggings", T3_EXO, exo_armor_textures, EquipmentSlot.LEGS, ArmorPowerType.RF, 10).setSpeedBoni(0.2f, 0.1f, 0f, 0f).setMiningBoni(0.075f, 0f)).setRADResistance(0.5f).setRepairMats(PLATE_CARBON, CARBON_FIBERS, 1.0f / 3.0f, 3).setArmorModel(TGArmors.ARMORMODEL_EXO_SUIT_1, true).setKnockbackResistance(0.20f); // 2 for leggings
		t3_exo_Boots = ((PoweredArmor) new PoweredArmor("t3_exo_boots", T3_EXO, exo_armor_textures, EquipmentSlot.FEET, ArmorPowerType.RF, 10).setSpeedBoni(0.2f, 0.3f, 0, 0).setMiningBoni(0.075f, 0).setFallProtection(0.5f, 5.0f, 0, 0).setStepAssist(1.0f, 0)).setRADResistance(0.5f).setRepairMats(PLATE_CARBON, CARBON_FIBERS, 0.5f, 2).setArmorModel(TGArmors.ARMORMODEL_EXO_SUIT_2, true).setKnockbackResistance(0.15f); // 3 for boots

		String[] t2_beret_textures = { "beret_texture", "beret_texture_black", "beret_texture_green" };
		t2_beret = new GenericArmorMultiCamo("t2_beret", T2_BERET, t2_beret_textures, EquipmentSlot.HEAD).setSpeedBoni(0.1f, 0.0f).setRepairMats(HEAVY_CLOTH, HEAVY_CLOTH, 1.0f, 2).setArmorModel(TGArmors.ARMORMODEL_BERET_0, false); //0 for helmet

		String[] t4_praetor_textures = { "t4_praetor" };
		t4_praetor_Helmet = ((PoweredArmor) new PoweredArmor("t4_praetor_helmet", T4_PRAETOR, t4_praetor_textures, EquipmentSlot.HEAD, ArmorPowerType.RF, 10).setSpeedBoni(0.15f, 0.3f, 0, 0).setWaterspeedBonus(1.25f, 0).setMiningBoni(0.15f, 0).setMiningBoniWater(1.25f, 0).setOxygenGear(1.0f, 0.0f)).setHideFaceslot(true).setRADResistance(1f).setRepairMats(PLATE_CARBON, CIRCUIT_BOARD_ELITE, 0.5f, 2).setKnockbackResistance(0.20f); //0 for helmet
		t4_praetor_Chestplate = ((PoweredArmor) new PoweredArmor("t4_praetor_chestplate", T4_PRAETOR, t4_praetor_textures, EquipmentSlot.CHEST, ArmorPowerType.RF, 10000).setBattery(TGItems.ENERGY_CELL).setEmptyBattery(TGItems.ENERGY_CELL_EMPTY).setSpeedBoni(0.15f, 0.03f, 0, 0).setWaterspeedBonus(1.25f, 0).setMiningBoni(0.15f, 0).setMiningBoniWater(1.25f, 0).setCoolingSystem(1.0f, 0)).setRADResistance(1f).setRepairMats(PLATE_CARBON, CIRCUIT_BOARD_ELITE, 0.5f, 4).setHideGloveslot(true).setKnockbackResistance(0.35f); // 1 for chestplate
		t4_praetor_Leggings = ((PoweredArmor) new PoweredArmor("t4_praetor_leggings", T4_PRAETOR, t4_praetor_textures, EquipmentSlot.LEGS, ArmorPowerType.RF, 10).setSpeedBoni(0.15f, 0.03f, 0, 0).setWaterspeedBonus(1.25f, 0).setMiningBoni(0.15f, 0).setMiningBoniWater(1.25f, 0)).setRADResistance(1f).setRepairMats(PLATE_CARBON, CIRCUIT_BOARD_ELITE, 1.0f / 3.0f, 3).setKnockbackResistance(0.25f); // 2 for leggings
		t4_praetor_Boots = ((PoweredArmor) new PoweredArmor("t4_praetor_boots", T4_PRAETOR, t4_praetor_textures, EquipmentSlot.FEET, ArmorPowerType.RF, 10).setSpeedBoni(0.15f, 0.15f, 0, 0).setWaterspeedBonus(1.25f, 0).setMiningBoni(0.15f, 0).setMiningBoniWater(1.25f, 0).setStepAssist(1.0f, 0).setFallProtection(0.5f, 3.0f, 0, 0)).setRADResistance(1f).setRepairMats(PLATE_CARBON, CIRCUIT_BOARD_ELITE, 0.5f, 2).setKnockbackResistance(0.20f); // 3 for boots

		//T4 power armor mk2
		String[] t4_power_armor_textures = { "powerarmor_mk2_darkgrey", "powerarmor_mk2", "powerarmor_mk2_black", "powerarmor_mk2_white" };
		ResourceLocation pwrplate2 = new ResourceLocation(Techguns.MODID, "platesteel");
		t4_power_Helmet = ((PoweredArmor) new PoweredArmor("t4_power_helmet", T4_POWER, t4_power_armor_textures, EquipmentSlot.HEAD, ArmorPowerType.RF, 10).setMiningBoni(0.1f, 0.0f).setHealthBonus(2, 0).setSpeedBoni(0.05f, 0.03f, 0.0f, 0.0f)).setRADResistance(1.25f).setRepairMats(POWER_ARMOR_PLATING, WIRE_GOLD, 0.5f, 2).setArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_MK2_2, false, pwrplate2).setHideFaceslot(true).setKnockbackResistance(0.20f).setUseRenderHack(); //0 for helmet
		t4_power_Chestplate = ((PoweredArmor) new PoweredArmor("t4_power_chestplate", T4_POWER, t4_power_armor_textures, EquipmentSlot.CHEST, ArmorPowerType.RF, 3600).setSpeedBoni(0.05f, 0.03f, 0.0f, 0.0f).setMiningBoni(0.1f, 0.0f).setHealthBonus(3, 0).setBattery(TGItems.ENERGY_CELL).setEmptyBattery(TGItems.ENERGY_CELL_EMPTY)).setRADResistance(1.25f).setRepairMats(POWER_ARMOR_PLATING, WIRE_GOLD, 0.5f, 4).setArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_MK2_0, false, pwrplate).setHideBackslot(false).setHideGloveslot(true).setKnockbackResistance(0.35f).setUseRenderHack(); // 1 for chestplate
		t4_power_Leggings = ((PoweredArmor) new PoweredArmor("t4_power_leggings", T4_POWER, t4_power_armor_textures, EquipmentSlot.LEGS, ArmorPowerType.RF, 10).setSpeedBoni(0.05f, 0.03f, 0f, 0f).setMiningBoni(0.1f, 0f).setHealthBonus(2, 0)).setRADResistance(1.25f).setRepairMats(POWER_ARMOR_PLATING, WIRE_GOLD, 1.0f / 3.0f, 3).setArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_MK2_1, false, pwrplate2).setKnockbackResistance(0.25f).setUseRenderHack(); // 2 for leggings
		t4_power_Boots = ((PoweredArmor) new PoweredArmor("t4_power_boots", T4_POWER, t4_power_armor_textures, EquipmentSlot.FEET, ArmorPowerType.RF, 10).setSpeedBoni(0.05f, 0.15f, 0, 0).setMiningBoni(0.1f, 0).setFallProtection(0.4f, 1.0f, 0, 0).setHealthBonus(2, 0).setStepAssist(1.0f, 0)).setRADResistance(1.25f).setRepairMats(POWER_ARMOR_PLATING, WIRE_GOLD, 0.5f, 2).setArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_MK2_0, false, pwrplate2).setKnockbackResistance(0.20f).setUseRenderHack(); // 3 for boots

		riot_shield = new GenericShield("riot_shield", 1200, 4).setRepairMat(tg("ingotsteel"), 3);
		ballistic_shield = new GenericShield("ballistic_shield", 2400, 4).setRepairMat(tg("plateobsidiansteel"), 3);
		advanced_shield = new GenericShield("advanced_shield", 3200, 3).setRepairMat(tg("platecarbon"), 3);

		ShieldStats.SHIELD_STATS.put(Items.SHIELD, ShieldStats.VANILLA_SHIELD);
		ShieldStats.SHIELD_STATS.put(riot_shield, ShieldStats.RIOT_SHIELD_STATS);
		ShieldStats.SHIELD_STATS.put(ballistic_shield, ShieldStats.BALLISTIC_SHIELD_STATS);
		ShieldStats.SHIELD_STATS.put(advanced_shield, ShieldStats.ADVANCED_SHIELD_STATS);
	}
}
