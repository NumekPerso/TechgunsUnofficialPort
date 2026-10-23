package techguns.items.additionalslots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import techguns.TGItems;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.tginventory.TGSlotType;
import techguns.capabilities.TGExtendedPlayer;
import techguns.keybind.TGKeybindsID;
import techguns.packets.PacketTGKeybindPress;

public class ItemJetpack extends ItemTGSpecialSlotAmmo {

	public ItemJetpack(String unlocalizedName, int camoCount, int dur) {
		super(unlocalizedName, TGSlotType.BACKSLOT, camoCount, dur, TGItems.FUEL_TANK, TGItems.FUEL_TANK_EMPTY);
	}

	@Override
	public void onPlayerTick(ItemStack item, PlayerTickEvent event) {

		if (item.getDamageValue() >= item.getMaxDamage()) {
			tryReloadAndRepair(item, event.player);
		}

		TGExtendedPlayer extendedPlayer = TGExtendedPlayer.get(event.player);

		if (event.player.level().isClientSide) {

			Player self = Techguns.proxy.getPlayerClient();
			if (event.player == self) {

				if (extendedPlayer.enableJetpack) {
					boolean last_state = extendedPlayer.isJumpkeyPressed();
					boolean jump_pressed = Techguns.proxy.isJumpKeyDown();

					if (jump_pressed && !last_state) {
						extendedPlayer.setJumpkeyPressed(true);
						TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.JETPACK_BOOST_START));
					} else if (!jump_pressed && last_state) {
						extendedPlayer.setJumpkeyPressed(false);
						TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.JETPACK_BOOST_STOP));
					}
				}

				if (item.getDamageValue() < item.getMaxDamage()) {

					if (extendedPlayer.isJumpkeyPressed()) {
						applyBoostToPlayer(event.player, item);
					}
					// also act like glider
					ItemGlider.glide(event.player);

				} else {
					if (extendedPlayer.isJumpkeyPressed()) {
						extendedPlayer.setJumpkeyPressed(false);
						TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.JETPACK_BOOST_STOP));
					}
				}

			}
		} else {

			// sever side
			if (item.getDamageValue() < item.getMaxDamage()) {

				if (extendedPlayer.isJumpkeyPressed()) {
					applyBoostToPlayer(event.player, item);
				}
				// also act like glider
				ItemGlider.glide(event.player);

			}

		}
	}

	protected void applyBoostToPlayer(Player ply, ItemStack item) {
		Vec3 motion = ply.getDeltaMovement();
		ply.setDeltaMovement(motion.x, Math.min(motion.y + 0.15f, 0.4f), motion.z);
		ply.fallDistance = 0;
		item.setDamageValue(item.getDamageValue() + 1);
	}

	protected void descendPlayer(Player ply, ItemStack item) {
		Vec3 motion = ply.getDeltaMovement();
		ply.setDeltaMovement(motion.x, Math.max(motion.y - 0.15f, -0.4f), motion.z);
		ply.fallDistance = 0;
		item.setDamageValue(item.getDamageValue() + 1);
	}

	protected void hoverPlayer(Player ply, ItemStack item, boolean forwardPressed) {
		Vec3 motion = ply.getDeltaMovement();
		double motionY;
		if (motion.y > 0) {
			motionY = Math.max(0, motion.y - 0.2f);
		} else {
			motionY = Math.min(0, motion.y + 0.2f);
		}
		ply.setDeltaMovement(motion.x, motionY, motion.z);
		ply.fallDistance = 0;

		item.setDamageValue(item.getDamageValue() + 1);
	}

}
