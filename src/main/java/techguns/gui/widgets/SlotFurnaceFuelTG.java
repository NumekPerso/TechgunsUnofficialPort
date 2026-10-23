package techguns.gui.widgets;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import techguns.tileentities.operation.ItemStackHandlerPlus;

/**
 * Slot that only accepts furnace fuel (1.12 TileEntityFurnace.getItemBurnTime)
 */
public class SlotFurnaceFuelTG extends SlotMachineInput {

	public SlotFurnaceFuelTG(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack item) {
		return ForgeHooks.getBurnTime(item, null) > 0;
	}

}
