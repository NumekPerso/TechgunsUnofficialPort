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
 * Shaped recipe copying the NBT of the gun used in the recipe to the result (golden revolver).
 * Recipe type techguns:copy_nbt
 */
public class ShapedOreRecipeCopyNBTFactory {

	public static final String COPY_NBT_RECIPE = "copy_nbt";

	public static final RecipeSerializer<ShapedOreRecipeCopyNBT> SERIALIZER = new TGRecipeSerializers.Shaped<>(ShapedOreRecipeCopyNBT::new);

	public static class ShapedOreRecipeCopyNBT extends ShapedRecipe {

		public ShapedOreRecipeCopyNBT(ResourceLocation id, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, boolean showNotification) {
			super(id, group, category, width, height, ingredients, result, showNotification);
		}

		@Override
		public ItemStack assemble(CraftingContainer var1, RegistryAccess registryAccess) {
			int slot = 0;

			for (int i = 0; i < var1.getContainerSize(); i++) {
				if (!var1.getItem(i).isEmpty()) {
					if (var1.getItem(i).getItem() instanceof GenericGun) {
						slot = i;
					}
				}
			}

			CompoundTag tags = var1.getItem(slot).getTag();
			CompoundTag newTags = null;
			if (tags != null) {
				newTags = tags.copy();
			}
			ItemStack out = super.assemble(var1, registryAccess);
			if (newTags != null) {
				out.setTag(newTags);
			}
			return out;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return SERIALIZER;
		}
	}
}
