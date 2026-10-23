package techguns.plugins.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import techguns.TGMachines;
import techguns.gui.OreDrillGui;
import techguns.gui.containers.OreDrillContainer;
import techguns.tileentities.OreDrillTileEntMaster;
import techguns.util.TextUtil;

public class OreDrillJeiRecipeCategory extends BasicRecipeCategory<OreDrillJeiRecipe> {

	protected IDrawableStatic tank_overlay;
	protected IDrawableStatic progress_static;
	protected IDrawableAnimated progress;

	public OreDrillJeiRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, OreDrillGui.texture, "oredrill", TGJeiPlugin.ORE_DRILL, TGMachines.OREDRILL_CONTROLLER);
		tank_overlay = guiHelper.createDrawable(OreDrillGui.texture, 177, 40, 10, 50);
		this.progress_static = guiHelper.createDrawable(OreDrillGui.texture, 177, 1, 25, 36);
		this.progress = guiHelper.createAnimatedDrawable(progress_static, 100, IDrawableAnimated.StartDirection.TOP, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, OreDrillJeiRecipe recipe, IFocusGroup focuses) {
		IRecipeSlotRichTooltipCallback chanceTooltip = (view, tooltip) -> tooltip
				.add(Component.literal(TextUtil.transTG("oredrill.jei.chance") + ": " + String.format("%.2f", recipe.getChance() * 100f) + "%"));

		addItemSlot(builder, RecipeIngredientRole.INPUT, OreDrillContainer.SLOT_DRILL_X, OreDrillContainer.SLOTS_ROW1_Y, get(recipe.getItemInputs(), 0));
		addItemSlotRaw(builder, RecipeIngredientRole.INPUT, 69, 40, get(recipe.getItemInputs(), 1));
		addItemSlot(builder, RecipeIngredientRole.INPUT, OreDrillContainer.SLOT_DRILL_X, OreDrillContainer.SLOTS_FURNACE_Y, get(recipe.getItemInputs(), 2));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, OreDrillContainer.SLOTS_OUTPUT_X, OreDrillContainer.SLOTS_ROW1_Y, get(recipe.getItemOutputs(), 0))
				.addRichTooltipCallback(chanceTooltip);

		addFluidSlot(builder, RecipeIngredientRole.INPUT, OreDrillGui.INPUT_TANK_X + 2 + JEI_OFFSET_X, OreDrillGui.TANK_Y + 1 + JEI_OFFSET_Y, OreDrillGui.TANK_W, OreDrillGui.TANK_H,
				OreDrillTileEntMaster.CAPACITY_INPUT_TANK, tank_overlay, get(recipe.getFluidInputs(), 0));
		addFluidSlot(builder, RecipeIngredientRole.OUTPUT, OreDrillGui.OUTPUT_TANK_X + 2 + JEI_OFFSET_X, OreDrillGui.TANK_Y + 1 + JEI_OFFSET_Y, OreDrillGui.TANK_W, OreDrillGui.TANK_H,
				OreDrillTileEntMaster.CAPACITY_OUTPUT_TANK, tank_overlay, get(recipe.getFluidOutputs(), 0)).addRichTooltipCallback(chanceTooltip);
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.powerbar.draw(g, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);
		this.progress.draw(g, 74 + JEI_OFFSET_X, 18 + JEI_OFFSET_Y);
	}

}
