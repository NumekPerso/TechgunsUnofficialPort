package techguns.client.render.entities.projectiles;

import java.util.Random;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.TGRenderHelper.RenderType;
import techguns.entities.projectiles.SonicShotgunProjectile;

public class RenderSonicShotgunProjectile extends RenderGenericProjectile<SonicShotgunProjectile> {

	private static final ResourceLocation bulletTexture = new ResourceLocation(Techguns.MODID,"textures/entity/bullet.png");
	private static final ResourceLocation waveTexture_A = new ResourceLocation(Techguns.MODID,"textures/fx/sonicwave4x4.png");
	private static final ResourceLocation waveTexture = new ResourceLocation(Techguns.MODID,"textures/fx/shockwave.png");

	public RenderSonicShotgunProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(SonicShotgunProjectile proj, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {

		if (proj.tickCount >= 3) {

			if (proj.isMainProjectile()) {
				Random rand = new Random(proj.getId());

				poseStack.pushPose();

				TGRenderHelper.rotate(poseStack, proj.getYRot() - 90, 0.0F, 1.0F, 0.0F);
				TGRenderHelper.rotate(poseStack, proj.getXRot(), 0.0F, 0.0F, 1.0F);

				double d = ((float) proj.tickCount + partialTicks) * 0.3;

				TGRenderHelper.rotate(poseStack, 90.0F, 1.0F, 0.0F, 0.0F);

				float prog = (float) Math.min(1.0, ((float) proj.tickCount + partialTicks) / 20.0f);

				// Render static waves

				float opacity = 1.0f - (float) (Math.pow(prog, 2.0));

				/*
				 * 1.12 set GlStateManager.color() per wave inside a single POSITION_TEX draw call, so the color
				 * of the last wave was used for all waves: same random sequence, last color applied to every wave.
				 */
				double[] sizes = new double[6];
				double[] offsets = new double[6];
				float r = 1f, g = 1f, b = 1f;
				for (int i = 0; i <= 5; i++) {
					r = opacity * rand.nextFloat();
					g = opacity * rand.nextFloat();
					b = opacity * (0.5f + (rand.nextFloat() * 0.5f));

					offsets[i] = 2.5 - (double) i;
					sizes[i] = d * (0.5 + (rand.nextDouble()));
				}

				VertexConsumer buf = buffers.getBuffer(TGRenderHelper.getRenderType(RenderType.ADDITIVE, waveTexture));
				Matrix4f m = poseStack.last().pose();
				for (int i = 0; i <= 5; i++) {
					drawWave(buf, m, sizes[i], offsets[i], r, g, b, opacity);
				}

				poseStack.popPose();

			}
		}
	}

	private void drawWave(VertexConsumer buf, Matrix4f m, double size, double offset, float r, float g, float b, float a) {
		vertex(buf, m, offset, -size, -size, r, g, b, a, 0, 0);
		vertex(buf, m, offset, -size, size, r, g, b, a, 0, 1);
		vertex(buf, m, offset, size, size, r, g, b, a, 1, 1);
		vertex(buf, m, offset, size, -size, r, g, b, a, 1, 0);
	}

}
