package techguns.client.render.entities.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.models.TGObjModel;
import techguns.client.render.TGRenderHelper;
import techguns.entities.projectiles.Grenade40mmProjectile;

/**
 * 1.12 baked the OBJ model techguns:grenade40mm (blockstate flip-v false) with a sprite covering launchergrenade.png and drew its quads.
 * The OBJ is read with TGObjModel (loaded lazily on first render).
 */
public class RenderGrenade40mmProjectile extends EntityRenderer<Grenade40mmProjectile>{

	private static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/entity/launchergrenade.png");
	private static final TGObjModel bakedModel = new TGObjModel(new ResourceLocation(Techguns.MODID, "models/item/grenade40mm.obj"), false);

	public RenderGrenade40mmProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public ResourceLocation getTextureLocation(Grenade40mmProjectile entity) {
		return texture;
	}

	@Override
	public void render(Grenade40mmProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {

		poseStack.pushPose();

		TGRenderHelper.rotate(poseStack, entity.getYRot() - 90, 0.0F, 1.0F, 0.0F);
		TGRenderHelper.rotate(poseStack, entity.getXRot(), 0.0F, 0.0F, 1.0F);

		TGRenderHelper.rotate(poseStack, 180, 1, 0, 0);
		float scale = 0.5f;
		poseStack.scale(scale, scale, scale);

		VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
		bakedModel.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY);

		poseStack.popPose();

	}

}
