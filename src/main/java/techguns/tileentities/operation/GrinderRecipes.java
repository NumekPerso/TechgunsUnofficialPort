package techguns.tileentities.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import techguns.TGArmors;
import techguns.TGItems;
import techguns.items.armors.GenericArmor;
import techguns.items.guns.GenericGun;
import techguns.tileentities.GrinderTileEnt;
import techguns.util.ItemStackOreDict;

public class GrinderRecipes {

	public static ArrayList<GrinderRecipe> recipes = new ArrayList<>();

	public static void addRecipe(ItemStackOreDict input, ItemStack... outputs) {
		recipes.add(new GrinderRecipe(input, outputs));
	}

	public static void addRecipe(ItemStack input, ItemStack... outputs) {
		recipes.add(new GrinderRecipe(new ItemStackOreDict(input, input.getCount()), outputs));
	}

	public static void removeRecipeFor(ItemStack input) {
		Iterator<GrinderRecipe> it = recipes.iterator();
		while (it.hasNext()) {
			GrinderRecipe r = it.next();
			if (r.input.matches(input)) {
				it.remove();
			}
		}
	}

	public static void addRecipe(GenericGun gun, ItemStack[]... outputs) {
		addRecipe(new ItemStack(gun, 1), outputs);
	}

	/**
	 * 1.12 TGItems shared item stacks are items in the port
	 */
	public static void addRecipe(Item input, ItemStack[]... outputs) {
		addRecipe(new ItemStack(input, 1), outputs);
	}

	public static void addRecipe(ItemStack input, ItemStack[]... outputs) {
		ArrayList<ItemStack> al = new ArrayList<>();
		Arrays.stream(outputs).forEach(o -> Arrays.stream(o).forEach(p -> al.add(p)));

		recipes.add(new GrinderRecipe(new ItemStackOreDict(input, input.getCount()), al.toArray(new ItemStack[al.size()])));
	}

	public static void addRecipeChance(Item input, ItemStack[] outputs, double[] chances) {
		addRecipeChance(new ItemStack(input, 1), outputs, chances);
	}

	public static void addRecipeChance(ItemStack input, ItemStack[] outputs, double[] chances) {
		recipes.add(new GrinderRecipeChance(new ItemStackOreDict(input, input.getCount()), outputs, chances));
	}

	public static void addGenericArmorRecipes() {
		TGArmors.armors.forEach(a -> {
			recipes.add(new GrinderRecipeGenericArmor(a));
		});
	}

	public static MachineOperation getOperationForInput(ItemStack input, GrinderTileEnt tile) {
		for (GrinderRecipe r : recipes) {
			if (r.matchesInput(new ItemStackOreDict(input, 1))) {
				return r.getOperation(input, tile);
			}
		}
		return null;
	}

	public static boolean hasRecipeForInput(ItemStack input) {
		for (GrinderRecipe r : recipes) {
			if (r.matchesInput(new ItemStackOreDict(input, 1))) {
				return true;
			}
		}
		return false;
	}

	// ---- 1.12 TGMachineRecipes helpers for the grinder recipes ----

	public static ItemStack[] ironAndWood(int iron, int wood, int nuggets) {
		int size = (iron > 0 ? 1 : 0) + (wood > 0 ? 1 : 0) + (nuggets > 0 ? 1 : 0);
		ItemStack[] ret = new ItemStack[size];
		int i = 0;
		if (iron > 0) {
			ret[i++] = new ItemStack(Items.IRON_INGOT, iron);
		}
		if (wood > 0) {
			//1.12 Blocks.LOG meta 0 = oak log
			ret[i++] = new ItemStack(Blocks.OAK_LOG, wood);
		}
		if (nuggets > 0) {
			ret[i++] = new ItemStack(Items.IRON_NUGGET, nuggets);
		}
		return ret;
	}

	public static ItemStack[] steelAndPlastic(int steel, int plastic, int nuggets) {
		int size = (steel > 0 ? 1 : 0) + (plastic > 0 ? 1 : 0) + (nuggets > 0 ? 1 : 0);
		ItemStack[] ret = new ItemStack[size];
		int i = 0;
		if (steel > 0) {
			ret[i++] = TGItems.newStack(TGItems.INGOT_STEEL, steel);
		}
		if (plastic > 0) {
			ret[i++] = TGItems.newStack(TGItems.PLASTIC_SHEET, plastic);
		}
		if (nuggets > 0) {
			//1.12 also used INGOT_STEEL here
			ret[i++] = TGItems.newStack(TGItems.INGOT_STEEL, nuggets);
		}
		return ret;
	}

	public static ItemStack[] obsiAndPlastic(int steel, int plastic) {
		int size = (steel > 0 ? 1 : 0) + (plastic > 0 ? 1 : 0);
		ItemStack[] ret = new ItemStack[size];
		int i = 0;
		if (steel > 0) {
			ret[i++] = TGItems.newStack(TGItems.INGOT_OBSIDIAN_STEEL, steel);
		}
		if (plastic > 0) {
			ret[i++] = TGItems.newStack(TGItems.PLASTIC_SHEET, plastic);
		}
		return ret;
	}

	public static ItemStack[] carbonPlasticAndRedstone(int carbon, int plastic, int redstone) {
		int size = (carbon > 0 ? 1 : 0) + (plastic > 0 ? 1 : 0) + (redstone > 0 ? 1 : 0);
		ItemStack[] ret = new ItemStack[size];
		int i = 0;
		if (carbon > 0) {
			ret[i++] = TGItems.newStack(TGItems.CARBON_FIBERS, carbon);
		}
		if (plastic > 0) {
			ret[i++] = TGItems.newStack(TGItems.PLASTIC_SHEET, plastic);
		}
		if (redstone > 0) {
			ret[i++] = new ItemStack(Items.REDSTONE, redstone);
		}
		return ret;
	}

	public static class GrinderRecipe implements IMachineRecipe {
		protected ItemStackOreDict input;

		protected ItemStack[] outputs;

		public GrinderRecipe(ItemStackOreDict input, ItemStack... outputs) {
			super();
			this.input = input;
			this.outputs = outputs;
		}

		public boolean matchesInput(ItemStackOreDict input) {
			return this.input.matches(input);
		}

		public MachineOperation getOperation(ItemStack input, GrinderTileEnt tile) {

			ArrayList<ItemStack> inputs = new ArrayList<>(1);
			ItemStack it = input.copy();
			it.setCount(1);
			inputs.add(it);

			ArrayList<ItemStack> outputs = new ArrayList<>(this.outputs.length);
			Arrays.stream(this.outputs).forEach(i -> outputs.add(i));

			MachineOperation op = new MachineOperation(inputs, outputs, null, null, 1);
			return op;
		}

		@Override
		public List<List<ItemStack>> getItemInputs() {
			List<List<ItemStack>> list = new ArrayList<>();
			list.add(input.getItemStacks());
			return list;
		}

		@Override
		public List<List<ItemStack>> getItemOutputs() {
			List<List<ItemStack>> list = new ArrayList<>();
			Arrays.stream(this.outputs).forEach(s -> {
				ArrayList<ItemStack> list2 = new ArrayList<>();
				list2.add(s);
				list.add(list2);
			});
			return list;
		}

	}

	public static class GrinderRecipeChance extends GrinderRecipe {

		protected double[] chances;

		public GrinderRecipeChance(ItemStackOreDict input, ItemStack[] outputs, double[] chances) {
			super(input, outputs);
			this.chances = chances;
			if (this.outputs.length != this.chances.length) {
				throw new IllegalArgumentException("Output and Chances array must be same size!");
			}
		}

		public double[] getChances() {
			return chances;
		}

		@Override
		public MachineOperationChance getOperation(ItemStack input, GrinderTileEnt tile) {
			ArrayList<ItemStack> inputs = new ArrayList<>(1);
			ItemStack it = input.copy();
			it.setCount(1);
			inputs.add(it);

			ArrayList<ItemStack> outputs = new ArrayList<>(this.outputs.length);
			Arrays.stream(this.outputs).forEach(i -> outputs.add(i));

			return new MachineOperationChance(inputs, outputs, null, null, 1, chances);
		}
	}

	public static class GrinderRecipeGenericArmor extends GrinderRecipe {
		protected GenericArmor armor;

		public GrinderRecipeGenericArmor(GenericArmor armor) {
			super(new ItemStackOreDict(new ItemStack(armor), 1), ItemStack.EMPTY);
			this.armor = armor;
		}

		@Override
		public MachineOperation getOperation(ItemStack input, GrinderTileEnt tile) {
			ArrayList<ItemStack> inputs = new ArrayList<>(1);
			inputs.add(input);

			ArrayList<ItemStack> outputs = new ArrayList<>();

			ItemStack inverted = input.copy();
			inverted.setDamageValue(input.getMaxDamage() - input.getDamageValue());
			this.armor.getRepairMats(inverted).forEach(i -> outputs.add(i.copy()));

			MachineOperation op = new MachineOperation(inputs, outputs, null, null, 1);
			return op;
		}

		@Override
		public boolean matchesInput(ItemStackOreDict input) {
			return input.item != null && !input.isEmpty() && this.armor == input.item.getItem();
		}

		@Override
		public List<List<ItemStack>> getItemInputs() {
			List<List<ItemStack>> list = new ArrayList<List<ItemStack>>();
			List<ItemStack> list2 = NonNullList.<ItemStack>withSize(1, new ItemStack(armor));
			list.add(list2);
			return list;
		}

		@Override
		public List<List<ItemStack>> getItemOutputs() {
			List<List<ItemStack>> list = new ArrayList<List<ItemStack>>();
			ItemStack broken = new ItemStack(armor, 1);
			broken.setDamageValue(armor.getMaxDamage(broken));
			armor.getRepairMats(broken).forEach(i -> {
				list.add(NonNullList.<ItemStack>withSize(1, i));
			});
			return list;
		}
	}
}
