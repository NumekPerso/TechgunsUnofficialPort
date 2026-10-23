package techguns.api.radiation;

import net.minecraft.world.entity.ai.attributes.Attribute;

public class TGRadiation {
	/**
	 * Radiation is calculated as Level of Radiation effect - radiation resistance.
	 * this field is initialized when the class TGRadiationSystem is loaded (registered in the ATTRIBUTE registry event),
	 * every LivingEntity has it.
	 */
	public static Attribute RADIATION_RESISTANCE = null;
}
