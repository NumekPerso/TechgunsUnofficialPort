package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumDoorState implements StringRepresentable {
	CLOSED,
	OPENED,
	OPENING,
	CLOSING;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
