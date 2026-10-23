package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.tileentities.DungeonScannerTileEnt;

public class DungeonScannerContainer extends OwnedTileContainer {

	public DungeonScannerContainer(int id, Inventory inv, FriendlyByteBuf buf) {
		this(id, inv, TGBaseContainer.<DungeonScannerTileEnt>getClientTile(inv, buf));
	}

	public DungeonScannerContainer(int id, Inventory inv, DungeonScannerTileEnt ent) {
		super(TGMachines.DUNGEON_SCANNER_CONTAINER, id, inv, ent);
	}

	public DungeonScannerTileEnt getTile() {
		return (DungeonScannerTileEnt) this.tile;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}
}
