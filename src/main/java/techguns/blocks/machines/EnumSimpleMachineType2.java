package techguns.blocks.machines;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import techguns.api.machines.IMachineType;

/**
 * Machines of the 1.12 "simplemachine2" block (horizontal FACING block state).
 * 1.12 GRINDER used a separate inventory model (simplemachine2_grinder_inv): in 1.20 use an item model json.
 */
public enum EnumSimpleMachineType2 implements StringRepresentable, IMachineType {
	GRINDER(0, true, RenderShape.MODEL),
	ARMOR_BENCH(1, false, RenderShape.MODEL);

	/**
	 * 1.12 UpgradeBenchTileEnt.BLOCK_BB (0,0,0 - 1,0.75,1)
	 */
	public static final VoxelShape ARMOR_BENCH_SHAPE = Block.box(0, 0, 0, 16, 12, 16);

	private int id;
	private String name;
	private boolean isFullCube;
	private RenderShape renderType;

	private EnumSimpleMachineType2(int id, boolean isFullCube, RenderShape renderType) {
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

	@Override
	public VoxelShape getBoundingBox() {
		if (this == ARMOR_BENCH) {
			return ARMOR_BENCH_SHAPE;
		}
		return IMachineType.super.getBoundingBox();
	}
}
