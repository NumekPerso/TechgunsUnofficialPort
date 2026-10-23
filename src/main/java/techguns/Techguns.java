package techguns;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import techguns.capabilities.CapabilityEventHandler;
import techguns.client.ClientProxy;
import techguns.items.guns.ammo.AmmoTypes;
import techguns.server.ServerProxy;

@Mod(Techguns.MODID)
public class Techguns {
	public static final String MODID = "techguns";
	public static final String NAME = "Techguns";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static Techguns instance;

	public static CommonProxy proxy;

	public Techguns() {
		instance = this;
		//unsafe variant: the safe one validates both suppliers, which loads (and verifies) ClientProxy on a dedicated server
		proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

		TGConfig.register();
		TGFluids.enableForgeFluids();

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener((ModConfigEvent.Loading e) -> TGConfig.onConfigLoad(e));
		modBus.addListener((ModConfigEvent.Reloading e) -> TGConfig.onConfigLoad(e));
		modBus.addListener(this::onRegister);
		modBus.addListener(CapabilityEventHandler::registerCapabilities);

		TGPackets.init();

		proxy.registerModBusListeners(modBus);
	}

	/**
	 * Everything is created and registered in the RegisterEvent, keeping the 1.12 "static field" style of the mod.
	 */
	private void onRegister(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.BLOCK)) {
			TGBlocks.createBlocks();
			TGMachines.createBlocks();
		}
		if (event.getRegistryKey().equals(Registries.ITEM)) {
			TGItems.createItems();
			AmmoTypes.init();
			TGuns.createItems();
			TGArmors.createItems();
			TGItems.createAdditionalSlotItems();
			TGBlocks.createItemBlocks();
			TGMachines.createItemBlocks();
		}
		TGBlocks.register(event);
		TGMachines.register(event);
		TGFluids.register(event);
		TGRecipes.register(event);
		TGRadiationSystem.register(event);
		techguns.world.TGWorldgen.register(event);
		TGItems.registerItems(event);
		TGSounds.registerSounds(event);
		TGEntities.registerEntities(event);

		event.register(Registries.CREATIVE_MODE_TAB, helper -> {
			helper.register(new ResourceLocation(MODID, "techguns"), CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.techguns"))
					.icon(() -> new ItemStack(TGItems.PISTOL_ROUNDS))
					.displayItems((params, output) -> {
						for (Item item : TGItems.ITEMLIST) {
							if (TGConfig.debug || !isDebugItem(item)) {
								output.accept(item.getDefaultInstance());
							}
						}
					}).build());
		});
	}

	/**
	 * Debug only items (1.12 showed them only with the debug config): dungeon scanner / generator, worldgen tools
	 */
	private static boolean isDebugItem(Item item) {
		if (item instanceof techguns.blocks.machines.MachineItemBlock mib && mib.getBlock() instanceof techguns.blocks.machines.BasicMachine<?> machine) {
			return machine.getMachineType().debugOnly();
		}
		return item instanceof techguns.items.WorldGenTestTool || item instanceof techguns.items.BuildingScanTool;
	}
}
