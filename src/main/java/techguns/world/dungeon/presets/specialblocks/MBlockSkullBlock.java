package techguns.world.dungeon.presets.specialblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import techguns.util.MBlock;

/**
 * 1.12 skulls stored the rotation in the tile entity, 1.20 floor skulls have a ROTATION block state (0-15)
 */
public class MBlockSkullBlock extends MBlock {

	public MBlockSkullBlock(MBlock other) {
		super(other);
		this.hasTileEntity = true;
	}

	@Override
	public void tileEntityPostPlacementAction(LevelAccessor w, BlockState state, BlockPos p, int rotation) {
		if (state.hasProperty(SkullBlock.ROTATION)) {
			w.setBlock(p, state.setValue(SkullBlock.ROTATION, (rotation * 4) % 16), 2);
		}
	}

}
