package techguns.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface ITGParticle {

	public Vec3 getPos();

	public boolean shouldRemove();
	public void updateTick();

	/**
	 * 1.12: doRender(BufferBuilder, ...). Vertices are written with the matrix of the given pose stack
	 * (world particles: camera rotation, positions relative to the camera; item attached: pose of the item).
	 */
	public void doRender(PoseStack poseStack, MultiBufferSource buffers, Entity entityIn, float partialTickTime, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ);

	public AABB getRenderBoundingBox(float ptt, Entity viewEnt);

	public default boolean doNotSort() {
		return false;
	}

	public double getDepth();

	public void setDepth(double depth);
	public void setItemAttached();
}
