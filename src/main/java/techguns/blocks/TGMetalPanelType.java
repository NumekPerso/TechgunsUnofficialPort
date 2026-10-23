package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum TGMetalPanelType implements StringRepresentable {
	CONTAINER_RED,
	CONTAINER_GREEN,
	CONTAINER_BLUE,
	CONTAINER_ORANGE,
	PANEL_LARGE_BORDER,
	STEELFRAME_BLUE,
	STEELFRAME_DARK,
	STEELFRAME_SCAFFOLD;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
