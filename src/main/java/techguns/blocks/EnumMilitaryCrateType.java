package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumMilitaryCrateType implements StringRepresentable {
	AMMO,
	GUN,
	ARMOR,
	MEDICAL,
	EXPLOSIVE,
	GENERIC_OAK,
	GENERIC_JUNGLE,
	GENERIC_BIRCH,
	GENERIC_SPRUCE;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
