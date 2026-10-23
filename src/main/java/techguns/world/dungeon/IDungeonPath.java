package techguns.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import techguns.world.dungeon.MazeDungeonPath.PathSegment;
import techguns.world.dungeon.presets.IDungeonPreset;


public interface IDungeonPath {

	public int getNumSegments();

	public void generatePath();

	public void generateSegment(int x, int y, int z, int dir, PathSegment prev);

	public void generateDungeon(LevelAccessor world, int posX, int posY, int posZ, IDungeonPreset preset);

	public void generateNPCSpawners(LevelAccessor world, int posX, int posY, int posZ, IDungeonPreset preset);
	
	public Direction getEntranceRotation();
	
	public BlockPos getStartPos();
}
