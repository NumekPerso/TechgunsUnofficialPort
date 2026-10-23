package techguns;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.RegisterEvent;
import techguns.recipes.AmmoSumRecipeFactory;
import techguns.recipes.AmmoSwitchRecipeFactory;
import techguns.recipes.IngredientFactoryMatchNBTInt;
import techguns.recipes.MiningToolUpgradeHeadRecipeFactory;
import techguns.recipes.OreDictIngredientElectrumOrGold;
import techguns.recipes.OreDictIngredientHardenedGlass;
import techguns.recipes.ShapedOreRecipeCopyNBTFactory;

/**
 * Crafting recipe serializers and ingredient types (1.12 techguns.recipes factories, _factories.json).
 * The recipes themselves are datapack jsons in data/techguns/recipes (converted once from the 1.12 recipe jsons).
 */
public class TGRecipes {

	public static final String AMMO_CHANGE_CRAFTING = "ammo_change_crafting";

	/**
	 * Registers recipe serializers / ingredient serializers (check event.getRegistryKey())
	 */
	public static void register(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {
			event.register(Registries.RECIPE_SERIALIZER, helper -> {
				helper.register(rl(MiningToolUpgradeHeadRecipeFactory.MINING_TOOL_UPGRADE_RECIPE), MiningToolUpgradeHeadRecipeFactory.SERIALIZER);
				helper.register(rl(AmmoSumRecipeFactory.AMMO_SUM_RECIPE), AmmoSumRecipeFactory.SERIALIZER);
				helper.register(rl(AMMO_CHANGE_CRAFTING), AmmoSwitchRecipeFactory.SERIALIZER);
				helper.register(rl(ShapedOreRecipeCopyNBTFactory.COPY_NBT_RECIPE), ShapedOreRecipeCopyNBTFactory.SERIALIZER);
			});

			// Forge 47 has no ingredient serializer registry, ingredient types are registered in CraftingHelper
			CraftingHelper.register(rl("electrum_or_gold"), OreDictIngredientElectrumOrGold.SERIALIZER);
			CraftingHelper.register(rl("hardened_glass_or_glass"), OreDictIngredientHardenedGlass.SERIALIZER);
			CraftingHelper.register(rl("itemstack_has_nbt_int"), IngredientFactoryMatchNBTInt.INSTANCE);
		}
	}

	private static ResourceLocation rl(String path) {
		return new ResourceLocation(Techguns.MODID, path);
	}
}
