package techguns.world.structures;

import java.util.Random;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import techguns.world.TGSpawnData;
import techguns.world.dungeon.Dungeon;
import techguns.world.dungeon.presets.IDungeonPreset;

/**
 * 1.20: common code of the 1.12 dungeon structures (CastleStructure, TechFortressStructure, NetherDungeonStructure):
 * a Dungeon of the preset, placed one segment below the ground (cave floor in the nether).
 */
public abstract class DungeonStructure extends WorldgenStructure {

	protected abstract IDungeonPreset getPreset();

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		IDungeonPreset preset = getPreset();
		Dungeon dungeon = new Dungeon(preset);
		dungeon.generate(world, posX, posY + 1 - preset.getSizeY(), posZ, sizeX, sizeY, sizeZ, rnd);
	}

	@Override
	public BoundingBox getBoundingBox(TGSpawnData d) {
		// the dungeon is not rotated: segments from (x,z) to (x+sizeX, z+sizeZ); foundations go down 2 segments
		IDungeonPreset preset = getPreset();
		int s = preset.getSizeY();
		return new BoundingBox(d.x - 2, d.y - 3 * s - 2, d.z - 2, d.x + d.sizeX + preset.getSizeXZ() + 2, d.y + d.sizeY + 2 * s + 2, d.z + d.sizeZ + preset.getSizeXZ() + 2);
	}
}
