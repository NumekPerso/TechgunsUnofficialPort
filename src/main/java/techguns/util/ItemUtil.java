package techguns.util;

import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * utility class with static item related functions
 */
public class ItemUtil {

	public static void addShiftExpandedTooltip(List<Component> tooltip) {
		tooltip.add(Component.translatable("techguns.gun.tooltip.shift1").append(" ")
				.append(Component.translatable("techguns.gun.tooltip.shift2").withStyle(ChatFormatting.GREEN)).append(" ")
				.append(Component.translatable("techguns.gun.tooltip.shift3").withStyle(ChatFormatting.GRAY)));
	}

	public static void addToolClassesTooltip(Map<String, Integer> toolclasses, List<Component> tooltip) {
		if (toolclasses.size() > 0) {
			tooltip.add(Component.translatable("techguns.tooltip.toolclasses"));
			for (String s : toolclasses.keySet()) {
				tooltip.add(Component.literal("  " + s + " : " + toolclasses.get(s)));
			}
		}
	}

	/**
	 * Same item and same NBT, counts are ignored
	 */
	public static boolean isItemEqual(ItemStack item1, ItemStack item2) {
		if (item1.isEmpty() && item2.isEmpty()) {
			return true;
		} else if (item1.isEmpty() || item2.isEmpty()) {
			return false;
		} else {
			return ItemStack.isSameItemSameTags(item1, item2);
		}
	}
}
