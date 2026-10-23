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
import techguns.items.guns.GenericGunMeleeCharge;
import techguns.recipes.MiningToolUpgradeHeadRecipeFactory.MiningToolUpgradeRecipe;

/**
 * Shapeless mining tool + mining head recipe, the output shows the tool with the new mining head level
 */
public class MiningToolUpgradeRecipeWrapper implements ICraftingCategoryExtension {

	public List<List<ItemStack>> inputs = new ArrayList<>();
	public ItemStack output;
	protected final ResourceLocation id;

	public MiningToolUpgradeRecipeWrapper(MiningToolUpgradeRecipe rec) {
		this.id = rec.getId();
		ItemStack head = ItemStack.EMPTY;
		ItemStack gun = ItemStack.EMPTY;

		for (Ingredient i : rec.getIngredients()) {
			ItemStack[] items = i.getItems();
			if (items.length == 0) {
				continue;
			}
			ItemStack it = items[0].copy();
			if (!it.isEmpty() && !(it.getItem() instanceof GenericGunMeleeCharge)) {
				head = it;
				inputs.add(List.of(it));
			} else if (!it.isEmpty() && it.getItem() instanceof GenericGunMeleeCharge) {
				gun = it;
				inputs.add(List.of(it));
			}
		}

		output = rec.getResultItem(RegistryAccess.EMPTY).copy();
		if (output.getItem() instanceof GenericGunMeleeCharge g && !gun.isEmpty()) {
			if (gun.getTag() == null) {
				g.initTags(gun);
			}
			CompoundTag tags = gun.getTag().copy();
			tags.putInt("miningHead", g.getMiningHeadLevelForHead(head));
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
