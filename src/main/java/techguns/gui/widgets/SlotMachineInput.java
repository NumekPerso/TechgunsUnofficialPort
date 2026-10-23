package techguns.gui.widgets;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import techguns.tileentities.operation.ItemStackHandlerPlus;

/**
 * Machine input slot: accepts what the machine accepts from automation, the player can take items back out
 * (automation can't extract input slots)
 */
public class SlotMachineInput extends SlotItemHandler {

	protected final int index;
	protected ItemStackHandlerPlus inventory;

	public SlotMachineInput(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.index = index;
		this.inventory = itemHandler;
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return !stack.isEmpty() && inventory.isItemValidForSlot(index, stack);
	}

	/**
	 * Return whether this slot's stack can be taken from this slot.
	 */
	@Override
	public boolean mayPickup(Player playerIn) {
		return !inventory.extractWithoutCheck(index, 1, true).isEmpty();
	}

	/**
	 * Decrease the size of the stack in slot by the amount of the int arg. Returns the new stack.
	 */
	@Override
	@Nonnull
	public ItemStack remove(int amount) {
		return inventory.extractWithoutCheck(index, amount, false);
	}

}
