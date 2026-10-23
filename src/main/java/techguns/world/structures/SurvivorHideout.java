package techguns.world.structures;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import techguns.TGuns;
import techguns.Techguns;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.util.MBlock;
import techguns.util.MultiMMBlockIndexRoll;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockChestLoottable;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class SurvivorHideout extends WorldgenStructure {

	private static final ResourceLocation CHEST_LOOT = new ResourceLocation(Techguns.MODID, "chests/survivor_hideout");

	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(new MBlock("minecraft:dirt", 1));
		blockList.add(new MBlock("minecraft:fence", 0));
		blockList.add(MBlockRegister.AIR);
		blockList.add(MBlockRegister.SURIVIVOR_HIDEOUT_IRON_BOTTOM);
		blockList.add(new MBlock("techguns:sandbags", 0));
		blockList.add(MBlockRegister.SURIVIVOR_HIDEOUT_IRON_TOP);
		blockList.add(new MBlock("techguns:lamp0", 12));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 2, CHEST_LOOT));
		blockList.add(new MBlock("minecraft:stonebrick", 0));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 3, 2, 200, 1).addMobType("bandit", 1));
		blockList.add(new MBlock("minecraft:planks", 0));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 4, CHEST_LOOT));
		blockList.add(new MBlock("minecraft:oak_stairs", 0));
		blockList.add(new MultiMMBlockIndexRoll(new MBlock[] { new MBlock("techguns:metalpanel", 0), new MBlock("techguns:metalpanel", 1), new MBlock("techguns:metalpanel", 2),
				new MBlock("techguns:metalpanel", 3) }, new int[] { 1, 1, 1, 1 }));
		blockList.add(new MBlock("minecraft:planks", 1));
		blockList.add(new MBlock("minecraft:stone_slab", 3));
		blockList.add(MBlockRegister.CAMO_NET_TOP);
		blockList.add(new MBlock("minecraft:bed", 10));
		blockList.add(new MBlock("minecraft:bed", 2));
		blockList.add(new MBlock("minecraft:torch", 1));
		blockList.add(new MBlock("minecraft:crafting_table", 0));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 2, 1, 200, 1).addMobType("bandit", 1).setWeaponOverride(new ItemStack(TGuns.boltaction)));
		blockList.add(new MBlock("minecraft:furnace", 3));
		blockList.add(new MBlock("techguns:ladder0", 0));
		blockList.add(new MBlock("techguns:simplemachine", 9));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 3, CHEST_LOOT));

		blocks = StructureBlockUtils.loadStructureFromFile("survivor_hideout");
	}

	public SurvivorHideout() {
		super(11, 11, 19, 11, 11, 19);
		setXYZSize(11, 11, 19);
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];

		int indexroll = rnd.nextInt(4);

		StructureBlockUtils.cleanUpwards(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 7);
		StructureBlockUtils.placeFoundation(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, 3);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, this.lootTier, colorType, indexroll, rnd);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 1, this.lootTier, colorType);
	}

}
