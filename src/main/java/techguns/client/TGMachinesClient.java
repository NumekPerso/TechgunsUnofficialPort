package techguns.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import techguns.TGMachines;
import techguns.Techguns;
import techguns.client.models.machines.ModelAmmoPress;
import techguns.client.render.tileentities.RenderChargingStation;
import techguns.client.render.tileentities.RenderMachine;
import techguns.client.render.tileentities.TGMachineItemRenderer;
import techguns.gui.AmmoPressGui;
import techguns.gui.ChargingStationGui;

/**
 * Client registration of the machines: screens (1.12 TechgunsGuiHandler entries) and block entity renderers
 * (1.12 ClientRegistry.bindTileEntitySpecialRenderer), called from TGClientSetup.
 */
public class TGMachinesClient {

	public static final RenderMachine AMMO_PRESS_RENDERER = new RenderMachine(new ModelAmmoPress(), new ResourceLocation(Techguns.MODID, "textures/blocks/ammopress.png"));
	// ---- lot E (simple machines) ----
	public static final RenderMachine METAL_PRESS_RENDERER = new RenderMachine(new techguns.client.models.machines.ModelMetalPress(), new ResourceLocation(Techguns.MODID, "textures/blocks/metalpress.png"));
	// ---- end lot E ----
	// ---- lot F (fluids, chem lab, reaction chamber) ----
	public static final techguns.client.render.tileentities.RenderChemLab CHEM_LAB_RENDERER = new techguns.client.render.tileentities.RenderChemLab(new techguns.client.models.machines.ModelChemLab(), new ResourceLocation(Techguns.MODID, "textures/blocks/chemlab.png"));
	// ---- end lot F ----
	// ---- lot G (multiblocks, explosives, turret) ----
	public static final techguns.client.render.tileentities.RenderTurret TURRET_RENDERER = new techguns.client.render.tileentities.RenderTurret();
	// ---- end lot G ----


	public static void registerModBusListeners(IEventBus modBus) {
		modBus.addListener(TGMachinesClient::onClientSetup);
		modBus.addListener(TGMachinesClient::registerRenderers);
	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(TGMachines.AMMO_PRESS_CONTAINER, AmmoPressGui::new);
			MenuScreens.register(TGMachines.CHARGING_STATION_CONTAINER, ChargingStationGui::new);

			TGMachineItemRenderer.registerMachineItemRenderer(TGMachines.AMMO_PRESS, AMMO_PRESS_RENDERER);
			// ---- lot E (simple machines) ----
			MenuScreens.register(TGMachines.METAL_PRESS_CONTAINER, techguns.gui.MetalPressGui::new);
			TGMachineItemRenderer.registerMachineItemRenderer(TGMachines.METAL_PRESS, METAL_PRESS_RENDERER);
			MenuScreens.register(TGMachines.BLAST_FURNACE_CONTAINER, techguns.gui.BlastFurnaceGui::new);
			MenuScreens.register(TGMachines.GRINDER_CONTAINER, techguns.gui.GrinderGui::new);
			MenuScreens.register(TGMachines.CAMO_BENCH_CONTAINER, techguns.gui.CamoBenchGui::new);
			MenuScreens.register(TGMachines.REPAIR_BENCH_CONTAINER, techguns.gui.RepairBenchGui::new);
			MenuScreens.register(TGMachines.ARMOR_BENCH_CONTAINER, techguns.gui.UpgradeBenchGui::new);
			MenuScreens.register(TGMachines.DOOR3x3_CONTAINER, techguns.gui.Door3x3Gui::new);
			// ---- end lot E ----
			// ---- lot F (fluids, chem lab, reaction chamber) ----
			TGFluidsClient.onClientSetup();
			MenuScreens.register(TGMachines.CHEM_LAB_CONTAINER, techguns.gui.ChemLabGui::new);
			MenuScreens.register(TGMachines.REACTIONCHAMBER_CONTAINER, techguns.gui.ReactionChamberGui::new);
			TGMachineItemRenderer.registerMachineItemRenderer(TGMachines.CHEM_LAB, CHEM_LAB_RENDERER);
			// ---- end lot F ----
			// ---- lot G (multiblocks, explosives, turret) ----
			MenuScreens.register(TGMachines.FABRICATOR_CONTAINER, techguns.gui.FabricatorGui::new);
			MenuScreens.register(TGMachines.OREDRILL_CONTAINER, techguns.gui.OreDrillGui::new);
			MenuScreens.register(TGMachines.EXPLOSIVE_CHARGE_CONTAINER, techguns.gui.ExplosiveChargeGui::new);
			MenuScreens.register(TGMachines.TURRET_CONTAINER, techguns.gui.TurretGui::new);
			TGMachineItemRenderer.registerMachineItemRenderer(TGMachines.TURRET, TURRET_RENDERER.asItemRenderer());
			// ---- end lot G ----

		});
	}

	private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(TGMachines.AMMO_PRESS_TILE, ctx -> AMMO_PRESS_RENDERER);
		event.registerBlockEntityRenderer(TGMachines.CHARGING_STATION_TILE, RenderChargingStation::new);
		// ---- lot E (simple machines) ----
		event.registerBlockEntityRenderer(TGMachines.METAL_PRESS_TILE, ctx -> METAL_PRESS_RENDERER);
		event.registerBlockEntityRenderer(TGMachines.BLAST_FURNACE_TILE, techguns.client.render.tileentities.RenderBlastfurnace::new);
		event.registerBlockEntityRenderer(TGMachines.GRINDER_TILE, techguns.client.render.tileentities.RenderGrinder::new);
		// ---- end lot E ----
		// ---- lot F (fluids, chem lab, reaction chamber) ----
		event.registerBlockEntityRenderer(TGMachines.CHEM_LAB_TILE, ctx -> CHEM_LAB_RENDERER);
		event.registerBlockEntityRenderer(TGMachines.REACTIONCHAMBER_MASTER_TILE, techguns.client.render.tileentities.RenderReactionChamber::new);
		// ---- end lot F ----
		// ---- lot G (multiblocks, explosives, turret) ----
		event.registerBlockEntityRenderer(TGMachines.FABRICATOR_TILE_MASTER, techguns.client.render.tileentities.RenderFabricator::new);
		event.registerBlockEntityRenderer(TGMachines.OREDRILL_TILE_MASTER, techguns.client.render.tileentities.RenderOreDrill::new);
		event.registerBlockEntityRenderer(TGMachines.TURRET_TILE, ctx -> TURRET_RENDERER);
		event.registerEntityRenderer(techguns.entities.npcs.TGNpcEntities.TURRET, techguns.client.render.entities.npcs.RenderNPCTurret::new);
		// ---- end lot G ----

	}
}
