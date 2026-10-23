package techguns.client.models.additionalslots;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import techguns.client.models.armor.TGModelBiped;

/**
 * Models of the additional slot items rendered on the player (1.12 client.models.armor.ModelAdditionalSlotBase).
 * The poses are copied from the player model (1.20 poses already contain the sneaking offset, 1.12 translated by 0.2 when sneaking):
 * parts attached to bipedHead / the arms follow them, standalone back parts are rendered relative to the body rotation point.
 */
public abstract class ModelAdditionalSlotBase extends TGModelBiped {

	public ModelAdditionalSlotBase() {
		super();
	}

	public ModelAdditionalSlotBase(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn) {
		super(modelSize, p_i1149_2_, textureWidthIn, textureHeightIn);
	}

	public ModelAdditionalSlotBase(float modelSize) {
		super(modelSize);
	}

	/**
	 * 1.12 render(Entity, limbSwing, ...): setRotationAngles + render
	 */
	public void render(Player entityIn, HumanoidModel<?> parent, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale) {
		this.setPosesFrom(parent);
		this.copyRotateAngles();
		poseStack.pushPose();
		this.render(poseStack, buffer, packedLight, packedOverlay, scale, entityIn);
		poseStack.popPose();
	}

	public abstract void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, Player entityIn);

	public abstract void copyRotateAngles();

	/**
	 * Back parts are not children of the body: move them with the body (sneaking offset of the 1.20 poses)
	 */
	protected void translateToBody(PoseStack poseStack, float scale) {
		poseStack.translate(this.bipedBody.rotationPointX * scale, this.bipedBody.rotationPointY * scale, this.bipedBody.rotationPointZ * scale);
	}
}
