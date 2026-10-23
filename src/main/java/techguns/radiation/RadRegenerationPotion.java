package techguns.radiation;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import techguns.capabilities.TGExtendedPlayer;

/**
 * Removes (amplifier+1) RAD each second. Icon: textures/mob_effect/radregeneration.png (1.12 drew it from the TG inventory texture)
 */
public class RadRegenerationPotion extends MobEffect {

	public RadRegenerationPotion() {
		//1.12 color 0xffa2000 (the extra digit was ignored by the potion color rendering)
		super(MobEffectCategory.BENEFICIAL, 0xfa2000);
	}

	@Override
	public void applyEffectTick(LivingEntity elb, int amplifier) {

		int amount = (amplifier + 1);

		//server only in the port, the RAD level is synced to the client
		if (elb instanceof Player ply && !elb.level().isClientSide) {
			TGExtendedPlayer props = TGExtendedPlayer.get(ply);
			if (props != null) {
				props.addRadiation(-amount);
			}
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}
}
