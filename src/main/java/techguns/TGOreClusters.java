package techguns;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import techguns.blocks.EnumOreClusterType;
import techguns.blocks.IEnumOreClusterType;
import techguns.tileentities.operation.MachineOperation;

/**
 * Ore cluster data (1.12 TGOreClusters, instance was Techguns.orecluster): mining level and multipliers from the config,
 * weighted ores each cluster yields when mined by the ore drill.
 * 1.12 ore dictionary entries are item tags, the oil cluster uses TGFluids.worldspawn_oils (fluid tag techguns:oil_worldspawn, Techguns has
 * no oil fluid itself, like in 1.12 it comes from other mods). Tags are resolved when an operation starts.
 */
public class TGOreClusters {

	public static final TGOreClusters orecluster = new TGOreClusters();

	protected EnumMap<EnumOreClusterType, OreCluster> registry = new EnumMap<>(EnumOreClusterType.class);

	private boolean recipesInitialized = false;

	public TGOreClusters() {
		this.reloadConfig();
	}

	public OreCluster getClusterForType(IEnumOreClusterType type) {
		return registry.get(type);
	}

	/**
	 * 1.12 preInit: (re)reads the config values, keeps the ore entries
	 */
	public void reloadConfig() {
		put(EnumOreClusterType.COAL, TGConfig.mininglevel_coal, TGConfig.oremult_coal, TGConfig.powermult_coal);
		put(EnumOreClusterType.COMMON_METAL, TGConfig.mininglevel_common_metal, TGConfig.oremult_common_metal, TGConfig.powermult_common_metal);
		put(EnumOreClusterType.RARE_METAL, TGConfig.mininglevel_rare_metal, TGConfig.oremult_rare_metal, TGConfig.powermult_rare_metal);
		put(EnumOreClusterType.SHINY_METAL, TGConfig.mininglevel_shiny_metal, TGConfig.oremult_shiny_metal, TGConfig.powermult_shiny_metal);
		put(EnumOreClusterType.URANIUM, TGConfig.mininglevel_uranium, TGConfig.oremult_uranium, TGConfig.powermult_uranium);
		put(EnumOreClusterType.COMMON_GEM, TGConfig.mininglevel_common_gem, TGConfig.oremult_common_gem, TGConfig.powermult_common_gem);
		put(EnumOreClusterType.SHINY_GEM, TGConfig.mininglevel_shiny_gem, TGConfig.oremult_shiny_gem, TGConfig.powermult_shiny_gem);
		put(EnumOreClusterType.NETHER_CRYSTAL, TGConfig.mininglevel_nether_crystal, TGConfig.oremult_nether_crystal, TGConfig.powermult_nether_crystal);
		put(EnumOreClusterType.OIL, TGConfig.mininglevel_oil, TGConfig.oremult_oil, TGConfig.powermult_oil);
	}

	protected void put(EnumOreClusterType type, int mininglevel, double amount, double power) {
		OreCluster c = registry.get(type);
		if (c == null) {
			registry.put(type, new OreCluster(mininglevel, amount, power));
		} else {
			c.mininglevel = mininglevel;
			c.multiplier_amount = amount;
			c.multiplier_power = power;
		}
	}

	/**
	 * Called once from TGMachineRecipes (items registered)
	 */
	public void RecipeInit() {
		if (recipesInitialized) {
			return;
		}
		recipesInitialized = true;

		this.addOreToCluster(new ItemStack(Items.COAL_ORE), EnumOreClusterType.COAL, 99);
		this.addOreToCluster(new ItemStack(Items.DIAMOND), EnumOreClusterType.COAL, 1);

		this.addOreToCluster(new ItemStack(Items.IRON_ORE), EnumOreClusterType.COMMON_METAL, 45);
		this.addOreToCluster(new ItemStack(TGBlocks.ORE_COPPER), EnumOreClusterType.COMMON_METAL, 30);
		this.addOreToCluster(new ItemStack(TGBlocks.ORE_TIN), EnumOreClusterType.COMMON_METAL, 25);

		this.addOreToCluster(new ItemStack(TGBlocks.ORE_LEAD), EnumOreClusterType.RARE_METAL, 50);

		this.addOreToCluster(new ItemStack(TGBlocks.ORE_TITANIUM), EnumOreClusterType.SHINY_METAL, 20);
		this.addOreToCluster(new ItemStack(Items.GOLD_ORE), EnumOreClusterType.SHINY_METAL, 40);

		this.addOreToCluster(new ItemStack(Items.REDSTONE_ORE), EnumOreClusterType.COMMON_GEM, 40);
		this.addOreToCluster(new ItemStack(Items.LAPIS_ORE), EnumOreClusterType.COMMON_GEM, 20);

		this.addOreToCluster(new ItemStack(Items.DIAMOND_ORE), EnumOreClusterType.SHINY_GEM, 40);
		this.addOreToCluster(new ItemStack(Items.EMERALD_ORE), EnumOreClusterType.SHINY_GEM, 20);

		this.addOreToCluster(new ItemStack(TGBlocks.ORE_URANIUM), EnumOreClusterType.URANIUM, 50);

		this.addOreToCluster(new ItemStack(Items.NETHER_QUARTZ_ORE), EnumOreClusterType.NETHER_CRYSTAL, 50);
		this.addOreToCluster(new ItemStack(Items.GLOWSTONE), EnumOreClusterType.NETHER_CRYSTAL, 40);
		this.addOreToCluster(new ItemStack(Items.BLAZE_ROD), EnumOreClusterType.NETHER_CRYSTAL, 10);

		//1.12: worldspawn oil (TGFluids.OIL_WORLDSPAWN, else first of TGFluids.worldspawn_oils), resolved when used
		this.addFluidTagToCluster(TGFluids.TAG_OIL_WORLDSPAWN.location().toString(), EnumOreClusterType.OIL, 10);

		//add modded ores from oreDict (item tags)
		this.addOreToCluster("forge:ores/silver", EnumOreClusterType.SHINY_METAL, 40);

		this.addOreToCluster("forge:ores/osmium", EnumOreClusterType.RARE_METAL, 50);
		this.addOreToCluster("forge:ores/aluminum", EnumOreClusterType.RARE_METAL, 50);

		this.addOreToCluster("forge:ores/certus_quartz", EnumOreClusterType.COMMON_GEM, 40);
		this.addOreToCluster("forge:ores/charged_certus_quartz", EnumOreClusterType.COMMON_GEM, 5);
	}

	public boolean removeOreFromCluster(ItemStack ore, EnumOreClusterType type) {
		OreCluster c = this.registry.get(type);
		if (c != null) {
			return c.oreEntries.remove(new OreClusterWeightedEntry(ore, 1));
		}
		return false;
	}

	public void addOreToCluster(ItemStack ore, EnumOreClusterType type, int weight) {
		OreCluster c = this.registry.get(type);
		if (c != null && !ore.isEmpty()) {
			ItemStack ore2 = ore.copy();
			ore2.setCount(1);
			OreClusterWeightedEntry entry = new OreClusterWeightedEntry(ore2, weight);
			if (!c.oreEntries.contains(entry)) {
				c.oreEntries.add(entry);
			}
		}
	}

	public void addFluidTagToCluster(String fluidTag, EnumOreClusterType type, int weight) {
		OreCluster c = this.registry.get(type);
		if (c != null) {
			OreClusterWeightedEntry entry = new OreClusterWeightedEntry(TagKey.create(Registries.FLUID, new ResourceLocation(fluidTag)), weight);
			if (!c.oreEntries.contains(entry)) {
				c.oreEntries.add(entry);
			}
		}
	}

	public void addOreToCluster(String oredict, EnumOreClusterType type, int weight) {
		OreCluster c = this.registry.get(type);
		if (c != null) {
			OreClusterWeightedEntry entry = new OreClusterWeightedEntry(oredict, weight);
			if (!c.oreEntries.contains(entry)) {
				c.oreEntries.add(entry);
			}
		}
	}

	public static class OreClusterWeightedEntry implements WeightedEntry {
		protected final net.minecraft.util.random.Weight weight;
		ItemStack ore = ItemStack.EMPTY;
		TagKey<Fluid> fluidTag = null;
		String oredictname = null;
		TagKey<Item> itemTag = null;

		public OreClusterWeightedEntry(ItemStack ore, int itemWeightIn) {
			this.weight = net.minecraft.util.random.Weight.of(itemWeightIn);
			this.ore = ore;
		}

		public OreClusterWeightedEntry(TagKey<Fluid> fluid, int itemWeightIn) {
			this.weight = net.minecraft.util.random.Weight.of(itemWeightIn);
			this.fluidTag = fluid;
		}

		/**
		 * Item tag (1.12 ore dictionary name)
		 */
		public OreClusterWeightedEntry(String oredictname, int itemWeightIn) {
			this.weight = net.minecraft.util.random.Weight.of(itemWeightIn);
			this.oredictname = oredictname;
			this.itemTag = TagKey.create(Registries.ITEM, new ResourceLocation(oredictname));
		}

		@Override
		public net.minecraft.util.random.Weight getWeight() {
			return weight;
		}

		/**
		 * Empty if the tag has no entries (1.12 skipped ore dictionary entries without ores)
		 */
		public boolean isEmpty() {
			return getOre().isEmpty() && getFluid() == null;
		}

		public ItemStack getOre() {
			if (itemTag != null) {
				for (Holder<Item> h : BuiltInRegistries.ITEM.getTagOrEmpty(itemTag)) {
					return new ItemStack(h.value());
				}
				return ItemStack.EMPTY;
			}
			return ore;
		}

		public FluidStack getFluid() {
			if (fluidTag != null && fluidTag.equals(TGFluids.TAG_OIL_WORLDSPAWN)) {
				//prefer oil which has a block implementation, otherwise use first oil
				Fluid oil = TGFluids.OIL_WORLDSPAWN != null ? TGFluids.OIL_WORLDSPAWN : (TGFluids.worldspawn_oils.isEmpty() ? null : TGFluids.worldspawn_oils.get(0));
				return oil != null ? new FluidStack(oil, FluidType.BUCKET_VOLUME) : null;
			}
			if (fluidTag != null) {
				for (Holder<Fluid> h : BuiltInRegistries.FLUID.getTagOrEmpty(fluidTag)) {
					if (h.value().isSource(h.value().defaultFluidState())) {
						return new FluidStack(h.value(), FluidType.BUCKET_VOLUME);
					}
				}
			}
			return null;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof OreClusterWeightedEntry) {
				OreClusterWeightedEntry other = (OreClusterWeightedEntry) obj;
				if (this.oredictname != null && other.oredictname != null) {
					return this.oredictname.equals(other.oredictname);

				} else if (!this.ore.isEmpty() && !other.ore.isEmpty()) {
					return this.ore.getItem() == other.ore.getItem();

				} else if (this.fluidTag != null && other.fluidTag != null) {
					return this.fluidTag.equals(other.fluidTag);
				}
			}
			return this == obj;
		}

		@Override
		public int hashCode() {
			return oredictname != null ? oredictname.hashCode() : (fluidTag != null ? fluidTag.hashCode() : ore.getItem().hashCode());
		}

	}

	public static class OreCluster {
		int mininglevel;
		double multiplier_amount;
		double multiplier_power;

		ArrayList<OreClusterWeightedEntry> oreEntries = new ArrayList<>();

		public OreCluster(int mininglevel, double multiplier_amount, double multiplier_power) {
			super();
			this.mininglevel = mininglevel;
			this.multiplier_amount = multiplier_amount;
			this.multiplier_power = multiplier_power;
		}

		public int getMininglevel() {
			return mininglevel;
		}

		public double getMultiplier_amount() {
			return multiplier_amount;
		}

		public double getMultiplier_power() {
			return multiplier_power;
		}

		public ArrayList<OreClusterWeightedEntry> getOreEntries() {
			return oreEntries;
		}

		public MachineOperation getNewOperation(Level w, double orePerHour, int radius, double powerMult) {
			List<OreClusterWeightedEntry> entries = new ArrayList<>();
			for (OreClusterWeightedEntry e : oreEntries) {
				if (!e.isEmpty()) {
					entries.add(e);
				}
			}
			if (!entries.isEmpty()) {
				RandomSource rand = w.random;
				Optional<OreClusterWeightedEntry> opt = WeightedRandom.getRandomItem(rand, entries);
				if (opt.isPresent()) {
					OreClusterWeightedEntry entry = opt.get();

					MachineOperation op = null;

					ItemStack ore = entry.getOre();
					FluidStack fluid = entry.getFluid();
					if (!ore.isEmpty()) {
						op = new MachineOperation(new ItemStack(ore.getItem(), 1));
					} else if (fluid != null) {
						op = new MachineOperation(ItemStack.EMPTY, fluid);
					}
					if (op != null) {
						op.setPowerPerTick((int) (8 * orePerHour * (1 + Math.max(radius - 1, 0) * 0.2) * powerMult * TGConfig.oreDrillMultiplierPower));
						return op;
					}
				}
			}
			return getCobbleStoneOperation();
		}

		public MachineOperation getCobbleStoneOperation() {
			MachineOperation op = new MachineOperation(new ItemStack(Items.COBBLESTONE));
			op.setPowerPerTick((int) (24 * TGConfig.oreDrillMultiplierPower));
			return op;
		}
	}
}
