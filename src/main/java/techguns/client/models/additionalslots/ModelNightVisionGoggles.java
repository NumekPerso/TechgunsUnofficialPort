package techguns.client.models.additionalslots;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import techguns.client.models.TGModelRenderer;

public class ModelNightVisionGoggles extends ModelAdditionalSlotBase {
	// fields
	TGModelRenderer Head;
	TGModelRenderer Right;
	TGModelRenderer Left;
	TGModelRenderer Mount;
	TGModelRenderer Mid;

	TGModelRenderer Right_off;
	TGModelRenderer Left_off;
	TGModelRenderer Mount_off;
	TGModelRenderer Mid_off;

	public ModelNightVisionGoggles() {
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

		Right = new TGModelRenderer(this, 0, 17);
		Right.addBox(-1F, -1F, 0F, 2, 2, 4);
		Right.setRotationPoint(-1.7F, -4F, -8F);
		Right.setTextureSize(32, 32);
		Right.mirror = true;
		setRotation(Right, 0F, 0F, 0.7853982F);
		this.bipedHead.addChild(Right);

		Left = new TGModelRenderer(this, 0, 17);
		Left.addBox(-1F, -1F, 0F, 2, 2, 4);
		Left.setRotationPoint(1.7F, -4F, -8F);
		Left.setTextureSize(32, 32);
		Left.mirror = true;
		setRotation(Left, 0F, 0F, 0.7853982F);
		this.bipedHead.addChild(Left);

		Mount = new TGModelRenderer(this, 13, 21);
		Mount.addBox(-1F, 0F, 0F, 2, 1, 4);
		Mount.setRotationPoint(0F, -5F, -6.5F);
		Mount.setTextureSize(32, 32);
		Mount.mirror = true;
		setRotation(Mount, 0.7853982F, 0F, 0F);
		this.bipedHead.addChild(Mount);

		Mid = new TGModelRenderer(this, 13, 17);
		Mid.addBox(0F, 0F, 0F, 3, 1, 2);
		Mid.setRotationPoint(-1.5F, -5F, -7F);
		Mid.setTextureSize(32, 32);
		Mid.mirror = true;
		setRotation(Mid, 0F, 0F, 0F);
		this.bipedHead.addChild(Mid);

		/**
		 * off parts
		 */
		Right_off = new TGModelRenderer(this, 0, 17);
		Right_off.addBox(-1F, -1F, -2F, 2, 2, 4);
		Right_off.setRotationPoint(-1.7F, -9F, -5F);
		Right_off.setTextureSize(32, 32);
		Right_off.mirror = true;
		setRotation(Right_off, -2.356194F, -0.5410521F, 0.6108652F);
		this.bipedHead.addChild(Right_off);

		Left_off = new TGModelRenderer(this, 0, 17);
		Left_off.addBox(-1F, -1F, -2F, 2, 2, 4);
		Left_off.setRotationPoint(1.7F, -9F, -5F);
		Left_off.setTextureSize(32, 32);
		Left_off.mirror = true;
		setRotation(Left_off, -2.356194F, -0.5410521F, 0.6108652F);
		this.bipedHead.addChild(Left_off);

		Mount_off = new TGModelRenderer(this, 13, 21);
		Mount_off.addBox(-1F, 0F, -4F, 2, 1, 4);
		Mount_off.setRotationPoint(0F, -6F, -3.5F);
		Mount_off.setTextureSize(32, 32);
		Mount_off.mirror = true;
		setRotation(Mount_off, -1.301251F, 0F, 0F);
		this.bipedHead.addChild(Mount_off);

		Mid_off = new TGModelRenderer(this, 13, 17);
		Mid_off.addBox(-1F, 0F, -1F, 3, 1, 2);
		Mid_off.setRotationPoint(-0.5F, -10F, -5.5F);
		Mid_off.setTextureSize(32, 32);
		Mid_off.mirror = true;
		setRotation(Mid_off, 0.6108652F, 0F, 0F);
		this.bipedHead.addChild(Mid_off);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, Player entityIn) {

		boolean on = false;
		if (entityIn != null && entityIn.hasEffect(MobEffects.NIGHT_VISION)) {
			on = true;
		}

		this.Mid_off.isHidden = on;
		this.Mount_off.isHidden = on;
		this.Left_off.isHidden = on;
		this.Right_off.isHidden = on;

		this.Mid.isHidden = !on;
		this.Mount.isHidden = !on;
		this.Left.isHidden = !on;
		this.Right.isHidden = !on;

		this.bipedHead.render(poseStack, buffer, packedLight, packedOverlay, scale);
	}

	@Override
	public void copyRotateAngles() {
	}

}
