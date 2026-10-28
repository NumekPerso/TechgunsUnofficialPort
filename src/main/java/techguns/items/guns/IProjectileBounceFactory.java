package techguns.items.guns;

import techguns.entities.projectiles.GenericProjectile;

public interface IProjectileBounceFactory<T extends GenericProjectile> extends IProjectileFactory<T> {

	public T createBounceProjectile(T proj, double bounceX, double bounceY, double bounceZ);

	/**
	 * Bounces of the projectiles created by createProjectile (gun tooltip)
	 */
	public int getMaxBounces();
}
