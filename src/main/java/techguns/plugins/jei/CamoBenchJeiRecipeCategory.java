package techguns.plugins.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import techguns.TGMachines;
import techguns.gui.CamoBenchGui;
import techguns.gui.containers.CamoBenchContainer;

public class CamoBenchJeiRecipeCategory extends BasicRecipeCategory<CamoBenchJeiRecipe> {

	protected static final int CAMO_BENCH_OFFSET_X = -4;
	protected static final int CAMO_BENCH_OFFSET_Y = 2;

	public CamoBenchJeiRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, CamoBenchGui.texture, "camobench", TGJeiPlugin.CAMO_BENCH, TGMachines.CAMO_BENCH);
		background = guiHelper.createDrawable(CamoBenchGui.texture, 7 - CAMO_BENCH_OFFSET_X, 15 - CAMO_BENCH_OFFSET_Y, 162, 63);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CamoBenchJeiRecipe recipe, IFocusGroup focuses) {
		//1.12: only the input slot is shown, it cycles through all camos; the outputs (same items) are used for lookups
		addItemSlot(builder, RecipeIngredientRole.INPUT, CamoBenchContainer.SLOT_INPUT_X + CAMO_BENCH_OFFSET_X, CamoBenchContainer.SLOT_INPUT_Y + CAMO_BENCH_OFFSET_Y,
				get(recipe.getItemInputs(), 0));
		builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStacks(filterItems(get(recipe.getItemOutputs(), 0)));
	}

}
