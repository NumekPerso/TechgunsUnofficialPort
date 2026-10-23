package techguns.tileentities;

import static techguns.gui.ButtonConstants.BUTTON_ID_REDSTONE;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BasicRedstoneTileEnt extends BasicOwnedTileEnt {

	/**
	 * 0-ignore
	 * 1-high
	 * 2-low
	 */
	protected byte redstoneBehaviour = 0;

	public BasicRedstoneTileEnt(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, boolean hasRotation) {
		super(type, pos, state, inventorySize, hasRotation);
	}

	/**
	 * has currently a redstone signal?
	 */
	protected boolean hasSignal = false;

	public void onNeighborBlockChange() {
		if (!this.level.isClientSide) {
			int signal_str = this.level.getBestNeighborSignal(this.worldPosition);
			boolean signal = signal_str > 0;
			if (signal != hasSignal) {
				hasSignal = signal;
				this.needUpdate();
			}
		}
	}

	/**
	 * Returns if this tileent should operate depending on current redstone signal and setting
	 */
	public boolean isRedstoneEnabled() {
		return this.redstoneBehaviour == 0 || (this.hasSignal && this.redstoneBehaviour == 1) || (!this.hasSignal && this.redstoneBehaviour == 2);
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		this.hasSignal = tags.getBoolean("hasSignal");
		this.redstoneBehaviour = tags.getByte("redstoneBehaviour");
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putBoolean("hasSignal", this.hasSignal);
		tags.putByte("redstoneBehaviour", this.redstoneBehaviour);
	}

	/**
	 * translation key, used by the GUI
	 */
	public String getRedstoneModeText() {
		switch (this.redstoneBehaviour) {
		case 0:
			return "techguns.redstonemode.ignore";
		case 1:
			return "techguns.redstonemode.high";
		case 2:
			return "techguns.redstonemode.low";
		}
		return "invalid";
	}

	/**
	 * translation key, used by the GUI
	 */
	public String getSignalStateText() {
		if (this.hasSignal) {
			return "techguns.redstonemode.signal.high";
		} else {
			return "techguns.redstonemode.signal.low";
		}
	}

	/**
	 * 0-ignore
	 * 1-high
	 * 2-low
	 */
	public byte getRedstoneBehaviour() {
		return redstoneBehaviour;
	}

	public void setRedstoneBehaviour(byte redstoneBehaviour) {
		this.redstoneBehaviour = redstoneBehaviour;
	}

	public boolean hasSignal() {
		return hasSignal;
	}

	public void setHasSignal(boolean hasSignal) {
		this.hasSignal = hasSignal;
	}

	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if ((id == BUTTON_ID_REDSTONE) && (this.isUseableByPlayer(ply))) {
			this.redstoneBehaviour++;
			this.redstoneBehaviour = (byte) (this.redstoneBehaviour % 3);
			if (!this.level.isClientSide) {
				this.needUpdate();
			}
		}
		super.buttonClicked(id, ply, data);
	}
}
