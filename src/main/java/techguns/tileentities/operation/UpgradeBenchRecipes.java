package techguns.tileentities.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import techguns.TGArmors;
import techguns.items.armors.GenericArmor;

public class UpgradeBenchRecipes {

	public static ArrayList<UpgradeBenchRecipe> recipes = new ArrayList<>();

	public static void addRecipe(ItemStack upgradeItem, Enchantment ench, int level) {
		recipes.add(new UpgradeBenchRecipe(upgradeItem, ench, level));
	}

	/**
	 * 1.12 TGItems shared item stacks are items in the port
	 */
	public static void addRecipe(Item upgradeItem, Enchantment ench, int level) {
		addRecipe(new ItemStack(upgradeItem), ench, level);
	}

	@Nullable
	public static UpgradeBenchRecipe getUpgradeRecipeFor(ItemStack armor, ItemStack item) {
		for (int i = 0; i < recipes.size(); i++) {
			if (recipes.get(i).matches(armor, item)) {
				return recipes.get(i);
			}
		}
		return null;
	}

	@Nullable
	public static UpgradeBenchRecipe getUpgradeRecipeForUpgradeItem(ItemStack item) {
		for (int i = 0; i < recipes.size(); i++) {
			if (recipes.get(i).matchesUpgradeItem(item)) {
				return recipes.get(i);
			}
		}
		return null;
	}

	public static class UpgradeBenchRecipe implements IMachineRecipe {
		protected ItemStack upgradeItem;
		protected Enchantment ench;
		protected int level;

		public UpgradeBenchRecipe(ItemStack upgradeItem, Enchantment ench, int level) {
			this.upgradeItem = upgradeItem;
			this.ench = ench;
			this.level = level;
		}

		public boolean isValidItemInput(ItemStack item) {
			if (!item.isEmpty() && item.getItem() instanceof GenericArmor) {
				return ench.canEnchant(item);
			}
			return false;
		}

		public boolean matches(ItemStack armor, ItemStack item) {
			return isValidItemInput(armor) && matchesUpgradeItem(item);
		}

		public boolean matchesUpgradeItem(ItemStack item) {
			return !item.isEmpty() && ItemStack.isSameItem(upgradeItem, item);
		}

		public Enchantment getEnch() {
			return ench;
		}

		public int getLevel() {
			return level;
		}

		@Override
		public List<List<ItemStack>> getItemInputs() {
			ArrayList<List<ItemStack>> inputs = new ArrayList<>();

			ArrayList<ItemStack> armors = new ArrayList<>();
			TGArmors.armors.forEach(a -> {
				ItemStack stack = new ItemStack(a, 1);
				if (ench.canEnchant(stack)) {
					armors.add(stack);
				}
			});
			inputs.add(armors);
			inputs.add(NonNullList.<ItemStack>withSize(1, this.upgradeItem));
			return inputs;
		}

		@Override
		public List<List<ItemStack>> getItemOutputs() {

			ArrayList<List<ItemStack>> outputs = new ArrayList<>();

			ArrayList<ItemStack> armors = new ArrayList<>();
			TGArmors.armors.forEach(a -> {
				ItemStack stack = new ItemStack(a, 1);

				if (ench.canEnchant(stack)) {
					Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
					map.put(this.getEnch(), this.getLevel());
					EnchantmentHelper.setEnchantments(map, stack);

					armors.add(stack);
				}
			});
			outputs.add(armors);

			return outputs;
		}

	}

}
