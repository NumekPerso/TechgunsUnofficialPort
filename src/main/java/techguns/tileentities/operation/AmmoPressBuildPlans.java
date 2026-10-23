package techguns.tileentities.operation;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import techguns.TGItems;
import techguns.tileentities.AmmoPressTileEnt;
import techguns.util.ItemStackOreDict;

/**
 * Ammo press recipes: 1 metal1 + 2 metal2 + 1 powder -> rounds of the selected build plan.
 * The valid inputs are item tags (1.12 ore dictionary names), checked at runtime (see TGMachineRecipes).
 */
public class AmmoPressBuildPlans {
	public static final int AMMOUNT_PISTOL = 12;
	public static final int AMMOUNT_SHOTGUN = 16;
	public static final int AMMOUNT_RIFLE = 8;
	public static final int AMMOUNT_SNIPER = 4;
	public static ArrayList<ItemStackOreDict> metal1 = new ArrayList<ItemStackOreDict>();
	public static ArrayList<ItemStackOreDict> metal2 = new ArrayList<ItemStackOreDict>();
	public static ArrayList<ItemStackOreDict> powder = new ArrayList<ItemStackOreDict>();

	/**
	 * @param metal1_names item tag names for the first metal slot (1 needed)
	 * @param metal2_names item tag names for the second metal slot (2 needed)
	 * @param powder_names item tag names for the powder slot (1 needed)
	 */
	public static void init(ArrayList<String> metal1_names, ArrayList<String> metal2_names, ArrayList<String> powder_names) {

		if (metal1_names != null) {
			for (int i = 0; i < metal1_names.size(); i++) {
				metal1.add(new ItemStackOreDict(metal1_names.get(i), 1));
			}
		}

		if (metal2_names != null) {
			for (int i = 0; i < metal2_names.size(); i++) {
				metal2.add(new ItemStackOreDict(metal2_names.get(i), 2));
			}
		}

		if (powder_names != null) {
			for (int i = 0; i < powder_names.size(); i++) {
				powder.add(new ItemStackOreDict(powder_names.get(i), 1));
			}
		}

	}

	public static int getAmountForRecipe(ItemStack output) {
		if (output.getItem() == TGItems.PISTOL_ROUNDS) {
			return AMMOUNT_PISTOL;
		} else if (output.getItem() == TGItems.SHOTGUN_ROUNDS) {
			return AMMOUNT_SHOTGUN;
		} else if (output.getItem() == TGItems.RIFLE_ROUNDS) {
			return AMMOUNT_RIFLE;
		} else if (output.getItem() == TGItems.SNIPER_ROUNDS) {
			return AMMOUNT_SNIPER;
		}
		return 1;
	}

	public static ItemStack[] getPossibleResults() {
		return new ItemStack[] { TGItems.newStack(TGItems.PISTOL_ROUNDS, AMMOUNT_PISTOL), TGItems.newStack(TGItems.SHOTGUN_ROUNDS, AMMOUNT_SHOTGUN),
				TGItems.newStack(TGItems.RIFLE_ROUNDS, AMMOUNT_RIFLE), TGItems.newStack(TGItems.SNIPER_ROUNDS, AMMOUNT_SNIPER) };
	}

	public static boolean isInList(ItemStack it, ArrayList<ItemStackOreDict> list) {
		if (it.isEmpty()) {
			return false;
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isEqualWithOreDict(it)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidFor(ItemStack input, int slot) {
		switch (slot) {
		case AmmoPressTileEnt.SLOT_METAL1:
			return isInList(input, metal1);
		case AmmoPressTileEnt.SLOT_METAL2:
			return isInList(input, metal2);
		case AmmoPressTileEnt.SLOT_POWDER:
			return isInList(input, powder);
		default:
			return false;
		}
	}

	public static int getTotalPower(int recipeIndex) {
		return 100 * 5;
	}

	/**
	 * Output stack of a build plan (0 pistol, 1 shotgun, 2 rifle, 3 sniper)
	 */
	public static ItemStack getOutputForPlan(int plan) {
		switch (plan) {
		case 0:
			return TGItems.newStack(TGItems.PISTOL_ROUNDS, AMMOUNT_PISTOL);
		case 1:
			return TGItems.newStack(TGItems.SHOTGUN_ROUNDS, AMMOUNT_SHOTGUN);
		case 2:
			return TGItems.newStack(TGItems.RIFLE_ROUNDS, AMMOUNT_RIFLE);
		case 3:
			return TGItems.newStack(TGItems.SNIPER_ROUNDS, AMMOUNT_SNIPER);
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack getOutputFor(ItemStack metal1, ItemStack metal2, ItemStack powder, int plan) {
		if (isValidFor(metal1, 0) && isValidFor(metal2, 1) && isValidFor(powder, 2)) {

			if (metal1.getCount() >= 1 && metal2.getCount() >= 2 && powder.getCount() >= 1) {
				return getOutputForPlan(plan);
			}

		}
		return ItemStack.EMPTY;
	}

	protected static List<ItemStack> getStacks(ArrayList<ItemStackOreDict> list) {
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		list.forEach(i -> stacks.addAll(i.getItemStacks()));
		return stacks;
	}

	public static class AmmoPressMachineRecipe implements IMachineRecipe {

		byte plan;

		public AmmoPressMachineRecipe(int plan) {
			super();
			this.plan = (byte) plan;
		}

		@Override
		public List<List<ItemStack>> getItemInputs() {
			List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();

			inputs.add(getStacks(metal1));
			inputs.add(getStacks(metal2));
			inputs.add(getStacks(powder));

			return inputs;
		}

		@Override
		public List<List<ItemStack>> getItemOutputs() {
			List<List<ItemStack>> outputs = new ArrayList<List<ItemStack>>();
			ArrayList<ItemStack> output = new ArrayList<ItemStack>();
			output.add(getOutputForPlan(plan));
			outputs.add(output);
			return outputs;
		}
	}

	public static IMachineRecipe getRecipeForType(int type) {
		IMachineRecipe rec = new AmmoPressMachineRecipe(type);
		return rec;
	}
}
