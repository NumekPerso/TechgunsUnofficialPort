package techguns.client.render.entities.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.models.ModelMultipart;
import techguns.client.models.guns.ModelFragGrenade;
import techguns.entities.projectiles.FragGrenadeProjectile;

public class RenderFragGrenadeProjectile extends EntityRenderer<FragGrenadeProjectile> {

	private ResourceLocation textureLoc = new ResourceLocation(Techguns.MODID,"textures/guns/frag_grenade_texture.png");
	private ModelMultipart model = new ModelFragGrenade(false);

	public RenderFragGrenadeProjectile(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public ResourceLocation getTextureLocation(FragGrenadeProjectile entity) {
		return textureLoc;
	}

	@Override
	public void render(FragGrenadeProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		RenderGrenadeProjectile.renderGrenade(entity, partialTicks, poseStack, buffers, packedLight, model, this.getTextureLocation(entity));
	}
}
