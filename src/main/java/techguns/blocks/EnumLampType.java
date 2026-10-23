package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumLampType implements StringRepresentable {
	YELLOW,
	WHITE,
	YELLOW_LANTERN,
	WHITE_LANTERN;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
