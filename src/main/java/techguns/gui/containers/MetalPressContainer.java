package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInput;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.tileentities.MetalPressTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class MetalPressContainer extends BasicMachineContainer {

	protected MetalPressTileEnt tile;

	public static final int SLOT_INPUT1_X = 110;
	public static final int SLOT_INPUT2_X = 130;
	public static final int SLOT_INPUTS_Y = 17;

	public static final int SLOT_OUTPUT_X = 120;
	public static final int SLOT_OUTPUTS_Y = 60;
	public static final int SLOT_UPGRADE_X = 152;

	/**
	 * client constructor, the block position is sent by the server
	 */
	public MetalPressContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<MetalPressTileEnt>getClientTile(player, buf));
	}

	public MetalPressContainer(int id, Inventory player, MetalPressTileEnt ent) {
		super(TGMachines.METAL_PRESS_CONTAINER, id, player, ent);
		this.tile = ent;

		ItemStackHandlerPlus handler = ent.getInventory();

		this.addSlot(new SlotMachineInput(handler, MetalPressTileEnt.SLOT_INPUT1, SLOT_INPUT1_X, SLOT_INPUTS_Y));
		this.addSlot(new SlotMachineInput(handler, MetalPressTileEnt.SLOT_INPUT2, SLOT_INPUT2_X, SLOT_INPUTS_Y));

		this.addSlot(new SlotItemHandlerOutput(handler, MetalPressTileEnt.SLOT_OUTPUT, SLOT_OUTPUT_X, SLOT_OUTPUTS_Y));
		this.addSlot(new SlotMachineUpgrade(handler, MetalPressTileEnt.SLOT_UPGRADE, SLOT_UPGRADE_X, SLOT_OUTPUTS_Y));

		this.addPlayerInventorySlots(player);

		//1.12 FIELD_SYNC_ID_SWITCH
		this.addSyncedShort(() -> this.tile.getAutoSplitMode(), v -> this.tile.setAutoSplitMode((byte) v));
	}

	public MetalPressTileEnt getMetalPress() {
		return tile;
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		return this.transferStackInSlotMachine(ply, id, MetalPressTileEnt.SLOT_UPGRADE, this.tile::getValidSlotForItemInMachine);
	}

}
