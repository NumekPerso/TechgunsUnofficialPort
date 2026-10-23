package techguns.client.render.layers;

import java.util.HashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import techguns.TGItems;
import techguns.Techguns;
import techguns.client.models.additionalslots.ModelAntiGravPack;
import techguns.client.models.additionalslots.ModelFaceMask;
import techguns.client.models.additionalslots.ModelGasMask;
import techguns.client.models.additionalslots.ModelGlider;
import techguns.client.models.additionalslots.ModelGloves;
import techguns.client.models.additionalslots.ModelJetPack;
import techguns.client.models.additionalslots.ModelNightVisionGoggles;
import techguns.client.models.additionalslots.ModelOxygenTanks;

/**
 * Item -&gt; renderer of the additional slot items (1.12 AdditionalSlotRenderRegistry + the registration in ClientProxy).
 * The 1.12 RenderAdditionalSlotSharedItem (shared item variants) is not needed anymore: the variants are separate items.
 */
public class AdditionalSlotRenderRegistry {
	protected static final HashMap<Item, RenderAdditionalSlotItem> registry = new HashMap<>();

	public static void register(Item item, RenderAdditionalSlotItem render) {
		if (item != null) {
			registry.put(item, render);
		}
	}

	public static RenderAdditionalSlotItem getRenderForItem(ItemStack stack) {
		return registry.get(stack.getItem());
	}

	public static void init() {
		register(TGItems.GAS_MASK, new RenderAdditionalSlotItem(new ModelGasMask(), new ResourceLocation(Techguns.MODID, "textures/armors/gasmask.png")));
		register(TGItems.GLIDER, new RenderAdditionalSlotItem(new ModelGlider(), new ResourceLocation(Techguns.MODID, "textures/armors/glider.png")));
		register(TGItems.JUMPPACK, new RenderAdditionalSlotItem(new ModelJetPack(1), Techguns.MODID, "textures/armors/jetpack", 4));
		register(TGItems.JETPACK, new RenderAdditionalSlotItem(new ModelJetPack(0), Techguns.MODID, "textures/armors/jetpack", 4));
		register(TGItems.SCUBA_TANKS, new RenderAdditionalSlotItem(new ModelOxygenTanks(), new ResourceLocation(Techguns.MODID, "textures/armors/oxygentanks.png")));
		register(TGItems.NIGHTVISION_GOGGLES, new RenderAdditionalSlotItem(new ModelNightVisionGoggles(), new ResourceLocation(Techguns.MODID, "textures/armors/nightvisiongoggles.png")));
		register(TGItems.ANTI_GRAV_PACK, new RenderAdditionalSlotItem(new ModelAntiGravPack(), Techguns.MODID, "textures/armors/antigravpack", 5));
		register(TGItems.TACTICAL_MASK, new RenderAdditionalSlotItem(new ModelFaceMask(true), Techguns.MODID, "textures/armors/tacticalmask", 4));

		register(TGItems.OXYGEN_MASK, new RenderAdditionalSlotItem(new ModelFaceMask(true), new ResourceLocation(Techguns.MODID, "textures/armors/oxygenmask.png")));
		register(TGItems.WORKING_GLOVES, new RenderAdditionalSlotItem(new ModelGloves(0.45f, false), new ModelGloves(0.45f, true), new ResourceLocation(Techguns.MODID, "textures/models/armor/working_gloves.png"),
				new ResourceLocation(Techguns.MODID, "textures/models/armor/working_gloves_slim.png")));
	}
}
