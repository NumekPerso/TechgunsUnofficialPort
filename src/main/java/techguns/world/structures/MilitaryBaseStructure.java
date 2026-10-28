package techguns.world.structures;

import java.util.Random;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import techguns.world.StructureBlockUtils;
import techguns.world.TGSpawnContext;
import techguns.world.TGSpawnData;

public class MilitaryBaseStructure extends WorldgenStructure {

	public MilitaryBaseStructure(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		//NOT NEEDED, DUMMY
	}

	/** 1.12: 5 blocks height difference, sampled every 8 blocks: camps were spawned on hillsides */
	protected static final int HEIGHT_DIFF_LIMIT = 3;

	@Override
	protected int getStep() {
		return 4;
	}

	@Override
	public TGSpawnData findSpawn(TGSpawnContext ctx, int chunkX, int chunkZ, int sizeX, int sizeY, int sizeZ, Random rnd) {
		int x = chunkX * 16;
		int z = chunkZ * 16;

		int y = ctx.getValidSpawnYArea(x, z, sizeX, sizeZ, HEIGHT_DIFF_LIMIT, getStep());

		if (y > 0) {
			return new TGSpawnData(x, y, z, sizeX, sizeY, sizeZ, 0);
		} else {
			if (sizeX >= 32 && sizeZ >= 32) {
				return this.findSpawn(ctx, chunkX, chunkZ, sizeX - 16, sizeY, sizeZ - 16, rnd);
			}
		}
		return null;
	}

	@Override
	public boolean needsHeightField() {
		return true;
	}

	@Override
	public BoundingBox getBoundingBox(TGSpawnData d) {
		return new BoundingBox(d.x - 3, d.y - 40, d.z - 3, d.x + d.sizeX + 3, d.y + 40, d.z + d.sizeZ + 3);
	}

	@Override
	public void generate(LevelAccessor world, TGSpawnData d, Random rnd) {
		StructureBlockUtils.removeJunkInArea(world, d.x - 1, d.z - 1, d.sizeX + 2, d.sizeZ + 2);

		MilitaryCamp camp = new MilitaryCamp(2, rnd); //1.12: 4
		camp.colorType = d.color;
		camp.init(d.x, d.y, d.z, d.sizeX, d.sizeZ);
		camp.setBlocks(world, rnd);
	}

	@Override
	public int getSizeX(Random rnd) {
		return 32 + rnd.nextInt(48);
	}

	@Override
	public int getSizeZ(Random rnd) {
		return 32 + rnd.nextInt(48);
	}

	@Override
	public int getSizeY(Random rnd) {
		return 8;
	}

}
