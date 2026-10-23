package techguns.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.RegisterEvent;
import techguns.Techguns;

/**
 * World generation of Techguns: structures, dungeons (1.12 world package, TGStructureSpawnRegister)
 * <p>
 * All structures use the structure type "techguns:tg_structure" (WorldGenTGStructureSpawn) with one piece
 * "techguns:tg_piece" (TGStructurePiece), placed by the structure sets with "techguns:tg_spread" (TGStructurePlacement).
 */
public class TGWorldgen {

	public static final StructureType<WorldGenTGStructureSpawn> TG_STRUCTURE_TYPE = () -> WorldGenTGStructureSpawn.CODEC;
	public static final StructurePieceType TG_STRUCTURE_PIECE = TGStructurePiece::new;
	public static final StructurePlacementType<TGStructurePlacement> TG_PLACEMENT_TYPE = () -> TGStructurePlacement.CODEC;

	/**
	 * Registers features, structure types, structure piece types, ... (check event.getRegistryKey())
	 */
	/**
	 * 1.12 debug tools (TGItems.WORLDGEN_TEST_TOOL / BUIDLING_SCAN_TOOL, only created with debug=true in 1.12)
	 */
	public static techguns.items.WorldGenTestTool WORLDGEN_TEST_TOOL;
	public static techguns.items.BuildingScanTool BUILDING_SCAN_TOOL;

	public static void register(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.ITEM)) {
			// created before TGItems.registerItems, which registers TGItems.ITEMLIST
			WORLDGEN_TEST_TOOL = new techguns.items.WorldGenTestTool("worldgentesttool");
			BUILDING_SCAN_TOOL = new techguns.items.BuildingScanTool("buildingscantool");
		} else if (event.getRegistryKey().equals(Registries.STRUCTURE_TYPE)) {
			event.register(Registries.STRUCTURE_TYPE, helper -> helper.register(new ResourceLocation(Techguns.MODID, "tg_structure"), TG_STRUCTURE_TYPE));
		} else if (event.getRegistryKey().equals(Registries.STRUCTURE_PIECE)) {
			event.register(Registries.STRUCTURE_PIECE, helper -> helper.register(new ResourceLocation(Techguns.MODID, "tg_piece"), TG_STRUCTURE_PIECE));
		} else if (event.getRegistryKey().equals(Registries.STRUCTURE_PLACEMENT)) {
			event.register(Registries.STRUCTURE_PLACEMENT, helper -> helper.register(new ResourceLocation(Techguns.MODID, "tg_spread"), TG_PLACEMENT_TYPE));
		}
	}
}
