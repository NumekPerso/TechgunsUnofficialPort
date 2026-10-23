package techguns.client.render.entities.projectiles;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.TGRenderHelper.RenderType;

public abstract class RenderTextureProjectile<T extends Entity> extends EntityRenderer<T> {

	protected ResourceLocation textureLoc;
	protected float baseSize=0.1f;
	protected float scale=1.0f;

	public RenderTextureProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		if (textureLoc != null) {
			// 1.12: lighting disabled, no blending -> full bright with depth write
			this.renderTexture(entity, poseStack, buffers, baseSize * scale, RenderType.SOLID);
		}
	}

	protected void renderTexture(T entity, PoseStack poseStack, MultiBufferSource buffers, float size, RenderType blendMode) {
		poseStack.pushPose();
		poseStack.scale(size, size, size);
		VertexConsumer vc = buffers.getBuffer(TGRenderHelper.getRenderType(blendMode, this.getTextureLocation(entity)));
		this.drawProjectile(poseStack, vc);
		poseStack.popPose();
	}

	protected void drawProjectile(PoseStack poseStack, VertexConsumer vc)
    {
        float f = 0.f;
        float f1 = 1.f;
        float f2 = 0.f;
        float f3 = 1.f;

        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        // 1.12: rotate(180 - playerViewY, 0, 1, 0), rotate(-playerViewX, 1, 0, 0)
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        TGRenderHelper.rotate(poseStack, 180.0F, 0.0F, 1.0F, 0.0F);

        Matrix4f m = poseStack.last().pose();
        RenderGenericProjectile.vertex(vc, m, (0.0F - f5), (0.0F - f6), 0.0f, 1f, 1f, 1f, 1f, f, f3);
        RenderGenericProjectile.vertex(vc, m, (f4 - f5), (0.0F - f6), 0.0f, 1f, 1f, 1f, 1f, f1, f3);
        RenderGenericProjectile.vertex(vc, m, (f4 - f5), (f4 - f6), 0.0f, 1f, 1f, 1f, 1f, f1, f2);
        RenderGenericProjectile.vertex(vc, m, (0.0f - f5), (f4 - f6), 0.0f, 1f, 1f, 1f, 1f, f, f2);
    }

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return textureLoc != null ? textureLoc : InventoryMenu.BLOCK_ATLAS;
	}

}
