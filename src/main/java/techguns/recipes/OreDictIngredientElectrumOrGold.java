package techguns.recipes;

/**
 * Ingredient type techguns:electrum_or_gold (1.12 "electrumOrGold"): forge:ingots/electrum if another mod adds
 * electrum ingots, forge:ingots/gold otherwise.
 */
public class OreDictIngredientElectrumOrGold {

	public static final TagOrFallbackIngredient.Serializer SERIALIZER = new TagOrFallbackIngredient.Serializer("forge:ingots/electrum", "forge:ingots/gold");

}
