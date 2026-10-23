package techguns.gui.widgets;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;
import techguns.tileentities.operation.ItemStackHandlerPlus;

public class SlotTurretArmor extends SlotMachineInput implements ITGSlotBackground {

	public SlotTurretArmor(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return (stack.getItem() instanceof ITGSpecialSlot && ((ITGSpecialSlot) stack.getItem()).getSlot(stack) == TGSlotType.TURRETARMOR);
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
		return ITGSlotBackground.TURRETARMORSLOT_TEX;
	}

}
