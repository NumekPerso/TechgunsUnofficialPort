package techguns.client.models.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.models.ModelMultipart;
import techguns.client.render.TGRenderHelper;
import techguns.items.guns.GenericGun;

public class ModelGuidedMissileLauncher extends ModelMultipart {
	// fields
	public TGModelRenderer Barrel01;
	public TGModelRenderer Barrel02;
	public TGModelRenderer End01;
	public TGModelRenderer End02;
	public TGModelRenderer End03;
	public TGModelRenderer Optics02;
	public TGModelRenderer Optics03;
	public TGModelRenderer Optics04;
	public TGModelRenderer Optics05;
	public TGModelRenderer Optics06;
	public TGModelRenderer Optics07;
	public TGModelRenderer Holo2;
	public TGModelRenderer Optics01;
	public TGModelRenderer Holo2_1;
	public TGModelRenderer Front02;
	public TGModelRenderer Front03;
	public TGModelRenderer Front01;
	public TGModelRenderer Grip01;
	public TGModelRenderer Grip02;
	public TGModelRenderer Grip03;
	public TGModelRenderer Grip04;
	public TGModelRenderer Center01;
	public TGModelRenderer Center02;
	public TGModelRenderer Alpha01;
	public TGModelRenderer Alpha02;
	public TGModelRenderer Alpha03;
	public TGModelRenderer Alpha04;
	public TGModelRenderer Extra01;
	public TGModelRenderer Extra02;
	public TGModelRenderer Extra03;
	public TGModelRenderer Extra04;
	public TGModelRenderer Extra05;
	public TGModelRenderer Extra06;

	public ModelGuidedMissileLauncher() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.Optics07 = new TGModelRenderer(this, 68, 9);
		this.Optics07.setRotationPoint(23.0F, -2.0F, 4.0F);
		this.Optics07.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
		this.End02 = new TGModelRenderer(this, 94, 4);
		this.End02.setRotationPoint(-27.0F, -0.5F, -3.5F);
		this.End02.addBox(0.0F, 0.0F, 0.0F, 2, 6, 6, 0.0F);
		this.End03 = new TGModelRenderer(this, 107, 0);
		this.End03.setRotationPoint(-26.9F, -1.0F, -2.0F);
		this.End03.addBox(0.0F, 0.0F, 0.0F, 2, 7, 3, 0.0F);
		this.Optics03 = new TGModelRenderer(this, 35, 17);
		this.Optics03.setRotationPoint(17.0F, -1.0F, -1.5F);
		this.Optics03.addBox(0.0F, 0.0F, 0.0F, 8, 1, 2, 0.0F);
		this.Alpha01 = new TGModelRenderer(this, 0, 57);
		this.Alpha01.setRotationPoint(16.0F, -0.5F, 2.5F);
		this.Alpha01.addBox(0.0F, 0.0F, 0.0F, 10, 6, 0, 0.0F);
		this.Extra01 = new TGModelRenderer(this, 45, 57);
		this.Extra01.setRotationPoint(37.5F, -1.0F, -3.5F);
		this.Extra01.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.Center02 = new TGModelRenderer(this, 0, 38);
		this.Center02.setRotationPoint(26.0F, -0.5F, -3.5F);
		this.Center02.addBox(0.0F, 0.0F, 0.0F, 1, 6, 6, 0.0F);
		this.Front03 = new TGModelRenderer(this, 0, 0);
		this.Front03.setRotationPoint(26.9F, -0.5F, -2.0F);
		this.Front03.addBox(0.0F, 0.0F, 0.0F, 12, 6, 3, 0.0F);
		this.Center01 = new TGModelRenderer(this, 0, 38);
		this.Center01.setRotationPoint(15.0F, -0.5F, -3.5F);
		this.Center01.addBox(0.0F, 0.0F, 0.0F, 1, 6, 6, 0.0F);
		this.Optics05 = new TGModelRenderer(this, 61, 11);
		this.Optics05.setRotationPoint(23.0F, 3.5F, 3.0F);
		this.Optics05.addBox(0.0F, -2.0F, 0.0F, 1, 2, 3, 0.0F);
		this.setRotateAngle(Optics05, 0.5235987755982988F, 0.0F, 0.0F);
		this.Front02 = new TGModelRenderer(this, 0, 10);
		this.Front02.setRotationPoint(27.0F, 0.0F, -3.0F);
		this.Front02.addBox(0.0F, 0.0F, 0.0F, 12, 5, 5, 0.0F);
		this.End01 = new TGModelRenderer(this, 110, 5);
		this.End01.setRotationPoint(-26.9F, 1.0F, -4.0F);
		this.End01.addBox(0.0F, 0.0F, 0.0F, 2, 3, 7, 0.0F);
		this.Holo2 = new TGModelRenderer(this, 78, 0);
		this.Holo2.setRotationPoint(23.5F, -3.0F, 5.0F);
		this.Holo2.addBox(0.0F, 0.0F, 0.0F, 0, 8, 8, 0.0F);
		this.Optics01 = new TGModelRenderer(this, 32, 10);
		this.Optics01.setRotationPoint(18.0F, -3.0F, -0.5F);
		this.Optics01.addBox(0.0F, -1.0F, -1.0F, 7, 2, 2, 0.0F);
		this.setRotateAngle(Optics01, 0.7853981633974483F, 0.0F, 0.0F);
		this.Grip04 = new TGModelRenderer(this, 60, 44);
		this.Grip04.setRotationPoint(28.0F, 5.5F, -2.0F);
		this.Grip04.addBox(0.0F, 0.0F, 0.0F, 8, 5, 3, 0.0F);
		this.Alpha04 = new TGModelRenderer(this, 0, 57);
		this.Alpha04.setRotationPoint(16.0F, -0.5F, -3.5F);
		this.Alpha04.addBox(0.0F, 0.0F, 0.0F, 10, 6, 0, 0.0F);
		this.Extra06 = new TGModelRenderer(this, 45, 57);
		this.Extra06.setRotationPoint(27.5F, -1.0F, 1.5F);
		this.Extra06.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.Optics04 = new TGModelRenderer(this, 51, 10);
		this.Optics04.setRotationPoint(20.0F, 1.5F, 2.0F);
		this.Optics04.addBox(0.0F, 0.0F, 0.0F, 5, 2, 1, 0.0F);
		this.Grip01 = new TGModelRenderer(this, 38, 38);
		this.Grip01.setRotationPoint(16.0F, 5.5F, -1.5F);
		this.Grip01.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		this.setRotateAngle(Grip01, 0.0F, 0.0F, 0.3490658503988659F);
		this.Front01 = new TGModelRenderer(this, 31, 0);
		this.Front01.setRotationPoint(26.9F, 1.0F, -3.5F);
		this.Front01.addBox(0.0F, 0.0F, 0.0F, 12, 3, 6, 0.0F);
		this.Grip02 = new TGModelRenderer(this, 47, 38);
		this.Grip02.setRotationPoint(16.0F, 5.0F, -2.0F);
		this.Grip02.addBox(0.0F, 0.0F, 0.0F, 12, 2, 3, 0.0F);
		this.Extra03 = new TGModelRenderer(this, 45, 57);
		this.Extra03.setRotationPoint(27.5F, -1.0F, -3.5F);
		this.Extra03.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.Extra05 = new TGModelRenderer(this, 45, 61);
		this.Extra05.setRotationPoint(27.5F, -2.0F, 1.5F);
		this.Extra05.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1, 0.0F);
		this.Grip03 = new TGModelRenderer(this, 43, 44);
		this.Grip03.setRotationPoint(24.0F, 5.5F, -2.5F);
		this.Grip03.addBox(0.0F, 0.0F, 0.0F, 4, 5, 4, 0.0F);
		this.Alpha03 = new TGModelRenderer(this, -6, 51);
		this.Alpha03.setRotationPoint(16.0F, -0.5F, -3.5F);
		this.Alpha03.addBox(0.0F, 0.0F, 0.0F, 10, 0, 6, 0.0F);
		this.Extra02 = new TGModelRenderer(this, 45, 61);
		this.Extra02.setRotationPoint(27.5F, -2.0F, -3.5F);
		this.Extra02.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1, 0.0F);
		this.Barrel01 = new TGModelRenderer(this, 0, 21);
		this.Barrel01.setRotationPoint(-25.0F, 1.0F, -3.0F);
		this.Barrel01.addBox(0.0F, 0.0F, 0.0F, 51, 3, 5, 0.0F);
		this.Optics02 = new TGModelRenderer(this, 35, 14);
		this.Optics02.setRotationPoint(19.0F, -2.0F, -1.5F);
		this.Optics02.addBox(0.0F, 0.0F, 0.0F, 5, 1, 2, 0.0F);
		this.Holo2_1 = new TGModelRenderer(this, 78, -8);
		this.Holo2_1.setRotationPoint(23.5F, -3.0F, 5.0F);
		this.Holo2_1.addBox(0.0F, 0.0F, 0.0F, 0, 8, 8, 0.0F);
		this.Alpha02 = new TGModelRenderer(this, -6, 51);
		this.Alpha02.setRotationPoint(16.0F, 5.5F, -3.5F);
		this.Alpha02.addBox(0.0F, 0.0F, 0.0F, 10, 0, 6, 0.0F);
		this.Barrel02 = new TGModelRenderer(this, 0, 29);
		this.Barrel02.setRotationPoint(-25.0F, 0.0F, -2.0F);
		this.Barrel02.addBox(0.0F, 0.0F, 0.0F, 51, 5, 3, 0.0F);
		this.Optics06 = new TGModelRenderer(this, 67, 14);
		this.Optics06.setRotationPoint(23.0F, 1.0F, 5.0F);
		this.Optics06.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
		this.Extra04 = new TGModelRenderer(this, 45, 57);
		this.Extra04.setRotationPoint(37.5F, -1.0F, 1.5F);
		this.Extra04.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, int ammoLeft, float reloadProgress, ItemDisplayContext transformType, int part,
			float fireProgress, float chargeProgress) {
		final int normalLight = packedLight;
		this.Optics07.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.End02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.End03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Optics03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Alpha01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Extra01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Center02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Front03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Center01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Optics05.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Front02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.End01.render(poseStack, buffer, packedLight, packedOverlay, scale);

		this.Optics01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Grip04.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Alpha04.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Extra06.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Optics04.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Grip01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Front01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Grip02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Extra03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Extra05.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Grip03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Alpha03.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Extra02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Barrel01.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Optics02.render(poseStack, buffer, packedLight, packedOverlay, scale);

		this.Alpha02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Barrel02.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Optics06.render(poseStack, buffer, packedLight, packedOverlay, scale);
		this.Extra04.render(poseStack, buffer, packedLight, packedOverlay, scale);

		packedLight = TGRenderHelper.FULLBRIGHT;
		poseStack.pushPose();
		poseStack.translate(this.Holo2.offsetX, this.Holo2.offsetY, this.Holo2.offsetZ);
		poseStack.translate(this.Holo2.rotationPointX * scale, this.Holo2.rotationPointY * scale,
				this.Holo2.rotationPointZ * scale);
		poseStack.scale(0.5F, 0.5F, 0.5F);
		poseStack.translate(-this.Holo2.offsetX, -this.Holo2.offsetY, -this.Holo2.offsetZ);
		poseStack.translate(-this.Holo2.rotationPointX * scale, -this.Holo2.rotationPointY * scale,
				-this.Holo2.rotationPointZ * scale);
		boolean locked = false;
		if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ) {
			if (entityIn != null && entityIn instanceof Player) {
				Player player = (Player) entityIn;
				TGExtendedPlayer tge = TGExtendedPlayer.get(player);
				if (tge != null && player.getUseItem() != null && player.getUseItem().getItem() instanceof GenericGun) {
					GenericGun gun = (GenericGun)player.getUseItem().getItem();
					if (tge.lockOnTicks >= gun.getLockOnTicks()) {
						locked = true;
					}
				}
			}
		}
		if (locked) {
			this.Holo2_1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		}else {
			this.Holo2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		}
		poseStack.popPose();
		packedLight = normalLight;

	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(TGModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}
