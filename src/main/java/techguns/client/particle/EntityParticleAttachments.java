package techguns.client.particle;

import java.util.LinkedList;
import java.util.List;

/**
 * Attached particle lists of non player entities (1.12: stored in the TGShooterValues capability of NPCs),
 * kept by TGFXHandler in a client side map.
 */
public class EntityParticleAttachments implements ITGParticleAttachments {

	protected List<ITGParticle> entityParticles = null;
	protected List<TGParticleSystem> particleSysMH=null;
	protected List<TGParticleSystem> particleSysOH=null;
	protected List<ITGParticle> entityParticlesMH = null;
	protected List<ITGParticle> entityParticlesOH = null;

	@Override
	public List<ITGParticle> getEntityParticles() {
		return this.entityParticles;
	}

	@Override
	public List<TGParticleSystem> getParticleSysMainhand() {
		return this.particleSysMH;
	}

	@Override
	public List<TGParticleSystem> getParticleSysOffhand() {
		return this.particleSysOH;
	}

	@Override
	public List<ITGParticle> getOrInitEntityParticles() {
		if (this.entityParticles==null) this.entityParticles = new LinkedList<ITGParticle>();
		return this.entityParticles;
	}

	@Override
	public List<TGParticleSystem> getOrInitParticleSysMainhand() {
		if(this.particleSysMH==null) this.particleSysMH = new LinkedList<>();
		return this.particleSysMH;
	}

	@Override
	public List<TGParticleSystem> getOrInitParticleSysOffhand() {
		if(this.particleSysOH==null) this.particleSysOH = new LinkedList<>();
		return this.particleSysOH;
	}

	@Override
	public List<ITGParticle> getEntityParticlesMH() {
		return this.entityParticlesMH;
	}

	@Override
	public List<ITGParticle> getEntityParticlesOH() {
		return this.entityParticlesOH;
	}

	@Override
	public List<ITGParticle> getOrInitEntityParticlesOH() {
		if(this.entityParticlesOH==null) this.entityParticlesOH = new LinkedList<ITGParticle>();
		return this.entityParticlesOH;
	}

	@Override
	public List<ITGParticle> getOrInitEntityParticlesMH() {
		if(this.entityParticlesMH==null) this.entityParticlesMH = new LinkedList<ITGParticle>();
		return this.entityParticlesMH;
	}

	/**
	 * Nothing attached anymore, the entry can be dropped
	 */
	public boolean isEmpty() {
		return (entityParticles == null || entityParticles.isEmpty()) && (particleSysMH == null || particleSysMH.isEmpty())
				&& (particleSysOH == null || particleSysOH.isEmpty()) && (entityParticlesMH == null || entityParticlesMH.isEmpty())
				&& (entityParticlesOH == null || entityParticlesOH.isEmpty());
	}
}
