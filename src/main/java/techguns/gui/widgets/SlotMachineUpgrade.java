package techguns.gui.widgets;

import net.minecraft.world.item.ItemStack;
import techguns.tileentities.BasicMachineTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class SlotMachineUpgrade extends SlotMachineInput {

	public static final int MAX_UPGRADES = 7;

	public SlotMachineUpgrade(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack item) {
		return BasicMachineTileEnt.isMachineUpgrade(item);
	}

	@Override
	public int getMaxStackSize() {
		return MAX_UPGRADES;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return Math.min(MAX_UPGRADES, super.getMaxStackSize(stack));
	}

}
