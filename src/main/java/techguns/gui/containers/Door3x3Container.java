package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import techguns.TGMachines;
import techguns.tileentities.Door3x3TileEntity;

/**
 * Configuration GUI of the 3x3 doors (no slots)
 */
public class Door3x3Container extends OwnedTileContainer {

	Door3x3TileEntity door;

	public Door3x3Container(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<Door3x3TileEntity>getClientTile(player, buf));
	}

	public Door3x3Container(int id, Inventory player, Door3x3TileEntity ent) {
		super(TGMachines.DOOR3x3_CONTAINER, id, player, ent);
		this.door = ent;
		//1.12 FIELD_SYNC_ID_DOORMODE, FIELD_SYNC_ID_AUTCLOSE
		this.addSyncedShort(() -> this.door.getDoormode(), v -> this.door.setDoormode((byte) v));
		this.addSyncedShort(() -> this.door.isAutoClose() ? 1 : 0, v -> this.door.setAutoClose(v > 0));
	}

	public Door3x3TileEntity getDoor() {
		return door;
	}

}
