package techguns.client.models.armor;

import techguns.client.models.TGModelBase;
import techguns.client.models.TGModelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelAdvancedShield extends TGModelBase implements IShieldModel {
    public TGModelRenderer Plate;
    public TGModelRenderer Handle;
    public TGModelRenderer TopBorder;
    public TGModelRenderer BottomBorder;
    public TGModelRenderer SideBorder0;
    public TGModelRenderer SideBorder1;
    public TGModelRenderer Glass;
    public TGModelRenderer SideBorder2;
    public TGModelRenderer SideBorder3;

    public ModelAdvancedShield() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.SideBorder0 = new TGModelRenderer(this, 20, 0);
        this.SideBorder0.setRotationPoint(4.0F, -4.0F, -2.0F);
        this.SideBorder0.addBox(0.0F, 0.0F, 0.0F, 3, 15, 1, 0.0F);
        this.setRotateAngle(SideBorder0, 0.0F, -0.39269908169872414F, 0.0F);
        this.BottomBorder = new TGModelRenderer(this, 0, 16);
        this.BottomBorder.setRotationPoint(-4.0F, 10.0F, -2.0F);
        this.BottomBorder.addBox(0.0F, 0.0F, 0.0F, 8, 2, 1, 0.0F);
        this.setRotateAngle(BottomBorder, 0.39269908169872414F, 0.0F, 0.0F);
        this.Glass = new TGModelRenderer(this, 26, 17);
        this.Glass.setRotationPoint(-4.0F, -10.5F, -1.8F);
        //slightly shrunk: the translucent glass faces were coplanar with the borders (z-fighting)
        this.Glass.addBox(0.0F, 0.0F, 0.0F, 8, 7, 1, -0.01F);
        this.SideBorder3 = new TGModelRenderer(this, 29, 0);
        this.SideBorder3.setRotationPoint(4.0F, -11.0F, -2.0F);
        this.SideBorder3.addBox(0.0F, 0.0F, 0.0F, 2, 7, 1, 0.0F);
        this.setRotateAngle(SideBorder3, 0.0F, -0.39269908169872414F, 0.0F);
        this.Handle = new TGModelRenderer(this, 14, 20);
        this.Handle.setRotationPoint(-1.0F, -3.0F, -1.0F);
        this.Handle.addBox(0.0F, 0.0F, 0.0F, 2, 6, 6, 0.0F);
        this.SideBorder1 = new TGModelRenderer(this, 20, 0);
        this.SideBorder1.mirror = true;
        this.SideBorder1.setRotationPoint(-4.0F, -4.0F, -2.0F);
        this.SideBorder1.addBox(-3.0F, 0.0F, 0.0F, 3, 15, 1, 0.0F);
        this.setRotateAngle(SideBorder1, 0.0F, 0.39269908169872414F, 0.0F);
        this.Plate = new TGModelRenderer(this, 0, 0);
        this.Plate.setRotationPoint(-4.0F, -4.0F, -2.0F);
        this.Plate.addBox(0.0F, 0.0F, 0.0F, 8, 14, 1, 0.0F);
        this.TopBorder = new TGModelRenderer(this, 0, 20);
        this.TopBorder.setRotationPoint(-4.0F, -10.5F, -2.1F);
        this.TopBorder.addBox(0.0F, -1.0F, 0.0F, 8, 1, 1, 0.0F);
        this.setRotateAngle(TopBorder, -0.39269908169872414F, 0.0F, 0.0F);
        this.SideBorder2 = new TGModelRenderer(this, 29, 0);
        this.SideBorder2.mirror = true;
        this.SideBorder2.setRotationPoint(-4.0F, -11.0F, -2.0F);
        this.SideBorder2.addBox(-2.0F, 0.0F, 0.0F, 2, 7, 1, 0.0F);
        this.setRotateAngle(SideBorder2, 0.0F, 0.39269908169872414F, 0.0F);
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float f5) { 
        this.SideBorder0.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.BottomBorder.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.Glass.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.SideBorder3.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.Handle.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.SideBorder1.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.Plate.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.TopBorder.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.SideBorder2.render(poseStack, buffer, packedLight, packedOverlay, f5);
    }

    public void setRotateAngle(TGModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}