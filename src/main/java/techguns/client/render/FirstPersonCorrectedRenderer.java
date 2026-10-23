package techguns.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Wraps a projectile renderer and applies the FirstPersonFXCorrection to it, so projectiles and beams start at the gun
 * muzzle with any FOV.
 */
public class FirstPersonCorrectedRenderer<T extends Entity> extends EntityRenderer<T> {

	protected final EntityRenderer<T> renderer;

	public FirstPersonCorrectedRenderer(EntityRendererProvider.Context ctx, EntityRenderer<T> renderer) {
		super(ctx);
		this.renderer = renderer;
		this.shadowRadius = 0f;
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		//only projectiles close to the camera (leaving the first person gun) need the per vertex correction
		this.renderer.render(entity, entityYaw, partialTicks, poseStack, FirstPersonFXCorrection.wrapNear(buffers, entity, partialTicks), packedLight);
	}

	@Override
	public boolean shouldRender(T entity, Frustum frustum, double camX, double camY, double camZ) {
		return this.renderer.shouldRender(entity, frustum, camX, camY, camZ);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return this.renderer.getTextureLocation(entity);
	}
}
