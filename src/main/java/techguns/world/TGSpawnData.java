package techguns.world;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

/**
 * 1.20: where and how a Techguns structure spawns, decided when the structure start is created (the 1.12
 * spawnStructureWorldgen arguments), saved in the structure piece.
 */
public class TGSpawnData {
	public int x;
	public int y;
	public int z;
	public int sizeX;
	public int sizeY;
	public int sizeZ;
	public int direction;
	public BiomeColorType color = BiomeColorType.WOODLAND;
	public long seed;
	public boolean removeJunk = false;
	/** structure specific values */
	public int[] extra = new int[0];
	@Nullable
	public HeightField heights;

	public TGSpawnData() {
	}

	public TGSpawnData(int x, int y, int z, int sizeX, int sizeY, int sizeZ, int direction) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.direction = direction;
	}

	public TGSpawnData setExtra(int... extra) {
		this.extra = extra;
		return this;
	}

	/**
	 * Area around the rotation center, big enough for all rotations
	 */
	public BoundingBox getDefaultBoundingBox(int marginXZ, int below, int above) {
		int cx = x + sizeX / 2;
		int cz = z + sizeZ / 2;
		// rotation around (x+centerX, z+centerZ), centerX/Z may be swapped by the structure
		int r = Math.max(sizeX, sizeZ) / 2 + Math.abs(sizeX - sizeZ) / 2 + 2 + marginXZ;
		return new BoundingBox(cx - r, y - below, cz - r, cx + r, y + Math.max(sizeY, 1) + above, cz + r);
	}

	public CompoundTag save() {
		CompoundTag t = new CompoundTag();
		t.putInt("x", x);
		t.putInt("y", y);
		t.putInt("z", z);
		t.putInt("sx", sizeX);
		t.putInt("sy", sizeY);
		t.putInt("sz", sizeZ);
		t.putInt("dir", direction);
		t.putInt("color", color.ordinal());
		t.putLong("seed", seed);
		t.putBoolean("junk", removeJunk);
		t.putIntArray("extra", extra);
		if (heights != null) {
			t.put("heights", heights.save());
		}
		return t;
	}

	public static TGSpawnData load(CompoundTag t) {
		TGSpawnData d = new TGSpawnData(t.getInt("x"), t.getInt("y"), t.getInt("z"), t.getInt("sx"), t.getInt("sy"), t.getInt("sz"), t.getInt("dir"));
		d.color = BiomeColorType.values()[Math.max(0, Math.min(t.getInt("color"), BiomeColorType.values().length - 1))];
		d.seed = t.getLong("seed");
		d.removeJunk = t.getBoolean("junk");
		d.extra = t.getIntArray("extra");
		if (t.contains("heights")) {
			d.heights = HeightField.load(t.getCompound("heights"));
		}
		return d;
	}
}
