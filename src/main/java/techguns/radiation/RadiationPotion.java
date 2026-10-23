package techguns.radiation;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGSounds;
import techguns.api.radiation.TGRadiation;
import techguns.capabilities.TGExtendedPlayer;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.util.MathUtil;

/**
 * Radiation effect: each second (amplifier+1 - radiation resistance) RAD for players, damage for mobs.
 * The 1.12 inventory/HUD icon (drawn from the TG inventory texture) is the mob effect texture textures/mob_effect/radiation.png.
 */
public class RadiationPotion extends MobEffect {

	public RadiationPotion() {
		super(MobEffectCategory.HARMFUL, 0xf8ff00);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		//Rad can't be cured by milk buckets, return new list
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		return ret;
	}

	public static int getRadResistance(LivingEntity elb) {
		if (TGRadiation.RADIATION_RESISTANCE == null) {
			return 0;
		}
		AttributeInstance inst = elb.getAttribute(TGRadiation.RADIATION_RESISTANCE);
		return inst != null ? (int) inst.getValue() : 0;
	}

	@Override
	public void applyEffectTick(LivingEntity elb, int amplifier) {

		int res = getRadResistance(elb);

		int amount = MathUtil.clamp(amplifier + 1 - res, 0, 1000);

		if (elb instanceof Player ply) {
			if (!ply.getAbilities().instabuild) {
				//the port only changes the RAD level on the server, it is synced to the client (1.12 did it on both sides)
				if (!elb.level().isClientSide) {
					TGExtendedPlayer props = TGExtendedPlayer.get(ply);
					if (props != null) {
						props.addRadiation(amount);
					}
				}

				if (elb.level().isClientSide && amount > 0) {
					if (amplifier >= 2) {
						elb.level().playSound(ply, elb.getX(), elb.getY(), elb.getZ(), TGSounds.GEIGER_HIGH, SoundSource.PLAYERS, 1f, 1f);
					} else {
						elb.level().playSound(ply, elb.getX(), elb.getY(), elb.getZ(), TGSounds.GEIGER_LOW, SoundSource.PLAYERS, 1f, 1f);
					}
				}
			}
		} else {
			if (elb instanceof Mob && !elb.level().isClientSide) {
				float damage = amount * 0.5f;
				if (damage >= 1.0f) {
					elb.hurt(TGDamageSource.causeRadiationDamage(null, null, DeathType.BIO), damage);
				}
			}
		}

	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}

}
