package techguns.tileentities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import techguns.TGConfig;

/**
 * Machine with an internal Forge Energy storage, exposed on every side with the ForgeCapabilities.ENERGY capability
 */
public abstract class BasicPoweredTileEnt extends BasicRedstoneTileEnt {

	protected EnergyStoragePlus energy;

	protected LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> this.energy);

	public BasicPoweredTileEnt(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, boolean hasRotation, int maximumPower) {
		super(type, pos, state, inventorySize, hasRotation);
		energy = new EnergyStoragePlus(maximumPower) {
			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				int received = super.receiveEnergy(maxReceive, simulate);
				if (!simulate && received > 0) {
					BasicPoweredTileEnt.this.setChanged();
				}
				return received;
			}
		};
	}

	public EnergyStoragePlus getEnergyStorage() {
		return energy;
	}

	/**
	 * Drain amount power from internal storage, return if enough power left
	 * @param amount
	 * @return
	 */
	protected boolean consumePower(int amount) {
		if (TGConfig.machinesNeedNoPower) {
			return true;
		}
		if (this.energy.extractEnergy(amount, true) >= amount) {
			return this.energy.extractEnergy(amount, false) >= amount;
		} else {
			return false;
		}
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		this.energy.setEnergyStored(tags.getInt("powerStored"));
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putInt("powerStored", this.energy.getEnergyStored());
	}

	@Override
	public <T> @Nonnull LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ENERGY && !this.remove) {
			return energyCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		energyCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		energyCap = LazyOptional.of(() -> this.energy);
	}
}
