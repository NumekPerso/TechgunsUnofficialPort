package techguns.util;

import java.util.ArrayList;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * An item or an item tag (1.12 ore dictionary name) with a stack size, used as recipe input of machines.
 * The 1.12 ore dictionary names are item tags in 1.20 ("ingotSteel" -> "forge:ingots/steel"), tags are checked at
 * runtime so they follow datapack reloads.
 */
public class ItemStackOreDict {

	/**
	 * Name of the item tag (e.g. "forge:ingots/steel"), null if this is an item
	 */
	public String oreDictName;
	public TagKey<Item> tag;
	public int stackSize;
	public ItemStack item;
	//strict mode, check for NBT
	public boolean strict = false;

	public static final ItemStackOreDict EMPTY = new ItemStackOreDict();

	public ItemStackOreDict(String oreDictName, int stackSize) {
		this.oreDictName = oreDictName;
		this.tag = TagKey.create(Registries.ITEM, new ResourceLocation(oreDictName));
		this.stackSize = stackSize;
		this.item = ItemStack.EMPTY;
	}

	public ItemStackOreDict(String oreDictName) {
		this(oreDictName, 1);
	}

	public ItemStackOreDict(TagKey<Item> tag, int stackSize) {
		this.oreDictName = tag.location().toString();
		this.tag = tag;
		this.stackSize = stackSize;
		this.item = ItemStack.EMPTY;
	}

	private ItemStackOreDict() {
		this.oreDictName = null;
		this.tag = null;
		this.stackSize = 1;
		this.item = ItemStack.EMPTY;
	}

	public ItemStackOreDict(ItemStack item, int stackSize) {
		this.stackSize = stackSize;
		this.item = item;
		this.oreDictName = null;
		this.tag = null;
	}

	public ItemStackOreDict(ItemStack item) {
		if (item != null) {
			this.stackSize = item.getCount();
			this.item = item;
		} else {
			this.stackSize = 0;
			this.item = ItemStack.EMPTY;
		}
		this.oreDictName = null;
		this.tag = null;
	}

	public ItemStackOreDict(Item item, int stackSize) {
		this(new ItemStack(item, stackSize), stackSize);
	}

	public boolean hasItems() {
		if (isEmpty()) return false;

		if (this.tag != null) {
			return BuiltInRegistries.ITEM.getTag(this.tag).map(set -> set.size() > 0).orElse(false);
		} else {
			return !this.item.isEmpty();
		}
	}

	/**
	 * @return if using an oredict name
	 */
	public boolean isOreDict() {
		return this.tag != null;
	}

	public ItemStackOreDict setNoStrictMode() {
		this.strict = false;
		return this;
	}

	public ItemStackOreDict setStrictMode() {
		this.strict = true;
		return this;
	}

	public boolean isEmpty() {
		return this.tag == null && this.item.isEmpty();
	}

	protected static boolean itemMatches(ItemStack target, ItemStack input, boolean strict) {
		if (input.isEmpty() || target.isEmpty()) {
			return false;
		}
		if (strict) {
			return ItemStack.isSameItemSameTags(target, input);
		}
		return target.getItem() == input.getItem();
	}

	public boolean isEqualWithOreDict(ItemStack other) {
		if (this.tag == null) {
			return itemMatches(this.item, other, strict);
		} else {
			return !other.isEmpty() && other.is(this.tag);
		}
	}

	public ArrayList<ItemStack> getItemStacks() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();

		if (this.tag == null) {
			if (!this.item.isEmpty()) {
				ItemStack stack = this.item.copy();
				stack.setCount(this.stackSize);
				list.add(stack);
			}
		} else {
			for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(this.tag)) {
				list.add(new ItemStack(holder.value(), this.stackSize));
			}
		}

		return list;
	}

	public ArrayList<ItemStack> getItemStacks(int amountOverride) {
		ArrayList<ItemStack> list = this.getItemStacks();
		list.forEach(s -> s.setCount(amountOverride));
		return list;
	}

	@Override
	public String toString() {
		if (this.oreDictName != null) {
			return "ItemStackOreDict:" + this.oreDictName;
		} else if (!this.item.isEmpty()) {
			return this.item.toString();
		} else {
			return "EMPTY_ItemStackOreDict";
		}
	}

	/**
	 * Return if two itemStack ore dict match eachother
	 * @param other
	 * @return
	 */
	public boolean matches(ItemStackOreDict other) {
		if (this.tag != null && other.tag != null) {
			return this.tag.equals(other.tag);
		} else if (this.tag != null) {
			return this.isEqualWithOreDict(other.item);
		} else if (other.tag != null) {
			return other.isEqualWithOreDict(this.item);
		} else {
			return itemMatches(this.item, other.item, false);
		}
	}

	public boolean matches(ItemStack other) {
		return this.matches(new ItemStackOreDict(other, other.getCount()));
	}

}
