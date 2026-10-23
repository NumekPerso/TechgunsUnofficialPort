package techguns.capabilities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.Techguns;

@Mod.EventBusSubscriber(modid = Techguns.MODID)
public class CapabilityEventHandler {

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(TGExtendedPlayer.class);
	}

	@SubscribeEvent
	public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player ply) {
			TGExtendedPlayer cap = Techguns.proxy.createExtendedPlayer(ply);
			TGExtendedPlayerCapProvider provider = new TGExtendedPlayerCapProvider(cap);
			event.addCapability(TGExtendedPlayerCapProvider.ID, provider);
		}
	}

	/**
	 * Copy the player's caps when they respawn after dying or dimension change.
	 */
	@SubscribeEvent
	public static void playerClone(final PlayerEvent.Clone event) {
		event.getOriginal().reviveCaps();
		final TGExtendedPlayer oldValues = TGExtendedPlayer.get(event.getOriginal());
		final TGExtendedPlayer newValues = TGExtendedPlayer.get(event.getEntity());
		if (oldValues != null && newValues != null) {
			newValues.copyFrom(oldValues);
		}
		event.getOriginal().invalidateCaps();
	}
}
