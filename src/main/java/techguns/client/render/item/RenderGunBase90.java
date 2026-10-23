package techguns.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.world.item.ItemDisplayContext;
import techguns.client.models.ModelMultipart;

public class RenderGunBase90 extends RenderGunBase {

	public RenderGunBase90(ModelMultipart model, int parts) {
		super(model, parts);
	}

	@Override
	protected void setBaseRotation(PoseStack poseStack, ItemDisplayContext transform) {
		poseStack.mulPose(Axis.XP.rotationDegrees(-180.0f));
		poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
	}
}
