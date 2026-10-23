package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.tileentities.ExplosiveChargeTileEnt;

/**
 * Container of both explosive charges (no slots, the settings are synced)
 */
public class ExplosiveChargeContainer extends OwnedTileContainer {
	protected ExplosiveChargeTileEnt tile;

	/**
	 * client constructor, the block position is sent by the server
	 */
	public ExplosiveChargeContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<ExplosiveChargeTileEnt>getClientTile(player, buf));
	}

	public ExplosiveChargeContainer(int id, Inventory player, ExplosiveChargeTileEnt tile) {
		super(TGMachines.EXPLOSIVE_CHARGE_CONTAINER, id, player, tile);
		this.tile = tile;
		this.addSyncedShort(() -> this.tile.getBlastradius(), v -> this.tile.setBlastradius((short) v));
		this.addSyncedShort(() -> this.tile.getBlastlength(), v -> this.tile.setBlastlength((short) v));
		this.addSyncedShort(() -> this.tile.getFusetime(), v -> this.tile.setFusetime((short) v));
	}

	public ExplosiveChargeTileEnt getCharge() {
		return tile;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotid) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return tile.isUseableByPlayer(player);
	}

}
