package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import techguns.TGMachines;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.gui.containers.FabricatorContainer;
import techguns.tileentities.operation.FabricatorRecipe;
import techguns.tileentities.operation.FabricatorRecipe.RecipeData;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.tileentities.operation.MachineSlotItem;

public class FabricatorTileEntMaster extends MultiBlockMachineTileEntMaster {

	protected static final float SOUND_VOLUME = 0.5f;

	public static final int SLOT_INPUT1 = 0;
	public static final int SLOT_WIRES = 1;
	public static final int SLOT_POWDER = 2;
	public static final int SLOT_PLATE = 3;
	public static final int SLOT_OUTPUT = 4;
	public static final int SLOT_UPGRADE = 5;

	public MachineSlotItem input;
	public MachineSlotItem wireslot;
	public MachineSlotItem powderslot;
	public MachineSlotItem plateslot;

	public static final int powerPerTick = 80;

	public FabricatorTileEntMaster(BlockPos pos, BlockState state) {
		super(TGMachines.FABRICATOR_TILE_MASTER, pos, state, 6, 100000);

		input = new MachineSlotItem(this, SLOT_INPUT1);
		wireslot = new MachineSlotItem(this, SLOT_WIRES);
		powderslot = new MachineSlotItem(this, SLOT_POWDER);
		plateslot = new MachineSlotItem(this, SLOT_PLATE);

		this.inventory = new ItemStackHandlerPlus(6) {

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setContentsChanged(true);
			}

			@Override
			protected boolean allowItemInSlot(int slot, ItemStack stack) {
				switch (slot) {
				case SLOT_INPUT1:
				case SLOT_WIRES:
				case SLOT_POWDER:
				case SLOT_PLATE:
					return FabricatorTileEntMaster.this.isItemValidForSlot(slot, stack); //qualified: ItemStackHandlerPlus has its own isItemValidForSlot that calls allowItemInSlot
				case SLOT_OUTPUT:
					return false;
				case SLOT_UPGRADE:
					return isMachineUpgrade(stack);
				}
				return false;
			}

			@Override
			protected boolean allowExtractFromSlot(int slot, int amount) {
				return slot == SLOT_OUTPUT;
			}
		};
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return this.getValidSlotForItemInMachine(stack) == slot;
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Techguns.MODID + ".container.fabricator");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new FabricatorContainer(id, inv, this);
	}

	@Override
	public int getNeededPower() {
		return powerPerTick;
	}

	@Override
	public AABB getRenderBoundingBox() {
		if (this.isFormed() && multiblockDirection != null) {
			BlockPos p = this.getBlockPos();
			Direction left = multiblockDirection.getClockWise();

			BlockPos other = p.relative(multiblockDirection, 2).relative(left, 2).relative(Direction.UP, 2);
			BlockPos first = p.relative(multiblockDirection.getOpposite(), 2).relative(left.getOpposite(), 1).relative(Direction.DOWN, 1);
			return new AABB(first, other);
		} else {
			return super.getRenderBoundingBox();
		}
	}

	@Override
	protected void checkAndStartOperation() {
		this.setContentsChanged(false);
		RecipeData data = FabricatorRecipe.getRecipeDataFor(this.inventory.getStackInSlot(SLOT_INPUT1), this.inventory.getStackInSlot(FabricatorTileEntMaster.SLOT_WIRES), this.inventory.getStackInSlot(FabricatorTileEntMaster.SLOT_POWDER), this.inventory.getStackInSlot(FabricatorTileEntMaster.SLOT_PLATE), this.getMaxMachineUpgradeMultiplier(SLOT_UPGRADE), this.inventory.getStackInSlot(FabricatorTileEntMaster.SLOT_OUTPUT));
		if (data != null) {

			this.currentOperation = data.createOperation(this);

			this.input.consume(data.inputConsumption * data.stackMultiplier);
			this.wireslot.consume(data.wireConsumption * data.stackMultiplier);
			this.powderslot.consume(data.powderConsumption * data.stackMultiplier);
			this.plateslot.consume(data.plateConsumption * data.stackMultiplier);

			this.totaltime = 100;
			this.progress = 0;

			if (!this.level.isClientSide) {
				this.needUpdate();
			}
		}
	}

	@Override
	protected void finishedOperation() {

		ItemStack itemOut = this.currentOperation.getItemOutput0();
		if (!itemOut.isEmpty()) {
			if (!this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
				this.inventory.insertItemNoCheck(SLOT_OUTPUT, itemOut, false);
			} else {
				this.inventory.setStackInSlot(SLOT_OUTPUT, itemOut);
			}
		}
	}

	@Override
	protected void playAmbientSound() {
		if (this.level.isClientSide && this.progress == 1) {
			//machines.fabricatorWork
			level.playLocalSound(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), TGSounds.FABRICATOR_WORK, SoundSource.BLOCKS, SOUND_VOLUME, 1.0F, true);
		}
	}

	public int getValidSlotForItemInMachine(ItemStack stack) {
		if (FabricatorRecipe.itemStackInList(FabricatorRecipe.items_wireslot, stack)) {
			return SLOT_WIRES;
		}
		if (FabricatorRecipe.itemStackInList(FabricatorRecipe.items_powderslot, stack)) {
			return SLOT_POWDER;
		}
		if (FabricatorRecipe.itemStackInList(FabricatorRecipe.items_plateslot, stack)) {
			return SLOT_PLATE;
		}

		if (isMachineUpgrade(stack)) {
			return SLOT_UPGRADE;
		}

		return SLOT_INPUT1;
	}

	@Override
	public AABB getBBforSlave(BlockPos slavePos) {
		return new AABB(0, 0, 0, 1, 1, 1);
	}

}
