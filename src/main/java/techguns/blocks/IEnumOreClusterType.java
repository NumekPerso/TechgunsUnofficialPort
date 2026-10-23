package techguns.blocks;

import net.minecraft.util.StringRepresentable;

public interface IEnumOreClusterType extends StringRepresentable {
	public int getMiningLevel();

	public float getMultiplier();
}
