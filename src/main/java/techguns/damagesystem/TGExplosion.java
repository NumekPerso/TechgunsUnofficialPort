package techguns.damagesystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.entities.projectiles.GenericProjectile;

/**
 * Techguns explosion with a primary (full damage) and a secondary (falloff) radius. Blocks are only destroyed when
 * blockDamageFactor &gt;= 0.01 (projectiles pass 0 when the shooter is in safe mode / blockdamage is off).
 * Server side only.
 */
public class TGExplosion {

	boolean damagesTerrain;
	Random random;
	Level world;
	double x;
	double y;
	double z;
	Entity exploder;
	Entity projectile;
	/** Blocks affected by this explosion, with their distance to the center */
	HashMap<BlockPos, Double> affectedBlockPositions;

	Vec3 position;

	double primaryRadius;
	double primaryDamage;
	double secondaryRadius;
	double secondaryDamage;
	double blockDamageFactor;
	public float blockDropChance = 0.25f;

	Explosion explosionDummy;
	TGDamageSource dmgSrc = null;

	public TGExplosion(Level world, Entity exploder, Entity projectile, double x, double y, double z, double primaryDamage, double secondaryDamage, double primaryRadius, double secondaryRadius, double blockDamageFactor) {
		this.random = new Random();
		this.affectedBlockPositions = new HashMap<>();
		this.world = world;
		this.exploder = exploder;
		this.projectile = projectile;

		this.x = x;
		this.y = y;
		this.z = z;

		this.primaryDamage = primaryDamage;
		this.secondaryDamage = secondaryDamage;
		this.primaryRadius = primaryRadius;
		this.secondaryRadius = secondaryRadius;

		this.blockDamageFactor = blockDamageFactor;
		this.damagesTerrain = (blockDamageFactor >= 0.01);

		this.position = new Vec3(this.x, this.y, this.z);

		this.explosionDummy = new Explosion(world, exploder, x, y, z, (float) Math.max(primaryRadius, secondaryRadius), false, this.damagesTerrain ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP);
	}

	public TGExplosion setDmgSrc(TGDamageSource src) {
		this.dmgSrc = src;
		return this;
	}

	public Explosion getExplosionDummy() {
		return explosionDummy;
	}

	/**
	 * Does the explosion (destroy blocks, damage entities)
	 */
	public void doExplosion(boolean playSound) {
		if (playSound) this.world.playSound((Player) null, this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);

		HashMap<BlockPos, Double> set = new HashMap<BlockPos, Double>();

		double totalRadius = Math.max(primaryRadius, secondaryRadius);
		int radius = (int) Math.ceil(totalRadius);

		double stepOffset = 0.30000001192092896D;
		int steps = (int) Math.ceil((double) radius / stepOffset);

		for (int j = -radius; j < radius; ++j) {
			for (int k = -radius; k < radius; ++k) {
				for (int l = -radius; l < radius; ++l) {
					if (j == -radius || j == radius - 1 || k == -radius || k == radius - 1 || l == -radius || l == radius - 1) {
						double dx = (double) ((float) j / (float) radius);
						double dy = (double) ((float) k / (float) radius);
						double dz = (double) ((float) l / (float) radius);
						//normalize
						double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
						dx = dx / length;
						dy = dy / length;
						dz = dz / length;
						double px = 0;
						double py = 0;
						double pz = 0;

						double explosionDamping = 0.0;
						double explosionPower = this.primaryDamage;

						BlockPos prevPos = null;

						for (int i = 0; i < steps && explosionPower > 0.0; i++) {
							BlockPos blockpos = BlockPos.containing(x + px, y + py, z + pz);
							BlockState iblockstate = this.world.getBlockState(blockpos);

							double distance = this.position.distanceTo(new Vec3(blockpos.getX() + 0.5, blockpos.getY() + 0.5, blockpos.getZ() + 0.5));
							if (distance <= primaryRadius) explosionPower = primaryDamage;
							else if (distance <= secondaryRadius) explosionPower = secondaryDamage + ((distance - primaryRadius) / (secondaryRadius - primaryRadius)) * (primaryDamage - secondaryDamage);
							else explosionPower = 0.0;

							float resistance = 0.0f;
							if (!iblockstate.isAir()) {
								resistance = iblockstate.getExplosionResistance(world, blockpos, explosionDummy);

								if (explosionPower - explosionDamping > 0.0f && resistance < (explosionPower - explosionDamping) * blockDamageFactor && (this.exploder == null || this.exploder.shouldBlockExplode(explosionDummy, this.world, blockpos, iblockstate, (float) explosionPower))) {
									set.put(blockpos, distance);
									if (prevPos == null || !(prevPos.getX() == blockpos.getX() && prevPos.getY() == blockpos.getY() && prevPos.getZ() == blockpos.getZ())) {
										explosionDamping += resistance;
									}
								} else {
									explosionPower = 0.0;
								}
							}

							px += dx * stepOffset;
							py += dy * stepOffset;
							pz += dz * stepOffset;

							prevPos = blockpos;
						}
					}
				}
			}
		}

		this.affectedBlockPositions.putAll(set);
		float f3 = (float) (totalRadius);
		int k1 = Mth.floor(this.x - (double) f3 - 1.0D);
		int l1 = Mth.floor(this.x + (double) f3 + 1.0D);
		int i2 = Mth.floor(this.y - (double) f3 - 1.0D);
		int i1 = Mth.floor(this.y + (double) f3 + 1.0D);
		int j2 = Mth.floor(this.z - (double) f3 - 1.0D);
		int j1 = Mth.floor(this.z + (double) f3 + 1.0D);
		List<Entity> list = this.world.getEntities(this.projectile, new AABB((double) k1, (double) i2, (double) j2, (double) l1, (double) i1, (double) j1));
		ForgeEventFactory.onExplosionDetonate(this.world, explosionDummy, list, f3);

		breakBlocks();

		TGDamageSource tgs;
		if (this.dmgSrc == null) {
			tgs = TGDamageSource.causeExplosionDamage(projectile, exploder, DeathType.GORE);
		} else {
			tgs = TGDamageSource.copyWithNewEnt(this.dmgSrc, projectile, exploder);
		}

		for (int k2 = 0; k2 < list.size(); ++k2) {
			Entity entity = list.get(k2);

			if (!entity.ignoreExplosion() && GenericProjectile.BULLET_TARGETS.test(entity)) {

				double damage;

				//Check distance
				double distance = this.position.distanceTo(new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()));
				if (distance <= primaryRadius) damage = primaryDamage;
				else if (distance <= secondaryRadius) damage = secondaryDamage + ((distance - primaryRadius) / (secondaryRadius - primaryRadius)) * (primaryDamage - secondaryDamage);
				else damage = 0.0;

				//trace blocks
				if (damage > 0.0) {
					Vec3 start = this.position;
					Vec3 end = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight() * 0.5, entity.getZ());

					HitResult rtr = world.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.projectile != null ? this.projectile : entity));
					if (rtr != null && rtr.getType() == HitResult.Type.BLOCK) damage = 0.0;
				}

				if (damage > 0.0) {
					float f = 1.0f;
					if (exploder != null && exploder instanceof LivingEntity && entity instanceof LivingEntity) {
						f = DamageSystem.getDamageFactor((LivingEntity) exploder, (LivingEntity) entity);
					}
					entity.hurt(tgs, (float) Math.max(0, damage * f));
				}
			}
		}
	}

	private void breakBlocks() {
		if (this.damagesTerrain) {
			double r = (this.secondaryRadius - this.primaryRadius);

			for (Map.Entry<BlockPos, Double> entry : this.affectedBlockPositions.entrySet()) {
				BlockPos blockpos = entry.getKey();

				BlockState iblockstate = this.world.getBlockState(blockpos);

				if (!iblockstate.isAir()) {
					if (entry.getValue() > this.primaryRadius && Math.random() < (blockDropChance * (entry.getValue() - this.primaryRadius) / r)) {
						if (iblockstate.canDropFromExplosion(this.world, blockpos, explosionDummy)) {
							BlockEntity be = iblockstate.hasBlockEntity() ? this.world.getBlockEntity(blockpos) : null;
							Block.dropResources(iblockstate, this.world, blockpos, be);
						}
					}
					iblockstate.onBlockExploded(this.world, blockpos, this.explosionDummy);
				}
			}
		}
	}
}
