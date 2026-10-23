package techguns.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.Techguns;
import techguns.api.guns.GunHandType;
import techguns.api.guns.GunManager;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.render.ItemRenderHack;
import techguns.client.render.item.IItemRenderer;
import techguns.client.render.item.RenderGunBase;
import techguns.items.guns.EnumCrosshairStyle;
import techguns.items.guns.GenericGun;
import techguns.util.InventoryUtil;
import techguns.util.MathUtil;

/**
 * Port of the 1.12 TGGuiEvents: ammo counter / status icons and the Techguns crosshairs
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID, value = Dist.CLIENT)
public class TGHud {

	public static final ResourceLocation crosshairs_texture = new ResourceLocation(Techguns.MODID, "textures/gui/crosshairs.png");
	public static final ResourceLocation tg_crosshairs_texture = new ResourceLocation(Techguns.MODID, "textures/gui/tg_crosshairs.png");
	public static final ResourceLocation hud_texture = new ResourceLocation(Techguns.MODID, "textures/gui/tgplayerinventory.png");

	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "techguns_hud", TGHud::renderHud);
	}

	public static void renderHud(ForgeGui gui, GuiGraphics g, float partialTick, int screenWidth, int screenHeight) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer ply = mc.player;
		if (ply == null || ply.isSpectator() || mc.options.hideGui) return;

		TGExtendedPlayer props = TGExtendedPlayer.get(ply);

		ItemStack item = ply.getMainHandItem();
		ItemStack item_off = ply.getOffhandItem();

		int offsetY = screenHeight - 32;

		boolean showSafemode = false;
		if (!item.isEmpty() && item.getItem() instanceof GenericGun) {
			GenericGun gun = ((GenericGun) item.getItem());
			drawGunAmmoCount(mc, g, screenWidth, screenHeight, gun, item, ply, 0);
			showSafemode = true;
		}
		if (!item_off.isEmpty() && item_off.getItem() instanceof GenericGun) {
			if (GunManager.canUseOffhand(item, item_off, ply)) {
				GenericGun gun = ((GenericGun) item_off.getItem());
				drawGunAmmoCount(mc, g, screenWidth, screenHeight, gun, item_off, ply, -12);
				showSafemode = true;
			}
		}

		if (props != null && props.showTGHudElements && showSafemode) {
			g.blit(hud_texture, screenWidth - 10, offsetY, 242 + 7 * (props.enableSafemode ? 1 : 0), 14, 7, 7);
		}

		if (props != null && props.showTGHudElements) {
			offsetY -= 10;

			offsetY = TGArmorHud.render(g, ply, props, screenWidth, offsetY);

			offsetY = TGAdditionalSlotsHud.render(g, ply, props, screenWidth, offsetY);

			if (Techguns.proxy.getHasNightvision()) {
				g.blit(hud_texture, screenWidth - 10, offsetY, 242 + 7 * (props.enableNightVision ? 1 : 0), 7, 7, 7);
				offsetY -= 10;
			}

			if (Techguns.proxy.getHasStepassist()) {
				g.blit(hud_texture, screenWidth - 10, offsetY, 242 + 7 * (props.enableStepAssist ? 1 : 0), 21, 7, 7);
				offsetY -= 10;
			}

			//Check armor durability
			byte mode = 0;
			for (EquipmentSlot slot : new EquipmentSlot[] { EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD }) {
				ItemStack armor = ply.getItemBySlot(slot);
				if (!armor.isEmpty() && armor.isDamageableItem()) {
					double dur = ((armor.getMaxDamage() - armor.getDamageValue()) * 1.0D) / (armor.getMaxDamage() * 1.0D);
					if (dur < 0.35D && mode < 1) {
						mode = 1;
					} else if (armor.getDamageValue() >= (armor.getMaxDamage() - 1)) {
						mode = 2;
						break;
					}
				}
			}
			if (mode == 2) {
				g.blit(hud_texture, screenWidth - 10, offsetY, 242 + 7, 28, 7, 7);
				offsetY -= 10;
			} else if (mode == 1) {
				g.blit(hud_texture, screenWidth - 10, offsetY, 242, 28, 7, 7);
				offsetY -= 10;
			}

			//lot I (radiation)
			offsetY = TGRadiationHud.render(g, ply, props, screenWidth, offsetY);
		}
	}

	private static int getAmmoCountOfStack(ItemStack ammoitem, GenericGun gun, Player ply) {
		int count = InventoryUtil.countAmmoPlayer(ply, ammoitem);
		if (gun.getAmmoCount() > 1) {
			count = count / gun.getAmmoCount();
		}
		return count;
	}

	private static void drawGunAmmoCount(Minecraft mc, GuiGraphics g, int screenWidth, int screenHeight, GenericGun gun, ItemStack item, Player ply, int offsetY) {
		ItemStack[] ammoitem = gun.getReloadItem(item);
		int minCount = 0;
		for (ItemStack stack : ammoitem) {
			int c = getAmmoCountOfStack(stack, gun, ply);
			if (c > minCount) {
				minCount = c;
			}
		}

		String text = gun.getAmmoLeftCountTooltip(item) + "/" + gun.getClipsizeTooltip() + ChatFormatting.YELLOW + "x" + minCount;
		g.drawString(mc.font, text, screenWidth + 1 - text.length() * 6, screenHeight - mc.font.lineHeight - 2 + offsetY, 0xFFFFFFFF, false);
	}

	@SubscribeEvent
	public static void onRenderCrosshairs(RenderGuiOverlayEvent.Pre event) {
		if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || !(player.getMainHandItem().getItem() instanceof GenericGun)) {
			return;
		}
		//check 1st person
		if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
			return;
		}

		GuiGraphics g = event.getGuiGraphics();
		TGExtendedPlayer epc = TGExtendedPlayer.get(player);

		int x = event.getWindow().getGuiScaledWidth() / 2;
		int y = event.getWindow().getGuiScaledHeight() / 2;

		GenericGun gun = (GenericGun) player.getMainHandItem().getItem();

		if (epc != null && gun.getLockOnTicks() > 0 && epc.lockOnEntity != null && epc.lockOnTicks > 0) {
			event.setCanceled(true);

			float maxTicks = (float) gun.getLockOnTicks();
			float progress = (float) epc.lockOnTicks / maxTicks;

			int offset = (int) (Math.max(0.0f, (1.0f - progress) * 16.0f)) + 5;

			//Outer parts
			g.blit(crosshairs_texture, x - offset - 3, y - offset - 3, 0, 0, 7, 7);
			g.blit(crosshairs_texture, x + offset - 3, y - offset - 3, 7, 0, 7, 7);
			g.blit(crosshairs_texture, x - offset - 3, y + offset - 3, 14, 0, 7, 7);
			g.blit(crosshairs_texture, x + offset - 3, y + offset - 3, 21, 0, 7, 7);

			//draw center dot
			g.blit(crosshairs_texture, x, y, 0, 0, 1, 1);

			if (progress < 1.0f) {
				String text = "Locking... : " + epc.lockOnEntity.getName().getString();
				g.drawString(mc.font, text, x + 2, y + 10, 0xFFFFFFFF, false);
			} else {
				g.blit(crosshairs_texture, x - 6, y - 6, 28, 0, 13, 13);
				if (Util.getMillis() / 250 % 2 == 0) {
					g.blit(crosshairs_texture, x - 9, y - 9, 41, 0, 19, 19);
				}
				String text = "Locked On: " + epc.lockOnEntity.getName().getString();
				g.drawString(mc.font, text, x + 2, y + 10, 0xFFFF0000, false);
			}

		} else {
			//gun crosshair
			if (gun.isZooming()) {
				IItemRenderer irenderer = ItemRenderHack.getRendererForItem(gun);
				if (irenderer instanceof RenderGunBase rgun) {
					if (rgun.hasScopeTexture()) {
						event.setCanceled(true);
						return;
					}
				}
			}

			if (gun.getCrossHairStyle() != EnumCrosshairStyle.VANILLA) {
				event.setCanceled(true);

				//Vanilla does this setting too
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

				float spread = gun.getSpread();
				if (player.isBlocking()) {
					if (gun.getHandType() == GunHandType.ONE_HANDED) {
						spread *= 4.0f;
					} else {
						spread *= 8.0f;
					}
				}
				if (gun.isZooming()) {
					spread *= gun.getZoombonus();
				}

				int spacing = MathUtil.clamp(Math.round(100 * spread), 1, 10);

				ResourceLocation t = tg_crosshairs_texture;
				if (gun.getCrossHairStyle() == EnumCrosshairStyle.GUN_DYNAMIC) {
					g.blit(t, x - (4 + spacing), y, 3, 7, 4, 1);
					g.blit(t, x + (1 + spacing), y, 8, 7, 4, 1);
					g.blit(t, x, y, 7, 7, 1, 1);
					g.blit(t, x, y - (4 + spacing), 7, 3, 1, 4);
					g.blit(t, x, y + (1 + spacing), 7, 8, 1, 4);
				} else {
					EnumCrosshairStyle crosshair = gun.getCrossHairStyle();
					int lx = crosshair.getLocX();
					int ly = crosshair.getLocY();
					switch (crosshair.dynamicType) {
					case BOTH:
						g.blit(t, x - 1, y - 1, lx + 6, ly + 6, 3, 3);
						g.blit(t, x - 6 - spacing, y - 2, lx, ly + 5, 6, 5);
						g.blit(t, x + 1 + spacing, y - 2, lx + 9, ly + 5, 6, 5);
						g.blit(t, x - 2, y - 6 - spacing, lx + 5, ly, 5, 6);
						g.blit(t, x - 2, y + 1 + spacing, lx + 5, ly + 9, 5, 6);
						break;
					case HORIZONTAL:
						g.blit(t, x - 1, y - 1, lx + 6, ly + 6, 3, 3);
						g.blit(t, x - 6 - spacing, y - 7, lx, ly, 6, 16);
						g.blit(t, x + 1 + spacing, y - 7, lx + 9, ly, 6, 16);
						break;
					case X:
						g.blit(t, x - 1, y - 1, lx + 6, ly + 6, 3, 3);
						g.blit(t, x - 6 - spacing, y - 6 - spacing, lx, ly, 6, 6);
						g.blit(t, x + 1 + spacing, y - 6 - spacing, lx + 9, ly, 6, 6);
						g.blit(t, x - 6 - spacing, y + 1 + spacing, lx, ly + 9, 6, 6);
						g.blit(t, x + 1 + spacing, y + 1 + spacing, lx + 9, ly + 9, 6, 6);
						break;
					case TRI:
						g.blit(t, x - 1, y - 1, lx + 6, ly + 6, 3, 3);
						g.blit(t, x - 7, y - 6 - spacing, lx, ly, 16, 6);
						g.blit(t, x - 6 - spacing, y + 1 + spacing, lx, ly + 9, 6, 6);
						g.blit(t, x + 1 + spacing, y + 1 + spacing, lx + 9, ly + 9, 6, 6);
						break;
					case TRI_INV:
						g.blit(t, x - 1, y - 1, lx + 6, ly + 6, 3, 3);
						g.blit(t, x - 6 - spacing, y - 6 - spacing, lx, ly, 6, 6);
						g.blit(t, x + 1 + spacing, y - 6 - spacing, lx + 9, ly, 6, 6);
						g.blit(t, x - 7, y + 1 + spacing, lx, ly + 9, 16, 6);
						break;
					case VERTICAL:
					default:
						g.blit(t, x - 7, y - 7, lx, ly, 16, 16);
						break;
					}
				}
				RenderSystem.defaultBlendFunc();
			}
		}
	}
}
