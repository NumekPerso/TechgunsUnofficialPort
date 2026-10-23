package techguns.recipes;

/**
 * Ingredient type techguns:hardened_glass_or_glass (1.12 "hardenedGlassOrGlass"): forge:glass/hardened if another
 * mod adds hardened glass, forge:glass otherwise.
 */
public class OreDictIngredientHardenedGlass {

	public static final TagOrFallbackIngredient.Serializer SERIALIZER = new TagOrFallbackIngredient.Serializer("forge:glass/hardened", "forge:glass");

}
