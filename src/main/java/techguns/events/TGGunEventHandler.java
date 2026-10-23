package techguns.events;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.Techguns;
import techguns.items.guns.GenericGunMeleeCharge;
import techguns.items.guns.MiningDrill;
import techguns.util.BlockUtils;

/**
 * Gun related common events of the 1.12 TGEventHandler (mining tools).
 * The 1.12 charging state handling of TGTickHandler is not needed: TGExtendedPlayer.isChargingWeapon() is derived from the item use state.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID)
public class TGGunEventHandler {

	/**
	 * Mining radius: the break speed is reduced to the speed of the hardest block of the mining plane
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBreakEventHigh(PlayerEvent.BreakSpeed event) {
		Player ply = event.getEntity();
		ItemStack item = ply.getMainHandItem();
		if (!item.isEmpty() && item.getItem() instanceof GenericGunMeleeCharge g && event.getPosition().isPresent()) {
			if (g.getMiningRadius(item) > 0) {
				Direction sidehit = g.getSideHitMining(ply.level(), ply);

				if (sidehit != null) {
					BlockPos pos = event.getPosition().get();
					BlockState state = event.getState();
					float mainHardness = state.getDestroySpeed(ply.level(), pos);

					List<BlockPos> blocks = BlockUtils.getBlockPlaneAroundAxisForMining(ply.level(), ply, pos, sidehit.getAxis(), g.getMiningRadius(item), false, g, item);
					float maxHardness = 0f;
					for (BlockPos p : blocks) {
						BlockState s = ply.level().getBlockState(p);
						float h = s.getDestroySpeed(ply.level(), p);
						if (h > maxHardness) {
							maxHardness = h;
						}
					}

					if (maxHardness > mainHardness) {
						event.setNewSpeed(event.getNewSpeed() * mainHardness / maxHardness);
					}
				}
			}
		}
	}

	/**
	 * stop XP drop on silk harvesting with mining drill
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
		Player ply = event.getPlayer();
		if (ply != null) {
			ItemStack stack = ply.getMainHandItem();
			if (!stack.isEmpty() && stack.getItem() instanceof MiningDrill && ply.isShiftKeyDown()) {
				event.setExpToDrop(0);
			}
		}
	}
}
