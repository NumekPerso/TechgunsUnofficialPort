package techguns.client.armor;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import techguns.TGArmors;
import techguns.Techguns;
import techguns.client.models.armor.ModelAdvancedShield;
import techguns.client.models.armor.ModelBallisticShield;
import techguns.client.models.armor.ModelRiotShield;
import techguns.client.models.armor.ModelSteamArmor;
import techguns.client.models.armor.ModelT3PowerArmor;
import techguns.client.models.armor.ModelT4PowerArmorMk2;
import techguns.client.render.ItemRenderHack;
import techguns.client.render.item.RenderArmorItem;
import techguns.client.render.item.TileEntityItemRendererTGShield;
import techguns.items.armors.GenericShield;

/**
 * Client setup of the armors (armor parts of 1.12 ClientProxy): 3D inventory renderers of the power armors, shield renderers
 * and the "blocking" item property of the shields (vanilla only registers it for minecraft:shield).
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TGArmorClientSetup {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(TGArmorClientSetup::init);
	}

	protected static ResourceLocation tex(String path) {
		return new ResourceLocation(Techguns.MODID, path);
	}

	protected static ResourceLocation[] getTextures(String name, String... suffixes) {
		ResourceLocation[] tex = new ResourceLocation[suffixes.length + 1];
		tex[0] = new ResourceLocation(Techguns.MODID, name + ".png");
		for (int i = 1; i <= suffixes.length; i++) {
			tex[i] = new ResourceLocation(Techguns.MODID, name + "_" + suffixes[i - 1] + ".png");
		}
		return tex;
	}

	public static void init() {
		ItemRenderHack.registerItemRenderer("steam_helmet", new RenderArmorItem(new ModelSteamArmor(0), tex("textures/models/armor/steam_armor.png"), EquipmentSlot.HEAD));
		ItemRenderHack.registerItemRenderer("steam_chestplate", new RenderArmorItem(new ModelSteamArmor(0), tex("textures/models/armor/steam_armor.png"), EquipmentSlot.CHEST));
		ItemRenderHack.registerItemRenderer("steam_leggings", new RenderArmorItem(new ModelSteamArmor(1), tex("textures/models/armor/steam_armor.png"), EquipmentSlot.LEGS));
		ItemRenderHack.registerItemRenderer("steam_boots", new RenderArmorItem(new ModelSteamArmor(0), tex("textures/models/armor/steam_armor.png"), EquipmentSlot.FEET));

		ItemRenderHack.registerItemRenderer("t3_power_helmet", new RenderArmorItem(new ModelT3PowerArmor(0), tex("textures/models/armor/powerarmor.png"), EquipmentSlot.HEAD));
		ItemRenderHack.registerItemRenderer("t3_power_chestplate", new RenderArmorItem(new ModelT3PowerArmor(0), tex("textures/models/armor/powerarmor.png"), EquipmentSlot.CHEST));
		ItemRenderHack.registerItemRenderer("t3_power_leggings", new RenderArmorItem(new ModelT3PowerArmor(1), tex("textures/models/armor/powerarmor.png"), EquipmentSlot.LEGS));
		ItemRenderHack.registerItemRenderer("t3_power_boots", new RenderArmorItem(new ModelT3PowerArmor(0), tex("textures/models/armor/powerarmor.png"), EquipmentSlot.FEET));

		ItemRenderHack.registerItemRenderer("t4_power_helmet", new RenderArmorItem(new ModelT4PowerArmorMk2(0), tex("textures/models/armor/powerarmor_mk2_darkgrey.png"), EquipmentSlot.HEAD));
		ItemRenderHack.registerItemRenderer("t4_power_chestplate", new RenderArmorItem(new ModelT4PowerArmorMk2(0), tex("textures/models/armor/powerarmor_mk2_darkgrey.png"), EquipmentSlot.CHEST));
		ItemRenderHack.registerItemRenderer("t4_power_leggings", new RenderArmorItem(new ModelT4PowerArmorMk2(1), tex("textures/models/armor/powerarmor_mk2_darkgrey.png"), EquipmentSlot.LEGS));
		ItemRenderHack.registerItemRenderer("t4_power_boots", new RenderArmorItem(new ModelT4PowerArmorMk2(0), tex("textures/models/armor/powerarmor_mk2_darkgrey.png"), EquipmentSlot.FEET));

		TileEntityItemRendererTGShield.registerShield(TGArmors.riot_shield, new ModelRiotShield(), tex("textures/armors/riot_shield.png"),
				tex("textures/armors/riot_shield_black.png"), tex("textures/armors/riot_shield_darkgreen.png"),
				tex("textures/armors/riot_shield_grey.png"));

		TileEntityItemRendererTGShield.registerShield(TGArmors.ballistic_shield, new ModelBallisticShield(), tex("textures/armors/ballistic_shield.png"),
				tex("textures/armors/ballistic_shield_black.png"), tex("textures/armors/ballistic_shield_green.png"),
				tex("textures/armors/ballistic_shield_grey.png"));

		TileEntityItemRendererTGShield.registerShield(TGArmors.advanced_shield, new ModelAdvancedShield(), getTextures("textures/armors/advanced_shield", "silver", "green"));

		ResourceLocation blocking = new ResourceLocation("blocking");
		for (GenericShield shield : TGArmors.shields) {
			ItemProperties.register(shield, blocking, (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		}
	}
}
