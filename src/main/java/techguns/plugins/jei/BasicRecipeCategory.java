package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidStack;
import techguns.Techguns;
import techguns.gui.AmmoPressGui;

/**
 * Base JEI category of the Techguns machines: the background is the machine GUI texture (7,15 162x63),
 * slot positions are the container slot positions shifted by JEI_OFFSET_X/Y (like in 1.12).
 */
public abstract class BasicRecipeCategory<T extends BasicRecipeWrapper> implements IRecipeCategory<T> {

	protected IDrawable background;
	protected final Component localizedName;

	protected final IGuiHelper guiHelper;

	public static final int JEI_OFFSET_X = -8;
	public static final int JEI_OFFSET_Y = -16;

	protected final RecipeType<T> uid;

	protected final IDrawable icon;

	protected IDrawableAnimated powerbar;
	protected IDrawableStatic powerbar_static;

	public BasicRecipeCategory(IGuiHelper guiHelper, ResourceLocation guiTexture, String name, RecipeType<T> UID, ItemLike catalyst) {
		background = guiHelper.createDrawable(guiTexture, 7, 15, 162, 63);
		localizedName = Component.translatable(Techguns.MODID + ".container." + name);
		this.guiHelper = guiHelper;
		this.uid = UID;
		this.icon = guiHelper.createDrawableItemLike(catalyst);
		this.powerbar_static = guiHelper.createDrawable(AmmoPressGui.texture, 250, 0, 6, 50);
		this.powerbar = guiHelper.createAnimatedDrawable(powerbar_static, 100, IDrawableAnimated.StartDirection.TOP, true);
	}

	@Override
	public RecipeType<T> getRecipeType() {
		return uid;
	}

	@Override
	public Component getTitle() {
		return localizedName;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public int getWidth() {
		return background.getWidth();
	}

	@Override
	public int getHeight() {
		return background.getHeight();
	}

	@Override
	public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		background.draw(guiGraphics);
		this.drawExtras(guiGraphics);
		recipe.drawInfo(guiGraphics, (int) mouseX, (int) mouseY);
	}

	/**
	 * 1.12 IRecipeCategory.drawExtras
	 */
	protected void drawExtras(GuiGraphics guiGraphics) {
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		tooltip.addAll(recipe.getTooltipStrings((int) mouseX, (int) mouseY));
	}

	/**
	 * Item slot at the position of a container slot (1.12 guiItemStacks.init(i, input, slotX+JEI_OFFSET_X, slotY+JEI_OFFSET_Y),
	 * the 1.12 item was drawn 1 pixel inside)
	 */
	protected static IRecipeSlotBuilder addItemSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y, List<ItemStack> stacks) {
		return addItemSlotRaw(builder, role, x + JEI_OFFSET_X, y + JEI_OFFSET_Y, stacks);
	}

	/**
	 * Item slot at raw 1.12 JEI coordinates
	 */
	protected static IRecipeSlotBuilder addItemSlotRaw(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y, List<ItemStack> stacks) {
		IRecipeSlotBuilder slot = builder.addSlot(role, x + 1, y + 1);
		List<ItemStack> list = filterItems(stacks);
		if (!list.isEmpty()) {
			slot.addItemStacks(list);
		}
		return slot;
	}

	/**
	 * Fluid tank at raw 1.12 JEI coordinates (guiFluidStacks.init)
	 */
	protected static IRecipeSlotBuilder addFluidSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y, int w, int h, int capacity, IDrawable overlay, List<FluidStack> fluids) {
		IRecipeSlotBuilder slot = builder.addSlot(role, x, y).setFluidRenderer(capacity, false, w, h);
		if (overlay != null) {
			slot.setOverlay(overlay, 0, 0);
		}
		List<FluidStack> list = filterFluids(fluids);
		if (!list.isEmpty()) {
			slot.addIngredients(ForgeTypes.FLUID_STACK, list);
		}
		return slot;
	}

	protected static <E> List<E> get(List<List<E>> lists, int index) {
		if (lists != null && index < lists.size() && lists.get(index) != null) {
			return lists.get(index);
		}
		return Collections.emptyList();
	}

	protected static List<ItemStack> filterItems(List<ItemStack> stacks) {
		List<ItemStack> list = new ArrayList<>();
		if (stacks != null) {
			for (ItemStack s : stacks) {
				if (s != null && !s.isEmpty()) {
					list.add(s);
				}
			}
		}
		return list;
	}

	protected static List<FluidStack> filterFluids(List<FluidStack> fluids) {
		List<FluidStack> list = new ArrayList<>();
		if (fluids != null) {
			for (FluidStack f : fluids) {
				if (f != null && !f.isEmpty()) {
					list.add(f);
				}
			}
		}
		return list;
	}

	public static boolean isInRect(int mouseX, int mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

}
