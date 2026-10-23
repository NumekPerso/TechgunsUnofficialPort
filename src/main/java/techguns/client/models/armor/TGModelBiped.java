package techguns.client.models.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import techguns.client.models.TGModelBase;
import techguns.client.models.TGModelRenderer;

/**
 * Emulation of the 1.12 ModelBiped for the Techguns armor models (same default biped boxes, same render order).
 * The part poses are copied from the vanilla HumanoidModel the armor is rendered for (1.12 setRotationAngles), see
 * {@link #setPosesFrom}; {@link #setDefaultPose} emulates setRotationAngles(0,0,0,0,0) for the inventory rendering.
 */
public class TGModelBiped extends TGModelBase {

	public TGModelRenderer bipedHead;
	public TGModelRenderer bipedHeadwear;
	public TGModelRenderer bipedBody;
	public TGModelRenderer bipedRightArm;
	public TGModelRenderer bipedLeftArm;
	public TGModelRenderer bipedRightLeg;
	public TGModelRenderer bipedLeftLeg;

	public TGModelBiped() {
		this(0.0F);
	}

	public TGModelBiped(float modelSize) {
		this(modelSize, 0.0F, 64, 32);
	}

	public TGModelBiped(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn) {
		this.textureWidth = textureWidthIn;
		this.textureHeight = textureHeightIn;
		this.bipedHead = new TGModelRenderer(this, 0, 0);
		this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
		this.bipedHead.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
		this.bipedHeadwear = new TGModelRenderer(this, 32, 0);
		this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
		this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
		this.bipedBody = new TGModelRenderer(this, 16, 16);
		this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
		this.bipedBody.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
		this.bipedRightArm = new TGModelRenderer(this, 40, 16);
		this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i1149_2_, 0.0F);
		this.bipedLeftArm = new TGModelRenderer(this, 40, 16);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + p_i1149_2_, 0.0F);
		this.bipedRightLeg = new TGModelRenderer(this, 0, 16);
		this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i1149_2_, 0.0F);
		this.bipedLeftLeg = new TGModelRenderer(this, 0, 16);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + p_i1149_2_, 0.0F);
	}

	/**
	 * sets the rotation angles of a part
	 */
	protected static void setAngles(TGModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	/**
	 * Copies the poses of the vanilla model parts (1.20 setupAnim already includes sneaking / riding / swinging...)
	 */
	public void setPosesFrom(HumanoidModel<?> model) {
		this.bipedHead.copyFrom(model.head);
		this.bipedHeadwear.copyFrom(model.hat);
		this.bipedBody.copyFrom(model.body);
		this.bipedRightArm.copyFrom(model.rightArm);
		this.bipedLeftArm.copyFrom(model.leftArm);
		this.bipedRightLeg.copyFrom(model.rightLeg);
		this.bipedLeftLeg.copyFrom(model.leftLeg);
	}

	/**
	 * 1.12 setRotationAngles(0,0,0,0,0,scale,null) (not sneaking), used for the 3D inventory rendering
	 */
	public void setDefaultPose() {
		this.bipedHead.setRotationPoint(0f, 0f, 0f);
		setAngles(this.bipedHead, 0f, 0f, 0f);
		this.bipedBody.setRotationPoint(0f, 0f, 0f);
		setAngles(this.bipedBody, 0f, 0f, 0f);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		setAngles(this.bipedRightArm, 0f, 0f, 0.1f);
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		setAngles(this.bipedLeftArm, 0f, 0f, -0.1f);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.1F);
		setAngles(this.bipedRightLeg, 0f, 0f, 0f);
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.1F);
		setAngles(this.bipedLeftLeg, 0f, 0f, 0f);
		copyModelAngles(this.bipedHead, this.bipedHeadwear);
	}

	/**
	 * Part visibility of an armor slot (1.12 GenericArmor.getArmorModel / RenderArmorItem)
	 */
	public void setVisibleForSlot(EquipmentSlot armorSlot, boolean showHeadwear) {
		this.bipedHead.showModel = armorSlot == EquipmentSlot.HEAD;
		this.bipedHeadwear.showModel = armorSlot == EquipmentSlot.HEAD && showHeadwear;
		this.bipedBody.showModel = armorSlot == EquipmentSlot.CHEST || armorSlot == EquipmentSlot.LEGS;
		this.bipedRightArm.showModel = armorSlot == EquipmentSlot.CHEST;
		this.bipedLeftArm.showModel = armorSlot == EquipmentSlot.CHEST;
		this.bipedRightLeg.showModel = armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.FEET;
		this.bipedLeftLeg.showModel = armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.FEET;
	}

	/**
	 * 1.12 ModelBiped.render (without setRotationAngles, the sneaking offset is part of the copied 1.20 poses)
	 */
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, boolean isChild, float scale) {
		poseStack.pushPose();
		if (isChild) {
			poseStack.scale(0.75F, 0.75F, 0.75F);
			poseStack.translate(0.0F, 16.0F * scale, 0.0F);
			this.bipedHead.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			poseStack.popPose();
			poseStack.pushPose();
			poseStack.scale(0.5F, 0.5F, 0.5F);
			poseStack.translate(0.0F, 24.0F * scale, 0.0F);
			this.bipedBody.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedRightArm.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedLeftArm.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedRightLeg.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedLeftLeg.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedHeadwear.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
		} else {
			this.bipedHead.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedBody.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedRightArm.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedLeftArm.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedRightLeg.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedLeftLeg.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			this.bipedHeadwear.render(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
		}
		poseStack.popPose();
	}
}
