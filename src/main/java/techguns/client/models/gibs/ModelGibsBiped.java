package techguns.client.models.gibs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;


public class ModelGibsBiped extends ModelGibs
{
	public ModelPart bipedHead;
	public ModelPart bipedBody;
	public ModelPart bipedRightArm;
	public ModelPart bipedLeftArm;
	public ModelPart bipedRightLeg;
	public ModelPart bipedLeftLeg;

	/**
	 * @param root baked root of a humanoid layer (children head, body, right_arm, left_arm, right_leg, left_leg)
	 */
	public ModelGibsBiped(ModelPart root)
	{
		this(root.getChild("head"), root.getChild("body"), root.getChild("right_arm"), root.getChild("left_arm"), root.getChild("right_leg"), root.getChild("left_leg"));
	}

	public ModelGibsBiped(HumanoidModel<?> model)
	{
		this(model.head, model.body, model.rightArm, model.leftArm, model.rightLeg, model.leftLeg);
	}

	public ModelGibsBiped(ModelPart head, ModelPart body, ModelPart rightArm, ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg) {
		this.bipedHead = head;
		this.bipedBody = body;
		this.bipedRightArm = rightArm;
		this.bipedLeftArm = leftArm;
		this.bipedRightLeg = rightLeg;
		this.bipedLeftLeg = leftLeg;
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float scale, int part)
	{

		switch(part) {
		case 0:
			renderPart(this.bipedHead, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 1:
			renderPart(this.bipedBody, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 2:
			renderPart(this.bipedRightArm, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 3:
			renderPart(this.bipedLeftArm, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 4:
			renderPart(this.bipedRightLeg, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 5:
			renderPart(this.bipedLeftLeg, poseStack, buffer, packedLight, packedOverlay);
			break;
		}
	}

	@Override
	public int getNumGibs() {
		return 6;
	}
}
