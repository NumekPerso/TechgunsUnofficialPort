package techguns.gui.containers;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import techguns.TGMachines;
import techguns.gui.player.SlotArmor;
import techguns.items.armors.GenericArmor;
import techguns.items.armors.GenericShield;
import techguns.tileentities.RepairBenchTileEnt;

public class RepairBenchContainer extends OwnedTileContainer {

	protected static final int SLOT_REPAIR = 9;
	protected static final int PLAYER_INV_START = 10;
	protected static final int ARMOR_START = PLAYER_INV_START + 36;

	protected RepairBenchTileEnt repairBench;

	public RepairBenchContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<RepairBenchTileEnt>getClientTile(player, buf));
	}

	public RepairBenchContainer(int id, Inventory player, RepairBenchTileEnt ent) {
		super(TGMachines.REPAIR_BENCH_CONTAINER, id, player, ent);
		this.repairBench = ent;

		int i;
		for (i = 0; i < 9; ++i) {
			this.addSlot(new SlotItemHandler(ent.getInventory(), i, 8 + i * 18, 57));
		}
		this.addSlot(new SlotItemHandler(ent.getInventory(), 9, 8, 18) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				if (!stack.isEmpty()) {
					return stack.getItem() instanceof GenericArmor || stack.getItem() instanceof GenericShield;
				}
				return false;
			}
		});

		this.addPlayerInventorySlots(player);

		for (i = 0; i < 4; ++i) {
			this.addSlot(new SlotArmor(player, 39 - i, 89 + (i * 20), 18, i, player.player));
		}
		final Player ply = player.player;
		this.addSlot(new Slot(player, 40, 64, 18) {
			@Override
			public void setByPlayer(ItemStack stack) {
				if (net.minecraft.world.item.Equipable.get(stack) != null) {
					ply.onEquipItem(EquipmentSlot.OFFHAND, this.getItem(), stack);
				}
				super.setByPlayer(stack);
			}

			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
			}
		});
	}

	public RepairBenchTileEnt getRepairBench() {
		return repairBench;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotid) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotid);

		if (slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			if (!stack.isEmpty()) {

				if (slotid <= SLOT_REPAIR) {
					//Pressed in machine gui, put in player inv
					if (!this.moveItemStackTo(stack1, PLAYER_INV_START, ARMOR_START, false)) {
						return ItemStack.EMPTY;
					}
					slot.onQuickCraft(stack1, stack);

				} else if (slotid < ARMOR_START) {

					if (stack1.getItem() instanceof ArmorItem armor) {
						int type = getArmorIntFromEntityEquipmentSlot(armor.getEquipmentSlot());
						//put armor into player armor inventory
						if (!this.moveItemStackTo(stack1, ARMOR_START + type, ARMOR_START + type + 1, false)) {
							return ItemStack.EMPTY;
						}
						slot.onQuickCraft(stack1, stack);
					} else {
						if (!this.moveItemStackTo(stack1, 0, SLOT_REPAIR + 1, false)) {
							return ItemStack.EMPTY;
						}
						slot.onQuickCraft(stack1, stack);
					}
				} else {
					//pressed in armor gui, put item back in player inventory
					if (!this.moveItemStackTo(stack1, PLAYER_INV_START, ARMOR_START, false)) {
						return ItemStack.EMPTY;
					}
					slot.onQuickCraft(stack1, stack);
				}

				if (stack1.getCount() == 0) {
					slot.set(ItemStack.EMPTY);
				} else {
					slot.setChanged();
				}

				if (stack1.getCount() == stack.getCount()) {
					return ItemStack.EMPTY;
				}

				slot.onTake(player, stack1);
			}
		}
		return stack;
	}

}
