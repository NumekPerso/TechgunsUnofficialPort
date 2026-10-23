package techguns.plugins.jei;

import static techguns.gui.containers.ChargingStationContainer.SLOTS_ROW1_Y;
import static techguns.gui.containers.ChargingStationContainer.SLOT_INPUT_X;
import static techguns.gui.containers.ChargingStationContainer.SLOT_OUTPUT_X;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import techguns.TGMachines;
import techguns.gui.ChargingStationGui;

public class ChargingStationJeiRecipeCategory extends BasicRecipeCategory<ChargingStationJeiRecipe> {

	IDrawableStatic progress_static;
	IDrawableAnimated progress;

	public ChargingStationJeiRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, ChargingStationGui.texture, "chargingstation", TGJeiPlugin.CHARGING_STATION, TGMachines.CHARGING_STATION);
		this.progress_static = guiHelper.createDrawable(ChargingStationGui.texture, 0, 167, 26, 10);
		this.progress = guiHelper.createAnimatedDrawable(progress_static, 100, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ChargingStationJeiRecipe recipe, IFocusGroup focuses) {
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT_X, SLOTS_ROW1_Y, get(recipe.getItemInputs(), 0));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, SLOT_OUTPUT_X, SLOTS_ROW1_Y, get(recipe.getItemOutputs(), 0));
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.powerbar.draw(g, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);
		this.progress.draw(g, 40 + JEI_OFFSET_X, 20 + JEI_OFFSET_Y);
	}

}
