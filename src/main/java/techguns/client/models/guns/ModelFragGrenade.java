package techguns.client.models.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import techguns.client.models.ModelMultipart;

public class ModelFragGrenade extends ModelMultipart {

    public TGModelRenderer Top;
    public TGModelRenderer MainRot1;
    public TGModelRenderer MainRot2;
    public TGModelRenderer Main;
    public TGModelRenderer TopRotated;
    public TGModelRenderer Bottom;
    public TGModelRenderer BottomRotated;
    public TGModelRenderer TopPart;
    public TGModelRenderer Box01;
    public TGModelRenderer Box02;
    public TGModelRenderer Ring;

    protected boolean showRing;
    
    public ModelFragGrenade(boolean showRing) {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.Main = new TGModelRenderer(this, 13, 0);
        this.Main.setRotationPoint(-1.5F, -16.0F, -1.5F);
        this.Main.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
        this.MainRot2 = new TGModelRenderer(this, 13, 16);
        this.MainRot2.setRotationPoint(0.5F, -14.0F, 0.5F);
        this.MainRot2.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, -0.1F);
        this.setRotation(MainRot2, 0.0F, 0.7853981633974483F, 0.0F);
        this.BottomRotated = new TGModelRenderer(this, 15, 11);
        this.BottomRotated.setRotationPoint(0.5F, -12.0F, 0.5F);
        this.BottomRotated.addBox(-1.5F, 0.0F, -1.5F, 3, 1, 3, -0.1F);
        this.setRotation(BottomRotated, 0.0F, 0.7853981633974483F, 0.0F);
        this.TopPart = new TGModelRenderer(this, 15, 23);
        this.TopPart.setRotationPoint(-0.5F, -18.7F, -0.5F);
        this.TopPart.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, -0.2F);
        this.Box02 = new TGModelRenderer(this, 1, 28);
        this.Box02.setRotationPoint(4.1F, -17.3F, -0.5F);
        this.Box02.addBox(0.0F, 0.0F, 0.0F, 5, 1, 2, -0.3F);
        this.setRotation(Box02, 0.0F, 0.0F, 1.5707963267948966F);
        this.TopRotated = new TGModelRenderer(this, 15, 11);
        this.TopRotated.setRotationPoint(0.5F, -17.0F, 0.5F);
        this.TopRotated.addBox(-1.5F, 0.0F, -1.5F, 3, 1, 3, -0.1F);
        this.setRotation(TopRotated, 0.0F, 0.7853981633974483F, 0.0F);
        
        this.showRing=showRing;
        if (showRing){
	        this.Ring = new TGModelRenderer(this, 0, 7);
	        this.Ring.setRotationPoint(-0.5F, -20.2F, -3.3F);
	        this.Ring.addBox(0.0F, 0.0F, 0.0F, 2, 4, 4, -0.9F);
        }
        
        this.MainRot1 = new TGModelRenderer(this, 13, 16);
        this.MainRot1.setRotationPoint(0.5F, -16.0F, 0.5F);
        this.MainRot1.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, -0.1F);
        this.setRotation(MainRot1, 0.0F, 0.7853981633974483F, 0.0F);
        this.Top = new TGModelRenderer(this, 15, 11);
        this.Top.setRotationPoint(-1.0F, -17.0F, -1.0F);
        this.Top.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        this.Box01 = new TGModelRenderer(this, 19, 28);
        this.Box01.setRotationPoint(0.6F, -18.8F, -0.5F);
        this.Box01.addBox(0.0F, 0.0F, 0.0F, 4, 1, 2, -0.3F);
        this.setRotation(Box01, 0.0F, 0.0F, 0.40980330836826856F);
        this.Bottom = new TGModelRenderer(this, 15, 11);
        this.Bottom.setRotationPoint(-1.0F, -12.0F, -1.0F);
        this.Bottom.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
    }    
    
	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, int ammoLeft, float reloadProgress, ItemDisplayContext transformType, int part,
			float fireProgress, float chargeProgress) {
		
		this.Main.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.BottomRotated.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.MainRot2.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Box02.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Bottom.render(poseStack, buffer, packedLight, packedOverlay, scale);
        if (showRing && fireProgress<=0.0f){
        	this.Ring.render(poseStack, buffer, packedLight, packedOverlay, scale);
        }
        this.TopPart.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.MainRot1.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Top.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.TopRotated.render(poseStack, buffer, packedLight, packedOverlay, scale);
        this.Box01.render(poseStack, buffer, packedLight, packedOverlay, scale);
	}

}
