package techguns.entities.projectiles;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import techguns.TGBlocks;
import techguns.TGEntities;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IChargedProjectileFactory;
import techguns.blocks.BlockBioblob;
import techguns.tileentities.BioBlobTileEnt;

/**
 * Places / grows bio blob blocks (TGBlocks.BIOBLOB) on block hits (placing only when blockdamage is on).
 */
public class BioGunProjectile extends GenericProjectile {

	/**
	 * charge level (ammo consumed), synced to the client
	 */
	public int level;

	public BioGunProjectile(Level worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage, float speed, int TTL, float spread, float dmgDropStart,
			float dmgDropEnd, float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity, int level) {
		super(TGEntities.BIOGUN_PROJECTILE, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.level = level;
		this.gravity = gravity;
	}

	public BioGunProjectile(Level par2World, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
			float penetration, boolean blockdamage, EnumBulletFirePos leftGun, double gravity, int level) {
		super(TGEntities.BIOGUN_PROJECTILE, par2World, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, leftGun);
		this.level = level;
		this.gravity = gravity;
	}

	public BioGunProjectile(EntityType<? extends GenericProjectile> type, Level worldIn) {
		super(type, worldIn);
		if (worldIn.isClientSide) {
			Techguns.proxy.createFXOnEntity("BioGunTrail", this);
		}
	}

	@Override
	protected void onHitEffect(LivingEntity ent, HitResult rayTraceResult) {
		ent.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 3, false, true));
		doImpactEffects(null, rayTraceResult, null);
	}

	@Override
	protected TGDamageSource getProjectileDamageSource() {
		TGDamageSource src = TGDamageSource.causePoisonDamage(this, this.shooter, DeathType.BIO);
		src.goreChance = 1.0f;
		src.armorPenetration = this.penetration;
		src.setNoKnockback();
		return src;
	}

	/**
	 * 1.12 played the sound (distance delayed) and created the FX directly, only has an effect on the client
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

		this.level().playLocalSound(x, y, z, TGSounds.BIOGUN_IMPACT, SoundSource.AMBIENT, 1.0f, 1.0f, distdelay);
		Techguns.proxy.createFX("biogunImpact", this.level(), x, y, z, 0.0D, 0.0D, 0.0D, angles[0], angles[1]);
	}

	@Override
	protected void hitBlock(BlockHitResult mop) {
		BlockState statehit = this.level().getBlockState(mop.getBlockPos());

		if (statehit.getBlock() == TGBlocks.BIOBLOB) {
			//Hit Bioblob, increase size
			BlockEntity tile = this.level().getBlockEntity(mop.getBlockPos());
			if (tile instanceof BioBlobTileEnt blob) {
				blob.hitBlob(level, this.shooter);
			}
		} else {
			if (!this.level().isClientSide) {
				BlockPos blobPos = mop.getBlockPos().relative(mop.getDirection());

				if (this.level().isEmptyBlock(blobPos)) {
					if (this.blockdamage) {
						boolean canPlace = true;

						if (this.shooter instanceof Player) {
							canPlace = !ForgeEventFactory.onBlockPlace(this.shooter, BlockSnapshot.create(this.level().dimension(), this.level(), blobPos), mop.getDirection());
						}

						if (canPlace) {
							BlockState state = TGBlocks.BIOBLOB.defaultBlockState().setValue(BlockBioblob.FACING_ALL, mop.getDirection().getOpposite()).setValue(BlockBioblob.SIZE, 0);
							this.level().setBlock(blobPos, state, 3);
							if (this.level > 1) {
								BlockEntity tile = this.level().getBlockEntity(blobPos);
								if (tile instanceof BioBlobTileEnt blob) {
									blob.hitBlob(level - 1, this.shooter);
								}
							}
						}
					}
				} else {
					BlockEntity tile = this.level().getBlockEntity(blobPos);
					if (tile instanceof BioBlobTileEnt blob) {
						blob.hitBlob(level, this.shooter);
					}
				}
			}
		}

		super.hitBlock(mop);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isRemoved()) {
			return;
		}
		if (this.isInWater()) {
			for (int l = 0; l < 4; ++l) {
				float f4 = 0.25F;
				this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - this.motionX * (double) f4, this.getY() - this.motionY * (double) f4, this.getZ() - this.motionZ * (double) f4, this.motionX, this.motionY, this.motionZ);
			}
			this.discard();
		}
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		super.writeSpawnData(buffer);
		buffer.writeByte((byte) this.level);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		super.readSpawnData(additionalData);
		this.level = additionalData.readByte();
	}

	public int getChargeLevel() {
		return level;
	}

	public static class Factory implements IChargedProjectileFactory<BioGunProjectile> {

		@Override
		public BioGunProjectile createProjectile(GenericGun gun, Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity) {
			return this.createChargedProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, radius, gravity, 0f, 1);
		}

		@Override
		public DamageType getDamageType() {
			return DamageType.POISON;
		}

		@Override
		public BioGunProjectile createChargedProjectile(Level world, LivingEntity p, float damage, float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd,
				float dmgMin, float penetration, boolean blockdamage, EnumBulletFirePos firePos, float radius, double gravity, float charge, int ammoConsumed) {
			return new BioGunProjectile(world, p, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos, gravity, ammoConsumed);
		}
	}
}
