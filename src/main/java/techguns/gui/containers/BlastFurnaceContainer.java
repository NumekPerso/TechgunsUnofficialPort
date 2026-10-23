package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.gui.widgets.ITGSlotBackground;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInputBG;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.tileentities.BlastFurnaceTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class BlastFurnaceContainer extends BasicMachineContainer {

	protected BlastFurnaceTileEnt tile;

	public static final int SLOT_INPUT1_X = 19;
	public static final int SLOT_INPUT2_X = 47;

	public static final int SLOTS_ROW1_Y = 17;

	public static final int SLOT_OUTPUT_X = 116;
	public static final int SLOT_OUTPUT_Y = 50;

	public static final int SLOT_UPGRADE_X = 150;
	public static final int SLOT_UPGRADE_Y = 50;

	public BlastFurnaceContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<BlastFurnaceTileEnt>getClientTile(player, buf));
	}

	public BlastFurnaceContainer(int id, Inventory player, BlastFurnaceTileEnt ent) {
		super(TGMachines.BLAST_FURNACE_CONTAINER, id, player, ent);
		this.tile = ent;

		ItemStackHandlerPlus handler = ent.getInventory();

		this.addSlot(new SlotMachineInputBG(handler, BlastFurnaceTileEnt.SLOT_INPUT1, SLOT_INPUT1_X, SLOTS_ROW1_Y, ITGSlotBackground.INGOTSLOT_TEX));
		//1.12 SlotFabricator.FABRICATOR_SLOTTEX_POWDER = emptyslot_powder
		this.addSlot(new SlotMachineInputBG(handler, BlastFurnaceTileEnt.SLOT_INPUT2, SLOT_INPUT2_X, SLOTS_ROW1_Y, ITGSlotBackground.POWDERSLOT_TEX));

		this.addSlot(new SlotItemHandlerOutput(handler, BlastFurnaceTileEnt.SLOT_OUTPUT, SLOT_OUTPUT_X, SLOT_OUTPUT_Y));
		this.addSlot(new SlotMachineUpgrade(handler, BlastFurnaceTileEnt.SLOT_UPGRADE, SLOT_UPGRADE_X, SLOT_UPGRADE_Y));

		this.addPlayerInventorySlots(player);
	}

	public BlastFurnaceTileEnt getBlastFurnace() {
		return tile;
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		return this.transferStackInSlotMachine(ply, id, BlastFurnaceTileEnt.SLOT_UPGRADE, this.tile::getValidSlotForItemInMachine);
	}

}
