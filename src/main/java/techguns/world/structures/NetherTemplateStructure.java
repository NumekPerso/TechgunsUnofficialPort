package techguns.world.structures;

import java.util.List;
import java.util.Random;

import net.minecraft.world.level.LevelAccessor;
import techguns.util.MBlock;
import techguns.world.StructureBlockUtils;

/**
 * 1.20: common setBlocks of the 1.12 nether template structures (NetherAltarSmall, NetherSoulPlatform, ...), which
 * all were the same code with a different height offset and foundation depth.
 */
public abstract class NetherTemplateStructure extends WorldgenStructure {

	protected final int hoffset;
	protected final int foundationRange;

	public NetherTemplateStructure(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int hoffset, int foundationRange) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.hoffset = hoffset;
		this.foundationRange = foundationRange;
	}

	protected abstract List<MBlock> getBlockList();

	protected abstract short[][] getBlocks();

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];
		List<MBlock> blockList = getBlockList();
		short[][] blocks = getBlocks();
		StructureBlockUtils.placeFoundationNether(world, blocks, blockList, posX, posY + hoffset, posZ, centerX, centerZ, direction, 0, foundationRange);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY + hoffset, posZ, centerX, centerZ, direction, 0, this.lootTier, colorType);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY + hoffset, posZ, centerX, centerZ, direction, 1, this.lootTier, colorType);
	}
}
