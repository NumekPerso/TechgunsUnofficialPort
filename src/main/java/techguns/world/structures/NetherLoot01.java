package techguns.world.structures;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.util.MBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockChestLoottable;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class NetherLoot01 extends NetherTemplateStructure {
	private static final ResourceLocation CHEST_LOOT = new ResourceLocation(Techguns.MODID, "chests/factory_building");
	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(MBlockRegister.NETHERRACK_ROCKY);
		blockList.add(new MBlock("techguns:nethermetal", 3));
		blockList.add(MBlockRegister.AIR);
		blockList.add(new MBlock("minecraft:nether_brick_fence", 0));
		blockList.add(new MBlock("minecraft:netherrack", 0));
		blockList.add(new MBlock("minecraft:skull", 1));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 5, CHEST_LOOT));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 2, 1, 200, 1).addMobType("zombiepigmansoldier", 1));
		blocks = StructureBlockUtils.loadStructureFromFile("nether_loot_01");
	}

	public NetherLoot01() {
		super(6, 10, 6, 6, 10, 6, 1, 16);
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
