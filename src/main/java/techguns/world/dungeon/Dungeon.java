package techguns.world.dungeon;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import techguns.world.dungeon.presets.IDungeonPreset;

/**
 * 1.20: world.rand -> random given to generate (the structure random during world generation)
 */
public class Dungeon {

	private static final int GENERATE_ATTEMPTS = 5;

	IDungeonPreset preset;

	//maximum block extents
	int maxSizeX;
	int maxSizeY;
	int maxSizeZ;

	//number of segments, NOT blocks
	int sX;
	int sY;
	int sZ;

	public Direction startRotation;
	public BlockPos startPos;

	public Dungeon(IDungeonPreset preset) {
		this.preset = preset;
	}

	public void generate(LevelAccessor world, int posX, int posY, int posZ, int maxSizeX, int maxSizeY, int maxSizeZ) {
		generate(world, posX, posY, posZ, maxSizeX, maxSizeY, maxSizeZ, new Random(world.getRandom().nextLong()));
	}

	public void generate(LevelAccessor world, int posX, int posY, int posZ, int maxSizeX, int maxSizeY, int maxSizeZ, Random rand) {

		this.maxSizeX = maxSizeX;
		this.maxSizeY = maxSizeY;
		this.maxSizeZ = maxSizeZ;

		this.sX = maxSizeX / preset.getSizeXZ();
		this.sY = maxSizeY / preset.getSizeY();
		this.sZ = maxSizeZ / preset.getSizeXZ();

		IDungeonPath path = null;
		for (int i = 0; i < GENERATE_ATTEMPTS; i++) {
			IDungeonPath p = preset.getDungeonPath(sX, sY, sZ, rand);
			preset.initDungeonPath(p);
			p.generatePath();
			if (path == null || p.getNumSegments() > path.getNumSegments()) {
				path = p;
			}
		}

		path.generateDungeon(world, posX, posY, posZ, preset);

		path.generateNPCSpawners(world, posX, posY, posZ, preset);

		this.startPos = path.getStartPos();
		this.startRotation = path.getEntranceRotation();
	}

}
