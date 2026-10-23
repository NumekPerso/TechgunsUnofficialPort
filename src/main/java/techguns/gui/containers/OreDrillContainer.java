package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import techguns.TGMachines;
import techguns.TGPackets;
import techguns.packets.PacketUpdateTileEntTanks;
import techguns.gui.widgets.SlotDrill;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInput;
import techguns.tileentities.OreDrillTileEntMaster;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class OreDrillContainer extends BasicMachineContainer {

	OreDrillTileEntMaster tile;

	public static final int SLOT_DRILL_X = 30;
	public static final int SLOTS_ROW1_Y = 17;
	public static final int SLOTS_FURNACE_Y = 51;

	public static final int SLOTS_OUTPUT_X = 102;

	/**
	 * client constructor, the block position is sent by the server
	 */
	public OreDrillContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<OreDrillTileEntMaster>getClientTile(player, buf));
	}

	public OreDrillContainer(int id, Inventory player, OreDrillTileEntMaster ent) {
		super(TGMachines.OREDRILL_CONTAINER, id, player, ent);
		tile = ent;

		ItemStackHandlerPlus handler = ent.getInventory();

		this.addSlot(new SlotDrill(handler, OreDrillTileEntMaster.SLOT_DRILL, SLOT_DRILL_X, SLOTS_ROW1_Y, tile));
		this.addSlot(new SlotFurnaceFuel(handler, OreDrillTileEntMaster.SLOT_FURNACE, SLOT_DRILL_X, SLOTS_FURNACE_Y));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new SlotItemHandlerOutput(handler, OreDrillTileEntMaster.SLOT_OUTPUT1 + (i * 3) + j, SLOTS_OUTPUT_X + 18 * i, SLOTS_ROW1_Y + 18 * j));
			}
		}

		this.addPlayerInventorySlots(player);

		//1.12 FIELD_SYNC_ID_FUELBUFFER, FIELD_SYNC_ID_FUELBUFFER_MAX
		this.addSyncedInt(() -> this.tile.getFuelBuffer(), v -> this.tile.setFuelBuffer(v));
		this.addSyncedInt(() -> this.tile.getCurrentFuelBufferMax(), v -> this.tile.setCurrentFuelBufferMax(v));
		this.player = player.player;
	}

	protected Player player;
	protected FluidStack lastInputtank = null;
	protected FluidStack lastOutputtank = null;

	/**
	 * 1.12 sent PacketUpdateTileEntTanks every tick, only send it when the tanks changed
	 */
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		if (this.player instanceof ServerPlayer sply) {
			if (lastInputtank == null || !lastInputtank.isFluidStackIdentical(this.tile.inputTank.getFluid()) || lastOutputtank == null || !lastOutputtank.isFluidStackIdentical(this.tile.outputTank.getFluid())) {
				TGPackets.network.sendTo(new PacketUpdateTileEntTanks(this.tile, this.tile.getBlockPos()), sply);
				this.lastInputtank = this.tile.inputTank.getFluid().copy();
				this.lastOutputtank = this.tile.outputTank.getFluid().copy();
			}
		}
	}

	public OreDrillTileEntMaster getOreDrill() {
		return tile;
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		ItemStack stack = ItemStack.EMPTY;
		net.minecraft.world.inventory.Slot slot = this.slots.get(id);

		if (slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			if (!stack.isEmpty()) {

				int machine_inventory_end = OreDrillTileEntMaster.SLOT_OUTPUT1 + OreDrillTileEntMaster.SLOT_OUTPUT_COUNT;

				if (id == OreDrillTileEntMaster.SLOT_DRILL) {
					//NO SHIFT+CLICK OUT!!!
					return ItemStack.EMPTY;
				} else if (id >= OreDrillTileEntMaster.SLOT_FURNACE && id < machine_inventory_end) {

					//PRESSED IN MACHINE GUI 2-10 output, 1 fuelinput
					if (!this.moveItemStackTo(stack1, machine_inventory_end, machine_inventory_end + 36, false)) {
						return ItemStack.EMPTY;
					}
					slot.onQuickCraft(stack1, stack);
				} else if (id >= machine_inventory_end && id < machine_inventory_end + 36) {

					int validslot = tile.getValidSlotForItemInMachine(stack1);
					if (validslot >= 0) {

						if (!this.moveItemStackTo(stack1, validslot, validslot + 1, false)) {
							return ItemStack.EMPTY;
						}
						slot.onQuickCraft(stack1, stack);

					} else {
						return ItemStack.EMPTY;
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

				slot.onTake(ply, stack1);
			}
		}

		return stack;
	}

	/**
	 * 1.12 SlotFurnaceFuelTG: accepts furnace fuel
	 */
	public static class SlotFurnaceFuel extends SlotMachineInput {

		public SlotFurnaceFuel(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return ForgeHooks.getBurnTime(stack, null) > 0;
		}
	}

}
