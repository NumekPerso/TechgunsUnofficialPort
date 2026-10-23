package techguns.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import techguns.TGRadiationSystem;
import techguns.radiation.ItemRadiationData;
import techguns.radiation.ItemRadiationRegistry;
import techguns.util.TextUtil;

/**
 * Client parts of the radiation system (lot I): radiation entity renderer, tooltip of radioactive items (1.12 TGEventHandler).
 * The RAD HUD is drawn by TGRadiationHud (called by TGHud).
 */
public class TGRadiationClient {

	public static void registerModBusListeners(IEventBus modBus) {
		modBus.addListener((EntityRenderersEvent.RegisterRenderers e) -> e.registerEntityRenderer(TGRadiationSystem.RADIATION_ENTITY, NoopRenderer::new));
		MinecraftForge.EVENT_BUS.addListener(TGRadiationClient::itemRadiationTooltip);
	}

	public static void itemRadiationTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (!stack.isEmpty()) {
			ItemRadiationData data = ItemRadiationRegistry.getRadiationDataFor(stack);
			if (data != null && data.radamount > 0) {
				event.getToolTip().add(Component.literal(ChatFormatting.GREEN + TextUtil.trans("techguns.radiation") + " " + TextUtil.trans("potion.potency." + (data.radamount - 1))));
			}
		}
	}
}
