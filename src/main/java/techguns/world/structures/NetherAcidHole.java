package techguns.world.structures;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGFluids;
import techguns.util.MBlock;
import techguns.util.MultiMBlock;
import techguns.world.StructureBlockUtils;

public class NetherAcidHole extends NetherTemplateStructure {
	static ArrayList<MBlock> blockList = new ArrayList<MBlock>();
	static short[][] blocks;
	static {
		BlockState acid = TGFluids.BLOCK_FLUID_ACID != null ? TGFluids.BLOCK_FLUID_ACID.defaultBlockState() : Blocks.LAVA.defaultBlockState();
		blockList.add(MBlockRegister.NETHERRACK_ROCKY);
		blockList.add(MBlockRegister.AIR);
		blockList.add(new MBlock("minecraft:soul_sand", 0));
		blockList.add(new MultiMBlock(new BlockState[] { acid, Blocks.NETHERRACK.defaultBlockState() }, new int[] { 1, 1 }));
		blockList.add(new MBlock(acid));
		blocks = StructureBlockUtils.loadStructureFromFile("nether_acid_hole");
	}

	public NetherAcidHole() {
		super(9, 6, 9, 9, 6, 9, -1, 16);
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
