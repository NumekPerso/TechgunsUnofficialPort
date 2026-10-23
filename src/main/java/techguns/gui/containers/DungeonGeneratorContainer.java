package techguns.gui.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.tileentities.DungeonGeneratorTileEnt;

public class DungeonGeneratorContainer extends OwnedTileContainer {

	public DungeonGeneratorContainer(int id, Inventory inv, FriendlyByteBuf buf) {
		this(id, inv, TGBaseContainer.<DungeonGeneratorTileEnt>getClientTile(inv, buf));
	}

	public DungeonGeneratorContainer(int id, Inventory inv, DungeonGeneratorTileEnt ent) {
		super(TGMachines.DUNGEON_GENERATOR_CONTAINER, id, inv, ent);
	}

	public DungeonGeneratorTileEnt getTile() {
		return (DungeonGeneratorTileEnt) this.tile;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}
}
