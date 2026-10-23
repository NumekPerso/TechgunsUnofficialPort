package techguns.client.models;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.util.Mth;

/**
 * Writes the vertices of the pre-baked models directly in the vanilla NEW_ENTITY layout into a scratch buffer and appends them to
 * the BufferBuilder with one putBulkData copy, instead of ~16 ByteBuffer writes per vertex through BufferBuilder.vertex(...).
 * The bytes are exactly the ones BufferBuilder.vertex would write (checked by BakeVerifier).
 *
 * Only used for a plain vanilla BufferBuilder in the NEW_ENTITY format (GUI, held / dropped items, armor...). Any other consumer
 * (shader mods changing the format or wrapping the builder, outline / foil multi consumers, tint wrappers) goes through the normal
 * VertexConsumer calls. Render thread only.
 */
public final class FastVertexWriter {

	/** -Dtechguns.fastVertexWriter=false disables the direct writes */
	public static boolean enabled = !"false".equals(System.getProperty("techguns.fastVertexWriter"));

	public static final int VERTEX_SIZE = 36;

	/** statistics for BakeVerifier */
	public static long writtenVertices = 0L;

	private static ByteBuffer scratch = null;
	private static long address = 0L;

	private FastVertexWriter() {
	}

	/**
	 * @return the BufferBuilder if the vertices can be written directly, else null
	 */
	public static BufferBuilder target(VertexConsumer vc) {
		if (!enabled || vc.getClass() != BufferBuilder.class) {
			return null;
		}
		BufferBuilder bb = (BufferBuilder) vc;
		if (bb.format != DefaultVertexFormat.NEW_ENTITY || !bb.building || bb.defaultColorSet) {
			return null;
		}
		return bb;
	}

	/**
	 * @return address of a scratch area for the given number of vertices
	 */
	public static long reserve(int vertices) {
		int bytes = vertices * VERTEX_SIZE;
		if (scratch == null || scratch.capacity() < bytes) {
			int cap = Math.max(bytes, scratch == null ? VERTEX_SIZE * 4096 : scratch.capacity() * 2);
			scratch = scratch == null ? MemoryUtil.memAlloc(cap) : MemoryUtil.memRealloc(scratch, cap);
			address = MemoryUtil.memAddress0(scratch);
		}
		return address;
	}

	/**
	 * Appends the vertices written in the scratch area
	 */
	public static void commit(BufferBuilder bb, int vertices) {
		if (vertices <= 0) {
			return;
		}
		writtenVertices += vertices;
		scratch.clear();
		scratch.limit(vertices * VERTEX_SIZE);
		bb.putBulkData(scratch);
		scratch.clear();
	}

	/** BufferVertexConsumer.normalIntValue */
	public static byte normalByte(float f) {
		return (byte) ((int) (Mth.clamp(f, -1.0F, 1.0F) * 127.0F) & 255);
	}

	/**
	 * Writes the part of a vertex that is the same for all vertices of a render call (color, overlay, light) at p
	 */
	public static void putConstant(long p, byte r, byte g, byte b, byte a, int overlay, int light) {
		MemoryUtil.memPutByte(p + 12, r);
		MemoryUtil.memPutByte(p + 13, g);
		MemoryUtil.memPutByte(p + 14, b);
		MemoryUtil.memPutByte(p + 15, a);
		MemoryUtil.memPutShort(p + 24, (short) (overlay & 65535));
		MemoryUtil.memPutShort(p + 26, (short) (overlay >> 16 & 65535));
		MemoryUtil.memPutShort(p + 28, (short) (light & 65535));
		MemoryUtil.memPutShort(p + 30, (short) (light >> 16 & 65535));
	}

	public static void putVertex(long p, float x, float y, float z, float u, float v, byte nx, byte ny, byte nz) {
		MemoryUtil.memPutFloat(p, x);
		MemoryUtil.memPutFloat(p + 4, y);
		MemoryUtil.memPutFloat(p + 8, z);
		MemoryUtil.memPutFloat(p + 16, u);
		MemoryUtil.memPutFloat(p + 20, v);
		MemoryUtil.memPutByte(p + 32, nx);
		MemoryUtil.memPutByte(p + 33, ny);
		MemoryUtil.memPutByte(p + 34, nz);
		MemoryUtil.memPutByte(p + 35, (byte) 0);
	}

	/** (int)(f * 255) like BufferBuilder.vertex */
	public static byte colorByte(float f) {
		return (byte) ((int) (f * 255.0F));
	}
}
