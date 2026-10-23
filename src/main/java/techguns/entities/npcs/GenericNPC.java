package techguns.entities.npcs;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import techguns.TGArmors;
import techguns.TGuns;
import techguns.api.npc.INPCTechgunsShooter;
import techguns.api.npc.INpcTGDamageSystem;
import techguns.api.npc.factions.ITGNpcTeam;
import techguns.api.npc.factions.TGNpcFaction;
import techguns.damagesystem.DamageSystem;
import techguns.damagesystem.TGDamageSource;
import techguns.entities.ai.EntityAIHurtByTargetTGFactions;
import techguns.entities.ai.EntityAIRangedAttack;
import techguns.items.guns.GenericGun;

/**
 * Base class of the Techguns humanoid NPCs: shoots with the Techguns gun in its main hand (ranged AI of the gun), melee
 * attack otherwise. Uses the Techguns armor calculation (INpcTGDamageSystem).
 */
public class GenericNPC extends Monster implements RangedAttackMob, INPCTechgunsShooter, INpcTGDamageSystem, ITGNpcTeam, ITGSpawnerNPC {

	protected boolean hasAimedBowAnim;

	private static final EntityDataAccessor<Boolean> SWINGING_ARMS = SynchedEntityData.defineId(GenericNPC.class, EntityDataSerializers.BOOLEAN);
	private EntityAIRangedAttack aiRangedAttack = null;
	private MeleeAttackGoal aiAttackOnCollide = null;

	boolean tryLink = true;
	protected TGSpawnerNPCData spawnerData = new TGSpawnerNPCData();

	protected float armor = 0f;
	protected float penetrationResistance = 0f;

	public GenericNPC(EntityType<? extends GenericNPC> type, Level world) {
		super(type, world);
		this.aiAttackOnCollide = new MeleeAttackGoal(this, 1.2D, false);

		if (world != null && !world.isClientSide) {
			this.setCombatTask();
		}
		hasAimedBowAnim = true;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new EntityAIHurtByTargetTGFactions(this, false));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SWINGING_ARMS, false);
	}

	/**
	 * 1.12 GenericNPC.applyEntityAttributes
	 */
	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D);
	}

	/**
	 * Attributes of a NPC (1.12 applyEntityAttributes), on top of {@link #createAttributes()}
	 */
	public static AttributeSupplier.Builder createAttributes(double speed, double health, double damage, double followRange) {
		return createAttributes().add(Attributes.MOVEMENT_SPEED, speed).add(Attributes.MAX_HEALTH, health).add(Attributes.ATTACK_DAMAGE, damage).add(Attributes.FOLLOW_RANGE, followRange);
	}

	public boolean getHasAimedBowAnim() {
		return hasAimedBowAnim;
	}

	protected GenericGun pickRandomGun(int difficulty) {
		Random r = new Random();
		GenericGun gun;
		switch (r.nextInt(4)) {
		case 0:
			gun = TGuns.revolver;
			break;
		case 1:
			gun = TGuns.thompson;
			break;
		case 2:
			gun = TGuns.as50;
			break;
		case 3:
			gun = TGuns.grimreaper;
			break;
		case 4:
		default:
			gun = TGuns.handcannon;
		}
		return gun;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.VILLAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.VILLAGER_DEATH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.VILLAGER_AMBIENT;
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
	 * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
	 */
	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		livingdata = super.finalizeSpawn(level, difficulty, reason, livingdata, tag);
		this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
		this.setCombatTask();
		this.setCanPickUpLoot(false);
		return livingdata;
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
		int d = Math.round(difficulty.getSpecialMultiplier() * 3f);
		this.addRandomArmor(d);
	}

	protected void addRandomArmor(int difficulty) {
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(this.pickRandomGun(difficulty)));

		this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TGArmors.t1_combat_Helmet));
		this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TGArmors.t1_combat_Chestplate));
		this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(TGArmors.t1_combat_Leggings));
		this.setItemSlot(EquipmentSlot.FEET, new ItemStack(TGArmors.t1_combat_Boots));
	}

	/**
	 * Called by the TGSpawnManager instead of finalizeSpawn
	 */
	public void onSpawnByManager(int difficulty) {
		this.setCanPickUpLoot(false);
		this.addRandomArmor(difficulty);
		this.setCombatTask();
	}

	/**
	 * sets this entity's combat AI.
	 */
	public void setCombatTask() {
		if (this.aiAttackOnCollide == null || this.level() == null || this.level().isClientSide) {
			return;
		}
		this.goalSelector.removeGoal(this.aiAttackOnCollide);
		if (this.aiRangedAttack != null) {
			this.goalSelector.removeGoal(this.aiRangedAttack);
		}
		ItemStack itemstack = this.getMainHandItem();

		if (!itemstack.isEmpty() && itemstack.getItem() instanceof GenericGun) {
			GenericGun gun = (GenericGun) itemstack.getItem();
			this.aiRangedAttack = gun.getAIAttack(this);
			this.goalSelector.addGoal(4, this.aiRangedAttack);
		} else {
			this.goalSelector.addGoal(4, this.aiAttackOnCollide);
		}
	}

	/**
	 * The combat AI follows the held weapon (e.g. weapon set by a spawner after spawning)
	 */
	@Override
	public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
		super.setItemSlot(slot, stack);
		if (slot == EquipmentSlot.MAINHAND && this.level() != null && !this.level().isClientSide) {
			this.setCombatTask();
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

				Difficulty difficulty = this.level().getDifficulty();
				float acc = 1.0f;
				float dmg = 1.0f;
				switch (difficulty) {
				case EASY:
					acc = 1.3f;
					dmg = 0.6f;
					break;
				case NORMAL:
					acc = 1.15f;
					dmg = 0.8f;
					break;
				case HARD:
					acc = 1.0f;
					dmg = 1.0f;
					break;
				default:
					break;
				}

				((GenericGun) gun).fireWeaponFromNPC(this, dmg, acc);
			}
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.spawnerData.readFromEntityNBT(tag);
		this.setCombatTask();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		this.spawnerData.writeToEntityNBT(tag);
	}

	/**
	 * Returns the Y Offset of this entity.
	 */
	@Override
	public double getMyRidingOffset() {
		return super.getMyRidingOffset() - 0.5D;
	}

	@Override
	public float getWeaponPosX() {
		return 0;
	}

	@Override
	public float getWeaponPosY() {
		return 0;
	}

	@Override
	public float getWeaponPosZ() {
		return 0;
	}

	protected void setTGArmorStats(float armor, float penetrationResistance) {
		this.armor = armor;
		this.penetrationResistance = penetrationResistance;
	}

	@Override
	public float getTotalArmorAgainstType(TGDamageSource dmgsrc) {
		return DamageSystem.getArmorAgainstDamageTypeDefault(this, this.armor, dmgsrc.damageType);
	}

	@Override
	public float getPenetrationResistance(TGDamageSource dmgsrc) {
		return this.penetrationResistance;
	}

	/**
	 * Techguns armor calculation (1.12 TGEventHandler.onLivingHurt -> DamageSystem.livingHurt)
	 */
	@Override
	protected float getDamageAfterArmorAbsorb(DamageSource source, float amount) {
		return DamageSystem.getDamageAfterArmorAbsorbNPC(this, source, amount);
	}

	@Override
	public TGNpcFaction getTGFaction() {
		return TGNpcFaction.HOSTILE;
	}

	public void setSwingingArms(boolean swingingArms) {
		this.entityData.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
	}

	public boolean isSwingingArms() {
		return this.entityData.get(SWINGING_ARMS).booleanValue();
	}

	@Override
	public void checkDespawn() {
		super.checkDespawn();
		this.despawnEntitySpawner(this.level(), this.isRemoved());
	}

	@Override
	public void die(DamageSource cause) {
		super.die(cause);
		this.onDeathSpawner(this.level(), this.dead);
	}

	@Override
	public void tick() {
		super.tick();
		this.onUpdateSpawner(this.level());
	}

	@Override
	public boolean getTryLink() {
		return this.tryLink;
	}

	@Override
	public void setTryLink(boolean value) {
		this.tryLink = value;
	}

	@Override
	public TGSpawnerNPCData getSpawnerData() {
		return this.spawnerData;
	}

}
