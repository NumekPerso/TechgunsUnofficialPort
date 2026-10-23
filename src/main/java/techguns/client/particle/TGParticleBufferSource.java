package techguns.client.particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import techguns.client.render.TGRenderTypes;

/**
 * Batched buffer source of the world particles: one BufferBuilder per render type, every type is drawn once per frame
 * in endBatch (the immediate buffer source of the 1.12 port flushed at every render type change, almost one draw call
 * per particle when smoke / fire / sparks alternate).
 *
 * Depth order: alpha blended types sort their quads back to front on upload (sortOnUpload, vertices are in view space).
 * Between types: depth writing types first, then alpha blended types from the farthest to the nearest batch, then
 * additive types (order independent), then the types without depth test.
 */
public class TGParticleBufferSource implements MultiBufferSource {

	protected final Map<RenderType, Batch> batches = new HashMap<>();
	protected final List<Batch> active = new ArrayList<>();

	/**
	 * Depth of the particle being rendered (distance along the view direction), set by the particle manager
	 */
	protected double currentDepth = 0;

	protected static class Batch {
		final RenderType type;
		final BufferBuilder builder;
		final int order;
		double maxDepth;

		Batch(RenderType type) {
			this.type = type;
			this.builder = new BufferBuilder(Math.max(type.bufferSize(), 4096));
			this.order = TGRenderTypes.getFXDrawOrder(type);
		}
	}

	public void setCurrentDepth(double depth) {
		this.currentDepth = depth;
	}

	@Override
	public VertexConsumer getBuffer(RenderType type) {
		Batch b = batches.get(type);
		if (b == null) {
			b = new Batch(type);
			batches.put(type, b);
		}
		if (!b.builder.building()) {
			b.builder.begin(type.mode(), type.format());
			b.maxDepth = currentDepth;
			active.add(b);
		} else if (currentDepth > b.maxDepth) {
			b.maxDepth = currentDepth;
		}
		return b.builder;
	}

	/**
	 * Draws every batch (one draw call per render type)
	 */
	public void endBatch() {
		if (active.isEmpty()) {
			return;
		}
		if (active.size() > 1) {
			active.sort((b1, b2) -> {
				if (b1.order != b2.order) {
					return Integer.compare(b1.order, b2.order);
				}
				//farthest batch first
				return Double.compare(b2.maxDepth, b1.maxDepth);
			});
		}
		for (int i = 0; i < active.size(); i++) {
			Batch b = active.get(i);
			b.type.end(b.builder, RenderSystem.getVertexSorting());
		}
		active.clear();
	}
}
