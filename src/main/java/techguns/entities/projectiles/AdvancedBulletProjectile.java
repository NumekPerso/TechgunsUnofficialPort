package techguns.entities.projectiles;

import javax.annotation.Nullable;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import techguns.TGEntities;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;

public class AdvancedBulletProjectile extends GenericProjectile {

	public AdvancedBulletProjectile(EntityType<? extends GenericProjectile> type, Level world, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun) {
		super(type, world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
	}

	public AdvancedBulletProjectile(Level world, LivingEntity p, float damage, float speed, int TTL, float spread,
			float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun) {
		this(TGEntities.ADVANCED_BULLET_PROJECTILE, world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
	}

	public AdvancedBulletProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public static class Factory implements IProjectileFactory<AdvancedBulletProjectile> {

		@Override
		public AdvancedBulletProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return new AdvancedBulletProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.PROJECTILE;
		}
	}

	/**
	 * 1.12 played the sound (with distance delay) and spawned the blue impact FX directly, both only have an effect on the client
	 */
	@Override
	protected void doImpactEffects(@Nullable BlockState state, HitResult rayTraceResult, @Nullable SoundType sound) {
		if (!this.level().isClientSide) {
			return;
		}
		double x = rayTraceResult.getLocation().x;
		double y = rayTraceResult.getLocation().y;
		double z = rayTraceResult.getLocation().z;
		boolean distdelay = true;

		float[] angles = this.getImpactAngles(rayTraceResult);
		float pitch = angles[0];
		float yaw = angles[1];

		SoundEvent impactSound;
		String fx;
		switch (getImpactTypeForSound(sound)) {
		case 0:
			impactSound = TGSounds.BULLET_IMPACT_STONE;
			fx = "Impact_BulletRock_Blue";
			break;
		case 1:
			impactSound = TGSounds.BULLET_IMPACT_WOOD;
			fx = "Impact_BulletWood_Blue";
			break;
		case 2:
			impactSound = TGSounds.BULLET_IMPACT_GLASS;
			fx = "Impact_BulletGlass_Blue";
			break;
		case 3:
			impactSound = TGSounds.BULLET_IMPACT_METAL;
			fx = "Impact_BulletMetal_Blue";
			break;
		case 4:
			impactSound = TGSounds.BULLET_IMPACT_DIRT;
			fx = "Impact_BulletDirt_Blue";
			break;
		default:
			impactSound = TGSounds.BULLET_IMPACT_DIRT;
			fx = "Impact_BulletDefault_Blue";
			break;
		}
		this.level().playLocalSound(x, y, z, impactSound, SoundSource.AMBIENT, 1.0f, 1.0f, distdelay);
		Techguns.proxy.createFX(fx, this.level(), x, y, z, 0.0D, 0.0D, 0.0D, pitch, yaw);
	}
}
