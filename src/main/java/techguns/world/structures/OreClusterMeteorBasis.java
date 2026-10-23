package techguns.world.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.world.level.LevelAccessor;
import techguns.blocks.EnumConcreteType;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.blocks.EnumOreClusterType;
import techguns.blocks.TGMetalPanelType;
import techguns.util.MBlock;
import techguns.util.MBlockOreclusterType;
import techguns.util.MultiMBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class OreClusterMeteorBasis extends WorldgenStructure {

	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static EnumOreClusterType[] types = { EnumOreClusterType.COAL, EnumOreClusterType.COMMON_METAL, EnumOreClusterType.COMMON_GEM, EnumOreClusterType.RARE_METAL,
			EnumOreClusterType.SHINY_METAL, EnumOreClusterType.SHINY_GEM, EnumOreClusterType.URANIUM, EnumOreClusterType.NETHER_CRYSTAL };
	static MBlock[] ores;
	static {
		MBlock[] o = OreClusterSpike.createOres();
		ores = Arrays.copyOf(o, o.length + 1);
		ores[o.length] = new MultiMBlock(new String[] { "minecraft:netherrack", "minecraft:quartz_ore", "minecraft:glowstone" }, new int[] { 0, 0, 0 }, new int[] { 2, 1, 1 });
	}
	static int[] oreWeights = { 1, 1, 1, 1, 1, 1, 1, 1 };
	static int[] clusterWeights = { 5, 5, 5, 5, 5, 5, 5, 15 };
	static int totalweight = 0;
	static {
		totalweight = Arrays.stream(clusterWeights).sum();
		blockList.add(new MBlock("minecraft:stone", 0));
		blockList.add(new MBlock("minecraft:gravel", 0));
		blockList.add(new MBlock("techguns:sandbags", 0));
		blockList.add(new MBlock("minecraft:iron_bars", 0));
		blockList.add(new MBlock("minecraft:air", 0));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 2, 1, 200, 1).addMobType("armysoldier", 4).addMobType("commando", 1));
		blockList.add(new MBlock("techguns:metalpanel", TGMetalPanelType.PANEL_LARGE_BORDER.ordinal()));
		blockList.add(new MultiMBlock(new String[] { "minecraft:magma", "minecraft:stone" }, new int[] { 0, 0 }, new int[] { 1, 1 }));
		blockList.add(new MBlock("techguns:concrete", EnumConcreteType.CONCRETE_BROWN_LIGHT_SCAFF.ordinal()));
		blockList.add(new MBlock("techguns:lamp0", 6));
		blockList.add(new MBlock("minecraft:glass_pane", 15));
		blockList.add(new MBlock("techguns:metalpanel", 7));
		blockList.add(new MBlock("techguns:bunkerdoor", 1));
		blockList.add(new MBlock("techguns:bunkerdoor", 8));
		blockList.add(new MBlock("techguns:lamp0", 7));
		blockList.add(new MBlock("minecraft:magma", 0));
		blockList.add(new MBlockOreclusterType(types, clusterWeights, 0.5f, ores));
		blockList.add(new MBlock("techguns:ladder0", 12));
		blockList.add(new MBlockOreclusterType(types, clusterWeights, 0, null));
		blockList.add(new MBlock("techguns:ladder0", 0));
		blockList.add(new MBlock("minecraft:air", 0));

		blocks = StructureBlockUtils.loadStructureFromFile("orecluster_meteorbase");
	}

	public OreClusterMeteorBasis() {
		super(17, 12, 17, 17, 12, 17);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];

		int indexRoll = rollBlockIndex(rnd, totalweight, clusterWeights);

		StructureBlockUtils.cleanUpwards(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 30);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY - 5, posZ, centerX, centerZ, direction, 0, this.lootTier, colorType, indexRoll, rnd);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY - 5, posZ, centerX, centerZ, direction, 1, this.lootTier, colorType);
	}

}
