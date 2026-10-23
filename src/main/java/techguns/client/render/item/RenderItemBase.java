package techguns.client.render.item;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import techguns.Techguns;
import techguns.api.npc.INPCTechgunsShooter;
import techguns.client.models.ModelMultipart;
import techguns.client.particle.TGFXHandler;

public class RenderItemBase implements IItemRenderer {

	public static final float SCALE = 0.0625f;

	protected ModelMultipart model;
	protected ResourceLocation texture;

	protected float baseScale = 1.0f;

	protected float scale_thirdp = 0.35f;
	protected float scale_ground = 0.5f;
	protected float scale_ego = 0.5f;
	protected float scale_gui = 0.4f;
	protected float scale_itemframe = 0.5f;

	protected float[] translateBase = { 0f, 0f, 0f };

	protected float[][] translateType = { { 0f, 0f, 0f }, // TRANSLATE FIRST PERSON
			{ 0f, 0f, 0f }, // TRANSLATE THIRD PERSON
			{ 0f, 0f, 0f }, // TRANSLATE GUI
			{ 0f, 0f, 0f }, // TRANSLATE GROUND
			{ 0f, 0f, -0.05f } // TRANLATE FIXED (frame)
	};

	protected int parts = 1;

	protected String ambientParticleFX = null;

	public RenderItemBase(ModelMultipart model, ResourceLocation texture) {
		this.model = model;
		this.texture = texture;
	}

	/**
	 * Used by the bake verification (techguns.client.models.BakeVerifier)
	 */
	public ModelMultipart getModel() {
		return this.model;
	}

	public int getParts() {
		return this.parts;
	}

	public RenderItemBase setGUIScale(float guiscale) {
		this.scale_gui = guiscale;
		return this;
	}

	public RenderItemBase setFirstPersonScale(float scale) {
		this.scale_ego = scale;
		return this;
	}

	public RenderItemBase setGroundAndFrameScale(float scale) {
		this.scale_ground = scale;
		this.scale_itemframe = scale;
		return this;
	}

	/**
	 * Set the basic translation applied to ALL types
	 */
	public RenderItemBase setBaseTranslation(float x, float y, float z) {
		this.translateBase[0] = x;
		this.translateBase[1] = y;
		this.translateBase[2] = z;
		return this;
	}

	/**
	 * TRANSLATE FIRST PERSON x,y,z
	 * TRANSLATE THIRD PERSON x,y,z
	 * TRANSLATE GUI x,y,z
	 * TRANSLATEGROUND x,y,z
	 * TRANLATE FIXED (frame) x,y,z
	 *
	 * Left hand gets automatically mirrored
	 *
	 * @param translations - must be a float[5][3]
	 */
	public RenderItemBase setTransformTranslations(float[][] translations) {
		this.translateType = translations;
		return this;
	}

	public RenderItemBase setBaseScale(float baseScale) {
		this.baseScale = baseScale;
		return this;
	}

	public static boolean isFirstPerson(ItemDisplayContext transform) {
		return transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
	}

	public static boolean isThirdPerson(ItemDisplayContext transform) {
		return transform == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transform == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
	}

	public static boolean isLeftHand(ItemDisplayContext transform) {
		return transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transform == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
	}

	protected float getScaleFactorFromTransform(ItemDisplayContext transform) {
		switch (transform) {
		case FIRST_PERSON_LEFT_HAND:
		case FIRST_PERSON_RIGHT_HAND:
			return baseScale * scale_ego;

		case THIRD_PERSON_LEFT_HAND:
		case THIRD_PERSON_RIGHT_HAND:
			return baseScale * scale_thirdp;

		case GUI:
			return baseScale * scale_gui;

		case GROUND:
			return baseScale * scale_ground;

		case FIXED:
			return baseScale * scale_itemframe;

		default:
			return baseScale;
		}
	}

	protected void applyTranslation(PoseStack poseStack, ItemDisplayContext transform) {
		int index = -1;
		boolean flip = false;

		switch (transform) {
		case FIRST_PERSON_LEFT_HAND:
			flip = true; // fallthrough
		case FIRST_PERSON_RIGHT_HAND:
			index = 0;
			break;

		case THIRD_PERSON_LEFT_HAND:
			flip = true; // fallthrough
		case THIRD_PERSON_RIGHT_HAND:
			index = 1;
			break;
		case GUI:
			index = 2;
			break;
		case GROUND:
			index = 3;
			break;
		case FIXED:
			index = 4;
			break;
		default:
			break;
		}
		if (index >= 0) {
			float mirror = flip ? -1.0f : 1.0f;
			poseStack.translate((translateType[index][0]) * mirror, translateType[index][1], translateType[index][2]);
		}
	}

	/**
	 * center the model
	 */
	protected void applyBaseTranslation(PoseStack poseStack) {
		poseStack.translate(this.translateBase[0], this.translateBase[1], this.translateBase[2]);
	}

	/**
	 * Render type used for the model texture
	 */
	protected RenderType getRenderType(ResourceLocation tex) {
		return RenderType.entityCutoutNoCull(tex);
	}

	@Override
	public void renderItem(ItemDisplayContext transform, ItemStack stack, @Nullable LivingEntity elb, boolean leftHanded, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {

		poseStack.pushPose();

		this.applyTranslation(poseStack, transform);

		if (ItemDisplayContext.GUI == transform) {
			poseStack.mulPose(Axis.YP.rotationDegrees(40.0f));
			poseStack.mulPose(Axis.XP.rotationDegrees(20.0f));

		} else if (ItemDisplayContext.FIXED == transform) {
			poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
		}

		this.setBaseScale(poseStack, elb, transform);
		this.setBaseRotation(poseStack, transform);
		this.applyBaseTranslation(poseStack);

		VertexConsumer vc = buffers.getBuffer(this.getRenderType(texture));
		for (int i = 0; i < parts; i++) {
			model.render(poseStack, vc, packedLight, packedOverlay, elb, 0, 0, 0, 0, 0, SCALE, 0, 0, transform, i, 0, 0f);
		}
		this.renderItemParticles(poseStack, buffers, elb, transform, Techguns.proxy.getPartialTickTime());

		poseStack.popPose();
	}

	protected void setBaseScale(PoseStack poseStack, @Nullable LivingEntity entity, ItemDisplayContext transform) {
		float scale = getScaleFactorFromTransform(transform);

		if (entity != null && entity instanceof INPCTechgunsShooter) {
			INPCTechgunsShooter shooter = (INPCTechgunsShooter) entity;
			scale *= shooter.getGunScale();
		}

		poseStack.scale(scale, scale, scale);
	}

	protected void setBaseRotation(PoseStack poseStack, ItemDisplayContext transform) {
		poseStack.mulPose(Axis.XP.rotationDegrees(-180.0f));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
	}

	protected void renderItemParticles(PoseStack poseStack, MultiBufferSource buffers, @Nullable LivingEntity ent, ItemDisplayContext transform, float ptt) {
		if (ent == null) {
			return;
		}
		InteractionHand hand = InteractionHand.MAIN_HAND;
		if (transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transform == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
			if (ent.getMainArm() == HumanoidArm.RIGHT) {
				hand = InteractionHand.OFF_HAND;
			}
		} else if (transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transform == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
			if (ent.getMainArm() == HumanoidArm.LEFT) {
				hand = InteractionHand.OFF_HAND;
			}
		} else {
			return;
		}
		TGFXHandler.renderItemAttachedParticles(ent, hand, poseStack, buffers, ptt);
	}

	public String getAmbientParticleFX() {
		return ambientParticleFX;
	}

	public RenderItemBase setAmbientParticleFX(String ambientParticleFX) {
		this.ambientParticleFX = ambientParticleFX;
		return this;
	}

	/**
	 * Interpolated arm angles of a living entity (used by item attached particles)
	 */
	public static float interpolateRotation(float prev, float cur, float ptt) {
		return Mth.rotLerp(ptt, prev, cur);
	}
}
