package techguns.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.TGConfig;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.guns.GunManager;
import techguns.api.guns.IGenericGun;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.particle.TGFXHandler;
import techguns.client.render.ItemRenderHack;
import techguns.items.guns.GenericGun;
import techguns.packets.PacketShootGun;
import techguns.packets.PacketShootGunTarget;

/**
 * Client side input / tick / render events, ported from the 1.12 TGEventHandler.onMouseEvent, TGTickHandler and
 * handleFovEvent.
 *
 * 1.12 captured the raw mouse button events, 1.20 cancels the attack / use key interactions and polls the key state.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID, value = Dist.CLIENT)
public class TGClientEvents {

	private static boolean lastAttackDown = false;
	private static boolean lastUseDown = false;

	protected static boolean isGunShootingWithLeftClick(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof IGenericGun && ((IGenericGun) stack.getItem()).isShootWithLeftClick();
	}

	/**
	 * Offhand guns are fired with the use key, if the player is not sneaking and the mainhand allows it
	 */
	protected static boolean isOffhandGunFiring(Player ply) {
		return !ply.isShiftKeyDown() && isGunShootingWithLeftClick(ply.getOffhandItem()) && GunManager.canUseOffhand(ply);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer ply = mc.player;
		if (ply == null) {
			return;
		}

		if (event.isAttack()) {
			ItemStack main = ply.getMainHandItem();
			if (isGunShootingWithLeftClick(main)) {
				//guns don't attack or mine
				event.setCanceled(true);
				event.setSwingHand(false);
			} else if (!main.isEmpty() && main.getItem() instanceof IGenericGun) {
				//can't mine/attack while reloading
				long diff = ShooterValues.getReloadtime(ply, false) - System.currentTimeMillis();
				if (ShooterValues.getReloadtime(ply, false) > 0 && diff > 0) {
					event.setCanceled(true);
					event.setSwingHand(false);
				}
			}
		} else if (event.isUseItem()) {
			if (isOffhandGunFiring(ply)) {
				event.setCanceled(true);
				event.setSwingHand(false);
			} else if (!ply.getMainHandItem().isEmpty() && ply.getMainHandItem().getItem() instanceof GenericGun gun && gun.getLockOnTicks() > 0) {
				//Lock On Weapon: (re)start the lock
				TGExtendedPlayer props = TGExtendedPlayer.get(ply);
				if (props != null) {
					props.lockOnEntity = null;
					props.lockOnTicks = -1;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (event.phase == TickEvent.Phase.START) {
			LocalPlayer ply = mc.player;
			if (ply == null) {
				return;
			}
			ClientProxy cp = ClientProxy.get();

			TGKeybinds.handleKeys();

			boolean inGame = mc.screen == null && mc.mouseHandler.isMouseGrabbed() && !ply.isSpectator();
			boolean attackDown = inGame && mc.options.keyAttack.isDown();
			boolean useDown = inGame && mc.options.keyUse.isDown();

			//button press like in 1.12 MouseEvent: pressed sets the flag, released clears it
			if (attackDown && !lastAttackDown && isGunShootingWithLeftClick(ply.getMainHandItem())) {
				cp.keyFirePressedMainhand = true;
			} else if (!attackDown) {
				cp.keyFirePressedMainhand = false;
			}

			if (useDown && !lastUseDown && isOffhandGunFiring(ply)) {
				cp.keyFirePressedOffhand = true;
			} else if (!useDown) {
				cp.keyFirePressedOffhand = false;
			}

			lastAttackDown = attackDown;
			lastUseDown = useDown;

		} else {
			TGFXHandler.clientTick();
		}
	}

	/**
	 * Port of the 1.12 TGTickHandler.localClientPlayerTick: fires the guns of the local player
	 */
	@SubscribeEvent
	public static void localClientPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.START || !event.player.level().isClientSide) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		ClientProxy cp = ClientProxy.get();

		// ONLY DO FOR OWN PLAYER!
		if (event.player != mc.player) {
			return;
		}
		TGExtendedPlayer props = TGExtendedPlayer.get(event.player);
		if (props == null) {
			return;
		}

		if (mc.screen == null && !event.player.isSpectator()) {

			ItemStack stack = event.player.getMainHandItem();
			ItemStack stackOff = event.player.getOffhandItem();
			if (isGunShootingWithLeftClick(stack)) {
				if (cp.keyFirePressedMainhand) {
					IGenericGun gun = (IGenericGun) stack.getItem();
					if (props.getFireDelay(InteractionHand.MAIN_HAND) <= 0) {
						if (gun instanceof GenericGun g && g.getLockOnTicks() > 0 && props.lockOnEntity != null && props.lockOnTicks > g.getLockOnTicks()) {
							TGPackets.network.sendToServer(new PacketShootGunTarget(gun.isZooming(), InteractionHand.MAIN_HAND, props.lockOnEntity));
							gun.shootGunPrimary(stack, event.player.level(), event.player, gun.isZooming(), InteractionHand.MAIN_HAND, props.lockOnEntity);
						} else {
							TGPackets.network.sendToServer(new PacketShootGun(gun.isZooming(), InteractionHand.MAIN_HAND));
							gun.shootGunPrimary(stack, event.player.level(), event.player, gun.isZooming(), InteractionHand.MAIN_HAND, null);
						}
					}
					if (gun.isSemiAuto()) {
						cp.keyFirePressedMainhand = false;
					}
				}
			} else {
				cp.keyFirePressedMainhand = false;
			}

			if (isGunShootingWithLeftClick(stackOff)) {
				if (cp.keyFirePressedOffhand) {
					IGenericGun gun = (IGenericGun) stackOff.getItem();
					if (props.getFireDelay(InteractionHand.OFF_HAND) <= 0) {
						TGPackets.network.sendToServer(new PacketShootGun(gun.isZooming(), InteractionHand.OFF_HAND));
						gun.shootGunPrimary(stackOff, event.player.level(), event.player, gun.isZooming(), InteractionHand.OFF_HAND, null);
					}
					if (gun.isSemiAuto()) {
						cp.keyFirePressedOffhand = false;
					}
				}
			} else {
				cp.keyFirePressedOffhand = false;
			}

			//Reset lock if out of ammo
			if (!stack.isEmpty() && stack.getItem() instanceof GenericGun g && g.getLockOnTicks() > 0) {
				if (g.getAmmoLeft(stack) <= 0 && props.lockOnEntity != null) {
					props.lockOnEntity = null;
					props.lockOnTicks = -1;
				}
			}
		} else {
			cp.keyFirePressedMainhand = false;
			cp.keyFirePressedOffhand = false;
		}
	}

	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			ClientProxy cp = ClientProxy.get();
			cp.PARTIAL_TICK_TIME = event.renderTickTime;
			Player player = cp.getPlayerClient();
			if (player != null) {
				ItemStack current_item = player.getMainHandItem();
				if (!current_item.isEmpty() && current_item.getItem() instanceof IGenericGun gun) {
					if (gun.isHoldZoom()) {
						if (player.isShiftKeyDown()) {
							cp.player_zoom = gun.getZoomMult();
						} else {
							cp.player_zoom = 1.0f;
						}
					} else {
						if (!gun.isZooming()) {
							cp.player_zoom = 1.0f;
						}
					}
				} else {
					cp.player_zoom = 1.0f;
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void handleFovEvent(ComputeFovModifierEvent event) {
		float f = 1.0f;
		Player ply = event.getPlayer();
		if (TGConfig.cl_lockSpeedFov) {
			AttributeInstance iattributeinstance = ply.getAttribute(Attributes.MOVEMENT_SPEED);
			if (iattributeinstance != null && ply.getAbilities().getWalkingSpeed() != 0f) {
				f = 1 / ((float) (((iattributeinstance.getValue() / (double) ply.getAbilities().getWalkingSpeed() + 1.0D) / 2.0D)));
			}
			if (ply.isSprinting()) {
				f *= TGConfig.cl_fixedSprintFov;
			}
		}
		event.setNewFovModifier(event.getNewFovModifier() * ClientProxy.get().player_zoom * f);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
		ItemRenderHack.setCurrentRenderedEntity(event.getEntity());
		TGFXHandler.onRenderLivingPre(event);
	}

	@SubscribeEvent
	public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
		ItemRenderHack.setCurrentRenderedEntity(null);
	}

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			TGFXHandler.renderWorldParticles(event);
		}
	}
}
