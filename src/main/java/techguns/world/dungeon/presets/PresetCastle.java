package techguns.world.dungeon.presets;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.tileentities.TGSpawnerTileEnt;
import techguns.world.dungeon.DungeonSegment;
import techguns.world.dungeon.DungeonTemplate;
import techguns.world.dungeon.IDungeonPath;
import techguns.world.dungeon.MazeDungeonPath;
import techguns.world.dungeon.TemplateSegment.SegmentType;

public class PresetCastle implements IDungeonPreset {

	int sizeXZ = 0;
	int sizeY = 0;

	private static final ResourceLocation LOOTTABLE = new ResourceLocation(Techguns.MODID, "chests/castle");

	ArrayList<DungeonTemplate> undergroundTemplates = new ArrayList<>();
	ArrayList<DungeonTemplate> lowerTemplates = new ArrayList<>();
	ArrayList<DungeonTemplate> midTemplates = new ArrayList<>();
	ArrayList<DungeonTemplate> upperTemplates = new ArrayList<>();
	ArrayList<DungeonTemplate> topTemplates = new ArrayList<>();
	ArrayList<DungeonTemplate> roofTemplates = new ArrayList<>();

	public PresetCastle() {
		undergroundTemplates.add(DungeonTemplate.get("ncdung1").setLoottable(LOOTTABLE));
		lowerTemplates.add(DungeonTemplate.get("nclower1").setLoottable(LOOTTABLE));
		midTemplates.add(DungeonTemplate.get("ncmid1").setLoottable(LOOTTABLE));
		upperTemplates.add(DungeonTemplate.get("ncupper1").setLoottable(LOOTTABLE));
		topTemplates.add(DungeonTemplate.get("nctop1").setLoottable(LOOTTABLE));
		roofTemplates.add(DungeonTemplate.get("ncroof1").setLoottable(LOOTTABLE));
	}

	@Override
	public DungeonSegment getSegment(SegmentType type, int y, int yMin, int yMax, boolean isSegmentAbove, boolean isSegmentBelow, int seed) {
		Random rand = new Random(seed);
		if (y == -1) {
			return roofTemplates.get(rand.nextInt(roofTemplates.size())).segments.get(type);
		} else if (y == yMin) {
			return undergroundTemplates.get(rand.nextInt(undergroundTemplates.size())).segments.get(type);
		} else if (y <= yMin + 1) {
			return lowerTemplates.get(rand.nextInt(lowerTemplates.size())).segments.get(type);
		} else if (y < yMax - 2) {
			return midTemplates.get(rand.nextInt(midTemplates.size())).segments.get(type);
		} else if (y < yMax - 1) {
			return upperTemplates.get(rand.nextInt(upperTemplates.size())).segments.get(type);
		} else {
			return topTemplates.get(rand.nextInt(topTemplates.size())).segments.get(type);
		}
	}

	@Override
	public int getSizeXZ() {
		if (this.sizeXZ == 0)
			this.sizeXZ = this.midTemplates.get(0).sizeXZ;
		return this.sizeXZ;
	}

	@Override
	public int getSizeY() {
		if (this.sizeY == 0)
			this.sizeY = this.midTemplates.get(0).sizeY;
		return this.sizeY;
	}

	@Override
	public void initDungeonPath(IDungeonPath d_path) {
		MazeDungeonPath path = (MazeDungeonPath) d_path;
		path.startHeightLevel = 1;
		path.chanceStraight = 0.8f;
		path.chanceRamp = 0.5f;
		path.chanceRoom = 0.25f;
		path.chanceFork = 0.4f;
		path.chanceUp = 0.65f;
		path.useFoundations = true;
		path.usePillars = true;
		path.useRoof = true;
	}

	@Override
	public void initSpawner(TGSpawnerTileEnt spawner) {
		spawner.setParams(2, 2, 200, 2);
		spawner.addMobType("zombiesoldier", 1);
		spawner.addMobType("skeletonsoldier", 1);
	}

}
