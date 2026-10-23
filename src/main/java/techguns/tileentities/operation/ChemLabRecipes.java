package techguns.tileentities.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import techguns.tileentities.ChemLabTileEnt;
import techguns.tileentities.operation.ChemLabRecipes.ChemLabRecipe.ChemRecipeType;
import techguns.util.ItemStackOreDict;

/**
 * Chem lab recipes (1.12 ChemLabRecipes).
 * 1.20 additions: the fluid input can be a fluid tag (1.12 added one recipe per fluid of the fuel/oil lists), and a
 * recipe can have a condition checked at runtime (1.12 chose recipes at init, e.g. lava recipes only when no fuel
 * exists; fluid/item tags are only known after the datapack load in 1.20).
 */
public class ChemLabRecipes {

	private static ArrayList<ChemLabRecipe> recipes = new ArrayList<ChemLabRecipe>();

	private static ArrayList<ItemStack> bottleItems = new ArrayList<>();

	public static MachineOperation getOutputFor(ChemLabTileEnt tile) {
		ItemStack slot1 = tile.input1.get();
		ItemStack slot2 = tile.input2.get();
		ItemStack slot3 = tile.input_bottle.get();
		FluidStack fluidIn = tile.getCurrentInputFluid();

		for (int i = 0; i < recipes.size(); i++) {
			ChemLabRecipe recipe = recipes.get(i);
			if (!recipe.isEnabled()) {
				continue;
			}
			ChemRecipeType t = recipe.isValidInput(slot1, slot2, slot3, fluidIn);
			if (t != ChemRecipeType.INVALID) {
				return recipe.getOperationFor(tile, t);
			}
		}
		return null;
	}

	public static ArrayList<ChemLabRecipe> getRecipes() {
		return recipes;
	}

	public static int getTotalPower(int recipeIndex) {
		return recipes.get(recipeIndex).powerPerTick * 100;
	}

	public static ChemLabRecipe getRecipe(int index) {
		return recipes.get(index);
	}

	public static ArrayList<ChemLabRecipe> getRecipesUsing(ItemStack input) {
		ArrayList<ChemLabRecipe> ret = new ArrayList<ChemLabRecipe>();
		for (ChemLabRecipe r : recipes) {
			if (r.isEnabled() && r.isItemPartOfRecipe(input)) {
				ret.add(r);
			}
		}
		return ret;
	}

	public static ChemLabRecipe getRecipesFor(ItemStack output) {
		for (ChemLabRecipe r : recipes) {
			if (r.isEnabled() && ItemStack.isSameItemSameTags(r.output, output)) {
				return r;
			}
		}
		return null;
	}

	protected static void addToFlaskSlot(ItemStack item) {
		if (!item.isEmpty()) {
			for (int i = 0; i < bottleItems.size(); i++) {
				if (ItemStack.isSameItem(item, bottleItems.get(i))) {
					return;
				}
			}
			ItemStack s = item.copy();
			s.setCount(1);
			bottleItems.add(s);
		}
	}

	public static boolean allowInFlaskSlot(ItemStack item) {
		if (!item.isEmpty()) {
			for (int i = 0; i < bottleItems.size(); i++) {
				if (ItemStack.isSameItem(item, bottleItems.get(i))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasRecipeUsing(ItemStack item) {
		for (int i = 0; i < recipes.size(); i++) {
			if (recipes.get(i).isEnabled() && recipes.get(i).isItemPartOfRecipe(item)) {
				return true;
			}
		}
		return false;
	}

	public static boolean allowAsInput2(ItemStack slot1, ItemStack item) {
		for (int i = 0; i < recipes.size(); i++) {
			if (recipes.get(i).isEnabled() && recipes.get(i).isValidInputSlot1_2(slot1, item)) {
				return true;
			}
		}
		return false;
	}

	protected static ItemStackOreDict toOreDict(Object o) {
		if (o == null) {
			return ItemStackOreDict.EMPTY;
		} else if (o instanceof ItemStackOreDict) {
			return (ItemStackOreDict) o;
		} else if (o instanceof String) {
			return new ItemStackOreDict((String) o);
		} else if (o instanceof ItemStack) {
			return new ItemStackOreDict((ItemStack) o);
		} else if (o instanceof net.minecraft.world.level.ItemLike) {
			return new ItemStackOreDict(new ItemStack((net.minecraft.world.level.ItemLike) o));
		}
		throw new IllegalArgumentException("Invalid chem lab recipe input: " + o);
	}

	/**
	 * 1.12 addRecipe(input1, amount1, input2, amount2, bottle, amount3, fluidIn, fluidOut, output, swap, power).
	 * input1/input2: ItemStack, Item, tag name String ("forge:dusts/redstone"), ItemStackOreDict or null.
	 */
	public static ChemLabRecipe addRecipe(Object input1, int amount1, Object input2, int amount2, ItemStack bottle, int amount3, FluidStack fluidIn, FluidStack fluidOut, ItemStack output, boolean swap, int power) {
		ChemLabRecipe r = new ChemLabRecipe(toOreDict(input1), amount1, toOreDict(input2), amount2, bottle, amount3, fluidIn, swap, output, fluidOut, power);
		recipes.add(r);
		return r;
	}

	/**
	 * Recipe with a fluid tag as fluid input (amount in mB)
	 */
	public static ChemLabRecipe addRecipe(Object input1, int amount1, Object input2, int amount2, ItemStack bottle, int amount3, TagKey<Fluid> fluidTag, int fluidAmount, FluidStack fluidOut, ItemStack output, boolean swap, int power) {
		ChemLabRecipe r = new ChemLabRecipe(toOreDict(input1), amount1, toOreDict(input2), amount2, bottle, amount3, null, swap, output, fluidOut, power);
		r.fluidTag = fluidTag;
		r.amounts[3] = fluidAmount;
		recipes.add(r);
		return r;
	}

	public static class ChemLabRecipe implements IMachineRecipe {
		public ItemStackOreDict slot1;
		public ItemStackOreDict slot2;
		public ItemStack slot3;
		public FluidStack fluidIn;
		/**
		 * Fluid input as tag (1.20), null if fluidIn is used
		 */
		public TagKey<Fluid> fluidTag = null;
		public FluidStack fluidOutput;
		public boolean allowSwap;
		public ItemStack output;
		public int[] amounts = new int[4];
		public int powerPerTick = 5;
		/**
		 * Runtime condition (1.20), recipe is ignored when false
		 */
		protected BooleanSupplier condition = null;

		public boolean isItemPartOfRecipe(ItemStack item) {
			return (!this.slot1.isEmpty() && this.slot1.isEqualWithOreDict(item)) || (!this.slot2.isEmpty() && this.slot2.isEqualWithOreDict(item)) || (!this.slot3.isEmpty() && ItemStack.isSameItem(this.slot3, item));
		}

		public ChemLabRecipe(ItemStackOreDict slot1, int amount1, ItemStackOreDict slot2, int amount2, ItemStack bottle, int amount3, FluidStack fluidIn,
				boolean allowSwap, ItemStack output, FluidStack fluidOutput, int powerPerTick) {
			this.slot1 = slot1;
			this.slot2 = slot2;
			this.slot3 = bottle;
			if (this.slot3 == null) {
				this.slot3 = ItemStack.EMPTY;
			}
			this.fluidIn = fluidIn;
			this.fluidOutput = fluidOutput;
			this.allowSwap = allowSwap;
			this.output = output;
			if (this.output == null) {
				this.output = ItemStack.EMPTY;
			}
			this.amounts[0] = amount1;
			this.amounts[1] = amount2;
			this.amounts[2] = amount3;
			this.amounts[3] = fluidIn != null ? fluidIn.getAmount() : 0;
			this.powerPerTick = powerPerTick;

			addToFlaskSlot(this.slot3);
		}

		public ChemLabRecipe setCondition(BooleanSupplier condition) {
			this.condition = condition;
			return this;
		}

		public boolean isEnabled() {
			return this.condition == null || this.condition.getAsBoolean();
		}

		public static enum ChemRecipeType {
			INVALID,
			VALID,
			VALID_SWAPPED;
		}

		/**
		 * 1.12 OreDictionary.itemMatches(target, input, true) for the bottle slot
		 */
		protected boolean bottleMatches(ItemStack slot3) {
			return (!this.slot3.isEmpty() && ItemStack.isSameItem(this.slot3, slot3)) || (this.slot3.isEmpty() && slot3.isEmpty());
		}

		/**
		 * An empty ItemStackOreDict matches an empty slot (1.12 ItemStackOreDict(null) behaviour)
		 */
		protected static boolean slotMatches(ItemStackOreDict recipeSlot, ItemStack stack) {
			if (recipeSlot.isEmpty()) {
				return stack.isEmpty();
			}
			return recipeSlot.isEqualWithOreDict(stack);
		}

		//Returns if this contents are a valid input for this recipe;
		public ChemRecipeType isValidInput(ItemStack slot1, ItemStack slot2, ItemStack slot3, FluidStack fluidIn) {
			if (slotMatches(this.slot1, slot1) && slotMatches(this.slot2, slot2) && bottleMatches(slot3)) {
				if (checkLiquid(fluidIn) && checkAmounts(slot1, slot2, slot3, false)) {
					return ChemRecipeType.VALID;
				}
			}
			if (allowSwap && slotMatches(this.slot1, slot2) && slotMatches(this.slot2, slot1) && bottleMatches(slot3)) {
				if (checkLiquid(fluidIn) && checkAmounts(slot1, slot2, slot3, true)) {
					return ChemRecipeType.VALID_SWAPPED;
				}
			}
			return ChemRecipeType.INVALID;
		}

		public boolean isValidInputSlot1_2(ItemStack slot1, ItemStack slot2) {
			if (slotMatches(this.slot1, slot1) && slotMatches(this.slot2, slot2)) {
				return true;
			}
			if (allowSwap && (slotMatches(this.slot1, slot2) && slotMatches(this.slot2, slot1))) {
				return true;
			}
			return false;
		}

		private boolean isEnoughOrNull(ItemStack item, int amount) {
			if (item.isEmpty()) {
				return amount == 0;
			} else {
				return item.getCount() >= amount;
			}
		}

		private boolean checkAmounts(ItemStack slot1, ItemStack slot2, ItemStack slot3, boolean swapped12) {
			ItemStack i1 = slot1;
			ItemStack i2 = slot2;
			if (swapped12) {
				i2 = slot1;
				i1 = slot2;
			}

			return isEnoughOrNull(i1, this.amounts[0]) && isEnoughOrNull(i2, this.amounts[1]) && isEnoughOrNull(slot3, this.amounts[2]);
		}

		public boolean hasFluidInput() {
			return this.fluidIn != null || this.fluidTag != null;
		}

		private boolean checkLiquid(@Nullable FluidStack fluid) {
			if (!this.hasFluidInput()) {
				return true;
			} else if (fluid == null || fluid.isEmpty()) {
				//need fluid but input is null
				return false;
			}
			if (this.fluidTag != null) {
				return fluid.getFluid().is(this.fluidTag) && this.amounts[3] <= fluid.getAmount();
			}
			if (this.fluidIn.getFluid() == fluid.getFluid()) {
				return this.fluidIn.getAmount() <= fluid.getAmount();
			}
			return false;
		}

		public MachineOperation getOperationFor(ChemLabTileEnt tile, ChemRecipeType t) {
			ArrayList<ItemStack> inputs = new ArrayList<ItemStack>(3);

			ItemStack input1, input2;
			if (t == ChemRecipeType.VALID_SWAPPED) {
				input1 = tile.input2.get().copy();
				input1.setCount(this.amounts[1]);

				input2 = tile.input1.get().copy();
				input2.setCount(this.amounts[0]);
			} else {
				input1 = tile.input1.get().copy();
				input1.setCount(this.amounts[0]);

				input2 = tile.input2.get().copy();
				input2.setCount(this.amounts[1]);
			}

			ItemStack bottle = tile.input_bottle.get().copy();
			bottle.setCount(this.amounts[2]);

			inputs.add(input1);
			inputs.add(input2);
			inputs.add(bottle);

			ArrayList<FluidStack> fluidsIn = new ArrayList<FluidStack>(1);
			if (this.hasFluidInput()) {
				//the fluid in the tank (the recipe fluid or a fluid of the tag)
				FluidStack fluidin = tile.getCurrentInputFluid().copy();
				fluidin.setAmount(this.amounts[3]);
				fluidsIn.add(fluidin);
			}

			ArrayList<ItemStack> outputs = new ArrayList<ItemStack>(1);
			ItemStack output = this.output.copy();
			outputs.add(output);

			ArrayList<FluidStack> fluidsOut = new ArrayList<FluidStack>(1);
			if (this.fluidOutput != null) {
				FluidStack fluidout = this.fluidOutput.copy();
				fluidsOut.add(fluidout);
			}

			MachineOperation op = new MachineOperation(inputs, outputs, fluidsIn, fluidsOut, 1);
			op.setPowerPerTick(this.powerPerTick);
			return op;
		}

		@Override
		public List<List<ItemStack>> getItemInputs() {
			ArrayList<List<ItemStack>> ret = new ArrayList<>();

			ret.add(this.slot1.getItemStacks(this.amounts[0]));
			ret.add(this.slot2.getItemStacks(this.amounts[1]));

			ArrayList<ItemStack> bottle = new ArrayList<>();
			ItemStack b = this.slot3.copy();
			b.setCount(this.amounts[2]);
			bottle.add(b);
			ret.add(bottle);
			return ret;
		}

		@Override
		public List<List<ItemStack>> getItemOutputs() {
			ArrayList<List<ItemStack>> ret = new ArrayList<>();
			ArrayList<ItemStack> out = new ArrayList<>();
			out.add(this.output);
			ret.add(out);
			return ret;
		}

		@Override
		public List<List<FluidStack>> getFluidInputs() {
			ArrayList<List<FluidStack>> ret = new ArrayList<>();
			ArrayList<FluidStack> f = new ArrayList<>();
			if (this.fluidTag != null) {
				for (Holder<Fluid> h : BuiltInRegistries.FLUID.getTagOrEmpty(this.fluidTag)) {
					if (h.value().isSource(h.value().defaultFluidState())) {
						f.add(new FluidStack(h.value(), this.amounts[3]));
					}
				}
			} else {
				f.add(this.fluidIn);
			}
			ret.add(f);
			return ret;
		}

		@Override
		public List<List<FluidStack>> getFluidOutputs() {
			ArrayList<List<FluidStack>> ret = new ArrayList<>();
			ArrayList<FluidStack> f = new ArrayList<>();
			f.add(this.fluidOutput);
			ret.add(f);
			return ret;
		}

	}
}
