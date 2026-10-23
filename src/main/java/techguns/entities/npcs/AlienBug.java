package techguns.entities.npcs;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.api.npc.INpcTGDamageSystem;
import techguns.api.npc.factions.ITGNpcTeam;
import techguns.api.npc.factions.TGNpcFaction;
import techguns.damagesystem.DamageSystem;
import techguns.damagesystem.TGDamageSource;
import techguns.packets.PacketPlaySound;
import techguns.util.TGSoundCategory;

public class AlienBug extends Spider implements ITGNpcTeam, INpcTGDamageSystem, ITGSpawnerNPC {

	protected int attackTimer;

	/**
	 * 1.12: no loot table (TODO in 1.12)
	 */
	protected static final ResourceLocation LOOT = null; //TODO //new ResourceLocation(Techguns.MODID, "entities/alienbug");

	protected boolean tryLink = true;
	protected TGSpawnerNPCData spawnerData = new TGSpawnerNPCData();

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return LOOT != null ? LOOT : BuiltInLootTables.EMPTY;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Spider.createAttributes().add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 1.0D);
	}

	public AlienBug(EntityType<? extends AlienBug> type, Level w) {
		super(type, w);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return TGSounds.ALIENBUG_IDLE;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return TGSounds.ALIENBUG_HURT;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(TGSounds.ALIENBUG_STEP, 0.15F, 1.0F);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return TGSounds.ALIENBUG_DEATH;
	}

	public int getAttackTimer() {
		return this.attackTimer;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.attackTimer > 0) {
			--this.attackTimer;
		}
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
		this.goalSelector.addGoal(4, new AlienBug.AISpiderAttack(this));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new AlienBug.AIAlienBugTarget<>(this, Player.class));
		this.targetSelector.addGoal(3, new AlienBug.AIAlienBugTarget<>(this, IronGolem.class));
	}

	static class AIAlienBugTarget<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
		public AIAlienBugTarget(AlienBug bug, Class<T> classTarget) {
			super(bug, classTarget, true);
		}

		@Override
		public void start() {
			super.start();
			if (!this.mob.level().isClientSide) {
				TGPackets.network.sendToAllAround(new PacketPlaySound(TGSounds.ALIENBUG_AGGRO, this.mob, 1.0f, 1.0f, false, false, TGSoundCategory.HOSTILE), TGPackets.targetPointAroundEnt(mob, 24.0f));
			}
		}

	}

	static class AISpiderAttack extends MeleeAttackGoal {
		public AISpiderAttack(Spider spider) {
			super(spider, 1.0D, true);
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean canContinueToUse() {
			float f = this.mob.getLightLevelDependentMagicValue();

			if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
				this.mob.setTarget((LivingEntity) null);
				return false;
			} else {
				return super.canContinueToUse();
			}
		}

		@Override
		protected double getAttackReachSqr(LivingEntity attackTarget) {
			return (double) (4.0F + attackTarget.getBbWidth());
		}
	}

	@Override
	public boolean doHurtTarget(Entity ent) {
		boolean b = super.doHurtTarget(ent);
		this.attackTimer = 10;

		if (!this.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketPlaySound(TGSounds.ALIENBUG_BITE, this, 2.0f, 1.0f, false, false, TGSoundCategory.HOSTILE), TGPackets.targetPointAroundEnt(this, 24.0f));
		}

		this.level().broadcastEntityEvent(this, (byte) 4);

		return b;
	}

	@Override
	public TGNpcFaction getTGFaction() {
		return TGNpcFaction.HOSTILE;
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 4) {
			this.attackTimer = 10;
		} else {
			super.handleEntityEvent(id);
		}
	}

	@Override
	public float getTotalArmorAgainstType(TGDamageSource dmgsrc) {
		switch (dmgsrc.damageType) {
		case ENERGY:
		case EXPLOSION:
		case ICE:
		case LIGHTNING:
			return 5.0f;
		case PHYSICAL:
			return 10.0f;
		case POISON:
			return 20.0f;
		case PROJECTILE:
			return 10.0f;
		case RADIATION:
			return 20.0f;
		case FIRE:
		case UNRESISTABLE:
		default:
			return 0;
		}
	}

	@Override
	public float getPenetrationResistance(TGDamageSource dmgsrc) {
		return 0.0f;
	}

	@Override
	protected float getDamageAfterArmorAbsorb(DamageSource source, float amount) {
		return DamageSystem.getDamageAfterArmorAbsorbNPC(this, source, amount);
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
	@Nullable
	public TGSpawnerNPCData getSpawnerData() {
		return this.spawnerData;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.spawnerData.readFromEntityNBT(tag);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		this.spawnerData.writeToEntityNBT(tag);
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

	/**
	 * 1.12 forgot this for the alien bug (no relink to its spawner after a reload)
	 */
	@Override
	public void tick() {
		super.tick();
		this.onUpdateSpawner(this.level());
	}
}
