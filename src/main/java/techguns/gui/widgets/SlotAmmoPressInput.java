package techguns.gui.widgets;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import techguns.tileentities.operation.AmmoPressBuildPlans;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class SlotAmmoPressInput extends SlotMachineInput implements ITGSlotBackground {

	protected ResourceLocation bgtex;

	public SlotAmmoPressInput(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition, ResourceLocation bgtex) {
		super(itemHandler, index, xPosition, yPosition);
		this.bgtex = bgtex;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return AmmoPressBuildPlans.isValidFor(stack, index);
	}

	@Override
	public ResourceLocation getBackgroundTexture() {
		return bgtex;
	}

}
