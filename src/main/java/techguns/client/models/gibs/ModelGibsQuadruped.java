package techguns.client.models.gibs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ModelGibsQuadruped extends ModelGibs{
	public ModelPart head;
	public ModelPart body;
	public ModelPart leg1;
	public ModelPart leg2;
	public ModelPart leg3;
	public ModelPart leg4;

	/**
	 * @param root baked root of a quadruped layer (children head, body, right_hind_leg, left_hind_leg, right_front_leg, left_front_leg)
	 */
	public ModelGibsQuadruped(ModelPart root)
	{
		this.head = getChildOrNull(root, "head");
		this.body = getChildOrNull(root, "body");
		this.leg1 = getChildOrNull(root, "right_hind_leg");
		this.leg2 = getChildOrNull(root, "left_hind_leg");
		this.leg3 = getChildOrNull(root, "right_front_leg");
		this.leg4 = getChildOrNull(root, "left_front_leg");
	}


	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float scale, int part) {
		switch(part) {
		case 0:
			renderPart(this.head, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 1:
			renderPart(this.body, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 2:
			renderPart(this.leg1, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 3:
			renderPart(this.leg2, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 4:
			renderPart(this.leg3, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 5:
			renderPart(this.leg4, poseStack, buffer, packedLight, packedOverlay);
			break;
		}
	}


	@Override
	public int getNumGibs() {
		return 6;
	}

}
