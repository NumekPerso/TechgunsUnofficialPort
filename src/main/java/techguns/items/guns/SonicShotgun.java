package techguns.items.guns;

import java.util.ArrayList;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import techguns.TGPackets;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.SonicShotgunProjectile;
import techguns.packets.PacketSpawnParticleOnEntity;
import techguns.util.MathUtil;

/**
 * Fires a main projectile and 2 rings of projectiles (5 and 10) around it, all sharing the list of hit entities
 */
public class SonicShotgun extends GenericGun {

	@SuppressWarnings("rawtypes")
	public SonicShotgun(boolean addToGunList, String name, ProjectileSelector projectile_selector, boolean semiAuto,
			int minFiretime, int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound,
			int TTL, float accuracy) {
		super(addToGunList, name, projectile_selector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound,
				reloadsound, TTL, accuracy);
	}

	@SuppressWarnings("rawtypes")
	public SonicShotgun(String name, ProjectileSelector projectileSelector, boolean semiAuto, int minFiretime,
			int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound, int TTL,
			float accuracy) {
		super(name, projectileSelector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound, reloadsound, TTL,
				accuracy);
	}

	@Override
	protected void spawnProjectile(Level world, LivingEntity player, ItemStack itemstack, float spread,
			float offset, float damagebonus, EnumBulletFirePos firePos, Entity target) {

		int count = 5; //Base projectile count per ring
		int rings = 2; //number of rings

		spread = 0.075f;

		//Center
		ArrayList<Entity> entitiesHit = new ArrayList<Entity>();
		SonicShotgunProjectile proj = new SonicShotgunProjectile(world, player, damage * damagebonus, speed, this.ticksToLive, 0f, this.damageDropStart, this.damageDropEnd, this.damageMin * damagebonus, this.penetration, getDoBlockDamage(player), firePos);
		proj.entitiesHit = entitiesHit;
		proj.mainProjectile = true;
		if (offset > 0.0f) {
			proj.shiftForward(offset);
		}
		world.addFreshEntity(proj);
		TGPackets.network.sendToAllAround(new PacketSpawnParticleOnEntity("SonicShotgunTrail", proj), TGPackets.targetPointAroundEnt(proj, 100.0f));

		//Rings
		for (int j = 1; j <= rings; j++) {

			float angle = (float) ((Math.PI * 2.0) / (double) (count * j));
			for (int i = 0; i < (count * j); i++) {

				proj = new SonicShotgunProjectile(world, player, damage * damagebonus, speed, this.ticksToLive, 0f, this.damageDropStart, this.damageDropEnd, this.damageMin * damagebonus, this.penetration, getDoBlockDamage(player), firePos);
				proj.entitiesHit = entitiesHit;
				Vec3 dir = new Vec3(1, 0, 0);

				dir = dir.yRot(spread * j);
				dir = dir.xRot(angle * i);

				dir = MathUtil.rotateVec3AroundZ(dir, (float) (player.getXRot() * MathUtil.D2R));

				dir = dir.yRot((float) ((-player.getYHeadRot() - 90.0) * MathUtil.D2R));

				proj.motionX = dir.x * speed;
				proj.motionY = dir.y * speed;
				proj.motionZ = dir.z * speed;
				proj.setDeltaMovement(proj.motionX, proj.motionY, proj.motionZ);

				if (offset > 0.0f) {
					proj.shiftForward(offset);
				}

				world.addFreshEntity(proj);
			}
		}
	}
}
