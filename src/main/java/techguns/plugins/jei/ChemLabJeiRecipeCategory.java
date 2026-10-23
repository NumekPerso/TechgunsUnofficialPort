package techguns.plugins.jei;

import static techguns.gui.containers.ChemLabContainer.SLOTS_ROW1_Y;
import static techguns.gui.containers.ChemLabContainer.SLOTS_ROW2_Y;
import static techguns.gui.containers.ChemLabContainer.SLOT_INPUT1_X;
import static techguns.gui.containers.ChemLabContainer.SLOT_INPUT2_X;
import static techguns.gui.containers.ChemLabContainer.SLOT_OUTPUT_X;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import techguns.TGMachines;
import techguns.gui.ChemLabGui;
import techguns.tileentities.ChemLabTileEnt;

public class ChemLabJeiRecipeCategory extends BasicRecipeCategory<ChemLabJeiRecipe> {

	protected IDrawableStatic tank_overlay;
	protected ProgressBarChemLab progress;

	public ChemLabJeiRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, ChemLabGui.texture, "chemlab", TGJeiPlugin.CHEM_LAB, TGMachines.CHEM_LAB);
		tank_overlay = guiHelper.createDrawable(ChemLabGui.texture, 177, 32, 10, 50);
		progress = new ProgressBarChemLab(guiHelper, 54, 31);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ChemLabJeiRecipe recipe, IFocusGroup focuses) {
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT1_X, SLOTS_ROW1_Y, get(recipe.getItemInputs(), 0));
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT2_X, SLOTS_ROW1_Y, get(recipe.getItemInputs(), 1));
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT1_X, SLOTS_ROW2_Y, get(recipe.getItemInputs(), 2));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, SLOT_OUTPUT_X, SLOTS_ROW1_Y, get(recipe.getItemOutputs(), 0));

		addFluidSlot(builder, RecipeIngredientRole.INPUT, ChemLabGui.INPUT_TANK_X + 2 + JEI_OFFSET_X, ChemLabGui.TANK_Y + 1 + JEI_OFFSET_Y, ChemLabGui.TANK_W, ChemLabGui.TANK_H,
				ChemLabTileEnt.CAPACITY_INPUT_TANK, tank_overlay, get(recipe.getFluidInputs(), 0));
		addFluidSlot(builder, RecipeIngredientRole.OUTPUT, ChemLabGui.OUTPUT_TANK_X + 2 + JEI_OFFSET_X, ChemLabGui.TANK_Y + 1 + JEI_OFFSET_Y, ChemLabGui.TANK_W, ChemLabGui.TANK_H,
				ChemLabTileEnt.CAPACITY_OUTPUT_TANK, tank_overlay, get(recipe.getFluidOutputs(), 0));
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.powerbar.draw(g, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);
		this.progress.draw(g, 82 + JEI_OFFSET_X, 18 + JEI_OFFSET_Y);
	}

}
