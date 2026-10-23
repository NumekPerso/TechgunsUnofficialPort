package techguns.plugins.jei;

import static techguns.plugins.jei.BasicRecipeCategory.JEI_OFFSET_X;
import static techguns.plugins.jei.BasicRecipeCategory.JEI_OFFSET_Y;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import techguns.gui.PoweredTileEntGui;
import techguns.gui.ReactionChamberGui;
import techguns.tileentities.operation.ReactionChamberOperation;
import techguns.tileentities.operation.ReactionChamberRecipe;
import techguns.util.TextUtil;

public class ReactionChamberJeiRecipe extends BasicRecipeWrapper {

	protected ReactionChamberRecipe recipe;

	IDrawableStatic progress_static;
	IDrawableAnimated progress;

	IDrawableAnimated completion_bar;
	IDrawableAnimated intensity_bar;

	protected byte fluidlevel;
	protected byte intensity;
	protected byte requiredCompletion;
	protected byte recipeticks;
	protected byte intensityMargin;
	protected float chanceToJump;
	protected boolean stable;

	public ReactionChamberJeiRecipe(ReactionChamberRecipe recipe, IGuiHelper guiHelper) {
		super(recipe);
		this.recipe = recipe;
		this.progress_static = guiHelper.createDrawable(ReactionChamberGui.texture, 0, 175, 101, 4);
		this.progress = guiHelper.createAnimatedDrawable(progress_static, Math.max(1, this.recipe.ticks * ReactionChamberOperation.RECIPE_TICKRATE), IDrawableAnimated.StartDirection.LEFT, false);
		this.completion_bar = new ProgressBarReactionChamberCompletion(guiHelper, 100, 4, this.recipe.ticks, recipe.requiredCompletion);
		this.fluidlevel = recipe.liquidLevel;
		this.intensity = recipe.preferredIntensity;
		this.intensity_bar = new ProgressBarReactionChamberIntensityJump(guiHelper, 5, 40, recipe.preferredIntensity, recipe.intensityMargin);

		this.requiredCompletion = recipe.requiredCompletion;
		this.recipeticks = recipe.ticks;
		this.intensityMargin = recipe.intensityMargin;
		this.chanceToJump = recipe.instability;
		this.stable = recipe.isStable();
	}

	@Override
	protected int getRFperTick() {
		return this.recipe.RFTick;
	}

	public int getLiquidConsumption() {
		return this.recipe.liquidConsumtion;
	}

	public static List<ReactionChamberJeiRecipe> getRecipes(IGuiHelper guiHelper) {
		List<ReactionChamberJeiRecipe> recipes = new ArrayList<>();
		ReactionChamberRecipe.getRecipes().values().forEach(rec -> recipes.add(new ReactionChamberJeiRecipe(rec, guiHelper)));
		return recipes;
	}

	@Override
	public List<Component> getTooltipStrings(int mouseX, int mouseY) {
		if (BasicRecipeCategory.isInRect(mouseX, mouseY, 66 + JEI_OFFSET_X, 60 + JEI_OFFSET_Y, 102, 14)) {
			List<Component> tooltip = new ArrayList<>();
			tooltip.add(Component.literal(TextUtil.trans("techguns.container.reactionchamber.completion") + ": " + ChatFormatting.GREEN + this.requiredCompletion));
			tooltip.add(Component.literal(TextUtil.trans("techguns.container.reactionchamber.recipeticks") + ": " + ChatFormatting.RED + this.recipeticks));
			tooltip.add(Component.literal(TextUtil.trans("techguns.container.reactionchamber.time") + ": " + (this.recipeticks * ReactionChamberOperation.RECIPE_TICKRATE) / 20 + "s"));
			return tooltip;
		} else if (BasicRecipeCategory.isInRect(mouseX, mouseY, 113 + JEI_OFFSET_X, 16 + JEI_OFFSET_Y, 7, 41)) {
			List<Component> tooltip = new ArrayList<>();
			tooltip.add(Component.literal(TextUtil.trans("techguns.container.reactionchamber.intensity") + ": " + this.intensity));
			if (!stable) {
				tooltip.add(Component.literal(TextUtil.trans("techguns.container.reactionchamber.intensitymargin") + ": +/-" + this.intensityMargin));
				tooltip.add(Component.literal(TextUtil.trans("techguns.container.reactionchamber.chance") + ": " + ((int) (this.chanceToJump * 100)) + "%"));
			} else {
				tooltip.add(Component.literal(ChatFormatting.GREEN + TextUtil.trans("techguns.container.reactionchamber.stable")));
			}
			return tooltip;
		} else if (BasicRecipeCategory.isInRect(mouseX, mouseY, 96 + JEI_OFFSET_X, 38 + JEI_OFFSET_Y, 12, 12)) {
			List<Component> tooltip = new ArrayList<>();
			tooltip.add(Component.literal(TextUtil.trans("techguns.container.reactionchamber.risk") + ": " + this.recipe.risk));
			return tooltip;
		} else if (BasicRecipeCategory.isInRect(mouseX, mouseY, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y, 6, 60)) {
			List<Component> tooltip = new ArrayList<>();
			tooltip.add(Component.literal(TextUtil.trans("techguns.container.power") + ":"));
			tooltip.add(Component.literal("-" + this.getRFperTick() + " " + PoweredTileEntGui.POWER_UNIT + "/" + TextUtil.trans("techguns.container.reactionchamber.recipetick")));
			tooltip.add(Component.literal("-" + this.getRFperTick() * this.requiredCompletion + " " + PoweredTileEntGui.POWER_UNIT));
			return tooltip;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public void drawInfo(GuiGraphics g, int mouseX, int mouseY) {
		this.progress.draw(g, 68 + JEI_OFFSET_X, 70 + JEI_OFFSET_Y);
		this.completion_bar.draw(g, 68 + JEI_OFFSET_X, 62 + JEI_OFFSET_Y);
		this.intensity_bar.draw(g, 115 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);

		int level = 0;
		switch (this.recipe.risk) {
		case BREAK_ITEM:
		case NONE:
			break;
		case RAD_LOW:
		case RAD_MEDIUM:
			level = 1;
			break;
		case EXPLOSION_LOW:
		case EXPLOSION_MEDIUM:
		case RAD_HIGH:
			level = 2;
			break;
		case EXPLOSION_HIGH:
		case UNFORSEEN_CONSEQUENCES:
			level = 3;
			break;
		default:
			break;
		}
		g.blit(ReactionChamberGui.texture, JEI_OFFSET_X + 96, JEI_OFFSET_Y + 38, 205, 12 * level, 12, 12);

		drawString(g, this.fluidlevel * 10 + "%", 34 + 1 + JEI_OFFSET_X, 50 + 1 + JEI_OFFSET_Y, 4210752);
		drawString(g, this.intensity + "", 121 + 1 + JEI_OFFSET_X, 18 + 1 + JEI_OFFSET_Y, 4210752);
	}

}
