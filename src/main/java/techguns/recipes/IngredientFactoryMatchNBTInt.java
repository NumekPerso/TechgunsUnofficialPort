package techguns.recipes;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

/**
 * Ingredient type techguns:itemstack_has_nbt_int (1.12 "itemstackHasNBTInt"):
 * {"type":"techguns:itemstack_has_nbt_int","item":"techguns:chainsaw","key":"miningHead","value":0}
 */
public class IngredientFactoryMatchNBTInt implements IIngredientSerializer<IngredientHasNBTTag> {

	public static final IngredientFactoryMatchNBTInt INSTANCE = new IngredientFactoryMatchNBTInt();

	@Override
	public IngredientHasNBTTag parse(JsonObject json) {
		String key = GsonHelper.getAsString(json, "key");
		int value = GsonHelper.getAsInt(json, "value");
		ItemStack stack = new ItemStack(CraftingHelper.getItem(GsonHelper.getAsString(json, "item"), true));
		return new IngredientHasNBTTag(key, value, stack);
	}

	@Override
	public IngredientHasNBTTag parse(FriendlyByteBuf buffer) {
		ItemStack stack = buffer.readItem();
		String key = buffer.readUtf();
		int value = buffer.readVarInt();
		return new IngredientHasNBTTag(key, value, stack);
	}

	@Override
	public void write(FriendlyByteBuf buffer, IngredientHasNBTTag ingredient) {
		buffer.writeItem(ingredient.stack);
		buffer.writeUtf(ingredient.key);
		buffer.writeVarInt(((Number) ingredient.value).intValue());
	}

}
