package techguns.entities.projectiles;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.DamageSystem;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.packets.PacketSpawnParticle;

/**
 * A sonic shotgun shot is a main projectile and rings of invisible projectiles sharing the list of hit entities, each entity is only hit once.
 * The main projectile is rendered (1.12 render pass 1), the others are not (mainProjectile is synced in the spawn data).
 */
public class SonicShotgunProjectile extends GenericProjectile {

	public ArrayList<Entity> entitiesHit;

	public boolean mainProjectile = false;

	public SonicShotgunProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch,
			float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos firePos) {
		super(TGEntities.SONIC_SHOTGUN_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
				blockdamage, firePos);
	}

	public SonicShotgunProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage,
			EnumBulletFirePos firePos) {
		super(TGEntities.SONIC_SHOTGUN_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
	}

	public SonicShotgunProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = new TGDamageSource("tg_sonic", this, shooter, DamageType.PHYSICAL, DeathType.GORE);
		src.ignoreHurtresistTime = true;
		src.armorPenetration = this.penetration;
		src.setKnockback(2.0f);
		src.goreChance = 1.0f;
		return src;
	}

	@Override
	protected void doImpactEffects(@Nullable BlockState state, HitResult rayTraceResult, @Nullable SoundType sound) {
		if (mainProjectile) {
			if (!this.level().isClientSide) {
				TGPackets.network.sendToAllAround(new PacketSpawnParticle("SonicShotgunImpact", this.getX(), this.getY(), this.getZ()), TGPackets.targetPointAroundEnt(this, 50.0f));
			}
		}
	}

	@Override
	protected void onHit(HitResult mop) {
		if (mop instanceof EntityHitResult ehr) {
			Entity hit = ehr.getEntity();
			if (!this.level().isClientSide && entitiesHit != null && !entitiesHit.contains(hit)) {
				TGDamageSource src = getProjectileDamageSource();
				TGDamageSource knockback = TGDamageSource.getKnockbackDummyDmgSrc(this, this.shooter);

				if (hit instanceof LivingEntity ent) {
					float dmg = DamageSystem.getDamageFactor(this.shooter, ent);
					if (dmg > 0) {

						ent.hurt(knockback, 0.01f);
						ent.hurt(src, this.getDamage() * dmg);
						if (src.wasSuccessful()) {

							if (ent instanceof Mob) {
								this.setAIRevengeTarget(((Mob) ent));
							}

							this.onHitEffect(ent, mop);
						}
					}
				} else {
					hit.hurt(src, this.getDamage());
				}

				entitiesHit.add(hit);
			}
		} else if (mop instanceof BlockHitResult bhr && mop.getType() == HitResult.Type.BLOCK) {
			this.hitBlock(bhr);
			this.discard();
		}
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		super.writeSpawnData(buffer);
		buffer.writeBoolean(mainProjectile);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		super.readSpawnData(additionalData);
		this.mainProjectile = additionalData.readBoolean();
	}

	public boolean isMainProjectile() {
		return mainProjectile;
	}

	public static class Factory implements IProjectileFactory<SonicShotgunProjectile> {

		@Override
		public SonicShotgunProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new SonicShotgunProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.PHYSICAL;
		}
	}
}
