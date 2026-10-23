package techguns.world.structures;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import techguns.Techguns;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.util.MBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockChestLoottable;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class PoliceStation extends WorldgenStructure {

	private static final ResourceLocation CHEST_LOOT = new ResourceLocation(Techguns.MODID, "chests/policestation");

	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(new MBlock("techguns:concrete", 0));
		blockList.add(MBlockRegister.IRON_BLOCK_SMALL_INGOTS);
		blockList.add(MBlockRegister.IRON_PANE_MODERN_FENCE);
		blockList.add(MBlockRegister.AIR);
		blockList.add(new MBlock("techguns:sandbags", 0));
		blockList.add(new MBlock("techguns:lamp0", 11));
		blockList.add(new MBlock("minecraft:stonebrick", 0));
		blockList.add(MBlockRegister.BLUE_CONCRETE_SMALL_BRICKS);
		blockList.add(new MBlock("techguns:bunkerdoor", 2));
		blockList.add(new MBlock("techguns:bunkerdoor", 9));
		blockList.add(new MBlock("minecraft:glass_pane", 0));
		blockList.add(MBlockRegister.OAK_PLANKS_1);
		blockList.add(new MBlockChestLoottable("minecraft:chest", 2, CHEST_LOOT));
		blockList.add(MBlockRegister.COBBLESTONE_7);
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.SOLDIER_SPAWN, 3, 1, 200, 1).addMobType("zombiepoliceman", 1));
		blockList.add(new MBlock("techguns:lamp0", 7));
		blockList.add(new MBlock("minecraft:rail", 6));
		blockList.add(new MBlock("minecraft:rail", 9));
		blockList.add(new MBlock("techguns:ladder0", 4));
		blockList.add(new MBlock("minecraft:rail", 7));
		blockList.add(new MBlock("minecraft:rail", 8));
		blockList.add(new MBlock("minecraft:stone_slab", 8));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 3, CHEST_LOOT));
		blockList.add(new MBlock("minecraft:crafting_table", 0));
		blockList.add(new MBlock("minecraft:fence_gate", 0));
		blockList.add(new MBlock("techguns:bunkerdoor", 1));
		blockList.add(new MBlock("techguns:bunkerdoor", 8));

		blocks = StructureBlockUtils.loadStructureFromFile("policestation");
	}

	public PoliceStation() {
		super(13, 8, 13, 13, 8, 13);
		this.setXYZSize(13, 8, 13);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];
		StructureBlockUtils.cleanUpwards(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 7);
		StructureBlockUtils.placeFoundation(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 3);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, this.lootTier, colorType);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 1, this.lootTier, colorType);
	}

}
