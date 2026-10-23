package techguns.world.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.world.level.LevelAccessor;
import techguns.TGFluids;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.blocks.EnumOreClusterType;
import techguns.util.MBlock;
import techguns.util.MBlockOreclusterType;
import techguns.util.MultiMMBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

/**
 * Oil ore cluster in the desert. Like in 1.12, only spawned when a world spawn oil fluid exists
 * (TGFluids.OIL_WORLDSPAWN, from another mod), otherwise the lava/magma fallback is only used by /place.
 */
public class DesertOilCluster extends WorldgenStructure {

	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static EnumOreClusterType[] types = { EnumOreClusterType.OIL };
	static MBlock[] ores = null;
	static MBlock oilblock;
	static MBlock oilblockSand;
	static int[] oreWeights = { 1 };
	static int[] clusterWeights = { 10 };
	static int totalweight = 0;
	static {
		totalweight = Arrays.stream(clusterWeights).sum();
		if (TGFluids.OIL_WORLDSPAWN != null && TGFluids.OIL_WORLDSPAWN.defaultFluidState().createLegacyBlock() != null
				&& !TGFluids.OIL_WORLDSPAWN.defaultFluidState().createLegacyBlock().isAir()) {
			MBlock oil = new MBlock(TGFluids.OIL_WORLDSPAWN.defaultFluidState().createLegacyBlock());
			ores = new MBlock[] { oil };
			oilblock = oil;
			oilblockSand = new MultiMMBlock(new MBlock[] { oil, new MBlock("minecraft:sand", 0) }, new int[] { 1, 1 });
		} else {
			ores = new MBlock[] { new MBlock("minecraft:sandstone", 0) };
			oilblock = new MBlock("minecraft:lava", 0);
			oilblockSand = new MBlock("minecraft:magma", 0);
		}

		blockList.add(new MBlock("minecraft:sandstone", 0));
		blockList.add(new MBlock("techguns:sandbags", 0));
		blockList.add(new MBlock("minecraft:air", 0));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.SOLDIER_SPAWN, 3, 1, 200, 1).addMobType("armysoldier", 4).addMobType("commando", 1));
		blockList.add(oilblockSand);
		blockList.add(new MBlockOreclusterType(types, clusterWeights, 0.5f, ores));
		blockList.add(oilblock);
		blockList.add(new MBlockOreclusterType(types, clusterWeights, 0, null));
		blockList.add(oilblock);

		blocks = StructureBlockUtils.loadStructureFromFile("desert_oil_cluster");
	}

	public DesertOilCluster() {
		super(11, 10, 11, 11, 10, 11);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];

		int indexRoll = rollBlockIndex(rnd, totalweight, clusterWeights);

		StructureBlockUtils.cleanUpwards(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 6);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY - 4, posZ, centerX, centerZ, direction, 0, this.lootTier, colorType, indexRoll, rnd);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY - 4, posZ, centerX, centerZ, direction, 1, this.lootTier, colorType);
	}

}
