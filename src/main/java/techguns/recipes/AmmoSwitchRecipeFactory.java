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
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.ammo.AmmoType;

/**
 * Shapeless recipe gun + magazine: copies the gun NBT and switches the ammo variant to the one of the magazine.
 * Recipe type techguns:ammo_change_crafting
 */
public class AmmoSwitchRecipeFactory {

	public static final RecipeSerializer<AmmoSwitchRecipe> SERIALIZER = new TGRecipeSerializers.Shapeless<>(AmmoSwitchRecipe::new);

	public static class AmmoSwitchRecipe extends ShapelessRecipe {

		public AmmoSwitchRecipe(ResourceLocation id, String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> input) {
			super(id, group, category, result, input);
		}

		@Override
		public ItemStack assemble(CraftingContainer var1, RegistryAccess registryAccess) {
			int slot = 0;

			int slot_ammo = 0;

			for (int i = 0; i < var1.getContainerSize(); i++) {
				if (!var1.getItem(i).isEmpty()) {
					if (var1.getItem(i).getItem() instanceof GenericGun) {
						slot = i;
					} else if (var1.getItem(i).getItem() instanceof ITGSpecialSlot && (((ITGSpecialSlot) var1.getItem(i).getItem()).getSlot(var1.getItem(i)) == TGSlotType.AMMOSLOT)) {
						slot_ammo = i;
					}
				}
			}
			ItemStack gun = var1.getItem(slot);
			ItemStack ammo = var1.getItem(slot_ammo);

			CompoundTag tags = gun.getTag();
			CompoundTag newTags = null;
			if (tags != null && gun.getItem() instanceof GenericGun) {
				newTags = tags.copy();

				GenericGun g = (GenericGun) gun.getItem();
				AmmoType type = g.getAmmoType();

				String variant = type.getAmmoVariantKeyfor(ammo, 0);
				newTags.putString("ammovariant", variant);

				if (g.getAmmoCount() > 1) {
					newTags.putShort("ammo", (short) 1);
				} else {
					newTags.putShort("ammo", (short) g.getClipsize());
				}
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
