package techguns.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

/**
 * Ingredient using a preferred item tag when it has items (another mod adds them), the fallback tag otherwise
 * (1.12 OreDictIngredientElectrumOrGold / OreDictIngredientHardenedGlass checked OreDictionary.doesOreNameExist).
 * The tag is resolved lazily: tags are bound after the recipes are parsed.
 */
public class TagOrFallbackIngredient extends AbstractIngredient {

	protected final TagKey<Item> preferred;
	protected final TagKey<Item> fallback;
	protected final Serializer serializer;

	public TagOrFallbackIngredient(Serializer serializer) {
		super(Stream.of());
		this.serializer = serializer;
		this.preferred = serializer.preferred;
		this.fallback = serializer.fallback;
	}

	protected static boolean hasItems(TagKey<Item> tag) {
		return BuiltInRegistries.ITEM.getTag(tag).map(set -> set.size() > 0).orElse(false);
	}

	public TagKey<Item> getActiveTag() {
		return hasItems(preferred) ? preferred : fallback;
	}

	@Override
	public ItemStack[] getItems() {
		List<ItemStack> list = new ArrayList<>();
		for (Holder<Item> h : BuiltInRegistries.ITEM.getTagOrEmpty(getActiveTag())) {
			list.add(new ItemStack(h));
		}
		return list.toArray(new ItemStack[0]);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && !stack.isEmpty() && stack.is(getActiveTag());
	}

	@Override
	public IntList getStackingIds() {
		ItemStack[] items = getItems();
		IntList ids = new IntArrayList(items.length);
		for (ItemStack s : items) {
			ids.add(StackedContents.getStackingIndex(s));
		}
		ids.sort(null);
		return ids;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return serializer;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(serializer).toString());
		return json;
	}

	public static class Serializer implements IIngredientSerializer<TagOrFallbackIngredient> {
		protected final TagKey<Item> preferred;
		protected final TagKey<Item> fallback;

		public Serializer(String preferred, String fallback) {
			this.preferred = TagKey.create(Registries.ITEM, new ResourceLocation(preferred));
			this.fallback = TagKey.create(Registries.ITEM, new ResourceLocation(fallback));
		}

		@Override
		public TagOrFallbackIngredient parse(FriendlyByteBuf buffer) {
			return new TagOrFallbackIngredient(this);
		}

		@Override
		public TagOrFallbackIngredient parse(JsonObject json) {
			return new TagOrFallbackIngredient(this);
		}

		@Override
		public void write(FriendlyByteBuf buffer, TagOrFallbackIngredient ingredient) {
		}
	}
}
