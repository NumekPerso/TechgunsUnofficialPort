package techguns.client.models.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelBase;

/**
 * 1.12 ModelMachine: block entity model with the machine progress (0-1) as animation parameter
 */
public abstract class ModelMachine extends TGModelBase {

	public abstract void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, float progress);
}
