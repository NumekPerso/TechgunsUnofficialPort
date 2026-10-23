package techguns.entities.npcs;

import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import techguns.Techguns;
import techguns.api.npc.INPCTechgunsShooter;
import techguns.api.npc.factions.ITGNpcTeam;
import techguns.api.npc.factions.TGNpcFaction;
import techguns.entities.ai.EntityAIHurtByTargetTGFactions;
import techguns.entities.ai.EntityAIRangedAttack;
import techguns.entities.ai.TurretEntityAINearestAttackableTarget;
import techguns.entities.ai.TurretEntityAIWatchClosest;
import techguns.factions.TGNpcFactions;
import techguns.items.guns.GenericGun;
import techguns.tileentities.TurretTileEnt;

/**
 * The turret on top (or below) of a turret block: an immobile mob that shoots with the gun in the turret block,
 * using the ammo in its inventory. Spawned and linked by TurretTileEnt.
 * 1.12 IAnimals was a marker interface (animals and mobs), not needed in 1.20.
 */
public class NPCTurret extends PathfinderMob implements RangedAttackMob, INPCTechgunsShooter, ITGNpcTeam {

	private static final ResourceLocation[] textures = new ResourceLocation[] {
			new ResourceLocation(Techguns.MODID, "textures/blocks/turret_base.png"),
			new ResourceLocation(Techguns.MODID, "textures/blocks/turret_base2.png"),
			new ResourceLocation(Techguns.MODID, "textures/blocks/turret_base3.png"),
			new ResourceLocation(Techguns.MODID, "textures/blocks/turret_base4.png"),
			new ResourceLocation(Techguns.MODID, "textures/blocks/turret_base5.png")
	};

	/**
	 * Facing synced to the client (renderer, eye height)
	 */
	private static final EntityDataAccessor<Byte> DATA_FACING = SynchedEntityData.defineId(NPCTurret.class, EntityDataSerializers.BYTE);

	protected TurretEntityAIWatchClosest aiWatch = null;
	protected RandomLookAroundGoal aiIdle = null;
	protected EntityAIHurtByTargetTGFactions aiHurt = null;
	protected TurretEntityAINearestAttackableTarget<LivingEntity> aiTarget = null;

	private EntityAIRangedAttack aiRangedAttack = null;

	public TurretTileEnt mountedTileEnt = null;

	public boolean active = true;
	protected Direction turretFacing = Direction.UP;

	public NPCTurret(EntityType<? extends NPCTurret> type, Level world) {
		super(type, world);

		this.setAITasks();
		this.setNoGravity(true);

		this.mountedTileEnt = null;
	}

	public NPCTurret(Level world, TurretTileEnt mountedTileEnt) {
		this(TGNpcEntities.TURRET, world);

		this.mountedTileEnt = mountedTileEnt;
		if (world != null && !world.isClientSide) {
			this.setCombatTask();
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return PathfinderMob.createMobAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
				.add(Attributes.MAX_HEALTH, 30)
				.add(Attributes.FOLLOW_RANGE, 20.0D);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_FACING, (byte) Direction.UP.get3DDataValue());
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (DATA_FACING.equals(key)) {
			this.turretFacing = Direction.from3DDataValue(this.entityData.get(DATA_FACING));
			this.refreshDimensions();
		}
	}

	public static ResourceLocation getTexture(int tier) {
		return textures[tier];
	}

	public ResourceLocation getTexture() {
		int tier = 0;
		if (this.mountedTileEnt != null) {
			tier = this.mountedTileEnt.getTurretTier();
		}
		return getTexture(tier);
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
		if (this.turretFacing == Direction.DOWN) {
			return 0.15f;
		}
		return 0.75f;
	}

	@Override
	public boolean canAttackType(EntityType<?> type) {
		return true;
	}

	/**
	 * The gun of the turret block (1.12 getHeldItemMainhand)
	 */
	@Override
	public ItemStack getItemBySlot(EquipmentSlot slot) {
		if (slot == EquipmentSlot.MAINHAND && this.mountedTileEnt != null) {
			return this.getWeapon();
		}
		return super.getItemBySlot(slot);
	}

	protected ItemStack getWeapon() {
		if (mountedTileEnt != null) {
			return mountedTileEnt.getWeapon();
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	@Override
	public void die(DamageSource damageSource) {
		super.die(damageSource);
		if (this.mountedTileEnt != null) {
			this.mountedTileEnt.onTurretDeath();
		}
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
	}

	@Override
	public int getExperienceReward() {
		return 0;
	}

	/**
	 * Sets this turrets base, called from the base on nbt read
	 */
	public void setTileEnt(TurretTileEnt ent) {
		this.mountedTileEnt = ent;
		this.setCombatTask();
	}

	@Override
	public void tick() {
		//1.12 reset the position after each update: the turret never moves
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		super.tick();
		this.setPos(x, y, z);
		this.setDeltaMovement(0, 0, 0);

		if (this.mountedTileEnt == null || this.mountedTileEnt.isRemoved()) {
			this.mountedTileEnt = null;
			//try to relink
			int bx = (int) Math.floor(this.getX());

			int by = ((int) Math.round(this.getY())) - 1;
			if (this.turretFacing == Direction.DOWN) {
				by = ((int) Math.round(this.getY())) + 1;
			}

			int bz = (int) Math.floor(this.getZ());
			BlockEntity tile = this.level().getBlockEntity(new BlockPos(bx, by, bz));
			if (tile instanceof TurretTileEnt turretTile) {
				if (!this.level().isClientSide) {
					turretTile.setMountedTurret(this);
					this.mountedTileEnt = turretTile;
					this.mountedTileEnt.needUpdate();
					this.setCombatTask();
				} else {
					//client: link to the base for rendering (1.12 requested a tile sync)
					this.mountedTileEnt = turretTile;
					turretTile.mountedTurret = this;
				}
			} else if (!this.level().isClientSide) {
				this.hurt(this.damageSources().fellOutOfWorld(), 1.0f);
			}

		} else if (!this.level().isClientSide) {
			if (this.mountedTileEnt.mountedTurret != this) {
				this.discard();
			}
		}
	}

	/**
	 * Attack the specified entity using a ranged attack.
	 */
	@Override
	public void performRangedAttack(LivingEntity target, float distance) {

		ItemStack held = this.getMainHandItem();
		if (held.isEmpty()) {
			return;
		} else {
			Item gun = held.getItem();
			if (gun instanceof GenericGun) {

				if (this.mountedTileEnt != null && this.mountedTileEnt.consumeAmmo()) {

					((GenericGun) gun).fireWeaponFromNPC(this, 1.0f, 1.0f);
				}
			}

		}

	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	@Override
	public boolean requiresCustomPersistence() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void doPush(Entity entityIn) {
	}

	@Override
	public void knockback(double strength, double x, double z) {
	}

	public void destroy() {
		this.setHealth(0);
		this.discard();
	}

	public void setAITasks() {
		if (aiWatch == null) {
			this.aiWatch = new TurretEntityAIWatchClosest(this, LivingEntity.class, 80.0F, 20.0D);
		}
		if (aiIdle == null) {
			this.aiIdle = new RandomLookAroundGoal(this);
		}
		if (aiHurt == null) {
			this.aiHurt = new EntityAIHurtByTargetTGFactions(this, false);
		}
		if (aiTarget == null) {
			Predicate<LivingEntity> selector = (LivingEntity entity) -> {
				if (entity instanceof Enemy) {
					return true;
				}

				if (NPCTurret.this.mountedTileEnt != null) {
					byte pvp = NPCTurret.this.mountedTileEnt.getPvpSetting();
					UUID owner = NPCTurret.this.mountedTileEnt.getOwner();

					if (pvp != 0 && owner != null) {

						if (entity instanceof NPCTurret) {
							NPCTurret otherTurret = (NPCTurret) entity;
							if (otherTurret.mountedTileEnt != null) {
								UUID otherOwner = otherTurret.mountedTileEnt.getOwner();
								if (otherOwner != null) {
									return TGNpcFactions.shouldAttack(owner, otherOwner, NPCTurret.this.mountedTileEnt.getPvpSetting());
								}
							}

						} else if (entity instanceof Player) {
							UUID ply = ((Player) entity).getGameProfile().getId();
							if (ply != null) {
								if (owner.equals(ply)) {
									return false;
								}
								return TGNpcFactions.shouldAttack(owner, ply, NPCTurret.this.mountedTileEnt.getPvpSetting());
							}
						}

					}
					if (NPCTurret.this.mountedTileEnt.attackAnimals && entity instanceof Animal) {
						if (entity instanceof TamableAnimal) {
							return !((TamableAnimal) entity).isTame();
						} else if (entity instanceof AbstractHorse) {
							return !((AbstractHorse) entity).isTamed();
						} else if (entity instanceof OwnableEntity) {
							return ((OwnableEntity) entity).getOwnerUUID() == null;
						}
						return true;
					}
				}
				//Fallbacks for no UUID/owner set etc...
				return false;
			};
			this.aiTarget = new TurretEntityAINearestAttackableTarget<LivingEntity>(this, LivingEntity.class, 0, true, false, selector, 20.0D);
		}
		this.goalSelector.addGoal(2, this.aiWatch);
		this.goalSelector.addGoal(3, this.aiIdle);
		this.targetSelector.addGoal(1, this.aiHurt);
		this.targetSelector.addGoal(2, this.aiTarget);
	}

	public void disable() {
		this.active = false;
		this.goalSelector.removeGoal(this.aiWatch);
		this.goalSelector.removeGoal(this.aiIdle);
		this.targetSelector.removeGoal(this.aiHurt);
		this.targetSelector.removeGoal(this.aiTarget);
		if (this.aiRangedAttack != null) {
			this.goalSelector.removeGoal(this.aiRangedAttack);
		}
		this.setTarget(null);
	}

	/**
	 * sets this entity's combat AI.
	 */
	public void setCombatTask() {
		if (this.level() == null || this.level().isClientSide) {
			return;
		}
		if (this.aiRangedAttack != null) {
			this.goalSelector.removeGoal(this.aiRangedAttack);
			this.aiRangedAttack = null;
		}
		ItemStack itemstack = this.getMainHandItem();

		if (!itemstack.isEmpty() && itemstack.getItem() instanceof GenericGun) {
			GenericGun gun = (GenericGun) itemstack.getItem();
			this.aiRangedAttack = gun.getAIAttack(this);
			this.goalSelector.addGoal(1, this.aiRangedAttack);
			this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(gun.getAI_attackRange());
		}
	}

	@Override
	public int getArmorValue() {
		if (this.mountedTileEnt != null) {
			return mountedTileEnt.getTurretArmorValue();
		} else {
			return 0;
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("turretFacing", (byte) this.turretFacing.get3DDataValue());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setTurretFacing(Direction.from3DDataValue(compound.getByte("turretFacing")));
	}

	@Override
	public float getWeaponPosX() {
		return 0.05f;
	}

	@Override
	public float getWeaponPosY() {
		return 0.7f;
	}

	@Override
	public float getWeaponPosZ() {
		return 0.8f;
	}

	@Override
	public TGNpcFaction getTGFaction() {
		return TGNpcFaction.TURRET;
	}

	public Direction getTurretFacing() {
		return turretFacing;
	}

	public void setTurretFacing(Direction turretFacing) {
		this.turretFacing = turretFacing;
		this.entityData.set(DATA_FACING, (byte) turretFacing.get3DDataValue());
		this.refreshDimensions();
	}

}
