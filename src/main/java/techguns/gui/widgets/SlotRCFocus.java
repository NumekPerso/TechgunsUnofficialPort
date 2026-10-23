package techguns.gui.widgets;

import net.minecraft.world.item.ItemStack;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.tileentities.operation.ReactionBeamFocus;

public class SlotRCFocus extends SlotMachineInput {

	public SlotRCFocus(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack item) {
		if (!item.isEmpty() && item.getItem() instanceof ITGSpecialSlot) {
			ITGSpecialSlot itm = (ITGSpecialSlot) item.getItem();
			return itm.getSlot(item) == TGSlotType.REACTION_CHAMBER_FOCUS && ReactionBeamFocus.getBeamFocus(item) != null;
		}
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}

}
