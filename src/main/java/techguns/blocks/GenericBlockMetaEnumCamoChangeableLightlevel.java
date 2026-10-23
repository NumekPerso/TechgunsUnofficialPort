package techguns.blocks;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class GenericBlockMetaEnumCamoChangeableLightlevel<T extends Enum<T> & StringRepresentable & IEnumLightlevel> extends GenericBlockMetaEnumCamoChangeable<T> {

	public GenericBlockMetaEnumCamoChangeableLightlevel(Properties props, EnumProperty<T> type) {
		super(props.lightLevel(state -> state.getValue(type).getLightlevel()), type);
	}
}
