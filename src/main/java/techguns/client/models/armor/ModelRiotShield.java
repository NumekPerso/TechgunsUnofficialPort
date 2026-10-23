package techguns.client.models.armor;

import techguns.client.models.TGModelBase;
import techguns.client.models.TGModelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelRiotShield extends TGModelBase implements IShieldModel {
    public TGModelRenderer Plate;
    public TGModelRenderer Handle;
    public TGModelRenderer TopBorder;
    public TGModelRenderer BottomBorder;
    public TGModelRenderer SideBorder0;
    public TGModelRenderer SideBorder1;

    public ModelRiotShield() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.BottomBorder = new TGModelRenderer(this, 0, 22);
        this.BottomBorder.setRotationPoint(-5.0F, 10.0F, -1.5F);
        this.BottomBorder.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
        this.SideBorder1 = new TGModelRenderer(this, 27, 0);
        this.SideBorder1.setRotationPoint(-6.0F, -10.5F, -1.5F);
        this.SideBorder1.addBox(0.0F, 0.0F, 0.0F, 1, 21, 1, 0.0F);
        this.Plate = new TGModelRenderer(this, 0, 0);
        this.Plate.setRotationPoint(-5.0F, -10.0F, -2.0F);
        this.Plate.addBox(0.0F, 0.0F, 0.0F, 10, 20, 1, 0.0F);
        this.Handle = new TGModelRenderer(this, 16, 20);
        this.Handle.setRotationPoint(-1.0F, -3.0F, -1.0F);
        this.Handle.addBox(0.0F, 0.0F, 0.0F, 2, 6, 6, 0.0F);
        this.SideBorder0 = new TGModelRenderer(this, 27, 0);
        this.SideBorder0.setRotationPoint(5.0F, -10.5F, -1.5F);
        this.SideBorder0.addBox(0.0F, 0.0F, 0.0F, 1, 21, 1, 0.0F);
        this.TopBorder = new TGModelRenderer(this, 0, 22);
        this.TopBorder.setRotationPoint(-5.0F, -11.0F, -1.5F);
        this.TopBorder.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float f5) { 
        this.BottomBorder.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.SideBorder1.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.Plate.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.Handle.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.SideBorder0.render(poseStack, buffer, packedLight, packedOverlay, f5);
        this.TopBorder.render(poseStack, buffer, packedLight, packedOverlay, f5);
    }

}
