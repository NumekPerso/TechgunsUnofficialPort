package techguns.client.particle;

import java.util.List;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class TGFXType {
	public String name;
	boolean isList = false;

	public abstract List<TGParticleSystem> createParticleSystems(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ);
	public abstract List<TGParticleSystem> createParticleSystemsOnEntity(Entity ent);
	public abstract List<TGParticleSystem> createParticleSystemsOnParticle(Level world, TGParticle part);
	public abstract List<TGParticleSystem> createParticleSystemsOnEntityItemAttached(Entity ent, InteractionHand hand);
}
