package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.List;

import techguns.tileentities.MetalPressTileEnt;
import techguns.tileentities.operation.MetalPressRecipes;
import techguns.tileentities.operation.MetalPressRecipes.MetalPressRecipe;

public class MetalPressJeiRecipe extends BasicRecipeWrapper {

	MetalPressRecipe recipe;

	public MetalPressJeiRecipe(MetalPressRecipe recipe) {
		super(recipe);
		this.recipe = recipe;
	}

	@Override
	protected int getRFperTick() {
		return MetalPressTileEnt.POWER_PER_TICK;
	}

	public static List<MetalPressJeiRecipe> getRecipes() {
		List<MetalPressJeiRecipe> recipes = new ArrayList<MetalPressJeiRecipe>();
		MetalPressRecipes.getRecipes().forEach(r -> recipes.add(new MetalPressJeiRecipe(r)));
		return recipes;
	}

}
