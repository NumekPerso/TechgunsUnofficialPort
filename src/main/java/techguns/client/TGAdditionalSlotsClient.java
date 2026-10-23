package techguns.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import techguns.TGPackets;
import techguns.client.render.layers.AdditionalSlotRenderRegistry;
import techguns.client.render.layers.TGLayerRenderer;
import techguns.gui.player.TGPlayerInventoryContainer;
import techguns.gui.player.TGPlayerInventoryGui;
import techguns.gui.player.tabs.TGPlayerTab;
import techguns.keybind.TGKeybindsID;
import techguns.packets.PacketOpenPlayerGUI;
import techguns.packets.PacketTGKeybindPress;

/**
 * Client setup of the additional slots and the Techguns player inventory (lot D): screen, inventory tabs, render layer of
 * the slot items, jetpack toggle keybind.
 */
public class TGAdditionalSlotsClient {

	public static final KeyMapping KEY_TOGGLE_JETPACK = new KeyMapping("techguns.key.toggleJetpack", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, TGKeybinds.CATEGORY);
	/**
	 * Not in 1.12 (the inventory was only reachable with the tabs), unbound by default. Needed in creative mode where the
	 * vanilla survival inventory (with the tabs) is replaced by the creative inventory.
	 */
	public static final KeyMapping KEY_OPEN_TG_INVENTORY = new KeyMapping("techguns.key.openTGInventory", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), TGKeybinds.CATEGORY);

	public static void registerModBusListeners(IEventBus modBus) {
		modBus.addListener(TGAdditionalSlotsClient::onClientSetup);
		modBus.addListener(TGAdditionalSlotsClient::onRegisterKeys);
		modBus.addListener(TGAdditionalSlotsClient::onAddLayers);

		MinecraftForge.EVENT_BUS.addListener(TGPlayerTab::onScreenInitPost);
		MinecraftForge.EVENT_BUS.addListener(TGPlayerTab::onScreenRenderPre);
		MinecraftForge.EVENT_BUS.addListener((ScreenEvent.Closing e) -> TGPlayerTab.onScreenClosing(e));
		MinecraftForge.EVENT_BUS.addListener(TGAdditionalSlotsClient::onClientTick);
	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(TGPlayerInventoryContainer.TYPE, TGPlayerInventoryGui::new);
			AdditionalSlotRenderRegistry.init();
		});
	}

	private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
		event.register(KEY_TOGGLE_JETPACK);
		event.register(KEY_OPEN_TG_INVENTORY);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void onAddLayers(EntityRenderersEvent.AddLayers event) {
		for (String skin : event.getSkins()) {
			LivingEntityRenderer renderer = event.getSkin(skin);
			if (renderer instanceof PlayerRenderer playerRenderer) {
				playerRenderer.addLayer(new TGLayerRenderer(playerRenderer));
			}
		}
	}

	private static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) {
			return;
		}
		while (KEY_TOGGLE_JETPACK.consumeClick()) {
			TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.TOGGLE_JETPACK, true));
		}
		while (KEY_OPEN_TG_INVENTORY.consumeClick()) {
			TGPackets.network.sendToServer(new PacketOpenPlayerGUI((short) 0));
		}
	}
}
