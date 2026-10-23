package techguns.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import techguns.util.MBlock;
import techguns.util.MultiMMBlockIndexRoll;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

/**
 * Structure generation helpers of the 1.12 BlockUtils (the mining helpers of BlockUtils are in techguns.util.BlockUtils).
 * 1.20: World -> LevelAccessor; height queries use the virtual heights of TGStructureLevel during world generation.
 */
public class StructureBlockUtils {

	private static final String FILEPATH = "/assets/techguns/structures/";

	public static final float[][] FILTER_GAUSSIAN_3x3 = new float[][] { { 1 / 16f, 1 / 8f, 1 / 16f }, { 1 / 8f, 1 / 4f, 1 / 8f }, { 1 / 16f, 1 / 8f, 1 / 16f } };

	public static final float[][] FILTER_GAUSSIAN_5x5 = new float[][] { { .00325f, .01375f, .0235f, .01375f, .00325f }, { .01375f, .059f, .097f, .059f, .01375f },
			{ .0235f, .097f, .159f, .097f, .0235f }, { .01375f, .059f, .097f, .059f, .01375f }, { .00325f, .01375f, .0235f, .01375f, .00325f } };

	public static BlockPos rotateAroundY(MutableBlockPos pos, BlockPos axis, int steps) {
		int offsetX = pos.getX() - axis.getX();
		int offsetZ = pos.getZ() - axis.getZ();

		int newOffsetX = offsetX;
		int newOffsetZ = offsetZ;

		for (int i = steps; i > 0; i--) {
			int tmp = newOffsetX;

			newOffsetX = newOffsetZ;
			newOffsetZ = -tmp;
		}
		pos.set(axis.getX() + newOffsetX, pos.getY(), axis.getZ() + newOffsetZ);
		return pos;
	}

	public static MutableBlockPos rotateAroundY(MutableBlockPos pos, Vec3 axis, int steps) {
		double offsetX = pos.getX() + 0.5d - axis.x;
		double offsetZ = pos.getZ() + 0.5d - axis.z;

		double newOffsetX = offsetX;
		double newOffsetZ = offsetZ;

		for (int i = steps; i > 0; i--) {
			double tmp = newOffsetX;

			newOffsetX = newOffsetZ;
			newOffsetZ = -tmp;
		}
		pos.set(Math.floor(axis.x + newOffsetX), pos.getY(), Math.floor(axis.z + newOffsetZ));
		return pos;
	}

	public static void setBlockToAir(LevelAccessor world, BlockPos pos) {
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
	}

	public static void setBlockState(LevelAccessor world, BlockPos pos, BlockState state) {
		world.setBlock(pos, state, 3);
	}

	/**
	 * First free y above the ground, ignoring trees (1.12 getHeightValueNoTrees)
	 */
	public static int getHeightValueNoTrees(LevelAccessor w, int x, int z) {
		if (w instanceof TGStructureLevel) {
			return w.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
		}
		int y = w.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		MutableBlockPos p = new MutableBlockPos(x, y - 1, z);
		BlockState bs = w.getBlockState(p);
		while (y > w.getMinBuildHeight() && (bs.is(BlockTags.LOGS) || bs.is(BlockTags.LEAVES) || bs.is(BlockTags.REPLACEABLE_BY_TREES))) {
			y--;
			bs = w.getBlockState(p.move(Direction.DOWN));
		}
		return y;
	}

	/**
	 * First free y above the highest block (1.12 getHeightValueTopBlockAll)
	 */
	public static int getHeightValueTopBlockAll(LevelAccessor w, int x, int z) {
		if (w instanceof TGStructureLevel tgl) {
			return tgl.getRealHeight(x, z);
		}
		return w.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
	}

	public static boolean isGroundBlock(BlockState b) {
		return b.is(BlockTags.DIRT) || b.is(BlockTags.SAND) || b.is(BlockTags.BASE_STONE_OVERWORLD) || b.is(BlockTags.BASE_STONE_NETHER)
				|| b.is(BlockTags.TERRACOTTA) || b.is(Blocks.GRAVEL) || b.is(Blocks.CLAY) || b.is(Tags.Blocks.STONE) || b.is(Tags.Blocks.COBBLESTONE)
				|| b.is(Tags.Blocks.ORES) || b.is(Tags.Blocks.SANDSTONE) || b.is(Blocks.FARMLAND) || b.is(Blocks.DIRT_PATH) || b.is(Blocks.MYCELIUM);
	}

	public static void removeJunkInArea(LevelAccessor world, int posX, int posZ, int sizeX, int sizeZ) {
		MutableBlockPos pos = new MutableBlockPos();
		TGStructureLevel tgl = world instanceof TGStructureLevel t ? t : null;
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {
				if (tgl != null && !tgl.isInWriteBox(posX + x, posZ + z)) {
					continue;
				}
				int y = getHeightValueTopBlockAll(world, posX + x, posZ + z);
				boolean stop = false;
				for (; y > world.getMinBuildHeight() && !stop; y--) {
					pos.set(posX + x, y, posZ + z);
					BlockState bs = tgl != null ? tgl.getRealBlockState(pos) : world.getBlockState(pos);
					if (!isGroundBlock(bs)) {
						if (!bs.isAir()) {
							world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
						}
					} else {
						stop = true;
					}
				}
			}
		}
	}

	public static void fillBlocks(LevelAccessor world, MutableBlockPos start, int sizeX, int sizeY, int sizeZ, BlockState block) {
		int x = start.getX();
		int y = start.getY();
		int z = start.getZ();
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeZ; j++) {
				for (int k = 0; k < sizeY; k++) {
					MBlock.placeState(world, start.set(x + i, y + k, z + j), block, 3);
				}
			}
		}
	}

	public static void fillBlocksHollow(LevelAccessor world, MutableBlockPos start, int sizeX, int sizeY, int sizeZ, BlockState block) {
		int x = start.getX();
		int y = start.getY();
		int z = start.getZ();

		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeZ; j++) {
				for (int k = 0; k < sizeY; k++) {
					if (j == 0 || j == sizeZ - 1 || i == 0 || i == sizeX - 1) {
						world.setBlock(start.set(x + i, y + k, z + j), block, 3);
					}
				}
			}
		}
	}

	public static void flattenArea(LevelAccessor world, int posX, int posZ, int sizeX, int sizeZ, int maxHeightDiff) {
		int min = -1;
		int max = 0;
		boolean first = true;
		ArrayList<Integer> heights = new ArrayList<Integer>();
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {
				int y = getHeightValueNoTrees(world, posX + x, posZ + z) - 1;

				heights.add(y);
				if (first || y > max)
					max = y;
				if (first || y < min)
					min = y;
				first = false;
			}
		}

		int span = max - min;

		float median = 0;
		Collections.sort(heights);
		if (heights.size() > 2) {
			if (heights.size() % 2 == 0) {
				median = (heights.get(((int) Math.floor(heights.size() / 2)) - 1) + heights.get(((int) Math.ceil(heights.size() / 2)) - 1)) / 2;
			} else {
				median = heights.get((heights.size() / 2) - 1);
			}
		}

		float averageHeight = median;
		float f = averageHeight - ((float) maxHeightDiff / 2.0f);

		MutableBlockPos p = new MutableBlockPos();
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {
				int y = getHeightValueNoTrees(world, posX + x, posZ + z) - 1;
				int newY;
				if (span <= maxHeightDiff)
					newY = y;
				else
					newY = Math.round(((((float) (y - min) / (float) span) * (float) maxHeightDiff) + f));
				adjustHeightAtPos(world, posX + x, posZ + z, y, p, newY);
			}
		}
	}

	private static void adjustHeightAtPos(LevelAccessor world, int x, int z, int y, MutableBlockPos p, int newY) {
		if (newY > y) {
			BlockState b = world.getBlockState(p.set(x, y, z));
			if (!isGroundBlock(b)) {
				b = Blocks.DIRT.defaultBlockState();
			}
			if (b.is(Blocks.GRASS_BLOCK)) {
				// grass below grass
				world.setBlock(p.set(x, y, z), Blocks.DIRT.defaultBlockState(), 2);
			}
			for (int i = y + 1; i <= newY; i++) {
				world.setBlock(p.set(x, i, z), (i == newY) ? b : (b.is(Blocks.GRASS_BLOCK) ? Blocks.DIRT.defaultBlockState() : b), 2);
			}
		} else if (newY < y) {
			BlockState top = world.getBlockState(p.set(x, y, z));
			for (int i = y; i > newY; i--) {
				world.setBlock(p.set(x, i, z), Blocks.AIR.defaultBlockState(), 2);
			}
			if (top.is(Blocks.GRASS_BLOCK) && world.getBlockState(p.set(x, newY, z)).is(Blocks.DIRT)) {
				world.setBlock(p, top, 2);
			}
		}
	}

	public static void apply2DHeightmapFilter(LevelAccessor world, int posX, int posZ, int sizeX, int sizeZ, float[][] filter) {
		int xoffset = -filter.length / 2;
		int zoffset = -filter[0].length / 2;
		MutableBlockPos p = new MutableBlockPos();
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {
				int y = getHeightValueNoTrees(world, posX + x, posZ + z) - 1;
				float newY = 0.0f;
				for (int i = 0; i < filter.length; i++) {
					for (int j = 0; j < filter[i].length; j++) {
						newY += (getHeightValueNoTrees(world, posX + x + xoffset + i, posZ + z + zoffset + j) - 1.0f) * filter[i][j];
					}
				}
				adjustHeightAtPos(world, posX + x, posZ + z, y, p, Math.round(newY));
			}
		}
	}

	public static short[][] loadStructureFromFile(String filename) {
		String path = FILEPATH + filename;
		try (InputStream is = StructureBlockUtils.class.getResourceAsStream(path)) {
			if (is == null) {
				throw new IOException("Missing structure file " + path);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			int count = Integer.parseInt(br.readLine().trim());
			short[][] blocks = new short[count][4];
			String line;
			int i = 0;
			while ((line = br.readLine()) != null && i < count) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				String[] s = line.split(",");
				for (int j = 0; j < s.length; j++) {
					blocks[i][j] = Short.parseShort(s[j].trim());
				}
				i++;
			}
			return blocks;

		} catch (IOException e) {
			e.printStackTrace();
			return new short[0][0];
		}
	}

	public static void cleanUpwards(LevelAccessor world, short[][] blocks, List<MBlock> blockList, int posX, int posY, int posZ, int centerX, int centerZ, int rotation, int pass, int range) {
		MutableBlockPos p = new MutableBlockPos();
		BlockPos axis = new BlockPos(posX + centerX, 1, posZ + centerZ);
		MBlock block = new MBlock(Blocks.AIR.defaultBlockState());
		for (int i = 0; i < blocks.length; i++) {
			int x = blocks[i][0];
			int y = blocks[i][1];
			int z = blocks[i][2];
			if (y == 0) {
				for (int o = 1; o <= range; o++) {
					setMBlockRotated(world, block, p.set(posX + x, posY + y + o, posZ + z), axis, rotation, null, BiomeColorType.WOODLAND);
				}
			}
		}
	}

	public static void placeScannedStructure(LevelAccessor world, short[][] blocks, List<MBlock> blockList, int posX, int posY, int posZ, int centerX, int centerZ, int rotation, int pass, EnumLootType loottype, BiomeColorType biome) {
		placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, rotation, pass, loottype, biome, 0, null);
	}

	public static void placeScannedStructure(LevelAccessor world, short[][] blocks, List<MBlock> blockList, int posX, int posY, int posZ, int centerX, int centerZ, int rotation, int pass, EnumLootType loottype, BiomeColorType biome, int indexRoll, Random rnd) {
		MutableBlockPos p = new MutableBlockPos();
		BlockPos axis = new BlockPos(posX + centerX, 1, posZ + centerZ);
		for (int i = 0; i < blocks.length; i++) {
			int x = blocks[i][0];
			int y = blocks[i][1];
			int z = blocks[i][2];

			MBlock block = blockList.get(blocks[i][3]);

			if (block.getPass() == pass) {
				if (block instanceof MultiMMBlockIndexRoll) {
					setMBlockRotatedIndexRoll(world, (MultiMMBlockIndexRoll) block, p.set(posX + x, posY + y, posZ + z), axis, rotation, loottype, biome, indexRoll, rnd);
				} else {
					setMBlockRotated(world, block, p.set(posX + x, posY + y, posZ + z), axis, rotation, loottype, biome);
				}
			}
		}
	}

	public static void placeFoundation(LevelAccessor world, short[][] blocks, List<MBlock> blockList, int posX, int posY, int posZ, int centerX, int centerZ, int rotation, int pass, int range) {
		MutableBlockPos p = new MutableBlockPos();
		BlockPos axis = new BlockPos(posX + centerX, 1, posZ + centerZ);
		for (int i = 0; i < blocks.length; i++) {
			int x = blocks[i][0];
			int y = blocks[i][1];
			int z = blocks[i][2];
			if (y == 0) {
				MBlock block = blockList.get(blocks[i][3]);
				if (block.getPass() == pass) {
					for (int o = 1; o <= range; o++) {
						setMBlockRotatedReplaceableOnly(world, block, p.set(posX + x, posY + y - o, posZ + z), axis, rotation, null);
					}
				}
			}
		}
	}

	/**
	 * stops the foundation if there is a solid layer found (2 blocks thick or more)
	 */
	public static void placeFoundationNether(LevelAccessor world, short[][] blocks, List<MBlock> blockList, int posX, int posY, int posZ, int centerX, int centerZ, int rotation, int pass, int range) {
		MutableBlockPos p = new MutableBlockPos();
		BlockPos axis = new BlockPos(posX + centerX, 1, posZ + centerZ);
		for (int i = 0; i < blocks.length; i++) {
			int x = blocks[i][0];
			int y = blocks[i][1];
			int z = blocks[i][2];
			if (y == 0) {
				MBlock block = blockList.get(blocks[i][3]);
				if (block.getPass() == pass) {

					int solidcount = 0;
					for (int o = 1; o <= range; o++) {
						p.set(posX + x, posY + y - o, posZ + z);
						if (getIsBlockReplaceable(world, new MutableBlockPos(p.getX(), p.getY(), p.getZ()), axis, rotation)) {
							setMBlockRotatedReplaceableOnly(world, block, p.set(posX + x, posY + y - o, posZ + z), axis, rotation, null);
							solidcount = 0;
						} else {
							solidcount++;
							if (solidcount >= 2) {
								break;
							}
						}
					}
				}
			}
		}
	}

	public static void setMBlockRotated(LevelAccessor w, MBlock mblock, MutableBlockPos pos, BlockPos axis, int rotation, EnumLootType loottype, BiomeColorType biome) {
		rotateAroundY(pos, axis, rotation);
		mblock.setBlock(w, pos, rotation, loottype, biome);
	}

	public static void setMBlockRotatedIndexRoll(LevelAccessor w, MultiMMBlockIndexRoll mblock, MutableBlockPos pos, BlockPos axis, int rotation, EnumLootType loottype, BiomeColorType biome, int indexRoll, Random rnd) {
		rotateAroundY(pos, axis, rotation);
		mblock.setBlock(w, pos, rotation, loottype, biome, indexRoll, rnd);
	}

	public static void setMBlockRotatedReplaceableOnly(LevelAccessor w, MBlock mblock, MutableBlockPos pos, BlockPos axis, int rotation, EnumLootType loottype) {
		rotateAroundY(pos, axis, rotation);
		mblock.setBlockReplaceableOnly(w, pos, rotation, loottype, BiomeColorType.WOODLAND);
	}

	public static boolean getIsBlockReplaceable(LevelAccessor w, MutableBlockPos pos, BlockPos axis, int rotation) {
		rotateAroundY(pos, axis, rotation);
		return w.getBlockState(pos).canBeReplaced();
	}

	/**
	 * Fills a 'hollow' sphere, where block1 is the inner block and block2 is the hull
	 */
	public static void fillSphere2(LevelAccessor world, int posX, int posY, int posZ, float radius, MBlock block1, MBlock block2) {
		fillSphere2(world, posX, posY, posZ, radius, 1.0f, block1, block2);
	}

	public static void fillSphere2(LevelAccessor world, int posX, int posY, int posZ, float radius, float thickness, MBlock block1, MBlock block2) {
		Vec3 center = new Vec3(posX, posY, posZ);

		float rsquared = (float) Math.pow(radius, 2.0f);
		float rsquared2 = (float) Math.pow(radius - thickness, 2.0f);

		MutableBlockPos p = new MutableBlockPos();

		int r = ((int) (radius));
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int x = posX + i;
					int y = posY + j;
					int z = posZ + k;
					Vec3 v = new Vec3(x, y, z);
					float dsquared = (float) center.distanceToSqr(v);
					if (dsquared < rsquared2) {
						block1.setBlock(world, p.set(x, y, z), 0);
					} else if (dsquared < rsquared) {
						block2.setBlock(world, p.set(x, y, z), 0);
					}
				}
			}
		}
	}

	public static void fillCylinder(LevelAccessor world, int x1, int y1, int z1, int x2, int y2, int z2, float radius, MBlock block1, MBlock block2) {
		Vec3 v1 = new Vec3(x1, y1, z1);
		Vec3 v2 = new Vec3(x2, y2, z2);
		Vec3 v2v1 = v1.subtract(v2);
		double l = v2v1.length();
		int tmp = 0;
		if (x1 > x2) {
			tmp = x2;
			x2 = x1;
			x1 = tmp;
		}
		if (y1 > y2) {
			tmp = y2;
			y2 = y1;
			y1 = tmp;
		}
		if (z1 > z2) {
			tmp = z2;
			z2 = z1;
			z1 = tmp;
		}

		int r = (int) radius;

		MutableBlockPos p = new MutableBlockPos();

		for (int x = x1 - r; x < x2 + r; x++) {
			for (int y = y1 - r; y < y2 + r; y++) {
				for (int z = z1 - r; z < z2 + r; z++) {
					Vec3 v0 = new Vec3(x, y, z);
					Vec3 v1v0 = v1.subtract(v0);
					Vec3 v_ = v2v1.cross(v1v0);
					double distance = v_.length() / l;

					if (distance < radius - 1.0f) {
						block1.setBlock(world, p.set(x, y, z), 0);
					} else if (distance < radius) {
						block2.setBlock(world, p.set(x, y, z), 0);
					}
				}
			}
		}

	}

	public static void drawLine(LevelAccessor world, int x1, int y1, int z1, int x2, int y2, int z2, MBlock block) {

		MutableBlockPos p = new MutableBlockPos();

		int dx = x2 - x1;
		int dy = y2 - y1;
		int dz = z2 - z1;

		int ax = Math.abs(dx) << 1;
		int ay = Math.abs(dy) << 1;
		int az = Math.abs(dz) << 1;

		int signx = (int) Math.signum(dx);
		int signy = (int) Math.signum(dy);
		int signz = (int) Math.signum(dz);

		int x = x1;
		int y = y1;
		int z = z1;

		int deltax, deltay, deltaz;
		if (ax >= Math.max(ay, az)) /* x dominant */ {
			deltay = ay - (ax >> 1);
			deltaz = az - (ax >> 1);
			while (true) {
				block.setBlock(world, p.set(x, y, z), 0);
				if (x == x2) {
					return;
				}

				if (deltay >= 0) {
					y += signy;
					deltay -= ax;
				}

				if (deltaz >= 0) {
					z += signz;
					deltaz -= ax;
				}

				x += signx;
				deltay += ay;
				deltaz += az;
			}
		} else if (ay >= Math.max(ax, az)) /* y dominant */ {
			deltax = ax - (ay >> 1);
			deltaz = az - (ay >> 1);
			while (true) {
				block.setBlock(world, p.set(x, y, z), 0);
				if (y == y2) {
					return;
				}

				if (deltax >= 0) {
					block.setBlock(world, p.set(x + signx, y, z), 0);
					x += signx;
					deltax -= ay;
				}

				if (deltaz >= 0) {
					block.setBlock(world, p.set(x, y, z + signz), 0);
					z += signz;
					deltaz -= ay;
				}

				y += signy;
				deltax += ax;
				deltaz += az;
			}
		} else if (az >= Math.max(ax, ay)) /* z dominant */ {
			deltax = ax - (az >> 1);
			deltay = ay - (az >> 1);
			while (true) {
				block.setBlock(world, p.set(x, y, z), 0);
				if (z == z2) {
					return;
				}

				if (deltax >= 0) {
					x += signx;
					deltax -= az;
				}

				if (deltay >= 0) {
					y += signy;
					deltay -= az;
				}

				z += signz;
				deltax += ax;
				deltay += ay;
			}
		}
	}

	public static void fillSphere(LevelAccessor world, int posX, int posY, int posZ, float radius, MBlock block) {
		Vec3 center = new Vec3(posX, posY, posZ);

		float rsquared = (float) Math.pow(radius, 2.0f);

		MutableBlockPos p = new MutableBlockPos();

		int r = ((int) (radius));
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int x = posX + i;
					int y = posY + j;
					int z = posZ + k;
					Vec3 v = new Vec3(x, y, z);
					float dsquared = (float) center.distanceToSqr(v);
					if (dsquared < rsquared) {
						block.setBlock(world, p.set(x, y, z), 0);
					}
				}
			}
		}
	}

	public static int getGroundY(LevelAccessor world, int x, int z) {
		if (world instanceof TGStructureLevel) {
			return world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
		}
		return world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
	}

	public static void fillBlocksAir(LevelAccessor world, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
		MutableBlockPos p = new MutableBlockPos();
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeZ; j++) {
				for (int k = 0; k < sizeY; k++) {
					world.setBlock(p.set(x + i, y + k, z + j), Blocks.AIR.defaultBlockState(), 2);
				}
			}
		}
	}

	/**
	 * convert from old rotation int to Direction 0=WEST(-x), 1=NORTH(-z), 2=EAST(+x); 3=SOUTH(+z)
	 */
	public static Direction rotationToFacing(int rot) {
		switch (rot) {
		case 0:
			return Direction.WEST;
		case 1:
			return Direction.SOUTH;
		case 2:
			return Direction.EAST;
		default:
			return Direction.NORTH;
		}
	}

	public static int facingToRotation(Direction facing) {
		switch (facing) {
		case WEST:
			return 0;
		case SOUTH:
			return 1;
		case EAST:
			return 2;
		default:
			return 3;
		}
	}
}
