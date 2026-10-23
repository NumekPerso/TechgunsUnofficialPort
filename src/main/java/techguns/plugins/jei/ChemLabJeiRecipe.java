package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.List;

import techguns.tileentities.operation.ChemLabRecipes;
import techguns.tileentities.operation.ChemLabRecipes.ChemLabRecipe;

public class ChemLabJeiRecipe extends BasicRecipeWrapper {

	ChemLabRecipe recipe;

	public ChemLabJeiRecipe(ChemLabRecipe recipe) {
		super(recipe);
		this.recipe = recipe;
	}

	@Override
	protected int getRFperTick() {
		return recipe.powerPerTick;
	}

	public static List<ChemLabJeiRecipe> getRecipes() {
		List<ChemLabJeiRecipe> recipes = new ArrayList<ChemLabJeiRecipe>();
		ChemLabRecipes.getRecipes().forEach(r -> {
			//recipes disabled by their condition (e.g. lava recipes when fuels exist) are not shown
			if (r.isEnabled()) {
				recipes.add(new ChemLabJeiRecipe(r));
			}
		});
		return recipes;
	}

}
