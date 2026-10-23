package techguns.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import techguns.TGEntities;
import techguns.client.render.entities.projectiles.RenderAdvancedBulletProjectile;
import techguns.client.render.entities.projectiles.RenderBioGunProjectile;
import techguns.client.render.entities.projectiles.RenderBlasterProjectile;
import techguns.client.render.entities.projectiles.RenderFlameThrowerProjectile;
import techguns.client.render.entities.projectiles.RenderFragGrenadeProjectile;
import techguns.client.render.entities.projectiles.RenderGenericProjectile;
import techguns.client.render.entities.projectiles.RenderGrenade40mmProjectile;
import techguns.client.render.entities.projectiles.RenderGrenadeProjectile;
import techguns.client.render.entities.projectiles.RenderInvisibleProjectile;
import techguns.client.render.entities.projectiles.RenderLaserProjectile;
import techguns.client.render.entities.projectiles.RenderNDRProjectile;
import techguns.client.render.entities.projectiles.RenderRocketProjectile;
import techguns.client.render.entities.projectiles.RenderSonicShotgunProjectile;
import techguns.client.render.entities.projectiles.RenderStoneBulletProjectile;
import techguns.client.render.entities.projectiles.RenderTeslaProjectile;

/**
 * Projectile renderer registration, same renderers as 1.12 ClientProxy.registerEntityRenderers()
 * (FlyingGibs is registered by TGFXHandler)
 */
public class TGEntityRenderers {

	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		reg(event, TGEntities.GENERIC_PROJECTILE, RenderGenericProjectile::new);
		reg(event, TGEntities.GENERIC_PROJECTILE_INCENDIARY, RenderGenericProjectile::new);
		reg(event, TGEntities.ROCKET_PROJECTILE, RenderRocketProjectile::new);
		reg(event, TGEntities.STONE_BULLET_PROJECTILE, RenderStoneBulletProjectile::new);
		reg(event, TGEntities.BIOGUN_PROJECTILE, RenderBioGunProjectile::new);
		reg(event, TGEntities.FLAMETHROWER_PROJECTILE, RenderFlameThrowerProjectile::new);
		reg(event, TGEntities.GRENADE_PROJECTILE, RenderGrenadeProjectile::new);
		event.registerEntityRenderer(TGEntities.GRENADE_40MM_PROJECTILE, RenderGrenade40mmProjectile::new);
		reg(event, TGEntities.LASER_PROJECTILE, RenderLaserProjectile::new);
		reg(event, TGEntities.TESLA_PROJECTILE, RenderTeslaProjectile::new);
		reg(event, TGEntities.NDR_PROJECTILE, RenderNDRProjectile::new);
		reg(event, TGEntities.BLASTER_PROJECTILE, RenderBlasterProjectile::new);
		reg(event, TGEntities.GAUSS_PROJECTILE, RenderAdvancedBulletProjectile::new);
		reg(event, TGEntities.ADVANCED_BULLET_PROJECTILE, RenderAdvancedBulletProjectile::new);
		reg(event, TGEntities.GUIDED_MISSILE_PROJECTILE, RenderRocketProjectile::new);
		reg(event, TGEntities.DEATOMIZER_PROJECTILE, RenderInvisibleProjectile::new);
		reg(event, TGEntities.ALIEN_BLASTER_PROJECTILE, RenderInvisibleProjectile::new);
		reg(event, TGEntities.SONIC_SHOTGUN_PROJECTILE, RenderSonicShotgunProjectile::new);
		reg(event, TGEntities.CYBERDEMON_BLASTER_PROJECTILE, RenderInvisibleProjectile::new);
		reg(event, TGEntities.POWERHAMMER_PROJECTILE, RenderInvisibleProjectile::new);
		reg(event, TGEntities.CHAINSAW_PROJECTILE, RenderInvisibleProjectile::new);
		reg(event, TGEntities.FRAG_GRENADE_PROJECTILE, RenderFragGrenadeProjectile::new);
		reg(event, TGEntities.ROCKET_PROJECTILE_NUKE, RenderRocketProjectile::new);
		reg(event, TGEntities.TFG_PROJECTILE, RenderInvisibleProjectile::new);
		reg(event, TGEntities.GENERIC_PROJECTILE_EXPLOSIVE, RenderGenericProjectile::new);
		reg(event, TGEntities.ROCKET_PROJECTILE_HV, RenderRocketProjectile::new);
		reg(event, TGEntities.GUIDED_MISSILE_PROJECTILE_HV, RenderRocketProjectile::new);
	}

	/**
	 * Registers a projectile renderer wrapped in a FirstPersonCorrectedRenderer
	 */
	protected static <T extends Entity> void reg(EntityRenderersEvent.RegisterRenderers event, EntityType<? extends T> type, EntityRendererProvider<T> provider) {
		event.registerEntityRenderer(type, ctx -> new FirstPersonCorrectedRenderer<T>(ctx, provider.create(ctx)));
	}

	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
	}
}
