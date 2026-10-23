package techguns.client.models.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelTurretBase extends ModelMultipart {
	// fields
	TGModelRenderer Corner01;
	TGModelRenderer Top;
	TGModelRenderer Corner02;
	TGModelRenderer Corner03;
	TGModelRenderer Bar03;
	TGModelRenderer Corner04;
	TGModelRenderer Bottom04;
	TGModelRenderer Bar04;
	TGModelRenderer Bar01;
	TGModelRenderer Bar02;
	TGModelRenderer Base01;
	TGModelRenderer Bottom01;
	TGModelRenderer Socket02;
	TGModelRenderer Socket04;
	TGModelRenderer Bottom02;
	TGModelRenderer Socket01;
	TGModelRenderer Bottom03;
	TGModelRenderer Socket03;

	public ModelTurretBase() {
		textureWidth = 64;
		textureHeight = 64;

		Corner01 = new TGModelRenderer(this, 0, 30);
		Corner01.addBox(0F, 0F, 0F, 2, 15, 2);
		Corner01.setRotationPoint(-8F, 9F, 6F);
		Corner01.setTextureSize(64, 64);
		Corner01.mirror = true;
		setRotation(Corner01, 0F, 0F, 0F);
		Top = new TGModelRenderer(this, 30, 0);
		Top.addBox(0F, 0F, 0F, 8, 2, 8);
		Top.setRotationPoint(-4F, 8F, -4F);
		Top.setTextureSize(64, 64);
		Top.mirror = true;
		setRotation(Top, 0F, 0F, 0F);
		Corner02 = new TGModelRenderer(this, 0, 30);
		Corner02.addBox(0F, 0F, 0F, 2, 15, 2);
		Corner02.setRotationPoint(-8F, 9F, -8F);
		Corner02.setTextureSize(64, 64);
		Corner02.mirror = true;
		setRotation(Corner02, 0F, 0F, 0F);
		Corner03 = new TGModelRenderer(this, 0, 30);
		Corner03.addBox(0F, 0F, 0F, 2, 15, 2);
		Corner03.setRotationPoint(6F, 9F, -8F);
		Corner03.setTextureSize(64, 64);
		Corner03.mirror = true;
		setRotation(Corner03, 0F, 0F, 0F);
		Bar03 = new TGModelRenderer(this, 36, 18);
		Bar03.addBox(0F, 0F, -1F, 6, 1, 2);
		Bar03.setRotationPoint(7F, 9F, -7F);
		Bar03.setTextureSize(64, 64);
		Bar03.mirror = true;
		setRotation(Bar03, 0F, -2.356194F, 0F);
		Corner04 = new TGModelRenderer(this, 0, 30);
		Corner04.addBox(0F, 0F, 0F, 2, 15, 2);
		Corner04.setRotationPoint(6F, 9F, 6F);
		Corner04.setTextureSize(64, 64);
		Corner04.mirror = true;
		setRotation(Corner04, 0F, 0F, 0F);
		Bottom04 = new TGModelRenderer(this, 0, 15);
		Bottom04.addBox(0F, 0F, 0F, 1, 2, 12);
		Bottom04.setRotationPoint(6F, 22F, 7F);
		Bottom04.setTextureSize(64, 64);
		Bottom04.mirror = true;
		setRotation(Bottom04, 0F, -1.570796F, 0F);
		Bar04 = new TGModelRenderer(this, 36, 18);
		Bar04.addBox(0F, 0F, -1F, 6, 1, 2);
		Bar04.setRotationPoint(7F, 9F, 7F);
		Bar04.setTextureSize(64, 64);
		Bar04.mirror = true;
		setRotation(Bar04, 0F, 2.356194F, 0F);
		Bar01 = new TGModelRenderer(this, 36, 18);
		Bar01.addBox(0F, 0F, -1F, 6, 1, 2);
		Bar01.setRotationPoint(-7F, 9F, 7F);
		Bar01.setTextureSize(64, 64);
		Bar01.mirror = true;
		setRotation(Bar01, 0F, 0.7853982F, 0F);
		Bar02 = new TGModelRenderer(this, 36, 18);
		Bar02.addBox(0F, 0F, -1F, 6, 1, 2);
		Bar02.setRotationPoint(-7F, 9F, -7F);
		Bar02.setTextureSize(64, 64);
		Bar02.mirror = true;
		setRotation(Bar02, 0F, -0.7853982F, 0F);
		Base01 = new TGModelRenderer(this, 8, 36);
		Base01.addBox(0F, 0F, 0F, 14, 14, 14);
		Base01.setRotationPoint(-7F, 10F, -7F);
		Base01.setTextureSize(64, 64);
		Base01.mirror = true;
		setRotation(Base01, 0F, 0F, 0F);
		Bottom01 = new TGModelRenderer(this, 0, 15);
		Bottom01.addBox(0F, 0F, 0F, 1, 2, 12);
		Bottom01.setRotationPoint(-8F, 22F, -6F);
		Bottom01.setTextureSize(64, 64);
		Bottom01.mirror = true;
		setRotation(Bottom01, 0F, 0F, 0F);
		Socket02 = new TGModelRenderer(this, 0, 0);
		Socket02.addBox(0F, 0F, 0F, 1, 6, 6);
		Socket02.setRotationPoint(-8F, 13F, -3F);
		Socket02.setTextureSize(64, 64);
		Socket02.mirror = true;
		setRotation(Socket02, 0F, 0F, 0F);
		Socket04 = new TGModelRenderer(this, 0, 0);
		Socket04.addBox(0F, 0F, 0F, 1, 6, 6);
		Socket04.setRotationPoint(3F, 13F, 7F);
		Socket04.setTextureSize(64, 64);
		Socket04.mirror = true;
		setRotation(Socket04, 0F, -1.570796F, 0F);
		Bottom02 = new TGModelRenderer(this, 0, 15);
		Bottom02.addBox(0F, 0F, 0F, 1, 2, 12);
		Bottom02.setRotationPoint(7F, 22F, -6F);
		Bottom02.setTextureSize(64, 64);
		Bottom02.mirror = true;
		setRotation(Bottom02, 0F, 0F, 0F);
		Socket01 = new TGModelRenderer(this, 0, 0);
		Socket01.addBox(0F, 0F, 0F, 1, 6, 6);
		Socket01.setRotationPoint(7F, 13F, -3F);
		Socket01.setTextureSize(64, 64);
		Socket01.mirror = true;
		setRotation(Socket01, 0F, 0F, 0F);
		Bottom03 = new TGModelRenderer(this, 0, 15);
		Bottom03.addBox(0F, 0F, 0F, 1, 2, 12);
		Bottom03.setRotationPoint(6F, 22F, -8F);
		Bottom03.setTextureSize(64, 64);
		Bottom03.mirror = true;
		setRotation(Bottom03, 0F, -1.570796F, 0F);
		Socket03 = new TGModelRenderer(this, 0, 0);
		Socket03.addBox(0F, 0F, 0F, 1, 6, 6);
		Socket03.setRotationPoint(3F, 13F, -8F);
		Socket03.setTextureSize(64, 64);
		Socket03.mirror = true;
		setRotation(Socket03, 0F, -1.570796F, 0F);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, int ammoLeft, float reloadProgress, ItemDisplayContext transformType, int part,
			float fireProgress, float chargeProgress) {
		Corner01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Top.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Corner02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Corner03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Bar03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Corner04.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Bottom04.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Bar04.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Bar01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Bar02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Base01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Bottom01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Socket02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Socket04.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Bottom02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Socket01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Bottom03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		Socket03.render(poseStack, buffer, packedLight, packedOverlay, scale);

	}

}
