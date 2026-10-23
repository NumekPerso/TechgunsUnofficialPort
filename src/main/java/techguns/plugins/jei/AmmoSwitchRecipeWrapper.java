package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import techguns.items.guns.GenericGun;
import techguns.items.guns.ammo.AmmoType;
import techguns.recipes.AmmoSwitchRecipeFactory.AmmoSwitchRecipe;

/**
 * Shapeless gun + magazine recipe, the output shows the gun with the ammo variant of the magazine
 */
public class AmmoSwitchRecipeWrapper implements ICraftingCategoryExtension {

	public List<List<ItemStack>> inputs = new ArrayList<>();
	public ItemStack output;
	protected final ResourceLocation id;

	public AmmoSwitchRecipeWrapper(AmmoSwitchRecipe recipe) {
		this.id = recipe.getId();
		ItemStack ammo = ItemStack.EMPTY;
		ItemStack gun = ItemStack.EMPTY;

		for (Ingredient i : recipe.getIngredients()) {
			ItemStack[] items = i.getItems();
			if (items.length == 0) {
				continue;
			}
			ItemStack it = items[0].copy();
			if (!it.isEmpty() && !(it.getItem() instanceof GenericGun)) {
				ammo = it;
				inputs.add(List.of(it));
			} else if (!it.isEmpty() && it.getItem() instanceof GenericGun) {
				gun = it;
				inputs.add(List.of(it));
			}
		}

		output = recipe.getResultItem(RegistryAccess.EMPTY).copy();
		if (output.getItem() instanceof GenericGun g && !gun.isEmpty()) {
			if (gun.getTag() == null) {
				g.initTags(gun);
			}
			CompoundTag tags = gun.getTag().copy();
			AmmoType type = g.getAmmoType();
			String variant = type.getAmmoVariantKeyfor(ammo, 0);
			tags.putString("ammovariant", variant);
			output.setTag(tags);
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		craftingGridHelper.createAndSetInputs(builder, inputs, 0, 0);
		craftingGridHelper.createAndSetOutputs(builder, List.of(output));
		builder.setShapeless();
	}

	@Override
	public ResourceLocation getRegistryName() {
		return id;
	}

}
