package techguns.client;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import techguns.client.render.TGEntityRenderers;
import techguns.client.render.TGItemRenderers;

/**
 * Mod bus listeners of the client
 */
public class TGClientSetup {

	public static void registerModBusListeners(IEventBus modBus) {
		modBus.addListener(TGClientSetup::onClientSetup);
		modBus.addListener((RegisterKeyMappingsEvent e) -> TGKeybinds.register(e));
		modBus.addListener((RegisterGuiOverlaysEvent e) -> TGHud.registerOverlays(e));
		modBus.addListener((EntityRenderersEvent.RegisterRenderers e) -> TGEntityRenderers.registerRenderers(e));
		modBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions e) -> TGEntityRenderers.registerLayerDefinitions(e));
		techguns.client.particle.TGFXHandler.registerModBusListeners(modBus);
		// machine screens / block entity renderers (lot C)
		TGMachinesClient.registerModBusListeners(modBus);

		// additional slots, Techguns inventory tab (lot D)
		TGAdditionalSlotsClient.registerModBusListeners(modBus);

		// lot I (radiation)
		TGRadiationClient.registerModBusListeners(modBus);

		// lot J (NPC renderers)
		TGNpcClient.registerModBusListeners(modBus);

		// lot K (world)
		TGWorldgenClient.registerModBusListeners(modBus);

		// blocks: render layers, colors (lot B)
		TGBlocksClient.registerModBusListeners(modBus);

		// dev only: compare the pre-baked model rendering with the original path (-Dtechguns.verifyBake=true)
		if (Boolean.getBoolean(techguns.client.models.BakeVerifier.PROPERTY)) {
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.addListener(techguns.client.models.BakeVerifier::onClientTick);
		}

	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(TGItemRenderers::init);
	}
}
