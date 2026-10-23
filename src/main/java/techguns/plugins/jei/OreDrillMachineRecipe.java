package techguns.plugins.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import techguns.TGBlocks;
import techguns.TGFluids;
import techguns.TGItems;
import techguns.TGOreClusters;
import techguns.TGOreClusters.OreClusterWeightedEntry;
import techguns.blocks.EnumOreClusterType;
import techguns.tileentities.operation.IMachineRecipe;

/**
 * JEI display of one ore cluster entry: drill heads + ore cluster + furnace fuel + fuel fluid -> ore (or fluid)
 */
public class OreDrillMachineRecipe implements IMachineRecipe {

	protected final OreClusterWeightedEntry entry;

	protected static List<ItemStack> furnaceFuels = null;
	protected static ArrayList<FluidStack> fuelList = null;
	protected static ArrayList<ItemStack> drills = null;

	protected EnumOreClusterType type;

	protected float chance;

	public OreDrillMachineRecipe(OreClusterWeightedEntry entry, EnumOreClusterType type, List<ItemStack> furnaceFuels, int totalweight) {
		super();
		this.entry = entry;
		this.type = type;
		OreDrillMachineRecipe.furnaceFuels = furnaceFuels;
		this.chance = totalweight > 0 ? (1.0f * entry.getWeight().asInt()) / (totalweight * 1.0f) : 0f;
	}

	/**
	 * Reset the cached lists when JEI reloads (fuel fluids come from tags)
	 */
	public static void clearCache() {
		fuelList = null;
		drills = null;
	}

	@Override
	public List<List<ItemStack>> getItemInputs() {
		List<List<ItemStack>> list = IMachineRecipe.super.getItemInputs();
		list.add(getDrills());
		List<ItemStack> cluster = new ArrayList<>();
		if (TGBlocks.getOreCluster(type) != null) {
			cluster.add(new ItemStack(TGBlocks.getOreCluster(type)));
		}
		list.add(cluster);
		list.add(furnaceFuels != null ? furnaceFuels : new ArrayList<>());
		return list;
	}

	@Override
	public List<List<ItemStack>> getItemOutputs() {
		List<List<ItemStack>> list = IMachineRecipe.super.getItemOutputs();
		ItemStack ore = entry.getOre();
		if (!ore.isEmpty()) {
			List<ItemStack> l = new ArrayList<>();
			l.add(ore);
			list.add(l);
		}
		return list;
	}

	public EnumOreClusterType getType() {
		return type;
	}

	protected ArrayList<ItemStack> getDrills() {
		if (drills == null) {
			drills = new ArrayList<ItemStack>(9);
			drills.add(TGItems.newStack(TGItems.OREDRILLHEAD_STEEL, 1));
			drills.add(TGItems.newStack(TGItems.OREDRILLHEAD_OBSIDIANSTEEL, 1));
			drills.add(TGItems.newStack(TGItems.OREDRILLHEAD_CARBON, 1));
			drills.add(TGItems.newStack(TGItems.OREDRILLHEAD_MEDIUM_STEEL, 1));
			drills.add(TGItems.newStack(TGItems.OREDRILLHEAD_MEDIUM_OBSIDIANSTEEL, 1));
			drills.add(TGItems.newStack(TGItems.OREDRILLHEAD_MEDIUM_CARBON, 1));
			drills.add(TGItems.newStack(TGItems.OREDRILLHEAD_LARGE_STEEL, 1));
			drills.add(TGItems.newStack(TGItems.OREDRILLHEAD_LARGE_OBSIDIANSTEEL, 1));
			drills.add(TGItems.newStack(TGItems.OREDRILLHEAD_LARGE_CARBON, 1));
		}
		return drills;
	}

	public float getChance() {
		return chance;
	}

	@Override
	public List<List<FluidStack>> getFluidInputs() {
		List<List<FluidStack>> list = IMachineRecipe.super.getFluidInputs();
		if (fuelList == null) {
			fuelList = new ArrayList<FluidStack>(TGFluids.fuels.size());
			for (Fluid f : TGFluids.fuels) {
				fuelList.add(new FluidStack(f, FluidType.BUCKET_VOLUME * 16));
			}
		}
		list.add(fuelList);
		return list;
	}

	@Override
	public List<List<FluidStack>> getFluidOutputs() {
		List<List<FluidStack>> list = IMachineRecipe.super.getFluidOutputs();
		FluidStack fluid = entry.getFluid();
		if (fluid != null) {
			List<FluidStack> f = new ArrayList<>();
			f.add(fluid);
			list.add(f);
		}
		return list;
	}

	public static TGOreClusters.OreCluster getCluster(EnumOreClusterType type) {
		return TGOreClusters.orecluster.getClusterForType(type);
	}
}
