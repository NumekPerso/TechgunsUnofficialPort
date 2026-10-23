package techguns.gui.widgets;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import techguns.api.guns.IGenericGun;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class SlotTurretGun extends SlotMachineInput implements ITGSlotBackground {

	public SlotTurretGun(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.getItem() instanceof IGenericGun;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}

	@Override
	public ResourceLocation getBackgroundTexture() {
		return ITGSlotBackground.TURRETGUNSLOT_TEX;
	}
}
