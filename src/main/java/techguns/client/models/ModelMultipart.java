package techguns.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Port of the 1.12 ModelMultipart. The renderer calls render() once per part (part index 0..parts-1), with the
 * vertex consumer of the texture of that part.
 *
 * Porting rules for 1.12 model code:
 * - ModelRenderer -> TGModelRenderer, part.render(scale) -> part.render(poseStack, buffer, packedLight, packedOverlay, scale)
 * - GlStateManager.pushMatrix/popMatrix -> poseStack.pushPose()/popPose()
 * - GlStateManager.translate/scale -> poseStack.translate/scale
 * - GlStateManager.rotate(angle, x, y, z) -> TGRenderHelper.rotate(poseStack, angle, x, y, z)
 * - TGRenderHelper.enableFXLighting() ... disableFXLighting() -> render the parts in between with TGRenderHelper.FULLBRIGHT as light
 */
public abstract class ModelMultipart extends TGModelBase {

	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		render(poseStack, buffer, packedLight, packedOverlay, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, 100, -1.0f, ItemDisplayContext.GROUND, 0, 0.0f, 0f);
	}

	public abstract void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, int ammoLeft,
			float reloadProgress, ItemDisplayContext transformType, int part, float fireProgress, float chargeProgress);
}
