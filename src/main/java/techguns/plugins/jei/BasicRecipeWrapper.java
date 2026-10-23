package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import techguns.gui.PoweredTileEntGui;
import techguns.tileentities.operation.IMachineRecipe;
import techguns.util.TextUtil;

/**
 * 1.12 IRecipeWrapper of the Techguns machines: holds the IMachineRecipe (ingredients), power tooltip, extra drawing.
 */
public abstract class BasicRecipeWrapper {

	public IMachineRecipe recipe;

	public BasicRecipeWrapper(IMachineRecipe recipe) {
		super();
		this.recipe = recipe;
	}

	/**
	 * Coordinates relative to the recipe (like 1.12)
	 */
	public List<Component> getTooltipStrings(int mouseX, int mouseY) {

		if (BasicRecipeCategory.isInRect(mouseX, mouseY, 8 + BasicRecipeCategory.JEI_OFFSET_X, 17 + BasicRecipeCategory.JEI_OFFSET_Y, 6, 60)) {

			List<Component> tooltip = new ArrayList<>();
			tooltip.add(Component.literal(TextUtil.trans("techguns.container.power") + ":"));

			tooltip.add(Component.literal("-" + this.getRFperTick() + " " + PoweredTileEntGui.POWER_UNIT + "/t"));
			tooltip.add(Component.literal("-" + this.getRFperTick() * this.getDuration() + " " + PoweredTileEntGui.POWER_UNIT));

			return tooltip;
		} else {
			return Collections.emptyList();
		}

	}

	protected int getDuration() {
		return 20 * 5;
	}

	protected abstract int getRFperTick();

	/**
	 * 1.12 IRecipeWrapper.drawInfo
	 */
	public void drawInfo(GuiGraphics guiGraphics, int mouseX, int mouseY) {
	}

	public List<List<ItemStack>> getItemInputs() {
		return recipe.getItemInputs();
	}

	public List<List<ItemStack>> getItemOutputs() {
		return recipe.getItemOutputs();
	}

	public List<List<FluidStack>> getFluidInputs() {
		return recipe.getFluidInputs();
	}

	public List<List<FluidStack>> getFluidOutputs() {
		return recipe.getFluidOutputs();
	}

	protected static Font font() {
		return Minecraft.getInstance().font;
	}

	protected static void drawString(GuiGraphics g, String s, int x, int y, int color) {
		g.drawString(font(), s, x, y, color, false);
	}

}
