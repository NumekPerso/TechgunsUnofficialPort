package techguns.blocks.machines;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.RenderShape;
import techguns.api.machines.IMachineType;

/**
 * Machines of the 1.12 "simplemachine" block (horizontal FACING block state, json / obj model)
 */
public enum EnumSimpleMachineType implements StringRepresentable, IMachineType {
	CAMO_BENCH(0, true, RenderShape.MODEL),
	REPAIR_BENCH(1, true, RenderShape.MODEL),
	CHARGING_STATION(2, false, RenderShape.MODEL),
	BLAST_FURNACE(3, true, RenderShape.MODEL);

	private int id;
	private String name;
	private boolean isFullCube;
	private RenderShape renderType;

	private EnumSimpleMachineType(int id, boolean isFullCube, RenderShape renderType) {
		this.id = id;
		this.name = this.name().toLowerCase();
		this.isFullCube = isFullCube;
		this.renderType = renderType;
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
	public boolean isFullCube() {
		return isFullCube;
	}

	@Override
	public RenderShape getRenderType() {
		return renderType;
	}

	@Override
	public boolean debugOnly() {
		return false;
	}
}
