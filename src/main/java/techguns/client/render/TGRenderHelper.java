package techguns.client.render;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;

public class TGRenderHelper {

	/**
	 * Packed light value for full brightness, replaces 1.12 enableFXLighting()
	 */
	public static final int FULLBRIGHT = LightTexture.FULL_BRIGHT;

	/**
	 * Blend modes of 1.12 TGRenderHelper.enableBlendMode
	 */
	public enum RenderType {
		ALPHA, ADDITIVE, SOLID, ALPHA_SHADED, NO_Z_TEST;
	}

	/**
	 * Replacement for GlStateManager.rotate(angle, x, y, z): angle in degrees around a (not necessarily normalized) axis
	 */
	public static void rotate(PoseStack poseStack, float angle, float x, float y, float z) {
		if (angle == 0.0f) {
			return;
		}
		Vector3f axis = new Vector3f(x, y, z);
		float len = axis.length();
		if (len == 0.0f) {
			return;
		}
		axis.div(len);
		poseStack.mulPose(new Quaternionf().rotationAxis((float) Math.toRadians(angle), axis.x, axis.y, axis.z));
	}

	/**
	 * Vanilla render type for a blend mode of a 1.12 effect
	 */
	public static net.minecraft.client.renderer.RenderType getRenderType(RenderType type, ResourceLocation texture) {
		switch (type) {
		case ADDITIVE:
			return TGRenderTypes.fxAdditive(texture);
		case NO_Z_TEST:
			return TGRenderTypes.fxAdditiveNoDepth(texture);
		case ALPHA_SHADED:
			return net.minecraft.client.renderer.RenderType.entityTranslucent(texture);
		case SOLID:
			return TGRenderTypes.fxSolid(texture);
		case ALPHA:
		default:
			return TGRenderTypes.fxAlpha(texture);
		}
	}

	/**
	 * Whether the render type of this blend mode is an fx type of TGRenderTypes (true, write vertices with
	 * TGRenderTypes.fxVertex) or the vanilla entity translucent type (false)
	 */
	public static boolean isFXFormat(RenderType type) {
		return type != RenderType.ALPHA_SHADED;
	}
}
