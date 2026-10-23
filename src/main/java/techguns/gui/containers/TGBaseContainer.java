package techguns.gui.containers;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import techguns.tileentities.BasicInventoryTileEnt;

/**
 * Base container of the Techguns machines (1.12 TGBaseContainer).
 * The server menu is created by the tile entity (MenuProvider), the client menu is created by the MenuType from the
 * block position sent by NetworkHooks.openScreen and uses the client block entity.
 * Tile values shown in the GUI are synced with data slots (1.12 sendWindowProperty / updateProgressBar), see addSyncedInt.
 */
public class TGBaseContainer extends AbstractContainerMenu {
	protected BasicInventoryTileEnt basictile;

	public TGBaseContainer(MenuType<?> type, int id, Inventory player, BasicInventoryTileEnt ent) {
		super(type, id);
		this.basictile = ent;
	}

	/**
	 * Reads the block position written by NetworkHooks.openScreen and returns the client block entity
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BlockEntity> T getClientTile(Inventory inv, FriendlyByteBuf buf) {
		BlockEntity tile = inv.player.level().getBlockEntity(buf.readBlockPos());
		return (T) tile;
	}

	public BasicInventoryTileEnt getTile() {
		return basictile;
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return basictile.isUseableByPlayer(playerIn);
	}

	protected static int getArmorIntFromEntityEquipmentSlot(EquipmentSlot type) {
		switch (type) {
		case CHEST:
			return 1;
		case HEAD:
			return 0;
		case LEGS:
			return 2;
		default:
			return 3;
		}
	}

	public void addPlayerInventorySlots(Inventory player) {
		int i;

		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlot(new Slot(player, i, 8 + i * 18, 142));
		}
	}

	/**
	 * Syncs a 16 bit value (vanilla data slots are sent as short) from the server tile to the client tile
	 */
	protected void addSyncedShort(IntSupplier getter, IntConsumer setter) {
		this.addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return getter.getAsInt();
			}

			@Override
			public void set(int value) {
				setter.accept(value);
			}
		});
	}

	/**
	 * Syncs a full int value with 2 data slots (low and high 16 bits)
	 */
	protected void addSyncedInt(IntSupplier getter, IntConsumer setter) {
		final int[] clientValue = new int[1];
		this.addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return getter.getAsInt() & 0xFFFF;
			}

			@Override
			public void set(int value) {
				clientValue[0] = (clientValue[0] & 0xFFFF0000) | (value & 0xFFFF);
				setter.accept(clientValue[0]);
			}
		});
		this.addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return (getter.getAsInt() >>> 16) & 0xFFFF;
			}

			@Override
			public void set(int value) {
				clientValue[0] = (clientValue[0] & 0xFFFF) | ((value & 0xFFFF) << 16);
				setter.accept(clientValue[0]);
			}
		});
	}

	/**
	 * Shift click (1.12 transferStackInSlot) for machines: slots 0..highestSlot are the machine slots, followed by the 36 player slots.
	 * Items from the player inventory go to the slot returned by validSlot (-1 = none).
	 */
	protected ItemStack transferStackInSlotMachine(Player ply, int id, int highestSlot, java.util.function.ToIntFunction<ItemStack> validSlot) {
		int MAXSLOTS = highestSlot + 36 + 1; //36 = inventory size

		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(id);

		if (slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			if (!stack.isEmpty()) {

				if (id >= 0 && id <= highestSlot) {
					//PRESSED IN MACHINE GUI
					if (!this.moveItemStackTo(stack1, highestSlot + 1, MAXSLOTS, false)) {
						return ItemStack.EMPTY;
					}
					slot.onQuickCraft(stack1, stack);
				} else if (id > highestSlot && id < MAXSLOTS) {

					int validslot = validSlot.applyAsInt(stack1);
					if (validslot >= 0) {

						if (!this.moveItemStackTo(stack1, validslot, validslot + 1, false)) {
							return ItemStack.EMPTY;
						}
						slot.onQuickCraft(stack1, stack);

					} else {
						return ItemStack.EMPTY;
					}
				}

				if (stack1.getCount() == 0) {
					slot.set(ItemStack.EMPTY);
				} else {
					slot.setChanged();
				}

				if (stack1.getCount() == stack.getCount()) {
					return ItemStack.EMPTY;
				}

				slot.onTake(ply, stack1);
			}
		}

		return stack;
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		return ItemStack.EMPTY;
	}

}
