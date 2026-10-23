package techguns.items;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import techguns.TGItems;
import techguns.Techguns;

/**
 * Base item of the port. The registry name is kept on the item so the 1.12 style "create everything, then register the list"
 * flow can be kept (see TGItems.ITEMLIST / TGRegistry).
 */
public class GenericItem extends Item {

	protected final String name;

	public GenericItem(String name, boolean addToItemList, Properties props) {
		super(props);
		this.name = name;
		if (addToItemList) {
			TGItems.ITEMLIST.add(this);
		}
	}

	public GenericItem(String name, boolean addToItemList) {
		this(name, addToItemList, new Properties());
	}

	public GenericItem(String name) {
		this(name, true);
	}

	public String getTGName() {
		return name;
	}

	public ResourceLocation getTGRegistryName() {
		return new ResourceLocation(Techguns.MODID, name);
	}

	/**
	 * Items with a builtin/entity model are rendered by TGItemRenderer, the extension is harmless for items with a normal model
	 */
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(techguns.client.TGItemClientExtensions.INSTANCE);
	}
}
