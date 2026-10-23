package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumCamoNetType implements StringRepresentable {
	WOOD,
	DESERT,
	SNOW;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
