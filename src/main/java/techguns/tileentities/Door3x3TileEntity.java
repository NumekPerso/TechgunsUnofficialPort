package techguns.tileentities;

import static techguns.gui.ButtonConstants.BUTTON_ID_REDSTONE;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import techguns.TGBlocks;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.blocks.BlockTGDoor3x3;
import techguns.blocks.EnumDoorType;
import techguns.gui.containers.Door3x3Container;
import techguns.packets.PacketDoorStateChange;

/**
 * Tile entity of the master block of a 3x3 door (owner, security and redstone mode from BasicRedstoneTileEnt, no inventory).
 * The configuration GUI (Door3x3Gui) is opened with sneak + wrench.
 */
public class Door3x3TileEntity extends BasicRedstoneTileEnt {

	protected long lastStateChangeTime = 0L;

	/**
	 * 0 - manual
	 * 1 - redstone
	 * 2 - player-sensor
	 */
	protected byte doormode = 0;

	protected boolean autoClose = true;

	public static final int BUTTON_ID_DOORMODE = BUTTON_ID_REDSTONE + 1;
	public static final int BUTTON_ID_AUTOCLOSE = BUTTON_ID_REDSTONE + 2;

	public Door3x3TileEntity(BlockPos pos, BlockState state) {
		super(TGBlocks.DOOR3x3_TILEENT, pos, state, 0, false);
	}

	/**
	 * The door type is a block state property in the port
	 */
	public int getDoorType() {
		BlockState state = this.getBlockState();
		if (state.getBlock() instanceof BlockTGDoor3x3) {
			return state.getValue(BlockTGDoor3x3.TYPE).ordinal();
		}
		return 0;
	}

	public EnumDoorType getDoorTypeEnum() {
		return EnumDoorType.values()[getDoorType()];
	}

	@Override
	public AABB getRenderBoundingBox() {
		BlockState state = this.getBlockState();
		if (state.getBlock() instanceof BlockTGDoor3x3) {
			if (state.getValue(BlockTGDoor3x3.ZPLANE)) {
				return new AABB(getBlockPos()).inflate(1, 4, 3);
			} else {
				return new AABB(getBlockPos()).inflate(3, 4, 1);
			}
		}
		return super.getRenderBoundingBox();
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Techguns.MODID + ".container.door3x3");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new Door3x3Container(id, inv, this);
	}

	@Override
	public void setOwner(Player ply) {
		super.setOwner(ply);
		this.setChanged();
	}

	/**
	 * The door can't be dismantled / rotated with a wrench
	 */
	@Override
	public boolean canBeWrenchDismantled() {
		return false;
	}

	@Override
	public boolean canBeWrenchRotated() {
		return false;
	}

	// ---- door ----

	public long getLastStateChangeTime() {
		return lastStateChangeTime;
	}

	public void setLastStateChangeTime(long lastStateChangeTime) {
		this.lastStateChangeTime = lastStateChangeTime;
	}

	public boolean isOpenWithRightClick() {
		return this.doormode == 0;
	}

	public boolean isAutoClose() {
		return autoClose;
	}

	public void setAutoClose(boolean autoClose) {
		if (!this.autoClose && autoClose) {
			this.checkScheduleBlockUpdate();
		}
		this.autoClose = autoClose;
	}

	public boolean isPlayerDetector() {
		return this.doormode == 2;
	}

	public void checkScheduleBlockUpdate() {
		if (this.level == null || this.level.isClientSide) return;
		if (!this.autoClose && !(this.doormode == 2)) {
			this.level.scheduleTick(getBlockPos(), getBlockState().getBlock(), BlockTGDoor3x3.BLOCK_UPDATE_DELAY);
		}
	}

	public boolean checkAutoCloseDelay() {
		long time = System.currentTimeMillis();
		long diff = time - this.lastStateChangeTime;
		return diff > BlockTGDoor3x3.DOOR_AUTOCLOSE_DELAY;
	}

	public boolean checkPlayerSensorAutoCloseDelay() {
		long time = System.currentTimeMillis();
		long diff = time - this.lastStateChangeTime;
		return diff > BlockTGDoor3x3.DOOR_OPEN_TIME;
	}

	/**
	 * Send out timing to clients
	 */
	public void changeStateServerSide() {
		if (this.level != null && !this.level.isClientSide) {
			TGPackets.network.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)), new PacketDoorStateChange(this));
		}
	}

	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if (id == BUTTON_ID_AUTOCLOSE) {
			if (this.isUseableByPlayer(ply)) {
				this.setAutoClose(!this.autoClose);
				this.needUpdate();
			}
		} else if (id == BUTTON_ID_DOORMODE) {
			if (this.isUseableByPlayer(ply)) {
				byte mode = (byte) (this.doormode + 1);
				if (mode > 2) {
					mode = 0;
				}
				this.setDoormode(mode);
				this.needUpdate();
			}
		} else {
			super.buttonClicked(id, ply, data);
		}
	}

	public byte getDoormode() {
		return doormode;
	}

	public void setDoormode(byte doormode) {
		this.doormode = doormode;
		if (this.level != null && !this.level.isClientSide && (this.doormode == 2 || this.doormode == 0)) {
			this.level.scheduleTick(getBlockPos(), getBlockState().getBlock(), BlockTGDoor3x3.BLOCK_UPDATE_DELAY);
		}
	}

	public boolean isRedstoneMode() {
		return this.doormode == 1;
	}

	// ---- NBT ----

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		if (!tags.contains("owner")) {
			this.owner = null;
		}
		this.autoClose = !tags.contains("autoClose") || tags.getBoolean("autoClose");
		this.doormode = tags.getByte("doormode");
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putByte("doortype", (byte) this.getDoorType());
		tags.putBoolean("autoClose", this.autoClose);
		tags.putByte("doormode", this.doormode);
	}
}
