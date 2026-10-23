package techguns.client.render.entities.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.entities.projectiles.FlamethrowerProjectile;

/**
 * Like in 1.12 the projectile itself is not rendered (the flames are particles)
 */
public class RenderFlameThrowerProjectile extends RenderTextureProjectile<FlamethrowerProjectile>{

	public RenderFlameThrowerProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
		textureLoc = new ResourceLocation(Techguns.MODID,"textures/fx/fireball.png");
    	scale=1.0f;
    	baseSize=0.15f;
	}

	@Override
	public void render(FlamethrowerProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
	}
}
