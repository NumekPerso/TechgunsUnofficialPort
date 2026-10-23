package techguns.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.guns.GunHandType;
import techguns.api.guns.IGenericGun;
import techguns.capabilities.TGExtendedPlayer;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.packets.PacketEntityDeathType;
import techguns.packets.PacketNotifyAmbientEffectChange;
import techguns.packets.PacketSwapWeapon;
import techguns.packets.PacketTGExtendedPlayerSync;

/**
 * Common (both sides) event handlers of the gun system, ported from the 1.12 TGEventHandler and TGTickHandler.
 * Armor related handlers live in the armor package.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID)
public class TGEventHandler {

	protected static boolean allowOffhandUse(Player player, InteractionHand hand) {
		if (hand == InteractionHand.MAIN_HAND) return true;
		if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof IGenericGun) {
			IGenericGun g = (IGenericGun) player.getMainHandItem().getItem();
			if (g.getGunHandType() == GunHandType.TWO_HANDED) {
				return false;
			}
		}
		if (!player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() instanceof IGenericGun) {
			IGenericGun g = (IGenericGun) player.getOffhandItem().getItem();
			if (g.getGunHandType() == GunHandType.TWO_HANDED) {
				return false;
			}
		}
		return true;
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void rightClickEvent(PlayerInteractEvent.RightClickItem event) {
		if (!allowOffhandUse(event.getEntity(), event.getHand())) {
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.PASS);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void rightClickEvent(PlayerInteractEvent.RightClickBlock event) {
		boolean cancel = !allowOffhandUse(event.getEntity(), event.getHand());
		if (cancel) {
			event.setCanceled(true);
			event.setUseBlock(Result.ALLOW);
			event.setUseItem(Result.DENY);
			event.setCancellationResult(InteractionResult.PASS);
		} else if (event.getHand() == InteractionHand.MAIN_HAND) {
			Player ply = event.getEntity();
			if (ply.isShiftKeyDown() && !ply.getOffhandItem().isEmpty() && (ply.getOffhandItem().getItem() instanceof GenericGun) && (!event.getItemStack().isEmpty() && !(event.getItemStack().getItem() instanceof GenericGun))) {
				event.setUseBlock(Result.ALLOW);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void rightClickEvent(PlayerInteractEvent.EntityInteract event) {
		if (!allowOffhandUse(event.getEntity(), event.getHand())) {
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.PASS);
		}
	}

	@SubscribeEvent
	public static void onLivingDeathEvent(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (!entity.level().isClientSide) {

			if (entity instanceof Player) {
				TGExtendedPlayer tgplayer = TGExtendedPlayer.get((Player) entity);
				if (tgplayer != null) {
					tgplayer.foodleft = 0;
					tgplayer.lastSaturation = 0;
				}
			}

			if (event.getSource() instanceof TGDamageSource tgs) {
				if (tgs.deathType != DeathType.DEFAULT) {
					if (Math.random() < tgs.goreChance) {
						if (EntityDeathUtils.hasSpecialDeathAnim(entity, tgs.deathType)) {
							TGPackets.network.sendToAllAround(new PacketEntityDeathType(entity, tgs.deathType), TGPackets.targetPointAroundEnt(entity, 100.0f));
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer ply) {
			TGExtendedPlayer props = TGExtendedPlayer.get(ply);
			if (props != null) {
				TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(ply, props), ply);
			}
		}
	}

	@SubscribeEvent
	public static void onItemSwitch(LivingEquipmentChangeEvent event) {
		if (event.getSlot() == EquipmentSlot.MAINHAND || event.getSlot() == EquipmentSlot.OFFHAND) {
			if (event.getEntity().level().isClientSide) {
				return;
			}
			boolean fromEffect = false;
			boolean toEffect = false;
			if (!event.getTo().isEmpty() && event.getTo().getItem() instanceof GenericGun) {
				if (((GenericGun) event.getTo().getItem()).hasAmbientEffect()) {
					toEffect = true;
				}
			}
			if (!event.getFrom().isEmpty() && event.getFrom().getItem() instanceof GenericGun) {
				if (((GenericGun) event.getFrom().getItem()).hasAmbientEffect()) {
					fromEffect = true;
				}
			}

			if (fromEffect || toEffect) {
				InteractionHand hand = event.getSlot() == EquipmentSlot.OFFHAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
				TGPackets.network.sendToDimension(new PacketNotifyAmbientEffectChange(event.getEntity(), hand), event.getEntity().level().dimension());
			}
		}
	}

	/**
	 * Gun related part of the 1.12 TGTickHandler.onPlayerTick, armor bonuses are handled in the armor package
	 */
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		TGExtendedPlayer props = TGExtendedPlayer.get(event.player);
		if (props == null) {
			return;
		}
		if (event.phase == TickEvent.Phase.START) {
			//reduce fire delays on both sides
			if (props.fireDelayMainhand > 0) {
				props.fireDelayMainhand--;
			}
			if (props.loopSoundDelayMainhand > 0) {
				props.loopSoundDelayMainhand--;
			}
			if (props.fireDelayOffhand > 0) {
				props.fireDelayOffhand--;
			}
			if (props.loopSoundDelayOffhand > 0) {
				props.loopSoundDelayOffhand--;
			}

			if (!event.player.level().isClientSide) {
				if (props.swingSoundDelay > 0) {
					props.swingSoundDelay--;
				}
			}
		} else if (event.phase == TickEvent.Phase.END) {
			/**
			 * detect weapon swaps
			 */
			if (!event.player.level().isClientSide) { //does only work server side
				ItemStack gunMH = ItemStack.EMPTY;
				ItemStack gunOH = ItemStack.EMPTY;
				if (!event.player.getMainHandItem().isEmpty() && event.player.getMainHandItem().getItem() instanceof GenericGun) {
					gunMH = event.player.getMainHandItem();
				}
				if (!event.player.getOffhandItem().isEmpty() && event.player.getOffhandItem().getItem() instanceof GenericGun) {
					gunOH = event.player.getOffhandItem();
				}

				if (!gunOH.isEmpty() && props.gunMainHand == gunOH || !gunMH.isEmpty() && props.gunOffHand == gunMH) {
					TGPackets.network.sendToAllAround(new PacketSwapWeapon(event.player), TGPackets.targetPointAroundEnt(event.player, 50.0f));
					int i = props.fireDelayMainhand;
					props.fireDelayMainhand = props.fireDelayOffhand;
					props.fireDelayOffhand = i;
				}

				props.gunMainHand = gunMH;
				props.gunOffHand = gunOH;
			}
		}
	}
}
