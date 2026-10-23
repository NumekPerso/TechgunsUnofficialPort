package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInput;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.tileentities.GrinderTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class GrinderContainer extends BasicMachineContainer {
	protected GrinderTileEnt tile;

	public static final int SLOT_INPUT_X = 18;

	public static final int SLOTS_INPUT_Y = 17;

	public static final int SLOT_OUTPUT0_X = 80;

	public static final int SLOT_UPGRADE_Y = 60;

	public static final int SLOT_UPGRADE_X = 152;

	public GrinderContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<GrinderTileEnt>getClientTile(player, buf));
	}

	public GrinderContainer(int id, Inventory player, GrinderTileEnt ent) {
		super(TGMachines.GRINDER_CONTAINER, id, player, ent);
		this.tile = ent;

		ItemStackHandlerPlus handler = ent.getInventory();

		this.addSlot(new SlotMachineInput(handler, GrinderTileEnt.SLOT_INPUT, SLOT_INPUT_X, SLOTS_INPUT_Y));
		this.addSlot(new SlotMachineUpgrade(handler, GrinderTileEnt.SLOT_UPGRADE, SLOT_UPGRADE_X, SLOT_UPGRADE_Y));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new SlotItemHandlerOutput(handler, GrinderTileEnt.SLOT_OUTPUT0 + i * 3 + j, SLOT_OUTPUT0_X + j * 18, SLOTS_INPUT_Y + i * 18));
			}
		}

		this.addPlayerInventorySlots(player);
	}

	public GrinderTileEnt getGrinder() {
		return tile;
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		//machine slots 0-10 (input, upgrade, 9 outputs), then the player inventory
		return this.transferStackInSlotMachine(ply, id, GrinderTileEnt.SLOT_OUTPUT_LAST - 1, this.tile::getValidSlotForItemInMachine);
	}

}
