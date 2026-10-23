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

public class PresetNetherDungeonUnderground implements IDungeonPreset {

	int sizeXZ = 0;
	int sizeY = 0;

	private static final ResourceLocation LOOTTABLE = new ResourceLocation(Techguns.MODID, "chests/castle");

	ArrayList<DungeonTemplate> bottomTemplates = new ArrayList<>();
	ArrayList<DungeonTemplate> lowerTemplates = new ArrayList<>();
	ArrayList<DungeonTemplate> midTemplates = new ArrayList<>();
	ArrayList<DungeonTemplate> upperTemplates = new ArrayList<>();

	public PresetNetherDungeonUnderground() {
		bottomTemplates.add(DungeonTemplate.get("nether_b0").setLoottable(LOOTTABLE));
		lowerTemplates.add(DungeonTemplate.get("nether_b1").setLoottable(LOOTTABLE));
		midTemplates.add(DungeonTemplate.get("nether0").setLoottable(LOOTTABLE));
		upperTemplates.add(DungeonTemplate.get("nether1").setLoottable(LOOTTABLE));
	}

	@Override
	public DungeonSegment getSegment(SegmentType type, int y, int yMin, int yMax, boolean isSegmentAbove, boolean isSegmentBelow, int seed) {
		Random rand = new Random(seed);
		if (y == yMin) {
			return bottomTemplates.get(rand.nextInt(bottomTemplates.size())).segments.get(type);
		} else if (y <= yMin + 1) {
			return lowerTemplates.get(rand.nextInt(lowerTemplates.size())).segments.get(type);
		} else if (y <= yMin + 2) {
			return midTemplates.get(rand.nextInt(midTemplates.size())).segments.get(type);
		} else {
			return upperTemplates.get(rand.nextInt(upperTemplates.size())).segments.get(type);
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
		path.startHeightLevel = -1; //actually it's 1 because of bottom layer
		path.chanceStraight = 0.7f;
		path.chanceRamp = 0.40f;
		path.chanceRoom = 0.35f;
		path.chanceFork = 0.5f;
		path.chanceUp = 0.6f;
		path.useFoundations = true;
		path.usePillars = false;
		path.useRoof = false;
		path.useBottomLayer = true;
		path.entranceRampLength = 3;
	}

	@Override
	public void initSpawner(TGSpawnerTileEnt spawner) {
		spawner.setParams(2, 2, 200, 2);
		spawner.addMobType("zombiesoldier", 1);
		spawner.addMobType("skeletonsoldier", 1);
	}

}
