package techguns.world.structures;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import techguns.Techguns;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.util.MBlock;
import techguns.world.EnumLootType;
import techguns.world.StructureBlockUtils;
import techguns.world.TGSpawnContext;
import techguns.world.TGSpawnData;
import techguns.world.dungeon.presets.specialblocks.MBlockChestLoottable;
import techguns.world.dungeon.presets.specialblocks.MBlockTGSpawner;

public class AircraftCarrier extends WorldgenStructure {
	private final static ResourceLocation LOOT_TABLE = new ResourceLocation(Techguns.MODID, "chests/aircraftcarrier");

	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(MBlockRegister.HELIPAD_HAZARDBLOCK);
		blockList.add(MBlockRegister.FACTORY_PLATE_DOTTED_GREY);
		blockList.add(MBlockRegister.FACTORY_PLATE_VERY_RUSTY);
		blockList.add(MBlockRegister.FACTORY_PLATE_WORN_COLUMN);
		blockList.add(MBlockRegister.FACTORY_WIREFRAME);
		blockList.add(new MBlock("minecraft:crafting_table", 0));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 5, LOOT_TABLE));
		blockList.add(new MBlock("techguns:lamp0", Direction.NORTH.get3DDataValue()));
		blockList.add(new MBlock("techguns:lamp0", Direction.WEST.get3DDataValue()));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 5, LOOT_TABLE));
		blockList.add(new MBlock("minecraft:air", 0));
		blockList.add(MBlockRegister.SUPPLY_CRATES);
		blockList.add(MBlockRegister.SUPPLY_CRATES);
		blockList.add(new MBlock("minecraft:glass_pane", 0));
		blockList.add(MBlockRegister.TECHNICAL_PIPES);
		blockList.add(new MBlock("techguns:lamp0", Direction.SOUTH.get3DDataValue()));
		blockList.add(new MBlock("techguns:lamp0", Direction.EAST.get3DDataValue()));
		blockList.add(new MBlock("techguns:bunkerdoor", 2));
		blockList.add(new MBlock("techguns:bunkerdoor", 9));
		blockList.add(new MBlock("techguns:bunkerdoor", 8));
		blockList.add(MBlockRegister.FACTORY_SHINY_METAL);
		blockList.add(new MBlock("minecraft:glass", 0));
		blockList.add(new MBlock("minecraft:iron_bars", 0));
		blockList.add(MBlockRegister.TECHNICAL2_SCAFFOLD_TRANSPARENT);
		blockList.add(new MBlock("techguns:lamp0", Direction.UP.get3DDataValue()));
		blockList.add(new MBlock("techguns:bunkerdoor", 0));
		blockList.add(new MBlock("techguns:bunkerdoor", 1));
		blockList.add(new MBlock("techguns:bunkerdoor", 3));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 3, LOOT_TABLE));
		blockList.add(new MBlockChestLoottable("minecraft:chest", 2, LOOT_TABLE));
		blockList.add(MBlockRegister.ALUMINIUM_STAIRS_WEST);
		blockList.add(MBlockRegister.ALUMINIUM_STAIRS_EAST);
		blockList.add(new MBlock("techguns:ladder0", 0));
		blockList.add(MBlockRegister.IRON_BLOCK_VENTS);
		blockList.add(MBlockRegister.TECHNICAL_GRATE);
		blockList.add(new MBlockChestLoottable("minecraft:chest", 3, LOOT_TABLE));
		blockList.add(MBlockRegister.TECHNICAL2_FAN);
		blockList.add(new MBlockChestLoottable("minecraft:chest", 4, LOOT_TABLE));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.HOLE, 6, 2, 150, 2).addMobType("armysoldier", 1).addMobType("commando", 1));
		blockList.add(new MBlockTGSpawner(EnumMonsterSpawnerType.SOLDIER_SPAWN, 1, 1, 200, 0).addMobType("attackhelicopter", 1));
		blockList.add(MBlockRegister.SUPPLY_CRATES_CHANCE);

		blocks = StructureBlockUtils.loadStructureFromFile("aircraft_carrier");
	}

	public AircraftCarrier(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.lootTier = EnumLootType.TIER2;
	}

	/**
	 * 1.12 spawnStructureWorldgen: on the water surface at the chunk corner
	 */
	@Override
	public TGSpawnData findSpawn(TGSpawnContext ctx, int chunkX, int chunkZ, int sizeX, int sizeY, int sizeZ, Random rnd) {
		int y = ctx.getValidSpawnYWater(chunkX * 16, chunkZ * 16, sizeX, sizeZ, 2);
		if (y < 0) {
			return null;
		}
		int x = chunkX * 16;
		int z = chunkZ * 16;
		return new TGSpawnData(x, y - 3, z, sizeX, sizeY, sizeZ, rnd.nextInt(4));
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {
		int[] c = getCenterAndDirection(sizeX, sizeZ, direction);
		int centerX = c[0], centerZ = c[1];
		direction = c[2];
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 0, this.lootTier, colorType);
		StructureBlockUtils.placeScannedStructure(world, blocks, blockList, posX, posY, posZ, centerX, centerZ, direction, 1, this.lootTier, colorType);
	}

}
