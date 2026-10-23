package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumNetherMetalType implements StringRepresentable, IEnumLightlevel {
	PANEL,
	GRATE1,
	GRATE2,
	GREY_DARK,
	GREY,
	GREY_TILES,
	BORDER_RED,
	PLATE_BLACK,
	PLATE_RED,
	BORDER_LAVA(15);

	private int lightlevel = 0;

	private EnumNetherMetalType() {
	}

	private EnumNetherMetalType(int lightlevel) {
		this.lightlevel = lightlevel;
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}

	@Override
	public int getLightlevel() {
		return lightlevel;
	}
}
