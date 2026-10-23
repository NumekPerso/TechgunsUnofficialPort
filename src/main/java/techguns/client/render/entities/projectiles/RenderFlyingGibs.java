package techguns.client.render.entities.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import techguns.Techguns;
import techguns.client.models.gibs.ModelGibs;
import techguns.client.particle.DeathEffect;
import techguns.client.particle.DeathEffect.GoreData;
import techguns.client.particle.FlyingGibsClient;
import techguns.client.render.TGRenderHelper;
import techguns.entities.projectiles.FlyingGibs;

public class RenderFlyingGibs extends EntityRenderer<FlyingGibs> {

	private static final ResourceLocation GORE_TEXTURE = new ResourceLocation(Techguns.MODID, "textures/entity/gore.png");

	public RenderFlyingGibs(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(FlyingGibs par1entity, float entityYaw, float partialTickTime, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {

		LivingEntity entity = par1entity.entity;
		if (entity == null) {
			return;
		}

		GoreData data = par1entity instanceof FlyingGibsClient ? ((FlyingGibsClient) par1entity).data : DeathEffect.getGoreData(entity.getType());
		if (data == null) {
			return;
		}

		float scale = 1.0f;

		ModelGibs model = data.getModel();
		if (model != null) {

			poseStack.pushPose();

			EntityRenderer<?> render = this.entityRenderDispatcher.getRenderer(entity);
			ResourceLocation texture = data.texture;
			if (render instanceof LivingEntityRenderer) {
				if (texture == null) {
					texture = DeathEffectEntityRenderer.getEntityTexture(render, entity);
				}
				DeathEffectEntityRenderer.preRenderCallback((LivingEntityRenderer<?, ?>) render, entity, poseStack, partialTickTime);
			}
			if (texture == null) {
				texture = GORE_TEXTURE;
			}

			float angle = 0.0f;
			float rot_angle = 90.0f;
			if (par1entity.onGround()) {
				angle = 5 + ((float) par1entity.hitGroundTTL / (float) par1entity.maxTimeToLive) * 15.0f;
				rot_angle += ((float) (par1entity.maxTimeToLive - par1entity.hitGroundTTL) * angle);

				if (par1entity.timeToLive <= 20) {
					float offsetY = ((20-par1entity.timeToLive) + partialTickTime) * -0.05f;
					poseStack.translate(0.0f, offsetY, 0.0f);
				}

			} else {
				angle = 5 + ((float) par1entity.timeToLive / (float) par1entity.maxTimeToLive) * 15.0f;
				rot_angle += ((float) par1entity.tickCount + partialTickTime) * angle;
			}

			TGRenderHelper.rotate(poseStack, (float) rot_angle, (float) par1entity.rotationAxis.x, (float) par1entity.rotationAxis.y,
					(float) par1entity.rotationAxis.z);

			VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));
			model.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, par1entity, 0.0625f * scale, par1entity.bodypart);

			poseStack.popPose();
		}

	}

	@Override
	public ResourceLocation getTextureLocation(FlyingGibs entity) {
		if (entity.entity != null) {
			ResourceLocation tex = DeathEffect.getGoreData(entity.entity.getType()).texture;
			if (tex != null) {
				return tex;
			}
		}
		return GORE_TEXTURE;
	}

}
