package techguns.plugins.jei;

import static techguns.gui.containers.FabricatorContainer.SLOTS_ROW1_Y;
import static techguns.gui.containers.FabricatorContainer.SLOT_INPUT1_X;
import static techguns.gui.containers.FabricatorContainer.SLOT_OUTPUT_X;
import static techguns.gui.containers.FabricatorContainer.SLOT_OUTPUT_Y;
import static techguns.gui.containers.FabricatorContainer.SLOT_PLATE_X;
import static techguns.gui.containers.FabricatorContainer.SLOT_POWDER_X;
import static techguns.gui.containers.FabricatorContainer.SLOT_WIRES_X;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import techguns.TGMachines;
import techguns.gui.FabricatorGui;

public class FabricatorJeiRecipeCategory extends BasicRecipeCategory<FabricatorJeiRecipe> {

	IDrawableStatic progress_static;
	IDrawableAnimated progress;

	public FabricatorJeiRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, FabricatorGui.texture, "fabricator", TGJeiPlugin.FABRICATOR, TGMachines.FABRICATOR_CONTROLLER);
		this.progress_static = guiHelper.createDrawable(FabricatorGui.texture, 0, 167, 90, 10);
		this.progress = guiHelper.createAnimatedDrawable(progress_static, 100, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FabricatorJeiRecipe recipe, IFocusGroup focuses) {
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT1_X, SLOTS_ROW1_Y, get(recipe.getItemInputs(), 0));
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_WIRES_X, SLOTS_ROW1_Y, get(recipe.getItemInputs(), 1));
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_POWDER_X, SLOTS_ROW1_Y, get(recipe.getItemInputs(), 2));
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_PLATE_X, SLOTS_ROW1_Y, get(recipe.getItemInputs(), 3));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, SLOT_OUTPUT_X, SLOT_OUTPUT_Y, get(recipe.getItemOutputs(), 0));
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.powerbar.draw(g, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);
		this.progress.draw(g, 22 + JEI_OFFSET_X, 54 + JEI_OFFSET_Y);
	}

}
