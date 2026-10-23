package techguns.client.particle;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import techguns.client.particle.DeathEffect.GoreData;
import techguns.entities.projectiles.FlyingGibs;

/**
 * Client part of the 1.12 FlyingGibs: gore data and blood trail particle system
 */
public class FlyingGibsClient extends FlyingGibs {

	public GoreData data;

	public TGParticleSystem trail_system;

	public FlyingGibsClient(Level world, LivingEntity entity, GoreData data, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float size, int bodypart) {
		super(world, entity, posX, posY, posZ, motionX, motionY, motionZ, size, bodypart);
		this.data = data;

		if (data.type_trail != null) {
			trail_system = new TGParticleSystem(this, data.type_trail);
			TGFXHandler.particleManager.addEffect(trail_system);
		}
	}

	@Override
	protected void onHitGround() {
		if (trail_system != null) {
			trail_system.remove();
		}
	}

}
