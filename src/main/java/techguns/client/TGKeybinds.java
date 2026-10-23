package techguns.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import techguns.TGPackets;
import techguns.api.guns.GunManager;
import techguns.capabilities.TGExtendedPlayer;
import techguns.items.guns.GenericGun;
import techguns.keybind.TGKeybindsID;
import techguns.packets.PacketTGKeybindPress;
import techguns.util.InventoryUtil;

public class TGKeybinds {

	public static final String CATEGORY = "techguns.key.categories.techguns";

	public static final KeyMapping KEY_TOGGLE_NIGHTVISION = new KeyMapping("techguns.key.toggleNightvision", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
	public static final KeyMapping KEY_TOGGLE_SAFEMODE = new KeyMapping("techguns.key.toggleSafemode", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);
	public static final KeyMapping KEY_TOGGLE_STEPASSIST = new KeyMapping("techguns.key.toggleStepassist", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
	public static final KeyMapping KEY_FORCE_RELOAD = new KeyMapping("techguns.key.forceReload", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);

	public static void register(RegisterKeyMappingsEvent event) {
		event.register(KEY_TOGGLE_NIGHTVISION);
		event.register(KEY_TOGGLE_SAFEMODE);
		event.register(KEY_TOGGLE_STEPASSIST);
		event.register(KEY_FORCE_RELOAD);
	}

	/**
	 * Called every client tick
	 */
	public static void handleKeys() {
		while (KEY_TOGGLE_NIGHTVISION.consumeClick()) {
			TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.TOGGLE_NIGHTVISION, true));
		}
		while (KEY_TOGGLE_SAFEMODE.consumeClick()) {
			TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.TOGGLE_SAFEMODE, true));
		}
		while (KEY_TOGGLE_STEPASSIST.consumeClick()) {
			TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.TOGGLE_STEP_ASSIST, true));
		}
		while (KEY_FORCE_RELOAD.consumeClick()) {
			forceReload();
		}
	}

	protected static void forceReload() {
		Player ply = Minecraft.getInstance().player;
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props != null) {
			ItemStack stack_main = ply.getMainHandItem();
			ItemStack stack_off = ply.getOffhandItem();

			boolean canReloadMainhand = canReloadGun(props, ply, stack_main, InteractionHand.MAIN_HAND);
			boolean canReloadOffhand = GunManager.canUseOffhand(stack_main, stack_off, ply) && canReloadGun(props, ply, stack_off, InteractionHand.OFF_HAND);

			if (canReloadMainhand || canReloadOffhand) {
				InteractionHand hand;
				ItemStack gunToReload;
				if (!canReloadOffhand) { //only mainhand can be reloaded
					hand = InteractionHand.MAIN_HAND;
					gunToReload = stack_main;
				} else if (!canReloadMainhand) { //only offhand can be reloaded
					hand = InteractionHand.OFF_HAND;
					gunToReload = stack_off;
				} else { //both can be reloaded
					GenericGun gunMain = (GenericGun) stack_main.getItem();
					GenericGun gunOff = (GenericGun) stack_off.getItem();
					double ammoPercent = gunMain.getPercentAmmoLeft(stack_main);
					double ammoPercent_off = gunOff.getPercentAmmoLeft(stack_off);
					if (ammoPercent_off < ammoPercent) {
						hand = InteractionHand.OFF_HAND;
						gunToReload = stack_off;
					} else {
						hand = InteractionHand.MAIN_HAND;
						gunToReload = stack_main;
					}
				}
				TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.FORCE_RELOAD, hand));
				((GenericGun) gunToReload.getItem()).tryForcedReload(gunToReload, ply.level(), ply, hand);
			}
		}
	}

	private static boolean canReloadGun(TGExtendedPlayer props, Player ply, ItemStack stack, InteractionHand hand) {
		if (!stack.isEmpty() && stack.getItem() instanceof GenericGun) {
			GenericGun gun = (GenericGun) stack.getItem();
			if (props.getFireDelay(hand) <= 0 && !gun.isFullyLoaded(stack) && !(ply.getUseItem() == stack)) {
				//creative players reload without ammo
				if (ply.getAbilities().instabuild) {
					return true;
				}
				ItemStack[] ammo = gun.getReloadItem(stack);
				for (ItemStack s : ammo) {
					if (!InventoryUtil.canConsumeAmmoPlayer(ply, s)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}
