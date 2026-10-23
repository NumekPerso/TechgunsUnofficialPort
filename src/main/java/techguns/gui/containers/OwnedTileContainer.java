package techguns.gui.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import techguns.tileentities.BasicOwnedTileEnt;

public abstract class OwnedTileContainer extends TGBaseContainer {

	protected BasicOwnedTileEnt tile;

	public OwnedTileContainer(MenuType<?> type, int id, Inventory player, BasicOwnedTileEnt ent) {
		super(type, id, player, ent);
		this.tile = ent;
		//1.12 FIELD_SYNC_ID_SECURITY
		this.addSyncedShort(() -> this.tile.getSecurity(), v -> this.tile.setSecurity((byte) v));
	}

}
