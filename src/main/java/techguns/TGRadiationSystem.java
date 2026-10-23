package techguns;

import java.util.function.Predicate;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.RegisterEvent;
import techguns.api.radiation.TGRadiation;
import techguns.entities.special.EntityRadiation;
import techguns.radiation.ItemRadiationRegistry;
import techguns.radiation.RadRegenerationPotion;
import techguns.radiation.RadResistancePotion;
import techguns.radiation.RadiationPotion;

/**
 * Radiation system (1.12 TGRadiationSystem + radiation package).
 * Event handlers: radiation.RadiationEventHandler (forge bus), radiation.RadiationModEvents (mod bus),
 * client: client.TGRadiationClient, HUD: client.TGRadiationHud.
 */
public class TGRadiationSystem {

	public static final Predicate<Entity> RADIATION_TARGETS = input -> input instanceof LivingEntity;

	public static RadiationPotion radiation_effect;
	public static RadResistancePotion radresistance_effect;
	public static RadRegenerationPotion radregen_effect;

	public static int MINOR_POISONING = 500;
	public static int SEVERE_POISONING = 750;
	public static int LETHAL_POISONING = 1000;

	public static int RADLOST_ON_DEATH = 200;

	public static final int INVENTORY_RADIATION_INVERVALL = 60;

	public static Potion RAD_POTION;
	public static Potion RAD_RESISTANCE_POTION;
	public static Potion RAD_REGENERATION_POTION;
	public static Potion RAD_POTION_SEVERE;

	/**
	 * 1.12 attribute name techguns.radiationResistance (kept as translation key), registry name techguns:radiation_resistance
	 */
	public static final Attribute RADIATION_RESISTANCE = new RangedAttribute("attribute.name.techguns.radiationResistance", 0.0D, 0.0D, Float.MAX_VALUE).setSyncable(true);

	public static EntityType<EntityRadiation> RADIATION_ENTITY;

	static {
		TGRadiation.RADIATION_RESISTANCE = RADIATION_RESISTANCE;
	}

	public static boolean isEnabled() {
		return !TGConfig.WIP_disableRadiationSystem;
	}

	/**
	 * Registers mob effects, attributes, ... of the radiation system (check event.getRegistryKey())
	 */
	public static void register(RegisterEvent event) {
		event.register(Registries.ATTRIBUTE, helper -> {
			helper.register(new ResourceLocation(Techguns.MODID, "radiation_resistance"), RADIATION_RESISTANCE);
		});

		event.register(Registries.MOB_EFFECT, helper -> {
			radiation_effect = new RadiationPotion();
			helper.register(new ResourceLocation(Techguns.MODID, "radiation"), radiation_effect);

			radregen_effect = new RadRegenerationPotion();
			helper.register(new ResourceLocation(Techguns.MODID, "radregeneration"), radregen_effect);

			radresistance_effect = new RadResistancePotion();
			radresistance_effect.addAttributeModifier(RADIATION_RESISTANCE, "515AD21C-3FB2-4E36-BBCE-88C2ED738DE2", 1D, AttributeModifier.Operation.ADDITION);
			helper.register(new ResourceLocation(Techguns.MODID, "radresistance"), radresistance_effect);
		});

		//1.12 CommonProxy.registerPotionTypes
		event.register(Registries.POTION, helper -> {
			RAD_POTION = new Potion("radpotion", new MobEffectInstance(radiation_effect, 300, 4));
			helper.register(new ResourceLocation(Techguns.MODID, "radpotion"), RAD_POTION);

			RAD_POTION_SEVERE = new Potion("radpotion_severe", new MobEffectInstance(radiation_effect, 300, 24));
			helper.register(new ResourceLocation(Techguns.MODID, "radpotion_severe"), RAD_POTION_SEVERE);

			RAD_REGENERATION_POTION = new Potion("radregenerationpotion", new MobEffectInstance(radregen_effect, 400, 9));
			helper.register(new ResourceLocation(Techguns.MODID, "radregenerationpotion"), RAD_REGENERATION_POTION);

			RAD_RESISTANCE_POTION = new Potion("radresistancepotion", new MobEffectInstance(radresistance_effect, 900, 1));
			helper.register(new ResourceLocation(Techguns.MODID, "radresistancepotion"), RAD_RESISTANCE_POTION);
		});

		//1.12 TGEntities: RadiationEntity
		event.register(Registries.ENTITY_TYPE, helper -> {
			RADIATION_ENTITY = EntityType.Builder.<EntityRadiation>of(EntityRadiation::new, MobCategory.MISC).sized(0.5f, 0.5f)
					.clientTrackingRange(1).updateInterval(20).noSummon().fireImmune().build("radiationentity");
			helper.register(new ResourceLocation(Techguns.MODID, "radiationentity"), RADIATION_ENTITY);
		});
	}

	/**
	 * 1.12 postInit (radioactive items) + brewing recipes of TGMachineRecipes, called in the common setup (enqueueWork)
	 */
	public static void postInit() {
		if (TGRadiationSystem.isEnabled()) {
			ItemRadiationRegistry.addRadiationData(TGItems.ENRICHED_URANIUM, 3, 100);
			ItemRadiationRegistry.addRadiationData(TGItems.YELLOWCAKE, 1, 100);

			ItemRadiationRegistry.addRadiationData(TGItems.ANTI_GRAV_CORE, 4, 100);
			ItemRadiationRegistry.addRadiationData(TGItems.PLASMA_GENERATOR, 4, 100);

			ItemRadiationRegistry.addRadiationData(TGItems.TACTICAL_NUKE_WARHEAD, 1, 100);
			ItemRadiationRegistry.addRadiationData(TGItems.ROCKET_NUKE, 1, 100);

			//1.12 OreDictionary "ore_uranium" / "ingot_uranium" (typos of oreUranium / ingotUranium, that matched nothing): uranium tags
			ItemRadiationRegistry.addRadiationData(itemTag("forge", "ores/uranium"), 1, 100);
			ItemRadiationRegistry.addRadiationData(itemTag("forge", "ingots/uranium"), 1, 100);
		}

		//brewing (1.12 TGMachineRecipes)
		ItemStack awkwardPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);
		ItemStack radPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), TGRadiationSystem.RAD_POTION);

		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(awkwardPotion), Ingredient.of(itemTag("forge", "dusts/uranium")), radPotion);
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(awkwardPotion), Ingredient.of(TGItems.ENRICHED_URANIUM), PotionUtils.setPotion(new ItemStack(Items.POTION), TGRadiationSystem.RAD_POTION_SEVERE));

		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(awkwardPotion), Ingredient.of(itemTag("forge", "ingots/lead")), PotionUtils.setPotion(new ItemStack(Items.POTION), TGRadiationSystem.RAD_RESISTANCE_POTION));

		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(radPotion), Ingredient.of(Items.GOLDEN_CARROT), PotionUtils.setPotion(new ItemStack(Items.POTION), TGRadiationSystem.RAD_REGENERATION_POTION));
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(radPotion), Ingredient.of(Items.GLISTERING_MELON_SLICE), PotionUtils.setPotion(new ItemStack(Items.POTION), TGRadiationSystem.RAD_REGENERATION_POTION));
	}

	private static TagKey<Item> itemTag(String namespace, String path) {
		return TagKey.create(Registries.ITEM, new ResourceLocation(namespace, path));
	}

	public static void applyRadToEntities(BlockEntity tile, double radius, int duration, int strength, double inner_radius, int strength_outer) {
		applyRadToEntities(tile.getLevel(), tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5, tile.getBlockPos().getZ() + 0.5, radius, duration, strength, inner_radius, strength_outer);
	}

	public static void applyRadToEntities(Level world, double posX, double posY, double posZ, double radius, int duration, int strength, double inner_radius, int strength_outer) {
		Vec3 offset = new Vec3(posX + radius, posY + radius, posZ + radius);
		Vec3 offset2 = new Vec3(posX - radius, posY - radius, posZ - radius);
		AABB bb = new AABB(offset, offset2);

		Vec3 pos = new Vec3(posX, posY, posZ);
		world.getEntitiesOfClass(LivingEntity.class, bb, RADIATION_TARGETS).forEach(e -> {

			double distance = pos.distanceTo(e.position());
			if (distance < radius) {

				int str = strength;
				if (distance > inner_radius) {
					double factor = (distance - inner_radius) / (radius - inner_radius);
					//1.12 computed strength_outer + (strength - strength_outer) * factor: strength_outer just outside the inner
					//radius and the full strength at the border. Fixed: decreases from strength to strength_outer
					str = (int) Math.round(strength + (strength_outer - strength) * factor);
				}

				e.addEffect(new MobEffectInstance(TGRadiationSystem.radiation_effect, duration, str, true, true));

			}

		});
	}
}
