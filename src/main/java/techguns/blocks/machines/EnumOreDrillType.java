package techguns.blocks.machines;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import techguns.api.machines.IMachineType;

/**
 * Blocks of the 1.12 "oredrill" block, registry names oredrill_frame, oredrill_scaffold, ...
 */
public enum EnumOreDrillType implements StringRepresentable, IMachineType {
	FRAME(0, true, RenderShape.MODEL),
	SCAFFOLD(1, false, RenderShape.MODEL, true, SoundType.METAL, false),
	ROD(2, true, RenderShape.MODEL),
	ENGINE(3, true, RenderShape.MODEL),
	CONTROLLER(4, true, RenderShape.MODEL),
	SCAFFOLD_HIDDEN(5, false, RenderShape.INVISIBLE, true, SoundType.METAL, true);

	private int id;
	private String name;
	private boolean isFullCube;
	private RenderShape renderType;
	private boolean cutout;
	private SoundType soundType;
	private boolean hideInCreative;

	EnumOreDrillType(int id, boolean isFullCube, RenderShape renderType) {
		this(id, isFullCube, renderType, false, SoundType.METAL, false);
	}

	EnumOreDrillType(int id, boolean isFullCube, RenderShape renderType, boolean cutout, SoundType sound, boolean hideInCreative) {
		this.id = id;
		this.name = "oredrill_" + this.name().toLowerCase();
		this.isFullCube = isFullCube;
		this.renderType = renderType;
		this.cutout = cutout;
		this.soundType = sound;
		this.hideInCreative = hideInCreative;
	}

	@Override
	public int getIndex() {
		return id;
	}

	/**
	 * Registry name of the block (1.12 type name prefixed with oredrill_)
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	@Override
	public SoundType getSoundType() {
		return this.soundType;
	}

	@Override
	public RenderShape getRenderType() {
		return this.renderType;
	}

	@Override
	public boolean isFullCube() {
		return this.isFullCube;
	}

	@Override
	public boolean isCutout() {
		return this.cutout;
	}

	@Override
	public boolean debugOnly() {
		return false;
	}

	@Override
	public boolean hideInCreative() {
		return hideInCreative;
	}
}
