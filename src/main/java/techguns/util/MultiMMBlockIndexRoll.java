package techguns.util;

import java.util.Random;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import techguns.world.EnumLootType;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

public class MultiMMBlockIndexRoll extends MultiMMBlock {

	public MultiMMBlockIndexRoll(MBlock[] mblocks, int[] weights) {
		super(mblocks, weights);
	}

	public void setBlock(LevelAccessor w, MutableBlockPos pos, int rotation, EnumLootType loottype, BiomeColorType biome, int index, Random rnd) {
		this.mBlocks[Math.min(index, mBlocks.length - 1)].setBlock(w, pos, rotation, loottype, biome);
	}
}
