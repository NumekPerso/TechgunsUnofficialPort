package techguns.tileentities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import techguns.TGMachines;

/**
 * Housing (type 2) and glass (type 1) blocks of a formed fabricator. Housing blocks expose the item handler and energy
 * of the controller.
 */
public class FabricatorTileEntSlave extends MultiBlockMachineTileEntSlave {

	protected static final VoxelShape BB_GLASS_FORMED = Block.box(0, 0, 0, 16, 16 * 0.9f, 16);

	public FabricatorTileEntSlave(BlockPos pos, BlockState state) {
		super(TGMachines.FABRICATOR_TILE_SLAVE, pos, state);
	}

	@Override
	public <T> @Nonnull LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (type == 2) {
			if (capability == ForgeCapabilities.ITEM_HANDLER || capability == ForgeCapabilities.ENERGY) {
				MultiBlockMachineTileEntMaster master = getMaster();
				if (master != null) {
					return master.getCapability(capability, facing);
				}
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public VoxelShape getFormedCollisionBoundingBox() {
		if (this.hasMaster && this.type == 1) {
			return BB_GLASS_FORMED;
		}
		return Shapes.block();
	}
}
