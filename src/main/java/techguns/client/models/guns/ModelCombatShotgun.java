package techguns.client.models.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelCombatShotgun extends ModelMultipart {
	// fields
	TGModelRenderer Shape1;
	TGModelRenderer Shape2;
	TGModelRenderer Shape3;
	TGModelRenderer Pump;
	TGModelRenderer Shape5;
	TGModelRenderer Shape6;
	TGModelRenderer Shape7;
	TGModelRenderer Shape8;
	TGModelRenderer Shape9;
	TGModelRenderer Shape10;
	TGModelRenderer Shape11;
	TGModelRenderer Shape12;
	TGModelRenderer Shape13;
	TGModelRenderer Shape14;
	TGModelRenderer Shape15;
	TGModelRenderer Shape16;
	TGModelRenderer Shape17;
	TGModelRenderer Shape18;
	TGModelRenderer Shape19;
	TGModelRenderer Shape20;

	public ModelCombatShotgun() {
		textureWidth = 64;
		textureHeight = 32;

		Shape1 = new TGModelRenderer(this, 50, 4);
		Shape1.addBox(0F, 0F, 0F, 2, 1, 1);
		Shape1.setRotationPoint(-7F, 2.5F, 0F);
		Shape1.setTextureSize(64, 32);
		Shape1.mirror = true;
		setRotation(Shape1, 0F, 0F, 0F);
		Shape2 = new TGModelRenderer(this, 54, 14);
		Shape2.addBox(0F, 0F, 0F, 3, 7, 2);
		Shape2.setRotationPoint(-9F, 1.5F, -0.5F);
		Shape2.setTextureSize(64, 32);
		Shape2.mirror = true;
		setRotation(Shape2, 0F, 0F, 0.3346075F);
		Shape2.mirror = false;
		Shape3 = new TGModelRenderer(this, 0, 4);
		Shape3.addBox(0F, 0F, 0F, 24, 1, 1);
		Shape3.setRotationPoint(8F, -1.5F, 1F);
		Shape3.setTextureSize(64, 32);
		Shape3.mirror = true;
		setRotation(Shape3, 0F, 0F, 0F);
		Pump = new TGModelRenderer(this, 0, 23);
		Pump.addBox(0F, 0F, 0F, 12, 5, 4);
		Pump.setRotationPoint(7F, -1F, -1.5F);
		Pump.setTextureSize(64, 32);
		Pump.mirror = true;
		setRotation(Pump, 0F, 0F, 0F);
		Shape5 = new TGModelRenderer(this, 0, 4);
		Shape5.addBox(0F, 0F, 0F, 24, 1, 1);
		Shape5.setRotationPoint(8F, -1.5F, -1F);
		Shape5.setTextureSize(64, 32);
		Shape5.mirror = true;
		setRotation(Shape5, 0F, 0F, 0F);
		Shape6 = new TGModelRenderer(this, 0, 6);
		Shape6.addBox(0F, 0F, 0F, 22, 2, 2);
		Shape6.setRotationPoint(8F, 1F, -0.5F);
		Shape6.setTextureSize(64, 32);
		Shape6.mirror = true;
		setRotation(Shape6, 0F, 0F, 0F);
		Shape6.mirror = false;
		Shape7 = new TGModelRenderer(this, 32, 14);
		Shape7.addBox(0F, 0F, 0F, 8, 4, 3);
		Shape7.setRotationPoint(-10F, -1.5F, -1F);
		Shape7.setTextureSize(64, 32);
		Shape7.mirror = true;
		setRotation(Shape7, 0F, 0F, 0F);
		Shape8 = new TGModelRenderer(this, 32, 21);
		Shape8.addBox(0F, 0F, 0F, 10, 1, 2);
		Shape8.setRotationPoint(-2F, -3F, -0.5F);
		Shape8.setTextureSize(64, 32);
		Shape8.mirror = true;
		setRotation(Shape8, 0F, 0F, 0F);
		Shape8.mirror = false;
		Shape9 = new TGModelRenderer(this, 0, 0);
		Shape9.addBox(0F, 0F, 0F, 24, 3, 1);
		Shape9.setRotationPoint(8F, -2.5F, 0F);
		Shape9.setTextureSize(64, 32);
		Shape9.mirror = true;
		setRotation(Shape9, 0F, 0F, 0F);
		Shape10 = new TGModelRenderer(this, 32, 24);
		Shape10.addBox(0F, 0F, 0F, 10, 5, 3);
		Shape10.setRotationPoint(-2F, -2F, -1F);
		Shape10.setTextureSize(64, 32);
		Shape10.mirror = true;
		setRotation(Shape10, 0F, 0F, 0F);
		Shape11 = new TGModelRenderer(this, 50, 0);
		Shape11.addBox(0F, 0F, 0F, 2, 1, 1);
		Shape11.setRotationPoint(27F, 0.5F, 0F);
		Shape11.setTextureSize(64, 32);
		Shape11.mirror = true;
		setRotation(Shape11, 0F, 0F, 0F);
		Shape12 = new TGModelRenderer(this, 46, 9);
		Shape12.addBox(0F, 0F, 0F, 7, 1, 2);
		Shape12.setRotationPoint(-9F, -2.5F, -0.5F);
		Shape12.setTextureSize(64, 32);
		Shape12.mirror = true;
		setRotation(Shape12, 0F, 0F, 0F);
		Shape13 = new TGModelRenderer(this, 50, 2);
		Shape13.addBox(0F, 0F, 0F, 2, 1, 1);
		Shape13.setRotationPoint(-7.5F, -3.5F, 0F);
		Shape13.setTextureSize(64, 32);
		Shape13.mirror = true;
		setRotation(Shape13, 0F, 0F, 0F);
		Shape14 = new TGModelRenderer(this, 50, 0);
		Shape14.addBox(0F, 0F, 0F, 2, 1, 1);
		Shape14.setRotationPoint(29F, -3.5F, 0F);
		Shape14.setTextureSize(64, 32);
		Shape14.mirror = true;
		setRotation(Shape14, 0F, 0F, 0F);
		Shape15 = new TGModelRenderer(this, 18, 10);
		Shape15.addBox(0F, 0F, 0F, 2, 5, 3);
		Shape15.setRotationPoint(-21F, -0.5F, -1F);
		Shape15.setTextureSize(64, 32);
		Shape15.mirror = true;
		setRotation(Shape15, 0F, 0F, 0F);
		Shape16 = new TGModelRenderer(this, 0, 19);
		Shape16.addBox(0F, 0F, 0F, 12, 2, 2);
		Shape16.setRotationPoint(-21F, 2.5F, -0.5F);
		Shape16.setTextureSize(64, 32);
		Shape16.mirror = true;
		setRotation(Shape16, 0F, 0F, -0.2617994F);
		Shape17 = new TGModelRenderer(this, 10, 10);
		Shape17.addBox(0F, 0F, 0F, 2, 3, 2);
		Shape17.setRotationPoint(-23F, 5F, -0.5F);
		Shape17.setTextureSize(64, 32);
		Shape17.mirror = true;
		setRotation(Shape17, 0F, 0F, 0F);
		Shape17.mirror = false;
		Shape18 = new TGModelRenderer(this, 0, 10);
		Shape18.addBox(0F, 0F, 0F, 2, 5, 3);
		Shape18.setRotationPoint(-23F, 0F, -1F);
		Shape18.setTextureSize(64, 32);
		Shape18.mirror = true;
		setRotation(Shape18, 0F, 0F, 0F);
		Shape18.mirror = false;
		Shape19 = new TGModelRenderer(this, 10, 15);
		Shape19.addBox(0F, 0F, 0F, 1, 2, 2);
		Shape19.setRotationPoint(-21F, 4.5F, -0.5F);
		Shape19.setTextureSize(64, 32);
		Shape19.mirror = true;
		setRotation(Shape19, 0F, 0F, 0F);
		Shape20 = new TGModelRenderer(this, 29, 12);
		Shape20.addBox(0F, 0F, 0F, 10, 1, 1);
		Shape20.setRotationPoint(-19.5F, 0F, 0F);
		Shape20.setTextureSize(64, 32);
		Shape20.mirror = true;
		setRotation(Shape20, 0F, 0F, -0.1396263F);
	}

	
	
	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, int ammoLeft,
			float reloadProgress, ItemDisplayContext transformType, int part, float fireProgress, float chargeProgress) {

		if (part==0){
			Shape1.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape2.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape3.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape5.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape6.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape7.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape8.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape9.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape10.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape11.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape12.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape13.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape14.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape15.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape16.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape17.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape18.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape19.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Shape20.render(poseStack, buffer, packedLight, packedOverlay, scale);
		} else {
			float pumpprogress=0f;
			if(fireProgress>=0.65 && fireProgress<=0.95f) {
				pumpprogress = (fireProgress-0.65f)*3.333333f;
			} else if (reloadProgress>=0.9f) {
				pumpprogress = (reloadProgress-0.9f)*10f;
			}
			if(pumpprogress>0.5f) {
				pumpprogress=1f-pumpprogress;
			}
			poseStack.pushPose();
			poseStack.translate(-0.8f*pumpprogress,0,0);
			Pump.render(poseStack, buffer, packedLight, packedOverlay, scale);
			poseStack.popPose();
		}
	}
}
