package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.List;

import techguns.tileentities.FabricatorTileEntMaster;
import techguns.tileentities.operation.FabricatorRecipe;

public class FabricatorJeiRecipe extends BasicRecipeWrapper {

	FabricatorRecipe rec;

	public FabricatorJeiRecipe(FabricatorRecipe recipe) {
		super(recipe);
		this.rec = recipe;
	}

	@Override
	protected int getRFperTick() {
		return FabricatorTileEntMaster.powerPerTick;
	}

	public static List<FabricatorJeiRecipe> getRecipes() {
		List<FabricatorJeiRecipe> recipes = new ArrayList<FabricatorJeiRecipe>();
		FabricatorRecipe.getRecipes().forEach(r -> recipes.add(new FabricatorJeiRecipe(r)));
		return recipes;
	}

}
