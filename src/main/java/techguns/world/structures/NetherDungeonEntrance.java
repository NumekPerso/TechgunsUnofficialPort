package techguns.world.structures;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.level.LevelAccessor;
import techguns.util.MBlock;
import techguns.world.StructureBlockUtils;

public class NetherDungeonEntrance extends WorldgenStructure {

	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(new MBlock("techguns:nethermetal", 0));
		blockList.add(new MBlock("techguns:nethermetal", 8));
		blockList.add(new MBlock("techguns:nethermetal", 9));
		blockList.add(new MBlock("techguns:nethermetal", 7));
		blockList.add(new MBlock("techguns:door3x3", 4));
		blockList.add(new MBlock("techguns:door3x3", 12));
		blockList.add(new MBlock("minecraft:nether_brick_stairs", 1));
		blocks = StructureBlockUtils.loadStructureFromFile("netherdungeon_entrance");
	}

	public NetherDungeonEntrance() {
		super(10, 6, 7, 10, 6, 7);
		this.setXYZSize(10, 6, 7);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX - 2, centerZ, direction, 0, this.lootTier, colorType);
	}
}
