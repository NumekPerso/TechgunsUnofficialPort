package techguns.world.dungeon.presets.specialblocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGBlocks;
import techguns.blocks.BlockMilitaryCrate;
import techguns.blocks.EnumMilitaryCrateType;
import techguns.blocks.BlockTGSpawner;
import techguns.util.MBlock;

public class SpecialBlockHandler {

	public static void applySpecialMBlocks(List<MBlock> list, ResourceLocation loottable) {

		for (int i = 0; i < list.size(); i++) {

			MBlock mb = list.get(i);

			if (mb.block instanceof BlockTGSpawner) {
				list.set(i, new MBlockTGSpawner(mb));
			} else if (mb.block instanceof SkullBlock || mb.block instanceof WallSkullBlock) {
				list.set(i, new MBlockSkullBlock(mb));
			} else if (mb.block instanceof BlockMilitaryCrate) {
				List<BlockState> states = new ArrayList<>();
				for (EnumMilitaryCrateType t : EnumMilitaryCrateType.values()) {
					states.add(TGBlocks.getMilitaryCrate(t).defaultBlockState());
				}
				list.set(i, new RandomStateMBlock(mb, states));
			} else if (mb.block instanceof ChestBlock && loottable != null) {
				list.set(i, new MBlockChestLoottable(mb.getState(), loottable));
			}

		}

	}

}
