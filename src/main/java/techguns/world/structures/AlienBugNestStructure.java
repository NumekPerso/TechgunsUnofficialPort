package techguns.world.structures;

import java.util.Random;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import techguns.world.TGSpawnContext;
import techguns.world.TGSpawnData;

public class AlienBugNestStructure extends WorldgenStructure {

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		//NOT NEEDED, DUMMY
	}

	@Override
	public TGSpawnData findSpawn(TGSpawnContext ctx, int chunkX, int chunkZ, int sizeX, int sizeY, int sizeZ, Random rnd) {
		int x = chunkX * 16;
		int z = chunkZ * 16;
		int y = ctx.getValidSpawnY(x, z, sizeX, sizeZ, 256);
		if (y > 0) {
			return new TGSpawnData(x, y - 2, z, sizeX, sizeY, sizeZ, 0);
		}
		return null;
	}

	@Override
	public BoundingBox getBoundingBox(TGSpawnData d) {
		// main segment + 5 segments at up to 23 blocks + additional segments up to 23 blocks further, radius <= 6
		int cx = d.x + 16;
		int cz = d.z + 16;
		return new BoundingBox(cx - 64, d.y - 48, cz - 64, cx + 64, d.y + 16, cz + 64);
	}

	@Override
	public void generate(LevelAccessor world, TGSpawnData d, Random rnd) {
		new AlienBugNest(d.x, d.y, d.z, 16 + rnd.nextInt(16), d.sizeY, 16 + rnd.nextInt(16), rnd).setBlocks(world, rnd);
	}
}
