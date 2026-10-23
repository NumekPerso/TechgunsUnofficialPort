package techguns.client.models.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelLaserPistol extends ModelMultipart {
    public TGModelRenderer Barrel1;
    public TGModelRenderer Grip1;
    public TGModelRenderer Trigger;
    public TGModelRenderer grip2;
    public TGModelRenderer Barrel2;
    public TGModelRenderer BottomRound;
    public TGModelRenderer grip3;
    public TGModelRenderer BarrelFront;
    public TGModelRenderer BottomFront;
    public TGModelRenderer Front1;
    public TGModelRenderer Front2;
    public TGModelRenderer Barrel4;
    public TGModelRenderer Barrel5;
    public TGModelRenderer Cylinder1;
    public TGModelRenderer Cylinder2;
    public TGModelRenderer Side1;
    public TGModelRenderer CylinderFront;
    public TGModelRenderer TopRound;

    public ModelLaserPistol() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Grip1 = new TGModelRenderer(this, 37, 7);
        this.Grip1.setRotationPoint(-1.5F, 3.0F, 7.0F);
        this.Grip1.addBox(0.0F, 0.0F, 0.0F, 2, 6, 3, 0.0F);
        this.setRotation(Grip1, 0.2181661564992912F, -0.0F, 0.0F);
        this.grip3 = new TGModelRenderer(this, 56, 1);
        this.grip3.setRotationPoint(-1.5F, 8.0F, 5.5F);
        this.grip3.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.grip2 = new TGModelRenderer(this, 37, 0);
        this.grip2.setRotationPoint(-2.0F, 8.0F, 7.5F);
        this.grip2.addBox(0.0F, 0.0F, 0.0F, 3, 2, 4, 0.0F);
        this.Trigger = new TGModelRenderer(this, 58, 8);
        this.Trigger.setRotationPoint(-1.0F, 3.0F, 6.0F);
        this.Trigger.addBox(0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        this.BarrelFront = new TGModelRenderer(this, 30, 6);
        this.BarrelFront.setRotationPoint(-1.5F, 3.8F, -3.0F);
        this.BarrelFront.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
        this.CylinderFront = new TGModelRenderer(this, 8, 0);
        this.CylinderFront.setRotationPoint(-2.8F, -0.8F, -3.1F);
        this.CylinderFront.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotation(CylinderFront, 0.0F, 0.0F, 0.7853981633974483F);
        this.BottomFront = new TGModelRenderer(this, 28, 10);
        this.BottomFront.setRotationPoint(-0.5F, 8.3F, -2.5F);
        this.BottomFront.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotation(BottomFront, 0.0F, 0.0F, 0.7853981633974483F);
        this.Barrel5 = new TGModelRenderer(this, 9, 26);
        this.Barrel5.setRotationPoint(-2.0F, -1.5F, 6.0F);
        this.Barrel5.addBox(0.0F, 0.0F, 0.0F, 3, 1, 4, 0.0F);
        this.Cylinder2 = new TGModelRenderer(this, 19, 0);
        this.Cylinder2.setRotationPoint(-3.0F, 3.0F, 6.0F);
        this.Cylinder2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 5, 0.0F);
        this.setRotation(Cylinder2, 0.0F, 0.0F, 0.7853981633974483F);
        this.Barrel2 = new TGModelRenderer(this, 44, 22);
        this.Barrel2.setRotationPoint(-3.0F, -1.2F, -3.0F);
        this.Barrel2.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5, 0.0F);
        this.Cylinder1 = new TGModelRenderer(this, 19, 0);
        this.Cylinder1.setRotationPoint(2.0F, 3.0F, 6.0F);
        this.Cylinder1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 5, 0.0F);
        this.setRotation(Cylinder1, 0.0F, 0.0F, 0.7853981633974483F);
        this.Front2 = new TGModelRenderer(this, 34, 0);
        this.Front2.mirror = true;
        this.Front2.setRotationPoint(0.3F, 4.3F, -2.0F);
        this.Front2.addBox(0.0F, 0.0F, 0.0F, 0, 4, 1, 0.0F);
        this.BottomRound = new TGModelRenderer(this, 25, 15);
        this.BottomRound.setRotationPoint(-0.5F, 9.0F, -0.5F);
        this.BottomRound.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 6, 0.0F);
        this.setRotation(BottomRound, 0.0F, 0.0F, 0.7853981633974483F);
        this.Front1 = new TGModelRenderer(this, 34, 0);
        this.Front1.setRotationPoint(-1.3F, 4.3F, -2.0F);
        this.Front1.addBox(0.0F, 0.0F, 0.0F, 0, 4, 1, 0.0F);
        this.Side1 = new TGModelRenderer(this, 17, 8);
        this.Side1.setRotationPoint(2.0F, -0.7F, 10.0F);
        this.Side1.addBox(-2.0F, 0.0F, -3.0F, 2, 3, 3, 0.0F);
        this.setRotation(Side1, 0.0F, -0.2617993877991494F, 0.0F);
        this.Barrel4 = new TGModelRenderer(this, 25, 23);
        this.Barrel4.setRotationPoint(-3.0F, -1.5F, 10.0F);
        this.Barrel4.addBox(0.0F, 0.0F, 0.0F, 5, 5, 4, 0.0F);
        this.Barrel1 = new TGModelRenderer(this, 40, 9);
        this.Barrel1.setRotationPoint(-2.5F, -1.0F, 2.0F);
        this.Barrel1.addBox(0.0F, 0.0F, 0.0F, 4, 4, 8, 0.0F);
        this.TopRound = new TGModelRenderer(this, 28, 0);
        this.TopRound.setRotationPoint(-1.0F, -1.8F, 8.5F);
        this.TopRound.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
    }
	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, int ammoLeft, float reloadProgress, ItemDisplayContext transformType, int part,
			float fireProgress, float chargeProgress) {

        this.Grip1.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.grip3.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.grip2.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Trigger.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.BarrelFront.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.CylinderFront.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.BottomFront.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Barrel5.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Cylinder2.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Barrel2.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Cylinder1.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Front2.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.BottomRound.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Front1.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Side1.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Barrel4.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Barrel1.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.TopRound.render(poseStack, buffer, packedLight, packedOverlay, scale);
    }
}


