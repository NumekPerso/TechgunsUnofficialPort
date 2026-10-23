package techguns.client.models.additionalslots;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.entity.player.Player;
import techguns.client.models.TGModelRenderer;

public class ModelFaceMask extends ModelAdditionalSlotBase {
	// fields
	TGModelRenderer Head;
	TGModelRenderer Front;

	public ModelFaceMask(boolean hasFrontPart) {
		textureWidth = 32;
		textureHeight = 32;

		this.bipedHead = new TGModelRenderer(this, textureWidth, textureHeight);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);

		Head = new TGModelRenderer(this, 0, 0);
		Head.addBox(-4F, -8F, -4F, 8, 8, 8);
		Head.setRotationPoint(0F, 0F, 0F);
		Head.setTextureSize(32, 32);
		Head.mirror = true;
		setRotation(Head, 0F, 0F, 0F);
		this.bipedHead.addChild(Head);

		if (hasFrontPart) {
			Front = new TGModelRenderer(this, 9, 17);
			Front.addBox(0F, 0F, 0F, 4, 3, 1);
			Front.setRotationPoint(-2F, -3F, -5F);
			Front.setTextureSize(32, 32);
			Front.mirror = true;
			setRotation(Front, 0F, 0F, 0F);
			this.bipedHead.addChild(Front);
		}
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, Player entityIn) {
		this.bipedHead.render(poseStack, buffer, packedLight, packedOverlay, scale);
	}

	@Override
	public void copyRotateAngles() {
	}

}
