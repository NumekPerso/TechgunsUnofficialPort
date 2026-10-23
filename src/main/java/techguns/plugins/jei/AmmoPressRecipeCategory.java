package techguns.plugins.jei;

import static techguns.gui.containers.AmmoPressContainer.SLOTS_INPUT_Y;
import static techguns.gui.containers.AmmoPressContainer.SLOTS_OUTPUT_Y;
import static techguns.gui.containers.AmmoPressContainer.SLOT_METAL1_X;
import static techguns.gui.containers.AmmoPressContainer.SLOT_METAL2_X;
import static techguns.gui.containers.AmmoPressContainer.SLOT_OUTPUT_X;
import static techguns.gui.containers.AmmoPressContainer.SLOT_POWDER_X;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import techguns.TGMachines;
import techguns.gui.AmmoPressGui;

public class AmmoPressRecipeCategory extends BasicRecipeCategory<AmmoPressJeiRecipe> {

	IDrawableStatic progress_static;
	IDrawableAnimated progress;

	public AmmoPressRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, AmmoPressGui.texture, "ammopress", TGJeiPlugin.AMMO_PRESS, TGMachines.AMMO_PRESS);
		this.progress_static = guiHelper.createDrawable(AmmoPressGui.texture, 180, 0, 10, 21);
		this.progress = guiHelper.createAnimatedDrawable(progress_static, 100, IDrawableAnimated.StartDirection.TOP, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, AmmoPressJeiRecipe recipe, IFocusGroup focuses) {
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_METAL1_X, SLOTS_INPUT_Y, get(recipe.getItemInputs(), 0));
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_METAL2_X, SLOTS_INPUT_Y, get(recipe.getItemInputs(), 1));
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_POWDER_X, SLOTS_INPUT_Y, get(recipe.getItemInputs(), 2));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, SLOT_OUTPUT_X, SLOTS_OUTPUT_Y, get(recipe.getItemOutputs(), 0));
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.powerbar.draw(g, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);
		this.progress.draw(g, 124 + JEI_OFFSET_X, 37 + JEI_OFFSET_Y);
	}

}
