package techguns.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Wraps a vertex consumer and multiplies the vertex color, replaces 1.12 GlStateManager.color() before rendering a model
 */
public class TintedVertexConsumer implements VertexConsumer {

	protected final VertexConsumer parent;
	protected final float r, g, b, a;

	public TintedVertexConsumer(VertexConsumer parent, float r, float g, float b, float a) {
		this.parent = parent;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		parent.vertex(x, y, z);
		return this;
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		parent.color((int) (red * r), (int) (green * g), (int) (blue * b), (int) (alpha * a));
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
	public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
		parent.vertex(x, y, z, red * r, green * g, blue * b, alpha * a, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
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
