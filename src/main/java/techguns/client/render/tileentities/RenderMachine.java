package techguns.client.render.tileentities;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import techguns.client.models.machines.ModelMachine;
import techguns.client.render.TGRenderHelper;
import techguns.tileentities.BasicMachineTileEnt;

/**
 * Renders a machine with a ModelMachine (1.12 RenderMachine TESR), also used for the item (te = null)
 */
public class RenderMachine implements BlockEntityRenderer<BasicMachineTileEnt> {

	protected ModelMachine model;
	protected ResourceLocation texture;

	public RenderMachine(ModelMachine model, ResourceLocation texture) {
		super();
		this.model = model;
		this.texture = texture;
	}

	@Override
	public void render(BasicMachineTileEnt te, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		this.renderMachine(te, poseStack, buffers, packedLight, packedOverlay);
	}

	public void renderMachine(@Nullable BasicMachineTileEnt te, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		VertexConsumer vc = buffers.getBuffer(RenderType.entityCutout(texture));

		poseStack.pushPose();
		poseStack.translate(0.5d, 1.5d, 0.5d);

		poseStack.pushPose();
		TGRenderHelper.rotate(poseStack, 180F, 0.0F, 0.0F, 1.0F);
		TGRenderHelper.rotate(poseStack, 180F, 0.0F, 1.0F, 0.0F);

		float progress = 0.0f;
		if (te != null) {
			progress = te.getProgress();
			byte rotation = te.rotation;
			TGRenderHelper.rotate(poseStack, 90.0f * rotation, 0, 1, 0);
		}

		this.model.render(poseStack, vc, packedLight, packedOverlay, 0.0625f, progress);
		poseStack.popPose();
		poseStack.popPose();
	}

}
