package techguns.radiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Radioactive items (in the player inventory). 1.12 keyed the data by item + damage value, items are flattened in 1.20
 * so the data is keyed by item only. 1.12 ore dictionary entries are item tags, checked at runtime (tags are only
 * known after the datapack load).
 */
public class ItemRadiationRegistry {
	public static HashMap<Item, ItemRadiationData> registry = new HashMap<>();

	protected static List<TagEntry> tagRegistry = new ArrayList<>();

	public static void addRadiationData(ItemStack stack, int radamount, int radduration) {
		addRadiationData(stack.getItem(), radamount, radduration);
	}

	public static void addRadiationData(Item item, int radamount, int radduration) {
		if (item == null) {
			return;
		}
		if (registry.containsKey(item)) {
			System.out.println("Trying to add radiation data to item " + item + ". Data for this item already exists!");
		} else {
			registry.put(item, new ItemRadiationData((short) radamount, (short) radduration));
		}
	}

	/**
	 * Items of a tag (1.12 OreDictionary.getOres(name).forEach(addRadiationData))
	 */
	public static void addRadiationData(TagKey<Item> tag, int radamount, int radduration) {
		tagRegistry.add(new TagEntry(tag, new ItemRadiationData((short) radamount, (short) radduration)));
	}

	public static ItemRadiationData getRadiationDataFor(ItemStack stack) {
		if (stack.isEmpty()) return null;
		ItemRadiationData data = registry.get(stack.getItem());
		if (data != null) {
			return data;
		}
		for (TagEntry e : tagRegistry) {
			if (stack.is(e.tag)) {
				return e.data;
			}
		}
		return null;
	}

	public static ItemRadiationData getRadiationDataFor(Item item) {
		return getRadiationDataFor(new ItemStack(item));
	}

	protected static class TagEntry {
		final TagKey<Item> tag;
		final ItemRadiationData data;

		TagEntry(TagKey<Item> tag, ItemRadiationData data) {
			this.tag = tag;
			this.data = data;
		}
	}
}
