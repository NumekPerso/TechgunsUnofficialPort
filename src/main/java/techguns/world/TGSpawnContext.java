package techguns.world;

import java.util.Arrays;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import techguns.util.MathUtil;

/**
 * 1.20: terrain queries when a structure start is created (the 1.12 BlockUtils spawn height checks on the real world),
 * using the chunk generator noise: deterministic and independent of the generated chunks.
 */
public class TGSpawnContext {

	protected final ChunkGenerator generator;
	protected final LevelHeightAccessor heightAccessor;
	protected final RandomState randomState;

	protected final Long2IntOpenHashMap surfaceCache = new Long2IntOpenHashMap();
	protected final Long2IntOpenHashMap floorCache = new Long2IntOpenHashMap();

	public TGSpawnContext(ChunkGenerator generator, LevelHeightAccessor heightAccessor, RandomState randomState) {
		this.generator = generator;
		this.heightAccessor = heightAccessor;
		this.randomState = randomState;
	}

	public int getSeaLevel() {
		return generator.getSeaLevel();
	}

	/**
	 * first free y above the highest non air block
	 */
	public int surface(int x, int z) {
		long key = ChunkPos.asLong(x, z);
		if (surfaceCache.containsKey(key)) {
			return surfaceCache.get(key);
		}
		int h = generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
		surfaceCache.put(key, h);
		return h;
	}

	/**
	 * first free y above the highest solid block
	 */
	public int floor(int x, int z) {
		long key = ChunkPos.asLong(x, z);
		if (floorCache.containsKey(key)) {
			return floorCache.get(key);
		}
		int h = generator.getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, heightAccessor, randomState);
		floorCache.put(key, h);
		return h;
	}

	/**
	 * return -1 when liquid is top block, used to determine valid landspawns
	 */
	public int getHeightValueNoTreesIgnoreLiquid(int x, int z) {
		int s = surface(x, z);
		if (floor(x, z) < s) {
			return -1;
		}
		return s;
	}

	/**
	 * return -1 when the top block is not liquid
	 */
	public int getHeightValueLiquid(int x, int z) {
		int s = surface(x, z);
		if (floor(x, z) >= s) {
			return -1;
		}
		return s;
	}

	public int getValidSpawnYArea(int x, int z, int sizeX, int sizeZ, int heightDiffLimit, int step) {
		int count = 0;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		int sum = 0;

		int ix = 0;
		while (ix <= sizeX) {
			int iz = 0;
			while (iz <= sizeZ) {
				int h = getHeightValueNoTreesIgnoreLiquid(x + ix, z + iz);
				if (h < 0) {
					return -1;
				}
				min = Math.min(min, h);
				max = Math.max(max, h);
				sum += h;
				count++;
				iz += step;
			}
			ix += step;
		}

		if ((max - min) > heightDiffLimit) {
			return -1;
		}
		return sum / count;
	}

	public int getValidSpawnY(int x, int z, int sizeX, int sizeZ, int heightDiffLimit) {
		int h0 = getHeightValueNoTreesIgnoreLiquid(x, z);
		int h1 = getHeightValueNoTreesIgnoreLiquid(x + sizeX, z);
		int h2 = getHeightValueNoTreesIgnoreLiquid(x, z + sizeZ);
		int h3 = getHeightValueNoTreesIgnoreLiquid(x + sizeX, z + sizeZ);

		if (h0 < 0 || h1 < 0 || h2 < 0 || h3 < 0) {
			return -1;
		}
		if (!((MathUtil.abs(h0 - h1) < heightDiffLimit) && (MathUtil.abs(h0 - h2) < heightDiffLimit) && (MathUtil.abs(h0 - h3) < heightDiffLimit))) {
			return -1;
		}
		return getMedianHeight(h0, h1, h2, h3);
	}

	public int getValidSpawnYWater(int x, int z, int sizeX, int sizeZ, int heightDiffLimit) {
		int h0 = getHeightValueLiquid(x, z);
		int h1 = getHeightValueLiquid(x + sizeX, z);
		int h2 = getHeightValueLiquid(x, z + sizeZ);
		int h3 = getHeightValueLiquid(x + sizeX, z + sizeZ);

		if (h0 < 0 || h1 < 0 || h2 < 0 || h3 < 0) {
			return -1;
		}
		if (!((MathUtil.abs(h0 - h1) < heightDiffLimit) && (MathUtil.abs(h0 - h2) < heightDiffLimit) && (MathUtil.abs(h0 - h3) < heightDiffLimit))) {
			return -1;
		}
		return getMedianHeight(h0, h1, h2, h3);
	}

	private static int getMedianHeight(int h0, int h1, int h2, int h3) {
		int[] height = { h0, h1, h2, h3 };
		Arrays.sort(height);
		return (height[1] + height[2]) / 2;
	}

	protected int getAirHeight(int x, int z, int minH, int maxH, int airH) {
		NoiseColumn col = generator.getBaseColumn(x, z, heightAccessor, randomState);
		int countAir = 0;
		for (int y = maxH; y > minH; y--) {
			BlockState bs = col.getBlock(y);
			if (bs.isAir()) {
				countAir++;
			} else {
				if (countAir >= airH) {
					return y;
				}
				countAir = 0;
			}
		}
		return -1;
	}

	/**
	 * Floor height of a cave of at least heightDiff blocks at the 4 corners of the chunk (nether structures)
	 */
	public int getCaveHeight(int cx, int cz, int heightDiff, int minY, int maxY) {
		int h1 = getAirHeight(cx * 16, cz * 16, minY, maxY, heightDiff);
		int h2 = getAirHeight(cx * 16 + 15, cz * 16, minY, maxY, heightDiff);
		int h3 = getAirHeight(cx * 16, cz * 16 + 15, minY, maxY, heightDiff);
		int h4 = getAirHeight(cx * 16 + 15, cz * 16 + 15, minY, maxY, heightDiff);

		if (MathUtil.allInRange(minY, maxY, h1, h2, h3, h4)) {
			int min = MathUtil.min(h1, h2, h3, h4);
			int max = MathUtil.max(h1, h2, h3, h4);
			if (max - min <= heightDiff) {
				return MathUtil.getAverageHeight(h1, h2, h3, h4);
			}
		}
		return -1;
	}

	/**
	 * Heights of the area of the bounding box (+2 blocks for the 5x5 heightmap filter of the military camps)
	 */
	public HeightField createHeightField(BoundingBox box) {
		int x0 = box.minX() - 2;
		int z0 = box.minZ() - 2;
		int sx = box.getXSpan() + 4;
		int sz = box.getZSpan() + 4;
		HeightField h = new HeightField(x0, z0, sx, sz);
		// the terrain noise is computed in 4x4 block cells and interpolated: sample the heights every 4 blocks and
		// interpolate (computing every column is far too slow for the big structures)
		final int G = 4;
		int gx0 = Math.floorDiv(x0, G) * G;
		int gz0 = Math.floorDiv(z0, G) * G;
		for (int x = x0; x < x0 + sx; x++) {
			int ax = Math.floorDiv(x - gx0, G) * G + gx0;
			float fx = (x - ax) / (float) G;
			for (int z = z0; z < z0 + sz; z++) {
				int az = Math.floorDiv(z - gz0, G) * G + gz0;
				float fz = (z - az) / (float) G;
				float s = lerp2(surface(ax, az), surface(ax + G, az), surface(ax, az + G), surface(ax + G, az + G), fx, fz);
				float f = lerp2(floor(ax, az), floor(ax + G, az), floor(ax, az + G), floor(ax + G, az + G), fx, fz);
				int si = Math.round(s);
				int fi = Math.min(si, Math.round(f));
				h.set(x, z, si, fi);
			}
		}
		return h;
	}

	protected static float lerp2(int v00, int v10, int v01, int v11, float fx, float fz) {
		float a = v00 + (v10 - v00) * fx;
		float b = v01 + (v11 - v01) * fx;
		return a + (b - a) * fz;
	}
}
