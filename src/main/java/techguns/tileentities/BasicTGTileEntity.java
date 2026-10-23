package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base of all Techguns block entities (1.12 BasicTGTileEntity).
 * Client sync: everything written by writeClientDataToNBT is in the chunk update tag and in the block entity data packet
 * sent by needUpdate(), saving writes the client data too.
 */
public class BasicTGTileEntity extends BlockEntity {

	/**
	 * Has this tileent rotation saved in the tile?
	 */
	protected boolean hasRotation;
	//How the tile is rotated
	public byte rotation = 0;

	public BasicTGTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean hasRotation) {
		super(type, pos, state);
		this.hasRotation = hasRotation;
	}

	protected int getUseRangeSquared() {
		return 64;
	}

	public boolean isUseableByPlayer(Player player) {
		if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D,
					(double) this.worldPosition.getZ() + 0.5D) <= getUseRangeSquared();
		}
	}

	/**
	 * Called every tick on both sides by the machine block ticker (1.12 ITickable.update), does nothing by default
	 */
	public void update() {
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this, be -> {
			CompoundTag tags = new CompoundTag();
			((BasicTGTileEntity) be).writeClientDataToNBT(tags);
			return tags;
		});
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		CompoundTag tag = packet.getTag();
		if (tag != null) {
			this.readClientDataFromNBT(tag);
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tags = super.getUpdateTag();
		this.writeClientDataToNBT(tags);
		return tags;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.readClientDataFromNBT(tag);
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		this.writeClientDataToNBT(compound);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		this.readClientDataFromNBT(compound);
	}

	/**
	 * write all data the client needs too, to nbt.
	 * @param tags
	 */
	public void writeClientDataToNBT(CompoundTag tags) {
		if (this.hasRotation) {
			tags.putByte("rotation", this.rotation);
		}
	}

	/**
	 * read all data the client needs from nbt
	 * @param tags
	 */
	public void readClientDataFromNBT(CompoundTag tags) {
		if (this.hasRotation) {
			this.rotation = (byte) (tags.getByte("rotation") % 4);
		}
	}

	public boolean hasRotation() {
		return hasRotation;
	}

	public void rotateTile() {
		if (!this.level.isClientSide && this.hasRotation) {
			this.rotation = (byte) ((this.rotation + 1) % 4);
			this.needUpdate();
		}
	}

	public void rotateTile(Direction sideHit) {
		this.rotateTile();
	}

	public boolean canBeWrenchRotated() {
		return true;
	}

	public boolean canBeWrenchDismantled() {
		return true;
	}

	/**
	 * called serverside when this tileent should send out updated to client
	 */
	public void needUpdate() {
		if (this.level != null && !this.level.isClientSide) {
			BlockState state = this.getBlockState();
			this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
		}
		this.setChanged();
	}

}
