package techguns.client.models.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Shield models (1.12 ModelBase.render(entity, ..., scale) of the shield models)
 */
public interface IShieldModel {

	void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float f5);
}
