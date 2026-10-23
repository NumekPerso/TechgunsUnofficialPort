package techguns.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import techguns.items.guns.GenericGunMeleeCharge;

/**
 * Shapeless recipe mining tool + mining head: copies the tool NBT and sets the "miningHead" tag
 * (read by GenericGunMeleeCharge.getMiningHeadLevel). Recipe type techguns:miningtool_upgrade
 */
public class MiningToolUpgradeHeadRecipeFactory {

	public static final String MINING_TOOL_UPGRADE_RECIPE = "miningtool_upgrade";

	public static final RecipeSerializer<MiningToolUpgradeRecipe> SERIALIZER = new TGRecipeSerializers.Shapeless<>(MiningToolUpgradeRecipe::new);

	public static class MiningToolUpgradeRecipe extends ShapelessRecipe {

		public MiningToolUpgradeRecipe(ResourceLocation id, String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> input) {
			super(id, group, category, result, input);
		}

		@Override
		public ItemStack assemble(CraftingContainer var1, RegistryAccess registryAccess) {
			int slot = 0;

			int slot_other = 0;

			for (int i = 0; i < var1.getContainerSize(); i++) {
				if (!var1.getItem(i).isEmpty()) {
					if (var1.getItem(i).getItem() instanceof GenericGunMeleeCharge) {
						slot = i;
					} else {
						slot_other = i;
					}
				}
			}
			ItemStack gun = var1.getItem(slot);
			ItemStack head = var1.getItem(slot_other);

			CompoundTag tags = gun.getTag();
			CompoundTag newTags = null;
			if (tags != null && gun.getItem() instanceof GenericGunMeleeCharge) {
				newTags = tags.copy();

				GenericGunMeleeCharge g = (GenericGunMeleeCharge) gun.getItem();

				newTags.putInt("miningHead", g.getMiningHeadLevelForHead(head));
			}
			ItemStack out = super.assemble(var1, registryAccess);
			if (newTags != null) {
				out.setTag(newTags);
			}

			return out;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return SERIALIZER;
		}
	}

}
