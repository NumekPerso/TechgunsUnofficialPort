package techguns.world.dungeon.presets.specialblocks;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import techguns.util.MBlock;
import techguns.world.TGStructureLevel;

/**
 * 1.12: random meta of the block. 1.20: random state out of a list (e.g. military crates are separate blocks)
 */
public class RandomStateMBlock extends MBlock {

	protected List<BlockState> states;

	public RandomStateMBlock(MBlock other, List<BlockState> states) {
		super(other);
		this.states = states;
		this.hasTileEntity = true;
	}

	@Override
	public void tileEntityPostPlacementAction(LevelAccessor w, BlockState state, BlockPos p, int rotation) {
		int s = TGStructureLevel.posRandom(w, p).nextInt(states.size());
		w.setBlock(p, states.get(s), 2);
	}

}
