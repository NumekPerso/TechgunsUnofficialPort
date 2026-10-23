package techguns.tileentities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import techguns.TGMachines;

/**
 * Housing/glass block of the reaction chamber (1.12 ReactionChamberTileEntSlave).
 * type 2 = side connectors (items + fluid of the master), type 3 = energy connector on top
 */
public class ReactionChamberTileEntSlave extends MultiBlockMachineTileEntSlave {

	public ReactionChamberTileEntSlave(BlockPos pos, BlockState state) {
		super(TGMachines.REACTIONCHAMBER_SLAVE_TILE, pos, state);
	}

	@Override
	public <T> @Nonnull LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (this.hasMaster && this.level != null) {
			MultiBlockMachineTileEntMaster master = getMaster();
			if (master != null) {
				if (type == 2 && (capability == ForgeCapabilities.ITEM_HANDLER || capability == ForgeCapabilities.FLUID_HANDLER)) {
					return master.getCapability(capability, facing);
				} else if (type == 3 && capability == ForgeCapabilities.ENERGY) {
					return master.getCapability(capability, facing);
				}
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public VoxelShape getFormedCollisionBoundingBox() {
		if (this.hasMaster && this.level != null) {
			MultiBlockMachineTileEntMaster master = this.getMaster();
			if (master != null) {
				return Shapes.create(master.getBBforSlave(this.getBlockPos()));
			}
		}
		return Shapes.block();
	}
}
