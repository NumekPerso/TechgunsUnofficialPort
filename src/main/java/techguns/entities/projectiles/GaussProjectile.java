package techguns.entities.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import techguns.TGEntities;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.packets.PacketSpawnParticle;
import techguns.packets.PacketSpawnParticleOnEntity;

/**
 * Impact effects of blocks are inherited from AdvancedBulletProjectile (same blue impact effects in 1.12)
 */
public class GaussProjectile extends AdvancedBulletProjectile {

	public GaussProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun) {
		super(TGEntities.GAUSS_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
	}

	public GaussProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
		if (worldIn.isClientSide) {
			Techguns.proxy.createFXOnEntity("GaussProjectileTrail", this);
		}
	}

	/**
	 * 1.12 created the FX directly here, this is only called on the server: the (commented out in 1.12) packet is used instead
	 */
	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResult) {
		super.onHitEffect(ent, rayTraceResult);
		double x = rayTraceResult.getLocation().x;
		double y = rayTraceResult.getLocation().y;
		double z = rayTraceResult.getLocation().z;
		if (!this.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("GaussRifleImpact_Block", x, y, z), TGPackets.targetPointAroundEnt(this, 50.0f));
		} else {
			Techguns.proxy.createFX("GaussRifleImpact_Block", this.level(), x, y, z, 0, 0, 0);
			Techguns.proxy.createLightPulse(x, y, z, 5, 10, 3.0f, 1.0f, 0.5f, 0.75f, 1f);
		}
	}

	@Override
	protected void hitBlock(BlockHitResult raytraceResultIn) {
		super.hitBlock(raytraceResultIn);
		double x = raytraceResultIn.getLocation().x;
		double y = raytraceResultIn.getLocation().y;
		double z = raytraceResultIn.getLocation().z;

		if (!this.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("GaussRifleImpact_Block", x, y, z), TGPackets.targetPointAroundEnt(this, 50.0f));
		} else {
			Techguns.proxy.createLightPulse(x, y, z, 5, 10, 3.0f, 1.0f, 0.5f, 0.75f, 1f);
		}
	}

	public static class Factory implements IProjectileFactory<GaussProjectile> {

		@Override
		public GaussProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			//-0.14 -0.09 0.5
			float offsetX = 0.0f;
			if (firePos == EnumBulletFirePos.RIGHT) offsetX = -0.14f;
			else if (firePos == EnumBulletFirePos.LEFT) offsetX = 0.14f;
			float offsetY = -0.09f;
			float offsetZ = 0.5f;

			if (!world.isClientSide) {
				TGPackets.network.sendToAllAround(new PacketSpawnParticleOnEntity("GaussFireFX", p, offsetX, offsetY, offsetZ, true), TGPackets.targetPointAroundEnt(p, 25.0f));
			}
			return new GaussProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.PROJECTILE;
		}
	}
}
