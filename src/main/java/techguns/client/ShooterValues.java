package techguns.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import techguns.Techguns;
import techguns.api.capabilities.AttackTime;
import techguns.capabilities.TGExtendedPlayer;

/**
 * Access to recoil / reload / muzzle flash timings of shooters. Common code (no client classes used).
 */
public class ShooterValues {

	protected static AttackTime getAttackTimes(LivingEntity ent, boolean offHand) {
		if (ent instanceof Player) {
			TGExtendedPlayer caps = TGExtendedPlayer.get((Player) ent);
			if (caps != null) {
				return caps.getAttackTime(offHand);
			}
		}
		return null;
	}

	public static long getRecoiltime(LivingEntity ent, boolean offHand) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			return attack.getRecoilTime();
		}
		return 0L;
	}

	public static boolean isStillRecoiling(LivingEntity ent, boolean offHand, byte attacktype) {
		if (!Techguns.proxy.isClientPlayerAndIn1stPerson(ent)) {
			return false;
		}

		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack == null) {
			return false;
		} else {
			if (attack.getAttackType() != attacktype) {
				return false;
			} else if (attack.getRecoilTime() <= 0) {
				return false;
			} else {
				return (System.currentTimeMillis() - attack.getRecoilTime()) < 0;
			}
		}
	}

	public static void setRecoiltime(LivingEntity ent, boolean offHand, long time, int total, byte attacktype) {
		setRecoiltime(ent, offHand, time, total, attacktype, 0f);
	}

	public static void setRecoiltime(LivingEntity ent, boolean offHand, long time, int total, byte attacktype, float chargeProgress) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			attack.setRecoilTime(time);
			attack.setRecoilTimeTotal(total);
			attack.setAttackType(attacktype);
			attack.setRecoilChargeProgress(chargeProgress);
		}
	}

	public static long getRecoiltimeTotal(LivingEntity ent, boolean offHand) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			return attack.getRecoilTimeTotal();
		}
		return 0L;
	}

	public static long getReloadtime(LivingEntity ent, boolean offHand) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			return attack.getReloadTime();
		}
		return 0L;
	}

	public static byte getAttackType(LivingEntity ent, boolean offHand) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			return attack.getAttackType();
		}
		return (byte) 0;
	}

	public static void setReloadtime(LivingEntity ent, boolean offHand, long time, int total, byte attackType) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			attack.setReloadTime(time);
			attack.setReloadTimeTotal(total);
			attack.setAttackType(attackType);
		}
	}

	public static int getReloadtimeTotal(LivingEntity ent, boolean offHand) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			return attack.getReloadTimeTotal();
		}
		return 0;
	}

	public static boolean getIsCurrentlyUsingGun(LivingEntity ent, boolean offHand) {
		AttackTime attack = getAttackTimes(ent, offHand);
		return attack != null && (attack.isReloading() || attack.isRecoiling());
	}

	public static boolean getPlayerIsReloading(LivingEntity ent, boolean offHand) {
		AttackTime attack = getAttackTimes(ent, offHand);
		return attack != null && attack.isReloading();
	}

	public static void setMuzzleFlashTime(LivingEntity ent, boolean offHand, long time, int total) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			attack.setMuzzleFlashTime(time);
			attack.setMuzzleFlashTimeTotal(total);
		}
	}

	public static long getMuzzleFlashTime(LivingEntity ent, boolean offHand) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			return attack.getMuzzleFlashTime();
		}
		return 0L;
	}

	public static int getMuzzleFlashTimeTotal(LivingEntity ent, boolean offHand) {
		AttackTime attack = getAttackTimes(ent, offHand);
		if (attack != null) {
			return attack.getMuzzleFlashTimeTotal();
		}
		return 0;
	}
}
