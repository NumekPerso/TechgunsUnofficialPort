package techguns.api.render;

import net.minecraft.world.item.ItemStack;

/**
 * indicates that this item is rendered by a Techguns IItemRenderer (through a BlockEntityWithoutLevelRenderer)
 */
public interface IItemTGRenderer {
	/**
	 * Return if the passed ItemStack should use the custom renderer
	 */
	default boolean shouldUseRenderHack(ItemStack stack) {
		return true;
	}

	default boolean isModelBase(ItemStack stack) {
		return true;
	}
}
