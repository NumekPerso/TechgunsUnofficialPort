package techguns.client.render;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Keeps world effects attached to the first person gun (charge orbs, flames, beam origins, muzzle smoke) on the gun muzzle
 * with any FOV setting.
 *
 * The held item is always rendered with a fixed FOV of 70 while the world uses the FOV setting (and FOV effects). The 1.12
 * effect offsets match the gun only at FOV 70. In first person, vertices close to the camera are scaled sideways (view space)
 * by tan(worldFov/2)/tan(70/2), so they project where they would with FOV 70. The correction fades out with the distance, so
 * effects further away keep their real position.
 *
 * Only works with vertices written in view space, i.e. through a pose stack that contains the camera rotation (world particles
 * in RenderLevelStageEvent and entity renderers).
 */
public class FirstPersonFXCorrection {

	protected static final float TAN_HAND_FOV = (float) Math.tan(Math.toRadians(35.0));

	/**
	 * Distance (blocks) up to which the correction is fully applied
	 */
	protected static final float FULL_DIST = 1.5f;
	/**
	 * Distance (blocks) from which no correction is applied
	 */
	protected static final float NO_DIST = 5.0f;

	/**
	 * Extra distance (blocks) around an entity in which its vertices can be, for the wrapNear test
	 */
	protected static final float ENTITY_MARGIN = 2.0f;

	/**
	 * Correction factor of the world particles being rendered (1 = no correction). Set by TGFXHandler around the world
	 * particle rendering, applied per vertex by vertex(...) without wrapping the buffers.
	 */
	public static float particleK = 1.0f;

	/**
	 * Correction factor k (1 = no correction), must be called while the world projection matrix is set
	 */
	public static float computeK() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || !mc.options.getCameraType().isFirstPerson()) {
			return 1.0f;
		}
		float m11 = RenderSystem.getProjectionMatrix().m11();
		if (m11 == 0f) {
			return 1.0f;
		}
		float k = (1.0f / Math.abs(m11)) / TAN_HAND_FOV;
		if (Math.abs(k - 1.0f) < 0.01f) {
			return 1.0f;
		}
		return k;
	}

	/**
	 * Wraps the buffer source if a correction is needed, must be called while the world projection matrix is set
	 */
	public static MultiBufferSource wrap(MultiBufferSource source) {
		float k = computeK();
		if (k == 1.0f) {
			return source;
		}
		return renderType -> new CorrectedConsumer(source.getBuffer(renderType), k);
	}

	/**
	 * Wraps the buffer source only if the entity is close enough to the camera to be corrected (projectiles leaving the
	 * first person gun): entities further away than the correction distance are rendered without any wrapper
	 */
	public static MultiBufferSource wrapNear(MultiBufferSource source, Entity entity, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || !mc.options.getCameraType().isFirstPerson()) {
			return source;
		}
		Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
		double dx = Mth.lerp(partialTicks, entity.xo, entity.getX()) - cam.x;
		double dy = Mth.lerp(partialTicks, entity.yo, entity.getY()) - cam.y;
		double dz = Mth.lerp(partialTicks, entity.zo, entity.getZ()) - cam.z;
		double max = NO_DIST + ENTITY_MARGIN + entity.getBbWidth();
		if (dx * dx + dy * dy + dz * dz > max * max) {
			return source;
		}
		return wrap(source);
	}

	/**
	 * Starts a vertex with the correction k applied (k != 1): the position is transformed by the matrix into view space,
	 * scaled, and written untransformed. Continue with color(...) etc. like after vertex(matrix, x, y, z).
	 */
	public static VertexConsumer vertex(VertexConsumer vc, Matrix4f m, float x, float y, float z, float k) {
		float vx = m.m00() * x + m.m10() * y + m.m20() * z + m.m30();
		float vy = m.m01() * x + m.m11() * y + m.m21() * z + m.m31();
		float vz = m.m02() * x + m.m12() * y + m.m22() * z + m.m32();
		float s = getScale(k, vx, vy, vz);
		return vc.vertex(vx * s, vy * s, vz);
	}

	protected static float getScale(float k, double x, double y, double z) {
		double d = Math.sqrt(x * x + y * y + z * z);
		if (d <= FULL_DIST) {
			return k;
		} else if (d >= NO_DIST) {
			return 1.0f;
		}
		float t = 1.0f - (float) ((d - FULL_DIST) / (NO_DIST - FULL_DIST));
		t = t * t * (3.0f - 2.0f * t);
		return 1.0f + (k - 1.0f) * t;
	}

	protected static class CorrectedConsumer implements VertexConsumer {
		protected final VertexConsumer parent;
		protected final float k;

		public CorrectedConsumer(VertexConsumer parent, float k) {
			this.parent = parent;
			this.k = k;
		}

		@Override
		public VertexConsumer vertex(double x, double y, double z) {
			float s = getScale(k, x, y, z);
			parent.vertex(x * s, y * s, z);
			return this;
		}

		@Override
		public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
			float s = getScale(k, x, y, z);
			parent.vertex(x * s, y * s, z, red, green, blue, alpha, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			parent.color(red, green, blue, alpha);
			return this;
		}

		@Override
		public VertexConsumer uv(float u, float v) {
			parent.uv(u, v);
			return this;
		}

		@Override
		public VertexConsumer overlayCoords(int u, int v) {
			parent.overlayCoords(u, v);
			return this;
		}

		@Override
		public VertexConsumer uv2(int u, int v) {
			parent.uv2(u, v);
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			parent.normal(x, y, z);
			return this;
		}

		@Override
		public void endVertex() {
			parent.endVertex();
		}

		@Override
		public void defaultColor(int red, int green, int blue, int alpha) {
			parent.defaultColor(red, green, blue, alpha);
		}

		@Override
		public void unsetDefaultColor() {
			parent.unsetDefaultColor();
		}
	}
}
