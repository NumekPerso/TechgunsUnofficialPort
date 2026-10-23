package techguns.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import techguns.items.guns.GenericGun;

/**
 * Shaped recipe, the result gun gets the sum of the ammo of all guns used in the recipe (grim reaper).
 * Recipe type techguns:ammo_sum_recipe
 */
public class AmmoSumRecipeFactory {

	public static final String AMMO_SUM_RECIPE = "ammo_sum_recipe";

	public static final RecipeSerializer<AmmoSumRecipe> SERIALIZER = new TGRecipeSerializers.Shaped<>(AmmoSumRecipe::new);

	public static class AmmoSumRecipe extends ShapedRecipe {
		public AmmoSumRecipe(ResourceLocation id, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, boolean showNotification) {
			super(id, group, category, width, height, ingredients, result, showNotification);
		}

		@Override
		public ItemStack assemble(CraftingContainer var1, RegistryAccess registryAccess) {
			int ammoSum = 0;

			for (int i = 0; i < var1.getContainerSize(); i++) {
				if (!var1.getItem(i).isEmpty()) {
					if (var1.getItem(i).getItem() instanceof GenericGun) {
						GenericGun g = (GenericGun) var1.getItem(i).getItem();
						ammoSum += g.getCurrentAmmo(var1.getItem(i));
					}
				}
			}
			ItemStack out = super.assemble(var1, registryAccess);
			if (out.getItem() instanceof GenericGun g) {
				g.initTags(out);
			}
			CompoundTag tags = out.getOrCreateTag();
			tags.putShort("ammo", (short) ammoSum);

			return out;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return SERIALIZER;
		}
	}
}
