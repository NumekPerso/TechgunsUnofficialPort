package techguns.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import techguns.client.models.ModelMultipart;

public class RenderItemLMGMag extends RenderItemBase {

	public RenderItemLMGMag(ModelMultipart model, ResourceLocation texture) {
		super(model, texture);
	}

	@Override
	protected void setBaseRotation(PoseStack poseStack, ItemDisplayContext transform) {
		poseStack.mulPose(Axis.XP.rotationDegrees(-180.0f));

		if (ItemDisplayContext.GUI == transform || ItemDisplayContext.FIXED == transform) {
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
		} else {
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
		}
	}

}
