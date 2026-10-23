package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.gui.widgets.SlotFabricator;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInput;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.tileentities.FabricatorTileEntMaster;
import techguns.tileentities.operation.FabricatorRecipe;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class FabricatorContainer extends BasicMachineContainer {
	protected FabricatorTileEntMaster tile;

	public static final int SLOT_INPUT1_X = 19;
	public static final int SLOT_WIRES_X = 47;
	public static final int SLOT_POWDER_X = 68;
	public static final int SLOT_PLATE_X = 89;

	public static final int SLOTS_ROW1_Y = 17;

	public static final int SLOT_OUTPUT_X = 116;
	public static final int SLOT_OUTPUT_Y = 50;

	public static final int SLOT_UPGRADE_X = 150;
	public static final int SLOT_UPGRADE_Y = 50;

	/**
	 * client constructor, the block position is sent by the server
	 */
	public FabricatorContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<FabricatorTileEntMaster>getClientTile(player, buf));
	}

	public FabricatorContainer(int id, Inventory player, FabricatorTileEntMaster ent) {
		super(TGMachines.FABRICATOR_CONTAINER, id, player, ent);
		this.tile = ent;

		ItemStackHandlerPlus handler = ent.getInventory();

		this.addSlot(new SlotMachineInput(handler, FabricatorTileEntMaster.SLOT_INPUT1, SLOT_INPUT1_X, SLOTS_ROW1_Y));
		this.addSlot(new SlotFabricator(handler, FabricatorTileEntMaster.SLOT_WIRES, SLOT_WIRES_X, SLOTS_ROW1_Y, FabricatorRecipe.items_wireslot, SlotFabricator.FABRICATOR_SLOTTEX_WIRES));
		this.addSlot(new SlotFabricator(handler, FabricatorTileEntMaster.SLOT_POWDER, SLOT_POWDER_X, SLOTS_ROW1_Y, FabricatorRecipe.items_powderslot, SlotFabricator.FABRICATOR_SLOTTEX_POWDER));
		this.addSlot(new SlotFabricator(handler, FabricatorTileEntMaster.SLOT_PLATE, SLOT_PLATE_X, SLOTS_ROW1_Y, FabricatorRecipe.items_plateslot, SlotFabricator.FABRICATOR_SLOTTEX_PLATE));

		this.addSlot(new SlotItemHandlerOutput(handler, FabricatorTileEntMaster.SLOT_OUTPUT, SLOT_OUTPUT_X, SLOT_OUTPUT_Y));
		this.addSlot(new SlotMachineUpgrade(handler, FabricatorTileEntMaster.SLOT_UPGRADE, SLOT_UPGRADE_X, SLOT_UPGRADE_Y));

		this.addPlayerInventorySlots(player);
	}

	public FabricatorTileEntMaster getFabricator() {
		return tile;
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		return this.transferStackInSlotMachine(ply, id, FabricatorTileEntMaster.SLOT_UPGRADE, tile::getValidSlotForItemInMachine);
	}

}
