package techguns.client.models.additionalslots;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.entity.player.Player;
import techguns.client.models.TGModelRenderer;

public class ModelGloves extends ModelAdditionalSlotBase {

	public ModelGloves(float modelSize, boolean smallArms) {
		super(modelSize, 0F, 16, 16);

		if (smallArms) {
			this.bipedLeftArm = new TGModelRenderer(this, 0, 0);
			this.bipedLeftArm.mirror = true;
			this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
			this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
			this.bipedRightArm = new TGModelRenderer(this, 0, 0);
			this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
			this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
		} else {
			this.bipedRightArm = new TGModelRenderer(this, 0, 0);
			this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
			this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.bipedLeftArm = new TGModelRenderer(this, 0, 0);
			this.bipedLeftArm.mirror = true;
			this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
			this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		}
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, Player entityIn) {
		this.bipedLeftArm.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.bipedRightArm.render(poseStack, buffer, packedLight, packedOverlay, scale);
	}

	@Override
	public void copyRotateAngles() {
	}

}
