package techguns.tileentities.operation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MachineSlotFluid {

	FluidTank tank;

	public MachineSlotFluid(FluidTank tank) {
		super();
		this.tank = tank;
	}

	/**
	 * consumes a specific amount, without checks
	 */
	public void consume(int amount) {
		if (amount > 0) {
			this.tank.drain(amount, FluidAction.EXECUTE);
		}
	}

	public FluidStack get() {
		return tank.getFluid();
	}

	public boolean isEmpty() {
		return tank.getFluid().isEmpty();
	}

	public boolean canConsume(int amount) {
		if (amount <= 0) {
			return true;
		} else if (!this.isEmpty() && this.get().getAmount() >= amount) {
			return true;
		}
		return false;
	}
}
