package techguns.world.structures;

import java.util.Random;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class EmptyPlane extends WorldgenStructure {

	BlockState block;

	public EmptyPlane(BlockState block) {
		super(1, 1, 1, -1, -1, -1);
		this.block = block;
	}

	public EmptyPlane(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState block) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = block;
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		MutableBlockPos p = new MutableBlockPos();
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeZ; j++) {
				world.setBlock(p.set(posX + i, posY, posZ + j), block, 3);
			}
		}
	}

}
