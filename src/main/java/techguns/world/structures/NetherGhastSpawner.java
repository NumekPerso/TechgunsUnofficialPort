package techguns.world.structures;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.util.MBlock;
import techguns.util.MBlockVanillaSpawner;
import techguns.world.StructureBlockUtils;
import techguns.world.dungeon.presets.specialblocks.MBlockChestLoottable;

public class NetherGhastSpawner extends NetherTemplateStructure {
	private static final ResourceLocation CHEST_LOOT = new ResourceLocation(Techguns.MODID, "chests/factory_building");
	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		blockList.add(MBlockRegister.NETHERRACK_C2);
		blockList.add(MBlockRegister.AIR);
		blockList.add(MBlockRegister.NETHERRACK_C1);
		blockList.add(MBlockRegister.NETHERRACK_ROCKY);
		blockList.add(new MBlock("minecraft:glowstone", 0));
		blockList.add(new MBlock("techguns:nethermetal", 3));
		blockList.add(new MBlock("minecraft:iron_bars", 0));
		blockList.add(new MBlock("minecraft:skull", 1));
		blockList.add(new MBlock("minecraft:soul_sand", 0));
		blockList.add(new MBlockVanillaSpawner("ghastling"));
		blockList.add(MBlockRegister.NETHERRACK_C12);
		blockList.add(MBlockRegister.NETHERRACK_C8);
		blockList.add(new MBlockChestLoottable("minecraft:chest", 3, CHEST_LOOT));
		blocks = StructureBlockUtils.loadStructureFromFile("nether_ghast_spawner");
	}

	public NetherGhastSpawner() {
		super(10, 14, 10, 10, 14, 10, -2, 16);
	}

	@Override
	protected List<MBlock> getBlockList() {
		return blockList;
	}

	@Override
	protected short[][] getBlocks() {
		return blocks;
	}
}
