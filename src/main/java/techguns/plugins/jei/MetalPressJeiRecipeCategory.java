package techguns.plugins.jei;

import static techguns.gui.containers.MetalPressContainer.SLOT_INPUT1_X;
import static techguns.gui.containers.MetalPressContainer.SLOT_INPUT2_X;
import static techguns.gui.containers.MetalPressContainer.SLOT_INPUTS_Y;
import static techguns.gui.containers.MetalPressContainer.SLOT_OUTPUTS_Y;
import static techguns.gui.containers.MetalPressContainer.SLOT_OUTPUT_X;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import techguns.TGMachines;
import techguns.gui.MetalPressGui;

public class MetalPressJeiRecipeCategory extends BasicRecipeCategory<MetalPressJeiRecipe> {

	IDrawableStatic progress_static;
	IDrawableAnimated progress;

	public MetalPressJeiRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, MetalPressGui.texture, "metalpress", TGJeiPlugin.METAL_PRESS, TGMachines.METAL_PRESS);
		this.progress_static = guiHelper.createDrawable(MetalPressGui.texture, 180, 0, 10, 21);
		this.progress = guiHelper.createAnimatedDrawable(progress_static, 100, IDrawableAnimated.StartDirection.TOP, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MetalPressJeiRecipe recipe, IFocusGroup focuses) {
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT1_X, SLOT_INPUTS_Y, get(recipe.getItemInputs(), 0));
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT2_X, SLOT_INPUTS_Y, get(recipe.getItemInputs(), 1));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, SLOT_OUTPUT_X, SLOT_OUTPUTS_Y, get(recipe.getItemOutputs(), 0));
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.powerbar.draw(g, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);
		this.progress.draw(g, 124 + JEI_OFFSET_X, 37 + JEI_OFFSET_Y);
	}

}
