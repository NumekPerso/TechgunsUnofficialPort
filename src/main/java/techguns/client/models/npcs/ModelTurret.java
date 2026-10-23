package techguns.client.models.npcs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelTurret extends ModelMultipart {

	 //fields
   TGModelRenderer Mount;
   TGModelRenderer Base;
   TGModelRenderer Stick01;
   TGModelRenderer Stick02;
 
 public ModelTurret()
 {
   textureWidth = 64;
   textureHeight = 64;
   
     Mount = new TGModelRenderer(this, 34, 12);
     Mount.addBox(0F, 0F, -2F, 4, 1, 4);
     Mount.setRotationPoint(-2F, 13F, 0F);
     Mount.setTextureSize(64, 64);
     Mount.mirror = true;
     setRotation(Mount, 0F, 0F, 0F);
     Base = new TGModelRenderer(this, 32, 22);
     Base.addBox(-4F, 0F, -4F, 8, 1, 8);
     Base.setRotationPoint(0F, 23F, 0F);
     Base.setTextureSize(64, 64);
     Base.mirror = true;
     setRotation(Base, 0F, 0F, 0F);
     Stick01 = new TGModelRenderer(this, 9, 30);
     Stick01.addBox(0F, 0F, 0F, 2, 11, 2);
     Stick01.setRotationPoint(2F, 12F, -1F);
     Stick01.setTextureSize(64, 64);
     Stick01.mirror = true;
     setRotation(Stick01, 0F, 0F, 0F);
     Stick02 = new TGModelRenderer(this, 9, 30);
     Stick02.addBox(0F, 0F, 0F, 2, 11, 2);
     Stick02.setRotationPoint(-4F, 12F, -1F);
     Stick02.setTextureSize(64, 64);
     Stick02.mirror = true;
     setRotation(Stick02, 0F, 0F, 0F);
 }

 
 
	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, int ammoLeft, float reloadProgress, ItemDisplayContext transformType, int part,
			float fireProgress, float chargeProgress) {
	//	setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
		if (part == 0) {

			Base.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Stick01.render(poseStack, buffer, packedLight, packedOverlay, scale);
			Stick02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		} else {
			Mount.render(poseStack, buffer, packedLight, packedOverlay, scale);
		}

	}

}
