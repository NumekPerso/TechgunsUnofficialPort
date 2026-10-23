package techguns.client.render.entities.projectiles;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.TGRenderHelper.RenderType;
import techguns.entities.projectiles.LaserProjectile;

public class RenderLaserProjectile extends EntityRenderer<LaserProjectile> {

	public RenderLaserProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
		this.texture = new ResourceLocation("techguns:textures/fx/laser3.png");
		this.textureStart = new ResourceLocation("techguns:textures/fx/laser3_start.png");
		this.renderType = RenderType.ADDITIVE;
		this.laserWidth = 3.0d;
	}

	private ResourceLocation texture;
	private ResourceLocation textureStart;
	private double laserWidth;
	private RenderType renderType;

	/**
	 * Beams can be long, always render them (1.12 didn't frustum cull them either)
	 */
	@Override
	public boolean shouldRender(LaserProjectile entity, Frustum frustum, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public void render(LaserProjectile laser, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {

		double distance = laser.getDistance();
		int maxTicks = laser.getMaxTicks();

		float prog = ((float) laser.tickCount) / ((float) maxTicks);
		double width = laserWidth * (Math.sin(Math.sqrt(prog) * Math.PI)) * 2;

		double distance_start = Math.min(1.0d, distance);

		double u = (distance / laserWidth) * 2.0D;

		// 1.12: additive blending, no culling, no depth write (TGRenderTypes.fxAdditive)
		poseStack.pushPose();

		TGRenderHelper.rotate(poseStack, laser.getLaserYaw() - 90F, 0.0F, 1.0F, 0.0F);
		TGRenderHelper.rotate(poseStack, laser.getLaserPitch(), 0.0F, 0.0F, 1.0F);

		float f10 = 0.0125F;

		distance *= 80.0D;
		distance_start *= 80.0D;

		float rot_x = 45f + (prog * 180f);

		TGRenderHelper.rotate(poseStack, rot_x + 90f, 1.0F, 0.0F, 0.0F);
		poseStack.scale(f10, f10, f10);

		float brightness = (float) Math.sin(Math.sqrt(prog) * Math.PI);

		if (distance > distance_start) { //Beam Segment
			VertexConsumer vc = buffers.getBuffer(TGRenderHelper.getRenderType(this.renderType, this.getTextureLocation(laser)));
			poseStack.pushPose();
			for (int i = 0; i < 2; ++i) {
				TGRenderHelper.rotate(poseStack, 90.0F, 1.0F, 0.0F, 0.0F);
				Matrix4f m = poseStack.last().pose();

				RenderGenericProjectile.vertex(vc, m, distance, -width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, u + prog, 0);
				RenderGenericProjectile.vertex(vc, m, distance_start, -width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, prog, 0);
				RenderGenericProjectile.vertex(vc, m, distance_start, width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, prog, 1);
				RenderGenericProjectile.vertex(vc, m, distance, width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, u + prog, 1);
			}
			poseStack.popPose();
		}
		VertexConsumer vc = buffers.getBuffer(TGRenderHelper.getRenderType(this.renderType, textureStart));
		poseStack.pushPose();
		for (int i = 0; i < 2; ++i) //Beam start segment
		{
			TGRenderHelper.rotate(poseStack, 90.0F, 1.0F, 0.0F, 0.0F);
			Matrix4f m = poseStack.last().pose();

			RenderGenericProjectile.vertex(vc, m, distance_start, -width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, 1, 0);
			RenderGenericProjectile.vertex(vc, m, 0, -width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, 0, 0);
			RenderGenericProjectile.vertex(vc, m, 0, width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, 0, 1);
			RenderGenericProjectile.vertex(vc, m, distance_start, width, 0.0D, 1.0f, 1.0f, 1.0f, brightness, 1, 1);
		}
		poseStack.popPose();

		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(LaserProjectile entity) {
		return texture;
	}

}
