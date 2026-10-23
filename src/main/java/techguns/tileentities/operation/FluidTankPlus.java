package techguns.tileentities.operation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import techguns.tileentities.BasicInventoryTileEnt;

/**
 * Fluid tank of a machine, marks the machine contents as changed when the fluid changes.
 * (In 1.20 the FluidTank only calls onContentsChanged when something was actually filled/drained, like the 1.12 override did)
 */
public class FluidTankPlus extends FluidTank {

	BasicInventoryTileEnt tile;

	public FluidTankPlus(BasicInventoryTileEnt tile, int capacity) {
		super(capacity);
		this.tile = tile;
	}

	@Override
	protected void onContentsChanged() {
		super.onContentsChanged();
		if (this.tile != null) {
			this.tile.setContentsChanged(true);
		}
	}

	/**
	 * 1.12 fillInternal: fill ignoring the side restrictions of the machine (the 1.20 tank has none itself)
	 */
	public int fillInternal(FluidStack resource, FluidAction action) {
		return super.fill(resource, action);
	}
}
