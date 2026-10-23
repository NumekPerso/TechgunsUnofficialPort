package techguns.blocks;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * The camo of these blocks is the "type" block state property, the item (ItemBlockCamoChangeable, implements ICamoChangeable)
 * cycles it in the stack's BlockStateTag, so the camo bench can use it like the 1.12 one switched the item damage.
 */
public class GenericBlockMetaEnumCamoChangeable<T extends Enum<T> & StringRepresentable> extends GenericBlockMetaEnum<T> {

	public GenericBlockMetaEnumCamoChangeable(Properties props, EnumProperty<T> type) {
		super(props, type);
	}

	public int getCamoCount() {
		return clazz.getEnumConstants().length;
	}

	/**
	 * Next (or previous) camo of the given variant
	 */
	public T switchCamo(T current, boolean back) {
		int type = current.ordinal();
		if (back) {
			type--;
			if (type < 0) {
				type = clazz.getEnumConstants().length - 1;
			}
		} else {
			type++;
			if (type >= clazz.getEnumConstants().length) {
				type = 0;
			}
		}
		return clazz.getEnumConstants()[type];
	}

	public int getCurrentCamoIndex(T current) {
		return current.ordinal();
	}

	/**
	 * 1.12: "tile.techguns.<name>.<meta>.name", now the name of the variant's item
	 */
	public Component getCurrentCamoName(T current) {
		return Component.translatable(this.getDescriptionId() + "." + current.getSerializedName());
	}

	@Override
	public BlockItem createItemBlock(String name) {
		return new ItemBlockCamoChangeable<T>(this, name);
	}
}
