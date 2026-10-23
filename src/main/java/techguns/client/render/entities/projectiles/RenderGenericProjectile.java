package techguns.client.render.entities.projectiles;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.TGRenderTypes;
import techguns.client.render.TGRenderHelper.RenderType;
import techguns.entities.projectiles.GenericProjectile;

public class RenderGenericProjectile<T extends GenericProjectile> extends EntityRenderer<T> {

	private static final ResourceLocation bulletTextures = new ResourceLocation(Techguns.MODID,"textures/entity/bullet1.png"); //previously was bullet.png

	public RenderGenericProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return bulletTextures;
	}

	/**
	 * 1.12 enableGLStates(): color 1,1,1,1 and lighting disabled, no blending -> full bright, alpha with depth write
	 */
	protected RenderType getBlendMode() {
		return RenderType.SOLID;
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {

		if (entity.tickCount >= 2 || (entity.tickCount < 2 && partialTicks > 0.25f)) {
			this.renderBullet(entity, poseStack, buffers, 8.0D, 2.0D);
		}

	}

	/**
	 * 4 quads rotated around the flight direction
	 * @param length Actually, this is half of length/width
	 */
	protected void renderBullet(T entity, PoseStack poseStack, MultiBufferSource buffers, double length, double width) {
		poseStack.pushPose();

		TGRenderHelper.rotate(poseStack, entity.getYRot() - 90, 0.0F, 1.0F, 0.0F);
		TGRenderHelper.rotate(poseStack, entity.getXRot(), 0.0F, 0.0F, 1.0F);

		VertexConsumer vc = buffers.getBuffer(TGRenderHelper.getRenderType(this.getBlendMode(), this.getTextureLocation(entity)));

		float u1 = 0.0f;
		float u2 = 1.0f;
		float v1 = 0.0f;
		float v2 = 1.0f;
		float f10 = 0.0125F;

		TGRenderHelper.rotate(poseStack, 45.0F, 1.0F, 0.0F, 0.0F);
		poseStack.scale(f10, f10, f10);

		for (int i = 0; i < 4; ++i) {
			TGRenderHelper.rotate(poseStack, 90.0F, 1.0F, 0.0F, 0.0F);
			Matrix4f m = poseStack.last().pose();
			vertex(vc, m, -length, -width, 0.0D, 1f, 1f, 1f, 1f, u1, v1);
			vertex(vc, m, length, -width, 0.0D, 1f, 1f, 1f, 1f, u2, v1);
			vertex(vc, m, length, width, 0.0D, 1f, 1f, 1f, 1f, u2, v2);
			vertex(vc, m, -length, width, 0.0D, 1f, 1f, 1f, 1f, u1, v2);
		}

		poseStack.popPose();
	}

	/**
	 * Writes one vertex of an fx render type (TGRenderTypes.fxVertex), alpha is clamped to 0-1
	 */
	public static void vertex(VertexConsumer vc, Matrix4f m, double x, double y, double z, float r, float g, float b, float a, double u, double v) {
		TGRenderTypes.fxVertex(vc, m, (float) x, (float) y, (float) z, r, g, b, Math.max(0f, Math.min(1f, a)), (float) u, (float) v);
	}

}
