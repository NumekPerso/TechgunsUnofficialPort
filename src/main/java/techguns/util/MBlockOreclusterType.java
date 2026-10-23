package techguns.util;

import java.util.Random;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import techguns.TGBlocks;
import techguns.blocks.EnumOreClusterType;
import techguns.world.EnumLootType;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

/**
 * Ore cluster block of the rolled type (1.20: one block per ore cluster type, TGBlocks.ORE_CLUSTERS)
 */
public class MBlockOreclusterType extends MultiMMBlockIndexRoll {

	float oreChance = 0;

	MBlock[] oreMBlocks;

	public MBlockOreclusterType(EnumOreClusterType[] types, int[] weights, float oreChance, MBlock[] ores) {
		super(fillMBlocks(types), weights);
		this.oreChance = oreChance;
		this.oreMBlocks = ores;
	}

	protected static MBlock[] fillMBlocks(EnumOreClusterType[] types) {
		MBlock[] mBlocks = new MBlock[types.length];
		int i = -1;
		for (EnumOreClusterType o : types) {
			mBlocks[++i] = new MBlock(TGBlocks.getOreCluster(o).defaultBlockState());
		}
		return mBlocks;
	}

	@Override
	public void setBlock(LevelAccessor w, MutableBlockPos pos, int rotation, EnumLootType loottype, BiomeColorType biome, int index, Random rnd) {
		index = Math.min(index, mBlocks.length - 1);
		if (oreChance > 0 && oreMBlocks != null && rnd.nextFloat() < oreChance) {
			this.oreMBlocks[index].setBlock(w, pos, rotation, loottype, biome);
		} else {
			this.mBlocks[index].setBlock(w, pos, rotation, loottype, biome);
		}
	}

}
