package techguns.client.models.gibs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ModelGibsVillager extends ModelGibs {

	public ModelPart villagerHead;
	public ModelPart villagerBody;
	public ModelPart villagerArms;
	public ModelPart rightVillagerLeg;
	public ModelPart leftVillagerLeg;

	/**
	 * @param root baked root of a villager / witch layer (children head, body, arms, right_leg, left_leg)
	 */
	public ModelGibsVillager(ModelPart root)
	{
		this.villagerHead = getChildOrNull(root, "head");
		this.villagerBody = getChildOrNull(root, "body");
		this.villagerArms = getChildOrNull(root, "arms");
		this.rightVillagerLeg = getChildOrNull(root, "right_leg");
		this.leftVillagerLeg = getChildOrNull(root, "left_leg");
	}


	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float scale, int part) {

		switch(part) {
		case 0:
			renderPart(this.villagerHead, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 1:
			renderPart(this.villagerBody, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 2:
			renderPart(this.rightVillagerLeg, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 3:
			renderPart(this.leftVillagerLeg, poseStack, buffer, packedLight, packedOverlay);
			break;
		case 4:
			renderPart(this.villagerArms, poseStack, buffer, packedLight, packedOverlay);
			break;
		}
	}


	@Override
	public int getNumGibs() {
		return 5;
	}
}
