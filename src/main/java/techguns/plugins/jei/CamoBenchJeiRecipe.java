package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.network.chat.Component;
import techguns.tileentities.operation.CamoBenchRecipes;
import techguns.tileentities.operation.CamoBenchRecipes.CamoBenchRecipe;

public class CamoBenchJeiRecipe extends BasicRecipeWrapper {

	CamoBenchRecipe rec;

	public CamoBenchJeiRecipe(CamoBenchRecipe recipe) {
		super(recipe);
		this.rec = recipe;
	}

	@Override
	protected int getRFperTick() {
		return 0;
	}

	public static List<CamoBenchJeiRecipe> getRecipes() {
		List<CamoBenchJeiRecipe> recipes = new ArrayList<>();
		CamoBenchRecipes.getRecipes().forEach(r -> recipes.add(new CamoBenchJeiRecipe(r)));
		return recipes;
	}

	@Override
	public List<Component> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

}
