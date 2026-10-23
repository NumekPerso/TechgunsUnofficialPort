package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import techguns.TGMachines;
import techguns.TGPackets;
import techguns.gui.widgets.ITGSlotBackground;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInput;
import techguns.gui.widgets.SlotMachineInputBG;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.packets.PacketUpdateTileEntTanks;
import techguns.tileentities.ChemLabTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class ChemLabContainer extends BasicMachineContainer {

	public static final int SLOT_INPUT1_X = 35;
	public static final int SLOT_INPUT2_X = 57;

	public static final int SLOT_OUTPUT_X = 135;

	public static final int SLOTS_ROW1_Y = 17;
	public static final int SLOTS_ROW2_Y = 40;

	public static final int SLOT_UPGRADE_X = SLOT_OUTPUT_X;
	//1.12 MetalPressContainer.SLOT_OUTPUTS_Y
	public static final int SLOT_UPGRADE_Y = 60;

	FluidStack lastInputtank = null;
	FluidStack lastOutputtank = null;

	protected Player player;

	ChemLabTileEnt tile;

	/**
	 * client constructor, the block position is sent by the server
	 */
	public ChemLabContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<ChemLabTileEnt>getClientTile(player, buf));
	}

	public ChemLabContainer(int id, Inventory player, ChemLabTileEnt ent) {
		super(TGMachines.CHEM_LAB_CONTAINER, id, player, ent);
		this.tile = ent;
		this.player = player.player;

		ItemStackHandlerPlus handler = ent.getInventory();

		this.addSlot(new SlotMachineInput(handler, ChemLabTileEnt.SLOT_INPUT1, SLOT_INPUT1_X, SLOTS_ROW1_Y));
		this.addSlot(new SlotMachineInput(handler, ChemLabTileEnt.SLOT_INPUT2, SLOT_INPUT2_X, SLOTS_ROW1_Y));
		this.addSlot(new SlotMachineInputBG(handler, ChemLabTileEnt.SLOT_BOTTLE, SLOT_INPUT1_X, SLOTS_ROW2_Y, ITGSlotBackground.BOTTLESLOT_TEX));

		this.addSlot(new SlotItemHandlerOutput(handler, ChemLabTileEnt.SLOT_OUTPUT, SLOT_OUTPUT_X, SLOTS_ROW1_Y));
		this.addSlot(new SlotMachineUpgrade(handler, ChemLabTileEnt.SLOT_UPGRADE, SLOT_UPGRADE_X, SLOT_UPGRADE_Y));

		this.addPlayerInventorySlots(player);
		//drain mode: synced by the tile (needUpdate), tanks: PacketUpdateTileEntTanks
	}

	public ChemLabTileEnt getChemLab() {
		return tile;
	}

	/**
	 * 1.12 detectAndSendChanges sent the tanks every tick, only send them when they changed
	 */
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		if (this.player instanceof ServerPlayer sply) {
			if (fluidsChanged(lastInputtank, this.tile.getCurrentInputFluid()) || fluidsChanged(lastOutputtank, this.tile.getCurrentOutputFluid())) {
				TGPackets.network.sendTo(new PacketUpdateTileEntTanks(this.tile, this.tile.getBlockPos()), sply);
				this.lastInputtank = this.tile.getCurrentInputFluid().copy();
				this.lastOutputtank = this.tile.getCurrentOutputFluid().copy();
			}
		}
	}

	private boolean fluidsChanged(FluidStack f1, FluidStack f2) {
		if (f1 == null) {
			return true;
		}
		return !(f1.isFluidEqual(f2) && f1.getAmount() == f2.getAmount()) && !(f1.isEmpty() && f2.isEmpty());
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		return this.transferStackInSlotMachine(ply, id, ChemLabTileEnt.SLOT_UPGRADE, tile::getValidSlotForItemInMachine);
	}
}
