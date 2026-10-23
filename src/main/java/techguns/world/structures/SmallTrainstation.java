package techguns.world.structures;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import techguns.Techguns;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.util.MBlock;
import techguns.util.MultiMBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockChestLoottable;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class SmallTrainstation extends WorldgenStructure {

	private static final ResourceLocation CHEST_LOOT = new ResourceLocation(Techguns.MODID, "chests/small_trainstation");

	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(new MBlock("minecraft:gravel", 0));
		blockList.add(new MultiMBlock(new String[] { "minecraft:rail", "minecraft:air" }, new int[] { 0, 0 }, new int[] { 2, 1 }));
		blockList.add(MBlockRegister.COBBLESTONE_MOSSY_RND);
		blockList.add(MBlockRegister.STONEBRICK_STAIRS_EAST_CRACKED_RND);
		blockList.add(MBlockRegister.COBBLESTONE_FLOOR);
		blockList.add(MBlockRegister.BRICKS_CRACKED_RND);
		blockList.add(new MBlock("minecraft:air", 0));
		blockList.add(new MultiMBlock(new String[] { "minecraft:glass_pane", "minecraft:air" }, new int[] { 4, 0 }, new int[] { 1, 1 }));
		blockList.add(MBlockRegister.STONEBRICK_STAIRS_WEST_CRACKED_INV_RND);
		blockList.add(MBlockRegister.STONEBRICK_CRACKED_RND);
		blockList.add(new MBlock("minecraft:fence", 0));
		blockList.add(new MBlock("minecraft:wooden_pressure_plate", 0));
		blockList.add(new MBlock("minecraft:wooden_slab", 8));
		blockList.add(new MBlock("minecraft:ladder", 3));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 2, CHEST_LOOT));
		blockList.add(MBlockRegister.STONEBRICK_STAIRS_SOUTH_INV);
		blockList.add(MBlockRegister.STONEBRICK_STAIRS_NORTH_INV);
		blockList.add(new MBlock("minecraft:furnace", 4));
		blockList.add(new MBlock("minecraft:crafting_table", 0));
		blockList.add(MBlockRegister.STONEBRICK_STAIRS_EAST_CRACKED_INV_RND);
		blockList.add(MBlockRegister.STONEBRICK_STAIRS_WEST_CRACKED_RND);
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 3, 2, 200, 1).addMobType("zombieminer", 1));

		blocks = StructureBlockUtils.loadStructureFromFile("small_trainstation");
	}

	public SmallTrainstation(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];
		StructureBlockUtils.cleanUpwards(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 7);
		StructureBlockUtils.placeFoundation(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 1);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, this.lootTier, colorType);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 1, this.lootTier, colorType);
	}
}
