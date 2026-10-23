package techguns.api.machines;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Properties of a machine type (1.12: one enum constant = one metadata variant of a machine block).
 * In 1.20 every machine type is its own block, the enum constant only describes the block.
 * The block entity type is given to the machine block by TGMachines (the enum does not reference tile entity classes,
 * so enum constants of not yet ported machines can stay).
 */
public interface IMachineType {
	/**
	 * 1.12 metadata index, kept for reference
	 */
	public int getIndex();

	/**
	 * lowercase name = registry name of the machine block
	 */
	public String getName();

	public RenderShape getRenderType();

	public boolean isFullCube();

	public boolean debugOnly();

	public default boolean hideInCreative() {
		return false;
	}

	public default SoundType getSoundType() {
		return SoundType.METAL;
	}

	/**
	 * true for machines with a cutout model layer (glass), the render type is set in the block model json
	 */
	public default boolean isCutout() {
		return false;
	}

	public default VoxelShape getBoundingBox() {
		return Block.box(0, 0, 0, 16, 16, 16);
	}
}
