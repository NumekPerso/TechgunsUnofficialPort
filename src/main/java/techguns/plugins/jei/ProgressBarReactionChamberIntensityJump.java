package techguns.plugins.jei;

import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import techguns.gui.ReactionChamberGui;
import techguns.tileentities.operation.ReactionChamberOperation;

/**
 * Reaction chamber intensity bar, oscillates in the intensity margin for unstable recipes
 */
public class ProgressBarReactionChamberIntensityJump implements IDrawableAnimated {

	protected ITickTimer timer;
	protected IGuiHelper guiHelper;
	protected int w;
	protected int h;
	protected int intensity;
	protected int margin;

	public ProgressBarReactionChamberIntensityJump(IGuiHelper guiHelper, int w, int h, int intensity, int margin) {
		super();
		this.guiHelper = guiHelper;
		this.w = w;
		this.h = h;
		int totalTime = 3 * ReactionChamberOperation.RECIPE_TICKRATE;
		this.timer = guiHelper.createTickTimer(totalTime, totalTime, false);
		this.intensity = intensity;
		this.margin = margin;
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
		int height = 4 * this.intensity;
		int u_offset = 0;
		if (this.margin > 0) {
			float prog = ((float) this.timer.getValue()) / ((float) this.timer.getMaxValue());

			float jump_prog = (float) Math.sin(prog * 2.0f * Math.PI);

			int val = (int) Math.round(jump_prog * margin);
			if (val > this.margin) {
				val = margin;
			} else if (val < -this.margin) {
				val = -margin;
			}
			height = 4 * (this.intensity + val);
			u_offset = 8;
		}
		height = Math.max(0, Math.min(40, height));

		g.blit(ReactionChamberGui.texture, xOffset, yOffset + 40 - height, 190 + u_offset, 40 - height, 5, height);
		g.blit(ReactionChamberGui.texture, xOffset - 2, yOffset + 40 - 1 - (4 * this.intensity), 178, 22, 5, 3);
	}

}
