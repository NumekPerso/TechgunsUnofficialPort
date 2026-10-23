package techguns.plugins.jei;

import static techguns.gui.containers.BlastFurnaceContainer.SLOTS_ROW1_Y;
import static techguns.gui.containers.BlastFurnaceContainer.SLOT_INPUT1_X;
import static techguns.gui.containers.BlastFurnaceContainer.SLOT_INPUT2_X;
import static techguns.gui.containers.BlastFurnaceContainer.SLOT_OUTPUT_X;
import static techguns.gui.containers.BlastFurnaceContainer.SLOT_OUTPUT_Y;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import techguns.TGMachines;
import techguns.gui.BlastFurnaceGui;

public class BlastFurnaceJeiRecipeCategory extends BasicRecipeCategory<BlastFurnaceJeiRecipe> {

	IDrawableStatic progress_static;
	IDrawableAnimated progress;

	public BlastFurnaceJeiRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, BlastFurnaceGui.texture, "blast_furnace", TGJeiPlugin.BLAST_FURNACE, TGMachines.BLAST_FURNACE);
		this.progress_static = guiHelper.createDrawable(BlastFurnaceGui.texture, 0, 167, 90, 10);
		this.progress = guiHelper.createAnimatedDrawable(progress_static, 100, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BlastFurnaceJeiRecipe recipe, IFocusGroup focuses) {
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT1_X, SLOTS_ROW1_Y, get(recipe.getItemInputs(), 0));
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT2_X, SLOTS_ROW1_Y, get(recipe.getItemInputs(), 1));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, SLOT_OUTPUT_X, SLOT_OUTPUT_Y, get(recipe.getItemOutputs(), 0));
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.powerbar.draw(g, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);
		this.progress.draw(g, 20 + JEI_OFFSET_X, 54 + JEI_OFFSET_Y);
	}

}
