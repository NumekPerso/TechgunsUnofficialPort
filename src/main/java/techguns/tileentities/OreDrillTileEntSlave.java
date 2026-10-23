package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGMachines;

/**
 * Frame, scaffold, rod, engine and hidden scaffold blocks of an ore drill.
 * (1.12 kept the tile when a scaffold became hidden, in 1.20 the hidden scaffold is another block, see OreDrillDefinition)
 */
public class OreDrillTileEntSlave extends MultiBlockMachineTileEntSlave {

	public OreDrillTileEntSlave(BlockPos pos, BlockState state) {
		super(TGMachines.OREDRILL_TILE_SLAVE, pos, state);
	}

}
