package techguns.client.models.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelMac10 extends ModelMultipart {

	public TGModelRenderer Top;
	public TGModelRenderer Grip3;
	public TGModelRenderer Barrel;
	public TGModelRenderer IS2;
	public TGModelRenderer Grip2;
	public TGModelRenderer IS1;
	public TGModelRenderer Trigger;
	public TGModelRenderer Grip5;
	public TGModelRenderer Grip1;
	public TGModelRenderer Grip4;
	public TGModelRenderer Magazine;
	public TGModelRenderer Stock1;
	public TGModelRenderer Stock2;
	public TGModelRenderer Stock3;
	public TGModelRenderer Stock4;
	public TGModelRenderer Stock3_2;
	public TGModelRenderer Stock4_2;
	public TGModelRenderer Stock5;
	public TGModelRenderer Stock4_3;
	public TGModelRenderer Stock3_3;
	public TGModelRenderer IS3;
	public TGModelRenderer IS3_1;
	public TGModelRenderer Stock6;

	public ModelMac10() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.Grip4 = new TGModelRenderer(this, 11, 18);
		this.Grip4.setRotationPoint(-1.5F, 2.5F, 10.0F);
		this.Grip4.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1, -0.1F);
		this.setRotation(Grip4, -0.2181661564992912F, -0.0F, 0.0F);
		this.Stock2 = new TGModelRenderer(this, 25, 25);
		this.Stock2.setRotationPoint(-1.5F, 2.0F, 14.0F);
		this.Stock2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
		this.IS3 = new TGModelRenderer(this, 10, 22);
		this.IS3.setRotationPoint(0.5F, -2.0F, -2.3F);
		this.IS3.addBox(0.0F, 0.0F, 0.0F, 0, 1, 1, 0.0F);
		this.IS2 = new TGModelRenderer(this, 0, 21);
		this.IS2.setRotationPoint(-1.0F, -1.7F, -2.8F);
		this.IS2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, -0.3F);
		this.Stock4_2 = new TGModelRenderer(this, 26, 15);
		this.Stock4_2.setRotationPoint(0.4F, -1.4F, 11.7F);
		this.Stock4_2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, -0.2F);
		this.setRotation(Stock4_2, 0.08726646259971647F, -0.054105206811824215F, 0.7853981633974483F);
		this.Stock3_2 = new TGModelRenderer(this, 25, 12);
		this.Stock3_2.setRotationPoint(0.5F, -2.0F, 15.5F);
		this.Stock3_2.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, -0.2F);
		this.setRotation(Stock3_2, 0.0F, 0.7853981633974483F, 0.0F);
		this.Top = new TGModelRenderer(this, 1, 11);
		this.Top.setRotationPoint(-2.0F, -1.0F, -3.0F);
		this.Top.addBox(0.0F, 0.0F, 0.0F, 3, 4, 17, 0.0F);
		this.Stock3 = new TGModelRenderer(this, 25, 12);
		this.Stock3.setRotationPoint(-1.5F, -2.0F, 15.5F);
		this.Stock3.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, -0.2F);
		this.setRotation(Stock3, 0.0F, 0.7853981633974483F, 0.0F);
		this.Stock1 = new TGModelRenderer(this, 25, 21);
		this.Stock1.setRotationPoint(-2.0F, 2.0F, 15.0F);
		this.Stock1.addBox(0.0F, 0.0F, 0.0F, 3, 2, 1, 0.0F);
		this.Stock6 = new TGModelRenderer(this, 20, 0);
		this.Stock6.setRotationPoint(-0.5F, -0.4F, 13.38F);
		this.Stock6.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 1, -0.45F);
		this.setRotation(Stock6, 0.0F, 0.0F, 0.7853981633974483F);
		this.Stock5 = new TGModelRenderer(this, 24, 5);
		this.Stock5.setRotationPoint(-1.0F, -1.4F, 10.4F);
		this.Stock5.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, -0.2F);
		this.setRotation(Stock5, 0.7853981633974483F, 0.0F, 0.0F);
		this.Grip5 = new TGModelRenderer(this, 11, 11);
		this.Grip5.setRotationPoint(-1.5F, 5.2F, 9.3F);
		this.Grip5.addBox(0.0F, 0.0F, 0.0F, 2, 5, 1, -0.1F);
		this.setRotation(Grip5, 0.1291543646475804F, -0.0F, 0.0F);
		this.Magazine = new TGModelRenderer(this, 0, 10);
		this.Magazine.setRotationPoint(-1.5F, 9.7F, 7.0F);
		this.Magazine.addBox(0.0F, 0.0F, 0.0F, 2, 7, 3, -0.3F);
		this.Stock3_3 = new TGModelRenderer(this, 25, 8);
		this.Stock3_3.setRotationPoint(-1.4F, -1.4F, 10.2F);
		this.Stock3_3.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F);
		this.setRotation(Stock3_3, 0.0F, 0.0F, 0.7853981633974483F);
		this.Grip1 = new TGModelRenderer(this, 11, 0);
		this.Grip1.setRotationPoint(-1.0F, 3.0F, 3.0F);
		this.Grip1.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
		this.Stock4 = new TGModelRenderer(this, 26, 15);
		this.Stock4.setRotationPoint(-1.4F, -1.4F, 11.7F);
		this.Stock4.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, -0.2F);
		this.setRotation(Stock4, 0.054105206811824215F, -0.08726646259971647F, 0.7853981633974483F);
		this.Barrel = new TGModelRenderer(this, 1, 20);
		this.Barrel.setRotationPoint(-0.5F, 0.25F, -6.0F);
		this.Barrel.addBox(0.0F, 0.0F, 0.0F, 1, 1, 6, 0.0F);
		this.setRotation(Barrel, 0.0F, 0.0F, 0.7853981633974483F);
		this.Trigger = new TGModelRenderer(this, 11, 7);
		this.Trigger.setRotationPoint(-1.0F, 2.5F, 6.5F);
		this.Trigger.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.setRotation(Trigger, -0.40893064374227134F, -0.0F, 0.0F);
		this.Grip3 = new TGModelRenderer(this, 0, 0);
		this.Grip3.setRotationPoint(-1.5F, 3.0F, 7.0F);
		this.Grip3.addBox(0.0F, 0.0F, 0.0F, 2, 7, 3, 0.0F);
		this.Stock4_3 = new TGModelRenderer(this, 25, 8);
		this.Stock4_3.setRotationPoint(0.4F, -1.4F, 10.2F);
		this.Stock4_3.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F);
		this.setRotation(Stock4_3, 0.0F, 0.0F, 0.7853981633974483F);
		this.IS1 = new TGModelRenderer(this, 12, 23);
		this.IS1.setRotationPoint(-0.5F, -1.7F, 3.4F);
		this.IS1.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
		this.setRotation(IS1, 0.0F, 0.7853981633974483F, 0.0F);
		this.IS3_1 = new TGModelRenderer(this, 10, 22);
		this.IS3_1.setRotationPoint(-1.5F, -2.0F, -2.3F);
		this.IS3_1.addBox(0.0F, 0.0F, 0.0F, 0, 1, 1, 0.0F);
		this.Grip2 = new TGModelRenderer(this, 13, 2);
		this.Grip2.setRotationPoint(-1.0F, 5.0F, 4.0F);
		this.Grip2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, int ammoLeft,
			float reloadProgress, ItemDisplayContext transformType, int part, float fireProgress, float chargeProgress) {

		this.Grip3.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Grip4.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Top.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.IS3_1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Grip2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Trigger.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Grip5.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock4_2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock3_2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock4.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock4_3.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock3_3.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.IS1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.IS3.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock5.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock6.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Barrel.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Grip1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.IS2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Magazine.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Stock3.render(poseStack, buffer, packedLight, packedOverlay, scale);
	}
}