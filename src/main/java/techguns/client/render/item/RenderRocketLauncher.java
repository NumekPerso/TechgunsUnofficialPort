package techguns.client.render.item;

import java.util.HashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import techguns.Techguns;
import techguns.client.models.ModelMultipart;
import techguns.items.guns.GenericGun;
import techguns.items.guns.ammo.AmmoTypes;

public class RenderRocketLauncher extends RenderGunBase90 {

	protected static final HashMap<String, ResourceLocation> textures = new HashMap<>();
	static {
		textures.put(AmmoTypes.TYPE_DEFAULT, new ResourceLocation(Techguns.MODID, "textures/guns/rocket.png"));
		textures.put(AmmoTypes.TYPE_NUKE, new ResourceLocation(Techguns.MODID, "textures/guns/rocket_nuke.png"));
		textures.put(AmmoTypes.TYPE_HV, new ResourceLocation(Techguns.MODID, "textures/guns/rocket_hv.png"));
	}

	public RenderRocketLauncher(ModelMultipart model, int parts) {
		super(model, parts);
	}

	@Override
	public ResourceLocation getTextureForPart(GenericGun gun, int part, ItemStack stack) {
		if (part == 1) {
			String variant = gun.getCurrentAmmoVariantKey(stack);
			return textures.get(variant);
		}
		return super.getTextureForPart(gun, part, stack);
	}

}
