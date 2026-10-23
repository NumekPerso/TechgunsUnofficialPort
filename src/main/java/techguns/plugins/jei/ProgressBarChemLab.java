package techguns.plugins.jei;

import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import techguns.gui.ChemLabGui;

/**
 * Animated chem lab progress (same drawing as ChemLabGui)
 */
public class ProgressBarChemLab implements IDrawableAnimated {

	protected ITickTimer timer;
	protected IGuiHelper guiHelper;
	protected int w;
	protected int h;

	public ProgressBarChemLab(IGuiHelper guiHelper, int w, int h) {
		super();
		this.guiHelper = guiHelper;
		this.w = w;
		this.h = h;
		this.timer = guiHelper.createTickTimer(100, 100, false);
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
		int k = xOffset - 81;
		int l = yOffset - 17;

		float prog = ((float) this.timer.getValue()) / ((float) this.timer.getMaxValue());

		int i1 = (int) ((prog < 0.2f ? (prog * 5) : 1.0f) * 25);
		g.blit(ChemLabGui.texture, k + 81, l + 19, 178, 5, 8, i1);

		if (prog >= 0.2f) {
			int i2 = (int) ((prog < 0.4f ? (prog - 0.2f) * 5 : 1.0f) * 9);
			g.blit(ChemLabGui.texture, k + 88, l + 39, 186, 25, i2, 1);
		}
		if (prog >= 0.4f) {
			int i3 = (int) ((prog < 0.6f ? (prog - 0.4f) * 5 : 1.0f) * 20);
			g.blit(ChemLabGui.texture, k + 97, l + 23 + (20 - i3), 194, 9 + (20 - i3), 14, i3);
		}
		if (prog >= 0.6f) {
			int i4 = (int) ((prog < 0.8f ? (prog - 0.6f) * 5 : 1.0f) * 16);
			g.blit(ChemLabGui.texture, k + 108, l + 17, 205, 3, i4, 6);
		}
		if (prog >= 0.8f) {
			int i5 = (int) (((prog - 0.8f) * 5) * 24);
			g.blit(ChemLabGui.texture, k + 117, l + 20, 214, 6, 14, i5 + 1);
		}
	}

}
