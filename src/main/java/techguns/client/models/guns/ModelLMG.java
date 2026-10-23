package techguns.client.models.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelLMG extends ModelMultipart {

	 //fields
   TGModelRenderer Grip1;
   TGModelRenderer Trigger01;
   TGModelRenderer Trigger02;
   TGModelRenderer Grip02;
   TGModelRenderer IronSight01;
   TGModelRenderer Bullet01;
   TGModelRenderer IronSight02;
   TGModelRenderer Barrelpart;
   TGModelRenderer Mag03;
   TGModelRenderer Grip01;
   TGModelRenderer BarrelRails04;
   TGModelRenderer Stock01;
   TGModelRenderer Receiver07;
   TGModelRenderer Stock02;
   TGModelRenderer Stock04;
   TGModelRenderer Stock05;
   TGModelRenderer Stock03;
   TGModelRenderer Trigger03;
   TGModelRenderer Receiver06;
   TGModelRenderer Mag01;
   TGModelRenderer Mag02;
   TGModelRenderer Barrel03;
   TGModelRenderer Bullet06;
   TGModelRenderer Bullet05;
   TGModelRenderer Bullet04;
   TGModelRenderer Bullet03;
   TGModelRenderer Bullet02;
   TGModelRenderer Receiver01;
   TGModelRenderer Receiver02;
   TGModelRenderer Barrel;
   TGModelRenderer Receiver03;
   TGModelRenderer Receiver04;
   TGModelRenderer RedDot;
   TGModelRenderer TopRails;
   TGModelRenderer Holo01;
   TGModelRenderer Holo02;
   TGModelRenderer Holo06;
   TGModelRenderer Holo03;
   TGModelRenderer Holo04;
   TGModelRenderer Holo05;
   TGModelRenderer Receiver08;
   TGModelRenderer Stock06;
   TGModelRenderer Stock07;
   TGModelRenderer IronSight03;
   TGModelRenderer Barrel02;
   TGModelRenderer Receiver05;
   TGModelRenderer Bullet00;
   TGModelRenderer Bullet07;
   
 public ModelLMG()
 {
   textureWidth = 128;
   textureHeight = 128;
   
     Grip1 = new TGModelRenderer(this, 68, 0);
     Grip1.addBox(0F, 0F, 0F, 3, 9, 4);
     Grip1.setRotationPoint(-2F, 2F, 6F);
     Grip1.setTextureSize(128, 128);
     Grip1.mirror = true;
     setRotation(Grip1, 0.4461433F, 0F, 0F);
     Trigger01 = new TGModelRenderer(this, 83, 21);
     Trigger01.addBox(0F, 0F, 0F, 2, 3, 1);
     Trigger01.setRotationPoint(-1.5F, 2F, 2F);
     Trigger01.setTextureSize(128, 128);
     Trigger01.mirror = true;
     setRotation(Trigger01, 0F, 0F, 0F);
     Trigger02 = new TGModelRenderer(this, 83, 17);
     Trigger02.addBox(0F, 0F, 0F, 1, 2, 1);
     Trigger02.setRotationPoint(-1F, 1.5F, 5F);
     Trigger02.setTextureSize(128, 128);
     Trigger02.mirror = true;
     setRotation(Trigger02, -0.2617994F, 0F, 0F);
     Grip02 = new TGModelRenderer(this, 56, 6);
     Grip02.addBox(0F, 0F, 0F, 2, 8, 2);
     Grip02.setRotationPoint(-2F, 3.5F, -14.5F);
     Grip02.setTextureSize(128, 128);
     Grip02.mirror = true;
     setRotation(Grip02, 0F, 0.7853982F, 0F);
     IronSight01 = new TGModelRenderer(this, 5, 39);
     IronSight01.addBox(0F, 0F, 0F, 1, 4, 1);
     IronSight01.setRotationPoint(-1F, -8F, -31F);
     IronSight01.setTextureSize(128, 128);
     IronSight01.mirror = true;
     setRotation(IronSight01, 0.4363323F, 0F, 0F);
     Bullet01 = new TGModelRenderer(this, 0, 26);
     Bullet01.addBox(0F, 0F, 0F, 1, 1, 6);
     Bullet01.setRotationPoint(2.5F, -4F, -5.5F);
     Bullet01.setTextureSize(128, 128);
     Bullet01.mirror = true;
     setRotation(Bullet01, 0F, 0F, 0.7853982F);
     Bullet00 = new TGModelRenderer(this, 0, 26);
     Bullet00.addBox(0F, 0F, 0F, 1, 1, 6);
     Bullet00.setRotationPoint(1.5F, -4F, -5.5F);
     Bullet00.setTextureSize(128, 128);
     Bullet00.mirror = true;
     setRotation(Bullet00, 0F, 0F, 0.7853982F);
     IronSight02 = new TGModelRenderer(this, 0, 34);
     IronSight02.addBox(0F, 0F, 0F, 1, 2, 2);
     IronSight02.setRotationPoint(-1F, -9F, -32F);
     IronSight02.setTextureSize(128, 128);
     IronSight02.mirror = true;
     setRotation(IronSight02, 0F, 0F, 0F);
     Barrelpart = new TGModelRenderer(this, 0, 39);
     Barrelpart.addBox(0F, 0F, 0F, 1, 2, 4);
     Barrelpart.setRotationPoint(-1F, -4F, -32F);
     Barrelpart.setTextureSize(128, 128);
     Barrelpart.mirror = true;
     setRotation(Barrelpart, 0F, 0F, 0F);
     Mag03 = new TGModelRenderer(this, 39, 66);
     Mag03.addBox(0F, 0F, 0F, 3, 6, 7);
     Mag03.setRotationPoint(-3.5F, 1F, -6F);
     Mag03.setTextureSize(128, 128);
     Mag03.mirror = true;
     setRotation(Mag03, 0F, 0F, 0.7888888F);
     Grip01 = new TGModelRenderer(this, 53, 0);
     Grip01.addBox(0F, 0F, 0F, 3, 2, 4);
     Grip01.setRotationPoint(-2F, 1.5F, -16.5F);
     Grip01.setTextureSize(128, 128);
     Grip01.mirror = true;
     setRotation(Grip01, 0F, 0F, 0F);
     BarrelRails04 = new TGModelRenderer(this, 42, 33);
     BarrelRails04.addBox(0F, 0F, 0F, 2, 1, 12);
     BarrelRails04.setRotationPoint(-1.5F, 1F, -20F);
     BarrelRails04.setTextureSize(128, 128);
     BarrelRails04.mirror = true;
     setRotation(BarrelRails04, 0F, 0F, 0F);
     Stock01 = new TGModelRenderer(this, 106, 25);
     Stock01.addBox(0F, 0F, 0F, 3, 3, 8);
     Stock01.setRotationPoint(-2F, -2F, 11F);
     Stock01.setTextureSize(128, 128);
     Stock01.mirror = true;
     setRotation(Stock01, 0.1783541F, 0F, 0F);
     Receiver07 = new TGModelRenderer(this, 92, 21);
     Receiver07.addBox(0F, 0F, 0F, 3, 2, 2);
     Receiver07.setRotationPoint(-2F, -1F, 10F);
     Receiver07.setTextureSize(128, 128);
     Receiver07.mirror = true;
     setRotation(Receiver07, 0F, 0F, 0F);
     Stock02 = new TGModelRenderer(this, 110, 48);
     Stock02.addBox(0F, 0F, 0F, 3, 1, 6);
     Stock02.setRotationPoint(-2F, -6F, 23F);
     Stock02.setTextureSize(128, 128);
     Stock02.mirror = true;
     setRotation(Stock02, 0F, 0F, 0F);
     Stock04 = new TGModelRenderer(this, 79, 64);
     Stock04.addBox(0F, 0F, 0F, 3, 7, 8);
     Stock04.setRotationPoint(-2F, -4F, 21F);
     Stock04.setTextureSize(128, 128);
     Stock04.mirror = true;
     setRotation(Stock04, 0F, 0F, 0F);
     Stock05 = new TGModelRenderer(this, 119, 68);
     Stock05.addBox(0F, 0F, 0F, 3, 1, 1);
     Stock05.setRotationPoint(-2F, 2F, 20F);
     Stock05.setTextureSize(128, 128);
     Stock05.mirror = true;
     setRotation(Stock05, 0F, 0F, 0F);
     Stock03 = new TGModelRenderer(this, 108, 39);
     Stock03.addBox(0F, 0F, 0F, 3, 1, 7);
     Stock03.setRotationPoint(-2F, -5F, 22F);
     Stock03.setTextureSize(128, 128);
     Stock03.mirror = true;
     setRotation(Stock03, 0F, 0F, 0F);
     Trigger03 = new TGModelRenderer(this, 83, 26);
     Trigger03.addBox(0F, 0F, 0F, 2, 1, 5);
     Trigger03.setRotationPoint(-1.5F, 4F, 3F);
     Trigger03.setTextureSize(128, 128);
     Trigger03.mirror = true;
     setRotation(Trigger03, 0F, 0F, 0F);
     Receiver06 = new TGModelRenderer(this, 106, 13);
     Receiver06.addBox(0F, 0F, 0F, 3, 3, 8);
     Receiver06.setRotationPoint(-2F, -1F, 2F);
     Receiver06.setTextureSize(128, 128);
     Receiver06.mirror = true;
     setRotation(Receiver06, 0F, 0F, 0F);
     Mag01 = new TGModelRenderer(this, 0, 71);
     Mag01.addBox(0F, 0F, 0F, 13, 6, 7);
     Mag01.setRotationPoint(-7.5F, 5F, -6F);
     Mag01.setTextureSize(128, 128);
     Mag01.mirror = true;
     setRotation(Mag01, 0F, 0F, 0F);
     Mag02 = new TGModelRenderer(this, 0, 59);
     Mag02.addBox(0F, 0F, 0F, 9, 4, 7);
     Mag02.setRotationPoint(-3.5F, 1F, -6F);
     Mag02.setTextureSize(128, 128);
     Mag02.mirror = true;
     setRotation(Mag02, 0F, 0F, 0F);
     Barrel03 = new TGModelRenderer(this, 22, 25);
     Barrel03.addBox(0F, 0F, 0F, 3, 3, 2);
     Barrel03.setRotationPoint(-0.5F, -3.2F, -28F);
     Barrel03.setTextureSize(128, 128);
     Barrel03.mirror = true;
     setRotation(Barrel03, 0F, 0F, 0.7853982F);
     Bullet07 = new TGModelRenderer(this, 0, 26);
     Bullet07.addBox(0F, 0F, 0F, 1, 1, 6);
     Bullet07.setRotationPoint(4.5F, 1.5F, -5.5F);
     Bullet07.setTextureSize(128, 128);
     Bullet07.mirror = true;
     setRotation(Bullet07, 0F, 0F, 0.7853982F);     
     Bullet06 = new TGModelRenderer(this, 0, 26);
     Bullet06.addBox(0F, 0F, 0F, 1, 1, 6);
     Bullet06.setRotationPoint(4.5F, 0.5F, -5.5F);
     Bullet06.setTextureSize(128, 128);
     Bullet06.mirror = true;
     setRotation(Bullet06, 0F, 0F, 0.7853982F);
     Bullet05 = new TGModelRenderer(this, 0, 26);
     Bullet05.addBox(0F, 0F, 0F, 1, 1, 6);
     Bullet05.setRotationPoint(4F, -0.5F, -5.5F);
     Bullet05.setTextureSize(128, 128);
     Bullet05.mirror = true;
     setRotation(Bullet05, 0F, 0F, 0.7853982F);
     Bullet04 = new TGModelRenderer(this, 0, 26);
     Bullet04.addBox(0F, 0F, 0F, 1, 1, 6);
     Bullet04.setRotationPoint(4F, -1.5F, -5.5F);
     Bullet04.setTextureSize(128, 128);
     Bullet04.mirror = true;
     setRotation(Bullet04, 0F, 0F, 0.7853982F);
     Bullet03 = new TGModelRenderer(this, 0, 26);
     Bullet03.addBox(0F, 0F, 0F, 1, 1, 6);
     Bullet03.setRotationPoint(4F, -2.5F, -5.5F);
     Bullet03.setTextureSize(128, 128);
     Bullet03.mirror = true;
     setRotation(Bullet03, 0F, 0F, 0.7853982F);
     Bullet02 = new TGModelRenderer(this, 0, 26);
     Bullet02.addBox(0F, 0F, 0F, 1, 1, 6);
     Bullet02.setRotationPoint(3.5F, -3.5F, -5.5F);
     Bullet02.setTextureSize(128, 128);
     Bullet02.mirror = true;
     setRotation(Bullet02, 0F, 0F, 0.7853982F);
     Receiver01 = new TGModelRenderer(this, 54, 13);
     Receiver01.addBox(0F, 0F, 0F, 4, 1, 20);
     Receiver01.setRotationPoint(-2.5F, -7F, -8F);
     Receiver01.setTextureSize(128, 128);
     Receiver01.mirror = true;
     setRotation(Receiver01, 0F, 0F, 0F);
     Receiver02 = new TGModelRenderer(this, 54, 34);
     Receiver02.addBox(0F, 0F, 0F, 5, 6, 18);
     Receiver02.setRotationPoint(-3F, -5F, -26F);
     Receiver02.setTextureSize(128, 128);
     Receiver02.mirror = true;
     setRotation(Receiver02, 0F, 0F, 0F);
     Barrel = new TGModelRenderer(this, 0, 26);
     Barrel.addBox(0F, 0F, 0F, 2, 2, 18);
     Barrel.setRotationPoint(-0.5F, -5.5F, -44F);
     Barrel.setTextureSize(128, 128);
     Barrel.mirror = true;
     setRotation(Barrel, 0F, 0F, 0.7853982F);
     Receiver03 = new TGModelRenderer(this, 66, 81);
     Receiver03.addBox(0F, 0F, 0F, 5, 5, 21);
     Receiver03.setRotationPoint(-3F, -6F, -8F);
     Receiver03.setTextureSize(128, 128);
     Receiver03.mirror = true;
     setRotation(Receiver03, 0F, 0F, 0F);
     Receiver04 = new TGModelRenderer(this, 31, 47);
     Receiver04.addBox(0F, 0F, 0F, 3, 1, 16);
     Receiver04.setRotationPoint(-2F, -7F, -24F);
     Receiver04.setTextureSize(128, 128);
     Receiver04.mirror = true;
     setRotation(Receiver04, 0F, 0F, 0F);
     RedDot = new TGModelRenderer(this, 105, 10);
     RedDot.addBox(0F, 0F, 0F, 1, 1, 0);
     RedDot.setRotationPoint(-1F, -10.5F, 5.5F);
     RedDot.setTextureSize(128, 128);
     RedDot.mirror = true;
     setRotation(RedDot, 0F, 0F, 0F);
     TopRails = new TGModelRenderer(this, 0, 0);
     TopRails.addBox(0F, 0F, 0F, 2, 1, 17);
     TopRails.setRotationPoint(-1.5F, -8F, -5.5F);
     TopRails.setTextureSize(128, 128);
     TopRails.mirror = true;
     setRotation(TopRails, 0F, 0F, 0F);
     Holo01 = new TGModelRenderer(this, 2, 48);
     Holo01.addBox(0F, 0F, 0F, 3, 2, 8);
     Holo01.setRotationPoint(-2F, -9F, -0.5F);
     Holo01.setTextureSize(128, 128);
     Holo01.mirror = true;
     setRotation(Holo01, 0F, 0F, 0F);
     Holo02 = new TGModelRenderer(this, 116, 0);
     Holo02.addBox(0F, 0F, 0F, 1, 2, 5);
     Holo02.setRotationPoint(-2.5F, -9F, 1.5F);
     Holo02.setTextureSize(128, 128);
     Holo02.mirror = true;
     setRotation(Holo02, 0F, 0F, 0F);
     Holo06 = new TGModelRenderer(this, 116, 0);
     Holo06.addBox(0F, 0F, 0F, 1, 2, 5);
     Holo06.setRotationPoint(0.5F, -9F, 1.5F);
     Holo06.setTextureSize(128, 128);
     Holo06.mirror = true;
     setRotation(Holo06, 0F, 0F, 0F);
     Holo03 = new TGModelRenderer(this, 105, 0);
     Holo03.addBox(0F, 0F, 0F, 1, 3, 4);
     Holo03.setRotationPoint(0.5F, -12F, 2.5F);
     Holo03.setTextureSize(128, 128);
     Holo03.mirror = true;
     setRotation(Holo03, 0F, 0F, 0F);
     Holo04 = new TGModelRenderer(this, 105, 0);
     Holo04.addBox(0F, 0F, 0F, 1, 3, 4);
     Holo04.setRotationPoint(-2.5F, -12F, 2.5F);
     Holo04.setTextureSize(128, 128);
     Holo04.mirror = true;
     setRotation(Holo04, 0F, 0F, 0F);
     Holo05 = new TGModelRenderer(this, 104, 8);
     Holo05.addBox(0F, 0F, 0F, 2, 1, 4);
     Holo05.setRotationPoint(-1.5F, -12F, 2.5F);
     Holo05.setTextureSize(128, 128);
     Holo05.mirror = true;
     setRotation(Holo05, 0F, 0F, 0F);
     Receiver08 = new TGModelRenderer(this, 98, 81);
     Receiver08.addBox(0F, 0F, 0F, 5, 3, 10);
     Receiver08.setRotationPoint(-3F, -1F, -8F);
     Receiver08.setTextureSize(128, 128);
     Receiver08.mirror = true;
     setRotation(Receiver08, 0F, 0F, 0F);
     Stock06 = new TGModelRenderer(this, 102, 65);
     Stock06.addBox(-3F, 0F, 0F, 3, 5, 10);
     Stock06.setRotationPoint(2F, -6F, 13F);
     Stock06.setTextureSize(128, 128);
     Stock06.mirror = true;
     setRotation(Stock06, -0.2050762F, -0.1047198F, 0F);
     Stock07 = new TGModelRenderer(this, 102, 65);
     Stock07.addBox(0F, 0F, 0F, 3, 5, 10);
     Stock07.setRotationPoint(-3F, -6F, 13F);
     Stock07.setTextureSize(128, 128);
     Stock07.mirror = true;
     setRotation(Stock07, -0.2050762F, 0.1047198F, 0F);
     IronSight03 = new TGModelRenderer(this, 0, 39);
     IronSight03.addBox(0F, 0F, 0F, 1, 3, 1);
     IronSight03.setRotationPoint(-1F, -7F, -32F);
     IronSight03.setTextureSize(128, 128);
     IronSight03.mirror = true;
     setRotation(IronSight03, 0F, 0F, 0F);
     Barrel02 = new TGModelRenderer(this, 22, 31);
     Barrel02.addBox(0F, 0F, 0F, 2, 2, 8);
     Barrel02.setRotationPoint(-0.5F, -2.5F, -36F);
     Barrel02.setTextureSize(128, 128);
     Barrel02.mirror = true;
     setRotation(Barrel02, 0F, 0F, 0.7853982F);
     Receiver05 = new TGModelRenderer(this, 28, 12);
     Receiver05.addBox(0F, 0F, 0F, 4, 1, 18);
     Receiver05.setRotationPoint(-2.5F, -6F, -26F);
     Receiver05.setTextureSize(128, 128);
     Receiver05.mirror = true;
     setRotation(Receiver05, 0F, 0F, 0F);
 }
 
	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5, int ammoLeft, float reloadProgress, ItemDisplayContext transformType, int part,
			float fireProgress, float chargeProgress) {
		
   Grip1.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Trigger01.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Trigger02.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Grip02.render(poseStack, buffer, packedLight, packedOverlay, f5);
   IronSight01.render(poseStack, buffer, packedLight, packedOverlay, f5);
   IronSight02.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Barrelpart.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Mag03.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Grip01.render(poseStack, buffer, packedLight, packedOverlay, f5);
   BarrelRails04.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Stock01.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Receiver07.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Stock02.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Stock04.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Stock05.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Stock03.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Trigger03.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Receiver06.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Mag01.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Mag02.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Barrel03.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Receiver01.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Receiver02.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Barrel.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Receiver03.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Receiver04.render(poseStack, buffer, packedLight, packedOverlay, f5);
   RedDot.render(poseStack, buffer, packedLight, packedOverlay, f5);
   TopRails.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Holo01.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Holo02.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Holo06.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Holo03.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Holo04.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Holo05.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Receiver08.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Stock06.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Stock07.render(poseStack, buffer, packedLight, packedOverlay, f5);
   IronSight03.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Barrel02.render(poseStack, buffer, packedLight, packedOverlay, f5);
   Receiver05.render(poseStack, buffer, packedLight, packedOverlay, f5);
       
   if (reloadProgress==0f || reloadProgress > 0.5f) {
	   if (ammoLeft > 7 || ammoLeft==7 && fireProgress>0) {
	    	animateBullet(poseStack, buffer, packedLight, packedOverlay, Bullet07, f5, transformType, Bullet06, fireProgress);
	    	//Bullet06.render(poseStack, buffer, packedLight, packedOverlay, f5);
	    }
	    if (ammoLeft > 6 || ammoLeft==6 && fireProgress>0) {
	    	animateBullet(poseStack, buffer, packedLight, packedOverlay, Bullet06, f5, transformType, Bullet05, fireProgress);
	    	//Bullet06.render(poseStack, buffer, packedLight, packedOverlay, f5);
	    }
	    if (ammoLeft > 5 || ammoLeft==5 && fireProgress>0) {
	    	//Bullet05.render(poseStack, buffer, packedLight, packedOverlay, f5);
	    	animateBullet(poseStack, buffer, packedLight, packedOverlay, Bullet05, f5, transformType, Bullet04, fireProgress);
	    }
	    if (ammoLeft > 4 || ammoLeft==4 && fireProgress>0) {
	    	animateBullet(poseStack, buffer, packedLight, packedOverlay, Bullet04, f5, transformType, Bullet03, fireProgress);
	    	//Bullet04.render(poseStack, buffer, packedLight, packedOverlay, f5);
	    }
	    if (ammoLeft > 3 || ammoLeft==3 && fireProgress>0) {
	    	animateBullet(poseStack, buffer, packedLight, packedOverlay, Bullet03, f5, transformType, Bullet02, fireProgress);
	    	//Bullet03.render(poseStack, buffer, packedLight, packedOverlay, f5);
	    }
	    if (ammoLeft > 2 || ammoLeft==2 && fireProgress>0) {
	    	animateBullet(poseStack, buffer, packedLight, packedOverlay, Bullet02, f5, transformType, Bullet01, fireProgress);
	    	//Bullet02.render(poseStack, buffer, packedLight, packedOverlay, f5);
	    }
	    if (ammoLeft > 1 || ammoLeft==1 && fireProgress>0) {
	    	animateBullet(poseStack, buffer, packedLight, packedOverlay, Bullet01, f5, transformType, Bullet00, fireProgress);
	    	//Bullet01.render(poseStack, buffer, packedLight, packedOverlay, f5);
	    }
   }
 }
	
	protected void animateBullet(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, TGModelRenderer bullet, float scale, ItemDisplayContext transform, TGModelRenderer targetPos, float recoilProgress) {
		if ( transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
			float x,y,z;
			recoilProgress = recoilProgress*4F;
			if (recoilProgress>1.0f) {
				recoilProgress=1.0f;
			}
			
			x = (targetPos.rotationPointX-bullet.rotationPointX)*recoilProgress;
			y = (targetPos.rotationPointY-bullet.rotationPointY)*recoilProgress;
			z = (targetPos.rotationPointZ-bullet.rotationPointZ)*recoilProgress;
			
			poseStack.pushPose();
			poseStack.translate(x*scale, y*scale, z*scale);
			bullet.render(poseStack, buffer, packedLight, packedOverlay, scale);
			poseStack.popPose();
		} else {
			bullet.render(poseStack, buffer, packedLight, packedOverlay, scale);
		}
	}
}