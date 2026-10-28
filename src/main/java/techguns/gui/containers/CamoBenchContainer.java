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
import techguns.api.tginventory.TGSlotType;
import techguns.capabilities.TGExtendedPlayer;
import techguns.gui.player.SlotArmor;
import techguns.gui.player.SlotTG;
import techguns.gui.player.TGPlayerInventory;
import techguns.tileentities.CamoBenchTileEnt;

public class CamoBenchContainer extends OwnedTileContainer {

	public static final int SLOT_INPUT_X = 17;
	public static final int SLOT_INPUT_Y = 18;

	protected CamoBenchTileEnt camoBench;

	public CamoBenchContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<CamoBenchTileEnt>getClientTile(player, buf));
	}

	public CamoBenchContainer(int id, Inventory player, CamoBenchTileEnt ent) {
		super(TGMachines.CAMO_BENCH_CONTAINER, id, player, ent);
		this.camoBench = ent;

		this.addSlot(new SlotItemHandler(ent.getInventory(), 0, SLOT_INPUT_X, SLOT_INPUT_Y));

		this.addPlayerInventorySlots(player);
		int i;
		for (i = 0; i < 4; ++i) {
			this.addSlot(new SlotArmor(player, 39 - i, 99 + (i * 18), 18, i, player.player));
		}

		TGExtendedPlayer props = TGExtendedPlayer.get(player.player);
		if (props != null) {
			this.addSlot(new SlotTG(props.tg_inventory, TGPlayerInventory.SLOT_FACE, 44, 18, TGSlotType.FACESLOT));
			this.addSlot(new SlotTG(props.tg_inventory, TGPlayerInventory.SLOT_BACK, 62, 18, TGSlotType.BACKSLOT));
			this.addSlot(new SlotTG(props.tg_inventory, TGPlayerInventory.SLOT_HAND, 80, 18, TGSlotType.HANDSLOT));
		}
		final Player ply = player.player;
		this.addSlot(new Slot(player, 40, -17, 94) {
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

	public CamoBenchTileEnt getCamoBench() {
		return camoBench;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotid) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotid);

		if (slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			if (!stack.isEmpty()) {

				//camoslot pressed
				if (slotid == 0) {
					if (stack1.getItem() instanceof ArmorItem armor) {
						int type = getArmorIntFromEntityEquipmentSlot(armor.getEquipmentSlot());
						//put armor back in player armor inventory
						if (!this.moveItemStackTo(stack1, 37 + type, 37 + type + 1, false)) {
							//armor slot is full: player inventory
							if (!this.moveItemStackTo(stack1, 1, 37, false)) {
								return ItemStack.EMPTY;
							}
						}
						slot.onQuickCraft(stack1, stack);
					} else {
						//put item back in player inventory
						if (!this.moveItemStackTo(stack1, 1, 37, false)) {
							return ItemStack.EMPTY;
						}
						slot.onQuickCraft(stack1, stack);
					}
				} else if (slotid < 37) {
					//pressed in player inventory: camo slot, armor slot (quick equip) if the camo slot is used
					if (!this.moveItemStackTo(stack1, 0, 0 + 1, false)) {
						if (!(stack1.getItem() instanceof ArmorItem armor)) {
							return ItemStack.EMPTY;
						}
						int type = getArmorIntFromEntityEquipmentSlot(armor.getEquipmentSlot());
						if (!this.moveItemStackTo(stack1, 37 + type, 37 + type + 1, false)) {
							return ItemStack.EMPTY;
						}
					}
					slot.onQuickCraft(stack1, stack);
				} else {
					//pressed in an armor / Techguns / offhand slot: camo slot, player inventory if the camo slot is used
					if (!this.moveItemStackTo(stack1, 0, 0 + 1, false)) {
						if (!this.moveItemStackTo(stack1, 1, 37, false)) {
							return ItemStack.EMPTY;
						}
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
