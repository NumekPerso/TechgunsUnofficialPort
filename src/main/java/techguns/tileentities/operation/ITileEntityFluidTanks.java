package techguns.tileentities.operation;

import net.minecraft.nbt.CompoundTag;

public interface ITileEntityFluidTanks {
	public void saveTanksToNBT(CompoundTag tags);

	public void loadTanksFromNBT(CompoundTag tags);
}
