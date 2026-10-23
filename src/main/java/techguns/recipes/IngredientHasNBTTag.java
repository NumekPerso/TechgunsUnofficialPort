package techguns.recipes;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import techguns.items.guns.GenericGun;

/**
 * Ingredient matching an item whose NBT has the given key with the given value (mining tool upgrade: miningHead=0).
 */
public class IngredientHasNBTTag extends AbstractIngredient {

	protected final ItemStack stack;
	protected final String key;
	protected final Object value;
	protected final byte id;

	public IngredientHasNBTTag(@Nonnull String key, @Nonnull Object value, @Nonnull ItemStack stack) {
		super(Stream.of(new Ingredient.ItemValue(stack)));
		this.stack = stack;
		this.key = key;
		this.value = value;
		this.id = getIdforClass(value);
	}

	@Override
	public ItemStack[] getItems() {
		ItemStack[] base = super.getItems();
		int size = base.length;
		ItemStack[] matchingStacks = new ItemStack[size];

		for (int i = 0; i < size; i++) {
			matchingStacks[i] = base[i].copy();
			if (matchingStacks[i].getItem() instanceof GenericGun) {
				GenericGun g = (GenericGun) matchingStacks[i].getItem();
				g.initTags(matchingStacks[i]);
				CompoundTag tags = matchingStacks[i].getTag();
				setTagByType(id, tags, key, value);
			}
		}

		return matchingStacks;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack != null && !stack.isEmpty() && stack.is(this.stack.getItem())) {
			CompoundTag tags = stack.getTag();
			if (tags != null && tags.contains(key, id)) {
				Object o = getTagByType(id, tags, key);
				return value.equals(o);
			}
		}
		return false;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return IngredientFactoryMatchNBTInt.INSTANCE;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(IngredientFactoryMatchNBTInt.INSTANCE).toString());
		json.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
		json.addProperty("key", key);
		if (value instanceof Number n) {
			json.addProperty("value", n);
		} else {
			json.addProperty("value", String.valueOf(value));
		}
		return json;
	}

	protected static byte getIdforClass(Object o) {
		if (o instanceof Byte) {
			return Tag.TAG_BYTE;
		} else if (o instanceof Short) {
			return Tag.TAG_SHORT;
		} else if (o instanceof Integer) {
			return Tag.TAG_INT;
		} else if (o instanceof Long) {
			return Tag.TAG_LONG;
		} else if (o instanceof Float) {
			return Tag.TAG_FLOAT;
		} else if (o instanceof Double) {
			return Tag.TAG_DOUBLE;
		} else if (o instanceof String) {
			return Tag.TAG_STRING;
		}
		throw new UnsupportedOperationException("Invalid datatype for NBT tag: " + o.getClass().getName());
	}

	protected static Object getTagByType(byte id, CompoundTag tags, String key) {
		switch (id) {
		case Tag.TAG_BYTE:
			return tags.getByte(key);
		case Tag.TAG_SHORT:
			return tags.getShort(key);
		case Tag.TAG_INT:
			return tags.getInt(key);
		case Tag.TAG_LONG:
			return tags.getLong(key);
		case Tag.TAG_FLOAT:
			return tags.getFloat(key);
		case Tag.TAG_DOUBLE:
			return tags.getDouble(key);
		case Tag.TAG_STRING:
			return tags.getString(key);
		default:
			return null;
		}
	}

	protected static void setTagByType(byte id, CompoundTag tags, String key, Object value) {
		switch (id) {
		case Tag.TAG_BYTE:
			tags.putByte(key, (byte) value);
			break;
		case Tag.TAG_SHORT:
			tags.putShort(key, (short) value);
			break;
		case Tag.TAG_INT:
			tags.putInt(key, (int) value);
			break;
		case Tag.TAG_LONG:
			tags.putLong(key, (long) value);
			break;
		case Tag.TAG_FLOAT:
			tags.putFloat(key, (float) value);
			break;
		case Tag.TAG_DOUBLE:
			tags.putDouble(key, (double) value);
			break;
		case Tag.TAG_STRING:
			tags.putString(key, (String) value);
			break;
		default:
			break;
		}
	}

}
