package techguns.tileentities;

import static techguns.gui.ButtonConstants.BUTTON_ID_SECURITY;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import techguns.api.machines.ITGTileEntSecurity;

public class BasicOwnedTileEnt extends BasicInventoryTileEnt implements ITGTileEntSecurity {

	/**
	 * 1.12 FTBLib team support, not available in the port: security toggles between public (0) and owner only (3)
	 */
	public static final boolean FTBLIB_ENABLED = false;

	public BasicOwnedTileEnt(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, boolean hasRotation) {
		super(type, pos, state, inventorySize, hasRotation);
	}

	/**
	 * Accessible by:
	 *  0 - anyone
	 *  1 - allies of team with FTBU, else anyone
	 *  2 - team with FTBU, else anyone
	 *  3 - restricted to owner
	 */
	protected byte security = 0;

	protected UUID owner;

	/**
	 * Sets the owner of this tileent on placing
	 *
	 * @param ply
	 */
	@Override
	public void setOwner(Player ply) {
		UUID id = ply.getGameProfile().getId();
		if (id != null) {
			this.owner = id;
			this.security = 0;
		}
	}

	@Override
	public boolean isOwnedByPlayer(Player ply) {
		if (this.owner == null) {
			return false;
		}
		return this.owner.equals(ply.getGameProfile().getId());
	}

	@Override
	public UUID getOwner() {
		return this.owner;
	}

	/**
	 * 0 - everyone 1 - friends if FTButilities installed, otherwise like 0 2 -
	 * owner only
	 *
	 * @return
	 */
	@Override
	public byte getSecurity() {
		return this.security;
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);

		this.security = tags.getByte("security");

		String own = tags.getString("owner");
		if (own != null && !own.equals("")) {
			try {
				this.owner = UUID.fromString(own);
			} catch (IllegalArgumentException e) {
				this.owner = null;
			}
		}
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		if (this.owner != null) {
			tags.putByte("security", this.security);
			tags.putString("owner", this.owner.toString());
		}
	}

	@Override
	public void writeNBTforDismantling(CompoundTag compound) {
		super.writeNBTforDismantling(compound);
		compound.remove("owner");
	}

	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if (id == BUTTON_ID_SECURITY && (this.isOwnedByPlayer(ply))) {

			if (FTBLIB_ENABLED) {
				this.security++;
				this.security = (byte) (this.security % 4);
			} else {
				if (this.security == 0) {
					this.security = 3;
				} else {
					this.security = 0;
				}
			}
			if (!this.level.isClientSide) {
				this.needUpdate();
			}
		} else {
			super.buttonClicked(id, ply, data);
		}
	}

	@Override
	public boolean isUseableByPlayer(Player player) {
		if (super.isUseableByPlayer(player)) {
			if (this.owner == null || this.security == 0) {
				return true;
			} else if (this.isOwnedByPlayer(player)) {
				return true;
			} else {
				//1.12 TGNpcFactions.canAccess: levels 1/2 need FTBLib teams (not available), 3 is owner only
				return false;
			}
		}
		return false;
	}

	public void setSecurity(byte security) {
		this.security = security;
	}
}
