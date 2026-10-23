package techguns.client.render.entities.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.render.TGRenderHelper.RenderType;
import techguns.entities.projectiles.BioGunProjectile;

public class RenderBioGunProjectile extends RenderTextureProjectile<BioGunProjectile>{

	public RenderBioGunProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
		textureLoc = new ResourceLocation(Techguns.MODID,"textures/entity/bioblob.png");
		baseSize=0.15f;
		scale=1.0f;
	}

	@Override
	public void render(BioGunProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		float scale = 1.0f;
		switch (entity.getChargeLevel()) {
		case 1:
			scale = 1.25f;
			break;
		case 2:
			scale = 2.5f;
			break;
		case 3:
			scale = 4.0f;
			break;
		}

		this.renderTexture(entity, poseStack, buffers, baseSize * scale, RenderType.ADDITIVE);
	}
}
