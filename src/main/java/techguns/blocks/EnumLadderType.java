package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumLadderType implements StringRepresentable {
	METAL,
	SHINY,
	RUSTY,
	CARBON;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
