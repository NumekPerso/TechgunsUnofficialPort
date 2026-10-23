package techguns.world;

import net.minecraft.nbt.CompoundTag;

/**
 * 1.20: terrain heights of the area of a structure, computed from the chunk generator noise when the structure start
 * is created (deterministic: the same in every chunk the structure is generated in, see TGStructureLevel).
 * surface = first free y above the highest non air block (WORLD_SURFACE_WG), floor = first free y above the highest
 * solid block (OCEAN_FLOOR_WG), floor < surface means water/lava on top.
 */
public class HeightField {
	public final int x0;
	public final int z0;
	public final int sizeX;
	public final int sizeZ;
	protected final short[] surface;
	protected final short[] floor;

	public HeightField(int x0, int z0, int sizeX, int sizeZ) {
		this.x0 = x0;
		this.z0 = z0;
		this.sizeX = sizeX;
		this.sizeZ = sizeZ;
		this.surface = new short[sizeX * sizeZ];
		this.floor = new short[sizeX * sizeZ];
	}

	public boolean contains(int x, int z) {
		return x >= x0 && z >= z0 && x < x0 + sizeX && z < z0 + sizeZ;
	}

	protected int index(int x, int z) {
		return (x - x0) * sizeZ + (z - z0);
	}

	public int surface(int x, int z) {
		return surface[index(x, z)];
	}

	public int floor(int x, int z) {
		return floor[index(x, z)];
	}

	public void set(int x, int z, int surfaceY, int floorY) {
		int i = index(x, z);
		surface[i] = (short) surfaceY;
		floor[i] = (short) floorY;
	}

	public CompoundTag save() {
		CompoundTag t = new CompoundTag();
		t.putInt("x0", x0);
		t.putInt("z0", z0);
		t.putInt("sx", sizeX);
		t.putInt("sz", sizeZ);
		int[] packed = new int[surface.length];
		for (int i = 0; i < packed.length; i++) {
			packed[i] = (surface[i] << 16) | (floor[i] & 0xFFFF);
		}
		t.putIntArray("h", packed);
		return t;
	}

	public static HeightField load(CompoundTag t) {
		HeightField h = new HeightField(t.getInt("x0"), t.getInt("z0"), t.getInt("sx"), t.getInt("sz"));
		int[] packed = t.getIntArray("h");
		for (int i = 0; i < packed.length && i < h.surface.length; i++) {
			h.surface[i] = (short) (packed[i] >> 16);
			h.floor[i] = (short) (packed[i] & 0xFFFF);
		}
		return h;
	}
}
