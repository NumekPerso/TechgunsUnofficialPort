package techguns.api.guns;

/**
 * Items that are charged by holding the use key (charging guns, grenades), used by the renderers to compute the charge progress
 */
public interface IChargeableItem {
	/**
	 * Ticks for a full charge
	 */
	public float getFullChargeTime();
}
