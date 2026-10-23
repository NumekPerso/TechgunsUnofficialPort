package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import techguns.TGMachines;
import techguns.Techguns;
import techguns.gui.AmmoPressGui;
import techguns.gui.BlastFurnaceGui;
import techguns.gui.ChargingStationGui;
import techguns.gui.ChemLabGui;
import techguns.gui.FabricatorGui;
import techguns.gui.GrinderGui;
import techguns.gui.MetalPressGui;
import techguns.gui.ReactionChamberGui;
import techguns.gui.UpgradeBenchGui;
import techguns.gui.containers.AmmoPressContainer;
import techguns.gui.containers.BlastFurnaceContainer;
import techguns.gui.containers.ChargingStationContainer;
import techguns.gui.containers.FabricatorContainer;
import techguns.gui.containers.GrinderContainer;
import techguns.gui.containers.MetalPressContainer;
import techguns.recipes.AmmoSwitchRecipeFactory.AmmoSwitchRecipe;
import techguns.recipes.MiningToolUpgradeHeadRecipeFactory.MiningToolUpgradeRecipe;

/**
 * JEI integration (1.12 plugins/jei). Only loaded by JEI (client side), never referenced by Techguns code:
 * Techguns runs without JEI.
 */
@JeiPlugin
public class TGJeiPlugin implements IModPlugin {

	public static final ResourceLocation UID = new ResourceLocation(Techguns.MODID, "jei_plugin");

	public static final RecipeType<AmmoPressJeiRecipe> AMMO_PRESS = RecipeType.create(Techguns.MODID, "ammopress", AmmoPressJeiRecipe.class);
	public static final RecipeType<MetalPressJeiRecipe> METAL_PRESS = RecipeType.create(Techguns.MODID, "metalpress", MetalPressJeiRecipe.class);
	public static final RecipeType<ChemLabJeiRecipe> CHEM_LAB = RecipeType.create(Techguns.MODID, "chemlab", ChemLabJeiRecipe.class);
	public static final RecipeType<FabricatorJeiRecipe> FABRICATOR = RecipeType.create(Techguns.MODID, "fabricator", FabricatorJeiRecipe.class);
	public static final RecipeType<CamoBenchJeiRecipe> CAMO_BENCH = RecipeType.create(Techguns.MODID, "camobench", CamoBenchJeiRecipe.class);
	public static final RecipeType<ChargingStationJeiRecipe> CHARGING_STATION = RecipeType.create(Techguns.MODID, "chargingstation", ChargingStationJeiRecipe.class);
	public static final RecipeType<ReactionChamberJeiRecipe> REACTION_CHAMBER = RecipeType.create(Techguns.MODID, "reactionchamber", ReactionChamberJeiRecipe.class);
	public static final RecipeType<OreDrillJeiRecipe> ORE_DRILL = RecipeType.create(Techguns.MODID, "oredrill", OreDrillJeiRecipe.class);
	public static final RecipeType<BlastFurnaceJeiRecipe> BLAST_FURNACE = RecipeType.create(Techguns.MODID, "blastfurnace", BlastFurnaceJeiRecipe.class);
	public static final RecipeType<GrinderJeiRecipe> GRINDER = RecipeType.create(Techguns.MODID, "grinder", GrinderJeiRecipe.class);
	public static final RecipeType<UpgradeBenchJeiRecipe> UPGRADE_BENCH = RecipeType.create(Techguns.MODID, "upgradebench", UpgradeBenchJeiRecipe.class);

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

		registry.addRecipeCategories(
				new AmmoPressRecipeCategory(guiHelper),
				new MetalPressJeiRecipeCategory(guiHelper),
				new ChemLabJeiRecipeCategory(guiHelper),
				new FabricatorJeiRecipeCategory(guiHelper),
				new CamoBenchJeiRecipeCategory(guiHelper),
				new ChargingStationJeiRecipeCategory(guiHelper),
				new ReactionChamberJeiRecipeCategory(guiHelper),
				new OreDrillJeiRecipeCategory(guiHelper),
				new BlastFurnaceJeiRecipeCategory(guiHelper),
				new GrinderRecipeCategory(guiHelper),
				new UpgradeBenchJeiRecipeCategory(guiHelper));
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addCategoryExtension(MiningToolUpgradeRecipe.class, MiningToolUpgradeRecipeWrapper::new);
		registration.getCraftingCategory().addCategoryExtension(AmmoSwitchRecipe.class, AmmoSwitchRecipeWrapper::new);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

		registry.addRecipes(AMMO_PRESS, AmmoPressJeiRecipe.getRecipes());
		registry.addRecipes(METAL_PRESS, MetalPressJeiRecipe.getRecipes());
		registry.addRecipes(CHEM_LAB, ChemLabJeiRecipe.getRecipes());
		registry.addRecipes(FABRICATOR, FabricatorJeiRecipe.getRecipes());
		registry.addRecipes(CAMO_BENCH, CamoBenchJeiRecipe.getRecipes());
		registry.addRecipes(CHARGING_STATION, ChargingStationJeiRecipe.getRecipes());
		registry.addRecipes(REACTION_CHAMBER, ReactionChamberJeiRecipe.getRecipes(guiHelper));
		registry.addRecipes(ORE_DRILL, OreDrillJeiRecipe.getRecipes(getFurnaceFuels(registry)));
		registry.addRecipes(BLAST_FURNACE, BlastFurnaceJeiRecipe.getRecipes());
		registry.addRecipes(GRINDER, GrinderJeiRecipe.getRecipes());
		registry.addRecipes(UPGRADE_BENCH, UpgradeBenchJeiRecipe.getRecipes());
	}

	/**
	 * 1.12 IIngredientRegistry.getFuels()
	 */
	protected static List<ItemStack> getFurnaceFuels(IRecipeRegistration registry) {
		List<ItemStack> fuels = new ArrayList<>();
		for (ItemStack stack : registry.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK)) {
			if (ForgeHooks.getBurnTime(stack, net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0) {
				fuels.add(stack);
			}
		}
		return fuels;
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		registry.addRecipeClickArea(AmmoPressGui.class, 119, 36, 19, 22, AMMO_PRESS);
		registry.addRecipeClickArea(MetalPressGui.class, 119, 36, 19, 22, METAL_PRESS);
		registry.addRecipeClickArea(ChemLabGui.class, 80, 15, 52, 30, CHEM_LAB);
		registry.addRecipeClickArea(FabricatorGui.class, 20, 52, 96, 12, FABRICATOR);
		//1.12: no click area for the ore drill and the camo bench
		registry.addRecipeClickArea(ChargingStationGui.class, 38, 18, 28, 12, CHARGING_STATION);
		registry.addRecipeClickArea(ReactionChamberGui.class, 66, 60, 102, 14, REACTION_CHAMBER);
		registry.addRecipeClickArea(BlastFurnaceGui.class, 18, 52, 90, 12, BLAST_FURNACE);
		registry.addRecipeClickArea(GrinderGui.class, 30, 38, 43, 23, GRINDER);
		registry.addRecipeClickArea(UpgradeBenchGui.class, 73, 40, 38, 30, UPGRADE_BENCH);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
		//(new in the port) "+" button moving the inputs from the player inventory (36 slots after the machine slots)
		registry.addRecipeTransferHandler(AmmoPressContainer.class, TGMachines.AMMO_PRESS_CONTAINER, AMMO_PRESS, 0, 3, 5, 36);
		registry.addRecipeTransferHandler(MetalPressContainer.class, TGMachines.METAL_PRESS_CONTAINER, METAL_PRESS, 0, 2, 4, 36);
		registry.addRecipeTransferHandler(FabricatorContainer.class, TGMachines.FABRICATOR_CONTAINER, FABRICATOR, 0, 4, 6, 36);
		registry.addRecipeTransferHandler(ChargingStationContainer.class, TGMachines.CHARGING_STATION_CONTAINER, CHARGING_STATION, 0, 1, 3, 36);
		registry.addRecipeTransferHandler(BlastFurnaceContainer.class, TGMachines.BLAST_FURNACE_CONTAINER, BLAST_FURNACE, 0, 2, 4, 36);
		registry.addRecipeTransferHandler(GrinderContainer.class, TGMachines.GRINDER_CONTAINER, GRINDER, 0, 1, 11, 36);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(TGMachines.AMMO_PRESS, AMMO_PRESS);
		registry.addRecipeCatalyst(TGMachines.METAL_PRESS, METAL_PRESS);
		registry.addRecipeCatalyst(TGMachines.CHEM_LAB, CHEM_LAB);
		registry.addRecipeCatalyst(TGMachines.FABRICATOR_CONTROLLER, FABRICATOR);
		registry.addRecipeCatalyst(TGMachines.CAMO_BENCH, CAMO_BENCH);
		registry.addRecipeCatalyst(TGMachines.CHARGING_STATION, CHARGING_STATION);
		registry.addRecipeCatalyst(TGMachines.REACTIONCHAMBER_CONTROLLER, REACTION_CHAMBER);
		registry.addRecipeCatalyst(TGMachines.OREDRILL_CONTROLLER, ORE_DRILL);
		registry.addRecipeCatalyst(TGMachines.BLAST_FURNACE, BLAST_FURNACE);
		registry.addRecipeCatalyst(TGMachines.GRINDER, GRINDER);
		registry.addRecipeCatalyst(TGMachines.ARMOR_BENCH, UPGRADE_BENCH);
	}

}
