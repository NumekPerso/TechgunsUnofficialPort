package techguns.gui.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import techguns.tileentities.BasicMachineTileEnt;

public abstract class BasicMachineContainer extends RedstoneTileContainer {

	protected BasicMachineTileEnt tile;

	public BasicMachineContainer(MenuType<?> type, int id, Inventory player, BasicMachineTileEnt ent) {
		super(type, id, player, ent);
		this.tile = ent;
		//1.12 FIELD_SYNC_ID_PROGRESS, FIELD_SYNC_ID_TOTALTIME, FIELD_SYNC_ID_POWER_STORED
		this.addSyncedInt(() -> this.tile.progress, v -> this.tile.progress = v);
		this.addSyncedInt(() -> this.tile.totaltime, v -> this.tile.totaltime = v);
		this.addSyncedInt(() -> this.tile.getEnergyStorage().getEnergyStored(), v -> this.tile.getEnergyStorage().setEnergyStored(v));
	}

}
