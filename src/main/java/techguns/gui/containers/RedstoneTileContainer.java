package techguns.gui.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import techguns.tileentities.BasicRedstoneTileEnt;

public abstract class RedstoneTileContainer extends OwnedTileContainer {

	BasicRedstoneTileEnt tile;

	public RedstoneTileContainer(MenuType<?> type, int id, Inventory player, BasicRedstoneTileEnt ent) {
		super(type, id, player, ent);
		this.tile = ent;
		//1.12 FIELD_SYNC_ID_REDSTONE (+ the signal state, 1.12 only synced it with tile updates)
		this.addSyncedShort(() -> this.tile.getRedstoneBehaviour(), v -> this.tile.setRedstoneBehaviour((byte) v));
		this.addSyncedShort(() -> this.tile.hasSignal() ? 1 : 0, v -> this.tile.setHasSignal(v != 0));
	}

}
