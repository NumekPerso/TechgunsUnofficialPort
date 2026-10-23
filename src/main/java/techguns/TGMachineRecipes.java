package techguns;

import java.util.ArrayList;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import techguns.tileentities.operation.ChemLabRecipes;
import techguns.tileentities.operation.ReactionBeamFocus;
import techguns.tileentities.operation.ReactionChamberRecipe;
import techguns.tileentities.operation.ReactionChamberRecipe.RiskType;

import techguns.tileentities.operation.AmmoPressBuildPlans;
import techguns.tileentities.operation.ChargingStationRecipe;
import techguns.util.ItemStackOreDict;

/**
 * Class that contains recipes for machines. Called once in the common setup (items are registered).
 * 1.12 ore dictionary names are item tags: "ingotSteel" -> "forge:ingots/steel", "gunpowder" -> "forge:gunpowder".
 */
public class TGMachineRecipes {

	private static boolean added = false;

	public static void addRecipes() {
		if (added) {
			return;
		}
		added = true;

		//AMMO PRESS
		ArrayList<String> metal2 = new ArrayList<String>();
		ArrayList<String> metal1 = new ArrayList<String>();
		ArrayList<String> powders = new ArrayList<String>();
		metal2.add("forge:ingots/copper");
		metal2.add("forge:ingots/tin");
		metal2.add("forge:ingots/iron");
		metal2.add("forge:ingots/bronze");
		metal1.add("forge:ingots/steel");
		metal1.add("forge:ingots/lead");
		powders.add("forge:gunpowder");
		AmmoPressBuildPlans.init(metal1, metal2, powders);

		/**
		 * CHARGING STATION
		 */
		ChargingStationRecipe.addRecipe(new ItemStackOreDict(TGItems.ENERGY_CELL_EMPTY, 1), TGItems.newStack(TGItems.ENERGY_CELL, 1), 50000);
		ChargingStationRecipe.addRecipe(new ItemStackOreDict(TGItems.REDSTONE_BATTERY_EMPTY, 1), TGItems.newStack(TGItems.REDSTONE_BATTERY, 1), 20000);
		// ---- lot E (simple machines) ----
		//METAL PRESS
		//MetalPressRecipes.addRecipe("ingotSteel", new ItemStack(Blocks.OBSIDIAN,1), TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL,1), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:ingots/tin", "forge:ingots/tin", TGItems.newStack(TGItems.PLATE_TIN, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:ingots/copper", "forge:ingots/copper", TGItems.newStack(TGItems.PLATE_COPPER, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:ingots/bronze", "forge:ingots/bronze", TGItems.newStack(TGItems.PLATE_BRONZE, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:ingots/iron", "forge:ingots/iron", TGItems.newStack(TGItems.PLATE_IRON, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:ingots/steel", "forge:ingots/steel", TGItems.newStack(TGItems.PLATE_STEEL, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:ingots/lead", "forge:ingots/lead", TGItems.newStack(TGItems.PLATE_LEAD, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:plates/iron", new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.FLINT, 1), TGItems.newStack(TGItems.MECHANICAL_PARTS_IRON, 1), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:plates/obsidian_steel", "forge:gems/quartz", TGItems.newStack(TGItems.MECHANICAL_PARTS_OBSIDIAN_STEEL, 1), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:plates/carbon", new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BLAZE_ROD), TGItems.newStack(TGItems.MECHANICAL_PARTS_CARBON, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:plates/copper", "forge:plates/copper", TGItems.newStack(TGItems.WIRE_COPPER, 8), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:fibers/carbon", "forge:fibers/carbon", TGItems.newStack(TGItems.PLATE_CARBON, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:plates/obsidian_steel", TGItems.newStack(TGItems.TGX, 1), TGItems.newStack(TGItems.ADVANCED_ROUNDS, 16), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:ingots/obsidian_steel", "forge:ingots/obsidian_steel", TGItems.newStack(TGItems.PLATE_OBSIDIAN_STEEL, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:plates/steel", "forge:plates/bronze", TGItems.newStack(TGItems.STEAMARMOR_PLATE, 1), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:ingots/gold", "forge:ingots/gold", TGItems.newStack(TGItems.WIRE_GOLD, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:plates/iron", new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.TNT, 1), TGItems.newStack(TGItems.GRENADE_40MM, 16), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:ingots/titanium", "forge:ingots/titanium", TGItems.newStack(TGItems.PLATE_TITANIUM, 2), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe("forge:plates/obsidian_steel", "forge:plates/titanium", TGItems.newStack(TGItems.GAUSSRIFLE_SLUGS, 4), true);
		techguns.tileentities.operation.MetalPressRecipes.addRecipe(TGItems.newStack(TGItems.SNIPER_ROUNDS_INCENDIARY, 1), TGItems.newStack(TGItems.TGX, 1), TGItems.newStack(TGItems.SNIPER_ROUNDS_EXPLOSIVE, 1), true);

		/**
		 * Camo Bench (1.12 metadata colors -> the 16 color items)
		 */
		techguns.tileentities.operation.CamoBenchRecipes.addRecipe(techguns.tileentities.operation.CamoBenchRecipes.colored("wool"));
		techguns.tileentities.operation.CamoBenchRecipes.addRecipe(techguns.tileentities.operation.CamoBenchRecipes.colored("concrete"));
		techguns.tileentities.operation.CamoBenchRecipes.addRecipe(techguns.tileentities.operation.CamoBenchRecipes.colored("concrete_powder"));
		techguns.tileentities.operation.CamoBenchRecipes.addRecipe(techguns.tileentities.operation.CamoBenchRecipes.colored("terracotta"));
		techguns.tileentities.operation.CamoBenchRecipes.addRecipe(techguns.tileentities.operation.CamoBenchRecipes.colored("stained_glass"));
		techguns.tileentities.operation.CamoBenchRecipes.addRecipe(techguns.tileentities.operation.CamoBenchRecipes.colored("stained_glass_pane"));
		//CamoBenchRecipes.addRecipe(new CamoBenchRecipe(Blocks.BED));
		techguns.tileentities.operation.CamoBenchRecipes.addRecipe(techguns.tileentities.operation.CamoBenchRecipes.colored("banner"));
		techguns.tileentities.operation.CamoBenchRecipes.addRecipe(techguns.tileentities.operation.CamoBenchRecipes.colored("carpet"));

		//Techguns camo blocks (incl. the lamps, 1.12 TGLampCamoBenchRecipe): their items switch the camo themselves
		TGItems.ITEMLIST.forEach(i -> {
			if (i instanceof techguns.blocks.ItemBlockCamoChangeable<?> camoitem) {
				techguns.tileentities.operation.CamoBenchRecipes.addRecipe(new techguns.tileentities.operation.CamoBenchRecipes.CamoBenchRecipe(i, camoitem));
			}
		});

		/**
		 * Upgrade bench recipes
		 */
		//UpgradeBenchRecipes.addRecipe(TGItems.PLATE_CARBON, Enchantments.PROTECTION, 4);
		techguns.tileentities.operation.UpgradeBenchRecipes.addRecipe(TGItems.UPGRADE_PROTECTION_1, net.minecraft.world.item.enchantment.Enchantments.ALL_DAMAGE_PROTECTION, 1);
		techguns.tileentities.operation.UpgradeBenchRecipes.addRecipe(TGItems.UPGRADE_PROJECTILE_PROTECTION_1, net.minecraft.world.item.enchantment.Enchantments.PROJECTILE_PROTECTION, 1);
		techguns.tileentities.operation.UpgradeBenchRecipes.addRecipe(TGItems.UPGRADE_BLAST_PROTECTION_1, net.minecraft.world.item.enchantment.Enchantments.BLAST_PROTECTION, 1);

		techguns.tileentities.operation.UpgradeBenchRecipes.addRecipe(TGItems.UPGRADE_PROTECTION_2, net.minecraft.world.item.enchantment.Enchantments.ALL_DAMAGE_PROTECTION, 2);
		techguns.tileentities.operation.UpgradeBenchRecipes.addRecipe(TGItems.UPGRADE_PROJECTILE_PROTECTION_2, net.minecraft.world.item.enchantment.Enchantments.PROJECTILE_PROTECTION, 2);
		techguns.tileentities.operation.UpgradeBenchRecipes.addRecipe(TGItems.UPGRADE_BLAST_PROTECTION_2, net.minecraft.world.item.enchantment.Enchantments.BLAST_PROTECTION, 2);

		techguns.tileentities.operation.UpgradeBenchRecipes.addRecipe(TGItems.UPGRADE_PROTECTION_3, net.minecraft.world.item.enchantment.Enchantments.ALL_DAMAGE_PROTECTION, 3);
		techguns.tileentities.operation.UpgradeBenchRecipes.addRecipe(TGItems.UPGRADE_PROJECTILE_PROTECTION_3, net.minecraft.world.item.enchantment.Enchantments.PROJECTILE_PROTECTION, 3);
		techguns.tileentities.operation.UpgradeBenchRecipes.addRecipe(TGItems.UPGRADE_BLAST_PROTECTION_3, net.minecraft.world.item.enchantment.Enchantments.BLAST_PROTECTION, 3);

		/**
		 * Blast Furnace
		 */
		techguns.tileentities.operation.BlastFurnaceRecipes.addRecipe(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 4), new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COAL, 1), TGItems.newStack(TGItems.INGOT_STEEL, 4), 10, 800);
		//1.12 coal meta 1
		techguns.tileentities.operation.BlastFurnaceRecipes.addRecipe(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 4), new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.CHARCOAL, 1), TGItems.newStack(TGItems.INGOT_STEEL, 4), 10, 800);
		techguns.tileentities.operation.BlastFurnaceRecipes.addRecipe("forge:ingots/steel", 1, new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.OBSIDIAN, 1), TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, 1), 10, 200);
		techguns.tileentities.operation.BlastFurnaceRecipes.addRecipe("forge:ingots/copper", 3, "forge:ingots/tin", 1, TGItems.newStack(TGItems.INGOT_BRONZE, 4), 10, 100);

		/**
		 * Grinder recipes
		 */
		{
			//1.12 GOLD_OR_ELECTRUM: first "ingotElectrum" if another mod added it. Tags are not bound yet in the common setup, gold is used
			net.minecraft.world.item.ItemStack GOLD_OR_ELECTRUM = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GOLD_INGOT, 1);
			net.minecraft.world.item.ItemStack[] BARREL_STONE = { new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.COBBLESTONE, 3) };
			net.minecraft.world.item.ItemStack[] BARREL_OBSI = { TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, 3) };
			net.minecraft.world.item.Item REDSTONE = net.minecraft.world.item.Items.REDSTONE;
			net.minecraft.world.item.Item GUNPOWDER = net.minecraft.world.item.Items.GUNPOWDER;
			net.minecraft.world.item.Item BLAZE_POWDER = net.minecraft.world.item.Items.BLAZE_POWDER;

			techguns.tileentities.operation.GrinderRecipes.addGenericArmorRecipes();

			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.handcannon, BARREL_STONE, techguns.tileentities.operation.GrinderRecipes.ironAndWood(0, 1, 4));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.sawedoff, techguns.tileentities.operation.GrinderRecipes.ironAndWood(4, 1, 0), new net.minecraft.world.item.ItemStack[] { new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.FLINT, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.revolver, techguns.tileentities.operation.GrinderRecipes.ironAndWood(3, 1, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.goldenrevolver, techguns.tileentities.operation.GrinderRecipes.ironAndWood(3, 1, 0), new net.minecraft.world.item.ItemStack[] { new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GOLD_INGOT, 4) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.thompson, techguns.tileentities.operation.GrinderRecipes.ironAndWood(4, 1, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.ak47, BARREL_OBSI, techguns.tileentities.operation.GrinderRecipes.ironAndWood(4, 1, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.boltaction, BARREL_OBSI, techguns.tileentities.operation.GrinderRecipes.ironAndWood(4, 1, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.m4, BARREL_OBSI, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(2, 2, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.m4_infiltrator, BARREL_OBSI, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(3, 2, 0), new net.minecraft.world.item.ItemStack[] { new net.minecraft.world.item.ItemStack(REDSTONE, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.pistol, BARREL_OBSI, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(0, 1, 0), techguns.tileentities.operation.GrinderRecipes.ironAndWood(2, 0, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.combatshotgun, BARREL_OBSI, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(2, 2, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.mac10, BARREL_OBSI, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(1, 0, 5), techguns.tileentities.operation.GrinderRecipes.ironAndWood(1, 0, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.flamethrower, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(0, 2, 0), techguns.tileentities.operation.GrinderRecipes.ironAndWood(2, 0, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.rocketlauncher, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, 6) }, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(1, 0, 5));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.grimreaper, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, 32), TGItems.newStack(TGItems.CIRCUIT_BOARD_ELITE, 1), TGItems.newStack(TGItems.CARBON_FIBERS, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.grenadelauncher, BARREL_OBSI, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(2, 2, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.aug, BARREL_OBSI, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(2, 2, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.netherblaster, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, 4), TGItems.newStack(TGItems.CYBERNETIC_PARTS, 2) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.biogun, techguns.tileentities.operation.GrinderRecipes.steelAndPlastic(2, 2, 0), new net.minecraft.world.item.ItemStack[] { new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.GLASS, 2) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.teslagun, techguns.tileentities.operation.GrinderRecipes.carbonPlasticAndRedstone(3, 2, 2));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.lmg, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, 6), TGItems.newStack(TGItems.INGOT_STEEL, 4), TGItems.newStack(TGItems.PLASTIC_SHEET, 2) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.minigun, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, 20), TGItems.newStack(TGItems.INGOT_STEEL, 2), TGItems.newStack(TGItems.ELECTRIC_ENGINE, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.as50, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, 9), TGItems.newStack(TGItems.PLASTIC_SHEET, 2), new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.vector, techguns.tileentities.operation.GrinderRecipes.obsiAndPlastic(5, 2));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.scar, techguns.tileentities.operation.GrinderRecipes.obsiAndPlastic(5, 2));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.lasergun, techguns.tileentities.operation.GrinderRecipes.obsiAndPlastic(2, 2), new net.minecraft.world.item.ItemStack[] { new net.minecraft.world.item.ItemStack(REDSTONE, 20), TGItems.newStack(GOLD_OR_ELECTRUM, 3) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.blasterrifle, techguns.tileentities.operation.GrinderRecipes.carbonPlasticAndRedstone(3, 1, 20), new net.minecraft.world.item.ItemStack[] { TGItems.newStack(GOLD_OR_ELECTRUM, 3) });
			//TODO (1.12): BlasterShotgun NoREcipe
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.sonicshotgun, techguns.tileentities.operation.GrinderRecipes.carbonPlasticAndRedstone(3, 2, 2), new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.INGOT_TITANIUM, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.pdw, techguns.tileentities.operation.GrinderRecipes.carbonPlasticAndRedstone(6, 1, 0), techguns.tileentities.operation.GrinderRecipes.obsiAndPlastic(1, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.pulserifle, techguns.tileentities.operation.GrinderRecipes.carbonPlasticAndRedstone(6, 1, 0), techguns.tileentities.operation.GrinderRecipes.obsiAndPlastic(1, 0));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.mibgun, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(GOLD_OR_ELECTRUM, 3), TGItems.newStack(TGItems.INGOT_TITANIUM, 1), new net.minecraft.world.item.ItemStack(REDSTONE, 20) });
			//TODO (1.12): AlienBlaster norecipe
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.powerhammer, techguns.tileentities.operation.GrinderRecipes.ironAndWood(4, 0, 0), new net.minecraft.world.item.ItemStack[] { new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.FLINT, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.chainsaw, techguns.tileentities.operation.GrinderRecipes.ironAndWood(5, 0, 0), new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.PLASTIC_SHEET, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.nucleardeathray, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.CARBON_FIBERS, 6), TGItems.newStack(TGItems.INGOT_LEAD, 3), TGItems.newStack(TGItems.MECHANICAL_PARTS_CARBON, 1), TGItems.newStack(TGItems.CIRCUIT_BOARD_ELITE, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.gaussrifle, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.CARBON_FIBERS, 9), TGItems.newStack(TGItems.INGOT_TITANIUM, 1), TGItems.newStack(TGItems.PLASTIC_SHEET, 1), TGItems.newStack(TGItems.CIRCUIT_BOARD_ELITE, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.guidedmissilelauncher, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, 8), TGItems.newStack(TGItems.WIRE_COPPER, 1) });
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.miningdrill, techguns.tileentities.operation.GrinderRecipes.obsiAndPlastic(6, 2));
			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGuns.tfg, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.INGOT_TITANIUM, 5), TGItems.newStack(TGItems.INGOT_LEAD, 4), TGItems.newStack(TGItems.CARBON_FIBERS, 4) });

			techguns.tileentities.operation.GrinderRecipes.addRecipe(TGItems.PLATE_CARBON, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.CARBON_FIBERS, 1) });

			//Ammo crush
			techguns.tileentities.operation.GrinderRecipes.addRecipeChance(TGItems.RIFLE_ROUNDS, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.NUGGET_LEAD, 1), TGItems.newStack(TGItems.NUGGET_COPPER, 2), new net.minecraft.world.item.ItemStack(GUNPOWDER) }, new double[] { 1d, 1d, 0.125d });
			techguns.tileentities.operation.GrinderRecipes.addRecipeChance(TGItems.PISTOL_ROUNDS, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.NUGGET_LEAD, 1), TGItems.newStack(TGItems.NUGGET_COPPER, 1), new net.minecraft.world.item.ItemStack(GUNPOWDER) }, new double[] { 0.75d, 1.5d, 1.0d / 12.0d });
			techguns.tileentities.operation.GrinderRecipes.addRecipeChance(TGItems.SNIPER_ROUNDS, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.NUGGET_LEAD, 2), TGItems.newStack(TGItems.NUGGET_COPPER, 4), new net.minecraft.world.item.ItemStack(GUNPOWDER) }, new double[] { 1d, 1d, 0.25d });
			techguns.tileentities.operation.GrinderRecipes.addRecipeChance(TGItems.SHOTGUN_ROUNDS, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.NUGGET_LEAD, 1), TGItems.newStack(TGItems.NUGGET_COPPER, 1), new net.minecraft.world.item.ItemStack(GUNPOWDER) }, new double[] { 0.5d, 1d, 0.0625d });

			techguns.tileentities.operation.GrinderRecipes.addRecipeChance(TGItems.RIFLE_ROUNDS_INCENDIARY, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.NUGGET_LEAD, 1), TGItems.newStack(TGItems.NUGGET_COPPER, 2), new net.minecraft.world.item.ItemStack(GUNPOWDER), new net.minecraft.world.item.ItemStack(BLAZE_POWDER) }, new double[] { 1d, 1d, 0.125d, 0.125d });
			techguns.tileentities.operation.GrinderRecipes.addRecipeChance(TGItems.PISTOL_ROUNDS_INCENDIARY, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.NUGGET_LEAD, 1), TGItems.newStack(TGItems.NUGGET_COPPER, 1), new net.minecraft.world.item.ItemStack(GUNPOWDER), new net.minecraft.world.item.ItemStack(BLAZE_POWDER) }, new double[] { 0.75d, 1.5d, 1.0d / 12.0d, 0.125d });
			techguns.tileentities.operation.GrinderRecipes.addRecipeChance(TGItems.SNIPER_ROUNDS_INCENDIARY, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.NUGGET_LEAD, 2), TGItems.newStack(TGItems.NUGGET_COPPER, 4), new net.minecraft.world.item.ItemStack(GUNPOWDER), new net.minecraft.world.item.ItemStack(BLAZE_POWDER) }, new double[] { 1d, 1d, 0.25d, 0.125d });
			techguns.tileentities.operation.GrinderRecipes.addRecipeChance(TGItems.SHOTGUN_ROUNDS_INCENDIARY, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.NUGGET_LEAD, 1), TGItems.newStack(TGItems.NUGGET_COPPER, 1), new net.minecraft.world.item.ItemStack(GUNPOWDER), new net.minecraft.world.item.ItemStack(BLAZE_POWDER) }, new double[] { 0.5d, 1d, 0.0625d, 0.125d });

			techguns.tileentities.operation.GrinderRecipes.addRecipeChance(TGItems.SNIPER_ROUNDS_EXPLOSIVE, new net.minecraft.world.item.ItemStack[] { TGItems.newStack(TGItems.NUGGET_LEAD, 2), TGItems.newStack(TGItems.NUGGET_COPPER, 4), new net.minecraft.world.item.ItemStack(GUNPOWDER), new net.minecraft.world.item.ItemStack(BLAZE_POWDER), TGItems.newStack(TGItems.TGX, 1) }, new double[] { 1d, 1d, 0.25d, 0.125d, 0.5d });
		}

		// ---- end lot E ----
		// ---- lot F (fluids, chem lab, reaction chamber) ----
		addChemLabAndReactionChamberRecipes();
		// ---- end lot F ----
		// ---- lot G (multiblocks, explosives, turret) ----
		/**
		 * FABRICATOR
		 */
		techguns.tileentities.operation.FabricatorRecipe.initSlotItems();
		techguns.tileentities.operation.FabricatorRecipe.addRecipe(new ItemStackOreDict("forge:ingots/gold"), 1, techguns.tileentities.operation.FabricatorRecipe.copperWires, 1, techguns.tileentities.operation.FabricatorRecipe.redstone, 3, techguns.tileentities.operation.FabricatorRecipe.plastic, 1, TGItems.newStack(TGItems.ENERGY_CELL_EMPTY, 1), 1);
		techguns.tileentities.operation.FabricatorRecipe.addRecipe(new ItemStackOreDict(new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.SOUL_SAND, 1)), 1, techguns.tileentities.operation.FabricatorRecipe.goldWires, 1, techguns.tileentities.operation.FabricatorRecipe.redstone, 1, techguns.tileentities.operation.FabricatorRecipe.plastic, 1, TGItems.newStack(TGItems.CYBERNETIC_PARTS, 1), 1);
		techguns.tileentities.operation.FabricatorRecipe.addRecipe(new ItemStackOreDict("forge:ingots/titanium"), 2, techguns.tileentities.operation.FabricatorRecipe.circuit_basic, 4, techguns.tileentities.operation.FabricatorRecipe.mechanicalPartsT3, 1, techguns.tileentities.operation.FabricatorRecipe.carbonPlate, 4, TGItems.newStack(TGItems.POWER_ARMOR_PLATING, 1), 2);
		techguns.tileentities.operation.FabricatorRecipe.addRecipe(new ItemStackOreDict(TGItems.COIL, 1), 1, techguns.tileentities.operation.FabricatorRecipe.circuit_elite, 2, techguns.tileentities.operation.FabricatorRecipe.mechanicalPartsT3, 1, techguns.tileentities.operation.FabricatorRecipe.titaniumPlate, 1, TGItems.newStack(TGItems.SONIC_EMITTER, 1), 1);
		techguns.tileentities.operation.FabricatorRecipe.addRecipe(new ItemStackOreDict(TGItems.ENRICHED_URANIUM, 1), 1, techguns.tileentities.operation.FabricatorRecipe.circuit_elite, 2, techguns.tileentities.operation.FabricatorRecipe.mechanicalPartsT3, 2, techguns.tileentities.operation.FabricatorRecipe.leadPlate, 2, TGItems.newStack(TGItems.RAD_EMITTER, 1), 1);

		/**
		 * ORE CLUSTERS (1.12 Techguns.orecluster.RecipeInit)
		 */
		TGOreClusters.orecluster.RecipeInit();

		techguns.tileentities.operation.FabricatorRecipe.addRecipe(new ItemStackOreDict("forge:ingots/steel", 2), 1, techguns.tileentities.operation.FabricatorRecipe.circuit_basic, 1, techguns.tileentities.operation.FabricatorRecipe.redstone, 4, techguns.tileentities.operation.FabricatorRecipe.leadPlate, 2, TGItems.newStack(TGItems.NUCLEAR_POWERCELL_EMPTY, 1), 1);
		// ---- end lot G ----


	}

	// ---- lot F (fluids, chem lab, reaction chamber) ----
	/**
	 * 1.12 CHEM LAB and REACTION CHAMBER recipes. 1.12 chose some recipes at init depending on the ore dictionary /
	 * fluid registry (dustCoal, itemBioFuel, oils, fuels): tags are only known after the datapack load in 1.20, these
	 * recipes are all added with a runtime condition (ChemLabRecipe.setCondition) or a tag input that is empty without mods.
	 */
	private static void addChemLabAndReactionChamberRecipes() {
		TGFluids.RecipeInit();

		final net.minecraft.tags.TagKey<net.minecraft.world.item.Item> DUST_COAL = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, new net.minecraft.resources.ResourceLocation("forge", "dusts/coal"));

		//CHEM LAB
		ChemLabRecipes.addRecipe("forge:dusts/redstone", 1, "forge:dusts/coal", 1, null, 0, new FluidStack(Fluids.WATER, 250), null, new ItemStack(Items.GUNPOWDER, 1), true, 5);
		ChemLabRecipes.addRecipe("forge:dusts/redstone", 1, new ItemStack(Items.COAL), 1, null, 0, new FluidStack(Fluids.WATER, 250), null, new ItemStack(Items.GUNPOWDER, 1), true, 5)
				.setCondition(() -> new ItemStackOreDict(DUST_COAL, 1).getItemStacks().isEmpty());

		//oils (tag techguns:oil), without oil: raw rubber + coal + water
		ChemLabRecipes.addRecipe("forge:raw_rubber", 1, null, 0, null, 0, TGFluids.TAG_OIL, 500, null, TGItems.newStack(TGItems.RAW_PLASTIC, 1), false, 25)
				.setCondition(() -> !TGFluids.oils.isEmpty());
		ChemLabRecipes.addRecipe("forge:raw_rubber", 1, new ItemStack(Items.COAL, 1), 1, null, 0, new FluidStack(TGFluids.WATER, 1000), null, TGItems.newStack(TGItems.RAW_PLASTIC, 1), true, 25)
				.setCondition(() -> TGFluids.oils.isEmpty());

		ChemLabRecipes.addRecipe(TGItems.BIOMASS, 1, new ItemStack(Items.GUNPOWDER), 1, null, 0, new FluidStack(TGFluids.WATER, 1000), new FluidStack(TGFluids.ACID, 1000), null, true, 20);

		//1.12 "itemBioFuel" (only if another mod had it): item tag forge:biofuel, inert without mods
		ChemLabRecipes.addRecipe("forge:biofuel", 4, TGItems.newStack(TGItems.BIO_TANK_EMPTY, 1), 1, null, 0, new FluidStack(TGFluids.WATER, 500), null, TGItems.newStack(TGItems.BIO_TANK, 1), true, 1);
		ChemLabRecipes.addRecipe("forge:biofuel", 4, new ItemStack(Items.GUNPOWDER), 1, null, 0, new FluidStack(TGFluids.WATER, 1000), new FluidStack(TGFluids.ACID, 1000), null, true, 20);

		ItemStack fuelTank = TGItems.newStack(TGItems.FUEL_TANK, 1);
		ItemStack fuelTankEmpty = TGItems.newStack(TGItems.FUEL_TANK_EMPTY, 1);
		//fuels (tag techguns:fuel)
		ChemLabRecipes.addRecipe("forge:gunpowder", 1, "forge:gems/lapis", 1, null, 0, TGFluids.TAG_FUEL, 250, null, TGItems.newStack(TGItems.TGX, 1), true, 20)
				.setCondition(() -> !TGFluids.fuels.isEmpty());
		ChemLabRecipes.addRecipe(null, 0, null, 0, fuelTankEmpty, 1, TGFluids.TAG_FUEL, 250, null, fuelTank, false, 1)
				.setCondition(() -> !TGFluids.fuels.isEmpty());
		ChemLabRecipes.addRecipe(TGItems.newStack(TGItems.ROCKET, 1), 1, null, 0, null, 0, TGFluids.TAG_FUEL, 125, null, TGItems.newStack(TGItems.ROCKET_HIGH_VELOCITY, 1), false, 5)
				.setCondition(() -> !TGFluids.fuels.isEmpty());
		//lava recipes when there is no fuel
		java.util.function.BooleanSupplier lavaRecipes = () -> TGFluids.fuels.isEmpty() || TGFluids.keepLavaRecipesWhenFuelIsPresent;
		ChemLabRecipes.addRecipe("forge:gunpowder", 1, "forge:gems/lapis", 1, null, 0, new FluidStack(Fluids.LAVA, 500), null, TGItems.newStack(TGItems.TGX, 1), true, 20)
				.setCondition(lavaRecipes);
		ChemLabRecipes.addRecipe(fuelTankEmpty, 1, null, 0, null, 0, new FluidStack(TGFluids.LAVA, 500), null, fuelTank, false, 1)
				.setCondition(lavaRecipes);
		ChemLabRecipes.addRecipe(TGItems.newStack(TGItems.ROCKET, 1), 1, null, 0, null, 0, new FluidStack(TGFluids.LAVA, 250), null, TGItems.newStack(TGItems.ROCKET_HIGH_VELOCITY, 1), false, 5)
				.setCondition(lavaRecipes);

		ChemLabRecipes.addRecipe("forge:gems/diamond", 1, new ItemStack(Items.BLAZE_POWDER, 1), 1, null, 0, new FluidStack(TGFluids.LAVA, 1000), null, TGItems.newStack(TGItems.CARBON_FIBERS, 2), true, 25);

		//1.12 "logWood"
		ChemLabRecipes.addRecipe("minecraft:logs", 1, null, 0, null, 0, new FluidStack(TGFluids.WATER, 1000), null, TGItems.newStack(TGItems.RAW_RUBBER, 1), false, 20);

		ChemLabRecipes.addRecipe(TGItems.newStack(TGItems.BIOMASS, 1), 1, null, 0, TGItems.newStack(TGItems.BIO_TANK_EMPTY, 1), 1, new FluidStack(TGFluids.WATER, 500), null, TGItems.newStack(TGItems.BIO_TANK, 1), false, 1);

		ChemLabRecipes.addRecipe(new ItemStack(Items.COAL, 1), 1, null, 0, TGItems.newStack(TGItems.COMPRESSED_AIR_TANK_EMPTY, 1), 1, new FluidStack(TGFluids.WATER, 250), null, TGItems.newStack(TGItems.COMPRESSED_AIR_TANK, 1), true, 5);

		ChemLabRecipes.addRecipe(new ItemStack(Blocks.NETHERRACK), 1, new ItemStack(Blocks.SOUL_SAND), 1, null, 0, new FluidStack(TGFluids.LAVA, 1000), null, TGItems.newStack(TGItems.NETHER_CHARGE, 4), true, 20);

		//1.12 "oreUranium"
		ChemLabRecipes.addRecipe("forge:ores/uranium", 1, null, 0, null, 0, new FluidStack(TGFluids.ACID, 250), null, TGItems.newStack(TGItems.YELLOWCAKE, 3), false, 20);

		ChemLabRecipes.addRecipe(TGItems.ENRICHED_URANIUM, 1, null, 0, TGItems.newStack(TGItems.NUCLEAR_POWERCELL_EMPTY, 1), 1, new FluidStack(TGFluids.WATER, 1000), null, TGItems.newStack(TGItems.NUCLEAR_POWERCELL, 1), true, 40);

		if (TGFluids.MILK != null) {
			//1.12 dye meta 2 (cactus green)
			ChemLabRecipes.addRecipe(new ItemStack(Items.GREEN_DYE, 1), 1, "forge:raw_rubber", 1, null, 0, new FluidStack(TGFluids.MILK, 500), null, new ItemStack(Items.SLIME_BALL), true, 25);
		}

		ChemLabRecipes.addRecipe(new ItemStack(Items.LEATHER, 2), 2, "forge:slimeballs", 1, null, 0, new FluidStack(TGFluids.ACID, 500), null, TGItems.newStack(TGItems.TREATED_LEATHER, 2), true, 20);

		ChemLabRecipes.addRecipe(new ItemStack(Items.ROTTEN_FLESH), 1, null, 0, null, 0, new FluidStack(TGFluids.WATER, 500), null, new ItemStack(Items.LEATHER, 1), false, 15);

		//1.12 Blocks.CONCRETE meta 8 (silver = light gray)
		ChemLabRecipes.addRecipe(new ItemStack(Blocks.GRAVEL), 1, new ItemStack(Blocks.SAND), 1, null, 0, new FluidStack(TGFluids.WATER, 250), null, new ItemStack(Blocks.LIGHT_GRAY_CONCRETE, 2), false, 5);
		ChemLabRecipes.addRecipe(new ItemStack(Blocks.GRAVEL), 1, new ItemStack(Blocks.DIRT), 1, null, 0, new FluidStack(TGFluids.WATER, 250), null, new ItemStack(Blocks.CLAY, 2), false, 5);

		ChemLabRecipes.addRecipe(TGItems.RIFLE_ROUNDS, 1, new ItemStack(Items.BLAZE_POWDER), 1, null, 0, new FluidStack(TGFluids.LAVA, 250), null, TGItems.newStack(TGItems.RIFLE_ROUNDS_INCENDIARY, 1), false, 25);
		ChemLabRecipes.addRecipe(TGItems.PISTOL_ROUNDS, 2, new ItemStack(Items.BLAZE_POWDER), 1, null, 0, new FluidStack(TGFluids.LAVA, 250), null, TGItems.newStack(TGItems.PISTOL_ROUNDS_INCENDIARY, 2), false, 25);
		ChemLabRecipes.addRecipe(TGItems.SNIPER_ROUNDS, 1, new ItemStack(Items.BLAZE_POWDER), 1, null, 0, new FluidStack(TGFluids.LAVA, 250), null, TGItems.newStack(TGItems.SNIPER_ROUNDS_INCENDIARY, 1), false, 25);
		ChemLabRecipes.addRecipe(TGItems.SHOTGUN_ROUNDS, 8, new ItemStack(Items.BLAZE_POWDER), 1, null, 0, new FluidStack(TGFluids.LAVA, 250), null, TGItems.newStack(TGItems.SHOTGUN_ROUNDS_INCENDIARY, 8), false, 25);

		//lot I (radiation)
		if (TGFluids.MILK != null) {
			ChemLabRecipes.addRecipe(new ItemStack(Items.SUGAR), 4, new ItemStack(Items.GLISTERING_MELON_SLICE), 1, new ItemStack(Items.GLASS_BOTTLE), 1, new FluidStack(TGFluids.MILK, 1000), null, new ItemStack(TGItems.RAD_PILLS, 4), true, 20);
		}
		ChemLabRecipes.addRecipe(new ItemStack(Items.NETHER_WART), 1, new ItemStack(Items.GLISTERING_MELON_SLICE), 1, TGItems.newStack(TGItems.INFUSION_BAG, 1), 1, new FluidStack(TGFluids.ACID, 250), null, new ItemStack(TGItems.RAD_AWAY, 1), true, 25);

		/**
		 * REACTION CHAMBER
		 */
		//Beam focus
		ReactionBeamFocus.addBeamFocus(TGItems.RC_HEAT_RAY, 100, TGSounds.REACTION_CHAMBER_HEATRAY_WORK);
		ReactionBeamFocus.addBeamFocus(TGItems.RC_UV_EMITTER, 100, TGSounds.CHEM_LAB_WORK);

		//Recipes
		/**
		 * ID (STR), INPUT-STACK, FOCUS, FLUID_TYPE, ITEM_OUTPUTS, TICKS, REQ_COMPLETION, INTENSITY, INTENSITY_MARGIN, LIQUIDLEVEL, LIQUID_CONSUMPTION, INSTABILITY_CHANCE, RISK, RF_TICK
		 */
		ReactionChamberRecipe.addRecipe("RC_UV_WHEAT", new ItemStackOreDict(new ItemStack(Items.WHEAT_SEEDS, 1)), TGItems.RC_UV_EMITTER, () -> TGFluids.WATER, new ItemStack[] { new ItemStack(Items.WHEAT, 1), new ItemStack(Items.WHEAT_SEEDS, 2) }, 10, 5, 3, 1, 1, 1000, 0.5f, RiskType.EXPLOSION_LOW, 50000);

		ReactionChamberRecipe.addRecipe("RC_LASER_FOCUS", new ItemStackOreDict("forge:gems/diamond", 1), TGItems.RC_HEAT_RAY, () -> TGFluids.LIQUID_REDSTONE, new ItemStack[] { TGItems.newStack(TGItems.LASER_FOCUS, 1) }, 10, 5, 3, 1, 4, 4000, 0.5f, RiskType.BREAK_ITEM, 100000);

		ReactionChamberRecipe.addRecipe("RC_TITANIUM", new ItemStackOreDict(new ItemStack(TGBlocks.ORE_TITANIUM, 1)), TGItems.RC_HEAT_RAY, () -> TGFluids.ACID, new ItemStack[] { TGItems.newStack(TGItems.ORE_TITANIUM, 2), new ItemStack(Blocks.IRON_ORE, 1) }, 2, 1, 5, 0, 3, 100, 0f, RiskType.BREAK_ITEM, 25000);

		ReactionChamberRecipe.addRecipe("RC_BLAZEROD", new ItemStackOreDict(TGItems.newStack(TGItems.QUARTZ_ROD, 1)), TGItems.RC_HEAT_RAY, () -> TGFluids.LAVA, new ItemStack[] { new ItemStack(Items.BLAZE_ROD, 1) }, 5, 3, 7, 2, 4, 1000, 0.5f, RiskType.BREAK_ITEM, 250000);

		ReactionChamberRecipe.addRecipe("RC_GLOWSTONE", new ItemStackOreDict("forge:storage_blocks/redstone", 1), TGItems.RC_HEAT_RAY, () -> TGFluids.LAVA, new ItemStack[] { new ItemStack(Blocks.GLOWSTONE, 1) }, 5, 3, 7, 2, 4, 1000, 0.5f, RiskType.EXPLOSION_MEDIUM, 250000);

		ReactionChamberRecipe.addRecipe("RC_ANTIGRAV", new ItemStackOreDict(new ItemStack(Items.NETHER_STAR)), TGItems.RC_HEAT_RAY, () -> TGFluids.LIQUID_ENDER, new ItemStack[] { TGItems.newStack(TGItems.ANTI_GRAV_CORE, 1) }, 10, 7, 8, 2, 4, 4000, 1f, RiskType.EXPLOSION_LOW, 500000);

		ReactionChamberRecipe.addRecipe("RC_URANIUM", new ItemStackOreDict("forge:dusts/uranium", 1), TGItems.RC_HEAT_RAY, () -> TGFluids.WATER, new ItemStack[] { TGItems.newStack(TGItems.ENRICHED_URANIUM, 1) }, 5, 4, 7, 0, 3, 1000, 0f, RiskType.BREAK_ITEM, 250000);
	}
	// ---- end lot F ----
}
