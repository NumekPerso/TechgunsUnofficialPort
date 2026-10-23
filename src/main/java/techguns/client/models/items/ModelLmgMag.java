package techguns.client.models.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelLmgMag extends ModelMultipart {
	// fields
	TGModelRenderer Mag03;
	TGModelRenderer Mag01;
	TGModelRenderer Mag02;
	TGModelRenderer Bullet06;
	TGModelRenderer Bullet05;
	TGModelRenderer Bullet04;
	TGModelRenderer Bullet03;
	TGModelRenderer Bullet02;
	TGModelRenderer Bullet01;

	protected boolean empty;

	public ModelLmgMag(boolean empty) {
		textureWidth = 64;
		textureHeight = 32;
		this.empty = empty;

		Mag03 = new TGModelRenderer(this, 0, 0);
		Mag03.addBox(0F, 0F, 0F, 3, 6, 7);
		Mag03.setRotationPoint(-3.5F, 1F, -6F);
		Mag03.setTextureSize(128, 128);
		Mag03.mirror = true;
		setRotation(Mag03, 0F, 0F, 0.7888888F);
		Mag01 = new TGModelRenderer(this, 0, 14);
		Mag01.addBox(0F, 0F, 0F, 13, 6, 7);
		Mag01.setRotationPoint(-7.5F, 5F, -6F);
		Mag01.setTextureSize(128, 128);
		Mag01.mirror = true;
		setRotation(Mag01, 0F, 0F, 0F);
		Mag02 = new TGModelRenderer(this, 22, 2);
		Mag02.addBox(0F, 0F, 0F, 9, 4, 7);
		Mag02.setRotationPoint(-3.5F, 1F, -6F);
		Mag02.setTextureSize(128, 128);
		Mag02.mirror = true;
		setRotation(Mag02, 0F, 0F, 0F);
		if (!empty) {
			Bullet06 = new TGModelRenderer(this, 41, 20);
			Bullet06.addBox(0F, 0F, 0F, 1, 1, 6);
			Bullet06.setRotationPoint(4.5F, 0.5F, -5.5F);
			Bullet06.setTextureSize(128, 128);
			Bullet06.mirror = true;
			setRotation(Bullet06, 0F, 0F, 0.7853982F);
			Bullet05 = new TGModelRenderer(this, 41, 20);
			Bullet05.addBox(0F, 0F, 0F, 1, 1, 6);
			Bullet05.setRotationPoint(4F, -0.5F, -5.5F);
			Bullet05.setTextureSize(128, 128);
			Bullet05.mirror = true;
			setRotation(Bullet05, 0F, 0F, 0.7853982F);
			Bullet04 = new TGModelRenderer(this, 41, 20);
			Bullet04.addBox(0F, 0F, 0F, 1, 1, 6);
			Bullet04.setRotationPoint(3F, -0.5F, -5.5F);
			Bullet04.setTextureSize(128, 128);
			Bullet04.mirror = true;
			setRotation(Bullet04, 0F, 0F, 0.7853982F);
			Bullet03 = new TGModelRenderer(this, 41, 20);
			Bullet03.addBox(0F, 0F, 0F, 1, 1, 6);
			Bullet03.setRotationPoint(2F, -0.5F, -5.5F);
			Bullet03.setTextureSize(128, 128);
			Bullet03.mirror = true;
			setRotation(Bullet03, 0F, 0F, 0.7853982F);
			Bullet02 = new TGModelRenderer(this, 41, 20);
			Bullet02.addBox(0F, 0F, 0F, 1, 1, 6);
			Bullet02.setRotationPoint(1F, -0.5F, -5.5F);
			Bullet02.setTextureSize(128, 128);
			Bullet02.mirror = true;
			setRotation(Bullet02, 0F, 0F, 0.7853982F);
			Bullet01 = new TGModelRenderer(this, 41, 20);
			Bullet01.addBox(0F, 0F, 0F, 1, 1, 6);
			Bullet01.setRotationPoint(0F, -0.5F, -5.5F);
			Bullet01.setTextureSize(128, 128);
			Bullet01.mirror = true;
			setRotation(Bullet01, 0F, 0F, 0.7853982F);
		}
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, int ammoLeft,
			float reloadProgress, ItemDisplayContext transformType, int part, float fireProgress, float chargeProgress) {

		Mag03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Mag01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Mag02.render(poseStack, buffer, packedLight, packedOverlay, scale);

		if (!empty) {
			Bullet06.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Bullet05.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Bullet04.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Bullet03.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Bullet02.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Bullet01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		}
	}

}