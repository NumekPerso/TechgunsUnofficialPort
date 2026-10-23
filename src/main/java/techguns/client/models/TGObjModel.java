package techguns.client.models;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import techguns.Techguns;

/**
 * Minimal Wavefront OBJ model used by the OBJ based guns (gauss rifle, grenade launcher). The 1.12 version baked the OBJ with
 * the Forge OBJ loader (identity transform, own sprite covering the whole texture file) and drew it with RenderItem.renderModel.
 * This class reads the OBJ file directly (v / vt / vn / f, optional flip-v like the 1.12 blockstates) and draws the quads with the
 * texture of the gun, so the vertex positions and UVs are exactly the ones of the OBJ file.
 *
 * The file is loaded lazily on first render (client resource manager).
 */
public class TGObjModel {

	protected final ResourceLocation location;
	protected final boolean flipV;

	/** 4 vertices per quad (triangles repeat the last vertex), 8 floats per vertex: x y z u v nx ny nz */
	protected float[] data = null;
	protected int quadCount = 0;
	protected boolean loadFailed = false;

	public TGObjModel(ResourceLocation location, boolean flipV) {
		this.location = location;
		this.flipV = flipV;
	}

	protected void load() {
		List<float[]> verts = new ArrayList<>();
		List<float[]> uvs = new ArrayList<>();
		List<float[]> normals = new ArrayList<>();
		List<float[]> quads = new ArrayList<>(); // each: 32 floats

		try {
			Resource res = Minecraft.getInstance().getResourceManager().getResourceOrThrow(location);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.open(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					String[] p = line.split("\\s+");
					switch (p[0]) {
					case "v":
						verts.add(new float[] { Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3]) });
						break;
					case "vt":
						uvs.add(new float[] { Float.parseFloat(p[1]), p.length > 2 ? Float.parseFloat(p[2]) : 0f });
						break;
					case "vn":
						normals.add(new float[] { Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3]) });
						break;
					case "f":
						addFace(p, verts, uvs, normals, quads);
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			Techguns.LOGGER.error("Failed to load OBJ model " + location, e);
			loadFailed = true;
		}

		this.quadCount = quads.size();
		this.data = new float[quads.size() * 32];
		for (int i = 0; i < quads.size(); i++) {
			System.arraycopy(quads.get(i), 0, data, i * 32, 32);
		}
	}

	protected void addFace(String[] p, List<float[]> verts, List<float[]> uvs, List<float[]> normals, List<float[]> quads) {
		int n = p.length - 1;
		if (n < 3) {
			return;
		}
		int[] vi = new int[n];
		int[] ti = new int[n];
		int[] ni = new int[n];
		for (int i = 0; i < n; i++) {
			String[] c = p[i + 1].split("/", -1);
			vi[i] = index(c[0], verts.size());
			ti[i] = (c.length > 1 && !c[1].isEmpty()) ? index(c[1], uvs.size()) : -1;
			ni[i] = (c.length > 2 && !c[2].isEmpty()) ? index(c[2], normals.size()) : -1;
		}
		// triangle fan, quads stay quads
		if (n == 3 || n == 4) {
			quads.add(makeQuad(n == 3 ? new int[] { 0, 1, 2, 2 } : new int[] { 0, 1, 2, 3 }, vi, ti, ni, verts, uvs, normals));
		} else {
			for (int i = 1; i < n - 1; i++) {
				quads.add(makeQuad(new int[] { 0, i, i + 1, i + 1 }, vi, ti, ni, verts, uvs, normals));
			}
		}
	}

	protected static int index(String s, int size) {
		int i = Integer.parseInt(s);
		return i > 0 ? i - 1 : size + i;
	}

	protected float[] makeQuad(int[] order, int[] vi, int[] ti, int[] ni, List<float[]> verts, List<float[]> uvs, List<float[]> normals) {
		float[] q = new float[32];
		// face normal for faces without vertex normals
		float[] v0 = verts.get(vi[order[0]]);
		float[] v1 = verts.get(vi[order[1]]);
		float[] v2 = verts.get(vi[order[2]]);
		Vector3f fn = new Vector3f(v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]).cross(v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]);
		if (fn.lengthSquared() > 0) {
			fn.normalize();
		} else {
			fn.set(0, 1, 0);
		}
		for (int k = 0; k < 4; k++) {
			int idx = order[k];
			float[] v = verts.get(vi[idx]);
			float u = 0, tv = 0;
			if (ti[idx] >= 0) {
				float[] uv = uvs.get(ti[idx]);
				u = uv[0];
				tv = flipV ? 1.0f - uv[1] : uv[1];
			}
			float nx = fn.x, ny = fn.y, nz = fn.z;
			if (ni[idx] >= 0) {
				float[] nn = normals.get(ni[idx]);
				nx = nn[0];
				ny = nn[1];
				nz = nn[2];
			}
			int o = k * 8;
			q[o] = v[0];
			q[o + 1] = v[1];
			q[o + 2] = v[2];
			q[o + 3] = u;
			q[o + 4] = tv;
			q[o + 5] = nx;
			q[o + 6] = ny;
			q[o + 7] = nz;
		}
		return q;
	}

	/**
	 * Draws the model with the current pose, vertex format of RenderType.entityCutoutNoCull (position, color, uv, overlay, light, normal)
	 */
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
		if (TGModelRenderer.legacyRendering) {
			this.renderLegacy(poseStack, buffer, packedLight, packedOverlay);
			return;
		}
		if (data == null && !loadFailed) {
			load();
		}
		if (data == null) {
			return;
		}
		//same math as VertexConsumer.vertex(Matrix4f, ...) / normal(Matrix3f, ...), without the per vertex allocations
		Matrix4f m = poseStack.last().pose();
		Matrix3f n = poseStack.last().normal();
		float m00 = m.m00(), m01 = m.m01(), m02 = m.m02(), m10 = m.m10(), m11 = m.m11(), m12 = m.m12(), m20 = m.m20(), m21 = m.m21(), m22 = m.m22(), m30 = m.m30(), m31 = m.m31(), m32 = m.m32();
		float n00 = n.m00(), n01 = n.m01(), n02 = n.m02(), n10 = n.m10(), n11 = n.m11(), n12 = n.m12(), n20 = n.m20(), n21 = n.m21(), n22 = n.m22();
		float[] d = this.data;
		int count = quadCount * 4;
		com.mojang.blaze3d.vertex.BufferBuilder bb = FastVertexWriter.target(buffer);
		if (bb != null) {
			if (count <= 0) {
				return;
			}
			long p = FastVertexWriter.reserve(count);
			byte white = FastVertexWriter.colorByte(1f);
			for (int i = 0; i < count; i++) {
				int o = i * 8;
				float x = d[o], y = d[o + 1], z = d[o + 2];
				float nx = d[o + 5], ny = d[o + 6], nz = d[o + 7];
				FastVertexWriter.putConstant(p, white, white, white, white, packedOverlay, packedLight);
				FastVertexWriter.putVertex(p, m00 * x + (m10 * y + (m20 * z + m30)), m01 * x + (m11 * y + (m21 * z + m31)), m02 * x + (m12 * y + (m22 * z + m32)), d[o + 3], d[o + 4],
						FastVertexWriter.normalByte(n00 * nx + (n10 * ny + n20 * nz)), FastVertexWriter.normalByte(n01 * nx + (n11 * ny + n21 * nz)),
						FastVertexWriter.normalByte(n02 * nx + (n12 * ny + n22 * nz)));
				p += FastVertexWriter.VERTEX_SIZE;
			}
			FastVertexWriter.commit(bb, count);
			return;
		}
		for (int i = 0; i < count; i++) {
			int o = i * 8;
			float x = d[o], y = d[o + 1], z = d[o + 2];
			float nx = d[o + 5], ny = d[o + 6], nz = d[o + 7];
			buffer.vertex(m00 * x + (m10 * y + (m20 * z + m30)), m01 * x + (m11 * y + (m21 * z + m31)), m02 * x + (m12 * y + (m22 * z + m32)), 1f, 1f, 1f, 1f, d[o + 3], d[o + 4],
					packedOverlay, packedLight, n00 * nx + (n10 * ny + n20 * nz), n01 * nx + (n11 * ny + n21 * nz), n02 * nx + (n12 * ny + n22 * nz));
		}
	}

	/**
	 * Original render path (reference for BakeVerifier)
	 */
	public void renderLegacy(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
		if (data == null && !loadFailed) {
			load();
		}
		if (data == null) {
			return;
		}
		Matrix4f pose = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		for (int i = 0; i < quadCount; i++) {
			for (int k = 0; k < 4; k++) {
				int o = i * 32 + k * 8;
				buffer.vertex(pose, data[o], data[o + 1], data[o + 2]).color(1f, 1f, 1f, 1f).uv(data[o + 3], data[o + 4]).overlayCoords(packedOverlay).uv2(packedLight)
						.normal(normal, data[o + 5], data[o + 6], data[o + 7]).endVertex();
			}
		}
	}
}
