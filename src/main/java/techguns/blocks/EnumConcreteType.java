package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumConcreteType implements StringRepresentable {
	CONCRETE_BROWN,
	CONCRETE_BROWN_LIGHT,
	CONCRETE_GREY,
	CONCRETE_GREY_DARK,
	CONCRETE_BROWN_PIPES,
	CONCRETE_BROWN_LIGHT_SCAFF;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
