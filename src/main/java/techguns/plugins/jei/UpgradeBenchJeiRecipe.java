package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import techguns.TGConfig;
import techguns.tileentities.operation.UpgradeBenchRecipes;
import techguns.tileentities.operation.UpgradeBenchRecipes.UpgradeBenchRecipe;
import techguns.util.TextUtil;

public class UpgradeBenchJeiRecipe extends BasicRecipeWrapper {

	protected UpgradeBenchRecipe recipe;

	public UpgradeBenchJeiRecipe(UpgradeBenchRecipe recipe) {
		super(recipe);
		this.recipe = recipe;
	}

	@Override
	protected int getRFperTick() {
		return 0;
	}

	public static List<UpgradeBenchJeiRecipe> getRecipes() {
		List<UpgradeBenchJeiRecipe> recipes = new ArrayList<>();
		UpgradeBenchRecipes.recipes.forEach(r -> {
			recipes.add(new UpgradeBenchJeiRecipe(r));
		});
		return recipes;
	}

	@Override
	public List<Component> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

	@Override
	public void drawInfo(GuiGraphics g, int mouseX, int mouseY) {
		int xp = this.recipe.getLevel() * TGConfig.upgrade_xp_cost;
		drawXPText(g, font(), TextUtil.transTG("gui.xpcost") + ": " + xp, 95 + BasicRecipeCategory.JEI_OFFSET_X, 17 + BasicRecipeCategory.JEI_OFFSET_Y, 8453920);
	}

	protected void drawXPText(GuiGraphics g, Font fontRenderer, String s, int x, int y, int color) {
		int j = -16777216 | (color & 16579836) >> 2 | color & -16777216;
		g.drawString(fontRenderer, s, x, y + 1, j, false);
		g.drawString(fontRenderer, s, x + 1, y, j, false);
		g.drawString(fontRenderer, s, x + 1, y + 1, j, false);
		g.drawString(fontRenderer, s, x, y, color, false);
	}

}
