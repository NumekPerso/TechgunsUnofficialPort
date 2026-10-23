package techguns.blocks.machines;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.RenderShape;
import techguns.api.machines.IMachineType;

/**
 * 1.12 "explosive_charge" block types: TNT -> explosive_charge, ADVANCED -> explosive_charge_advanced
 */
public enum EnumExplosiveChargeType implements StringRepresentable, IMachineType {
	TNT("explosive_charge"),
	ADVANCED("explosive_charge_advanced");

	private final String registryName;

	EnumExplosiveChargeType(String registryName) {
		this.registryName = registryName;
	}

	@Override
	public String getName() {
		return registryName;
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}

	@Override
	public int getIndex() {
		return this.ordinal();
	}

	@Override
	public RenderShape getRenderType() {
		return RenderShape.MODEL;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean debugOnly() {
		return false;
	}

}
