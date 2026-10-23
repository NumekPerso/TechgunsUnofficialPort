package techguns.client.models.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelStielgranate extends ModelMultipart {

	// fields
	TGModelRenderer Granate01;
	TGModelRenderer Stiel01;
	TGModelRenderer Stiel02;

	public ModelStielgranate() {
		textureWidth = 32;
		textureHeight = 32;

		Granate01 = new TGModelRenderer(this, 0, 5);
		Granate01.addBox(0F, 0F, 0F, 2, 12, 2);
		Granate01.setRotationPoint(-1F, -22F, 0.5F);
		Granate01.setTextureSize(32, 32);
		Granate01.mirror = true;
		setRotation(Granate01, 0F, 0.7853982F, 0F);
		Stiel01 = new TGModelRenderer(this, 13, 0);
		Stiel01.addBox(0F, 0F, 0F, 4, 5, 4);
		Stiel01.setRotationPoint(-1.5F, -27F, -1.5F);
		Stiel01.setTextureSize(32, 32);
		Stiel01.mirror = true;
		setRotation(Stiel01, 0F, 0F, 0F);
		Stiel02 = new TGModelRenderer(this, 0, 0);
		Stiel02.addBox(0F, 0F, 0F, 3, 1, 3);
		Stiel02.setRotationPoint(-1F, -10F, -1F);
		Stiel02.setTextureSize(32, 32);
		Stiel02.mirror = true;
		setRotation(Stiel02, 0F, 0F, 0F);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, int ammoLeft,
			float reloadProgress, ItemDisplayContext transformType, int part, float fireProgress, float chargeProgress) {
		Granate01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Stiel01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Stiel02.render(poseStack, buffer, packedLight, packedOverlay, scale);
	}

}
