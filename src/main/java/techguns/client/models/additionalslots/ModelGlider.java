package techguns.client.models.additionalslots;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.entity.player.Player;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.models.TGModelRenderer;

public class ModelGlider extends ModelAdditionalSlotBase {

	TGModelRenderer BackBack;
	TGModelRenderer MidR01;
	TGModelRenderer TopR01;
	TGModelRenderer TopR02;
	TGModelRenderer MidR02;
	TGModelRenderer TopL01;
	TGModelRenderer TopL02;
	TGModelRenderer InnerR02;
	TGModelRenderer MidL02;
	TGModelRenderer MidL01;
	TGModelRenderer InnerR01;
	TGModelRenderer InnerL02;
	TGModelRenderer InnerL01;

	ArrayList<TGModelRenderer> parts = new ArrayList<>();

	public ModelGlider() {
		textureWidth = 32;
		textureHeight = 32;

		BackBack = new TGModelRenderer(this, 0, 0);
		BackBack.addBox(-3F, 0.5F, 2F, 7, 10, 4);
		BackBack.setRotationPoint(-0.5F, 0F, 0F);
		BackBack.setTextureSize(32, 32);
		BackBack.mirror = true;
		setRotation(BackBack, 0F, 0F, 0F);

		MidR01 = new TGModelRenderer(this, 23, 0);
		MidR01.addBox(0F, -1.0F, 1.0F, 1, 7, 2);
		MidR01.setRotationPoint(-9.5F, 3F, 3F);
		MidR01.setTextureSize(32, 32);
		MidR01.mirror = true;
		setRotation(MidR01, 0F, 0F, 0F);

		TopR01 = new TGModelRenderer(this, 13, 15);
		TopR01.addBox(0F, -1.0F, 1.0F, 6, 1, 2);
		TopR01.setRotationPoint(-9.5F, 2F, 3F);
		TopR01.setTextureSize(32, 32);
		TopR01.mirror = true;
		setRotation(TopR01, 0F, 0F, 0F);

		TopR02 = new TGModelRenderer(this, 13, 15);
		TopR02.addBox(0F, -1.0F, 1.0F, 6, 1, 2);
		TopR02.setRotationPoint(-15.5F, 3F, 3F);
		TopR02.setTextureSize(32, 32);
		TopR02.mirror = true;
		setRotation(TopR02, 0F, 0F, 0F);

		MidR02 = new TGModelRenderer(this, 21, 19);
		MidR02.addBox(0F, -1.0F, 1.0F, 1, 6, 2);
		MidR02.setRotationPoint(-16.5F, 4F, 3F);
		MidR02.setTextureSize(32, 32);
		MidR02.mirror = true;
		setRotation(MidR02, 0F, 0F, 0F);

		TopL01 = new TGModelRenderer(this, 13, 15);
		TopL01.addBox(0F, -1.0F, 1.0F, 6, 1, 2);
		TopL01.setRotationPoint(3.5F, 2F, 3F);
		TopL01.setTextureSize(32, 32);
		TopL01.mirror = true;
		setRotation(TopL01, 0F, 0F, 0F);

		TopL02 = new TGModelRenderer(this, 13, 15);
		TopL02.addBox(0F, -1.0F, 1.0F, 6, 1, 2);
		TopL02.setRotationPoint(9.5F, 3F, 3F);
		TopL02.setTextureSize(32, 32);
		TopL02.mirror = true;
		setRotation(TopL02, 0F, 0F, 0F);

		InnerR02 = new TGModelRenderer(this, 0, 24);
		InnerR02.addBox(0F, -1.0F, 1.0F, 6, 6, 1);
		InnerR02.setRotationPoint(-15.5F, 4F, 3.5F);
		InnerR02.setTextureSize(32, 32);
		InnerR02.mirror = true;
		setRotation(InnerR02, 0F, 0F, 0F);

		MidL02 = new TGModelRenderer(this, 21, 19);
		MidL02.addBox(0F, -1.0F, 1.0F, 1, 6, 2);
		MidL02.setRotationPoint(15.5F, 4F, 3F);
		MidL02.setTextureSize(32, 32);
		MidL02.mirror = true;
		setRotation(MidL02, 0F, 0F, 0F);

		MidL01 = new TGModelRenderer(this, 23, 0);
		MidL01.addBox(0F, -1.0F, 1.0F, 1, 7, 2);
		MidL01.setRotationPoint(8.5F, 3F, 3F);
		MidL01.setTextureSize(32, 32);
		MidL01.mirror = true;
		setRotation(MidL01, 0F, 0F, 0F);

		InnerR01 = new TGModelRenderer(this, 0, 15);
		InnerR01.addBox(0F, -1.0F, 1.0F, 5, 7, 1);
		InnerR01.setRotationPoint(-8.5F, 3F, 3.5F);
		InnerR01.setTextureSize(32, 32);
		InnerR01.mirror = true;
		setRotation(InnerR01, 0F, 0F, 0F);

		InnerL02 = new TGModelRenderer(this, 0, 24);
		InnerL02.addBox(0F, -1.0F, 1.0F, 6, 6, 1);
		InnerL02.setRotationPoint(9.5F, 4F, 3.5F);
		InnerL02.setTextureSize(32, 32);
		InnerL02.mirror = true;
		setRotation(InnerL02, 0F, 0F, 0F);

		InnerL01 = new TGModelRenderer(this, 0, 15);
		InnerL01.addBox(0F, -1.0F, 1.0F, 5, 7, 1);
		InnerL01.setRotationPoint(3.5F, 3F, 3.5F);
		InnerL01.setTextureSize(32, 32);
		InnerL01.mirror = true;
		setRotation(InnerL01, 0F, 0F, 0F);

		parts.add(BackBack);
		parts.add(MidR01);
		parts.add(TopR01);
		parts.add(TopR02);
		parts.add(MidR02);
		parts.add(TopL01);
		parts.add(TopL02);
		parts.add(MidL02);
		parts.add(MidL01);
		parts.add(InnerR01);
		parts.add(InnerR02);
		parts.add(InnerL01);
		parts.add(InnerL02);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, Player entityIn) {
		boolean isGliding = false;
		if (entityIn != null) {
			TGExtendedPlayer props = TGExtendedPlayer.get(entityIn);
			isGliding = props != null && props.isGliding;
		}
		translateToBody(poseStack, scale);
		BackBack.render(poseStack, buffer, packedLight, packedOverlay, scale);
		if (isGliding) {
			MidR01.render(poseStack, buffer, packedLight, packedOverlay, scale);
			TopR01.render(poseStack, buffer, packedLight, packedOverlay, scale);
			TopR02.render(poseStack, buffer, packedLight, packedOverlay, scale);
			MidR02.render(poseStack, buffer, packedLight, packedOverlay, scale);
			TopL01.render(poseStack, buffer, packedLight, packedOverlay, scale);
			TopL02.render(poseStack, buffer, packedLight, packedOverlay, scale);
			InnerR02.render(poseStack, buffer, packedLight, packedOverlay, scale);
			MidL02.render(poseStack, buffer, packedLight, packedOverlay, scale);
			MidL01.render(poseStack, buffer, packedLight, packedOverlay, scale);
			InnerR01.render(poseStack, buffer, packedLight, packedOverlay, scale);
			InnerL02.render(poseStack, buffer, packedLight, packedOverlay, scale);
			InnerL01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		}
	}

	@Override
	public void copyRotateAngles() {
		parts.forEach(p -> {
			p.rotateAngleX = this.bipedBody.rotateAngleX;
		});
	}

}
