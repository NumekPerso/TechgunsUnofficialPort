package techguns.recipes;

import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * Serializers of the Techguns crafting recipe types (1.12 IRecipeFactory): the json / network format is the vanilla
 * shaped / shapeless format, the recipe is then rebuilt as the Techguns subclass (like 1.12 ShapedOreRecipe.factory).
 */
public class TGRecipeSerializers {

	@FunctionalInterface
	public interface ShapedFactory<T extends ShapedRecipe> {
		T create(ResourceLocation id, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, boolean showNotification);
	}

	@FunctionalInterface
	public interface ShapelessFactory<T extends ShapelessRecipe> {
		T create(ResourceLocation id, String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients);
	}

	public static class Shaped<T extends ShapedRecipe> implements RecipeSerializer<T> {
		protected final ShapedFactory<T> factory;

		public Shaped(ShapedFactory<T> factory) {
			this.factory = factory;
		}

		protected T convert(ResourceLocation id, ShapedRecipe r) {
			return factory.create(id, r.getGroup(), r.category(), r.getWidth(), r.getHeight(), r.getIngredients(), r.getResultItem(RegistryAccess.EMPTY), r.showNotification());
		}

		@Override
		public T fromJson(ResourceLocation id, JsonObject json) {
			return convert(id, RecipeSerializer.SHAPED_RECIPE.fromJson(id, json));
		}

		@Override
		public T fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
			return convert(id, RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buffer));
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, T recipe) {
			RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
		}
	}

	public static class Shapeless<T extends ShapelessRecipe> implements RecipeSerializer<T> {
		protected final ShapelessFactory<T> factory;

		public Shapeless(ShapelessFactory<T> factory) {
			this.factory = factory;
		}

		protected T convert(ResourceLocation id, ShapelessRecipe r) {
			return factory.create(id, r.getGroup(), r.category(), r.getResultItem(RegistryAccess.EMPTY), r.getIngredients());
		}

		@Override
		public T fromJson(ResourceLocation id, JsonObject json) {
			return convert(id, RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json));
		}

		@Override
		public T fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
			return convert(id, RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buffer));
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, T recipe) {
			RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
		}
	}
}
