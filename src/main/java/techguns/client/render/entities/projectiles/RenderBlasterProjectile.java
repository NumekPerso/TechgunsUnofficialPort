package techguns.client.render.entities.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.render.TGRenderHelper.RenderType;
import techguns.entities.projectiles.BlasterProjectile;

public class RenderBlasterProjectile<T extends BlasterProjectile> extends RenderGenericProjectile<T> {
	private static ResourceLocation textureLoc = new ResourceLocation(Techguns.MODID,"textures/fx/laser3.png");

	public RenderBlasterProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return textureLoc;
	}

	@Override
	protected RenderType getBlendMode() {
		return RenderType.ADDITIVE;
	}

}
