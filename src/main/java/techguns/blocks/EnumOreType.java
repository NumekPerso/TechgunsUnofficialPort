package techguns.blocks;

import net.minecraft.util.StringRepresentable;

/**
 * 1.12 variants of the "basicore" block, each one is its own block in the port (registry name = variant name)
 */
public enum EnumOreType implements StringRepresentable {
	ORE_COPPER(4.0f, 1),
	ORE_TIN(4.0f, 1),
	ORE_LEAD(6.0f, 2),
	ORE_TITANIUM(8.0f, 3),
	ORE_URANIUM(7.0f, 2, 4);

	protected float hardness;
	protected int mininglevel;
	protected int lightlevel;
	protected boolean enabled = true;

	private EnumOreType(float hardness, int mininglevel) {
		this(hardness, mininglevel, 0);
	}

	private EnumOreType(float hardness, int mininglevel, int lightlevel) {
		this.hardness = hardness;
		this.mininglevel = mininglevel;
		this.lightlevel = lightlevel;
	}

	public void disable() {
		this.enabled = false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public float getHardness() {
		return hardness;
	}

	/**
	 * 1.12 harvest level, in the port: tags minecraft:needs_stone_tool (1), needs_iron_tool (2), needs_diamond_tool (3)
	 */
	public int getMininglevel() {
		return mininglevel;
	}

	public int getLightlevel() {
		return lightlevel;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase();
	}
}
