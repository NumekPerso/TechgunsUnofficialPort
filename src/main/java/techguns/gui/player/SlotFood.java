package techguns.gui.player;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotFood extends Slot {

	public SlotFood(Container inv, int id, int x, int y) {
		super(inv, id, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack item) {
		return item.isEdible();
	}
}
