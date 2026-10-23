package techguns.tileentities.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import techguns.items.armors.ICamoChangeable;

/**
 * Camo bench recipes for blocks.
 * <p>
 * 1.12 switched the metadata of the vanilla colored blocks (wool, concrete...), in 1.20 every color is its own item: a
 * recipe is the ordered list of the color variant items (1.12 metadata order = DyeColor order) and switching the camo
 * replaces the item of the stack (count and NBT are kept).
 * The Techguns camo blocks (1.12 ICamoChangeable blocks, TGLampCamoBenchRecipe) have an ICamoChangeable item in the port
 * (ItemBlockCamoChangeable, the variant is in the BlockStateTag), the camo bench switches them like the armors; their
 * recipes are only registered here for listings.
 */
public class CamoBenchRecipes {

	protected static LinkedHashMap<Item, CamoBenchRecipe> recipes = new LinkedHashMap<>();
	protected static ArrayList<CamoBenchRecipe> recipe_list = new ArrayList<>();

	public static void addRecipe(CamoBenchRecipe r) {
		r.items.forEach(i -> recipes.put(i, r));
		recipe_list.add(r);
	}

	@Nullable
	public static CamoBenchRecipe getRecipeFor(Item b) {
		return recipes.get(b);
	}

	/**
	 * Recipe for the 16 color variants "minecraft:<color>_<suffix>" (1.12 blocks with color metadata)
	 */
	public static CamoBenchRecipe colored(String suffix) {
		ArrayList<Item> items = new ArrayList<>();
		for (DyeColor c : DyeColor.values()) {
			Item it = BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft", c.getName() + "_" + suffix));
			if (it != null && it != Items.AIR) {
				items.add(it);
			}
		}
		return new CamoBenchRecipe(items);
	}

	public static class CamoBenchRecipe implements IMachineRecipe {

		protected List<Item> items;
		protected ICamoChangeable camoblock;
		protected boolean customMeta = false;

		/**
		 * The camo variants, in 1.12 metadata order
		 */
		public CamoBenchRecipe(Collection<Item> items) {
			super();
			this.items = new ArrayList<>(new LinkedHashSet<>(items));
		}

		/**
		 * Techguns camo block (its item switches the camo itself)
		 */
		public CamoBenchRecipe(Item item, ICamoChangeable camoblock) {
			super();
			this.items = new ArrayList<>();
			this.items.add(item);
			this.customMeta = true;
			this.camoblock = camoblock;
		}

		public int getCamoIndex(ItemStack item) {
			if (!customMeta) {
				return Math.max(0, this.items.indexOf(item.getItem()));
			} else if (this.camoblock != null) {
				return this.camoblock.getCurrentCamoIndex(item);
			}
			return 0;
		}

		public int getCamoCount() {
			if (!customMeta) {
				return this.items.size();
			} else if (this.camoblock != null) {
				return this.camoblock.getCamoCount();
			}
			return 1;
		}

		/**
		 * Returns the stack with the next (previous) camo, a new stack for the vanilla variant items
		 */
		public ItemStack switchCamo(ItemStack item, boolean back) {
			if (!customMeta) {
				int size = this.items.size();
				int m = getCamoIndex(item);
				int newMeta = back ? (m - 1 + size) % size : (m + 1) % size;
				ItemStack stack = new ItemStack(this.items.get(newMeta), item.getCount());
				if (item.getTag() != null) {
					stack.setTag(item.getTag().copy());
				}
				return stack;
			} else if (this.camoblock != null) {
				this.camoblock.switchCamo(item, back);
			}
			return item;
		}

		public ICamoChangeable getCamoblock() {
			return camoblock;
		}

		public boolean hasCustomMeta() {
			return customMeta;
		}

		@Override
		public List<List<ItemStack>> getItemInputs() {
			ArrayList<List<ItemStack>> list = new ArrayList<>();

			ArrayList<ItemStack> stacks = new ArrayList<>();

			if (!this.customMeta) {
				this.items.forEach(i -> stacks.add(new ItemStack(i, 1)));
			} else {
				ItemStack stack = new ItemStack(this.items.get(0), 1);
				for (int i = 0; i < this.camoblock.getCamoCount(); i++) {
					stacks.add(stack);
					stack = stack.copy();
					this.camoblock.switchCamo(stack);
				}
			}

			list.add(stacks);
			return list;
		}

		@Override
		public List<List<ItemStack>> getItemOutputs() {
			return this.getItemInputs();
		}

	}

	public static ArrayList<CamoBenchRecipe> getRecipes() {
		return recipe_list;
	}

}
