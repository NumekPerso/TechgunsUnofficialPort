package techguns.entities.projectiles;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGExplosion;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IChargedProjectileFactory;
import techguns.packets.PacketPlaySound;
import techguns.packets.PacketSpawnParticle;
import techguns.util.TGSoundCategory;

public class TFGProjectile extends GenericProjectile {

	/**
	 * Size of the plasma ball (depends on the charge), synced to the client
	 */
	public float size = 1.0f;

	public TFGProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage, float speed, int TTL, float spread, float dmgDropStart,
			float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity, float size) {
		super(TGEntities.TFG_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.size = size;
		this.gravity = gravity;
	}

	public TFGProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity, float size) {
		super(TGEntities.TFG_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.size = size;
		this.gravity = gravity;
	}

	public TFGProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResult) {
		this.explode();
	}

	@Override
	protected void hitBlock(BlockHitResult raytraceResultIn) {
		this.explode();
	}

	protected void explode() {
		if (!this.level().isClientSide) {
			float exp_dmgMax = this.damage * 0.66f * size;
			float exp_dmgMin = this.damage * 0.33f * size;
			float exp_r1 = size * 1.0f;
			float exp_r2 = size * 2.0f;

			TGPackets.network.sendToAllAround(new PacketSpawnParticle("TFGExplosion", this.getX(), this.getY(), this.getZ(), size * 0.75f), TGPackets.targetPointAroundEnt(this, 100.0f));

			TGExplosion explosion = new TGExplosion(this.level(), this.shooter, this, getX(), getY(), getZ(), exp_dmgMax, exp_dmgMin, exp_r1, exp_r2, this.blockdamage ? 0.5 : 0.0);
			explosion.blockDropChance = 0.1f;

			explosion.doExplosion(false);
			TGPackets.network.sendToAllAround(new PacketPlaySound(TGSounds.TFG_EXPLOSION, (int) this.getX(), (int) this.getY(), (int) this.getZ(), 5.0f, 1.0f, false, TGSoundCategory.EXPLOISON), TGPackets.targetPointAroundEnt(this, 200.0f));

			if (this.size > 3.0f) {
				TGPackets.network.sendToAllAround(new PacketPlaySound(TGSounds.TFG_EXPLOSION_ECHO, (int) this.getX(), (int) this.getY(), (int) this.getZ(), 10.0f, 1.0f, false, TGSoundCategory.EXPLOISON), TGPackets.targetPointAroundEnt(this, 200.0f));
			}

		} else {
			Techguns.proxy.createLightPulse(this.getX(), this.getY(), this.getZ(), 5, 15, 5f + (size), 1f + (size * 0.5f), 0.5f, 1.0f, 0.5f);
		}
		this.discard();
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		super.writeSpawnData(buffer);
		buffer.writeFloat(this.size);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		super.readSpawnData(additionalData);
		this.size = additionalData.readFloat();
		Techguns.proxy.createFXOnEntity("TFGTrail", this, this.size * 0.75f);
	}

	public float getSize() {
		return size;
	}

	public static class Factory implements IChargedProjectileFactory<TFGProjectile> {

		@Override
		public TFGProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return this.createChargedProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, radius, gravity, 0f, 1);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.ENERGY;
		}

		@Override
		public TFGProjectile createChargedProjectile(Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity, float charge, int ammoConsumed) {
			TFGProjectile proj = new TFGProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, gravity, ammoConsumed);
			proj.size = 1.0f + (charge * 3.0f);
			if (charge >= 1.0f) proj.size += 1.0f;
			return proj;
		}
	}
}
