package techguns.client.render.entities.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import techguns.Techguns;
import techguns.client.models.ModelMultipart;
import techguns.client.models.projectiles.ModelRocket;
import techguns.client.render.TGRenderHelper;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.GuidedMissileProjectileHV;
import techguns.entities.projectiles.RocketProjectileHV;
import techguns.entities.projectiles.RocketProjectileNuke;

public class RenderRocketProjectile<T extends GenericProjectile> extends EntityRenderer<T> {

	private ResourceLocation textureLoc = new ResourceLocation(Techguns.MODID,"textures/guns/rocket.png");
	private ResourceLocation textureLocNuke = new ResourceLocation(Techguns.MODID,"textures/guns/rocket_nuke.png");
	private ResourceLocation textureLocHV = new ResourceLocation(Techguns.MODID,"textures/guns/rocket_hv.png");

	private ModelMultipart model = new ModelRocket();

	public RenderRocketProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(T par1Entity, float entityYaw, float par9, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {

		poseStack.pushPose();

		TGRenderHelper.rotate(poseStack, Mth.lerp(par9, par1Entity.yRotO, par1Entity.getYRot()) - 90.0F, 0.0F, 1.0F, 0.0F);
		TGRenderHelper.rotate(poseStack, Mth.lerp(par9, par1Entity.xRotO, par1Entity.getXRot()), 0.0F, 0.0F, 1.0F);

		poseStack.scale(0.9f, 0.9f, 0.9f);

		VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(par1Entity)));

		model.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, par1Entity, 0, 0, 0, 0, 0, 0.0625F);

		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		if(entity instanceof RocketProjectileNuke) {
			return textureLocNuke;
		} else if (entity instanceof RocketProjectileHV || entity instanceof GuidedMissileProjectileHV) {
			return textureLocHV;
		}
		return textureLoc;
	}
}
