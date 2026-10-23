package techguns.util;

import java.util.Random;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import techguns.world.BlockRotator;
import techguns.world.EnumLootType;
import techguns.world.TGStructureLevel;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

/**
 * Random block out of a weighted list. 1.20: constructed from 1.12 registry names + metas (or states), the roll uses
 * a position based random (TGStructureLevel.posRandom) so it is the same in every chunk the structure is generated in.
 */
public class MultiMBlock extends MBlock {

	protected BlockState[] states;
	protected int[] weights;

	protected int totalWeight;

	public MultiMBlock(String[] blocks, int[] metas, int[] weights) {
		this(toStates(blocks, metas), weights);
	}

	public MultiMBlock(BlockState[] states, int[] weights) {
		super(states[0]);
		this.states = states;
		this.weights = weights;
		this.calcTotalweight();
	}

	protected static BlockState[] toStates(String[] blocks, int[] metas) {
		BlockState[] states = new BlockState[blocks.length];
		for (int i = 0; i < blocks.length; i++) {
			states[i] = techguns.world.LegacyBlocks.get(blocks[i], metas[i]);
		}
		return states;
	}

	private void calcTotalweight() {
		this.totalWeight = 0;
		for (int i = 0; i < this.weights.length; i++) {
			this.totalWeight += weights[i];
		}
	}

	@Override
	public void setBlock(LevelAccessor w, MutableBlockPos pos, int rotation, EnumLootType loottype, BiomeColorType biome) {
		int index = this.rollBlockIndex(TGStructureLevel.posRandom(w, pos));
		if (index >= states.length) {
			index = states.length - 1;
		}

		BlockState targetState = BlockRotator.getRotatedHorizontal(states[index], rotation);
		placeState(w, pos, targetState, 3);
		if (this.hasTileEntity) {
			this.tileEntityPostPlacementAction(w, targetState, pos, rotation);
		}
	}

	/**
	 * Roll index of the next block to use;
	 */
	protected int rollBlockIndex(Random rnd) {
		int roll = rnd.nextInt(this.totalWeight + 1);

		int sum = 0;
		for (int i = 0; i < this.states.length; i++) {
			sum += this.weights[i];
			if (roll <= sum) {
				return i;
			}
		}
		return weights.length;
	}
}
