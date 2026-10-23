package techguns.client.render.entities.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import techguns.Techguns;
import techguns.client.models.ModelMultipart;
import techguns.client.models.guns.ModelStielgranate;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.item.RenderItemBase;
import techguns.entities.projectiles.GrenadeProjectile;

public class RenderGrenadeProjectile extends EntityRenderer<GrenadeProjectile> {

	private ResourceLocation textureLoc = new ResourceLocation(Techguns.MODID,"textures/guns/stielgranate.png");
	private ModelMultipart model = new ModelStielgranate();

	public RenderGrenadeProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public ResourceLocation getTextureLocation(GrenadeProjectile entity) {
		return textureLoc;
	}

	@Override
	public void render(GrenadeProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		renderGrenade(entity, partialTicks, poseStack, buffers, packedLight, model, this.getTextureLocation(entity));
	}

	/**
	 * Shared by the stielgranate and the frag grenade renderer (identical in 1.12)
	 */
	protected static void renderGrenade(GrenadeProjectile entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, ModelMultipart model, ResourceLocation texture) {

		poseStack.pushPose();

		TGRenderHelper.rotate(poseStack, Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F, 0.0F, 1.0F, 0.0F);
		TGRenderHelper.rotate(poseStack, Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()), 0.0F, 0.0F, 1.0F);

		int rotate = entity.tickCount % 20;
		float f = (rotate + partialTicks) / 20.0f;

		TGRenderHelper.rotate(poseStack, -360.0f * f, 0f, 0f, 1f);

		poseStack.scale(0.8f, 0.8f, 0.8f);

		TGRenderHelper.rotate(poseStack, -180.0f, 1.0f, 0, 0);
		TGRenderHelper.rotate(poseStack, 180.0f, 0f, 1.0f, 0);

		poseStack.translate(-RenderItemBase.SCALE * 0.5f, 0.83f, -RenderItemBase.SCALE);

		VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));

		model.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, entity, 0, 0, 0, 0, 0, 0.0625F);

		poseStack.popPose();
	}

}
