package techguns.client.models.additionalslots;

import java.util.Map;
import java.util.WeakHashMap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.entity.player.Player;
import techguns.Techguns;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.models.TGModelRenderer;
import techguns.util.MathUtil;

public class ModelJetPack extends ModelAdditionalSlotBase {

	// fields
	TGModelRenderer RocketTopR;
	TGModelRenderer TopR02;
	TGModelRenderer MidR02;
	TGModelRenderer TopL02;
	TGModelRenderer InnerR02;
	TGModelRenderer MidL02;
	TGModelRenderer InnerL02;
	TGModelRenderer BackBack;
	TGModelRenderer RocketR;
	TGModelRenderer RocketL;
	TGModelRenderer RocketTopL;

	private int variant;

	/**
	 * 1.12 spawned the flame particles every rendered frame, the port spawns them once per player tick
	 */
	private final Map<Player, Integer> lastFlameTick = new WeakHashMap<>();

	public ModelJetPack(int variant) {
		textureWidth = 32;
		textureHeight = 32;

		this.variant = variant;

		BackBack = new TGModelRenderer(this, 16, 0);
		BackBack.addBox(-2F, 0F, 0F, 4, 7, 4);
		BackBack.setRotationPoint(0F, 1F, 2F);
		BackBack.setTextureSize(32, 32);
		BackBack.mirror = true;
		setRotation(BackBack, 0F, 0F, 0F);

		RocketR = new TGModelRenderer(this, 0, 0);
		RocketR.addBox(-3F, 0F, 0F, 3, 9, 3);
		RocketR.setRotationPoint(-2F, -1F, 0.5F);
		RocketR.setTextureSize(32, 32);
		RocketR.mirror = true;
		setRotation(RocketR, 0F, 0F, 0F);
		this.BackBack.addChild(RocketR);

		RocketL = new TGModelRenderer(this, 0, 0);
		RocketL.addBox(-3F, 0F, 0F, 3, 9, 3);
		RocketL.setRotationPoint(5F, -1F, 0.5F);
		RocketL.setTextureSize(32, 32);
		RocketL.mirror = true;
		setRotation(RocketL, 0F, 0F, 0F);
		this.BackBack.addChild(RocketL);

		RocketTopR = new TGModelRenderer(this, 0, 13);
		RocketTopR.addBox(1F, 0F, -3.5F, 2, 2, 2);
		RocketTopR.setRotationPoint(-3.5F, -2F, 4F);
		RocketTopR.setTextureSize(32, 32);
		RocketTopR.mirror = true;
		setRotation(RocketTopR, 0F, 0F, 0F);
		this.RocketR.addChild(RocketTopR);

		RocketTopL = new TGModelRenderer(this, 0, 13);
		RocketTopL.addBox(-6F, 0F, -3.5F, 2, 2, 2);
		RocketTopL.setRotationPoint(3.5F, -2F, 4F);
		RocketTopL.setTextureSize(32, 32);
		RocketTopL.mirror = true;
		setRotation(RocketTopL, 0F, 0F, 0F);
		this.RocketL.addChild(RocketTopL);

		if (this.variant == 0) {
			InnerR02 = new TGModelRenderer(this, 0, 24);
			InnerR02.addBox(0F, -1F, -2F, 5, 6, 1);
			InnerR02.setRotationPoint(-10F, 2F, 3.5F);
			InnerR02.setTextureSize(32, 32);
			InnerR02.mirror = true;
			setRotation(InnerR02, 0F, 0F, 0F);
			this.BackBack.addChild(InnerR02);

			TopR02 = new TGModelRenderer(this, 13, 15);
			TopR02.addBox(10F, -3F, -5.5F, 5, 1, 2);
			TopR02.setRotationPoint(-10F, 1F, 3F);
			TopR02.setTextureSize(32, 32);
			TopR02.mirror = true;
			setRotation(TopR02, 0F, 0F, 0F);
			this.InnerR02.addChild(TopR02);

			MidR02 = new TGModelRenderer(this, 21, 19);
			MidR02.addBox(10F, -3F, -5.5F, 1, 6, 2);
			MidR02.setRotationPoint(-11F, 2F, 3F);
			MidR02.setTextureSize(32, 32);
			MidR02.mirror = true;
			setRotation(MidR02, 0F, 0F, 0F);
			this.InnerR02.addChild(MidR02);

			InnerL02 = new TGModelRenderer(this, 0, 24);
			InnerL02.addBox(0F, -1F, -2F, 5, 6, 1);
			InnerL02.setRotationPoint(5F, 2F, 3.5F);
			InnerL02.setTextureSize(32, 32);
			InnerL02.mirror = true;
			setRotation(InnerL02, 0F, 0F, 0F);
			this.BackBack.addChild(InnerL02);

			TopL02 = new TGModelRenderer(this, 13, 15);
			TopL02.addBox(-5F, -3F, -5.5F, 5, 1, 2);
			TopL02.setRotationPoint(5F, 1F, 3F);
			TopL02.setTextureSize(32, 32);
			TopL02.mirror = true;
			setRotation(TopL02, 0F, 0F, 0F);
			this.InnerL02.addChild(TopL02);

			MidL02 = new TGModelRenderer(this, 21, 19);
			MidL02.addBox(-5F, -3F, -5.5F, 1, 6, 2);
			MidL02.setRotationPoint(10F, 2F, 3F);
			MidL02.setTextureSize(32, 32);
			MidL02.mirror = true;
			setRotation(MidL02, 0F, 0F, 0F);
			this.InnerL02.addChild(MidL02);
		}

	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, Player entityPlayer) {
		poseStack.pushPose();
		translateToBody(poseStack, scale);
		BackBack.render(poseStack, buffer, packedLight, packedOverlay, scale);
		poseStack.popPose();

		if (entityPlayer != null) {
			boolean showFlames = false;

			TGExtendedPlayer props = TGExtendedPlayer.get(entityPlayer);

			if (props != null) {
				showFlames = props.isJumpkeyPressed() && entityPlayer.getDeltaMovement().y > -0.1f;
			}

			Integer last = lastFlameTick.get(entityPlayer);
			if (showFlames && (last == null || last != entityPlayer.tickCount)) {
				lastFlameTick.put(entityPlayer, entityPlayer.tickCount);

				float rot = -entityPlayer.yBodyRot;

				double dZ = -0.3;
				double dX = 0.2;

				if (entityPlayer.isShiftKeyDown()) {
					dZ = -0.45;
				}

				double offsetZ = dZ * MathUtil.cos360(rot) - dX * MathUtil.sin360(rot);
				double offsetX = dZ * MathUtil.sin360(rot) + dX * MathUtil.cos360(rot);

				double offsetZ2 = dZ * MathUtil.cos360(rot) - (dX * -1) * MathUtil.sin360(rot);
				double offsetX2 = dZ * MathUtil.sin360(rot) + (dX * -1) * MathUtil.cos360(rot);

				float motiony = -0.025f;
				if (entityPlayer.getDeltaMovement().y < motiony) {
					motiony = (float) entityPlayer.getDeltaMovement().y;
				}

				float offsetY = 1.5f;
				Techguns.proxy.createFX("FlamethrowerTrailFlames", entityPlayer.level(), entityPlayer.getX() + offsetX, entityPlayer.getY() + offsetY, entityPlayer.getZ() + offsetZ, 0, motiony, 0);
				Techguns.proxy.createFX("FlamethrowerTrailFlames", entityPlayer.level(), entityPlayer.getX() + offsetX2, entityPlayer.getY() + offsetY, entityPlayer.getZ() + offsetZ2, 0, motiony, 0);
			}
		}

	}

	@Override
	public void copyRotateAngles() {
		BackBack.rotateAngleX = this.bipedBody.rotateAngleX;
	}

}
