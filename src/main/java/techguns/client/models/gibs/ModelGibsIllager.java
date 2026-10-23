package techguns.client.models.gibs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ModelGibsIllager extends ModelGibs {

	ModelPart head;
	ModelPart body;
	ModelPart arms;
	ModelPart leg0;
	ModelPart leg1;

	/**
	 * @param root baked root of an illager layer (children head, body, arms, right_leg, left_leg)
	 */
	public ModelGibsIllager(ModelPart root) {
		super();
		this.head = getChildOrNull(root, "head");
		this.body = getChildOrNull(root, "body");
		this.arms = getChildOrNull(root, "arms");
		this.leg0 = getChildOrNull(root, "right_leg");
		this.leg1 = getChildOrNull(root, "left_leg");
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
			renderPart(this.leg0, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 3:
			renderPart(this.leg1, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 4:
			renderPart(this.arms, poseStack, buffer, packedLight, packedOverlay);
			break;
		}
	}

	@Override
	public int getNumGibs() {
		return 5;
	}

}
