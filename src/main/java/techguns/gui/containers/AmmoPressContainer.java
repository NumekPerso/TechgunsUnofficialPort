package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.gui.widgets.ITGSlotBackground;
import techguns.gui.widgets.SlotAmmoPressInput;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.tileentities.AmmoPressTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class AmmoPressContainer extends BasicMachineContainer {
	protected AmmoPressTileEnt tile;

	public static final int SLOT_METAL1_X = 100;
	public static final int SLOT_METAL2_X = 120;
	public static final int SLOT_POWDER_X = 140;

	public static final int SLOTS_INPUT_Y = 17;

	public static final int SLOT_OUTPUT_X = 120;
	public static final int SLOTS_OUTPUT_Y = 60;

	public static final int SLOT_UPGRADE_X = 152;

	/**
	 * client constructor, the block position is sent by the server
	 */
	public AmmoPressContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<AmmoPressTileEnt>getClientTile(player, buf));
	}

	public AmmoPressContainer(int id, Inventory player, AmmoPressTileEnt ent) {
		super(TGMachines.AMMO_PRESS_CONTAINER, id, player, ent);
		this.tile = ent;

		ItemStackHandlerPlus handler = ent.getInventory();

		this.addSlot(new SlotAmmoPressInput(handler, AmmoPressTileEnt.SLOT_METAL1, SLOT_METAL1_X, SLOTS_INPUT_Y, ITGSlotBackground.INGOTDARKSLOT_TEX));
		this.addSlot(new SlotAmmoPressInput(handler, AmmoPressTileEnt.SLOT_METAL2, SLOT_METAL2_X, SLOTS_INPUT_Y, ITGSlotBackground.INGOTSLOT_TEX));
		this.addSlot(new SlotAmmoPressInput(handler, AmmoPressTileEnt.SLOT_POWDER, SLOT_POWDER_X, SLOTS_INPUT_Y, ITGSlotBackground.POWDERSLOT_TEX));

		this.addSlot(new SlotItemHandlerOutput(handler, AmmoPressTileEnt.SLOT_OUTPUT, SLOT_OUTPUT_X, SLOTS_OUTPUT_Y));
		this.addSlot(new SlotMachineUpgrade(handler, AmmoPressTileEnt.SLOT_UPGRADE, SLOT_UPGRADE_X, SLOTS_OUTPUT_Y));

		this.addPlayerInventorySlots(player);

		//1.12 FIELD_SYNC_ID_POWER_BUILDPLAN
		this.addSyncedShort(() -> this.tile.buildPlan, v -> this.tile.buildPlan = (byte) v);
	}

	public AmmoPressTileEnt getAmmoPress() {
		return tile;
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		return this.transferStackInSlotMachine(ply, id, AmmoPressTileEnt.SLOT_UPGRADE, AmmoPressTileEnt::getValidSlotForItemInMachine);
	}

}
