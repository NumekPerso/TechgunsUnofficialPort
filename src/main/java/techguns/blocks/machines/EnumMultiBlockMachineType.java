package techguns.blocks.machines;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import techguns.api.machines.IMachineType;

/**
 * Blocks of the 1.12 "multiblockmachine" block (FORMED + direction block state)
 */
public enum EnumMultiBlockMachineType implements StringRepresentable, IMachineType {
	FABRICATOR_HOUSING(0, true, RenderShape.MODEL),
	FABRICATOR_GLASS(1, false, RenderShape.MODEL, true, SoundType.GLASS),
	FABRICATOR_CONTROLLER(2, true, RenderShape.MODEL),

	REACTIONCHAMBER_HOUSING(3, true, RenderShape.MODEL),
	REACTIONCHAMBER_GLASS(4, false, RenderShape.MODEL, true, SoundType.GLASS),
	REACTIONCHAMBER_CONTROLLER(5, true, RenderShape.MODEL, true, SoundType.METAL);

	private int id;
	private String name;
	private boolean isFullCube;
	private RenderShape renderType;
	private boolean cutout;
	private SoundType soundType;

	EnumMultiBlockMachineType(int id, boolean isFullCube, RenderShape renderType) {
		this(id, isFullCube, renderType, false, SoundType.METAL);
	}

	EnumMultiBlockMachineType(int id, boolean isFullCube, RenderShape renderType, boolean cutout, SoundType sound) {
		this.id = id;
		this.name = this.name().toLowerCase();
		this.isFullCube = isFullCube;
		this.renderType = renderType;
		this.cutout = cutout;
		this.soundType = sound;
	}

	@Override
	public int getIndex() {
		return id;
	}

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
}
