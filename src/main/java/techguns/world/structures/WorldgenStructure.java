package techguns.world.structures;

import java.util.Random;

import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import techguns.world.EnumLootType;
import techguns.world.StructureBlockUtils;
import techguns.world.TGSpawnContext;
import techguns.world.TGSpawnData;

/**
 * Base of the code/template built structures (1.12 WorldgenStructure).
 * <p>
 * 1.20: spawnStructureWorldgen / spawnStructureCaveWorldgen are split in findSpawn / findSpawnCave (where, decided
 * when the structure start is created, with the terrain of the chunk generator) and generate (placement, run for
 * every chunk the structure touches with a TGStructureLevel, see WorldGenTGStructureSpawn).
 * Instances are created per generation (TGStructureSpawn factory), so the fields may be changed while generating.
 */
public abstract class WorldgenStructure {
	public int minX; //Minimum size
	public int minY;
	public int minZ;

	public int maxX; //Maximum size
	public int maxY;
	public int maxZ;

	public int sizeX;
	public int sizeY;
	public int sizeZ;
	//unrotated center
	public int centX;
	public int centZ;

	public boolean canSwapXZ; //IF (min/max) X != Z ... X and Z can be swapped

	public boolean removeJunkOnWorldspawn = false;

	public EnumLootType lootTier = EnumLootType.TIER0;

	protected int heightdiffLimit = 3;

	public static final int NETHER_STRUCT_MIN_Y = 20;
	public static final int NETHER_STRUCT_MAX_y = 100;

	public WorldgenStructure() {
		this(0, 0, 0, 0, 0, 0);
	}

	protected int getStep() {
		return 4;
	}

	public WorldgenStructure(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super();
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	public WorldgenStructure setXZSize(int x, int z) {
		this.sizeX = x;
		this.sizeZ = z;
		this.centX = (int) (sizeX / 2.0f);
		this.centZ = (int) (sizeZ / 2.0f);
		return this;
	}

	public WorldgenStructure setXYZSize(int x, int y, int z) {
		this.sizeX = x;
		this.sizeY = y;
		this.sizeZ = z;
		this.centX = (int) (sizeX / 2.0f);
		this.centZ = (int) (sizeZ / 2.0f);
		return this;
	}

	public int getSizeX(Random rnd) {
		return sizeX;
	}

	public int getSizeZ(Random rnd) {
		return sizeZ;
	}

	public int getSizeY(Random rnd) {
		return sizeY;
	}

	public WorldgenStructure setSwapXZ(boolean b) {
		this.canSwapXZ = b;
		return this;
	}

	/**
	 * @param direction - where does it point; 0=WEST(-x), 1=NORTH(-z), 2=EAST(+x); 3=SOUTH(+z);
	 */
	public abstract void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd);

	public static int[] rotatePoint(int x, int z, int direction, int centerX, int centerZ) {
		int a = x - centerX;
		int b = z - centerZ;

		for (int i = 0; i < direction; i++) {
			int a2 = a;
			a = b;
			b = -a2;
		}

		x = a + centerX;
		z = b + centerZ;

		return new int[] { x, z };
	}

	/**
	 * 1.12 spawnStructureWorldgen (surface): position, rotation and height, or null if the terrain does not fit
	 */
	public TGSpawnData findSpawn(TGSpawnContext ctx, int chunkX, int chunkZ, int sizeX, int sizeY, int sizeZ, Random rnd) {
		int direction = rnd.nextInt(4);

		int sizeXr = direction == 0 || direction == 2 ? sizeX : sizeZ;
		int sizeZr = direction == 0 || direction == 2 ? sizeZ : sizeX;

		int centerX = (int) (sizeX / 2.0f);
		int centerZ = (int) (sizeZ / 2.0f);

		int[] p0 = rotatePoint(0, 0, direction, centerX, centerZ);
		int[] p1 = rotatePoint(sizeX, 0, direction, centerX, centerZ);
		int[] p2 = rotatePoint(0, sizeZ, direction, centerX, centerZ);
		int[] p3 = rotatePoint(sizeX, sizeZ, direction, centerX, centerZ);

		int minX = Math.min(Math.min(p0[0], p1[0]), Math.min(p2[0], p3[0]));
		int minZ = Math.min(Math.min(p0[1], p1[1]), Math.min(p2[1], p3[1]));

		int x = minX + chunkX * 16;
		int z = minZ + chunkZ * 16;

		int offsetX = this.getRotationShiftX(direction);
		int offsetZ = this.getRotationShiftZ(direction);

		int y = ctx.getValidSpawnYArea(x + offsetX, z + offsetZ, sizeXr, sizeZr, this.heightdiffLimit, getStep());

		if (y < 0) {
			return null;
		}

		TGSpawnData d = new TGSpawnData(x, y - 1, z, sizeX, sizeY, sizeZ, direction);
		d.removeJunk = removeJunkOnWorldspawn;
		return d;
	}

	/**
	 * 1.12 spawnStructureCaveWorldgen (nether)
	 */
	public TGSpawnData findSpawnCave(TGSpawnContext ctx, int chunkX, int chunkZ, int sizeX, int sizeY, int sizeZ, Random rnd) {
		int direction = rnd.nextInt(4);

		int centerX = (int) (sizeX / 2.0f);
		int centerZ = (int) (sizeZ / 2.0f);

		int[] p0 = rotatePoint(0, 0, direction, centerX, centerZ);
		int[] p1 = rotatePoint(sizeX, 0, direction, centerX, centerZ);
		int[] p2 = rotatePoint(0, sizeZ, direction, centerX, centerZ);
		int[] p3 = rotatePoint(sizeX, sizeZ, direction, centerX, centerZ);

		int minX = Math.min(Math.min(p0[0], p1[0]), Math.min(p2[0], p3[0]));
		int minZ = Math.min(Math.min(p0[1], p1[1]), Math.min(p2[1], p3[1]));

		int x = minX + chunkX * 16;
		int z = minZ + chunkZ * 16;

		int y = ctx.getCaveHeight(chunkX, chunkZ, 10, NETHER_STRUCT_MIN_Y, NETHER_STRUCT_MAX_y);

		if (y < 0) {
			return null;
		}

		return new TGSpawnData(x, y - 1, z, sizeX, sizeY, sizeZ, direction);
	}

	/**
	 * Area the structure can change, all blocks outside are not placed
	 */
	public BoundingBox getBoundingBox(TGSpawnData d) {
		return d.getDefaultBoundingBox(3, 34, 34);
	}

	/**
	 * 1.20: does the generation read the terrain height (flattening, ground y)? Then the terrain heights of the
	 * bounding box are computed when the structure start is created (HeightField)
	 */
	public boolean needsHeightField() {
		return false;
	}

	/**
	 * Places the structure (1.20: called for every chunk inside the bounding box, world only writes the current chunk)
	 */
	public void generate(LevelAccessor world, TGSpawnData d, Random rnd) {
		if (d.removeJunk) {
			int sizeXr = d.direction == 0 || d.direction == 2 ? d.sizeX : d.sizeZ;
			int sizeZr = d.direction == 0 || d.direction == 2 ? d.sizeZ : d.sizeX;
			StructureBlockUtils.removeJunkInArea(world, d.x - 1, d.z - 1, sizeXr + 2, sizeZr + 2);
		}
		this.setBlocks(world, d.x, d.y, d.z, d.sizeX, d.sizeY, d.sizeZ, d.direction, d.color, rnd);
	}

	protected int getRotationShiftX(int direction) {
		return 0;
	}

	protected int getRotationShiftZ(int direction) {
		return 0;
	}

	//W-N-E-S
	public static Direction directionToFacing(int direction) {
		switch (direction) {
		case 0:
			return Direction.WEST;
		case 2:
			return Direction.EAST;
		case 3:
			return Direction.SOUTH;
		default:
			return Direction.NORTH;
		}
	}

	public enum BiomeColorType {
		WOODLAND, SNOW, DESERT, NETHER;
	}

	/**
	 * Common start of the 1.12 template structure setBlocks: swaps the direction if the given size is rotated
	 * @return {centerX, centerZ, direction}
	 */
	protected int[] getCenterAndDirection(int sizeX, int sizeZ, int direction) {
		int centerX, centerZ;
		if (((sizeX < this.minX) && (sizeZ > this.minX) && (sizeX >= this.minZ)) || ((sizeZ < this.minZ) && (sizeX > this.minZ) && (sizeZ >= this.minX))) {
			direction = (direction + 1) % 4;
			centerZ = (int) (sizeX / 2.0f);
			centerX = (int) (sizeZ / 2.0f);
		} else {
			centerX = (int) (sizeX / 2.0f);
			centerZ = (int) (sizeZ / 2.0f);
		}
		return new int[] { centerX, centerZ, direction };
	}

	protected static int rollBlockIndex(Random rnd, int totalWeight, int[] weights) {
		int roll = rnd.nextInt(totalWeight + 1);

		int sum = 0;
		for (int i = 0; i < weights.length; i++) {
			sum += weights[i];
			if (roll <= sum) {
				return i;
			}
		}
		return weights.length;
	}
}
