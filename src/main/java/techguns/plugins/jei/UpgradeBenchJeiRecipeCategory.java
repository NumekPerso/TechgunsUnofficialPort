package techguns.plugins.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import techguns.TGMachines;
import techguns.gui.UpgradeBenchGui;
import techguns.gui.containers.UpgradeBenchContainer;

public class UpgradeBenchJeiRecipeCategory extends BasicRecipeCategory<UpgradeBenchJeiRecipe> {

	UpgradeBenchAnimation anim;

	public UpgradeBenchJeiRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, UpgradeBenchGui.texture, "upgradebench", TGJeiPlugin.UPGRADE_BENCH, TGMachines.ARMOR_BENCH);
		this.anim = new UpgradeBenchAnimation(guiHelper, 89, 31);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, UpgradeBenchJeiRecipe recipe, IFocusGroup focuses) {
		IRecipeSlotBuilder in = addItemSlot(builder, RecipeIngredientRole.INPUT, UpgradeBenchContainer.SLOT_INPUT_X, UpgradeBenchContainer.SLOT_Y, get(recipe.getItemInputs(), 0));
		addItemSlot(builder, RecipeIngredientRole.INPUT, UpgradeBenchContainer.SLOT_UPGRADE_X, UpgradeBenchContainer.SLOT_Y, get(recipe.getItemInputs(), 1));
		IRecipeSlotBuilder out = addItemSlot(builder, RecipeIngredientRole.OUTPUT, UpgradeBenchContainer.SLOT_OUTPUT_X, UpgradeBenchContainer.SLOT_Y, get(recipe.getItemOutputs(), 0));
		//same armor in the input and output slot
		if (get(recipe.getItemInputs(), 0).size() == get(recipe.getItemOutputs(), 0).size()) {
			builder.createFocusLink(in, out);
		}
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.anim.draw(g, JEI_OFFSET_X + 1, JEI_OFFSET_Y + 1);
	}

}
