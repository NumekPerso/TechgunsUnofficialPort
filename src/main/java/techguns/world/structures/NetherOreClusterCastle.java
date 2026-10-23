package techguns.world.structures;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import techguns.TGuns;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.util.MBlock;
import techguns.util.MultiMBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class NetherOreClusterCastle extends NetherTemplateStructure {
	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(new MBlock("techguns:nethermetal", 0));
		blockList.add(new MBlock("techguns:nethermetal", 7));
		blockList.add(MBlockRegister.NETHERBRICKS_C13);
		blockList.add(MBlockRegister.AIR);
		blockList.add(new MBlock("minecraft:nether_brick_fence", 0));
		blockList.add(new MBlock("techguns:nethermetal", 6));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 2, 1, 200, 0).addMobType("zombiepigmansoldier", 1).setWeaponOverride(new ItemStack(TGuns.boltaction)));
		blockList.add(new MultiMBlock(new String[] { "techguns:orecluster", "minecraft:air" }, new int[] { 7, 0 }, new int[] { 40, 60 }));
		blockList.add(new MBlock("techguns:orecluster", 7));
		blocks = StructureBlockUtils.loadStructureFromFile("nether_orecluster_castle");
	}

	public NetherOreClusterCastle() {
		super(11, 9, 11, 11, 9, 11, -1, 16);
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
