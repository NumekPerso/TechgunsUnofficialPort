package techguns.blocks.machines;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import techguns.TGItems;

/**
 * Item of a machine block (1.12 GenericItemBlockMetaMachineBlock): passes the clicked side to
 * BasicMachine.onBlockPlacedByExtended. Machines rendered by a block entity renderer get the machine item renderer.
 */
public class MachineItemBlock extends BlockItem implements TGItems.ITGRegistryNamed {

	protected final BasicMachine<?> machine;

	public MachineItemBlock(BasicMachine<?> block) {
		this(block, new Item.Properties());
	}

	public MachineItemBlock(BasicMachine<?> block, Item.Properties props) {
		super(block, props);
		this.machine = block;
		TGItems.ITEMLIST.add(this);
	}

	@Override
	public ResourceLocation getTGRegistryName() {
		return machine.getTGRegistryName();
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		BasicMachine.setPlacingSide(context.getClickedFace());
		try {
			return super.place(context);
		} finally {
			BasicMachine.setPlacingSide(null);
		}
	}

	/**
	 * Called by the Item constructor (fields are not set yet). The custom renderer is only used by items with a
	 * builtin/entity model (machines rendered by a block entity renderer, e.g. ammo_press).
	 */
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(techguns.client.render.tileentities.TGMachineItemClientExtensions.INSTANCE);
	}
}
