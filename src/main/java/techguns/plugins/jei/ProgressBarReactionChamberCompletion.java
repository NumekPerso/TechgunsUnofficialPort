package techguns.plugins.jei;

import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import techguns.gui.ReactionChamberGui;
import techguns.tileentities.operation.ReactionChamberOperation;

/**
 * Reaction chamber completion bar, fills in steps of 1 recipe tick until the required completion
 */
public class ProgressBarReactionChamberCompletion implements IDrawableAnimated {

	protected ITickTimer timer;
	protected IGuiHelper guiHelper;
	protected int w;
	protected int h;
	protected int recipeTicks;
	protected int totalTime;
	protected int guiStepWidth;
	protected int requiredCompletion;

	public ProgressBarReactionChamberCompletion(IGuiHelper guiHelper, int w, int h, int recipeTicks, int reqCompletion) {
		super();
		this.guiHelper = guiHelper;
		this.w = w;
		this.h = h;
		this.totalTime = Math.max(1, recipeTicks * ReactionChamberOperation.RECIPE_TICKRATE);
		this.timer = guiHelper.createTickTimer(totalTime, totalTime, false);
		this.recipeTicks = recipeTicks;
		this.requiredCompletion = Math.max(1, reqCompletion);
		this.guiStepWidth = (int) (100.0f / (float) this.requiredCompletion);
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
		int completion_totalTime = this.requiredCompletion * ReactionChamberOperation.RECIPE_TICKRATE;
		float prog = ((float) this.timer.getValue()) / ((float) completion_totalTime);
		if (prog > 1.0f) {
			prog = 1.0f;
		}
		int steps = ((int) (prog * completion_totalTime)) / ((int) ((float) completion_totalTime / (float) this.requiredCompletion));
		g.blit(ReactionChamberGui.texture, xOffset, yOffset, 0, 167, steps * this.guiStepWidth, 4);
	}

}
