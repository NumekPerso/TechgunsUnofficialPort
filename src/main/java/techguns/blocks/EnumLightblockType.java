package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumLightblockType implements StringRepresentable {
	NEONTUBES2,
	NEONTUBES2_ROTATED,
	NEONTUBES4,
	NEONTUBES4_ROTATED,
	NEONSQUARE_WHITE;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
