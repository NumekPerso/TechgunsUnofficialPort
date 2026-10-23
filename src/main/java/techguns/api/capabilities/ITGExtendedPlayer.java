package techguns.api.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * Should only be used on Player
 */
public interface ITGExtendedPlayer extends ITGShooterValues {
	public Player getEntity();

	public int getFireDelay(InteractionHand hand);

	public void setFireDelay(InteractionHand hand, int delay);

	public Container getTGInventory();

	public void saveToNBT(final CompoundTag tags);

	public void loadFromNBT(final CompoundTag tags);
}
