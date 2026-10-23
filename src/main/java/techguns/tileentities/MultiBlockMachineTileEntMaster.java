package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import techguns.blocks.machines.MultiBlockMachine;
import techguns.blocks.machines.multiblocks.MultiBlockMachineSchematic;
import techguns.blocks.machines.multiblocks.MultiBlockRegister;

/**
 * Controller of a multiblock machine (1.12 MultiBlockMachineTileEntMaster). The structure is described by a
 * MultiBlockMachineSchematic registered in MultiBlockRegister for the master class.
 */
public abstract class MultiBlockMachineTileEntMaster extends BasicMachineTileEnt {

	protected boolean formed = false;
	protected Direction multiblockDirection;

	public MultiBlockMachineTileEntMaster(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, int maximumPower) {
		super(type, pos, state, inventorySize, false, maximumPower);
	}

	public Direction getMultiblockDirection() {
		return multiblockDirection;
	}

	public boolean isFormed() {
		return formed;
	}

	@Override
	protected boolean enabled() {
		return this.isFormed();
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		this.formed = tags.getBoolean("formed");
		if (this.formed) {
			this.multiblockDirection = Direction.from3DDataValue(tags.getByte("multiblockDirection"));
		}
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putBoolean("formed", this.formed);
		if (this.formed) {
			tags.putByte("multiblockDirection", (byte) this.multiblockDirection.get3DDataValue());
		}
	}

	@Override
	public void onBlockBreak() {
		super.onBlockBreak();
		this.onMultiBlockBreak();
	}

	@Override
	public boolean canBeWrenchRotated() {
		return false;
	}

	@Override
	public boolean canBeWrenchDismantled() {
		return false;
	}

	public void onMultiBlockBreak() {
		if (!this.level.isClientSide && this.formed) {
			MultiBlockMachineSchematic multiblock = MultiBlockRegister.REGISTER.get(this.getClass());
			if (multiblock != null) {
				multiblock.unform(this.level, this);
			}
		}
	}

	public void form(Direction facing) {
		this.formed = true;
		if (!this.level.isClientSide) {
			this.multiblockDirection = facing;
			BlockState bs = this.level.getBlockState(worldPosition);
			if (bs.hasProperty(MultiBlockMachine.FORMED)) {
				bs = bs.setValue(MultiBlockMachine.FORMED, true);
				if (facing != null && facing.getAxis().isHorizontal()) {
					//1.12 getActualState: the direction is a block state property in 1.20
					bs = bs.setValue(MultiBlockMachine.MULTIBLOCK_DIRECTION, facing);
				}
				this.level.setBlock(worldPosition, bs, 3);
			}
			this.needUpdate();
		}
	}

	public void unform() {
		this.formed = false;
		if (!this.level.isClientSide) {
			BlockState bs = level.getBlockState(worldPosition);
			if (bs.getBlock() == this.getBlockState().getBlock() && bs.hasProperty(MultiBlockMachine.FORMED)) {
				this.level.setBlock(worldPosition, bs.setValue(MultiBlockMachine.FORMED, false), 3);
			}
		}
	}

	public abstract AABB getBBforSlave(BlockPos slavePos);

}
