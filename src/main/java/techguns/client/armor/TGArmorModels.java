package techguns.client.armor;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import techguns.TGArmors;
import techguns.client.models.armor.ModelArmorCoat;
import techguns.client.models.armor.ModelBeret;
import techguns.client.models.armor.ModelExoSuit;
import techguns.client.models.armor.ModelSteamArmor;
import techguns.client.models.armor.ModelT3PowerArmor;
import techguns.client.models.armor.ModelT4PowerArmorMk2;
import techguns.client.models.armor.TGModelBiped;

/**
 * Armor model registry (1.12 ClientProxy.registerArmorModel / getArmorModel)
 */
public class TGArmorModels {

	protected static HashMap<String, TGArmorModel> armorModelRegistry = null;

	public static void registerArmorModel(ResourceLocation key, TGModelBiped model) {
		armorModelRegistry.put(key.toString(), new TGArmorModel(model));
	}

	/**
	 * All armor models (read only, used by the bake verification)
	 */
	public static java.util.Map<String, TGArmorModel> getAllArmorModels() {
		if (armorModelRegistry == null) {
			init();
		}
		return java.util.Collections.unmodifiableMap(armorModelRegistry);
	}

	@Nullable
	public static TGArmorModel getArmorModel(ResourceLocation key) {
		if (armorModelRegistry == null) {
			init();
		}
		return armorModelRegistry.get(key.toString());
	}

	protected static synchronized void init() {
		if (armorModelRegistry != null) {
			return;
		}
		armorModelRegistry = new HashMap<>();
		registerArmorModel(TGArmors.ARMORMODEL_STEAM_ARMOR_0, new ModelSteamArmor(0));
		registerArmorModel(TGArmors.ARMORMODEL_STEAM_ARMOR_1, new ModelSteamArmor(1));
		registerArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_0, new ModelT3PowerArmor(0));
		registerArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_1, new ModelT3PowerArmor(1));
		registerArmorModel(TGArmors.ARMORMODEL_EXO_SUIT_0, new ModelExoSuit(0, 1.0f));
		registerArmorModel(TGArmors.ARMORMODEL_EXO_SUIT_1, new ModelExoSuit(1, 0.5f));
		registerArmorModel(TGArmors.ARMORMODEL_EXO_SUIT_2, new ModelExoSuit(0, 0.75f));
		registerArmorModel(TGArmors.ARMORMODEL_BERET_0, new ModelBeret());
		registerArmorModel(TGArmors.ARMORMODEL_COAT_0, new ModelArmorCoat(0, 1.0f));
		registerArmorModel(TGArmors.ARMORMODEL_COAT_1, new ModelArmorCoat(1, 0.51f));
		registerArmorModel(TGArmors.ARMORMODEL_COAT_2, new ModelArmorCoat(2, 0.49f));
		registerArmorModel(TGArmors.ARMORMODEL_COAT_3, new ModelArmorCoat(3, 0.75f));
		registerArmorModel(TGArmors.ARMORMODEL_STEAM_ARMOR_2, new ModelSteamArmor(0, 0.01f));
		registerArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_2, new ModelT3PowerArmor(0, 0.01f));

		registerArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_MK2_0, new ModelT4PowerArmorMk2(0));
		registerArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_MK2_1, new ModelT4PowerArmorMk2(1));
		registerArmorModel(TGArmors.ARMORMODEL_POWER_ARMOR_MK2_2, new ModelT4PowerArmorMk2(0, 0.01f));
	}
}
