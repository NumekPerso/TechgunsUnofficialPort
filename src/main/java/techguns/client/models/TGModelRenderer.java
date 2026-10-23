package techguns.client.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

/**
 * Emulation of the 1.12 ModelRenderer, so the 1.12 Techguns models (exported from Techne / Tabula) keep their exact
 * geometry and UV mapping. Boxes are built with the 1.20 ModelPart.Cube, which uses the same box/UV algorithm as the
 * 1.12 ModelBox.
 *
 * Differences to 1.12:
 * - render() takes the PoseStack / VertexConsumer / light / overlay (no GL state)
 * - the "scale" parameter is still supported (1.12 always passed 0.0625 = 1/16)
 *
 * Like in 1.12, the texture size used for a box is the one set at the time addBox is called (setTextureSize only
 * affects boxes added afterwards).
 */
public class TGModelRenderer {

	public float textureWidth;
	public float textureHeight;
	private int textureOffsetX;
	private int textureOffsetY;

	public float rotationPointX;
	public float rotationPointY;
	public float rotationPointZ;
	public float rotateAngleX;
	public float rotateAngleY;
	public float rotateAngleZ;

	public boolean mirror;
	public boolean showModel = true;
	/**
	 * Hides the model.
	 */
	public boolean isHidden;

	public final List<ModelPart.Cube> cubeList = new ArrayList<>();
	public List<TGModelRenderer> childModels;

	public final String boxName;

	public float offsetX;
	public float offsetY;
	public float offsetZ;

	public TGModelRenderer(TGModelBase model, String boxNameIn) {
		this.textureWidth = 64.0F;
		this.textureHeight = 32.0F;
		this.showModel = true;
		this.boxName = boxNameIn;
		if (model != null) {
			model.boxList.add(this);
			this.setTextureSize(model.textureWidth, model.textureHeight);
		}
	}

	public TGModelRenderer(TGModelBase model) {
		this(model, (String) null);
	}

	public TGModelRenderer(TGModelBase model, int texOffX, int texOffY) {
		this(model);
		this.setTextureOffset(texOffX, texOffY);
	}

	/**
	 * Sets the current box's rotation points and rotation angles to another box.
	 */
	public void addChild(TGModelRenderer renderer) {
		if (this.childModels == null) {
			this.childModels = new ArrayList<>();
		}
		this.childModels.add(renderer);
	}

	public TGModelRenderer setTextureOffset(int x, int y) {
		this.textureOffsetX = x;
		this.textureOffsetY = y;
		return this;
	}

	public TGModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth) {
		return this.addBox(offX, offY, offZ, width, height, depth, 0.0F, this.mirror);
	}

	public TGModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
		return this.addBox(offX, offY, offZ, width, height, depth, 0.0F, mirrored);
	}

	/**
	 * Creates a textured box.
	 */
	public TGModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
		return this.addBox(offX, offY, offZ, width, height, depth, scaleFactor, this.mirror);
	}

	public TGModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
		this.cubeList.add(new ModelPart.Cube(this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, (float) width, (float) height, (float) depth,
				scaleFactor, scaleFactor, scaleFactor, mirrored, this.textureWidth, this.textureHeight, EnumSet.allOf(Direction.class)));
		return this;
	}

	/**
	 * Adds a box with explicit texture offset (1.12: cubeList.add(new ModelBox(renderer, u, v, ...)))
	 */
	public TGModelRenderer addBox(int texU, int texV, float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
		this.cubeList.add(new ModelPart.Cube(texU, texV, offX, offY, offZ, (float) width, (float) height, (float) depth,
				scaleFactor, scaleFactor, scaleFactor, mirrored, this.textureWidth, this.textureHeight, EnumSet.allOf(Direction.class)));
		return this;
	}

	public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
		this.rotationPointX = rotationPointXIn;
		this.rotationPointY = rotationPointYIn;
		this.rotationPointZ = rotationPointZIn;
	}

	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale) {
		this.render(poseStack, buffer, packedLight, packedOverlay, scale, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, float red, float green, float blue, float alpha) {
		if (legacyRendering || !RenderSystem.isOnRenderThread()) {
			this.renderLegacy(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
			return;
		}
		PoseStack.Pose pose = poseStack.last();
		this.renderFast(pose.pose(), pose.normal(), buffer, packedLight, packedOverlay, scale, red, green, blue, alpha, true);
	}

	/**
	 * The original (1.12 emulation) render path: one pose push and transformation per part, cubes compiled by ModelPart.Cube.
	 * Kept as reference for the bake verification (BakeVerifier) and as fallback (-Dtechguns.legacyModelRender=true).
	 */
	public void renderLegacy(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, float red, float green, float blue, float alpha) {
		if (!this.isHidden && this.showModel) {
			poseStack.pushPose();
			poseStack.translate(this.offsetX, this.offsetY, this.offsetZ);

			if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
				if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
					poseStack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
				}
			} else {
				poseStack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
				this.applyRotation(poseStack);
			}

			this.compileCubes(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);

			if (this.childModels != null) {
				for (TGModelRenderer child : this.childModels) {
					child.renderLegacy(poseStack, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha);
				}
			}
			poseStack.popPose();
		}
	}

	// ------------------------------------------------------------------------------------------------------------------
	// Fast path: pre-baked vertex data.
	//
	// The cubes of a part are converted once to flat quad data (model units). A part whose fields (offset, rotation point,
	// angles, visibility, scale) and whose children did not change since its previous render is "stable": its whole subtree is
	// transformed once into the space of its parent (bakedData) and every render is a single loop over that data with the
	// current pose, without pose push, matrix allocation or rotation. A part that changes (animation) is drawn with its own
	// matrix (no allocation either) and its children are handled the same way, so static children of animated parts stay baked.
	// Zero area quads (boxes with a zero size) produce no pixel and are skipped.
	// Rendering only happens on the render thread, the static scratch buffers below are not shared with other threads.
	// ------------------------------------------------------------------------------------------------------------------

	/**
	 * Use the original render path everywhere (debug / fallback): -Dtechguns.legacyModelRender=true
	 */
	public static boolean legacyRendering = Boolean.getBoolean("techguns.legacyModelRender");

	/**
	 * floats per quad: normal (3), then 4 vertices x y z u v
	 */
	public static final int QUAD_FLOATS = 23;

	/** own cubes in model units (Cube coordinates / 16), built from cubeList */
	private float[] cubeData;
	private int cubeDataQuads;
	private int cubeDataCubeCount = -1;

	/** snapshot of the fields at the last dynamic render */
	private boolean sValid;
	private boolean sVisible;
	private float sOffX, sOffY, sOffZ, sRpX, sRpY, sRpZ, sRotX, sRotY, sRotZ, sScale;
	private int sCubeCount, sChildCount;
	/** incremented each time the snapshot changes, the sum over a subtree identifies its state */
	private long version;

	/** this part and its children, transformed into the parent space */
	private float[] bakedData;
	private int bakedQuads;
	private long bakedVersion = -1L;

	private static int depth = 0;
	private static Matrix4f[] M_STACK = new Matrix4f[0];
	private static Matrix3f[] N_STACK = new Matrix3f[0];
	private static final Matrix4f IDENTITY4 = new Matrix4f();
	private static final Matrix3f IDENTITY3 = new Matrix3f();
	private static final Quaternionf QUAT = new Quaternionf();

	private static float[] bakeTmp = new float[QUAD_FLOATS * 256];
	private static int bakeTmpSize = 0;

	private static void ensureStack(int d) {
		if (d >= M_STACK.length) {
			int n = Math.max(16, d * 2);
			Matrix4f[] m = new Matrix4f[n];
			Matrix3f[] nm = new Matrix3f[n];
			for (int i = 0; i < n; i++) {
				m[i] = i < M_STACK.length ? M_STACK[i] : new Matrix4f();
				nm[i] = i < N_STACK.length ? N_STACK[i] : new Matrix3f();
			}
			M_STACK = m;
			N_STACK = nm;
		}
	}

	private int childCount() {
		return this.childModels == null ? 0 : this.childModels.size();
	}

	private boolean ownEqual(float scale) {
		boolean visible = !this.isHidden && this.showModel;
		if (!this.sValid || visible != this.sVisible) {
			return false;
		}
		if (!visible) {
			return true;
		}
		return this.offsetX == this.sOffX && this.offsetY == this.sOffY && this.offsetZ == this.sOffZ && this.rotationPointX == this.sRpX && this.rotationPointY == this.sRpY
				&& this.rotationPointZ == this.sRpZ && this.rotateAngleX == this.sRotX && this.rotateAngleY == this.sRotY && this.rotateAngleZ == this.sRotZ && scale == this.sScale
				&& this.cubeList.size() == this.sCubeCount && this.childCount() == this.sChildCount;
	}

	/**
	 * @return -1 if this part or a part of its subtree changed since its snapshot, else the sum of the versions of the subtree
	 *         (versions only grow, so the same sum means the same state)
	 */
	private long subtreeState(float scale) {
		if (!this.ownEqual(scale)) {
			return -1L;
		}
		long v = this.version;
		if (this.sVisible && this.childModels != null) {
			for (int i = 0; i < this.childModels.size(); i++) {
				long c = this.childModels.get(i).subtreeState(scale);
				if (c < 0L) {
					return -1L;
				}
				v += c;
			}
		}
		return v;
	}

	private void updateSnapshot(float scale) {
		this.sValid = true;
		this.sVisible = true;
		this.sOffX = this.offsetX;
		this.sOffY = this.offsetY;
		this.sOffZ = this.offsetZ;
		this.sRpX = this.rotationPointX;
		this.sRpY = this.rotationPointY;
		this.sRpZ = this.rotationPointZ;
		this.sRotX = this.rotateAngleX;
		this.sRotY = this.rotateAngleY;
		this.sRotZ = this.rotateAngleZ;
		this.sScale = scale;
		this.sCubeCount = this.cubeList.size();
		this.sChildCount = this.childCount();
		this.version++;
	}

	/**
	 * Same transformation as the legacy render path (same JOML calls on the matrices as PoseStack.translate / mulPose)
	 */
	private void applyLocalTransform(Matrix4f m, Matrix3f n, float scale) {
		m.translate(this.offsetX, this.offsetY, this.offsetZ);
		if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
			if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
				m.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
			}
		} else {
			m.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
			if (this.rotateAngleZ != 0.0F) {
				QUAT.rotationZ(this.rotateAngleZ);
				m.rotate(QUAT);
				n.rotate(QUAT);
			}
			if (this.rotateAngleY != 0.0F) {
				QUAT.rotationY(this.rotateAngleY);
				m.rotate(QUAT);
				n.rotate(QUAT);
			}
			if (this.rotateAngleX != 0.0F) {
				QUAT.rotationX(this.rotateAngleX);
				m.rotate(QUAT);
				n.rotate(QUAT);
			}
		}
	}

	private void ensureCubeData() {
		if (this.cubeData != null && this.cubeDataCubeCount == this.cubeList.size()) {
			return;
		}
		QuadCollector col = new QuadCollector();
		PoseStack.Pose identity = new PoseStack().last();
		for (ModelPart.Cube cube : this.cubeList) {
			cube.compile(identity, col, 0, 0, 1f, 1f, 1f, 1f);
		}
		this.cubeData = col.toArray();
		this.cubeDataQuads = this.cubeData.length / QUAD_FLOATS;
		this.cubeDataCubeCount = this.cubeList.size();
	}

	protected void renderFast(Matrix4f m, Matrix3f n, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, float red, float green, float blue, float alpha, boolean topLevel) {
		if (this.isHidden || !this.showModel) {
			//a hidden child changes the content of its parent, a hidden top level part does not matter (keep its bake)
			if (!topLevel && (!this.sValid || this.sVisible)) {
				this.sValid = true;
				this.sVisible = false;
				this.version++;
				this.bakedData = null;
			}
			return;
		}
		long state = this.subtreeState(scale);
		if (state >= 0L) {
			if (this.bakedData == null || this.bakedVersion != state) {
				this.bake(scale);
				this.bakedVersion = state;
			}
			emitQuads(this.bakedData, this.bakedQuads, m, n, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			return;
		}
		//changed since the last render: draw dynamically, bake on the next render if it stays the same
		this.updateSnapshot(scale);
		this.bakedData = null;

		int d = depth++;
		try {
			ensureStack(d);
			Matrix4f lm = M_STACK[d].set(m);
			Matrix3f ln = N_STACK[d].set(n);
			this.applyLocalTransform(lm, ln, scale);
			if (!this.cubeList.isEmpty()) {
				this.ensureCubeData();
				if (scale != 0.0625F) {
					//legacy: poseStack.scale(scale * 16) around the cubes
					bakeTmpSize = 0;
					transformQuads(this.cubeData, this.cubeDataQuads, IDENTITY4, IDENTITY3, scale * 16.0F);
					emitQuads(bakeTmp, bakeTmpSize / QUAD_FLOATS, lm, ln, buffer, packedLight, packedOverlay, red, green, blue, alpha);
				} else {
					emitQuads(this.cubeData, this.cubeDataQuads, lm, ln, buffer, packedLight, packedOverlay, red, green, blue, alpha);
				}
			}
			if (this.childModels != null) {
				for (int i = 0; i < this.childModels.size(); i++) {
					this.childModels.get(i).renderFast(lm, ln, buffer, packedLight, packedOverlay, scale, red, green, blue, alpha, false);
				}
			}
		} finally {
			depth--;
		}
	}

	private void bake(float scale) {
		bakeTmpSize = 0;
		this.appendSubtree(IDENTITY4, IDENTITY3, scale);
		this.bakedData = Arrays.copyOf(bakeTmp, bakeTmpSize);
		this.bakedQuads = bakeTmpSize / QUAD_FLOATS;
	}

	/**
	 * Appends the quads of this part and its children to bakeTmp, m / n transform from the parent space of this part
	 */
	private void appendSubtree(Matrix4f m, Matrix3f n, float scale) {
		if (this.isHidden || !this.showModel) {
			return;
		}
		int d = depth++;
		try {
			ensureStack(d);
			Matrix4f lm = M_STACK[d].set(m);
			Matrix3f ln = N_STACK[d].set(n);
			this.applyLocalTransform(lm, ln, scale);
			if (!this.cubeList.isEmpty()) {
				this.ensureCubeData();
				transformQuads(this.cubeData, this.cubeDataQuads, lm, ln, scale != 0.0625F ? scale * 16.0F : 1.0F);
			}
			if (this.childModels != null) {
				for (int i = 0; i < this.childModels.size(); i++) {
					this.childModels.get(i).appendSubtree(lm, ln, scale);
				}
			}
		} finally {
			depth--;
		}
	}

	/**
	 * Transforms quads (positions pre-scaled by k, like the legacy poseStack.scale(k) around the cubes) and appends them to bakeTmp
	 */
	private static void transformQuads(float[] src, int quads, Matrix4f m, Matrix3f n, float k) {
		int needed = bakeTmpSize + quads * QUAD_FLOATS;
		if (needed > bakeTmp.length) {
			bakeTmp = Arrays.copyOf(bakeTmp, Math.max(needed, bakeTmp.length * 2));
		}
		float m00 = m.m00(), m01 = m.m01(), m02 = m.m02(), m10 = m.m10(), m11 = m.m11(), m12 = m.m12(), m20 = m.m20(), m21 = m.m21(), m22 = m.m22(), m30 = m.m30(), m31 = m.m31(), m32 = m.m32();
		float n00 = n.m00(), n01 = n.m01(), n02 = n.m02(), n10 = n.m10(), n11 = n.m11(), n12 = n.m12(), n20 = n.m20(), n21 = n.m21(), n22 = n.m22();
		//PoseStack.scale with a uniform scale <= 0 negates the normal matrix
		float ns = k > 0.0F ? 1.0F : -1.0F;
		float[] dst = bakeTmp;
		int o = bakeTmpSize;
		for (int q = 0; q < quads; q++) {
			int s = q * QUAD_FLOATS;
			float nx = src[s] * ns, ny = src[s + 1] * ns, nz = src[s + 2] * ns;
			dst[o] = n00 * nx + (n10 * ny + n20 * nz);
			dst[o + 1] = n01 * nx + (n11 * ny + n21 * nz);
			dst[o + 2] = n02 * nx + (n12 * ny + n22 * nz);
			for (int v = 0; v < 4; v++) {
				int sv = s + 3 + v * 5;
				int ov = o + 3 + v * 5;
				float x = src[sv] * k, y = src[sv + 1] * k, z = src[sv + 2] * k;
				dst[ov] = m00 * x + (m10 * y + (m20 * z + m30));
				dst[ov + 1] = m01 * x + (m11 * y + (m21 * z + m31));
				dst[ov + 2] = m02 * x + (m12 * y + (m22 * z + m32));
				dst[ov + 3] = src[sv + 3];
				dst[ov + 4] = src[sv + 4];
			}
			o += QUAD_FLOATS;
		}
		bakeTmpSize = o;
	}

	/**
	 * Writes quads with the given pose (same math as ModelPart.Cube.compile, without allocation)
	 */
	public static void emitQuads(float[] data, int quads, Matrix4f m, Matrix3f n, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		float m00 = m.m00(), m01 = m.m01(), m02 = m.m02(), m10 = m.m10(), m11 = m.m11(), m12 = m.m12(), m20 = m.m20(), m21 = m.m21(), m22 = m.m22(), m30 = m.m30(), m31 = m.m31(), m32 = m.m32();
		float n00 = n.m00(), n01 = n.m01(), n02 = n.m02(), n10 = n.m10(), n11 = n.m11(), n12 = n.m12(), n20 = n.m20(), n21 = n.m21(), n22 = n.m22();
		BufferBuilder bb = FastVertexWriter.target(buffer);
		if (bb != null) {
			if (quads <= 0) {
				return;
			}
			long base = FastVertexWriter.reserve(quads * 4);
			byte cr = FastVertexWriter.colorByte(red), cg = FastVertexWriter.colorByte(green), cb = FastVertexWriter.colorByte(blue), ca = FastVertexWriter.colorByte(alpha);
			long p = base;
			for (int q = 0; q < quads; q++) {
				int s = q * QUAD_FLOATS;
				float nx = data[s], ny = data[s + 1], nz = data[s + 2];
				byte bx = FastVertexWriter.normalByte(n00 * nx + (n10 * ny + n20 * nz));
				byte by = FastVertexWriter.normalByte(n01 * nx + (n11 * ny + n21 * nz));
				byte bz = FastVertexWriter.normalByte(n02 * nx + (n12 * ny + n22 * nz));
				for (int v = 0; v < 4; v++) {
					int sv = s + 3 + v * 5;
					float x = data[sv], y = data[sv + 1], z = data[sv + 2];
					FastVertexWriter.putConstant(p, cr, cg, cb, ca, packedOverlay, packedLight);
					FastVertexWriter.putVertex(p, m00 * x + (m10 * y + (m20 * z + m30)), m01 * x + (m11 * y + (m21 * z + m31)), m02 * x + (m12 * y + (m22 * z + m32)), data[sv + 3],
							data[sv + 4], bx, by, bz);
					p += FastVertexWriter.VERTEX_SIZE;
				}
			}
			FastVertexWriter.commit(bb, quads * 4);
			return;
		}
		for (int q = 0; q < quads; q++) {
			int s = q * QUAD_FLOATS;
			float nx = data[s], ny = data[s + 1], nz = data[s + 2];
			float fx = n00 * nx + (n10 * ny + n20 * nz);
			float fy = n01 * nx + (n11 * ny + n21 * nz);
			float fz = n02 * nx + (n12 * ny + n22 * nz);
			for (int v = 0; v < 4; v++) {
				int sv = s + 3 + v * 5;
				float x = data[sv], y = data[sv + 1], z = data[sv + 2];
				buffer.vertex(m00 * x + (m10 * y + (m20 * z + m30)), m01 * x + (m11 * y + (m21 * z + m31)), m02 * x + (m12 * y + (m22 * z + m32)), red, green, blue, alpha,
						data[sv + 3], data[sv + 4], packedOverlay, packedLight, fx, fy, fz);
			}
		}
	}

	/**
	 * A quad whose 4 positions are at most 2 distinct points has no area and produces no pixel (boxes with a zero size).
	 * The 4 positions start at offset, stride floats apart.
	 */
	public static boolean isDegenerateQuad(float[] d, int offset, int stride) {
		int distinct = 1;
		for (int i = 1; i < 4; i++) {
			int a = offset + i * stride;
			boolean seen = false;
			for (int j = 0; j < i; j++) {
				int b = offset + j * stride;
				if (d[a] == d[b] && d[a + 1] == d[b + 1] && d[a + 2] == d[b + 2]) {
					seen = true;
					break;
				}
			}
			if (!seen) {
				distinct++;
			}
		}
		return distinct <= 2;
	}

	/**
	 * Collects the quads written by ModelPart.Cube.compile with an identity pose, skipping zero area quads
	 */
	private static class QuadCollector implements VertexConsumer {
		private float[] data = new float[QUAD_FLOATS * 6];
		private int size = 0;
		private final float[] cur = new float[QUAD_FLOATS];
		private int vIndex = 0;

		@Override
		public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY,
				float normalZ) {
			if (this.vIndex == 0) {
				this.cur[0] = normalX;
				this.cur[1] = normalY;
				this.cur[2] = normalZ;
			}
			int o = 3 + this.vIndex * 5;
			this.cur[o] = x;
			this.cur[o + 1] = y;
			this.cur[o + 2] = z;
			this.cur[o + 3] = texU;
			this.cur[o + 4] = texV;
			if (++this.vIndex == 4) {
				this.vIndex = 0;
				if (!isDegenerateQuad(this.cur, 3, 5)) {
					if (this.size + QUAD_FLOATS > this.data.length) {
						this.data = Arrays.copyOf(this.data, this.data.length * 2);
					}
					System.arraycopy(this.cur, 0, this.data, this.size, QUAD_FLOATS);
					this.size += QUAD_FLOATS;
				}
			}
		}

		float[] toArray() {
			return Arrays.copyOf(this.data, this.size);
		}

		@Override
		public VertexConsumer vertex(double x, double y, double z) {
			throw new UnsupportedOperationException();
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			throw new UnsupportedOperationException();
		}

		@Override
		public VertexConsumer uv(float u, float v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public VertexConsumer overlayCoords(int u, int v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public VertexConsumer uv2(int u, int v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void endVertex() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void defaultColor(int red, int green, int blue, int alpha) {
		}

		@Override
		public void unsetDefaultColor() {
		}
	}

	/**
	 * 1.12 rendered rotations in the order Z, Y, X
	 */
	protected void applyRotation(PoseStack poseStack) {
		if (this.rotateAngleZ != 0.0F) {
			poseStack.mulPose(Axis.ZP.rotation(this.rotateAngleZ));
		}
		if (this.rotateAngleY != 0.0F) {
			poseStack.mulPose(Axis.YP.rotation(this.rotateAngleY));
		}
		if (this.rotateAngleX != 0.0F) {
			poseStack.mulPose(Axis.XP.rotation(this.rotateAngleX));
		}
	}

	protected void compileCubes(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, float red, float green, float blue, float alpha) {
		if (this.cubeList.isEmpty()) {
			return;
		}
		//Cube.compile divides by 16, so a scale of 1/16 needs no extra transformation
		boolean customScale = scale != 0.0625F;
		if (customScale) {
			poseStack.pushPose();
			float s = scale * 16.0F;
			poseStack.scale(s, s, s);
		}
		PoseStack.Pose pose = poseStack.last();
		for (ModelPart.Cube cube : this.cubeList) {
			cube.compile(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
		if (customScale) {
			poseStack.popPose();
		}
	}

	/**
	 * Renders without the offset / like 1.12 renderWithRotation
	 */
	public void renderWithRotation(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale) {
		if (!this.isHidden && this.showModel) {
			poseStack.pushPose();
			poseStack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
			if (this.rotateAngleY != 0.0F) {
				poseStack.mulPose(Axis.YP.rotation(this.rotateAngleY));
			}
			if (this.rotateAngleX != 0.0F) {
				poseStack.mulPose(Axis.XP.rotation(this.rotateAngleX));
			}
			if (this.rotateAngleZ != 0.0F) {
				poseStack.mulPose(Axis.ZP.rotation(this.rotateAngleZ));
			}
			this.compileCubes(poseStack, buffer, packedLight, packedOverlay, scale, 1f, 1f, 1f, 1f);
			poseStack.popPose();
		}
	}

	/**
	 * Allows the changing of Angles after a box has been rendered (1.12 postRender), applies the transformation of this part
	 * to the pose stack (without push).
	 */
	public void postRender(PoseStack poseStack, float scale) {
		if (!this.isHidden && this.showModel) {
			if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
				if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
					poseStack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
				}
			} else {
				poseStack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
				this.applyRotation(poseStack);
			}
		}
	}

	/**
	 * Returns the model renderer with the new texture parameters.
	 */
	public TGModelRenderer setTextureSize(int textureWidthIn, int textureHeightIn) {
		this.textureWidth = (float) textureWidthIn;
		this.textureHeight = (float) textureHeightIn;
		return this;
	}

	/**
	 * Copies rotation point and angles from a vanilla ModelPart (used by armor models attached to a HumanoidModel)
	 */
	public void copyFrom(ModelPart part) {
		this.rotationPointX = part.x;
		this.rotationPointY = part.y;
		this.rotationPointZ = part.z;
		this.rotateAngleX = part.xRot;
		this.rotateAngleY = part.yRot;
		this.rotateAngleZ = part.zRot;
	}
}
