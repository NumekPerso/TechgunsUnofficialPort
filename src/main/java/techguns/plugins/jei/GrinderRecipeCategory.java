package techguns.plugins.jei;

import static techguns.gui.containers.GrinderContainer.SLOTS_INPUT_Y;
import static techguns.gui.containers.GrinderContainer.SLOT_INPUT_X;
import static techguns.gui.containers.GrinderContainer.SLOT_OUTPUT0_X;

import java.util.List;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import techguns.TGMachines;
import techguns.gui.GrinderGui;
import techguns.tileentities.operation.GrinderRecipes.GrinderRecipeChance;
import techguns.util.TextUtil;

public class GrinderRecipeCategory extends BasicRecipeCategory<GrinderJeiRecipe> {

	IDrawableStatic progress1_static;
	IDrawableAnimated progress1;
	IDrawableStatic progress2_static;
	IDrawableAnimated progress2;

	public GrinderRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, GrinderGui.texture, "grinder", TGJeiPlugin.GRINDER, TGMachines.GRINDER);
		this.progress1_static = guiHelper.createDrawable(GrinderGui.texture, 0, 167, 21, 21);
		this.progress1 = guiHelper.createAnimatedDrawable(progress1_static, 100, IDrawableAnimated.StartDirection.LEFT, false);
		this.progress2_static = guiHelper.createDrawable(GrinderGui.texture, 0, 167, 21, 21);
		this.progress2 = guiHelper.createAnimatedDrawable(progress2_static, 100, IDrawableAnimated.StartDirection.RIGHT, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, GrinderJeiRecipe recipe, IFocusGroup focuses) {
		addItemSlot(builder, RecipeIngredientRole.INPUT, SLOT_INPUT_X, SLOTS_INPUT_Y, get(recipe.getItemInputs(), 0));

		List<List<ItemStack>> outputs = recipe.getItemOutputs();
		double[] chances = recipe.getRecipe() instanceof GrinderRecipeChance r ? r.getChances() : null;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int index = i * 3 + j;
				var slot = addItemSlot(builder, RecipeIngredientRole.OUTPUT, SLOT_OUTPUT0_X + j * 18, SLOTS_INPUT_Y + i * 18, get(outputs, index));
				if (chances != null && index < chances.length && chances[index] < 1) {
					double chance = chances[index];
					slot.addRichTooltipCallback((view, tooltip) -> tooltip
							.add(Component.literal(TextUtil.transTG("oredrill.jei.chance") + ": " + String.format("%.2f", chance * 100f) + "%")));
				}
			}
		}
	}

	@Override
	protected void drawExtras(GuiGraphics g) {
		this.powerbar.draw(g, 8 + JEI_OFFSET_X, 17 + JEI_OFFSET_Y);
		this.progress1.draw(g, 32 + JEI_OFFSET_X, 40 + JEI_OFFSET_Y);
		this.progress2.draw(g, 52 + JEI_OFFSET_X, 40 + JEI_OFFSET_Y);
	}

}
