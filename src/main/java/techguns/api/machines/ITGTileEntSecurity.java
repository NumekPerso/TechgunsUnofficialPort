package techguns.api.machines;

import java.util.UUID;

import net.minecraft.world.entity.player.Player;

public interface ITGTileEntSecurity {
	public void setOwner(Player ply);

	public boolean isOwnedByPlayer(Player ply);

	public UUID getOwner();

	/**
	 * 0 - everyone
	 * 1 - friends if FTButilities installed, otherwise like 0
	 * 2 - owner only
	 * @return
	 */
	public byte getSecurity();
}
