package techguns.blocks;

import java.util.function.Supplier;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 1.12 BlockTGStairs was a copy of the vanilla stairs with an additional "type2" texture variant.
 * The port uses the vanilla StairBlock (shapes, waterlogging, shape connections), flattened: the type2=true variant is
 * its own block (stairs_metal_2 / stairs_concrete_2).
 */
public class BlockTGStairs extends StairBlock implements IGenericBlock {

	public BlockTGStairs(Supplier<BlockState> baseState, Properties props) {
		super(baseState, props);
	}
}
