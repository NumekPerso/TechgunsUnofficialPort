package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Part of a multiblock machine (1.12 MultiBlockMachineTileEntSlave, was a plain TileEntity with the same client sync as
 * BasicTGTileEntity, now extends it).
 */
public abstract class MultiBlockMachineTileEntSlave extends BasicTGTileEntity {

	protected int masterX;
	protected int masterY;
	protected int masterZ;
	protected boolean hasMaster;

	/**
	 * 0 - unformed, other values depend on tile entity type
	 */
	protected byte type = 0;

	private MultiBlockMachineTileEntMaster masterTileEnt = null;

	public MultiBlockMachineTileEntSlave(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, false);
	}

	protected MultiBlockMachineTileEntMaster getMaster() {
		if (hasMaster) {
			if (masterTileEnt == null || masterTileEnt.isRemoved()) {
				BlockEntity tile = this.level.getBlockEntity(new BlockPos(this.masterX, this.masterY, this.masterZ));
				if (tile instanceof MultiBlockMachineTileEntMaster) {
					this.masterTileEnt = (MultiBlockMachineTileEntMaster) tile;
					return this.masterTileEnt;
				}
			} else {
				return masterTileEnt;
			}
		}
		return null;
	}

	public void unform() {
		this.hasMaster = false;
		this.masterTileEnt = null;
		this.setType((byte) 0);
	}

	public void setType(byte type) {
		this.type = type;
	}

	public void form(BlockPos masterPos, byte type) {
		this.hasMaster = true;
		this.masterX = masterPos.getX();
		this.masterY = masterPos.getY();
		this.masterZ = masterPos.getZ();
		this.type = type;
	}

	public int getSlaveType() {
		return this.type;
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putBoolean("hasMaster", this.hasMaster);
		if (this.hasMaster) {
			tags.putInt("masterX", this.masterX);
			tags.putInt("masterY", this.masterY);
			tags.putInt("masterZ", this.masterZ);
			tags.putByte("type", this.type);
		}
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		this.hasMaster = tags.getBoolean("hasMaster");
		if (this.hasMaster) {
			this.masterX = tags.getInt("masterX");
			this.masterY = tags.getInt("masterY");
			this.masterZ = tags.getInt("masterZ");
			this.type = tags.getByte("type");
		} else {
			this.type = 0;
		}
	}

	public boolean hasMaster() {
		return hasMaster;
	}

	public void onBlockBreak() {
		if (!this.level.isClientSide && this.hasMaster) {
			MultiBlockMachineTileEntMaster master = this.getMaster();
			if (master != null) {
				master.onMultiBlockBreak();
			}
		}
	}

	public BlockPos getMasterPos() {
		return new BlockPos(masterX, masterY, masterZ);
	}

	/**
	 * Shape of this block while the multiblock is formed (relative to the block position)
	 */
	public VoxelShape getFormedCollisionBoundingBox() {
		return Shapes.block();
	}
}
