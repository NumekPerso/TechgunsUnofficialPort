package techguns.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemDisplayContext;
import techguns.client.models.ModelMultipart;
import techguns.client.render.TGRenderHelper;

public class RenderGunBaseObj extends RenderGunBase {

	private float rotY;

	public RenderGunBaseObj(ModelMultipart model, int parts, float rotY) {
		super(model, parts);
		this.rotY = rotY;
	}

	@Override
	protected void setBaseRotation(PoseStack poseStack, ItemDisplayContext transform) {
		TGRenderHelper.rotate(poseStack, rotY, 0f, 1.0f, 0);
	}

}
