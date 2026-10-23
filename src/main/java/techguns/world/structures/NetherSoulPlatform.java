package techguns.world.structures;

import java.util.ArrayList;
import java.util.List;

import techguns.blocks.EnumMonsterSpawnerType;
import techguns.util.MBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class NetherSoulPlatform extends NetherTemplateStructure {
	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(new MBlock("techguns:nethermetal", 3));
		blockList.add(new MBlock("minecraft:air", 0));
		blockList.add(new MBlock("techguns:nethermetal", 4));
		blockList.add(new MBlock("minecraft:skull", 1));
		blockList.add(new MBlock("techguns:nethermetal", 5));
		blockList.add(new MBlock("minecraft:soul_sand", 0));
		blockList.add(new MBlock("techguns:nethermetal", 7));
		blockList.add(new MBlock("minecraft:glowstone", 0));
		blockList.add(new MBlock("minecraft:nether_brick_fence", 0));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 3, 2, 200, 1).addMobType("ghastling", 1));
		blocks = StructureBlockUtils.loadStructureFromFile("nether_soul_platform");
	}

	public NetherSoulPlatform() {
		super(13, 10, 13, 13, 10, 13, -2, 16);
	}

	@Override
	protected List<MBlock> getBlockList() {
		return blockList;
	}

	@Override
	protected short[][] getBlocks() {
		return blocks;
	}
}
