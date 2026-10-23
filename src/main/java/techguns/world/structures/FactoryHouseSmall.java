package techguns.world.structures;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import techguns.TGBlocks;
import techguns.Techguns;
import techguns.blocks.BlockTGLadder;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.util.MBlock;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockChestLoottable;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class FactoryHouseSmall extends WorldgenStructure {

	private static final ResourceLocation CHEST_LOOT = new ResourceLocation(Techguns.MODID, "chests/factory_building");

	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(new MBlock("techguns:concrete", 2));
		blockList.add(MBlockRegister.TECHNICAL_BLOCK_SCAFFOLD);
		blockList.add(MBlockRegister.FACTORY_PLATE);
		blockList.add(new MBlock("minecraft:glass_pane", 0));
		blockList.add(MBlockRegister.TECHNICAL_CONCRETE);
		blockList.add(MBlockRegister.FACTORY_CRATE);
		blockList.add(new MBlockChestLoottable("minecraft:chest", 5, CHEST_LOOT));
		blockList.add(new MBlock("minecraft:air", 0));
		blockList.add(new MBlock("techguns:lamp0", Direction.NORTH.get3DDataValue()));
		blockList.add(new MBlock("techguns:lamp0", Direction.WEST.get3DDataValue()));
		blockList.add(MBlockRegister.FACTORY_PLATE_DOTTED);
		blockList.add(new MBlock("minecraft:crafting_table", 0));
		blockList.add(new MBlock(TGBlocks.BUNKER_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.SOUTH).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT)
				.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)));
		blockList.add(new MBlock(TGBlocks.BUNKER_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.SOUTH).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT)
				.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)));
		blockList.add(new MBlock("techguns:lamp0", Direction.UP.get3DDataValue()));
		blockList.add(new MBlock("techguns:lamp0", Direction.SOUTH.get3DDataValue()));
		blockList.add(new MBlock("minecraft:furnace", 3));
		blockList.add(new MBlock("techguns:lamp0", Direction.EAST.get3DDataValue()));
		blockList.add(new MBlock(TGBlocks.LADDER_0.defaultBlockState().setValue(BlockTGLadder.FACING, Direction.SOUTH)));
		blockList.add(new MBlock("minecraft:iron_bars", 0));
		blockList.add(new MBlock("techguns:lamp0", Direction.WEST.get3DDataValue()));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 5, 2, 150, 2).addMobType("zombieminer", 1));
		blocks = StructureBlockUtils.loadStructureFromFile("factory_building_small");
	}

	public FactoryHouseSmall(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.setSwapXZ(true);
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
