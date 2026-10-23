package techguns.blocks.machines;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.RenderShape;
import techguns.api.machines.IMachineType;

/**
 * Machines of the 1.12 "basicmachine" block: rotation is stored in the tile entity, most of them are rendered by a
 * block entity renderer (1.12 TESR).
 */
public enum EnumMachineType implements StringRepresentable, IMachineType {
	AMMO_PRESS(0, false, false, RenderShape.ENTITYBLOCK_ANIMATED),
	METAL_PRESS(1, false, false, RenderShape.ENTITYBLOCK_ANIMATED),
	CHEM_LAB(2, false, false, RenderShape.ENTITYBLOCK_ANIMATED),
	TURRET(3, false, false, RenderShape.ENTITYBLOCK_ANIMATED),
	DUNGEON_SCANNER(4, true, true, RenderShape.MODEL),
	DUNGEON_GENERATOR(5, true, true, RenderShape.MODEL);

	private int id;
	private String name;
	private boolean isFullCube;
	private RenderShape renderType;
	private boolean debugonly;

	EnumMachineType(int id, boolean isFullCube, boolean debugonly, RenderShape renderType) {
		this.id = id;
		this.name = this.name().toLowerCase();
		this.isFullCube = isFullCube;
		this.renderType = renderType;
		this.debugonly = debugonly;
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
	public RenderShape getRenderType() {
		return this.renderType;
	}

	@Override
	public boolean isFullCube() {
		return this.isFullCube;
	}

	@Override
	public boolean debugOnly() {
		return this.debugonly;
	}
}
