package techguns.items.additionalslots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import techguns.api.tginventory.TGSlotType;
import techguns.capabilities.TGExtendedPlayer;

public class ItemGlider extends ItemTGSpecialSlot {

	public ItemGlider(String unlocalizedName, int camoCount, int dur) {
		super(unlocalizedName, TGSlotType.BACKSLOT, camoCount, dur);
	}

	@Override
	public void onPlayerTick(ItemStack item, PlayerTickEvent event) {
		glide(event.player);
	}

	public static void glide(Player player) {
		Player ply = player;

		if (!ply.onGround() && ply.isShiftKeyDown()) {
			Vec3 motion = player.getDeltaMovement();
			if (motion.y < -0.1f) {

				TGExtendedPlayer extendedPlayer = TGExtendedPlayer.get(ply);
				extendedPlayer.isGliding = true;

				/**
				 * totally stolen from modular powersuits glider code
				 */
				Vec3 lookVec = player.getLookAngle();
				double moveX = lookVec.x;
				double moveZ = lookVec.z;

				double motionAdd = Math.min(0.08, -0.1 - motion.y);
				player.setDeltaMovement(motion.x + moveX * motionAdd, motion.y + motionAdd, motion.z + moveZ * motionAdd);

				//1.12 player.jumpMovementFactor += 0.03f (more air control), the 1.20 air speed can't be changed: apply the extra acceleration
				player.moveRelative(0.03f, new Vec3(player.xxa, 0, player.zza));
			}
			player.fallDistance = 0;
		}
	}

}
