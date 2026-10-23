package techguns.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import techguns.TGRadiationSystem;
import techguns.capabilities.TGExtendedPlayer;
import techguns.radiation.RadiationPotion;
import techguns.util.MathUtil;

/**
 * RAD level and current RAD/s at the right side of the screen (1.12 TGGuiEvents), called by TGHud
 */
public class TGRadiationHud {

	public static int render(GuiGraphics g, Player ply, TGExtendedPlayer props, int screenWidth, int offsetY) {
		if (props.radlevel > 0) {
			Minecraft mc = Minecraft.getInstance();

			String prefix = ChatFormatting.WHITE.toString();
			if (props.radlevel >= TGRadiationSystem.LETHAL_POISONING) {
				prefix = ChatFormatting.RED.toString();
			} else if (props.radlevel >= TGRadiationSystem.SEVERE_POISONING) {
				prefix = ChatFormatting.GOLD.toString();
			} else if (props.radlevel >= TGRadiationSystem.MINOR_POISONING) {
				prefix = ChatFormatting.YELLOW.toString();
			}
			String radtext = props.radlevel + " RAD";
			g.drawString(mc.font, prefix + radtext, screenWidth - radtext.length() * 6, offsetY, 0xFFFFFFFF, false);

			offsetY -= 10;

			String currentRadText = "";
			MobEffectInstance currentRad = ply.getEffect(TGRadiationSystem.radiation_effect);
			if (currentRad != null) {
				int res = RadiationPotion.getRadResistance(ply);

				int amount = MathUtil.clamp(currentRad.getAmplifier() + 1 - res, 0, 1000);

				currentRadText = "+[" + amount + " RAD/s]";
				g.drawString(mc.font, currentRadText, screenWidth - currentRadText.length() * 6 + 4, offsetY, 0xFFFFFFFF, false);

				offsetY -= 10;
			}
		}
		return offsetY;
	}
}
