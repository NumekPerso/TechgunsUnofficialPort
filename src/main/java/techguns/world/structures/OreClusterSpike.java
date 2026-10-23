package techguns.world.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.world.level.LevelAccessor;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.blocks.EnumOreClusterType;
import techguns.blocks.EnumOreType;
import techguns.util.MBlock;
import techguns.util.MBlockOreClusterTypeOre;
import techguns.util.MBlockOreclusterType;
import techguns.util.MultiMBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class OreClusterSpike extends WorldgenStructure {

	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static EnumOreClusterType[] types = { EnumOreClusterType.COAL, EnumOreClusterType.COMMON_METAL, EnumOreClusterType.COMMON_GEM, EnumOreClusterType.RARE_METAL,
			EnumOreClusterType.SHINY_METAL, EnumOreClusterType.SHINY_GEM, EnumOreClusterType.URANIUM };

	/**
	 * Ores of the cluster types (used by SmallMine, OreClusterSpike, OreClusterMeteorBasis)
	 */
	static MBlock[] createOres() {
		return new MBlock[] { new MBlock("minecraft:coal_ore", 0),
				new MultiMBlock(new String[] { "minecraft:iron_ore", "techguns:basicore", "techguns:basicore" }, new int[] { 0, EnumOreType.ORE_COPPER.ordinal(), EnumOreType.ORE_TIN.ordinal() }, new int[] { 1, 1, 1 }),
				new MultiMBlock(new String[] { "minecraft:redstone_ore", "minecraft:lapis_ore" }, new int[] { 0, 0 }, new int[] { 1, 1 }),
				new MBlock("techguns:basicore", EnumOreType.ORE_LEAD.ordinal()),
				new MultiMBlock(new String[] { "minecraft:gold_ore", "techguns:basicore" }, new int[] { 0, EnumOreType.ORE_TITANIUM.ordinal() }, new int[] { 1, 1 }),
				new MultiMBlock(new String[] { "minecraft:diamond_ore", "minecraft:emerald_ore", "minecraft:stone" }, new int[] { 0, 0, 0 }, new int[] { 2, 1, 3 }),
				new MultiMBlock(new String[] { "techguns:basicore", "minecraft:stone" }, new int[] { EnumOreType.ORE_URANIUM.ordinal(), 0 }, new int[] { 1, 1 }), };
	}

	static MBlock[] ores = createOres();
	static int[] oreWeights = { 1, 1, 1, 1, 1, 1, 1 };
	static int[] clusterWeights = { 5, 10, 10, 10, 5, 2, 3 };
	static int totalweight = 0;
	static {
		totalweight = Arrays.stream(clusterWeights).sum();
		blockList.add(new MBlock("minecraft:air", 0));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 4, 2, 200, 1).addMobType("alienbug", 1));
		blockList.add(new MultiMBlock(new String[] { "minecraft:stone", "minecraft:air" }, new int[] { 0, 0 }, new int[] { 1, 1 }));
		blockList.add(new MBlock("techguns:slimyladder", 4));
		blockList.add(new MBlock("techguns:slimy", 0));
		blockList.add(new MBlock("minecraft:stone", 0));
		blockList.add(new MBlockOreClusterTypeOre(ores, oreWeights, new MBlock("minecraft:stone", 0), 0.5f));
		blockList.add(new MBlock("techguns:slimyladder", 2));
		blockList.add(new MBlockOreclusterType(types, clusterWeights, 0.5f, ores));
		blockList.add(new MBlock("techguns:slimyladder", 3));
		blockList.add(new MBlockOreclusterType(types, clusterWeights, 0, null));

		blocks = StructureBlockUtils.loadStructureFromFile("orecluster_spike");
	}

	public OreClusterSpike() {
		super(8, 7, 8, 8, 7, 8);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];

		int indexRoll = rollBlockIndex(rnd, totalweight, clusterWeights);

		StructureBlockUtils.cleanUpwards(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 6);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, this.lootTier, colorType, indexRoll, rnd);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 1, this.lootTier, colorType);
	}

}
