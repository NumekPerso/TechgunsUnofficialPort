package techguns.client.render.tileentities;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import techguns.client.models.machines.ModelChemLab;
import techguns.client.render.TGRenderHelper;
import techguns.tileentities.BasicMachineTileEnt;

/**
 * 1.12 RenderMachine with ModelChemLab: the glass parts are rendered in a translucent pass after the model
 */
public class RenderChemLab extends RenderMachine {

	public RenderChemLab(ModelChemLab model, ResourceLocation texture) {
		super(model, texture);
	}

	@Override
	public void renderMachine(@Nullable BasicMachineTileEnt te, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		super.renderMachine(te, poseStack, buffers, packedLight, packedOverlay);

		poseStack.pushPose();
		poseStack.translate(0.5d, 1.5d, 0.5d);
		TGRenderHelper.rotate(poseStack, 180F, 0.0F, 0.0F, 1.0F);
		TGRenderHelper.rotate(poseStack, 180F, 0.0F, 1.0F, 0.0F);
		if (te != null) {
			TGRenderHelper.rotate(poseStack, 90.0f * te.rotation, 0, 1, 0);
		}
		((ModelChemLab) this.model).renderGlass(poseStack, buffers.getBuffer(RenderType.entityTranslucent(texture)), packedLight, packedOverlay, 0.0625f);
		poseStack.popPose();
	}
}
