package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import techguns.TGConfig;
import techguns.TGOreClusters;
import techguns.TGOreClusters.OreCluster;
import techguns.TGOreClusters.OreClusterWeightedEntry;
import techguns.blocks.EnumOreClusterType;
import techguns.gui.PoweredTileEntGui;
import techguns.util.TextUtil;

public class OreDrillJeiRecipe extends BasicRecipeWrapper {

	protected final OreDrillMachineRecipe mr;

	public OreDrillJeiRecipe(OreDrillMachineRecipe recipe) {
		super(recipe);
		mr = recipe;
	}

	@Override
	public List<Component> getTooltipStrings(int mouseX, int mouseY) {
		if (BasicRecipeCategory.isInRect(mouseX, mouseY, 8 + BasicRecipeCategory.JEI_OFFSET_X, 17 + BasicRecipeCategory.JEI_OFFSET_Y, 6, 60)) {
			List<Component> tooltip = new ArrayList<>();
			OreCluster cluster = TGOreClusters.orecluster.getClusterForType(mr.getType());
			//if clustersize, mininglevel are 1, formula results in 4.5 * ...
			double ore_per_hour = 4.5 * cluster.getMultiplier_amount() * TGConfig.oreDrillMultiplierOres;
			int powerTick = (int) (8 * ore_per_hour * cluster.getMultiplier_power() * TGConfig.oreDrillMultiplierPower);

			tooltip.add(Component.literal(TextUtil.transTG("oredrill.powerperore") + ": "));
			tooltip.add(Component.literal((int) ((powerTick * 20 * 60 * 60) / ore_per_hour) + " " + PoweredTileEntGui.POWER_UNIT));
			return tooltip;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	protected int getRFperTick() {
		return 0;
	}

	public float getChance() {
		return mr.getChance();
	}

	/**
	 * @param furnaceFuels all items that burn in a furnace (1.12 IIngredientRegistry.getFuels())
	 */
	public static List<OreDrillJeiRecipe> getRecipes(List<ItemStack> furnaceFuels) {
		OreDrillMachineRecipe.clearCache();
		List<OreDrillJeiRecipe> recipes = new ArrayList<OreDrillJeiRecipe>();
		for (EnumOreClusterType type : EnumOreClusterType.values()) {
			OreCluster cluster = TGOreClusters.orecluster.getClusterForType(type);
			if (cluster == null) {
				continue;
			}
			//entries of empty tags (ores of other mods that are not installed) are never mined, like in the ore drill
			List<OreClusterWeightedEntry> entries = new ArrayList<>();
			int totalweight = 0;
			for (OreClusterWeightedEntry we : cluster.getOreEntries()) {
				if (!we.isEmpty()) {
					entries.add(we);
					totalweight += we.getWeight().asInt();
				}
			}
			for (OreClusterWeightedEntry we : entries) {
				recipes.add(new OreDrillJeiRecipe(new OreDrillMachineRecipe(we, type, furnaceFuels, totalweight)));
			}
		}
		return recipes;
	}

}
