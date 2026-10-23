package techguns.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * May only be implemented by Blocks. Blocks are created and named in TGBlocks.createBlocks(), their items in
 * TGBlocks.createItemBlocks().
 */
public interface IGenericBlock {

	/**
	 * return the correct itemblock for initialization, null if the block has no item of its own (doors placed by another item...).
	 * The item adds itself to TGItems.ITEMLIST.
	 */
	public default BlockItem createItemBlock(String name) {
		return new GenericItemBlock((Block) this, name);
	}
}
