package techguns.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.level.block.Block;
import techguns.TGItems;
import techguns.Techguns;

/**
 * Item of the bunker door (1.12 techguns.items.ItemTGDoor2x1). The 1.12 placement code was a copy of the vanilla door
 * item, the vanilla DoubleHighBlockItem + DoorBlock.getStateForPlacement do the same (hinge side, powered state).
 */
public class ItemTGDoor2x1 extends DoubleHighBlockItem implements TGItems.ITGRegistryNamed {

	protected final String name;

	public ItemTGDoor2x1(String name, Block block) {
		super(block, new Properties());
		this.name = name;
		TGItems.ITEMLIST.add(this);
	}

	@Override
	public ResourceLocation getTGRegistryName() {
		return new ResourceLocation(Techguns.MODID, name);
	}
}
