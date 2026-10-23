package techguns.gui.widgets;

import net.minecraft.resources.ResourceLocation;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class SlotMachineInputBG extends SlotMachineInput implements ITGSlotBackground {

	protected ResourceLocation bgtexture;

	public SlotMachineInputBG(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition, ResourceLocation bgtexture) {
		super(itemHandler, index, xPosition, yPosition);
		this.bgtexture = bgtexture;
	}

	@Override
	public ResourceLocation getBackgroundTexture() {
		return bgtexture;
	}
}
