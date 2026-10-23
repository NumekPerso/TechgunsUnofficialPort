package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInput;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.tileentities.ChargingStationTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class ChargingStationContainer extends BasicMachineContainer {

	public static final int SLOT_INPUT_X = 19;
	public static final int SLOT_OUTPUT_X = 68;
	public static final int SLOTS_ROW1_Y = 17;

	//1.12 MetalPressContainer.SLOT_UPGRADE_X / SLOT_OUTPUTS_Y
	public static final int SLOT_UPGRADE_X = 152;
	public static final int SLOT_UPGRADE_Y = 60;

	ChargingStationTileEnt tile;

	/**
	 * client constructor, the block position is sent by the server
	 */
	public ChargingStationContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<ChargingStationTileEnt>getClientTile(player, buf));
	}

	public ChargingStationContainer(int id, Inventory player, ChargingStationTileEnt ent) {
		super(TGMachines.CHARGING_STATION_CONTAINER, id, player, ent);
		this.tile = ent;

		ItemStackHandlerPlus handler = ent.getInventory();

		this.addSlot(new SlotMachineInput(handler, ChargingStationTileEnt.SLOT_INPUT, SLOT_INPUT_X, SLOTS_ROW1_Y));

		this.addSlot(new SlotItemHandlerOutput(handler, ChargingStationTileEnt.SLOT_OUTPUT, SLOT_OUTPUT_X, SLOTS_ROW1_Y));
		this.addSlot(new SlotMachineUpgrade(handler, ChargingStationTileEnt.SLOT_UPGRADE, SLOT_UPGRADE_X, SLOT_UPGRADE_Y));

		this.addPlayerInventorySlots(player);
	}

	public ChargingStationTileEnt getChargingStation() {
		return tile;
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		return this.transferStackInSlotMachine(ply, id, ChargingStationTileEnt.SLOT_UPGRADE, tile::getValidSlotForItemInMachine);
	}
}
