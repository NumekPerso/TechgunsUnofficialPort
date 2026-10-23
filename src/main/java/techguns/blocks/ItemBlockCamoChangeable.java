package techguns.blocks;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import techguns.items.armors.ICamoChangeable;

/**
 * Item of the camo changeable blocks: switching the camo changes the block state type stored in the stack
 * (1.12 switched the item damage).
 */
public class ItemBlockCamoChangeable<T extends Enum<T> & StringRepresentable> extends GenericItemBlockMeta<T> implements ICamoChangeable {

	protected final GenericBlockMetaEnumCamoChangeable<T> camoblock;

	public ItemBlockCamoChangeable(GenericBlockMetaEnumCamoChangeable<T> block, String name) {
		super(block, name);
		this.camoblock = block;
	}

	@Override
	public int getCamoCount() {
		return camoblock.getCamoCount();
	}

	@Override
	public int switchCamo(ItemStack item, boolean back) {
		T next = camoblock.switchCamo(camoblock.getTypeFromStack(item), back);
		camoblock.setTypeForStack(item, next);
		return camoblock.getCurrentCamoIndex(next);
	}

	@Override
	public int getCurrentCamoIndex(ItemStack item) {
		return camoblock.getCurrentCamoIndex(camoblock.getTypeFromStack(item));
	}

	@Override
	public Component getCurrentCamoName(ItemStack item) {
		return camoblock.getCurrentCamoName(camoblock.getTypeFromStack(item));
	}
}
