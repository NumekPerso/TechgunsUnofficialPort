package techguns.world.structures;

import java.util.Random;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import techguns.util.MBlock;
import techguns.world.StructureBlockUtils;

public class Containers extends WorldgenStructure {

	int containerWidth = 2;
	int containerHeight = 2;
	int containerMinLength = 2;
	BlockState containerBlock = null;

	public Containers(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int containerWidth, int containerHeight, int containerMinLength, MBlock containerBlock) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.containerWidth = containerWidth;
		this.containerHeight = containerHeight;
		this.containerMinLength = containerMinLength;
		this.containerBlock = containerBlock.getState();
	}

	public Containers(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, MBlock containerBlock) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.containerBlock = containerBlock.getState();
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int count; //Number of containers
		int offset = 0;
		MutableBlockPos pos = new MutableBlockPos();

		if (sizeX > sizeZ) {
			count = (int) (((float) sizeZ + 1.0f) / ((float) containerWidth + 1.0f));
			for (int i = 0; i < count; i++) {
				StructureBlockUtils.fillBlocks(world, pos.set(posX, posY + 1, posZ + offset), sizeX, containerHeight, containerWidth, containerBlock);
				offset += (containerWidth + 1);
			}
		} else {
			count = (int) (((float) sizeX + 1.0f) / ((float) containerWidth + 1.0f));
			for (int i = 0; i < count; i++) {
				StructureBlockUtils.fillBlocks(world, pos.set(posX + offset, posY + 1, posZ), containerWidth, containerHeight, sizeZ, containerBlock);
				offset += (containerWidth + 1);
			}
		}
	}

}
