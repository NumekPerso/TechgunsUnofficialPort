package techguns.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import techguns.TGBlocks;
import techguns.TGItems;
import techguns.Techguns;
import techguns.blocks.GenericItemBlockMeta;
import techguns.client.render.tileentities.RenderDoor3x3Fast;

/**
 * Client setup of the blocks (lot B). Render layers are set in the block models ("render_type"), no block colors.
 * - item property "techguns:variant" (index of the block state type stored in the stack) used by the item model
 *   overrides of the camo changeable blocks
 * - 3x3 door renderer (moving door parts while opening / closing) and its additional models
 */
public class TGBlocksClient {

	public static final ResourceLocation VARIANT_PROPERTY = new ResourceLocation(Techguns.MODID, "variant");

	public static void registerModBusListeners(IEventBus modBus) {
		modBus.addListener(TGBlocksClient::onClientSetup);
		modBus.addListener((EntityRenderersEvent.RegisterRenderers e) -> e.registerBlockEntityRenderer(TGBlocks.DOOR3x3_TILEENT, RenderDoor3x3Fast::new));
		modBus.addListener((ModelEvent.RegisterAdditional e) -> RenderDoor3x3Fast.registerModels(e));
	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			for (Item item : TGItems.ITEMLIST) {
				if (item instanceof GenericItemBlockMeta<?> meta) {
					ItemProperties.register(item, VARIANT_PROPERTY, (stack, level, entity, seed) -> meta.getMetaBlock().getTypeFromStack(stack).ordinal());
				}
			}
		});
	}
}
