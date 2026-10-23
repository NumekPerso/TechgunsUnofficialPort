package techguns.world.structures;

import java.util.ArrayList;
import java.util.List;

import techguns.blocks.EnumMonsterSpawnerType;
import techguns.util.MBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class NetherAltarSmall extends NetherTemplateStructure {
	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(new MBlock("techguns:nethermetal", 0));
		blockList.add(new MBlock("minecraft:air", 0));
		blockList.add(new MBlock("techguns:nethermetal", 8));
		blockList.add(new MBlock("techguns:nethermetal", 9));
		blockList.add(new MBlock("minecraft:nether_brick_stairs", 0));
		blockList.add(new MBlock("minecraft:nether_brick_fence", 0));
		blockList.add(new MBlock("techguns:nethermetal", 7));
		blockList.add(new MBlock("minecraft:nether_brick_stairs", 2));
		blockList.add(new MBlock("minecraft:nether_brick_stairs", 3));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 3, 2, 200, 1).addMobType("cyberdemon", 1));
		blockList.add(new MBlock("minecraft:nether_brick_stairs", 1));
		blocks = StructureBlockUtils.loadStructureFromFile("nether_altar_small");
	}

	public NetherAltarSmall() {
		super(11, 10, 11, 11, 10, 11, -2, 16);
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
