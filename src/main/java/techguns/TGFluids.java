package techguns;

import java.util.ArrayList;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import techguns.blocks.BlockFluidAcid;

/**
 * Fluids of Techguns (1.12 TGFluids): fluid types, fluids, source/flowing blocks and buckets.
 *
 * 1.12 registered only 2 fluids itself: "creeper_acid" (block + bucket) and "milk" (if no other mod had milk).
 * In 1.20 milk is the Forge milk fluid (ForgeMod.enableMilkFluid, called from enableForgeFluids in the mod constructor):
 * it is filled/emptied with the vanilla milk bucket, there is no placeable milk block (1.12 block_milk only existed when
 * no other mod provided milk).
 *
 * The other fluids (oils, fuels, liquid redstone/coal/ender, biofuel) were taken from other mods by fluid name with
 * the config lists FluidListFuel/FluidListOil/FluidListOilWorldspawn. In 1.20 they are fluid tags (datapack
 * configurable) that include the usual forge tags as optional entries (data/techguns/tags/fluids):
 * techguns:fuel, techguns:oil, techguns:oil_worldspawn, techguns:liquid_redstone, techguns:liquid_coal,
 * techguns:liquid_ender, techguns:biofuel. They are resolved each time tags are (re)loaded (1.12 RecipeInit), with the
 * 1.12 fallbacks to lava/water when no mod provides them.
 *
 * Acid is not in the minecraft:water tag: the Forge FluidType gives swimming/drowning physics, the water tag would
 * make it behave like water in vanilla code (extinguishing, sponges, fishing...).
 */
public class TGFluids {

	/**
	 * Same as Fluids.WATER
	 */
	public static Fluid WATER = Fluids.WATER;
	/**
	 * Same as Fluids.LAVA
	 */
	public static Fluid LAVA = Fluids.LAVA;

	/**
	 * Forge milk fluid (minecraft:milk), set in the fluid RegisterEvent
	 */
	public static Fluid MILK;

	public static ArrayList<Fluid> oils = new ArrayList<Fluid>();

	public static ArrayList<Fluid> worldspawn_oils = new ArrayList<Fluid>();
	public static Fluid OIL_WORLDSPAWN = null;

	public static ArrayList<Fluid> fuels = new ArrayList<Fluid>();

	/**
	 * First fluid of techguns:liquid_redstone, lava if none (1.12 fallback)
	 */
	public static Fluid LIQUID_REDSTONE = Fluids.LAVA;
	/**
	 * First fluid of techguns:liquid_coal, water if none (1.12 fallback)
	 */
	public static Fluid LIQUID_COAL = Fluids.WATER;
	/**
	 * First fluid of techguns:liquid_ender, lava if none (1.12 fallback)
	 */
	public static Fluid LIQUID_ENDER = Fluids.LAVA;
	/**
	 * First fluid of techguns:biofuel, lava if none (1.12 fallback)
	 */
	public static Fluid BIOFUEL = Fluids.LAVA;

	/**
	 * Creeper acid (techguns:creeper_acid), flowing variant techguns:flowing_creeper_acid
	 */
	public static ForgeFlowingFluid ACID;
	public static ForgeFlowingFluid ACID_FLOWING;
	public static FluidType ACID_TYPE;

	public static Block BLOCK_FLUID_ACID;
	public static Item ACID_BUCKET;

	public static boolean addedMilk = false;
	public static boolean addedAcid = false;

	/**
	 * 1.12 TGConfig.keepLavaRecipesWhenFuelIsPresent (default false): keep the lava chem lab recipes even when fuels exist
	 */
	public static boolean keepLavaRecipesWhenFuelIsPresent = false;

	//registry names
	public static final ResourceLocation ACID_NAME = new ResourceLocation(Techguns.MODID, "creeper_acid");
	public static final ResourceLocation ACID_FLOWING_NAME = new ResourceLocation(Techguns.MODID, "flowing_creeper_acid");
	public static final ResourceLocation ACID_BLOCK_NAME = new ResourceLocation(Techguns.MODID, "block_creeper_acid");
	public static final ResourceLocation ACID_BUCKET_NAME = new ResourceLocation(Techguns.MODID, "creeper_acid_bucket");

	//fluid tags (replace the 1.12 config fluid name lists)
	public static final TagKey<Fluid> TAG_FUEL = tag("fuel");
	public static final TagKey<Fluid> TAG_OIL = tag("oil");
	public static final TagKey<Fluid> TAG_OIL_WORLDSPAWN = tag("oil_worldspawn");
	public static final TagKey<Fluid> TAG_LIQUID_REDSTONE = tag("liquid_redstone");
	public static final TagKey<Fluid> TAG_LIQUID_COAL = tag("liquid_coal");
	public static final TagKey<Fluid> TAG_LIQUID_ENDER = tag("liquid_ender");
	public static final TagKey<Fluid> TAG_BIOFUEL = tag("biofuel");

	private static TagKey<Fluid> tag(String name) {
		return TagKey.create(Registries.FLUID, new ResourceLocation(Techguns.MODID, name));
	}

	/**
	 * Called from the mod constructor (1.12 preInit): requests the Forge milk fluid
	 */
	public static void enableForgeFluids() {
		ForgeMod.enableMilkFluid();
		MinecraftForge.EVENT_BUS.addListener((TagsUpdatedEvent e) -> RecipeInit());
	}

	/**
	 * Registers fluid types, fluids, fluid blocks and buckets (check event.getRegistryKey())
	 */
	public static void register(RegisterEvent event) {
		if (event.getRegistryKey().equals(ForgeRegistries.Keys.FLUID_TYPES)) {
			event.register(ForgeRegistries.Keys.FLUID_TYPES, helper -> helper.register(ACID_NAME, getAcidType()));

		} else if (event.getRegistryKey().equals(Registries.FLUID)) {
			event.register(Registries.FLUID, helper -> {
				createAcidFluids();
				helper.register(ACID_NAME, ACID);
				helper.register(ACID_FLOWING_NAME, ACID_FLOWING);
				addedAcid = true;
			});

		} else if (event.getRegistryKey().equals(Registries.BLOCK)) {
			event.register(Registries.BLOCK, helper -> {
				helper.register(ACID_BLOCK_NAME, getAcidBlock());
			});

		} else if (event.getRegistryKey().equals(Registries.ITEM)) {
			//registered by TGItems.registerItems (runs after this), also puts it in the creative tab
			TGItems.ITEMLIST.add(getAcidBucket());
		}
	}

	protected static FluidType getAcidType() {
		if (ACID_TYPE == null) {
			//1.12: new Fluid("creeper_acid").setGaseous(false).setLuminosity(0).setUnlocalizedName("creeperAcid").setDensity(100)
			ACID_TYPE = new FluidType(FluidType.Properties.create().descriptionId("fluid.creeperAcid").density(100).lightLevel(0)
					.canExtinguish(true).canConvertToSource(false).supportsBoating(true).canHydrate(false)
					.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
				@Override
				public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
					DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> techguns.client.TGFluidsClient.initializeAcidClient(consumer));
				}
			};
		}
		return ACID_TYPE;
	}

	protected static void createAcidFluids() {
		if (ACID == null) {
			ForgeFlowingFluid.Properties props = new ForgeFlowingFluid.Properties(TGFluids::getAcidType, () -> ACID, () -> ACID_FLOWING)
					.block(() -> (LiquidBlock) getAcidBlock()).bucket(TGFluids::getAcidBucket);
			ACID = new ForgeFlowingFluid.Source(props);
			ACID_FLOWING = new ForgeFlowingFluid.Flowing(props);
		}
	}

	protected static Block getAcidBlock() {
		if (BLOCK_FLUID_ACID == null) {
			//1.12 BlockFluidAcid(ACID, Material.WATER)
			BLOCK_FLUID_ACID = new BlockFluidAcid(() -> ACID, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).replaceable().noCollission()
					.strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid());
		}
		return BLOCK_FLUID_ACID;
	}

	protected static Item getAcidBucket() {
		if (ACID_BUCKET == null) {
			ACID_BUCKET = new TGBucketItem(ACID_BUCKET_NAME, () -> ACID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
		}
		return ACID_BUCKET;
	}

	/**
	 * Fills the fluid lists from the fluid tags, called each time tags are loaded (1.12 RecipeInit in postInit)
	 */
	public static void RecipeInit() {
		if (ForgeMod.MILK.isPresent()) {
			MILK = ForgeMod.MILK.get();
			addedMilk = true;
		}

		fuels = getFluidsInTag(TAG_FUEL);
		oils = getFluidsInTag(TAG_OIL);

		worldspawn_oils = getFluidsInTag(TAG_OIL_WORLDSPAWN);
		OIL_WORLDSPAWN = null;
		for (Fluid f : worldspawn_oils) {
			if (f.defaultFluidState().createLegacyBlock().getBlock() != net.minecraft.world.level.block.Blocks.AIR) {
				OIL_WORLDSPAWN = f;
				break;
			}
		}

		LIQUID_REDSTONE = firstOrDefault(TAG_LIQUID_REDSTONE, LAVA);
		LIQUID_COAL = firstOrDefault(TAG_LIQUID_COAL, WATER);
		LIQUID_ENDER = firstOrDefault(TAG_LIQUID_ENDER, LAVA);
		BIOFUEL = firstOrDefault(TAG_BIOFUEL, LAVA);
	}

	/**
	 * Source fluids in a tag (flowing variants are skipped)
	 */
	public static ArrayList<Fluid> getFluidsInTag(TagKey<Fluid> tag) {
		ArrayList<Fluid> list = new ArrayList<>();
		if (ForgeRegistries.FLUIDS.tags() != null) {
			ForgeRegistries.FLUIDS.tags().getTag(tag).forEach(f -> {
				if (f.isSource(f.defaultFluidState()) && !list.contains(f)) {
					list.add(f);
				}
			});
		}
		return list;
	}

	protected static Fluid firstOrDefault(TagKey<Fluid> tag, Fluid def) {
		ArrayList<Fluid> list = getFluidsInTag(tag);
		return list.isEmpty() ? def : list.get(0);
	}

	public static boolean isFuel(@Nullable FluidStack stack) {
		return stack != null && !stack.isEmpty() && fuels.contains(stack.getFluid());
	}

	public static boolean isOil(@Nullable FluidStack stack) {
		return stack != null && !stack.isEmpty() && oils.contains(stack.getFluid());
	}

	/**
	 * Bucket of a Techguns fluid (1.12 universal bucket), registered with the Techguns items
	 */
	public static class TGBucketItem extends BucketItem implements TGItems.ITGRegistryNamed {
		protected final ResourceLocation registryName;

		public TGBucketItem(ResourceLocation registryName, java.util.function.Supplier<? extends Fluid> fluid, Item.Properties props) {
			super(fluid, props);
			this.registryName = registryName;
		}

		@Override
		public ResourceLocation getTGRegistryName() {
			return registryName;
		}
	}
}
