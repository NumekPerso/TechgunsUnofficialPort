package techguns.util;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import techguns.world.BlockRotator;
import techguns.world.EnumLootType;
import techguns.world.LegacyBlocks;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

/**
 * Block depending on the biome color type (WOODLAND, SNOW, DESERT, NETHER)
 */
public class MBlockBiomeColorType extends MBlock {

	protected BlockState[] states;

	public MBlockBiomeColorType(String[] blocks, int[] metas) {
		super(blocks[0], metas[0]);
		this.states = new BlockState[blocks.length];

		for (int i = 0; i < blocks.length; i++) {
			this.states[i] = LegacyBlocks.get(blocks[i], metas[i]);
		}
	}

	@Override
	public void setBlock(LevelAccessor w, MutableBlockPos pos, int rotation, EnumLootType loottype, BiomeColorType biome) {
		int index = biome.ordinal();
		if (index >= states.length) {
			index = 0;
		}

		BlockState targetState = BlockRotator.getRotatedHorizontal(states[index], rotation);
		placeState(w, pos, targetState, 3);
		if (this.hasTileEntity) {
			this.tileEntityPostPlacementAction(w, targetState, pos, rotation);
		}
	}

}
