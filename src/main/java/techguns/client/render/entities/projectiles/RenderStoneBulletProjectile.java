package techguns.client.render.entities.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.entities.projectiles.StoneBulletProjectile;

public class RenderStoneBulletProjectile extends RenderTextureProjectile<StoneBulletProjectile> {

    public RenderStoneBulletProjectile(EntityRendererProvider.Context ctx)
    {
    	super(ctx);
    	textureLoc = new ResourceLocation(Techguns.MODID,"textures/entity/handgunbullet.png");
    	scale=1.0f;
    	baseSize=0.1f;
    }

}
