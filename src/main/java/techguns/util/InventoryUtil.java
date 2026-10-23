package techguns.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGItems;
import techguns.capabilities.TGExtendedPlayer;
import techguns.gui.player.TGPlayerInventory;

/**
 * Ammo is taken from / put into the ammo slots of the Techguns player inventory first (like 1.12), then the offhand and the
 * main inventory (the offhand is an addition of the port).
 */
public class InventoryUtil {

	public static int addAmmoToPlayerInventory(Player ply, ItemStack ammo) {
		Inventory inv = ply.getInventory();
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props != null) {
			int amount = addItemToInventory(props.tg_inventory.inventory, ammo, TGPlayerInventory.SLOTS_AMMO_START, TGPlayerInventory.SLOTS_AMMO_END + 1);
			if (amount > 0) {
				return addItemToInventory(inv.items, TGItems.newStack(ammo, amount), 0, inv.items.size());
			} else {
				return 0;
			}
		}
		return addItemToInventory(inv.items, ammo, 0, inv.items.size());
	}

	public static int addAmmoToAmmoInventory(Player ply, ItemStack ammo) {
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props != null) {
			return addItemToInventory(props.tg_inventory.inventory, ammo, TGPlayerInventory.SLOTS_AMMO_START, TGPlayerInventory.SLOTS_AMMO_END + 1);
		}
		return ammo.getCount();
	}

	/**
	 * Try merge itemstack with inventory, return amount of stacksize that could NOT be merged.
	 */
	public static int addItemToInventory(NonNullList<ItemStack> mainInventory, ItemStack item2, int startIndex, int endIndex) {
		ItemStack item = item2.copy();

		for (int i = startIndex; i < endIndex; i++) {
			//1st try a merge
			if (ItemUtil.isItemEqual(mainInventory.get(i), item) && (mainInventory.get(i).getCount() < item.getMaxStackSize())) {
				int diff = (mainInventory.get(i).getCount() + item.getCount()) - item.getMaxStackSize();
				if (diff < 0) {
					int c = mainInventory.get(i).getCount();
					mainInventory.get(i).setCount(c + item.getCount());
					item.setCount(0);
					return 0;
				} else {
					mainInventory.get(i).setCount(item.getMaxStackSize());
					item.setCount(diff);
				}
			}
		}

		// could not fully merge, try fill empty slot
		if (item.getCount() > 0) {
			for (int i = startIndex; i < endIndex; i++) {
				if (mainInventory.get(i).isEmpty()) {
					mainInventory.set(i, item.copy());
					item.setCount(0);
					return 0;
				}
			}
		}
		//Return number of items that could not be put into inventory
		return item.getCount();
	}

	public static boolean consumeAmmoPlayer(Player ply, ItemStack[] ammo) {
		if (ammo.length == 1) {
			return consumeAmmoPlayer(ply, ammo[0]);
		} else {
			boolean canconsume = true;
			for (int i = 0; i < ammo.length; i++) {
				if (!canConsumeAmmoPlayer(ply, ammo[i])) {
					canconsume = false;
					break;
				}
			}
			if (canconsume) {
				for (int i = 0; i < ammo.length; i++) {
					consumeAmmoPlayer(ply, ammo[i]);
				}
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean consumeAmmoPlayer(Player ply, ItemStack ammo) {
		if (ammo.isEmpty()) return true;
		Inventory inv = ply.getInventory();
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		NonNullList<ItemStack> tginv = props != null ? props.tg_inventory.inventory : NonNullList.create();
		int tgStart = props != null ? TGPlayerInventory.SLOTS_AMMO_START : 0;
		int tgEnd = props != null ? TGPlayerInventory.SLOTS_AMMO_END + 1 : 0;
		int amount = ammo.getCount();
		if (amount == 1) {
			if (consumeAmmo(tginv, ammo, tgStart, tgEnd)) {
				return true;
			}
			if (consumeAmmo(inv.offhand, ammo, 0, inv.offhand.size())) {
				return true;
			}
			return consumeAmmo(inv.items, ammo, 0, inv.items.size());
		} else {
			//Check first if amount can be consumed (each canConsumeItem returns the missing amount of the whole stack)
			int found = (amount - canConsumeItem(tginv, ammo, tgStart, tgEnd)) + (amount - canConsumeItem(inv.offhand, ammo, 0, inv.offhand.size()))
					+ (amount - canConsumeItem(inv.items, ammo, 0, inv.items.size()));
			if (found >= amount) {
				int missing = consumeItem(tginv, ammo, tgStart, tgEnd);
				if (missing > 0) {
					missing = consumeItem(inv.offhand, TGItems.newStack(ammo, missing), 0, inv.offhand.size());
				}
				if (missing > 0) {
					return consumeItem(inv.items, TGItems.newStack(ammo, missing), 0, inv.items.size()) <= 0;
				}
				return true;
			}
			return false;
		}
	}

	public static boolean canConsumeAmmoPlayer(Player ply, ItemStack ammo) {
		if (ammo.isEmpty()) return true;
		Inventory inv = ply.getInventory();
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		int amount = ammo.getCount();
		int found = (amount - canConsumeItem(inv.offhand, ammo, 0, inv.offhand.size())) + (amount - canConsumeItem(inv.items, ammo, 0, inv.items.size()));
		if (props != null) {
			found += amount - canConsumeItem(props.tg_inventory.inventory, ammo, TGPlayerInventory.SLOTS_AMMO_START, TGPlayerInventory.SLOTS_AMMO_END + 1);
		}
		return found >= amount;
	}

	/**
	 * Return missing items, that can not be consumed, 0 == amount can be consumed
	 */
	public static int canConsumeItem(NonNullList<ItemStack> inv, ItemStack item, int startIndex, int endIndex) {
		int amount = item.getCount();
		for (int i = startIndex; i < endIndex; i++) {
			if (ItemUtil.isItemEqual(inv.get(i), item)) {
				amount -= inv.get(i).getCount();
				if (amount <= 0) {
					return 0;
				}
			}
		}
		return amount;
	}

	/**
	 * Consume items, return the amount that could not be consumed
	 */
	public static int consumeItem(NonNullList<ItemStack> inv, ItemStack item, int startIndex, int endIndex) {
		int amount = item.getCount();
		for (int i = startIndex; i < endIndex; i++) {
			ItemStack s = inv.get(i);
			if (ItemUtil.isItemEqual(s, item)) {
				if (s.getCount() > amount) {
					s.shrink(amount);
					return 0;
				} else {
					amount -= s.getCount();
					inv.set(i, ItemStack.EMPTY);
					if (amount <= 0) {
						return 0;
					}
				}
			}
		}
		return amount;
	}

	public static boolean consumeAmmo(NonNullList<ItemStack> inv, ItemStack ammo, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			ItemStack s = inv.get(i);
			if (!s.isEmpty() && ItemUtil.isItemEqual(s, ammo)) {
				s.shrink(1);
				if (s.isEmpty()) {
					inv.set(i, ItemStack.EMPTY);
				}
				return true;
			}
		}
		return false;
	}

	public static ItemStack consumeFood(NonNullList<ItemStack> inv, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; ++i) {
			if (!inv.get(i).isEmpty() && inv.get(i).isEdible()) {
				ItemStack food = TGItems.newStack(inv.get(i), 1);
				inv.get(i).shrink(1);
				if (inv.get(i).isEmpty()) {
					inv.set(i, ItemStack.EMPTY);
				}
				return food;
			}
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Counts the ammo item in the main inventory, the offhand and the ammo slots of the Techguns inventory (HUD)
	 */
	public static int countAmmoPlayer(Player ply, ItemStack ammoitem) {
		Inventory inv = ply.getInventory();
		int count = countItemInInv(inv.items, ammoitem, 0, inv.items.size());
		count += countItemInInv(inv.offhand, ammoitem, 0, inv.offhand.size());
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props != null) {
			count += countItemInInv(props.tg_inventory.inventory, ammoitem, TGPlayerInventory.SLOTS_AMMO_START, TGPlayerInventory.SLOTS_AMMO_END + 1);
		}
		return count;
	}

	public static int countItemInInv(NonNullList<ItemStack> inv, ItemStack stackToSearchFor, int startIndex, int endIndex) {
		int count = 0;
		for (int i = startIndex; i < endIndex; i++) {
			if (ItemUtil.isItemEqual(inv.get(i), stackToSearchFor)) {
				count += inv.get(i).getCount();
			}
		}
		return count;
	}
}
