package techguns.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.capabilities.TGExtendedPlayer;
import techguns.items.armors.PoweredArmor;
import techguns.util.InventoryUtil;

/**
 * Armor related HUD elements, called by TGHud: the power indicator of powered armors (1.12 TGGuiEvents)
 */
public class TGArmorHud {

	/**
	 * Draws armor HUD elements at the right side of the screen starting at offsetY (going up), returns the new offsetY
	 */
	public static int render(GuiGraphics g, Player ply, TGExtendedPlayer props, int screenWidth, int offsetY) {
		//draw power icon
		ItemStack chest = ply.getInventory().armor.get(2);
		if (!chest.isEmpty() && chest.getItem() instanceof PoweredArmor pwrchest) {
			g.blit(TGHud.hud_texture, screenWidth - 10, offsetY, 249, 35, 7, 7);

			double percent = (PoweredArmor.getPower(chest) * 1.0D) / (pwrchest.maxpower * 1.0D);

			int count = 0;
			ItemStack ammoitem = pwrchest.getBattery();
			if (!ammoitem.isEmpty()) {
				//main inventory and ammo slots of the Techguns player inventory (the port also counts the offhand)
				count = InventoryUtil.countAmmoPlayer(ply, ammoitem);
			}

			String text = ChatFormatting.YELLOW + "" + count + "x" + ChatFormatting.WHITE + (int) (percent * 100) + "%";
			g.drawString(Minecraft.getInstance().font, text, screenWidth - 2 - text.length() * 6 - 8 + 24, offsetY, 0xFFFFFFFF, false);
			offsetY -= 10;
		}
		return offsetY;
	}
}
