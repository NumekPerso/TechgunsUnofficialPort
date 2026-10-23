package techguns.world.structures;

import java.util.ArrayList;
import java.util.List;

import techguns.util.MBlock;
import techguns.util.MultiMBlock;
import techguns.world.StructureBlockUtils;

public class NetherOreClusterSmall extends NetherTemplateStructure {
	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(new MBlock("minecraft:magma", 0));
		blockList.add(MBlockRegister.AIR);
		blockList.add(new MultiMBlock(new String[] { "techguns:orecluster", "minecraft:netherrack" }, new int[] { 7, 0 }, new int[] { 50, 50 }));
		blockList.add(new MBlock("techguns:orecluster", 7));
		blockList.add(new MultiMBlock(new String[] { "techguns:orecluster", "minecraft:air" }, new int[] { 7, 0 }, new int[] { 50, 50 }));
		blocks = StructureBlockUtils.loadStructureFromFile("nether_orecluster_small");
	}

	public NetherOreClusterSmall() {
		super(3, 3, 3, 3, 3, 3, 1, 2);
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
