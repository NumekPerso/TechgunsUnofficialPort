package techguns.world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;

import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.EntityBlockStateFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import techguns.TGBlocks;
import techguns.TGMachines;
import techguns.blocks.BlockTGDoor3x3;
import techguns.blocks.BlockTGLamp;
import techguns.blocks.BlockTGLadder;
import techguns.blocks.EnumConcreteType;
import techguns.blocks.EnumDoorState;
import techguns.blocks.EnumLadderType;
import techguns.blocks.EnumLampType;
import techguns.blocks.EnumLightblockType;
import techguns.blocks.EnumMilitaryCrateType;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.blocks.EnumNetherMetalType;
import techguns.blocks.EnumOreClusterType;
import techguns.blocks.EnumOreType;
import techguns.blocks.TGMetalPanelType;
import techguns.blocks.BlockTGCamoNet;
import techguns.blocks.EnumCamoNetType;

/**
 * 1.20: converts 1.12 block registry names + metadata (used by the structure palettes of the 1.12 world generation
 * and by the dungeon templates) to flattened 1.20 block states.
 * <p>
 * Vanilla blocks use the vanilla world upgrade tables (BlockStateData = 1.12 id/meta to 1.13 states, then the data
 * fixer up to the current version), Techguns blocks are mapped by hand from their 1.12 getStateFromMeta.
 */
public class LegacyBlocks {
	private static final Logger LOGGER = LogUtils.getLogger();

	/** data version of the 1.13 flattening (BlockStateData output) */
	private static final int FLATTENING_VERSION = 1466;

	private static final Map<String, BlockState> CACHE = new ConcurrentHashMap<>();

	public static BlockState get(String name, int meta) {
		if (!name.contains(":")) {
			name = "minecraft:" + name;
		}
		String key = name + "@" + meta;
		BlockState state = CACHE.get(key);
		if (state == null) {
			try {
				state = convert(name, meta);
			} catch (Exception e) {
				LOGGER.error("Techguns: could not convert legacy block {} meta {}", name, meta, e);
				state = null;
			}
			if (state == null) {
				LOGGER.warn("Techguns: unknown legacy block {} meta {}, using air", name, meta);
				state = Blocks.AIR.defaultBlockState();
			}
			CACHE.put(key, state);
		}
		return state;
	}

	protected static BlockState convert(String name, int meta) {
		if (name.startsWith("minecraft:")) {
			return convertVanilla(name, meta);
		} else if (name.startsWith("techguns:")) {
			return convertTechguns(name.substring(9), meta);
		} else if (name.equals("chisel:netherbrick")) {
			return Blocks.NETHER_BRICKS.defaultBlockState();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected static BlockState convertVanilla(String name, int meta) {
		int id = EntityBlockStateFix.getBlockId(name);
		if (id == 0 && !name.equals("minecraft:air")) {
			return null;
		}
		BlockState special = convertTileEntityBlock(name, meta);
		if (special != null) {
			return special;
		}
		Dynamic<?> tag = BlockStateData.getTag((id << 4) | (meta & 15));
		if (id != 0 && "minecraft:air".equals(tag.get("Name").asString(""))) {
			// meta not used by this block, use meta 0
			tag = BlockStateData.getTag(id << 4);
		}
		Dynamic<Tag> nbt = new Dynamic<>(NbtOps.INSTANCE, (Tag) tag.convert(NbtOps.INSTANCE).getValue());
		Dynamic<Tag> upgraded = DataFixers.getDataFixer().update(References.BLOCK_STATE, nbt, FLATTENING_VERSION,
				SharedConstants.getCurrentVersion().getDataVersion().getVersion());
		return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), (CompoundTag) upgraded.getValue());
	}

	/**
	 * Blocks the vanilla upgrade needs the tile entity for (BlockStateData "%%FILTER_ME%%"): 1.12 defaults
	 */
	protected static BlockState convertTileEntityBlock(String name, int meta) {
		switch (name) {
		case "minecraft:skull":
			if ((meta & 7) <= 1) {
				return Blocks.SKELETON_SKULL.defaultBlockState();
			}
			return Blocks.SKELETON_WALL_SKULL.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.from3DDataValue(meta & 7));
		case "minecraft:bed":
			return Blocks.RED_BED.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.from2DDataValue(meta & 3))
					.setValue(BlockStateProperties.BED_PART, (meta & 8) > 0 ? net.minecraft.world.level.block.state.properties.BedPart.HEAD : net.minecraft.world.level.block.state.properties.BedPart.FOOT);
		case "minecraft:flower_pot":
			return Blocks.FLOWER_POT.defaultBlockState();
		case "minecraft:standing_banner":
			return Blocks.WHITE_BANNER.defaultBlockState().setValue(BlockStateProperties.ROTATION_16, meta & 15);
		case "minecraft:wall_banner":
			return Blocks.WHITE_WALL_BANNER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,
					Direction.from3DDataValue(Math.max(2, meta & 7)));
		case "minecraft:noteblock":
			return Blocks.NOTE_BLOCK.defaultBlockState();
		default:
			return null;
		}
	}

	protected static <T extends Enum<T>> T byMeta(Class<T> clazz, int meta) {
		T[] values = clazz.getEnumConstants();
		return values[Math.max(0, Math.min(meta, values.length - 1))];
	}

	protected static BlockState convertTechguns(String name, int meta) {
		switch (name) {
		case "concrete":
			return TGBlocks.CONCRETE.defaultBlockState().setValue(TGBlocks.CONCRETE_TYPE, byMeta(EnumConcreteType.class, meta));
		case "metalpanel":
			return TGBlocks.METAL_PANEL.defaultBlockState().setValue(TGBlocks.METAL_PANEL_TYPE, byMeta(TGMetalPanelType.class, meta));
		case "nethermetal":
			return TGBlocks.NETHER_METAL.defaultBlockState().setValue(TGBlocks.NETHER_METAL_TYPE, byMeta(EnumNetherMetalType.class, meta));
		case "neonlights":
			return TGBlocks.NEONLIGHT_BLOCK.defaultBlockState().setValue(TGBlocks.NEONLIGHT_TYPE, byMeta(EnumLightblockType.class, meta));
		case "lamp0": {
			BlockState s = TGBlocks.LAMP_0.defaultBlockState();
			EnumLampType[] types = EnumLampType.values();
			if (meta < 6) {
				s = s.setValue(TGBlocks.LAMP_TYPE, types[0]).setValue(BlockTGLamp.FACING_ALL, Direction.from3DDataValue(meta));
			} else if (meta < 12) {
				s = s.setValue(TGBlocks.LAMP_TYPE, types[1]).setValue(BlockTGLamp.FACING_ALL, Direction.from3DDataValue(meta - 6));
			} else if (meta == 12) {
				s = s.setValue(TGBlocks.LAMP_TYPE, types[2]);
			} else if (meta == 13) {
				s = s.setValue(TGBlocks.LAMP_TYPE, types[3]);
			}
			return s;
		}
		case "ladder0":
			return TGBlocks.LADDER_0.defaultBlockState().setValue(BlockTGLadder.FACING, Direction.from2DDataValue(meta >> 2))
					.setValue(TGBlocks.LADDER_TYPE, byMeta(EnumLadderType.class, meta & 3));
		case "door3x3":
			return TGBlocks.DOOR3x3.defaultBlockState().setValue(BlockTGDoor3x3.STATE, EnumDoorState.values()[meta & 3])
					.setValue(BlockTGDoor3x3.ZPLANE, (meta & 4) > 0).setValue(BlockTGDoor3x3.MASTER, (meta & 8) > 0);
		case "stairs_metal":
		case "stairs_concrete": {
			Block b;
			if (name.equals("stairs_metal")) {
				b = (meta & 8) > 0 ? TGBlocks.METAL_STAIRS_2 : TGBlocks.METAL_STAIRS;
			} else {
				b = (meta & 8) > 0 ? TGBlocks.CONCRETE_STAIRS_2 : TGBlocks.CONCRETE_STAIRS;
			}
			return b.defaultBlockState().setValue(BlockStateProperties.HALF, (meta & 4) > 0 ? Half.TOP : Half.BOTTOM)
					.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.from2DDataValue(meta & 3));
		}
		case "military_crate":
			return TGBlocks.getMilitaryCrate(byMeta(EnumMilitaryCrateType.class, meta)).defaultBlockState();
		case "orecluster":
			return TGBlocks.getOreCluster(byMeta(EnumOreClusterType.class, meta)).defaultBlockState();
		case "basicore":
			return TGBlocks.TG_ORES.get(byMeta(EnumOreType.class, meta)).defaultBlockState();
		case "camonet":
			return TGBlocks.CAMONET.defaultBlockState().setValue(BlockTGCamoNet.CAMO_TYPE, byMeta(EnumCamoNetType.class, meta));
		case "camonet_top":
			return TGBlocks.CAMONET_TOP.defaultBlockState().setValue(BlockTGCamoNet.CAMO_TYPE, byMeta(EnumCamoNetType.class, meta));
		case "sandbags":
			return TGBlocks.SANDBAGS.defaultBlockState();
		case "slimy":
			return TGBlocks.SLIMY_BLOCK.defaultBlockState();
		case "sand_hard":
			return TGBlocks.SAND_HARD.defaultBlockState();
		case "slimyladder":
			return copyProperties(convertVanilla("minecraft:ladder", meta), TGBlocks.SLIMY_LADDER.defaultBlockState());
		case "bunkerdoor":
			return copyProperties(convertVanilla("minecraft:iron_door", meta), TGBlocks.BUNKER_DOOR.defaultBlockState());
		case "tg_spawner":
			return TGBlocks.MONSTER_SPAWNER.defaultBlockState().setValue(TGBlocks.MONSTER_SPAWNER.TYPE, byMeta(EnumMonsterSpawnerType.class, meta));
		case "simplemachine": {
			// 1.12 EnumSimpleMachineType: CAMO_BENCH, REPAIR_BENCH, CHARGING_STATION, BLAST_FURNACE
			Block[] machines = { TGMachines.CAMO_BENCH, TGMachines.REPAIR_BENCH, TGMachines.CHARGING_STATION, TGMachines.BLAST_FURNACE };
			BlockState s = machines[meta & 3].defaultBlockState();
			return setFacing(s, Direction.from2DDataValue(meta >> 2));
		}
		case "debugblock":
			// 1.12 BlockDebugMarker (template markers), not ported
			return Blocks.AIR.defaultBlockState();
		default:
			return null;
		}
	}

	/**
	 * Copies the values of all properties both states have
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BlockState copyProperties(BlockState from, BlockState to) {
		if (from == null) {
			return to;
		}
		for (Property p : from.getProperties()) {
			if (to.hasProperty(p)) {
				to = to.setValue(p, from.getValue(p));
			}
		}
		return to;
	}

	/**
	 * Sets a horizontal "facing" property, if the state has one
	 */
	public static BlockState setFacing(BlockState state, Direction dir) {
		for (Property<?> p : state.getProperties()) {
			if (p instanceof DirectionProperty dp && p.getName().equals("facing") && dp.getPossibleValues().contains(dir)) {
				return state.setValue(dp, dir);
			}
		}
		return state;
	}
}
