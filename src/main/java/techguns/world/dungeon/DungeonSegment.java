package techguns.world.dungeon;

import net.minecraft.world.level.LevelAccessor;
import techguns.tileentities.DungeonScannerTileEnt;
import techguns.world.Structure;
import techguns.world.dungeon.TemplateSegment.SegmentType;

/**
 * An 'instance' of a TemplateSegment for a specific DungeonTemplate
 */
public class DungeonSegment {

	public DungeonTemplate template;
	public SegmentType type;
	public Structure structure;

	public DungeonSegment(DungeonTemplate template, SegmentType type) {
		this.template = template;
		this.type = type;
	}

	/**
	 * x, y, z = World position of Scanner TileEnt
	 */
	public void scanBlocks(LevelAccessor world, int posX, int posY, int posZ) {
		TemplateSegment segment = TemplateSegment.templateSegments.get(type);

		int x = posX + DungeonScannerTileEnt.SPACING + segment.col * (template.sizeXZ + DungeonScannerTileEnt.SPACING);
		int y = posY;
		int z = posZ + DungeonScannerTileEnt.SPACING + segment.row * (template.sizeXZ + DungeonScannerTileEnt.SPACING);

		this.structure = Structure.scanBlocks(world, x, y, z, template.sizeXZ, template.sizeY * segment.sizeY, template.sizeXZ);
	}

	//Place Segment in template position
	public void placeTemplateSegment(LevelAccessor world, int posX, int posY, int posZ, int rotation) {
		TemplateSegment segment = TemplateSegment.templateSegments.get(type);

		int x = posX + DungeonScannerTileEnt.SPACING + segment.col * (template.sizeXZ + DungeonScannerTileEnt.SPACING);
		int y = posY;
		int z = posZ + DungeonScannerTileEnt.SPACING + segment.row * (template.sizeXZ + DungeonScannerTileEnt.SPACING);

		this.structure.placeBlocks(world, x, y, z, rotation);
	}

	//place at position in dungeon
	public void placeSegment(LevelAccessor world, int posX, int posY, int posZ, int rotation) {
		this.structure.placeBlocks(world, posX, posY, posZ, rotation);
	}
}
