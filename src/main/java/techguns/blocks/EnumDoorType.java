package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumDoorType implements StringRepresentable {
	METAL,
	HANGAR_UP,
	HANGAR_DOWN,
	NETHER;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
