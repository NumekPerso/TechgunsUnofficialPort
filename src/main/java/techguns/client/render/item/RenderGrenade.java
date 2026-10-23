package techguns.client.render.item;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import techguns.api.guns.IChargeableItem;
import techguns.client.models.ModelMultipart;

public class RenderGrenade extends RenderItemBase {

	protected float baseRotY = 180.0f;

	public RenderGrenade(ModelMultipart model, ResourceLocation texture) {
		super(model, texture);
	}

	public RenderGrenade(ModelMultipart model, ResourceLocation texture, float baseRotationY) {
		super(model, texture);
		this.baseRotY = baseRotationY;
	}

	@Override
	protected void setBaseRotation(PoseStack poseStack, ItemDisplayContext transform) {
		poseStack.mulPose(Axis.XP.rotationDegrees(-180.0f));
		poseStack.mulPose(Axis.YP.rotationDegrees(baseRotY));
	}

	@Override
	public void renderItem(ItemDisplayContext transform, ItemStack stack, @Nullable LivingEntity elb, boolean leftHanded, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {

		float chargeProgress = 0;

		poseStack.pushPose();

		this.applyTranslation(poseStack, transform);

		if (isFirstPerson(transform)) {

			if (elb != null && !elb.getUseItem().isEmpty() && elb.getUseItem().getItem() == stack.getItem() && stack.getItem() instanceof IChargeableItem chargeable) {

				int dur = stack.getUseDuration() - elb.getUseItemRemainingTicks();

				chargeProgress = dur / chargeable.getFullChargeTime();

				chargeProgress = Mth.clamp(chargeProgress, 0f, 1f);

				poseStack.mulPose(Axis.XP.rotationDegrees(25.0f * chargeProgress));
			}

		} else if (ItemDisplayContext.GUI == transform) {
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
			model.render(poseStack, vc, packedLight, packedOverlay, elb, 0, 0, 0, 0, 0, SCALE, 0, 0, transform, i, chargeProgress, chargeProgress);
		}

		poseStack.popPose();
	}

}
