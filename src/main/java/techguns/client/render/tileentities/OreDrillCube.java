package techguns.client.render.tileentities;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Textured cube centered at the origin (1.12 OreDrillCube, tessellator quads with POSITION_TEX_NORMAL)
 */
public class OreDrillCube {
	protected ResourceLocation texture;

	public OreDrillCube(ResourceLocation texture) {
		this.texture = texture;
	}

	public void drawCube(float sizeX, float sizeY, float sizeZ, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
		VertexConsumer buf = buffers.getBuffer(RenderType.entityCutout(texture));
		Matrix4f m = poseStack.last().pose();
		Matrix3f n = poseStack.last().normal();

		// top
		v(buf, m, n, -sizeX, sizeY, -sizeZ, 0, 1, 0, 1, 0, light, overlay);
		v(buf, m, n, -sizeX, sizeY, sizeZ, 0, 0, 0, 1, 0, light, overlay);
		v(buf, m, n, sizeX, sizeY, sizeZ, 1, 0, 0, 1, 0, light, overlay);
		v(buf, m, n, sizeX, sizeY, -sizeZ, 1, 1, 0, 1, 0, light, overlay);
		// bottom
		v(buf, m, n, sizeX, -sizeY, -sizeZ, 1, 0, 0, -1, 0, light, overlay);
		v(buf, m, n, sizeX, -sizeY, sizeZ, 1, 1, 0, -1, 0, light, overlay);
		v(buf, m, n, -sizeX, -sizeY, sizeZ, 0, 1, 0, -1, 0, light, overlay);
		v(buf, m, n, -sizeX, -sizeY, -sizeZ, 0, 0, 0, -1, 0, light, overlay);
		// front
		v(buf, m, n, -sizeX, -sizeY, -sizeZ, 0, 1, 0, 0, -1, light, overlay);
		v(buf, m, n, -sizeX, sizeY, -sizeZ, 0, 0, 0, 0, -1, light, overlay);
		v(buf, m, n, sizeX, sizeY, -sizeZ, 1, 0, 0, 0, -1, light, overlay);
		v(buf, m, n, sizeX, -sizeY, -sizeZ, 1, 1, 0, 0, -1, light, overlay);
		// back
		v(buf, m, n, sizeX, -sizeY, sizeZ, 1, 0, 0, 0, 1, light, overlay);
		v(buf, m, n, sizeX, sizeY, sizeZ, 1, 1, 0, 0, 1, light, overlay);
		v(buf, m, n, -sizeX, sizeY, sizeZ, 0, 1, 0, 0, 1, light, overlay);
		v(buf, m, n, -sizeX, -sizeY, sizeZ, 0, 0, 0, 0, 1, light, overlay);
		// right
		v(buf, m, n, sizeX, -sizeY, -sizeZ, 0, 0, 1, 0, 0, light, overlay);
		v(buf, m, n, sizeX, sizeY, -sizeZ, 0, 1, 1, 0, 0, light, overlay);
		v(buf, m, n, sizeX, sizeY, sizeZ, 1, 1, 1, 0, 0, light, overlay);
		v(buf, m, n, sizeX, -sizeY, sizeZ, 1, 0, 1, 0, 0, light, overlay);
		// left
		v(buf, m, n, -sizeX, -sizeY, sizeZ, 1, 0, -1, 0, 0, light, overlay);
		v(buf, m, n, -sizeX, sizeY, sizeZ, 1, 1, -1, 0, 0, light, overlay);
		v(buf, m, n, -sizeX, sizeY, -sizeZ, 0, 1, -1, 0, 0, light, overlay);
		v(buf, m, n, -sizeX, -sizeY, -sizeZ, 0, 0, -1, 0, 0, light, overlay);
	}

	protected static void v(VertexConsumer buf, Matrix4f m, Matrix3f n, float x, float y, float z, float u, float v, float nx, float ny, float nz, int light, int overlay) {
		buf.vertex(m, x, y, z).color(1f, 1f, 1f, 1f).uv(u, v).overlayCoords(overlay).uv2(light).normal(n, nx, ny, nz).endVertex();
	}
}
