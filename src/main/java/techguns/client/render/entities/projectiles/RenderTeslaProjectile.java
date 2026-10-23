package techguns.client.render.entities.projectiles;

import java.util.Random;

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
import techguns.entities.projectiles.TeslaProjectile;

public class RenderTeslaProjectile extends EntityRenderer<TeslaProjectile> {

	final static int SIN_COUNT = 5; // Number of overlapping sin functions
	final static double WIDTH = 0.5;
	final static double SIN_DISTANCE = 10.0; //ideal distance for one sinus curve;

	final static double OFFSET = 0.20; // Distance per bolt vertex

	public RenderTeslaProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
		this.texture = new ResourceLocation("techguns:textures/fx/laser_blue.png");
		this.renderType = RenderType.ADDITIVE;
		this.laserWidth = 3.0d;
	}

	private ResourceLocation texture;
	private double laserWidth;
	private RenderType renderType;

	/**
	 * Beams can be long, always render them (1.12 didn't frustum cull them either)
	 */
	@Override
	public boolean shouldRender(TeslaProjectile entity, Frustum frustum, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public void render(TeslaProjectile laser, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {

		double distance = laser.getDistance();
		int maxTicks = laser.getMaxTicks();

		float progress = ((float) laser.tickCount) / ((float) maxTicks);

		Random rand = new Random(laser.getSeed()); //Fixed seed for 1 bolt;

		double[] dY = new double[SIN_COUNT];
		double[] dZ = new double[SIN_COUNT];

		for (int i = 0; i < SIN_COUNT; i++) {
			dY[i] = 0.5 - rand.nextDouble(); //fixed for this bolt
			dZ[i] = 0.5 - rand.nextDouble();
		}

		// 1.12 stored the adjusted offset in a field of the (shared) renderer, the start value is used every frame here
		double offset = OFFSET;
		int count = (int) Math.round(distance / offset);
		if (count <= 0) {
			return;
		}
		offset = (distance / (double) count);

		float xOffset = 0.0f;

		int xreps = Math.max(1, (int) Math.round(distance / SIN_DISTANCE));
		double xprev = 0, yprev = 0, zprev = 0, widthprev = 1.0, alphaprev = 1.0;

		// 1.12: additive blending, no culling, no depth write (TGRenderTypes.fxAdditive)
		VertexConsumer vc = buffers.getBuffer(TGRenderHelper.getRenderType(this.renderType, this.getTextureLocation(laser)));

		poseStack.pushPose();

		TGRenderHelper.rotate(poseStack, laser.getLaserYaw() - 90F, 0.0F, 1.0F, 0.0F);
		TGRenderHelper.rotate(poseStack, laser.getLaserPitch(), 0.0F, 0.0F, 1.0F);

		for (int i = 0; i <= count; i++) {
			double d = (double) i / (double) count; //distance progress (0-1)

			double x = xOffset + (double) i * offset;
			double y = 0;
			double z = 0;

			if (i > 1) {
				for (int j = 1; j <= SIN_COUNT; j++) {
					double yfactor = ((rand.nextDouble() - 0.5) + (progress * dY[j - 1] * 1.0)) * (2.0 / (double) j);
					double zfactor = ((rand.nextDouble() - 0.5) + (progress * dZ[j - 1] * 1.0)) * (2.0 / (double) j);
					y += Math.sin((d * Math.PI) * (double) (j * xreps)) * yfactor;
					z += Math.sin((d * Math.PI) * (double) (j * xreps)) * zfactor;
				}
			}

			double pulse = 1.0 - Math.sqrt(Math.abs(progress - d) * 2.0);

			double width = Math.max(0.0, WIDTH * pulse);
			if (i >= 1) {
				drawSegment(poseStack, vc, xprev, yprev, zprev, x, y, z, widthprev, width, alphaprev, pulse);
			}
			widthprev = width;
			alphaprev = pulse;
			xprev = x;
			yprev = y;
			zprev = z;
		}

		poseStack.popPose();
	}

	void drawSegment(PoseStack poseStack, VertexConsumer vc, double x1, double y1, double z1, double x2, double y2, double z2, double width1, double width2, double a1, double a2) {

		double scale = 0.5;
		y1 *= scale;
		y2 *= scale;
		z1 *= scale;
		z2 *= scale;
		width1 *= scale;
		width2 *= scale;

		poseStack.pushPose();
		TGRenderHelper.rotate(poseStack, 45.0f, 1.0f, 0.0f, 0.0f);
		Matrix4f m = poseStack.last().pose();

		//set alpha1
		RenderGenericProjectile.vertex(vc, m, x1, y1 - width1, z1, 1.0f, 1.0f, 1.0f, (float) a1, 0, 0);
		RenderGenericProjectile.vertex(vc, m, x1, y1 + width1, z1, 1.0f, 1.0f, 1.0f, (float) a1, 0, 1);
		RenderGenericProjectile.vertex(vc, m, x2, y2 + width2, z2, 1.0f, 1.0f, 1.0f, (float) a1, 1, 1);
		RenderGenericProjectile.vertex(vc, m, x2, y2 - width2, z2, 1.0f, 1.0f, 1.0f, (float) a1, 1, 0);
		//--
		//set alpha2
		RenderGenericProjectile.vertex(vc, m, x1, y1, z1 - width1, 1.0f, 1.0f, 1.0f, (float) a1, 0, 0);
		RenderGenericProjectile.vertex(vc, m, x1, y1, z1 + width1, 1.0f, 1.0f, 1.0f, (float) a1, 0, 1);
		RenderGenericProjectile.vertex(vc, m, x2, y2, z2 + width2, 1.0f, 1.0f, 1.0f, (float) a1, 1, 1);
		RenderGenericProjectile.vertex(vc, m, x2, y2, z2 - width2, 1.0f, 1.0f, 1.0f, (float) a1, 1, 0);

		poseStack.popPose();

	}

	@Override
	public ResourceLocation getTextureLocation(TeslaProjectile entity) {
		return texture;
	}

}
