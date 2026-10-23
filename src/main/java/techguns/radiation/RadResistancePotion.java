package techguns.radiation;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Radiation resistance (+1 per level, attribute modifier added by TGRadiationSystem).
 * Icon: textures/mob_effect/radresistance.png (1.12 drew it from the TG inventory texture)
 */
public class RadResistancePotion extends MobEffect {

	public RadResistancePotion() {
		//1.12 setBeneficial()
		super(MobEffectCategory.BENEFICIAL, 0x99ac2d);
	}

}
