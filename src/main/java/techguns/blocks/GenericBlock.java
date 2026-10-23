package techguns.blocks;

import net.minecraft.world.level.block.Block;

/**
 * Simple Techguns block. 1.12 material/hardness/harvest tool are block properties and tags (mineable/needs_x_tool) now.
 */
public class GenericBlock extends Block implements IGenericBlock {

	public GenericBlock(Properties props) {
		super(props);
	}
}
