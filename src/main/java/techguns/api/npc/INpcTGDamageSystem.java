package techguns.api.npc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import techguns.damagesystem.TGDamageSource;

/**
 * Entities implementing this use the Techguns armor calculation (armor per damage type, penetration) instead of the vanilla one.
 * Must only be implemented by subclasses of LivingEntity, which override getDamageAfterArmorAbsorb with
 * {@link techguns.damagesystem.DamageSystem#getDamageAfterArmorAbsorbNPC}.
 */
public interface INpcTGDamageSystem {
	public float getTotalArmorAgainstType(TGDamageSource dmgsrc);

	public float getPenetrationResistance(TGDamageSource dmgsrc);

	/**
	 * @param elb - should be considered "this", passed as param to allow default implementation
	 * @param src
	 * @return
	 */
	public default float getToughnessAfterPentration(LivingEntity elb, TGDamageSource src) {
		float toughness = (float) elb.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
		float pen = Math.max(src.armorPenetration - this.getPenetrationResistance(src), 0f) * 4f; //per part for players, so *4 for total
		return Math.max(toughness - pen, 0f);
	}
}
