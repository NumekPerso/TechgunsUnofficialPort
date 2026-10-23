package techguns;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegisterEvent;
import techguns.blocks.machines.BasicMachine;
import techguns.blocks.machines.EnumMachineType;
import techguns.blocks.machines.EnumSimpleMachineType;
import techguns.blocks.machines.MachineItemBlock;
import techguns.blocks.machines.SimpleMachine;
import techguns.gui.containers.AmmoPressContainer;
import techguns.gui.containers.ChargingStationContainer;
import techguns.tileentities.AmmoPressTileEnt;
import techguns.tileentities.ChargingStationTileEnt;

/**
 * Machine blocks, block entity types and menu types (1.12 machine part of TGBlocks + tile entity / GUI registration).
 * One block per 1.12 machine type (registry name = lowercase machine type name), see PORTING_GUIDE.md "Adding a machine".
 * Screens and block entity renderers are registered on the client by client.TGMachinesClient.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TGMachines {

	public static final List<BasicMachine<?>> BLOCKLIST = new ArrayList<>();

	//blocks
	public static BasicMachine<EnumMachineType> AMMO_PRESS;
	public static SimpleMachine<EnumSimpleMachineType> CHARGING_STATION;
	// ---- lot E (simple machines) ----
	public static BasicMachine<EnumMachineType> METAL_PRESS;
	public static SimpleMachine<EnumSimpleMachineType> BLAST_FURNACE;
	public static SimpleMachine<techguns.blocks.machines.EnumSimpleMachineType2> GRINDER;
	public static SimpleMachine<EnumSimpleMachineType> CAMO_BENCH;
	public static SimpleMachine<EnumSimpleMachineType> REPAIR_BENCH;
	public static SimpleMachine<techguns.blocks.machines.EnumSimpleMachineType2> ARMOR_BENCH;
	// ---- end lot E ----
	// ---- lot F (fluids, chem lab, reaction chamber) ----
	public static BasicMachine<EnumMachineType> CHEM_LAB;
	public static techguns.blocks.machines.MultiBlockMachine<techguns.blocks.machines.EnumMultiBlockMachineType> REACTIONCHAMBER_HOUSING;
	public static techguns.blocks.machines.MultiBlockMachine<techguns.blocks.machines.EnumMultiBlockMachineType> REACTIONCHAMBER_GLASS;
	public static techguns.blocks.machines.MultiBlockMachine<techguns.blocks.machines.EnumMultiBlockMachineType> REACTIONCHAMBER_CONTROLLER;

	// ---- end lot F ----
	// ---- lot G (multiblocks, explosives, turret) ----
	public static techguns.blocks.machines.MultiBlockMachine<techguns.blocks.machines.EnumMultiBlockMachineType> FABRICATOR_HOUSING;
	public static techguns.blocks.machines.MultiBlockMachine<techguns.blocks.machines.EnumMultiBlockMachineType> FABRICATOR_GLASS;
	public static techguns.blocks.machines.MultiBlockMachine<techguns.blocks.machines.EnumMultiBlockMachineType> FABRICATOR_CONTROLLER;
	public static techguns.blocks.machines.BlockOreDrill OREDRILL_FRAME;
	public static techguns.blocks.machines.BlockOreDrill OREDRILL_SCAFFOLD;
	public static techguns.blocks.machines.BlockOreDrill OREDRILL_ROD;
	public static techguns.blocks.machines.BlockOreDrill OREDRILL_ENGINE;
	public static techguns.blocks.machines.BlockOreDrill OREDRILL_CONTROLLER;
	public static techguns.blocks.machines.BlockOreDrill OREDRILL_SCAFFOLD_HIDDEN;
	public static techguns.blocks.machines.BlockExplosiveCharge<techguns.blocks.machines.EnumExplosiveChargeType> EXPLOSIVE_CHARGE;
	public static techguns.blocks.machines.BlockExplosiveCharge<techguns.blocks.machines.EnumExplosiveChargeType> EXPLOSIVE_CHARGE_ADVANCED;
	public static BasicMachine<EnumMachineType> TURRET;
	// ---- end lot G ----
	// ---- lot K (dungeon scanner / generator) ----
	public static BasicMachine<EnumMachineType> DUNGEON_SCANNER;
	public static BasicMachine<EnumMachineType> DUNGEON_GENERATOR;
	// ---- end lot K ----


	//block entity types
	public static BlockEntityType<AmmoPressTileEnt> AMMO_PRESS_TILE;
	public static BlockEntityType<ChargingStationTileEnt> CHARGING_STATION_TILE;
	// ---- lot E (simple machines) ----
	public static BlockEntityType<techguns.tileentities.MetalPressTileEnt> METAL_PRESS_TILE;
	public static BlockEntityType<techguns.tileentities.BlastFurnaceTileEnt> BLAST_FURNACE_TILE;
	public static BlockEntityType<techguns.tileentities.GrinderTileEnt> GRINDER_TILE;
	public static BlockEntityType<techguns.tileentities.CamoBenchTileEnt> CAMO_BENCH_TILE;
	public static BlockEntityType<techguns.tileentities.RepairBenchTileEnt> REPAIR_BENCH_TILE;
	public static BlockEntityType<techguns.tileentities.UpgradeBenchTileEnt> ARMOR_BENCH_TILE;
	// ---- end lot E ----
	// ---- lot F (fluids, chem lab, reaction chamber) ----
	public static BlockEntityType<techguns.tileentities.ChemLabTileEnt> CHEM_LAB_TILE;
	public static BlockEntityType<techguns.tileentities.ReactionChamberTileEntMaster> REACTIONCHAMBER_MASTER_TILE;
	public static BlockEntityType<techguns.tileentities.ReactionChamberTileEntSlave> REACTIONCHAMBER_SLAVE_TILE;

	// ---- end lot F ----
	// ---- lot G (multiblocks, explosives, turret) ----
	public static BlockEntityType<techguns.tileentities.FabricatorTileEntMaster> FABRICATOR_TILE_MASTER;
	public static BlockEntityType<techguns.tileentities.FabricatorTileEntSlave> FABRICATOR_TILE_SLAVE;
	public static BlockEntityType<techguns.tileentities.OreDrillTileEntMaster> OREDRILL_TILE_MASTER;
	public static BlockEntityType<techguns.tileentities.OreDrillTileEntSlave> OREDRILL_TILE_SLAVE;
	public static BlockEntityType<techguns.tileentities.ExplosiveChargeTileEnt> EXPLOSIVE_CHARGE_TILE;
	public static BlockEntityType<techguns.tileentities.ExplosiveChargeAdvTileEnt> EXPLOSIVE_CHARGE_ADVANCED_TILE;
	public static BlockEntityType<techguns.tileentities.TurretTileEnt> TURRET_TILE;
	// ---- end lot G ----
	// ---- lot K ----
	public static BlockEntityType<techguns.tileentities.DungeonScannerTileEnt> DUNGEON_SCANNER_TILE;
	public static BlockEntityType<techguns.tileentities.DungeonGeneratorTileEnt> DUNGEON_GENERATOR_TILE;
	// ---- end lot K ----


	//menu types
	public static MenuType<AmmoPressContainer> AMMO_PRESS_CONTAINER;
	public static MenuType<ChargingStationContainer> CHARGING_STATION_CONTAINER;
	// ---- lot E (simple machines) ----
	public static MenuType<techguns.gui.containers.MetalPressContainer> METAL_PRESS_CONTAINER;
	public static MenuType<techguns.gui.containers.BlastFurnaceContainer> BLAST_FURNACE_CONTAINER;
	public static MenuType<techguns.gui.containers.GrinderContainer> GRINDER_CONTAINER;
	public static MenuType<techguns.gui.containers.CamoBenchContainer> CAMO_BENCH_CONTAINER;
	public static MenuType<techguns.gui.containers.RepairBenchContainer> REPAIR_BENCH_CONTAINER;
	public static MenuType<techguns.gui.containers.UpgradeBenchContainer> ARMOR_BENCH_CONTAINER;
	public static MenuType<techguns.gui.containers.Door3x3Container> DOOR3x3_CONTAINER;
	// ---- end lot E ----
	// ---- lot F (fluids, chem lab, reaction chamber) ----
	public static MenuType<techguns.gui.containers.ChemLabContainer> CHEM_LAB_CONTAINER;
	public static MenuType<techguns.gui.containers.ReactionChamberContainer> REACTIONCHAMBER_CONTAINER;

	// ---- end lot F ----
	// ---- lot G (multiblocks, explosives, turret) ----
	public static MenuType<techguns.gui.containers.FabricatorContainer> FABRICATOR_CONTAINER;
	public static MenuType<techguns.gui.containers.OreDrillContainer> OREDRILL_CONTAINER;
	public static MenuType<techguns.gui.containers.ExplosiveChargeContainer> EXPLOSIVE_CHARGE_CONTAINER;
	public static MenuType<techguns.gui.containers.TurretContainer> TURRET_CONTAINER;
	// ---- end lot G ----
	// ---- lot K ----
	public static MenuType<techguns.gui.containers.DungeonScannerContainer> DUNGEON_SCANNER_CONTAINER;
	public static MenuType<techguns.gui.containers.DungeonGeneratorContainer> DUNGEON_GENERATOR_CONTAINER;
	// ---- end lot K ----


	/**
	 * Creates the machine blocks, called from the block RegisterEvent
	 */
	public static void createBlocks() {
		AMMO_PRESS = addMachine(new BasicMachine<>(EnumMachineType.AMMO_PRESS, () -> AMMO_PRESS_TILE));
		CHARGING_STATION = addMachine(new SimpleMachine<>(EnumSimpleMachineType.CHARGING_STATION, () -> CHARGING_STATION_TILE));
		// ---- lot E (simple machines) ----
		METAL_PRESS = addMachine(new BasicMachine<>(EnumMachineType.METAL_PRESS, () -> METAL_PRESS_TILE));
		BLAST_FURNACE = addMachine(new SimpleMachine<>(EnumSimpleMachineType.BLAST_FURNACE, () -> BLAST_FURNACE_TILE));
		GRINDER = addMachine(new SimpleMachine<>(techguns.blocks.machines.EnumSimpleMachineType2.GRINDER, () -> GRINDER_TILE));
		CAMO_BENCH = addMachine(new SimpleMachine<>(EnumSimpleMachineType.CAMO_BENCH, () -> CAMO_BENCH_TILE));
		REPAIR_BENCH = addMachine(new SimpleMachine<>(EnumSimpleMachineType.REPAIR_BENCH, () -> REPAIR_BENCH_TILE));
		ARMOR_BENCH = addMachine(new SimpleMachine<>(techguns.blocks.machines.EnumSimpleMachineType2.ARMOR_BENCH, () -> ARMOR_BENCH_TILE));
		// ---- end lot E ----
		// ---- lot F (fluids, chem lab, reaction chamber) ----
		CHEM_LAB = addMachine(new BasicMachine<>(EnumMachineType.CHEM_LAB, () -> CHEM_LAB_TILE));
		REACTIONCHAMBER_HOUSING = addMachine(new techguns.blocks.machines.MultiBlockMachine<>(techguns.blocks.machines.EnumMultiBlockMachineType.REACTIONCHAMBER_HOUSING, () -> REACTIONCHAMBER_SLAVE_TILE));
		REACTIONCHAMBER_GLASS = addMachine(new techguns.blocks.machines.MultiBlockMachine<>(techguns.blocks.machines.EnumMultiBlockMachineType.REACTIONCHAMBER_GLASS, () -> REACTIONCHAMBER_SLAVE_TILE));
		REACTIONCHAMBER_CONTROLLER = addMachine(new techguns.blocks.machines.MultiBlockMachine<>(techguns.blocks.machines.EnumMultiBlockMachineType.REACTIONCHAMBER_CONTROLLER, () -> REACTIONCHAMBER_MASTER_TILE));
		techguns.blocks.machines.multiblocks.MultiBlockRegister.register(techguns.tileentities.ReactionChamberTileEntMaster.class, new techguns.blocks.machines.multiblocks.ReactionChamberDefinition());

		// ---- end lot F ----
		// ---- lot G (multiblocks, explosives, turret) ----
		FABRICATOR_HOUSING = addMachine(new techguns.blocks.machines.MultiBlockMachine<>(techguns.blocks.machines.EnumMultiBlockMachineType.FABRICATOR_HOUSING, () -> FABRICATOR_TILE_SLAVE));
		FABRICATOR_GLASS = addMachine(new techguns.blocks.machines.MultiBlockMachine<>(techguns.blocks.machines.EnumMultiBlockMachineType.FABRICATOR_GLASS, () -> FABRICATOR_TILE_SLAVE));
		FABRICATOR_CONTROLLER = addMachine(new techguns.blocks.machines.MultiBlockMachine<>(techguns.blocks.machines.EnumMultiBlockMachineType.FABRICATOR_CONTROLLER, () -> FABRICATOR_TILE_MASTER));
		techguns.blocks.machines.multiblocks.MultiBlockRegister.register(techguns.tileentities.FabricatorTileEntMaster.class, new techguns.blocks.machines.multiblocks.FabricatorDefinition());
		OREDRILL_FRAME = addMachine(new techguns.blocks.machines.BlockOreDrill(techguns.blocks.machines.EnumOreDrillType.FRAME, () -> OREDRILL_TILE_SLAVE));
		OREDRILL_SCAFFOLD = addMachine(new techguns.blocks.machines.BlockOreDrill(techguns.blocks.machines.EnumOreDrillType.SCAFFOLD, () -> OREDRILL_TILE_SLAVE));
		OREDRILL_ROD = addMachine(new techguns.blocks.machines.BlockOreDrill(techguns.blocks.machines.EnumOreDrillType.ROD, () -> OREDRILL_TILE_SLAVE));
		OREDRILL_ENGINE = addMachine(new techguns.blocks.machines.BlockOreDrill(techguns.blocks.machines.EnumOreDrillType.ENGINE, () -> OREDRILL_TILE_SLAVE));
		OREDRILL_CONTROLLER = addMachine(new techguns.blocks.machines.BlockOreDrill(techguns.blocks.machines.EnumOreDrillType.CONTROLLER, () -> OREDRILL_TILE_MASTER));
		OREDRILL_SCAFFOLD_HIDDEN = addMachine(new techguns.blocks.machines.BlockOreDrill(techguns.blocks.machines.EnumOreDrillType.SCAFFOLD_HIDDEN, () -> OREDRILL_TILE_SLAVE));
		techguns.blocks.machines.multiblocks.MultiBlockRegister.register(techguns.tileentities.OreDrillTileEntMaster.class, new techguns.blocks.machines.multiblocks.OreDrillDefinition());
		EXPLOSIVE_CHARGE = addMachine(new techguns.blocks.machines.BlockExplosiveCharge<>(techguns.blocks.machines.EnumExplosiveChargeType.TNT, () -> EXPLOSIVE_CHARGE_TILE));
		EXPLOSIVE_CHARGE_ADVANCED = addMachine(new techguns.blocks.machines.BlockExplosiveCharge<>(techguns.blocks.machines.EnumExplosiveChargeType.ADVANCED, () -> EXPLOSIVE_CHARGE_ADVANCED_TILE));
		TURRET = addMachine(new BasicMachine<>(EnumMachineType.TURRET, () -> TURRET_TILE));
		// ---- end lot G ----
		// ---- lot K ----
		DUNGEON_SCANNER = addMachine(new BasicMachine<>(EnumMachineType.DUNGEON_SCANNER, () -> DUNGEON_SCANNER_TILE));
		DUNGEON_GENERATOR = addMachine(new BasicMachine<>(EnumMachineType.DUNGEON_GENERATOR, () -> DUNGEON_GENERATOR_TILE));
		// ---- end lot K ----

	}

	/**
	 * Creates the machine block items (added to TGItems.ITEMLIST), called from the item RegisterEvent
	 */
	public static void createItemBlocks() {
		for (BasicMachine<?> machine : BLOCKLIST) {
			machine.createItemBlock();
		}
	}

	/**
	 * Registers machine blocks, block entity types and menu types
	 */
	public static void register(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.BLOCK)) {
			event.register(Registries.BLOCK, helper -> {
				for (BasicMachine<?> machine : BLOCKLIST) {
					helper.register(machine.getTGRegistryName(), machine);
				}
			});
		} else if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
			event.register(Registries.BLOCK_ENTITY_TYPE, helper -> {
				AMMO_PRESS_TILE = registerTile(helper, AMMO_PRESS, AmmoPressTileEnt::new);
				CHARGING_STATION_TILE = registerTile(helper, CHARGING_STATION, ChargingStationTileEnt::new);
				// ---- lot E (simple machines) ----
				METAL_PRESS_TILE = registerTile(helper, METAL_PRESS, techguns.tileentities.MetalPressTileEnt::new);
				BLAST_FURNACE_TILE = registerTile(helper, BLAST_FURNACE, techguns.tileentities.BlastFurnaceTileEnt::new);
				GRINDER_TILE = registerTile(helper, GRINDER, techguns.tileentities.GrinderTileEnt::new);
				CAMO_BENCH_TILE = registerTile(helper, CAMO_BENCH, techguns.tileentities.CamoBenchTileEnt::new);
				REPAIR_BENCH_TILE = registerTile(helper, REPAIR_BENCH, techguns.tileentities.RepairBenchTileEnt::new);
				ARMOR_BENCH_TILE = registerTile(helper, ARMOR_BENCH, techguns.tileentities.UpgradeBenchTileEnt::new);
				// ---- end lot E ----
				// ---- lot F (fluids, chem lab, reaction chamber) ----
				CHEM_LAB_TILE = registerTile(helper, CHEM_LAB, techguns.tileentities.ChemLabTileEnt::new);
				REACTIONCHAMBER_MASTER_TILE = registerTile(helper, REACTIONCHAMBER_CONTROLLER, techguns.tileentities.ReactionChamberTileEntMaster::new);
				//one block entity type for the housing and glass blocks (1.12 ReactionChamberTileEntSlave)
				REACTIONCHAMBER_SLAVE_TILE = BlockEntityType.Builder.of(techguns.tileentities.ReactionChamberTileEntSlave::new, REACTIONCHAMBER_HOUSING, REACTIONCHAMBER_GLASS).build(null);
				helper.register(new ResourceLocation(Techguns.MODID, "reactionchamber_slave"), REACTIONCHAMBER_SLAVE_TILE);

				// ---- end lot F ----
				// ---- lot G (multiblocks, explosives, turret) ----
				FABRICATOR_TILE_MASTER = registerTile(helper, FABRICATOR_CONTROLLER, techguns.tileentities.FabricatorTileEntMaster::new);
				FABRICATOR_TILE_SLAVE = BlockEntityType.Builder.of(techguns.tileentities.FabricatorTileEntSlave::new, FABRICATOR_HOUSING, FABRICATOR_GLASS).build(null);
				helper.register(new ResourceLocation(Techguns.MODID, "fabricator_slave"), FABRICATOR_TILE_SLAVE);
				OREDRILL_TILE_MASTER = registerTile(helper, OREDRILL_CONTROLLER, techguns.tileentities.OreDrillTileEntMaster::new);
				OREDRILL_TILE_SLAVE = BlockEntityType.Builder.of(techguns.tileentities.OreDrillTileEntSlave::new, OREDRILL_FRAME, OREDRILL_SCAFFOLD, OREDRILL_ROD, OREDRILL_ENGINE, OREDRILL_SCAFFOLD_HIDDEN).build(null);
				helper.register(new ResourceLocation(Techguns.MODID, "oredrill_slave"), OREDRILL_TILE_SLAVE);
				EXPLOSIVE_CHARGE_TILE = registerTile(helper, EXPLOSIVE_CHARGE, techguns.tileentities.ExplosiveChargeTileEnt::new);
				EXPLOSIVE_CHARGE_ADVANCED_TILE = registerTile(helper, EXPLOSIVE_CHARGE_ADVANCED, techguns.tileentities.ExplosiveChargeAdvTileEnt::new);
				TURRET_TILE = registerTile(helper, TURRET, techguns.tileentities.TurretTileEnt::new);
				// ---- end lot G ----
				// ---- lot K ----
				DUNGEON_SCANNER_TILE = registerTile(helper, DUNGEON_SCANNER, techguns.tileentities.DungeonScannerTileEnt::new);
				DUNGEON_GENERATOR_TILE = registerTile(helper, DUNGEON_GENERATOR, techguns.tileentities.DungeonGeneratorTileEnt::new);
				// ---- end lot K ----

			});
		} else if (event.getRegistryKey().equals(Registries.MENU)) {
			event.register(Registries.MENU, helper -> {
				AMMO_PRESS_CONTAINER = registerMenu(helper, "ammo_press", AmmoPressContainer::new);
				CHARGING_STATION_CONTAINER = registerMenu(helper, "charging_station", ChargingStationContainer::new);
				// ---- lot E (simple machines) ----
				METAL_PRESS_CONTAINER = registerMenu(helper, "metal_press", techguns.gui.containers.MetalPressContainer::new);
				BLAST_FURNACE_CONTAINER = registerMenu(helper, "blast_furnace", techguns.gui.containers.BlastFurnaceContainer::new);
				GRINDER_CONTAINER = registerMenu(helper, "grinder", techguns.gui.containers.GrinderContainer::new);
				CAMO_BENCH_CONTAINER = registerMenu(helper, "camo_bench", techguns.gui.containers.CamoBenchContainer::new);
				REPAIR_BENCH_CONTAINER = registerMenu(helper, "repair_bench", techguns.gui.containers.RepairBenchContainer::new);
				ARMOR_BENCH_CONTAINER = registerMenu(helper, "armor_bench", techguns.gui.containers.UpgradeBenchContainer::new);
				DOOR3x3_CONTAINER = registerMenu(helper, "door3x3", techguns.gui.containers.Door3x3Container::new);
				// ---- end lot E ----
				// ---- lot F (fluids, chem lab, reaction chamber) ----
				CHEM_LAB_CONTAINER = registerMenu(helper, "chem_lab", techguns.gui.containers.ChemLabContainer::new);
				REACTIONCHAMBER_CONTAINER = registerMenu(helper, "reactionchamber", techguns.gui.containers.ReactionChamberContainer::new);

				// ---- end lot F ----
				// ---- lot G (multiblocks, explosives, turret) ----
				FABRICATOR_CONTAINER = registerMenu(helper, "fabricator", techguns.gui.containers.FabricatorContainer::new);
				OREDRILL_CONTAINER = registerMenu(helper, "oredrill", techguns.gui.containers.OreDrillContainer::new);
				EXPLOSIVE_CHARGE_CONTAINER = registerMenu(helper, "explosive_charge", techguns.gui.containers.ExplosiveChargeContainer::new);
				TURRET_CONTAINER = registerMenu(helper, "turret", techguns.gui.containers.TurretContainer::new);
				// ---- end lot G ----
				// ---- lot K ----
				DUNGEON_SCANNER_CONTAINER = registerMenu(helper, "dungeon_scanner", techguns.gui.containers.DungeonScannerContainer::new);
				DUNGEON_GENERATOR_CONTAINER = registerMenu(helper, "dungeon_generator", techguns.gui.containers.DungeonGeneratorContainer::new);
				// ---- end lot K ----

			});
		}
	}

	/**
	 * Machine recipes need registered items: added in the common setup
	 */
	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(TGMachineRecipes::addRecipes);
	}

	protected static <T extends BasicMachine<?>> T addMachine(T machine) {
		BLOCKLIST.add(machine);
		return machine;
	}

	/**
	 * Registers the block entity type of a machine block, with the registry name of the block
	 */
	protected static <T extends BlockEntity> BlockEntityType<T> registerTile(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper, BasicMachine<?> block, BlockEntityType.BlockEntitySupplier<T> factory) {
		BlockEntityType<T> type = BlockEntityType.Builder.of(factory, block).build(null);
		helper.register(block.getTGRegistryName(), type);
		return type;
	}

	/**
	 * Registers a menu type, the client menu is created from the block position written by NetworkHooks.openScreen
	 */
	protected static <T extends AbstractContainerMenu> MenuType<T> registerMenu(RegisterEvent.RegisterHelper<MenuType<?>> helper, String name, IContainerFactory<T> factory) {
		MenuType<T> type = IForgeMenuType.create(factory);
		helper.register(new ResourceLocation(Techguns.MODID, name), type);
		return type;
	}
}
