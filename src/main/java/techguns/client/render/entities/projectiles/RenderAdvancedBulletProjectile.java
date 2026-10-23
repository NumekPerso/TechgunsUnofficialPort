package techguns.client.render.entities.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.render.TGRenderHelper.RenderType;
import techguns.entities.projectiles.AdvancedBulletProjectile;

public class RenderAdvancedBulletProjectile<T extends AdvancedBulletProjectile> extends RenderGenericProjectile<T> {

	private static ResourceLocation textureLoc = new ResourceLocation(Techguns.MODID,"textures/entity/bullet_blue.png");

	public RenderAdvancedBulletProjectile(EntityRendererProvider.Context ctx) {
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

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {

		float delay = 2.5f / entity.getSpeed();

		if ((float) entity.tickCount + partialTicks > delay) {
			this.renderBullet(entity, poseStack, buffers, 16.0D, 2.0D);
		}

	}
}
