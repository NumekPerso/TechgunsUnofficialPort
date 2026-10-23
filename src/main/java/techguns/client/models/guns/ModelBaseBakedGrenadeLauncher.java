package techguns.client.models.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import techguns.client.render.TGRenderHelper;

public class ModelBaseBakedGrenadeLauncher extends ModelBaseBaked {

	public ModelBaseBakedGrenadeLauncher(ResourceLocation... objLocations) {
		super(objLocations);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, int ammoLeft, float reloadProgress, ItemDisplayContext transformType, int part, float fireProgress, float chargeProgress) {

		objModels.get(0).render(poseStack, buffer, packedLight, packedOverlay);
		poseStack.pushPose();
		poseStack.translate(0, -2f, 0);
		TGRenderHelper.rotate(poseStack, 60.0f * fireProgress, 1, 0, 0);
		poseStack.translate(0, 2f, 0);
		objModels.get(1).render(poseStack, buffer, packedLight, packedOverlay);
		poseStack.popPose();
	}

}
