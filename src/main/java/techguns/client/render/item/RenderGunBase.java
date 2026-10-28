package techguns.client.render.item;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import techguns.Techguns;
import techguns.api.capabilities.AttackTime;
import techguns.api.capabilities.ITGShooterValues;
import techguns.api.npc.INPCTechgunsShooter;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.models.ModelMultipart;
import techguns.client.render.TintedVertexConsumer;
import techguns.client.render.fx.IScreenEffect;
import techguns.items.guns.GenericGun;

public class RenderGunBase extends RenderItemBase {

	protected IScreenEffect muzzleFX = null;

	protected IScreenEffect scope = null;

	protected float muzzleFX_x_l = 0f;
	protected float muzzleFX_x_r = 0f;
	protected float muzzleFX_y = 0f;
	protected float muzzleFX_z = 0f;
	protected float muzzleFX_scale = 1.0f;

	protected float muzzleFX_3p_y = 0f;
	protected float muzzleFX_3p_z = 0f;
	protected float muzzleFX_3p_scale = 0.5f;

	protected float mf_jitterX = 0f;
	protected float mf_jitterY = 0f;
	protected float mf_jitterAngle = 0f;
	protected float mf_jitterScale = 0f;

	protected GunAnimation recoilAnim = GunAnimation.genericRecoil;
	protected float[] recoilParams = new float[] { 0.15f, 5.0f };
	protected GunAnimation reloadAnim = GunAnimation.genericReload;
	protected float[] reloadParams = new float[] { 1.0f, 40.0f };

	protected GunAnimation scopeRecoilAnim = null;
	protected float[] scopeRecoilParams = null;

	protected GunAnimation recoilAnim3p = GunAnimation.genericRecoil;
	protected float[] recoilParams3p = new float[] { 0f, 5.0f };
	protected GunAnimation reloadAnim3p = GunAnimation.genericReload;
	protected float[] reloadParams3p = new float[] { 0f, 40.0f };

	protected float scopescale = 3.0f;

	protected float chargeTranslation = 0.25f;

	public RenderGunBase(ModelMultipart model, int parts) {
		super(model, null);
		this.parts = parts;
		this.scale_ground = this.scale_thirdp;
	}

	/**
	 * Set the basic translation applied to ALL types
	 */
	@Override
	public RenderGunBase setBaseTranslation(float x, float y, float z) {
		return (RenderGunBase) super.setBaseTranslation(x, y, z);
	}

	@Override
	public RenderGunBase setGUIScale(float guiscale) {
		return (RenderGunBase) super.setGUIScale(guiscale);
	}

	@Override
	public RenderGunBase setFirstPersonScale(float scale) {
		return (RenderGunBase) super.setFirstPersonScale(scale);
	}

	@Override
	public RenderGunBase setGroundAndFrameScale(float scale) {
		return (RenderGunBase) super.setGroundAndFrameScale(scale);
	}

	public RenderGunBase setReloadAnim(GunAnimation anim, float... params) {
		this.reloadAnim = anim;
		this.reloadParams = params;
		return this;
	}

	public RenderGunBase setRecoilAnim(GunAnimation anim, float... params) {
		this.recoilAnim = anim;
		this.recoilParams = params;
		return this;
	}

	public RenderGunBase setScopeRecoilAnim(GunAnimation anim, float... params) {
		this.scopeRecoilAnim = anim;
		this.scopeRecoilParams = params;
		return this;
	}

	public RenderGunBase setReloadAnim3p(GunAnimation anim, float... params) {
		this.reloadAnim3p = anim;
		this.reloadParams3p = params;
		return this;
	}

	public RenderGunBase setRecoilAnim3p(GunAnimation anim, float... params) {
		this.recoilAnim3p = anim;
		this.recoilParams3p = params;
		return this;
	}

	public RenderGunBase setChargeTranslationAmount(float value) {
		this.chargeTranslation = value;
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
	@Override
	public RenderGunBase setTransformTranslations(float[][] translations) {
		return (RenderGunBase) super.setTransformTranslations(translations);
	}

	public RenderGunBase setMuzzleFx(IScreenEffect muzzleFX, float x, float y, float z, float scale, float x_l) {
		this.muzzleFX = muzzleFX;
		this.muzzleFX_x_r = x;
		this.muzzleFX_x_l = x_l;
		this.muzzleFX_y = y;
		this.muzzleFX_z = z;
		this.muzzleFX_scale = scale;
		return this;
	}

	public RenderGunBase setMuzzleFXPos3P(float y, float z) {
		this.muzzleFX_3p_y = y;
		this.muzzleFX_3p_z = z;
		return this;
	}

	public RenderGunBase setScope(IScreenEffect scope) {
		this.scope = scope;
		return this;
	}

	public RenderGunBase setScope(IScreenEffect scope, float scale) {
		this.scope = scope;
		this.scopescale = scale;
		return this;
	}

	public boolean hasScopeTexture() {
		return this.scope != null;
	}

	protected static ITGShooterValues getShooterValues(LivingEntity ent) {
		ITGShooterValues values = null;
		if (ent instanceof Player) {
			values = TGExtendedPlayer.get((Player) ent);
		}
		return values;
	}

	@Override
	public void renderItem(ItemDisplayContext transform, ItemStack stack, @Nullable LivingEntity entityIn, boolean leftHand, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		GenericGun gun = ((GenericGun) stack.getItem());
		ITGShooterValues values = getShooterValues(entityIn);

		boolean sneaking = false;
		boolean isOffhand = false;
		if (entityIn != null) {
			sneaking = entityIn.isCrouching();
			isOffhand = ((!leftHand) && entityIn.getMainArm() == HumanoidArm.LEFT) || ((leftHand) && entityIn.getMainArm() == HumanoidArm.RIGHT);
		}

		poseStack.pushPose();

		float fireProgress = 0.0f;
		float reloadProgress = 0.0f;
		float muzzleFlashProgress = 0.0f;
		float chargeProgress = 0.0f;

		byte attackType = 0;

		boolean renderScope = false;

		if (values != null && (isFirstPerson(transform) || isThirdPerson(transform))) {
			AttackTime attack = values.getAttackTime(isOffhand);
			attackType = attack.getAttackType();

			if (gun.canCharge() && !isOffhand && !entityIn.getUseItem().isEmpty()) {

				int dur = stack.getUseDuration() - entityIn.getUseItemRemainingTicks();

				chargeProgress = dur / gun.getFullChargeTime();

				if (chargeProgress < 0.0f) {
					chargeProgress = 0.0f;
				} else if (chargeProgress > 1.0f) {
					chargeProgress = 1.0f;
				}

			} else if (attack.isReloading()) {
				long diff = attack.getReloadTime() - System.currentTimeMillis();

				if (diff <= 0) {
					attack.setReloadTime(0);
					attack.setReloadTimeTotal(0);
					attack.setAttackType((byte) 0);
				} else {
					reloadProgress = 1.0f - ((float) diff / (float) attack.getReloadTimeTotal());
				}
			} else if (attack.isRecoiling()) {

				long diff = attack.getRecoilTime() - System.currentTimeMillis();

				if (diff <= 0) {
					attack.setRecoilTime(0);
					attack.setRecoilTimeTotal(0);
					attack.setAttackType((byte) 0);
					attack.setRecoilChargeProgress(0f);
				} else {
					fireProgress = 1.0f - ((float) diff / (float) attack.getRecoilTimeTotal());

					if (gun.canCharge()) {
						chargeProgress = (1.0f - fireProgress) * attack.getRecoilChargeProgress();
					}
				}
			}

			//Calculate muzzleFlash progress
			if (attack.getMuzzleFlashTime() > 0) {
				long diff = attack.getMuzzleFlashTime() - System.currentTimeMillis();
				if (diff <= 0 || diff > attack.getMuzzleFlashTimeTotal()) {
					attack.setMuzzleFlashTime(0L);
					attack.setMuzzleFlashTimeTotal(0);
				} else {
					muzzleFlashProgress = 1.0f - ((float) diff / (float) attack.getMuzzleFlashTimeTotal());
				}
			}
		}

		this.applyTranslation(poseStack, transform);

		if (isFirstPerson(transform)) {

			if (!isOffhand && gun.isZooming() && this.scope != null) {
				renderScope = true;
			} else {
				this.transformFirstPerson(poseStack, fireProgress, reloadProgress, chargeProgress, ItemDisplayContext.FIRST_PERSON_LEFT_HAND == transform, sneaking && isOffhand);
			}

		} else if (isThirdPerson(transform)) {
			this.transformThirdPerson(poseStack, entityIn, fireProgress, reloadProgress, ItemDisplayContext.THIRD_PERSON_LEFT_HAND == transform);

		} else if (ItemDisplayContext.GUI == transform) {
			this.transformGUI(poseStack);

		} else if (ItemDisplayContext.GROUND == transform) {
			this.transformGround(poseStack);

		} else if (ItemDisplayContext.FIXED == transform) {
			this.transformFixed(poseStack);
		}

		if (!renderScope) {
			this.setBaseScale(poseStack, entityIn, transform);
			this.setBaseRotation(poseStack, transform);
			this.applyBaseTranslation(poseStack);

			ResourceLocation tex = null;
			for (int i = 0; i < parts; i++) {
				ResourceLocation partTex = this.getTextureForPart(gun, i, stack);
				if (partTex != null) {
					tex = partTex;
				}
				if (tex == null) {
					continue;
				}
				VertexConsumer vc = buffers.getBuffer(this.getRenderTypeForPart(tex, i));
				float[] color = this.getColorForPart(gun, i, stack);
				if (color != null) {
					vc = new TintedVertexConsumer(vc, color[0], color[1], color[2], color[3]);
				}
				model.render(poseStack, vc, packedLight, packedOverlay, entityIn, 0, 0, 0, 0, 0, SCALE, gun.getAmmoLeft(stack), reloadProgress, transform, i, fireProgress, chargeProgress);
			}
			poseStack.popPose();

			//like 1.12: particles only get the reload animation, the muzzle flash is drawn in the untransformed hand space
			poseStack.pushPose();
			this.applyAnimForParticles(poseStack, entityIn, reloadProgress, transform, sneaking, isOffhand);
			this.renderItemParticles(poseStack, buffers, entityIn, transform, Techguns.proxy.getPartialTickTime());
			poseStack.popPose();

			//Draw muzzle FX
			if (muzzleFlashProgress > 0) {
				if (isFirstPerson(transform)) {
					this.drawMuzzleFx(poseStack, buffers, muzzleFlashProgress, attackType, leftHand);
				} else {
					this.drawMuzzleFx3P(poseStack, buffers, muzzleFlashProgress, attackType, leftHand);
				}
			} else if (reloadProgress <= 0) {
				if (isFirstPerson(transform)) {
					this.drawIdleFx(poseStack, buffers, leftHand);
				} else if (isThirdPerson(transform)) {
					this.drawIdleFx3P(poseStack, buffers, leftHand);
				}
			}

		} else {
			poseStack.popPose();
			poseStack.pushPose();
			if (this.scopeRecoilAnim != null && fireProgress > 0f) {
				this.scopeRecoilAnim.play(poseStack, fireProgress, ItemDisplayContext.FIRST_PERSON_LEFT_HAND == transform, this.scopeRecoilParams);
			}
			this.renderScope(poseStack, buffers, fireProgress, leftHand);
			poseStack.popPose();
		}
	}

	protected void drawIdleFx(PoseStack poseStack, MultiBufferSource buffers, boolean leftHand) {
	}

	protected void drawIdleFx3P(PoseStack poseStack, MultiBufferSource buffers, boolean leftHand) {
	}

	/**
	 * Replaces 1.12 setGLColorForPart, return null for no tint
	 */
	protected float[] getColorForPart(GenericGun gun, int part, ItemStack stack) {
		return null;
	}

	public void applyAnimForParticles(PoseStack poseStack, LivingEntity entity, float reloadProgress, ItemDisplayContext transform, boolean sneaking, boolean isOffhand) {
		if (reloadProgress == 0) return;
		if (isFirstPerson(transform)) {
			this.transformFirstPerson(poseStack, 0f, reloadProgress, 0f, ItemDisplayContext.FIRST_PERSON_LEFT_HAND == transform, sneaking && isOffhand);
		} else if (isThirdPerson(transform)) {
			this.transformThirdPerson(poseStack, entity, 0f, reloadProgress, ItemDisplayContext.THIRD_PERSON_LEFT_HAND == transform);
		}
	}

	protected void transformFirstPerson(PoseStack poseStack, float fireProgress, float reloadProgress, float chargeProgress, boolean left, boolean shoudLowerWeapon) {
		if (chargeProgress > 0) {
			poseStack.translate(0, 0, this.chargeTranslation * chargeProgress);
		}
		if (fireProgress > 0) {
			this.recoilAnim.play(poseStack, fireProgress, left, this.recoilParams);
		} else if (reloadProgress > 0) {
			this.reloadAnim.play(poseStack, reloadProgress, left, this.reloadParams);
		} else if (shoudLowerWeapon) {
			poseStack.mulPose(Axis.XP.rotationDegrees(-35f));
		}
	}

	protected void transformThirdPerson(PoseStack poseStack, LivingEntity ent, float fireProgress, float reloadProgress, boolean left) {
		if (ent != null && ent instanceof INPCTechgunsShooter) {
			INPCTechgunsShooter shooter = (INPCTechgunsShooter) ent;
			if (!shooter.hasWeaponArmPose()) {
				poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
			}
		}

		if (fireProgress > 0) {
			this.recoilAnim3p.play(poseStack, fireProgress, left, this.recoilParams3p);
		} else if (reloadProgress > 0) {
			this.reloadAnim3p.play(poseStack, reloadProgress, left, this.reloadParams3p);
		}
	}

	protected void transformGUI(PoseStack poseStack) {
		poseStack.mulPose(Axis.YP.rotationDegrees(40.0f));
		poseStack.mulPose(Axis.XP.rotationDegrees(20.0f));
	}

	protected void transformGround(PoseStack poseStack) {
	}

	protected void transformFixed(PoseStack poseStack) {
		poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
	}

	protected void drawMuzzleFx(PoseStack poseStack, MultiBufferSource buffers, float progress, byte attackType, boolean leftHand) {
		if (this.muzzleFX != null) {
			float x = leftHand ? this.muzzleFX_x_l : this.muzzleFX_x_r;

			//Muzzle flash jitter
			float[] jitter = Techguns.proxy instanceof techguns.client.ClientProxy cp ? cp.getMuzzleFlashJitter() : new float[4];
			float scale = this.muzzleFX_scale;
			float offsetX = x;
			float offsetY = this.muzzleFX_y;
			if (this.mf_jitterScale > 0.0f) scale += mf_jitterScale * jitter[2];
			if (this.mf_jitterX > 0.0f) offsetX += mf_jitterX * jitter[0];
			if (this.mf_jitterY > 0.0f) offsetY += mf_jitterY * jitter[1];

			this.muzzleFX.doRender(poseStack, buffers, progress, offsetX, offsetY, this.muzzleFX_z, scale, false);
		}
	}

	protected void drawMuzzleFx3P(PoseStack poseStack, MultiBufferSource buffers, float progress, byte attackType, boolean leftHand) {
		if (this.muzzleFX != null) {
			this.muzzleFX.doRender(poseStack, buffers, progress, 0, this.muzzleFX_3p_y, this.muzzleFX_3p_z, this.muzzleFX_scale * this.muzzleFX_3p_scale, true);
		}
	}

	protected void renderScope(PoseStack poseStack, MultiBufferSource buffers, float fireProgress, boolean leftHand) {
		if (this.scope != null) {
			float x = leftHand ? 0.56f : -0.56f;
			this.scope.doRender(poseStack, buffers, fireProgress, x, 0.52f, 0f, scopescale, false);
		}
	}

	@Override
	public RenderGunBase setBaseScale(float baseScale) {
		return (RenderGunBase) super.setBaseScale(baseScale);
	}

	public RenderGunBase setMuzzleFlashJitter(float jX, float jY, float jAngle, float jScale) {
		this.mf_jitterX = jX;
		this.mf_jitterY = jY;
		this.mf_jitterAngle = jAngle;
		this.mf_jitterScale = jScale;
		return this;
	}

	/**
	 * Replaces 1.12 bindTextureForPart: return the texture used for this part, null keeps the texture of the previous part
	 */
	public ResourceLocation getTextureForPart(GenericGun gun, int part, ItemStack stack) {
		if (part == 0) {
			if (gun.hasCustomTexture) {
				return gun.getCurrentTexture(stack);
			} else {
				return TextureAtlas.LOCATION_BLOCKS;
			}
		}
		return null;
	}

	@Override
	public RenderGunBase setAmbientParticleFX(String ambientParticleFX) {
		this.ambientParticleFX = ambientParticleFX;
		return this;
	}

	/**
	 * Models with zero thickness planes (both faces in the same plane) need back face culling, without it the two faces
	 * flicker (z-fighting)
	 */
	protected boolean cullFaces = false;

	public RenderGunBase setCullFaces() {
		this.cullFaces = true;
		return this;
	}

	@Override
	protected RenderType getRenderType(ResourceLocation tex) {
		return this.cullFaces ? RenderType.entityCutout(tex) : RenderType.entityCutoutNoCull(tex);
	}

	/**
	 * Render type of one part of the model (1.12 models could switch GL blend states in the middle of render()), defaults to getRenderType
	 */
	protected RenderType getRenderTypeForPart(ResourceLocation tex, int part) {
		return this.getRenderType(tex);
	}
}
