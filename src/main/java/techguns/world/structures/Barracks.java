package techguns.world.structures;

import java.util.Random;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import techguns.Techguns;
import techguns.util.MBlock;
import techguns.world.BlockRotator;
import techguns.world.LegacyBlocks;
import techguns.world.StructureBlockUtils;
import techguns.world.TGStructureLevel;

public class Barracks extends WorldgenStructure {

	private static final ResourceLocation CHEST_LOOT = new ResourceLocation(Techguns.MODID, "chests/militarybase_barracks");

	MBlock b_pillar = MBlockRegister.BARRACKS_WOOD_PILLAR;
	MBlock b_wall = MBlockRegister.BARRACKS_WOOD_WALL;
	MBlock b_floor = MBlockRegister.BARRACKS_WOOD_FLOOR;
	MBlock b_scaffold = MBlockRegister.BARRACKS_WOOD_SCAFFOLD;
	MBlock b_roof = MBlockRegister.BARRACKS_WOOD_ROOF;
	MBlock b_roofSlab = MBlockRegister.BARRACKS_WOOD_ROOFSLAB;
	MBlock b_stairs = MBlockRegister.WOOD_STAIRS_OAK;
	MBlock b_torch = MBlockRegister.TGLAMP;
	MBlock b_ground = MBlockRegister.GRAVEL;
	MBlock b_window = MBlockRegister.GLASS_PANE;

	public Barracks(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/**
	 * 1.12 ItemDoor.placeDoor
	 */
	public static void placeDoor(LevelAccessor world, MutableBlockPos p, Direction facing, BlockState door) {
		placeDoor(world, p, facing, door, false);
	}

	public static void placeDoor(LevelAccessor world, MutableBlockPos p, Direction facing, BlockState door, boolean rightHinge) {
		BlockState lower = door.setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
				.setValue(DoorBlock.HINGE, rightHinge ? net.minecraft.world.level.block.state.properties.DoorHingeSide.RIGHT : net.minecraft.world.level.block.state.properties.DoorHingeSide.LEFT);
		world.setBlock(p, lower, 2);
		world.setBlock(p.above(), lower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 2);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {

		MutableBlockPos p = new MutableBlockPos();

		Direction facing = directionToFacing(direction);

		//remove 2 in direction to fit Stairs/Walkway
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
		default:
			sizeZ -= 2;
			break;
		}

		sizeX -= ((sizeX - 1) % 4);
		sizeZ -= ((sizeZ - 1) % 4);

		//fixed height;
		sizeY = 7;

		//Outer Building
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {
				for (int y = 0; y < sizeY; y++) {
					if (y == 0) { //Ground
						world.setBlock(p.set(posX + x, posY + y, posZ + z), b_ground.getState(), 2);
					} else if (y == 1) { //Pillars
						if (x % 4 == 0 && z % 4 == 0) {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), b_pillar.getState(), 2);
						}
					} else if (y == 2) { //Floor
						if (x == 0 || z == 0 || x == sizeX - 1 || z == sizeZ - 1) {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), b_scaffold.getState(), 2);
						} else {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), b_floor.getState(), 2);
						}
					} else if (y <= 4) { //Wall
						if (x == 0 || z == 0 || x == sizeX - 1 || z == sizeZ - 1) {
							if (x % 4 == 0 && z % 4 == 0) {
								world.setBlock(p.set(posX + x, posY + y, posZ + z), b_scaffold.getState(), 2);
							} else {
								if (y == 4 && (x % 4 == 2 || z % 4 == 2)) { //Window
									world.setBlock(p.set(posX + x, posY + y, posZ + z), b_window.getState(), 2);
								} else {
									world.setBlock(p.set(posX + x, posY + y, posZ + z), b_wall.getState(), 2);
								}
							}
						}
					} else if (y == 5) { //roof/scaffold
						if (x == 0 || z == 0 || x == sizeX - 1 || z == sizeZ - 1) {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), b_scaffold.getState(), 2);
						}
					} else if (y == 6) {
						if (x == 0 || z == 0 || x == sizeX - 1 || z == sizeZ - 1) {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), b_roofSlab.getState(), 2);
						} else {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), b_roof.getState(), 2);
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
			zoffset = ((sizeZ / 8) * 4) + 2;
			dx = -1;
			dz = 0;
			break;
		case 1:
			xoffset = ((sizeX / 8) * 4) + 2;
			zoffset = 0;
			dx = 0;
			dz = -1;
			break;
		case 2:
			xoffset = sizeX - 1;
			zoffset = ((sizeZ / 8) * 4) + 2;
			dx = 1;
			dz = 0;
			break;
		case 3:
		default:
			xoffset = ((sizeX / 8) * 4) + 2;
			zoffset = sizeZ - 1;
			dx = 0;
			dz = 1;
			break;
		}

		//Door
		StructureBlockUtils.setBlockToAir(world, p.set(posX + xoffset, posY + 4, posZ + zoffset));
		StructureBlockUtils.setBlockToAir(world, p.set(posX + xoffset, posY + 3, posZ + zoffset));
		placeDoor(world, p.set(posX + xoffset, posY + 3, posZ + zoffset), facing.getOpposite(), Blocks.OAK_DOOR.defaultBlockState());

		//Lamp
		world.setBlock(p.set(posX + xoffset + dx, posY + 5, posZ + zoffset + dz), BlockRotator.getWithFacing(b_torch.getState(), facing.getOpposite()), 2);

		//Ramp
		world.setBlock(p.set(posX + xoffset + dx, posY + 2, posZ + zoffset + dz), BlockRotator.getWithFacing(b_stairs.getState(), facing.getOpposite()), 2);
		world.setBlock(p.set(posX + xoffset + dx * 2, posY + 1, posZ + zoffset + dz * 2), BlockRotator.getWithFacing(b_stairs.getState(), facing.getOpposite()), 2);

		// Indoor stuff & Furniture
		int xHalf = sizeX / 2;
		int zHalf = sizeZ / 2;

		for (int x = 1; x < sizeX - 1; x++) {
			for (int z = 1; z < sizeZ - 1; z++) {
				for (int y = 3; y < 6; y++) {
					if (x == 1 || z == 1 || x == sizeX - 2 || z == sizeZ - 2) {
						if (y == 5 && (x % 4 == 2 || z % 4 == 2)) { //torch
							world.setBlock(p.set(posX + x, posY + y, posZ + z), BlockRotator.getWithFacing(b_torch.getState(), Direction.UP), 2);
						}
						if (y == 3 && (x % 4 == 0 || z % 4 == 0) && x != xoffset && z != zoffset) {

							Direction chestrot = Direction.EAST;
							if (z % 4 == 0) {
								if (x > xHalf) {
									chestrot = chestrot.getOpposite();
								}
							} else if (x % 4 == 0) {
								chestrot = Direction.SOUTH;
								if (z > zHalf) {
									chestrot = chestrot.getOpposite();
								}
							}
							world.setBlock(p.set(posX + x, posY + y, posZ + z), BlockRotator.getWithFacing(Blocks.CHEST.defaultBlockState(), chestrot), 2);

							BlockEntity tile = world.getBlockEntity(p);
							if (tile instanceof RandomizableContainerBlockEntity chest) {
								chest.setLootTable(CHEST_LOOT, TGStructureLevel.posRandom(world, p).nextLong());
							}

						}
					} else {
						if (x == 2 || z == 2 || x == sizeX - 3 || z == sizeZ - 3) {
							//Beds
							if (sizeX / 4 > 1 || sizeZ / 4 > 1) {
								if (y == 3 && (x % 4 == 2 && z % 4 == 2) && x != xoffset && z != zoffset) {
									if (x == 2) {
										world.setBlock(p.set(posX + x, posY + y, posZ + z), LegacyBlocks.get("minecraft:bed", 1), 3);
										world.setBlock(p.set(posX + x - 1, posY + y, posZ + z), LegacyBlocks.get("minecraft:bed", 1 + 8), 3);
									} else if (x == sizeX - 3) {
										world.setBlock(p.set(posX + x, posY + y, posZ + z), LegacyBlocks.get("minecraft:bed", 3), 3);
										world.setBlock(p.set(posX + x + 1, posY + y, posZ + z), LegacyBlocks.get("minecraft:bed", 3 + 8), 3);
									} else if (z == 2) {
										world.setBlock(p.set(posX + x, posY + y, posZ + z), LegacyBlocks.get("minecraft:bed", 2), 3);
										world.setBlock(p.set(posX + x, posY + y, posZ + z - 1), LegacyBlocks.get("minecraft:bed", 2 + 8), 3);
									} else if (z == sizeZ - 3) {
										world.setBlock(p.set(posX + x, posY + y, posZ + z), LegacyBlocks.get("minecraft:bed", 0), 3);
										world.setBlock(p.set(posX + x, posY + y, posZ + z + 1), LegacyBlocks.get("minecraft:bed", 0 + 8), 3);
									}
								}
							}
						}
						//more lamps
						if (y == 5 && x % 8 == 0 && z % 8 == 0) {
							world.setBlock(p.set(posX + x, posY + y, posZ + z), BlockRotator.getWithFacing(b_torch.getState(), Direction.UP), 2);
						}
					}
				}
			}
		}
	}

}
