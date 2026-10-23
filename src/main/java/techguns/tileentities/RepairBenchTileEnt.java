package techguns.tileentities;

import static techguns.gui.ButtonConstants.BUTTON_ID_SECURITY;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import techguns.TGMachines;
import techguns.Techguns;
import techguns.gui.containers.RepairBenchContainer;
import techguns.items.armors.GenericArmor;
import techguns.items.armors.GenericShield;

public class RepairBenchTileEnt extends BasicOwnedTileEnt {

	public RepairBenchTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.REPAIR_BENCH_TILE, pos, state, 10, false);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Techguns.MODID + ".container.repairbench");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new RepairBenchContainer(id, inv, this);
	}

	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if (id > BUTTON_ID_SECURITY && id <= BUTTON_ID_SECURITY + 4 && this.isUseableByPlayer(ply)) {
			int slot = 3 - ((id - BUTTON_ID_SECURITY) - 1);
			ItemStack item = ply.getInventory().armor.get(slot);

			doRepair(item);

		} else if (id == BUTTON_ID_SECURITY + 5) {
			doRepair(ply.getInventory().offhand.get(0));

		} else if (id == BUTTON_ID_SECURITY + 6) {
			doRepair(this.inventory.getStackInSlot(9));
			this.setChanged();

		} else {
			super.buttonClicked(id, ply, data);
		}
	}

	protected void doRepair(ItemStack item) {
		List<ItemStack> mats = null;
		if (!item.isEmpty() && item.getItem() instanceof GenericArmor) {
			GenericArmor armor = (GenericArmor) item.getItem();
			mats = armor.getRepairMats(item);
		} else if (!item.isEmpty() && item.getItem() instanceof GenericShield) {
			mats = ((GenericShield) item.getItem()).getRepairMats(item);
		}

		if (mats != null && mats.size() > 0) {

			boolean canConsume = true;
			for (int i = 0; i < mats.size(); i++) {
				if (canConsumeItem(this.inventory, mats.get(i), 0, 9) > 0) {
					canConsume = false;
					break;
				}
			}

			if (canConsume) {
				for (int i = 0; i < mats.size(); i++) {
					consumeItem(this.inventory, mats.get(i), 0, 9);
				}
				item.setDamageValue(0);
			}

		}

	}

	/**
	 * 1.12 InventoryUtil.canConsumeItem(ItemStackHandler...): returns the missing amount, 0 == amount can be consumed
	 */
	public static int canConsumeItem(IItemHandler inv, ItemStack item, int startIndex, int endIndex) {
		int needed = item.getCount();

		for (int i = startIndex; i < endIndex; ++i) {
			ItemStack s = inv.getStackInSlot(i);
			if (!s.isEmpty() && s.getItem() == item.getItem()) {
				needed -= s.getCount();
				if (needed <= 0) {
					return 0;
				}
			}
		}
		return needed;
	}

	/**
	 * 1.12 InventoryUtil.consumeItem(ItemStackHandler...): consumes from multiple stacks, returns amount that could not be consumed
	 */
	public static int consumeItem(IItemHandlerModifiable inv, ItemStack item, int startIndex, int endIndex) {
		int needed = item.getCount();

		for (int i = startIndex; i < endIndex; ++i) {
			ItemStack s = inv.getStackInSlot(i);
			if (!s.isEmpty() && s.getItem() == item.getItem()) {
				int amount = Math.min(needed, s.getCount());
				ItemStack newStack = s.copy();
				newStack.shrink(amount);
				inv.setStackInSlot(i, newStack.isEmpty() ? ItemStack.EMPTY : newStack);
				needed -= amount;
				if (needed <= 0) {
					return 0;
				}
			}
		}
		return needed;
	}
}
