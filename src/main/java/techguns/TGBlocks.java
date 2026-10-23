package techguns;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import techguns.blocks.BlockBioblob;
import techguns.blocks.BlockMilitaryCrate;
import techguns.blocks.BlockOreCluster;
import techguns.blocks.BlockSandbags;
import techguns.blocks.BlockTGCamoNet;
import techguns.blocks.BlockTGCamoNetTop;
import techguns.blocks.BlockTGDoor2x1;
import techguns.blocks.BlockTGDoor3x3;
import techguns.blocks.BlockTGLadder;
import techguns.blocks.BlockTGLamp;
import techguns.blocks.BlockTGOre;
import techguns.blocks.BlockTGSlimyLadder;
import techguns.blocks.BlockTGStairs;
import techguns.blocks.EnumConcreteType;
import techguns.blocks.EnumDoorType;
import techguns.blocks.EnumLadderType;
import techguns.blocks.EnumLampType;
import techguns.blocks.EnumLightblockType;
import techguns.blocks.EnumMilitaryCrateType;
import techguns.blocks.EnumNetherMetalType;
import techguns.blocks.EnumOreClusterType;
import techguns.blocks.EnumOreType;
import techguns.blocks.GenericBlock;
import techguns.blocks.GenericBlockMetaEnumCamoChangeable;
import techguns.blocks.GenericBlockMetaEnumCamoChangeableLightlevel;
import techguns.blocks.GenericItemBlock;
import techguns.blocks.GenericItemBlockMeta;
import techguns.blocks.IGenericBlock;
import techguns.blocks.ItemTGDoor2x1;
import techguns.blocks.ItemTGDoor3x3;
import techguns.blocks.TGMetalPanelType;
import techguns.tileentities.BioBlobTileEnt;
import techguns.tileentities.Door3x3TileEntity;
import techguns.world.OreGenerator;

/**
 * Blocks of Techguns (1.12 TGBlocks), machine blocks are in TGMachines.
 * Metadata blocks of 1.12 are flattened into one block per variant when the variant is a material (ores, crates, ore
 * clusters...). The camo changeable decoration blocks (metal panels, concrete, nether metal, neon lights, camo nets,
 * ladders, lamps) keep their variant as the "type" (lamp: "lamp_type") block state property, their item stores it in
 * the BlockStateTag (see GenericBlockMetaEnum.getStackFor) and implements ICamoChangeable for the camo bench.
 * Skipped: BlockFluidAcid (fluids), BlockTGSpawner (NPCs), BlockDebugMarker.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TGBlocks {

	/**
	 * All blocks with their registry names, in creation order (= creative tab order)
	 */
	public static final Map<String, Block> BLOCKLIST = new LinkedHashMap<>();

	public static final Map<EnumOreType, BlockTGOre> TG_ORES = new EnumMap<>(EnumOreType.class);
	public static BlockTGOre ORE_COPPER;
	public static BlockTGOre ORE_TIN;
	public static BlockTGOre ORE_LEAD;
	public static BlockTGOre ORE_TITANIUM;
	public static BlockTGOre ORE_URANIUM;

	public static BlockBioblob BIOBLOB;

	public static BlockSandbags SANDBAGS;

	public static final EnumProperty<EnumLampType> LAMP_TYPE = EnumProperty.create("lamp_type", EnumLampType.class);
	public static BlockTGLamp<EnumLampType> LAMP_0;

	public static final EnumProperty<TGMetalPanelType> METAL_PANEL_TYPE = EnumProperty.create("type", TGMetalPanelType.class);
	public static GenericBlockMetaEnumCamoChangeable<TGMetalPanelType> METAL_PANEL;

	public static final EnumProperty<EnumNetherMetalType> NETHER_METAL_TYPE = EnumProperty.create("type", EnumNetherMetalType.class);
	public static GenericBlockMetaEnumCamoChangeableLightlevel<EnumNetherMetalType> NETHER_METAL;

	public static final EnumProperty<EnumLadderType> LADDER_TYPE = EnumProperty.create("type", EnumLadderType.class);
	public static BlockTGLadder<EnumLadderType> LADDER_0;

	public static final EnumProperty<EnumConcreteType> CONCRETE_TYPE = EnumProperty.create("type", EnumConcreteType.class);
	public static GenericBlockMetaEnumCamoChangeable<EnumConcreteType> CONCRETE;

	public static BlockTGDoor3x3 DOOR3x3;
	/**
	 * 1.12 TGItems.DOOR3x3 (one item with a meta per type), item_door3x3, item_door3x3_1, item_door3x3_2, item_door3x3_3
	 */
	public static final Map<EnumDoorType, ItemTGDoor3x3> DOOR3x3_ITEMS = new EnumMap<>(EnumDoorType.class);

	public static BlockTGCamoNet CAMONET;
	public static BlockTGCamoNetTop CAMONET_TOP;

	public static BlockTGDoor2x1 BUNKER_DOOR;
	/**
	 * 1.12 TGItems.BUNKER_DOOR (item_bunkerdoor)
	 */
	public static ItemTGDoor2x1 BUNKER_DOOR_ITEM;

	public static BlockTGStairs METAL_STAIRS;
	public static BlockTGStairs METAL_STAIRS_2;
	public static BlockTGStairs CONCRETE_STAIRS;
	public static BlockTGStairs CONCRETE_STAIRS_2;

	public static final EnumProperty<EnumLightblockType> NEONLIGHT_TYPE = EnumProperty.create("type", EnumLightblockType.class);
	public static GenericBlockMetaEnumCamoChangeable<EnumLightblockType> NEONLIGHT_BLOCK;

	public static final Map<EnumMilitaryCrateType, BlockMilitaryCrate> MILITARY_CRATES = new EnumMap<>(EnumMilitaryCrateType.class);

	/**
	 * 1.12 SAND_HARD (type bugnest_sand)
	 */
	public static GenericBlock SAND_HARD;

	/**
	 * 1.12 SLIMY_BLOCK (type bugnest_eggs)
	 */
	public static GenericBlock SLIMY_BLOCK;

	public static BlockTGSlimyLadder SLIMY_LADDER;

	public static final Map<EnumOreClusterType, BlockOreCluster<EnumOreClusterType>> ORE_CLUSTERS = new EnumMap<>(EnumOreClusterType.class);

	public static BlockEntityType<Door3x3TileEntity> DOOR3x3_TILEENT;
	public static BlockEntityType<BioBlobTileEnt> BIOBLOB_TILEENT;

	// lot K: NPC spawner of the structures (1.12 tg_spawner)
	public static techguns.blocks.BlockTGSpawner MONSTER_SPAWNER;
	public static BlockEntityType<techguns.tileentities.TGSpawnerTileEnt> MONSTER_SPAWNER_TILEENT;
	// end lot K

	protected static <T extends Block> T add(String name, T block) {
		BLOCKLIST.put(name, block);
		return block;
	}

	/**
	 * Creates the blocks, called from the block RegisterEvent
	 */
	public static void createBlocks() {
		for (EnumOreType t : EnumOreType.values()) {
			TG_ORES.put(t, add(t.getSerializedName(), new BlockTGOre(t)));
		}
		ORE_COPPER = TG_ORES.get(EnumOreType.ORE_COPPER);
		ORE_TIN = TG_ORES.get(EnumOreType.ORE_TIN);
		ORE_LEAD = TG_ORES.get(EnumOreType.ORE_LEAD);
		ORE_TITANIUM = TG_ORES.get(EnumOreType.ORE_TITANIUM);
		ORE_URANIUM = TG_ORES.get(EnumOreType.ORE_URANIUM);

		BIOBLOB = add("bioblob", new BlockBioblob());
		SANDBAGS = add("sandbags", new BlockSandbags());

		LAMP_0 = add("lamp0", new BlockTGLamp<EnumLampType>(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GLASS)
				.requiresCorrectToolForDrops().strength(4.0f).lightLevel(s -> 15).noOcclusion(), LAMP_TYPE));

		METAL_PANEL = add("metalpanel", new GenericBlockMetaEnumCamoChangeable<TGMetalPanelType>(metalProps(8.0f), METAL_PANEL_TYPE));
		NETHER_METAL = add("nethermetal", new GenericBlockMetaEnumCamoChangeableLightlevel<EnumNetherMetalType>(metalProps(8.0f), NETHER_METAL_TYPE));

		CONCRETE = add("concrete", new GenericBlockMetaEnumCamoChangeable<EnumConcreteType>(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
				.instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(8.0f), CONCRETE_TYPE));

		LADDER_0 = add("ladder0", new BlockTGLadder<EnumLadderType>(metalProps(6.0f).noOcclusion(), LADDER_TYPE));

		DOOR3x3 = add("door3x3", new BlockTGDoor3x3());
		CAMONET = add("camonet", new BlockTGCamoNet());
		CAMONET_TOP = add("camonet_top", new BlockTGCamoNetTop());
		BUNKER_DOOR = add("bunkerdoor", new BlockTGDoor2x1());

		METAL_STAIRS = add("stairs_metal", new BlockTGStairs(() -> METAL_PANEL.defaultBlockState(), metalProps(8.0f)));
		METAL_STAIRS_2 = add("stairs_metal_2", new BlockTGStairs(() -> METAL_PANEL.defaultBlockState(), metalProps(8.0f)));
		CONCRETE_STAIRS = add("stairs_concrete", new BlockTGStairs(() -> CONCRETE.defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
				.instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(6.0f)));
		CONCRETE_STAIRS_2 = add("stairs_concrete_2", new BlockTGStairs(() -> CONCRETE.defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
				.instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(6.0f)));

		NEONLIGHT_BLOCK = add("neonlights", new GenericBlockMetaEnumCamoChangeable<EnumLightblockType>(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW)
				.sound(SoundType.GLASS).strength(4.0f).lightLevel(s -> 15), NEONLIGHT_TYPE));

		for (EnumMilitaryCrateType t : EnumMilitaryCrateType.values()) {
			MILITARY_CRATES.put(t, add("military_crate_" + t.getSerializedName(), new BlockMilitaryCrate(t)));
		}

		SAND_HARD = add("bugnest_sand", new GenericBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.SNARE)
				.sound(SoundType.SAND).strength(3.0f)));
		SLIMY_BLOCK = add("bugnest_eggs", new GenericBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.SLIME_BLOCK).strength(4.0f)));
		SLIMY_LADDER = add("slimyladder", new BlockTGSlimyLadder());

		for (EnumOreClusterType t : EnumOreClusterType.values()) {
			ORE_CLUSTERS.put(t, add("orecluster_" + t.getSerializedName(), new BlockOreCluster<EnumOreClusterType>(t)));
		}
		// lot K
		MONSTER_SPAWNER = add("tg_spawner", new techguns.blocks.BlockTGSpawner());
		// end lot K
	}

	protected static BlockBehaviour.Properties metalProps(float hardness) {
		return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(hardness);
	}

	public static BlockMilitaryCrate getMilitaryCrate(EnumMilitaryCrateType type) {
		return MILITARY_CRATES.get(type);
	}

	public static BlockOreCluster<EnumOreClusterType> getOreCluster(EnumOreClusterType type) {
		return ORE_CLUSTERS.get(type);
	}

	/**
	 * Creates the block items (added to TGItems.ITEMLIST), called from the item RegisterEvent
	 */
	public static void createItemBlocks() {
		for (Map.Entry<String, Block> e : BLOCKLIST.entrySet()) {
			Block b = e.getValue();
			if (b instanceof IGenericBlock gb) {
				gb.createItemBlock(e.getKey());
			} else {
				new GenericItemBlock(b, e.getKey());
			}
		}

		DOOR3x3_ITEMS.put(EnumDoorType.METAL, new ItemTGDoor3x3("item_door3x3", DOOR3x3, EnumDoorType.METAL));
		DOOR3x3_ITEMS.put(EnumDoorType.HANGAR_UP, new ItemTGDoor3x3("item_door3x3_1", DOOR3x3, EnumDoorType.HANGAR_UP));
		DOOR3x3_ITEMS.put(EnumDoorType.HANGAR_DOWN, new ItemTGDoor3x3("item_door3x3_2", DOOR3x3, EnumDoorType.HANGAR_DOWN));
		DOOR3x3_ITEMS.put(EnumDoorType.NETHER, new ItemTGDoor3x3("item_door3x3_3", DOOR3x3, EnumDoorType.NETHER));

		BUNKER_DOOR_ITEM = new ItemTGDoor2x1("item_bunkerdoor", BUNKER_DOOR);
	}

	/**
	 * Registers the blocks (and any other registry entry owned by the blocks: block entities, features...)
	 */
	public static void register(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.BLOCK)) {
			event.register(Registries.BLOCK, helper -> {
				BLOCKLIST.forEach((name, block) -> helper.register(new ResourceLocation(Techguns.MODID, name), block));
			});
		} else if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
			DOOR3x3_TILEENT = BlockEntityType.Builder.of(Door3x3TileEntity::new, DOOR3x3).build(null);
			BIOBLOB_TILEENT = BlockEntityType.Builder.of(BioBlobTileEnt::new, BIOBLOB).build(null);
			event.register(Registries.BLOCK_ENTITY_TYPE, helper -> {
				helper.register(new ResourceLocation(Techguns.MODID, "door3x3tileent"), DOOR3x3_TILEENT);
				helper.register(new ResourceLocation(Techguns.MODID, "bioblob"), BIOBLOB_TILEENT);
				// lot K
				MONSTER_SPAWNER_TILEENT = BlockEntityType.Builder.of(techguns.tileentities.TGSpawnerTileEnt::new, MONSTER_SPAWNER).build(null);
				helper.register(new ResourceLocation(Techguns.MODID, "tgspawner"), MONSTER_SPAWNER_TILEENT);
				// end lot K
			});
		} else if (event.getRegistryKey().equals(Registries.PLACEMENT_MODIFIER_TYPE)) {
			event.register(Registries.PLACEMENT_MODIFIER_TYPE, helper -> {
				helper.register(new ResourceLocation(Techguns.MODID, "ore_config"), OreGenerator.CONFIG_FILTER);
			});
		}
	}

	/**
	 * Adds the other variants of the meta blocks (1.12 getSubBlocks) after their default stack in the Techguns tab
	 */
	@SubscribeEvent
	public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (!event.getTabKey().location().equals(new ResourceLocation(Techguns.MODID, "techguns"))) {
			return;
		}
		for (Item item : TGItems.ITEMLIST) {
			if (item instanceof GenericItemBlockMeta<?> meta) {
				ItemStack previous = item.getDefaultInstance();
				if (!event.getEntries().contains(previous)) {
					continue;
				}
				List<ItemStack> variants = meta.getVariantStacks();
				for (ItemStack stack : variants) {
					if (stack.hasTag()) {
						event.getEntries().putAfter(previous, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
						previous = stack;
					}
				}
			}
		}
	}
}
