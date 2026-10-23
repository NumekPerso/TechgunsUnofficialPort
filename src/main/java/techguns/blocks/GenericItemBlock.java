package techguns.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import techguns.TGItems;
import techguns.Techguns;

public class GenericItemBlock extends BlockItem implements TGItems.ITGRegistryNamed {

	protected final String name;

	public GenericItemBlock(Block block, String name, Properties props) {
		super(block, props);
		this.name = name;
		TGItems.ITEMLIST.add(this);
	}

	public GenericItemBlock(Block block, String name) {
		this(block, name, new Properties());
	}

	@Override
	public ResourceLocation getTGRegistryName() {
		return new ResourceLocation(Techguns.MODID, name);
	}
}
