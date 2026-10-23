package techguns.world.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.world.level.LevelAccessor;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.blocks.EnumOreClusterType;
import techguns.util.MBlock;
import techguns.util.MBlockBiomeColorType;
import techguns.util.MBlockOreClusterTypeOre;
import techguns.util.MBlockOreclusterType;
import techguns.util.MultiMMBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class SmallMine extends WorldgenStructure {

	static short[][] blocks;
	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static EnumOreClusterType[] types = { EnumOreClusterType.COAL, EnumOreClusterType.COMMON_METAL, EnumOreClusterType.COMMON_GEM, EnumOreClusterType.RARE_METAL,
			EnumOreClusterType.SHINY_METAL, EnumOreClusterType.SHINY_GEM, EnumOreClusterType.URANIUM };
	static MBlock[] ores = OreClusterSpike.createOres();
	static int[] oreWeights = { 1, 1, 1, 1, 1, 1, 1 };
	static int[] clusterWeights = { 10, 10, 10, 5, 2, 2, 2 };
	static int totalweight = 0;
	static {
		totalweight = Arrays.stream(clusterWeights).sum();
		blockList.add(new MBlock("minecraft:air", 0));
		blockList.add(new MBlock("minecraft:stone", 0));
		blockList.add(new MBlock("minecraft:log", 0));
		blockList.add(new MBlockBiomeColorType(new String[] { "minecraft:grass", "minecraft:snow", "minecraft:sand", "minecraft:netherrack" }, new int[] { 0, 0, 0, 0 }));
		blockList.add(new MBlock("minecraft:log", 8));
		blockList.add(new MultiMMBlock(new MBlock[] { new MBlockBiomeColorType(new String[] { "minecraft:grass", "minecraft:grass", "minecraft:sand", "minecraft:netherrack" }, new int[] { 0, 0, 0, 0 }),
				new MBlock("minecraft:air", 0) }, new int[] { 4, 1 }));
		blockList.add(new MBlock("minecraft:rail", 1));
		blockList.add(new MBlock("minecraft:log", 4));
		blockList.add(new MBlock("minecraft:stone_stairs", 1));
		blockList.add(new MBlock("minecraft:rail", 3));
		blockList.add(new MBlock("minecraft:torch", 3));
		blockList.add(new MBlock("minecraft:torch", 4));
		blockList.add(new MBlock("minecraft:torch", 2));
		blockList.add(new MBlockOreClusterTypeOre(ores, oreWeights, new MBlock("minecraft:stone", 0), 0.75f));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 2, 1, 200, 1).addMobType("zombieminer", 1));
		blockList.add(new MBlockOreclusterType(types, clusterWeights, 0.5f, ores));
		blockList.add(new MBlockOreclusterType(types, clusterWeights, 0, null));
		blocks = StructureBlockUtils.loadStructureFromFile("small_mine");
	}

	public SmallMine() {
		super(17, 11, 11, 17, 11, 11);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];
		int indexRoll = rollBlockIndex(rnd, totalweight, clusterWeights);
		StructureBlockUtils.cleanUpwards(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 4);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY - 5, posZ, centerX, centerZ, direction, 0, this.lootTier, colorType, indexRoll, rnd);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY - 5, posZ, centerX, centerZ, direction, 1, this.lootTier, colorType);
	}
}
