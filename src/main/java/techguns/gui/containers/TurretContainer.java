package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;
import techguns.gui.widgets.ITGSlotBackground;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInputBG;
import techguns.gui.widgets.SlotTurretArmor;
import techguns.gui.widgets.SlotTurretGun;
import techguns.items.guns.GenericGun;
import techguns.tileentities.TurretTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class TurretContainer extends RedstoneTileContainer {

	protected TurretTileEnt tile;

	/**
	 * client constructor, the block position is sent by the server
	 */
	public TurretContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<TurretTileEnt>getClientTile(player, buf));
	}

	public TurretContainer(int id, Inventory player, TurretTileEnt ent) {
		super(TGMachines.TURRET_CONTAINER, id, player, ent);
		this.tile = ent;

		ItemStackHandlerPlus handler = ent.getInventory();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new SlotMachineInputBG(handler, (i * 3) + j, 18 + 18 * i, 17 + 18 * j, ITGSlotBackground.AMMOSLOT_TEX));
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new OutputSlotBG(handler, 9 + (i * 3) + j, 116 + 18 * i, 17 + 18 * j, ITGSlotBackground.AMMOEMPTYSLOT_TEX));
			}
		}

		this.addSlot(new SlotTurretGun(handler, 18, 85, 19));

		this.addSlot(new SlotTurretArmor(handler, 19, 85, 42));

		this.addPlayerInventorySlots(player);

		//1.12 FIELD_SYNC_ID_TURRETDEATH, FIELD_SYNC_ID_REPAIRTIME, FIELD_SYNC_ID_POWER_STORED
		this.addSyncedShort(() -> this.tile.turretDeath ? 1 : 0, v -> this.tile.turretDeath = v != 0);
		this.addSyncedInt(() -> this.tile.repairTime, v -> this.tile.repairTime = v);
		this.addSyncedInt(() -> this.tile.getEnergyStorage().getEnergyStored(), v -> this.tile.getEnergyStorage().setEnergyStored(v));
		this.addSyncedShort(() -> this.tile.attackAnimals ? 1 : 0, v -> this.tile.attackAnimals = v != 0);
		this.addSyncedShort(() -> this.tile.getPvpSetting(), v -> this.tile.setPvpSetting((byte) v));
	}

	public TurretTileEnt getTurret() {
		return tile;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotid) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotid);

		if (slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			if (!stack.isEmpty()) {

				//machineslot pressed
				if (slotid <= TurretTileEnt.SLOT_ARMOR) {

					//put item back in player inventory
					if (!this.moveItemStackTo(stack1, TurretTileEnt.SLOT_ARMOR + 1, TurretTileEnt.SLOT_ARMOR + 1 + 36, false)) {
						return ItemStack.EMPTY;
					}
					slot.onQuickCraft(stack1, stack);

				} else if (slotid > TurretTileEnt.SLOT_ARMOR) {
					//pressed in player gui

					if (stack.getItem() instanceof GenericGun) {

						//put weapon in weaponslot
						if (!this.moveItemStackTo(stack1, TurretTileEnt.SLOT_WEAPON, TurretTileEnt.SLOT_WEAPON + 1, false)) {
							return ItemStack.EMPTY;
						}
						slot.onQuickCraft(stack1, stack);
					} else if (stack.getItem() instanceof ITGSpecialSlot && (((ITGSpecialSlot) stack.getItem()).getSlot(stack) == TGSlotType.TURRETARMOR)) {
						//put in armorslot
						if (!this.moveItemStackTo(stack1, TurretTileEnt.SLOT_ARMOR, TurretTileEnt.SLOT_ARMOR + 1, false)) {
							return ItemStack.EMPTY;
						}
						slot.onQuickCraft(stack1, stack);
					} else {
						//put item in input inventory (1.12 used an empty slot range here)
						if (!this.moveItemStackTo(stack1, TurretTileEnt.SLOT_INPUT1, TurretTileEnt.SLOT_INPUT1 + TurretTileEnt.INPUTS_SIZE, false)) {
							return ItemStack.EMPTY;
						}
						slot.onQuickCraft(stack1, stack);
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

				slot.onTake(player, stack1);
			}
		}
		return stack;
	}

	/**
	 * Output slot with an empty slot icon
	 */
	public static class OutputSlotBG extends SlotItemHandlerOutput implements ITGSlotBackground {
		protected ResourceLocation bg;

		public OutputSlotBG(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition, ResourceLocation bg) {
			super(itemHandler, index, xPosition, yPosition);
			this.bg = bg;
		}

		@Override
		public ResourceLocation getBackgroundTexture() {
			return bg;
		}
	}

}
