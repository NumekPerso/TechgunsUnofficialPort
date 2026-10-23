package techguns.client.render;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Render types replacing the 1.12 GL blend states of Techguns effects (muzzle flashes, particles, beams, projectiles).
 *
 * Shader compatibility (Iris / Oculus, OptiFine): shader packs replace the vanilla core shaders by their own gbuffers programs,
 * looked up by the vanilla shader getter, and bind the vertex attributes of the vanilla vertex format of that shader.
 * The fx types therefore only use vanilla (shader, vertex format) pairs that vanilla itself renders in the world:
 * <ul>
 * <li>additive fx: {@code rendertype_eyes} + NEW_ENTITY (like spider eyes). Unlit, no alpha test, fades to black in fog.
 * Iris: gbuffers_spidereyes, whose default blend is additive (SRC_ALPHA, ONE) like ours.</li>
 * <li>alpha fx: {@code rendertype_beacon_beam} + BLOCK (like translucent beacon beams). Unlit, no alpha test, fog.
 * Iris: gbuffers_beaconbeam (full bright), blend of the render type.</li>
 * </ul>
 * The old types used {@code position_color_tex}: Iris maps it to a program declared with POSITION_TEX_COLOR, so color
 * and UV attributes were swapped and the effects were invisible.
 *
 * Vertices of ALL fx types must be written with {@link #fxVertex} (or in the NEW_ENTITY order
 * vertex().color().uv().overlayCoords().uv2().normal().endVertex(): elements a format does not have are skipped).
 * They are unlit (full bright) like the 1.12 FX lighting.
 */
public class TGRenderTypes extends RenderType {

	private TGRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}

	/**
	 * Packed light written in fx vertices (ignored by the fx shaders, used by shader packs)
	 */
	public static final int FX_LIGHT = LightTexture.FULL_BRIGHT;

	/**
	 * Draw order of batched fx (see getFXDrawOrder)
	 */
	public static final int ORDER_SOLID = 0;
	public static final int ORDER_ALPHA = 1;
	public static final int ORDER_ADDITIVE = 2;
	public static final int ORDER_NO_DEPTH = 3;

	private static final Map<RenderType, Integer> DRAW_ORDER = new ConcurrentHashMap<>();

	private static RenderType order(int order, RenderType type) {
		DRAW_ORDER.put(type, order);
		return type;
	}

	/**
	 * Draw order of a render type when effects are batched per render type: depth writing types first, then alpha
	 * blended types (also the vanilla types), then additive types (order independent), then types without depth test
	 */
	public static int getFXDrawOrder(RenderType type) {
		Integer order = DRAW_ORDER.get(type);
		return order != null ? order : ORDER_ALPHA;
	}

	private static final Function<ResourceLocation, RenderType> FX_ADDITIVE = Util.memoize(tex -> order(ORDER_ADDITIVE, create("techguns_fx_additive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false,
			CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState(new TextureStateShard(tex, false, false))
					.setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false))));

	private static final Function<ResourceLocation, RenderType> FX_ADDITIVE_NO_DEPTH = Util.memoize(tex -> order(ORDER_NO_DEPTH, create("techguns_fx_additive_nodepth", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false,
			CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState(new TextureStateShard(tex, false, false))
					.setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).setDepthTestState(NO_DEPTH_TEST).createCompositeState(false))));

	private static final Function<ResourceLocation, RenderType> FX_ALPHA = Util.memoize(tex -> order(ORDER_ALPHA, create("techguns_fx_alpha", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true,
			CompositeState.builder().setShaderState(RENDERTYPE_BEACON_BEAM_SHADER).setTextureState(new TextureStateShard(tex, false, false))
					.setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false))));

	private static final Function<ResourceLocation, RenderType> FX_SOLID = Util.memoize(tex -> order(ORDER_SOLID, create("techguns_fx_solid", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true,
			CompositeState.builder().setShaderState(RENDERTYPE_BEACON_BEAM_SHADER).setTextureState(new TextureStateShard(tex, false, false))
					.setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false))));

	private static final Function<ResourceLocation, RenderType> ENTITY_ADDITIVE = Util.memoize(tex -> create("techguns_entity_additive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false,
			CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setTextureState(new TextureStateShard(tex, false, false))
					.setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).setOverlayState(OVERLAY).createCompositeState(false)));

	/**
	 * Additive blending (GL_SRC_ALPHA, GL_ONE) in the ENTITY format (models rendered with ModelPart, e.g. the
	 * overlay of the bio / laser death effects), no depth write, emissive
	 */
	public static RenderType entityAdditive(ResourceLocation texture) {
		return ENTITY_ADDITIVE.apply(texture);
	}

	/**
	 * Additive blending (GL_SRC_ALPHA, GL_ONE), no depth write: muzzle flashes, lasers, glowing particles
	 */
	public static RenderType fxAdditive(ResourceLocation texture) {
		return FX_ADDITIVE.apply(texture);
	}

	/**
	 * Additive blending without depth test (1.12 NO_Z_TEST)
	 */
	public static RenderType fxAdditiveNoDepth(ResourceLocation texture) {
		return FX_ADDITIVE_NO_DEPTH.apply(texture);
	}

	/**
	 * Alpha blending (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA), no depth write: smoke, scopes
	 */
	public static RenderType fxAlpha(ResourceLocation texture) {
		return FX_ALPHA.apply(texture);
	}

	/**
	 * Alpha blending with depth write
	 */
	public static RenderType fxSolid(ResourceLocation texture) {
		return FX_SOLID.apply(texture);
	}

	/**
	 * Writes one vertex of an fx render type (position transformed by the matrix), full bright
	 */
	public static void fxVertex(VertexConsumer vc, Matrix4f m, float x, float y, float z, float r, float g, float b, float a, float u, float v) {
		vc.vertex(m, x, y, z).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(FX_LIGHT).normal(0.0f, 1.0f, 0.0f).endVertex();
	}

	/**
	 * Writes one vertex of an fx render type, color as 0-255 ints
	 */
	public static void fxVertex(VertexConsumer vc, Matrix4f m, float x, float y, float z, int r, int g, int b, int a, float u, float v) {
		vc.vertex(m, x, y, z).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(FX_LIGHT).normal(0.0f, 1.0f, 0.0f).endVertex();
	}

	/**
	 * Writes one vertex of an fx render type, the position is already transformed (view space)
	 */
	public static void fxVertexRaw(VertexConsumer vc, float x, float y, float z, float r, float g, float b, float a, float u, float v) {
		vc.vertex(x, y, z).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(FX_LIGHT).normal(0.0f, 1.0f, 0.0f).endVertex();
	}
}
