package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import techguns.tileentities.operation.MachineOperation;

/**
 * Powered machine that runs MachineOperations (1.12 BasicMachineTileEnt).
 * update() runs on both sides (the client mirrors the progress for animations and sounds), new operations are only
 * started on the server.
 */
public abstract class BasicMachineTileEnt extends BasicPoweredTileEnt {

	public MachineOperation currentOperation = null;
	public int totaltime = 0;
	public int progress = 0;

	public BasicMachineTileEnt(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, boolean hasRotation, int maximumPower) {
		super(type, pos, state, inventorySize, hasRotation, maximumPower);
	}

	/**
	 * 1.12 TGItems.isMachineUpgrade
	 */
	public static boolean isMachineUpgrade(ItemStack stack) {
		return techguns.items.TGSharedItem.isMachineUpgrade(stack);
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		this.totaltime = tags.getInt("totaltime");
		this.progress = tags.getInt("progress");

		this.readOperationFromNBT(tags);
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putInt("totaltime", this.totaltime);
		tags.putInt("progress", this.progress);

		if (this.currentOperation != null) {
			this.currentOperation.writeToNBT(tags);
		}
	}

	public void readOperationFromNBT(CompoundTag tags) {
		if (tags.contains("operation")) {
			this.currentOperation = new MachineOperation(null, null, null, null, 0);
			this.currentOperation.readFromNBT(tags);
		} else {
			this.currentOperation = null;
		}
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely
	 * cooked
	 */
	public int getProgressScaled(int limit) {
		if (totaltime != 0) {
			return this.progress * limit / this.totaltime;
		} else {
			return 0;
		}
	}

	/**
	 * Returns an integer between 0 and the passed value representing the stored power
	 */
	public int getPowerScaled(int limit) {
		if (this.energy.getMaxEnergyStored() != 0) {
			return this.energy.getEnergyStored() * limit / this.energy.getMaxEnergyStored();
		} else {
			return 0;
		}
	}

	public float getProgress() {
		if (this.totaltime > 0) {
			return (float) this.progress / (float) this.totaltime;
		}
		return 0.0f;
	}

	public boolean isWorking() {
		return this.currentOperation != null;
	}

	public ItemStack onFluidContainerInteract(ItemStack fluidContainer) {
		return fluidContainer;
	}

	protected abstract int getNeededPower();

	public int getMaxMachineUpgradeMultiplier(int slot) {
		ItemStack upg = this.inventory.getStackInSlot(slot);
		if (isMachineUpgrade(upg)) {
			return 1 + upg.getCount();
		}
		return 1;
	}

	/**
	 * used by multiblocks to disable machine when broken
	 * @return
	 */
	protected boolean enabled() {
		return true;
	}

	@Override
	public void update() {
		if (this.isRedstoneEnabled() && this.enabled()) {
			if (this.currentOperation != null) {

				if (this.consumePower(this.getNeededPower() * this.currentOperation.getStackMultiplier())) {
					this.progress++;

					playAmbientSound();

					if (progress >= totaltime) {

						if (!this.level.isClientSide) {
							this.finishedOperation();
						}
						this.progress = 0;
						this.totaltime = 0;
						this.currentOperation = null;

						if (!this.level.isClientSide) {
							checkAndStartOperation();
							this.needUpdate();
						}
					} else if (!this.level.isClientSide) {
						this.setChanged();
					}
				}

			} else {

				if (!this.level.isClientSide && this.contentsChanged) {
					checkAndStartOperation();
				}

			}
		}

	}

	protected abstract void checkAndStartOperation();

	protected abstract void finishedOperation();

	protected abstract void playAmbientSound();

}
