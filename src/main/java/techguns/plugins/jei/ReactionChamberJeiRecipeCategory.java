package techguns.plugins.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import techguns.TGMachines;
import techguns.gui.ChemLabGui;
import techguns.gui.ReactionChamberGui;
import techguns.gui.containers.ReactionChamberContainer;
import techguns.tileentities.ReactionChamberTileEntMaster;
import techguns.util.TextUtil;

public class ReactionChamberJeiRecipeCategory extends BasicRecipeCategory<ReactionChamberJeiRecipe> {

	protected IDrawableStatic tank_overlay;

	public ReactionChamberJeiRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, ReactionChamberGui.texture, "reactionchamber", TGJeiPlugin.REACTION_CHAMBER, TGMachines.REACTIONCHAMBER_CONTROLLER);
		tank_overlay = guiHelper.createDrawable(ReactionChamberGui.texture, 177, 32, 10, 50);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ReactionChamberJeiRecipe recipe, IFocusGroup focuses) {
		addItemSlot(builder, RecipeIngredientRole.INPUT, ReactionChamberContainer.SLOT_INPUT_X, ReactionChamberContainer.SLOTS_ROW1_Y, get(recipe.getItemInputs(), 0));
		addItemSlot(builder, RecipeIngredientRole.INPUT, ReactionChamberContainer.SLOT_FOCUS_X, ReactionChamberContainer.SLOTS_ROW1_Y, get(recipe.getItemInputs(), 1));

		addItemSlot(builder, RecipeIngredientRole.OUTPUT, ReactionChamberContainer.SLOT_OUTPUT_X, ReactionChamberContainer.SLOT_OUTPUT_Y, get(recipe.getItemOutputs(), 0));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, ReactionChamberContainer.SLOT_OUTPUT_X + 18, ReactionChamberContainer.SLOT_OUTPUT_Y, get(recipe.getItemOutputs(), 1));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, ReactionChamberContainer.SLOT_OUTPUT_X, ReactionChamberContainer.SLOT_OUTPUT_Y + 18, get(recipe.getItemOutputs(), 2));
		addItemSlot(builder, RecipeIngredientRole.OUTPUT, ReactionChamberContainer.SLOT_OUTPUT_X + 18, ReactionChamberContainer.SLOT_OUTPUT_Y + 18, get(recipe.getItemOutputs(), 3));

		int consumption = recipe.getLiquidConsumption();
		addFluidSlot(builder, RecipeIngredientRole.INPUT, ChemLabGui.INPUT_TANK_X + 2 + JEI_OFFSET_X, ChemLabGui.TANK_Y + 1 + JEI_OFFSET_Y, ChemLabGui.TANK_W, ChemLabGui.TANK_H,
				ReactionChamberTileEntMaster.CAPACITY_INPUT_TANK, tank_overlay, get(recipe.getFluidInputs(), 0))
				.addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.literal(
						ChatFormatting.GRAY + TextUtil.trans("techguns.container.reactionchamber.consumption") + ": " + String.format("%,d", consumption) + " mB")));
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.powerbar.draw(g, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);
	}

}
