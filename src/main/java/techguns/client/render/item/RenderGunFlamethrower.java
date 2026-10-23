package techguns.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import techguns.client.models.ModelMultipart;
import techguns.client.render.fx.ScreenEffect;

public class RenderGunFlamethrower extends RenderGunBase90 {

	public RenderGunFlamethrower(ModelMultipart model, int parts) {
		super(model, parts);
	}

	/**
	 * 1.12 Entity.isOffsetPositionInLiquid: true if the offset bounding box is free of collisions and liquids
	 */
	protected static boolean isOffsetPositionInLiquid(LocalPlayer player, double x, double y, double z) {
		AABB aabb = player.getBoundingBox().move(x, y, z);
		return player.level().noCollision(player, aabb) && !player.level().containsAnyLiquid(aabb);
	}

	@Override
	protected void drawIdleFx(PoseStack poseStack, MultiBufferSource buffers, boolean leftHand) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && isOffsetPositionInLiquid(player, 0, 1.5, 0)) {
			float p = ((float) (player.level().getGameTime() % 30) / 30.0f);
			float x = leftHand ? this.muzzleFX_x_l : this.muzzleFX_x_r;
			ScreenEffect.FlamethrowerMuzzleFlame.doRender(poseStack, buffers, p, x, this.muzzleFX_y, this.muzzleFX_z, this.muzzleFX_scale * 0.5f, false);
		}
	}

	@Override
	protected void drawIdleFx3P(PoseStack poseStack, MultiBufferSource buffers, boolean leftHand) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && isOffsetPositionInLiquid(player, 0, 1.5, 0)) {
			float p = ((float) (player.level().getGameTime() % 30) / 30.0f);
			ScreenEffect.FlamethrowerMuzzleFlame.doRender(poseStack, buffers, p, 0, this.muzzleFX_3p_y, this.muzzleFX_3p_z, this.muzzleFX_scale * 0.5f * this.muzzleFX_3p_scale, true);
		}
	}

}
