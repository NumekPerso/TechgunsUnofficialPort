package techguns.world.structures;

import java.util.Random;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import techguns.util.MBlock;
import techguns.world.StructureBlockUtils;

public class Tent extends WorldgenStructure {

	int type;

	MBlock roofBlock = new MBlock("techguns:camonet_top", 0);
	MBlock wallBlock = new MBlock("techguns:camonet", 0);
	MBlock groundBlock = new MBlock("minecraft:gravel", 0);
	BlockState roofState = roofBlock.getState();
	BlockState wallState = wallBlock.getState();
	BlockState groundState = groundBlock.getState();

	public Tent(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int type) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.type = type;
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {

		if (type == 0) {
			//1 sized border
			posX++;
			posZ++;
			sizeX -= 2;
			sizeZ -= 2;
		}

		MutableBlockPos p = new MutableBlockPos();
		//Tent walls and roof;
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeZ; j++) {
				for (int k = 0; k < sizeY; k++) {
					p.set(posX + i, posY + k, posZ + j);
					if (k == 0) {
						world.setBlock(p, groundState, 2);
					} else if (k < sizeY - 1) {
						if (j == 0 || j == sizeZ - 1 || i == 0 || i == sizeX - 1) {
							world.setBlock(p, wallState, 2);
						}
					} else {
						world.setBlock(p, roofState, 2);
					}
				}
			}
		}

		if (type == 0 || type == 1) {
			//Tent door and walkway
			switch (direction) {
			case 0:// (-x)
				StructureBlockUtils.setBlockToAir(world, p.set(posX, posY + 1, posZ + (sizeZ / 2)));
				StructureBlockUtils.setBlockToAir(world, p.set(posX, posY + 2, posZ + (sizeZ / 2)));
				if (type == 0)
					world.setBlock(p.set(posX - 1, posY, posZ + (sizeZ / 2)), groundState, 2);
				break;
			case 1://(-z)
				StructureBlockUtils.setBlockToAir(world, p.set(posX + (sizeX / 2), posY + 1, posZ));
				StructureBlockUtils.setBlockToAir(world, p.set(posX + (sizeX / 2), posY + 2, posZ));
				if (type == 0)
					world.setBlock(p.set(posX + (sizeX / 2), posY, posZ - 1), groundState, 2);
				break;
			case 2://(+x)
				StructureBlockUtils.setBlockToAir(world, p.set(posX + sizeX - 1, posY + 1, posZ + (sizeZ / 2)));
				StructureBlockUtils.setBlockToAir(world, p.set(posX + sizeX - 1, posY + 2, posZ + (sizeZ / 2)));
				if (type == 0)
					world.setBlock(p.set(posX + sizeX, posY, posZ + (sizeZ / 2)), groundState, 2);
				break;
			case 3:// (+z)
				StructureBlockUtils.setBlockToAir(world, p.set(posX + (sizeX / 2), posY + 1, posZ + sizeZ - 1));
				StructureBlockUtils.setBlockToAir(world, p.set(posX + (sizeX / 2), posY + 2, posZ + sizeZ - 1));
				if (type == 0)
					world.setBlock(p.set(posX + (sizeX / 2), posY, posZ + sizeZ), groundState, 2);
				break;
			}
		}

		if (type == 2) {
			for (int y = 1; y < sizeY - 1; y++) {
				if (direction == 0 || direction == 2) {
					for (int z = 1; z < sizeZ - 1; z++) {
						if (z % 3 != 0) {
							int x = (direction == 0) ? 0 : sizeX - 1;
							StructureBlockUtils.setBlockToAir(world, p.set(posX + x, posY + y, posZ + z));
						}
					}
				} else {
					for (int x = 1; x < sizeX - 1; x++) {
						if (x % 3 != 1) {
							int z = (direction == 1) ? 0 : sizeZ - 1;
							StructureBlockUtils.setBlockToAir(world, p.set(posX + x, posY + y, posZ + z));
						}
					}
				}
			}
		}
	}

}
