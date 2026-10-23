package techguns.tileentities.operation;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Recipe display interface (used by JEI in 1.12)
 */
public interface IMachineRecipe {
	//public MachineOperation createNewOperation(int stackMultiplier);

	public default List<List<ItemStack>> getItemInputs() {
		return new ArrayList<List<ItemStack>>();
	}

	public default List<List<ItemStack>> getItemOutputs() {
		return new ArrayList<List<ItemStack>>();
	}

	public default List<List<FluidStack>> getFluidInputs() {
		return new ArrayList<List<FluidStack>>();
	}

	public default List<List<FluidStack>> getFluidOutputs() {
		return new ArrayList<List<FluidStack>>();
	}
}
