package techguns.plugins.jei;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;

import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import techguns.gui.UpgradeBenchGui;

/**
 * Upgrade bench "enchanting" animation with a cycling hue (like UpgradeBenchGui)
 */
public class UpgradeBenchAnimation implements IDrawableAnimated {

	protected ITickTimer timer;
	protected ITickTimer timer_hue;
	protected IGuiHelper guiHelper;
	protected int w;
	protected int h;

	public UpgradeBenchAnimation(IGuiHelper guiHelper, int w, int h) {
		this.w = w;
		this.h = h;
		this.guiHelper = guiHelper;
		this.timer = guiHelper.createTickTimer(3, 3, true);
		this.timer_hue = guiHelper.createTickTimer(359, 359, false);
	}

	@Override
	public int getWidth() {
		return w;
	}

	@Override
	public int getHeight() {
		return h;
	}

	@Override
	public void draw(GuiGraphics g, int xOffset, int yOffset) {
		int k = xOffset;
		int l = yOffset;

		float hue = this.timer_hue.getValue() / 360f;
		int i = this.timer.getValue();

		int x = i >= 2 ? 0 : 89;
		int y = i % 2 == 1 ? 0 : 31;

		Color c = new Color(Color.HSBtoRGB(hue, 1f, 1f));
		RenderSystem.enableBlend();
		g.setColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1f);
		g.blit(UpgradeBenchGui.texture, k + 37, l + 40, 0 + x, 167 + y, 89, 31);
		g.setColor(1f, 1f, 1f, 1f);
		RenderSystem.disableBlend();
	}

}
