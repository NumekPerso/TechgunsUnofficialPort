package techguns.client.models.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelRocket extends ModelMultipart
{
  //fields
    TGModelRenderer R9;
    TGModelRenderer R11;
    TGModelRenderer R8;
    TGModelRenderer R3;
    TGModelRenderer R10;
    TGModelRenderer R2;
    TGModelRenderer R4;
    TGModelRenderer R5;
    TGModelRenderer R6;
    TGModelRenderer R7;
    TGModelRenderer R1;
  
  public ModelRocket()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      R9 = new TGModelRenderer(this, 0, 0);
      R9.addBox(0F, 0F, 0F, 6, 4, 4);
      R9.setRotationPoint(-9.9F, -1.5F, -1.5F);
      R9.setTextureSize(128, 64);
      R9.mirror = true;
      setRotation(R9, 0F, 0F, 0F);
      R11 = new TGModelRenderer(this, 30, 0);
      R11.addBox(0F, 0F, 0F, 2, 1, 6);
      R11.setRotationPoint(-9.8F, 0F, -2.5F);
      R11.setTextureSize(128, 64);
      R11.mirror = true;
      setRotation(R11, 0F, 0F, 0F);
      R8 = new TGModelRenderer(this, 0, 16);
      R8.addBox(0F, 0F, 0F, 10, 3, 3);
      R8.setRotationPoint(-10F, -1F, -1F);
      R8.setTextureSize(128, 64);
      R8.mirror = true;
      setRotation(R8, 0F, 0F, 0F);
      R3 = new TGModelRenderer(this, 20, 3);
      R3.addBox(0F, 0F, 0F, 1, 2, 2);
      R3.setRotationPoint(10F, -0.5F, -0.5F);
      R3.setTextureSize(128, 64);
      R3.mirror = true;
      setRotation(R3, 0F, 0F, 0F);
      R10 = new TGModelRenderer(this, 30, 7);
      R10.addBox(0F, 0F, 0F, 2, 6, 1);
      R10.setRotationPoint(-9.8F, -2.5F, 0F);
      R10.setTextureSize(128, 64);
      R10.mirror = true;
      setRotation(R10, 0F, 0F, 0F);
      R2 = new TGModelRenderer(this, 0, 16);
      R2.addBox(0F, 0F, 0F, 10, 3, 3);
      R2.setRotationPoint(0F, -1F, -1F);
      R2.setTextureSize(128, 64);
      R2.mirror = true;
      setRotation(R2, 0F, 0F, 0F);
      R4 = new TGModelRenderer(this, 12, 9);
      R4.addBox(0F, 0F, 0F, 4, 2, 5);
      R4.setRotationPoint(4F, -0.5F, -2F);
      R4.setTextureSize(128, 64);
      R4.mirror = true;
      setRotation(R4, 0F, 0F, 0F);
      R5 = new TGModelRenderer(this, 0, 8);
      R5.addBox(0F, 0F, 0F, 4, 5, 2);
      R5.setRotationPoint(4F, -2F, -0.5F);
      R5.setTextureSize(128, 64);
      R5.mirror = true;
      setRotation(R5, 0F, 0F, 0F);
      R6 = new TGModelRenderer(this, 30, 7);
      R6.addBox(0F, 0F, 0F, 2, 6, 1);
      R6.setRotationPoint(4.1F, -2.5F, 0F);
      R6.setTextureSize(128, 64);
      R6.mirror = true;
      setRotation(R6, 0F, 0F, 0F);
      R7 = new TGModelRenderer(this, 30, 0);
      R7.addBox(0F, 0F, 0F, 2, 1, 6);
      R7.setRotationPoint(4.1F, 0F, -2.5F);
      R7.setTextureSize(128, 64);
      R7.mirror = true;
      setRotation(R7, 0F, 0F, 0F);
      R1 = new TGModelRenderer(this, 0, 0);
      R1.addBox(0F, 0F, 0F, 6, 4, 4);
      R1.setRotationPoint(3F, -1.5F, -1.5F);
      R1.setTextureSize(128, 64);
      R1.mirror = true;
      setRotation(R1, 0F, 0F, 0F);
  }
  
  
  
  @Override
public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, int ammoLeft, float reloadProgress,
		ItemDisplayContext transformType, int part, float fireProgress, float chargeProgress) {
    R9.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R11.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R8.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R3.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R10.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R2.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R4.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R5.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R6.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R7.render(poseStack, buffer, packedLight, packedOverlay, scale);
    R1.render(poseStack, buffer, packedLight, packedOverlay, scale);
  }

}