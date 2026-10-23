package techguns.gui.widgets;

import java.util.ArrayList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import techguns.tileentities.operation.FabricatorRecipe;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.util.ItemStackOreDict;

public class SlotFabricator extends SlotMachineInput implements ITGSlotBackground {

	public static final ResourceLocation FABRICATOR_SLOTTEX_WIRES = ITGSlotBackground.WIRESSLOT_TEX;
	public static final ResourceLocation FABRICATOR_SLOTTEX_POWDER = ITGSlotBackground.POWDERSLOT_TEX;
	public static final ResourceLocation FABRICATOR_SLOTTEX_PLATE = ITGSlotBackground.PLATESLOT_TEX;

	protected ArrayList<ItemStackOreDict> validItems;

	protected ResourceLocation bgtexture;

	public SlotFabricator(ItemStackHandlerPlus itemHandler, int index, int xPosition, int yPosition, ArrayList<ItemStackOreDict> validItems, ResourceLocation bgtexture) {
		super(itemHandler, index, xPosition, yPosition);
		this.validItems = validItems;
		this.bgtexture = bgtexture;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return FabricatorRecipe.itemStackInList(validItems, stack);
	}

	@Override
	public ResourceLocation getBackgroundTexture() {
		return bgtexture;
	}

}
