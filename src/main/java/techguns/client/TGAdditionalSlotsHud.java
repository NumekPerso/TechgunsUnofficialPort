package techguns.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGItems;
import techguns.capabilities.TGExtendedPlayer;
import techguns.gui.player.TGPlayerInventory;
import techguns.items.additionalslots.ItemTGSpecialSlotAmmo;
import techguns.util.InventoryUtil;

/**
 * Back slot HUD element (jetpack / jumppack / antigrav pack fuel and enable state), 1.12 TGGuiEvents
 */
public class TGAdditionalSlotsHud {

	public static int render(GuiGraphics g, Player ply, TGExtendedPlayer props, int screenWidth, int offsetY) {
		ItemStack backslot = props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_BACK);
		if (!backslot.isEmpty()) {
			//1.12: TODO: unhardcode this
			if (backslot.getItem() == TGItems.JETPACK || backslot.getItem() == TGItems.JUMPPACK || backslot.getItem() == TGItems.ANTI_GRAV_PACK) {
				int x = 242;
				if (props.enableJetpack) {
					x += 7;
				}
				g.blit(TGHud.hud_texture, screenWidth - 10, offsetY, x, 42, 7, 7);

				double percent = 1.0D - (backslot.getDamageValue() * 1.0f) / (backslot.getMaxDamage() * 1.0f);

				ItemStack ammoitem = ((ItemTGSpecialSlotAmmo) backslot.getItem()).getAmmo();
				int count = InventoryUtil.countAmmoPlayer(ply, ammoitem);

				String text = ChatFormatting.YELLOW + "" + count + "x" + ChatFormatting.WHITE + (int) (percent * 100) + "%";
				g.drawString(Minecraft.getInstance().font, text, screenWidth - 2 - text.length() * 6 - 8 + 24, offsetY, 0xFFFFFFFF, false);

				offsetY -= 10;
			}
		}
		return offsetY;
	}
}
