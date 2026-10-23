package techguns.world.dungeon.presets.specialblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import techguns.util.MBlock;
import techguns.world.TGStructureLevel;

public class MBlockChestLoottable extends MBlock {

	protected ResourceLocation loottable;

	/**
	 * @param block 1.12 registry name of the chest block
	 */
	public MBlockChestLoottable(String block, int meta, ResourceLocation loottable) {
		super(block, meta);
		this.loottable = loottable;
		this.hasTileEntity = true;
	}

	public MBlockChestLoottable(BlockState state, ResourceLocation loottable) {
		super(state);
		this.loottable = loottable;
		this.hasTileEntity = true;
	}

	@Override
	public void tileEntityPostPlacementAction(LevelAccessor w, BlockState state, BlockPos p, int rotation) {
		BlockEntity tile = w.getBlockEntity(p);
		if (tile instanceof RandomizableContainerBlockEntity chest) {
			chest.setLootTable(this.loottable, TGStructureLevel.posRandom(w, p).nextLong());
		}
	}

}
