package techguns.world.structures;

import java.util.Random;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import techguns.TGBlocks;
import techguns.Techguns;
import techguns.util.MBlock;
import techguns.world.BlockRotator;
import techguns.world.EnumLootType;
import techguns.world.StructureBlockUtils;
import techguns.world.TGStructureLevel;

public class Bunker extends WorldgenStructure {

	private static final ResourceLocation CHEST_LOOT = new ResourceLocation(Techguns.MODID, "chests/militarybase_bunker");

	MBlock groundBlock = new MBlock("techguns:concrete", 1);
	MBlock stairsblock = new MBlock("minecraft:stone_stairs", 0);
	MBlock wallBlock = new MBlock("techguns:concrete", 0);
	MBlock roofBlock = new MBlock("techguns:concrete", 2);
	MBlock sandbags = new MBlock("techguns:sandbags", 0);
	MBlock ladder = new MBlock("techguns:ladder0", 0);
	MBlock[] crateBlocks = MBlockRegister.crates;

	MBlock lantern = new MBlock("minecraft:glowstone", 0);
	MBlock lamps = new MBlock("techguns:lamp0", 0);

	int type = 0;

	public Bunker(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int type) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.type = type;
		this.lootTier = EnumLootType.TIER1;
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {

		MutableBlockPos p = new MutableBlockPos();

		//Adjust direction from size
		if ((sizeX <= 4) && (sizeZ >= 5)) {
			if (direction == 0 || direction == 2) {
				direction = (direction + 1) % 4;
			}
		} else if ((sizeZ <= 4) && (sizeX >= 5)) {
			if (direction == 1 || direction == 3) {
				direction = (direction + 1) % 4;
			}
		}

		switch (direction) {
		case 0:
			posX += 2;
			sizeX -= 2;
			break;
		case 1:
			posZ += 2;
			sizeZ -= 2;
			break;
		case 2:
			sizeX -= 2;
			break;
		case 3:
			sizeZ -= 2;
			break;
		}

		Direction facing = directionToFacing(direction);

		for (int y = -1; y < sizeY; y++) {
			for (int x = 0; x < sizeX; x++) {
				for (int z = 0; z < sizeZ; z++) {
					if (y == -1) {
						world.setBlock(p.set(posX + x, posY + y, posZ + z), groundBlock.getState(), 2);
					} else if (y == 0) {
						if (x == 0 || x == sizeX - 1 || z == 0 || z == sizeZ - 1) {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), wallBlock.getState(), 2);
						} else {
							StructureBlockUtils.setBlockToAir(world, p.set(posX + x, posY + y, posZ + z));
						}
					} else if (y == 1) {
						if (type == 2 || type == 3) { //no windows
							if (x == 0 || x == sizeX - 1 || z == 0 || z == sizeZ - 1) {
								world.setBlock(p.set(posX + x, posY + y, posZ + z), wallBlock.getState(), 2);
							}
						} else {
							if (((x == 0 || x == sizeX - 1) && (z == 0 || z == sizeZ - 1 || z % 3 == 0)) || ((x == 0 || x == sizeX - 1 || x % 3 == 0) && (z == 0 || z == sizeZ - 1))) {
								world.setBlock(p.set(posX + x, posY + y, posZ + z), wallBlock.getState(), 2);
							}
						}
					} else if (y == 2) {
						if (x == 0 || x == sizeX - 1 || z == 0 || z == sizeZ - 1) {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), wallBlock.getState(), 2);
						} else {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), roofBlock.getState(), 2);
						}
					} else if (y == 3 && (type == 1 || type == 3)) { //Sandbag roof;
						if (x == 0 || x == sizeX - 1 || z == 0 || z == sizeZ - 1) {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), sandbags.getState(), 2);
						}
					}

				}
			}
		}

		// Entrance
		int xoffset, zoffset; //Door/center positions
		int dx, dz;

		switch (direction) {
		case 0:
			xoffset = 0;
			zoffset = sizeZ / 2;
			dx = -1;
			dz = 0;
			break;
		case 1:
			xoffset = sizeX / 2;
			zoffset = 0;
			dx = 0;
			dz = -1;
			break;
		case 2:
			xoffset = sizeX - 1;
			zoffset = sizeZ / 2;
			dx = 1;
			dz = 0;
			break;
		case 3:
		default:
			xoffset = sizeX / 2;
			zoffset = sizeZ - 1;
			dx = 0;
			dz = 1;
			break;
		}

		//Door
		StructureBlockUtils.setBlockToAir(world, p.set(posX + xoffset, posY + 1, posZ + zoffset));
		StructureBlockUtils.setBlockToAir(world, p.set(posX + xoffset, posY + 0, posZ + zoffset));

		Barracks.placeDoor(world, p.set(posX + xoffset, posY + 0, posZ + zoffset), facing, TGBlocks.BUNKER_DOOR.defaultBlockState(), true);

		//Doorway
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? 1 : 0), posY + 0, posZ + zoffset + ((dz == 0) ? 1 : 0)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? 1 : 0), posY + 1, posZ + zoffset + ((dz == 0) ? 1 : 0)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? -1 : 0), posY + 0, posZ + zoffset + ((dz == 0) ? -1 : 0)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? -1 : 0), posY + 1, posZ + zoffset + ((dz == 0) ? -1 : 0)), wallBlock.getState(), 2);

		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? 1 : dx), posY - 1, posZ + zoffset + ((dz == 0) ? 1 : dz)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? -1 : dx), posY - 1, posZ + zoffset + ((dz == 0) ? -1 : dz)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? 1 : dx * 2), posY - 1, posZ + zoffset + ((dz == 0) ? 1 : dz * 2)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? -1 : dx * 2), posY - 1, posZ + zoffset + ((dz == 0) ? -1 : dz * 2)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? 1 : dx), posY + 0, posZ + zoffset + ((dz == 0) ? 1 : dz)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? -1 : dx), posY + 0, posZ + zoffset + ((dz == 0) ? -1 : dz)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? 1 : dx * 2), posY + 0, posZ + zoffset + ((dz == 0) ? 1 : dz * 2)), wallBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? -1 : dx * 2), posY + 0, posZ + zoffset + ((dz == 0) ? -1 : dz * 2)), wallBlock.getState(), 2);

		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? 1 : dx), posY + 2, posZ + zoffset + ((dz == 0) ? 1 : dz)), BlockRotator.getWithFacing(lamps.getState(), facing.getOpposite()), 2);
		world.setBlock(p.set(posX + xoffset + ((dx == 0) ? -1 : dx), posY + 2, posZ + zoffset + ((dz == 0) ? -1 : dz)), BlockRotator.getWithFacing(lamps.getState(), facing.getOpposite()), 2);

		world.setBlock(p.set(posX + xoffset, posY - 1, posZ + zoffset), groundBlock.getState(), 2);
		world.setBlock(p.set(posX + xoffset + dx, posY - 1, posZ + zoffset + dz), groundBlock.getState(), 2);
		StructureBlockUtils.setBlockToAir(world, p.set(posX + xoffset + dx, posY + 0, posZ + zoffset + dz));
		StructureBlockUtils.setBlockToAir(world, p.set(posX + xoffset + dx, posY + 1, posZ + zoffset + dz));

		//Stairs
		world.setBlock(p.set(posX + xoffset + dx * 2, posY + 0, posZ + zoffset + dz * 2), BlockRotator.getWithFacing(stairsblock.getState(), facing), 2);
		StructureBlockUtils.setBlockToAir(world, p.set(posX + xoffset + dx * 2, posY + 1, posZ + zoffset + dz * 2));

		//Inner objects
		if (type == 2 || type == 3) {
			//Lamp
			world.setBlock(p.set(posX + xoffset - dx, posY + 1, posZ + zoffset - dz), BlockRotator.getWithFacing(lamps.getState(), Direction.UP), 2);
			setCrateBlocks(world, p, posX + 2, posY, posZ + 2, sizeX - 4, 2, sizeZ - 4, rnd);
		} else {
			//Lamp
			world.setBlock(p.set(posX + (sizeX / 2), posY + 1, posZ + (sizeZ / 2)), BlockRotator.getWithFacing(lamps.getState(), Direction.UP), 2);
		}

		//Ladder
		if (type == 1 || type == 3) {
			Direction ladderFacing = Direction.NORTH;
			switch (direction) {
			case 0: //-X
				xoffset = sizeX - 2;
				zoffset = sizeZ - 2;
				dx = 1;
				dz = 0;
				ladderFacing = Direction.WEST;
				break;
			case 1: //-Z
				zoffset = sizeZ - 2;
				xoffset = 1;
				dx = 0;
				dz = 1;
				ladderFacing = Direction.NORTH;
				break;
			case 2: //+X
				xoffset = 1;
				zoffset = 1;
				dx = -1;
				dz = 0;
				ladderFacing = Direction.EAST;
				break;
			case 3: //+Z
				xoffset = sizeX - 2;
				zoffset = 1;
				dx = 0;
				dz = -1;
				ladderFacing = Direction.SOUTH;
				break;
			}

			StructureBlockUtils.setBlockToAir(world, p.set(posX + xoffset, posY + 2, posZ + zoffset));
			world.setBlock(p.set(posX + xoffset + dx, posY + 1, posZ + zoffset + dz), wallBlock.getState(), 2);

			world.setBlock(p.set(posX + xoffset, posY + 0, posZ + zoffset), BlockRotator.getWithFacing(ladder.getState(), ladderFacing), 2);
			world.setBlock(p.set(posX + xoffset, posY + 1, posZ + zoffset), BlockRotator.getWithFacing(ladder.getState(), ladderFacing), 2);
			world.setBlock(p.set(posX + xoffset, posY + 2, posZ + zoffset), BlockRotator.getWithFacing(ladder.getState(), ladderFacing), 2);
		}

	}

	private void setCrateBlocks(LevelAccessor world, MutableBlockPos p, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, Random r) {
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {

				float chestroll = r.nextFloat();
				boolean chest = chestroll <= 0.1f;

				if (!chest) {
					for (int y = 0; y < sizeY; y++) {
						if (!world.isEmptyBlock(p.set(posX + x, posY + y - 1, posZ + z)) && r.nextFloat() > 0.5) {
							int index = r.nextInt(crateBlocks.length);
							world.setBlock(p.set(posX + x, posY + y, posZ + z), crateBlocks[index].getState(), 2);
						}
					}
				} else {
					int meta = r.nextInt(4);
					world.setBlock(p.set(posX + x, posY, posZ + z), BlockRotator.getRotatedHorizontal(Blocks.CHEST.defaultBlockState(), meta), 3);

					BlockEntity tile = world.getBlockEntity(p);
					if (tile instanceof RandomizableContainerBlockEntity c) {
						c.setLootTable(CHEST_LOOT, TGStructureLevel.posRandom(world, p).nextLong());
					}
				}
			}
		}
	}

}
