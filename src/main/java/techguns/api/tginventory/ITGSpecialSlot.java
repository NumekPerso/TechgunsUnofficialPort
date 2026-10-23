package techguns.api.tginventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import techguns.items.armors.TGArmorBonus;

public interface ITGSpecialSlot {
	/**
	 * The slot type this item belongs
	 */
	public TGSlotType getSlot(ItemStack item);

	/**
	 * Called each player tick (both sides, END phase), when worn on back/face/hand slot
	 */
	public default void onPlayerTick(ItemStack item, PlayerTickEvent player) {
	};

	/**
	 * @param type The bonus type
	 * @param consume if power should be consumed (the bonus is actively used)
	 */
	public default float getBonus(TGArmorBonus type, ItemStack stack, boolean consume, Player player) {
		return 0f;
	}
}
