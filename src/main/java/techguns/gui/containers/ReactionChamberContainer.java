package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import techguns.TGMachines;
import techguns.TGPackets;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInput;
import techguns.gui.widgets.SlotRCFocus;
import techguns.packets.PacketUpdateTileEntTanks;
import techguns.tileentities.ReactionChamberTileEntMaster;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class ReactionChamberContainer extends BasicMachineContainer {
	ReactionChamberTileEntMaster tile;

	public static final int SLOT_INPUT_X = 35;
	public static final int SLOT_FOCUS_X = 93;

	public static final int SLOTS_ROW1_Y = 17;

	public static final int SLOT_OUTPUT_X = 134;
	public static final int SLOT_OUTPUT_Y = 17;

	protected Player player;
	protected FluidStack lastInputtank = null;

	/**
	 * client constructor, the block position is sent by the server
	 */
	public ReactionChamberContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<ReactionChamberTileEntMaster>getClientTile(player, buf));
	}

	public ReactionChamberContainer(int id, Inventory player, ReactionChamberTileEntMaster ent) {
		super(TGMachines.REACTIONCHAMBER_CONTAINER, id, player, ent);
		this.tile = ent;
		this.player = player.player;

		ItemStackHandlerPlus handler = ent.getInventory();

		this.addSlot(new SlotMachineInput(handler, ReactionChamberTileEntMaster.SLOT_INPUT, SLOT_INPUT_X, SLOTS_ROW1_Y));

		this.addSlot(new SlotRCFocus(handler, ReactionChamberTileEntMaster.SLOT_FOCUS, SLOT_FOCUS_X, SLOTS_ROW1_Y));

		this.addSlot(new SlotItemHandlerOutput(handler, ReactionChamberTileEntMaster.SLOT_OUTPUT, SLOT_OUTPUT_X, SLOT_OUTPUT_Y));
		this.addSlot(new SlotItemHandlerOutput(handler, ReactionChamberTileEntMaster.SLOT_OUTPUT + 1, SLOT_OUTPUT_X + 18, SLOT_OUTPUT_Y));
		this.addSlot(new SlotItemHandlerOutput(handler, ReactionChamberTileEntMaster.SLOT_OUTPUT + 2, SLOT_OUTPUT_X, SLOT_OUTPUT_Y + 18));
		this.addSlot(new SlotItemHandlerOutput(handler, ReactionChamberTileEntMaster.SLOT_OUTPUT + 3, SLOT_OUTPUT_X + 18, SLOT_OUTPUT_Y + 18));

		this.addPlayerInventorySlots(player);

		//1.12 FIELD_SYNC_ID_LIQUIDLEVEL, FIELD_SYNC_ID_INTENSITY
		this.addSyncedShort(() -> this.tile.getLiquidLevel(), v -> this.tile.setLiquidLevel((byte) v));
		this.addSyncedShort(() -> this.tile.getIntensity(), v -> this.tile.setIntensity((byte) v));
	}

	public ReactionChamberTileEntMaster getReactionChamber() {
		return tile;
	}

	/**
	 * 1.12 sent the tank every tick, only send it when it changed
	 */
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if (this.player instanceof ServerPlayer sply) {
			FluidStack current = this.tile.inputTank.getFluid();
			if (lastInputtank == null || !(lastInputtank.isFluidEqual(current) && lastInputtank.getAmount() == current.getAmount())) {
				TGPackets.network.sendTo(new PacketUpdateTileEntTanks(this.tile, this.tile.getBlockPos()), sply);
				this.lastInputtank = current.copy();
			}
		}
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		int HIGHEST_MACHINE_SLOT = ReactionChamberTileEntMaster.SLOT_OUTPUT + ReactionChamberTileEntMaster.OUTPUT_SLOTS_COUNT - 1;
		return this.transferStackInSlotMachine(ply, id, HIGHEST_MACHINE_SLOT, tile::getValidSlotForItemInMachine);
	}
}
