package techguns.world;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import techguns.blocks.BlockTGLamp;
import techguns.util.MBlock;

/**
 * Placement layer of blocks: blocks that need a support (torches, doors, ladders...) are placed in a second pass.
 * 1.20: block classes/tags instead of the 1.12 block list.
 */
public class BlockData {

	private final static int DEFAULT_LAYER = 1;

	public final static int MIN_LAYER = 0; //Expand when needed
	public final static int MAX_LAYER = 5;

	public static int getBlockLayer(MBlock mb) {
		return getBlockLayer(mb.getState());
	}

	public static int getBlockLayer(BlockState state) {
		if (state.getBlock() instanceof TorchBlock || state.getBlock() instanceof RedstoneTorchBlock || state.is(BlockTags.TRAPDOORS)
				|| state.is(BlockTags.DOORS) || state.getBlock() instanceof LadderBlock || state.getBlock() instanceof BaseRailBlock
				|| state.getBlock() instanceof RedStoneWireBlock || state.getBlock() instanceof LiquidBlock || state.getBlock() instanceof LeverBlock
				|| state.is(BlockTags.BUTTONS) || state.is(BlockTags.ALL_SIGNS) || state.is(BlockTags.BANNERS) || state.is(BlockTags.ANVIL)
				|| state.getBlock() instanceof BlockTGLamp || state.is(Blocks.LADDER)) {
			return 2;
		}
		return DEFAULT_LAYER;
	}
}
