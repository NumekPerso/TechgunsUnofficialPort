package techguns.client.render.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import techguns.client.models.ModelMultipart;
import techguns.items.guns.GenericGun;

public class RenderMiningToolMultiTexture extends RenderGunBase90 {

	ResourceLocation[] textures;

	public RenderMiningToolMultiTexture(ModelMultipart model, int parts, ResourceLocation[] textures) {
		super(model, parts);
		this.textures = textures;
	}

	@Override
	public ResourceLocation getTextureForPart(GenericGun gun, int part, ItemStack stack) {
		if (part == 1) {
			int textureIndex = gun.getMiningHeadLevel(stack);
			if (textureIndex >= 0 && textureIndex < textures.length) {
				return textures[textureIndex];
			}
		}
		return super.getTextureForPart(gun, part, stack);
	}

}
