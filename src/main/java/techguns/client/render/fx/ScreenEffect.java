package techguns.client.render.fx;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.TGRenderHelper.RenderType;
import techguns.client.render.TGRenderTypes;

public class ScreenEffect implements IScreenEffect {

	protected ResourceLocation fxTexture;
	protected int cols;
	protected int rows;
	protected int numSprites;
	protected RenderType type;

	protected float colorR = 1.0f;
	protected float colorG = 1.0f;
	protected float colorB = 1.0f;
	protected float alpha = 1.0f;

	protected FadeType fadeType = FadeType.NONE;

	protected boolean flipY = false;
	protected boolean flipX = false;

	public ScreenEffect() {
	}

	public ScreenEffect(String fxTexture, int cols, int rows, int numSprites, RenderType type) {
		this.fxTexture = new ResourceLocation(Techguns.MODID, fxTexture.toLowerCase());
		this.cols = cols;
		this.rows = rows;
		this.numSprites = numSprites;
		this.type = type;
	}

	public IScreenEffect setColor(float R, float G, float B, float A) {
		this.colorR = R;
		this.colorG = G;
		this.colorB = B;
		this.alpha = A;
		return this;
	}

	public ScreenEffect setFade(FadeType type) {
		this.fadeType = type;
		return this;
	}

	public IScreenEffect setFlipAxis(boolean x, boolean y) {
		this.flipX = x;
		this.flipY = y;
		return this;
	}

	@Override
	public void doRender(PoseStack poseStack, MultiBufferSource buffers, float progress, float offsetX, float offsetY, float offsetZ, float scale, float rot_x, float rot_y, float rot_z, boolean is3p) {
		int currentFrame = (int) ((float) numSprites * progress);
		if (currentFrame < numSprites) {
			int col = currentFrame % cols;
			int row = (currentFrame / cols);

			float u = 1.f / cols;
			float v = 1.f / rows;

			float U1 = col * u;
			float V1 = row * v;
			float U2 = (col + 1) * u;
			float V2 = (row + 1) * v;

			poseStack.pushPose();
			if (rot_x != 0) poseStack.mulPose(Axis.XP.rotationDegrees(rot_x));
			if (rot_y != 0) poseStack.mulPose(Axis.YP.rotationDegrees(rot_y));
			if (rot_z != 0) poseStack.mulPose(Axis.ZP.rotationDegrees(rot_z));

			if (this.flipX) {
				float tmp = U1;
				U1 = U2;
				U2 = tmp;
			}
			if (this.flipY) {
				float tmp = V1;
				V1 = V2;
				V2 = tmp;
			}

			float f = scale / 2.0f;

			float alpha = 0.0f;
			switch (fadeType) {
			case FAST:
				double d = (1.0 - Math.cos(Math.sqrt(progress) * 2.0 * Math.PI)) * 0.5;
				alpha = (float) d * this.alpha;
				break;
			case SMOOTH:
				double d2 = Math.sin(Math.PI * progress);
				alpha = (float) (d2 * d2) * this.alpha;
				break;
			case NONE:
			default:
				alpha = this.alpha;
				break;
			}

			VertexConsumer buffer = buffers.getBuffer(TGRenderHelper.getRenderType(this.type, this.fxTexture));
			Matrix4f m = poseStack.last().pose();

			vertex(buffer, m, offsetX - f, offsetY + f, offsetZ, U1, V2, alpha);
			vertex(buffer, m, offsetX - f, offsetY - f, offsetZ, U1, V1, alpha);
			vertex(buffer, m, offsetX + f, offsetY - f, offsetZ, U2, V1, alpha);
			vertex(buffer, m, offsetX + f, offsetY + f, offsetZ, U2, V2, alpha);

			if (is3p) {
				vertex(buffer, m, offsetX - f, offsetY, offsetZ + f, U1, V2, alpha);
				vertex(buffer, m, offsetX - f, offsetY, offsetZ - f, U1, V1, alpha);
				vertex(buffer, m, offsetX + f, offsetY, offsetZ - f, U2, V1, alpha);
				vertex(buffer, m, offsetX + f, offsetY, offsetZ + f, U2, V2, alpha);

				vertex(buffer, m, offsetX, offsetY - f, offsetZ + f, U1, V2, alpha);
				vertex(buffer, m, offsetX, offsetY - f, offsetZ - f, U1, V1, alpha);
				vertex(buffer, m, offsetX, offsetY + f, offsetZ - f, U2, V1, alpha);
				vertex(buffer, m, offsetX, offsetY + f, offsetZ + f, U2, V2, alpha);
			}
			poseStack.popPose();
		}
	}

	protected void vertex(VertexConsumer buffer, Matrix4f m, float x, float y, float z, float u, float v, float alpha) {
		TGRenderTypes.fxVertex(buffer, m, x, y, z, colorR, colorG, colorB, alpha, u, v);
	}

	public enum FadeType {
		SMOOTH, FAST, NONE;
	}
}
