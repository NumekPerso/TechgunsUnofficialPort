package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public enum EnumMonsterSpawnerType implements StringRepresentable {
	HOLE,
	SOLDIER_SPAWN;

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}

}
