package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import techguns.tileentities.operation.BlastFurnaceRecipes;
import techguns.tileentities.operation.BlastFurnaceRecipes.BlastFurnaceRecipe;
import techguns.util.TextUtil;

public class BlastFurnaceJeiRecipe extends BasicRecipeWrapper {

	BlastFurnaceRecipe recipe;

	public BlastFurnaceJeiRecipe(BlastFurnaceRecipe recipe) {
		super(recipe);
		this.recipe = recipe;
	}

	@Override
	protected int getDuration() {
		return recipe.duration;
	}

	@Override
	protected int getRFperTick() {
		return recipe.powerPerTick;
	}

	public static List<BlastFurnaceJeiRecipe> getRecipes() {
		List<BlastFurnaceJeiRecipe> recipes = new ArrayList<BlastFurnaceJeiRecipe>();
		BlastFurnaceRecipes.getRecipes().forEach(r -> recipes.add(new BlastFurnaceJeiRecipe(r)));
		return recipes;
	}

	@Override
	public List<Component> getTooltipStrings(int mouseX, int mouseY) {
		if (BasicRecipeCategory.isInRect(mouseX, mouseY, 22 + BasicRecipeCategory.JEI_OFFSET_X, 54 + BasicRecipeCategory.JEI_OFFSET_Y, 102, 12)) {
			List<Component> tooltip = new ArrayList<>();
			tooltip.add(Component.literal(TextUtil.transTG("tooltip.duration") + ": " + recipe.duration + " " + TextUtil.transTG("tooltip.ticks")));
			return tooltip;
		} else {
			return super.getTooltipStrings(mouseX, mouseY);
		}
	}

}
