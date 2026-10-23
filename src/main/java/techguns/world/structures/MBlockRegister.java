package techguns.world.structures;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import techguns.TGBlocks;
import techguns.blocks.EnumConcreteType;
import techguns.blocks.EnumMilitaryCrateType;
import techguns.blocks.TGMetalPanelType;
import techguns.util.MBlock;
import techguns.util.MBlockCamoNetTop;
import techguns.util.MultiMBlock;

/**
 * Shared structure blocks (1.12 MBlockRegister). 1.12 Block+meta are given as 1.12 registry names (LegacyBlocks).
 */
public class MBlockRegister {
	public static MBlock[] crates = new MBlock[EnumMilitaryCrateType.values().length];
	static {
		for (EnumMilitaryCrateType t : EnumMilitaryCrateType.values()) {
			crates[t.ordinal()] = new MBlock(TGBlocks.getMilitaryCrate(t).defaultBlockState());
		}
	}

	protected static BlockState[] crateStates(boolean withAir) {
		EnumMilitaryCrateType[] types = EnumMilitaryCrateType.values();
		BlockState[] s = new BlockState[types.length + (withAir ? 1 : 0)];
		for (int i = 0; i < types.length; i++) {
			s[i] = TGBlocks.getMilitaryCrate(types[i]).defaultBlockState();
		}
		if (withAir) {
			s[types.length] = Blocks.AIR.defaultBlockState();
		}
		return s;
	}

	public static MultiMBlock SUPPLY_CRATES = new MultiMBlock(crateStates(false), new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 });

	public static MultiMBlock SUPPLY_CRATES_CHANCE = new MultiMBlock(crateStates(true), new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 9 });

	public static MBlock AIR = new MBlock(Blocks.AIR.defaultBlockState());

	public static MBlock WOOD_STAIRS_OAK = new MBlock("minecraft:oak_stairs", 0);
	public static MBlock TGLAMP = new MBlock("techguns:lamp0", 0);
	public static MBlock GRAVEL = new MBlock("minecraft:gravel", 0);
	public static MBlock GLASS_PANE = new MBlock("minecraft:glass_pane", 0);

	//Blocks that get changed by chisel
	public static MBlock BARRACKS_WOOD_PILLAR = new MBlock("minecraft:log", 0);
	public static MBlock BARRACKS_WOOD_WALL = new MBlock("minecraft:planks", 0);
	public static MBlock BARRACKS_WOOD_FLOOR = new MBlock("minecraft:planks", 0);
	public static MBlock BARRACKS_WOOD_SCAFFOLD = new MBlock("minecraft:planks", 5);
	public static MBlock BARRACKS_WOOD_ROOF = new MBlock("minecraft:planks", 1);
	public static MBlock BARRACKS_WOOD_ROOFSLAB = new MBlock("minecraft:wooden_slab", 1);

	//chisel blocks with fallbacks (1.12 EnumDyeColor ordinals: GRAY=7, SILVER=8, BLUE=11, YELLOW=4)
	public static MBlock FACTORY_PLATE_DOTTED = new MBlock("minecraft:hardened_clay", 0);
	public static MBlock FACTORY_PLATE = new MBlock("minecraft:brick_block", 0);
	public static MBlock FACTORY_CRATE = new MBlock(TGBlocks.METAL_PANEL.getStateFor(TGMetalPanelType.CONTAINER_RED));
	public static MBlock FACTORY_PLATE_DOTTED_GREY = new MBlock("minecraft:concrete", 8);
	public static MBlock FACTORY_PLATE_VERY_RUSTY = new MBlock("minecraft:concrete", 7);
	public static MBlock FACTORY_PLATE_WORN_COLUMN = new MBlock("minecraft:hardened_clay", 0);
	public static MBlock FACTORY_WIREFRAME = new MBlock("techguns:concrete", 3);
	public static MBlock FACTORY_SHINY_METAL = new MBlock("techguns:metalpanel", TGMetalPanelType.PANEL_LARGE_BORDER.ordinal());

	public static MBlock TECHNICAL_CONCRETE = new MBlock("techguns:concrete", 1);
	public static MBlock TECHNICAL_BLOCK_SCAFFOLD = new MBlock(TGBlocks.METAL_PANEL.getStateFor(TGMetalPanelType.STEELFRAME_SCAFFOLD));
	public static MBlock TECHNICAL_PIPES = new MBlock("techguns:concrete", EnumConcreteType.CONCRETE_BROWN_PIPES.ordinal());
	public static MBlock TECHNICAL_GRATE = new MBlock("techguns:metalpanel", TGMetalPanelType.STEELFRAME_DARK.ordinal());

	public static MBlock TECHNICAL2_SCAFFOLD_TRANSPARENT = new MBlock("techguns:metalpanel", TGMetalPanelType.STEELFRAME_SCAFFOLD.ordinal());
	public static MBlock TECHNICAL2_FAN = new MBlock("techguns:metalpanel", TGMetalPanelType.STEELFRAME_DARK.ordinal());

	public static MBlock IRON_BLOCK_VENTS = new MBlock("minecraft:iron_block", 0);
	public static MBlock IRON_BLOCK_SMALL_INGOTS = new MBlock("minecraft:iron_block", 0);

	public static MBlock ALUMINIUM_STAIRS_WEST = new MBlock(TGBlocks.METAL_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
	public static MBlock ALUMINIUM_STAIRS_EAST = new MBlock(TGBlocks.METAL_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));

	//nether chisel blocks
	public static MBlock NETHERRACK_ROCKY = new MBlock("minecraft:netherrack", 0);
	public static MBlock NETHERRACK_C1 = new MBlock("minecraft:netherrack", 0);
	public static MBlock NETHERRACK_C2 = new MBlock("minecraft:netherrack", 0);
	public static MBlock NETHERRACK_C12 = new MBlock("minecraft:netherrack", 0);
	public static MBlock NETHERRACK_C8 = new MBlock("minecraft:netherrack", 0);

	public static MBlock NETHERBRICKS_C13 = new MBlock("minecraft:nether_brick", 0);

	//Military Base
	public static MBlock MILBASE_FENCE = new MBlock("minecraft:iron_bars", 0);
	public static MBlock MILBASE_ROADBLOCK = new MBlock("minecraft:gravel", 0);
	public static MBlock MILBASE_ROADBLOCK_SECONDARY = new MBlock("techguns:concrete", 2);

	//Tanks
	public static MBlock TANKS_BASE = new MBlock("techguns:metalpanel", 7);
	public static MBlock TANKS_WALL = new MBlock("techguns:metalpanel", 0);
	public static MBlock TANKS_BORDER = new MBlock("techguns:metalpanel", 3);

	//Helipad
	public static MBlock HELIPAD_GLOWBLOCK = new MBlock("techguns:neonlights", 4);
	public static MBlock HELIPAD_WIREFRAME = new MBlock("techguns:metalpanel", 6);
	public static MBlock HELIPAD_CONCRETE = new MBlock("techguns:concrete", 3);
	public static MBlock HELIPAD_HAZARDBLOCK = new MBlock("minecraft:concrete", 4);

	//cobblestone
	public static MultiMBlock COBBLESTONE_MOSSY_RND = new MultiMBlock(new String[] { "minecraft:cobblestone", "minecraft:mossy_cobblestone" }, new int[] { 0, 0 }, new int[] { 2, 1 });
	public static MBlock COBBLESTONE_FLOOR = new MBlock(Blocks.STONE.defaultBlockState());

	//limestone
	public static MBlock STONEBRICK_STAIRS_NORTH_INV = new MBlock(Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP));
	public static MBlock STONEBRICK_STAIRS_SOUTH_INV = new MBlock(Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP));

	public static MBlock STONEBRICK_STAIRS_EAST_CRACKED_RND = new MultiMBlock(new BlockState[] { Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST), Blocks.AIR.defaultBlockState() }, new int[] { 4, 1 });
	public static MBlock STONEBRICK_STAIRS_WEST_CRACKED_RND = new MultiMBlock(new BlockState[] { Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST), Blocks.AIR.defaultBlockState() }, new int[] { 4, 1 });

	public static MBlock STONEBRICK_STAIRS_EAST_CRACKED_INV_RND = new MultiMBlock(new BlockState[] { Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP), Blocks.AIR.defaultBlockState() }, new int[] { 4, 1 });
	public static MBlock STONEBRICK_STAIRS_WEST_CRACKED_INV_RND = new MultiMBlock(new BlockState[] { Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP), Blocks.AIR.defaultBlockState() }, new int[] { 4, 1 });

	public static MBlock STONEBRICK_CRACKED_RND = new MultiMBlock(new String[] { "minecraft:stonebrick", "minecraft:stonebrick", "minecraft:stonebrick", "minecraft:air" }, new int[] { 0, 1, 2, 0 }, new int[] { 3, 3, 3, 2 });

	//bricks
	public static MBlock BRICKS_CRACKED_RND = new MBlock("minecraft:brick_block", 0);

	//stone brick panel
	public static MBlock STONE_BRICKS_PANEL = new MBlock("minecraft:stone", 0);
	public static MBlock STONE_BRICKS_SMALL = new MBlock("minecraft:stonebrick", 0);

	public static MBlock STONE_BRICKS_SUNKEN = new MBlock("minecraft:stonebrick", 0);

	public static MBlock COBBLESTONE_1 = new MBlock("minecraft:cobblestone", 0);
	public static MBlock COBBLESTONE_2 = new MBlock("minecraft:cobblestone", 0);
	public static MBlock COBBLESTONE_3 = new MBlock("minecraft:cobblestone", 0);

	public static MBlock COBBLESTONE_13 = new MBlock("minecraft:stonebrick", 0);
	public static MBlock COBBLESTONE_15 = new MBlock("minecraft:stonebrick", 0);

	public static MBlock COBBLESTONE_7 = new MBlock("minecraft:stone", 6);

	public static MBlock ANDESITE_3 = new MBlock("minecraft:stone", 6);
	public static MBlock ANDESITE_4 = new MBlock("minecraft:stone", 6);
	public static MBlock ANDESITE_11 = new MBlock("minecraft:stone", 6);

	public static MBlock ANDESITE1_0 = new MBlock("minecraft:stone", 6);

	public static MBlock BRICKS_WEATHERED = new MBlock("minecraft:brick_block", 0);
	public static MBlock BRICKS_WIDE = new MBlock("minecraft:brick_block", 0);

	public static MBlock LABORATORY_WALL = new MBlock("minecraft:quartz_block", 0);
	public static MBlock LABORATORY_FLOOR = new MBlock("minecraft:stone", 6);
	public static MBlock LABORATORY_BORDER = new MBlock("minecraft:quartz_block", 2);
	public static MBlock LABORATORY_DECORATION = new MBlock("minecraft:quartz_block", 1);

	public static MBlock LIME_WALL = new MBlock("minecraft:sandstone", 2);
	public static MBlock LIME_FLOOR = new MBlock("minecraft:sandstone", 0);
	public static MBlock LIME_BORDER = new MBlock("minecraft:sandstone", 1);
	public static MBlock LIME_DECORATION = new MBlock("minecraft:sandstone", 1);

	public static MBlock IRON_PANE_MODERN_FENCE = new MBlock("minecraft:iron_bars", 0);

	public static MBlock OAK_PLANKS_1 = new MBlock("minecraft:planks", 0);

	public static MBlock BLUE_CONCRETE_SMALL_BRICKS = new MBlock("minecraft:concrete", 11);

	public static MBlock GAS_STATION_CONSOLE = new MBlock("minecraft:quartz_stairs", 1);

	public static MBlock SURIVIVOR_HIDEOUT_IRON_BOTTOM = new MBlock("minecraft:iron_block", 0);
	public static MBlock SURIVIVOR_HIDEOUT_IRON_TOP = new MBlock("minecraft:iron_block", 0);

	public static MBlockCamoNetTop CAMO_NET_TOP = new MBlockCamoNetTop();
}
