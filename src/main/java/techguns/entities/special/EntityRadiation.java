package techguns.entities.special;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.PushReaction;
import techguns.TGRadiationSystem;

/**
 * Invisible entity that radiates the entities around it (left by nukes), 1.12 EntityRadiation
 */
public class EntityRadiation extends Entity {

	//every second apply rad
	public static final int RAD_INTERVAL = 20;

	//lifetime in ticks
	protected int duration;

	protected float radius;
	protected float inner_radius;

	protected int strength;
	protected int strength_outer;

	protected int strength_original;
	protected int strength_outer_original;

	public EntityRadiation(EntityType<? extends EntityRadiation> type, Level worldIn) {
		super(type, worldIn);
		this.noPhysics = true;
	}

	public EntityRadiation(Level worldIn, double posX, double posY, double posZ, int duration, float innter_radius, int strength, float radius, int strength_outer) {
		this(TGRadiationSystem.RADIATION_ENTITY, worldIn);
		this.setPos(posX, posY, posZ);
		this.setRadiationValues(duration, innter_radius, strength, radius, strength_outer);
	}

	public EntityRadiation(Entity otherEntPos, int duration, float innter_radius, int strength, float radius, int strength_outer) {
		this(otherEntPos.level(), otherEntPos.getX(), otherEntPos.getY(), otherEntPos.getZ(), duration, innter_radius, strength, radius, strength_outer);
	}

	public EntityRadiation(BlockEntity tilePos, int duration, float innter_radius, int strength, float radius, int strength_outer) {
		this(tilePos.getLevel(), tilePos.getBlockPos().getX() + 0.5, tilePos.getBlockPos().getY() + 0.5, tilePos.getBlockPos().getZ() + 0.5, duration, innter_radius, strength, radius, strength_outer);
	}

	public EntityRadiation setRadiationValues(int duration, float innter_radius, int strength, float radius, int strength_outer) {
		this.duration = duration;
		this.inner_radius = innter_radius;
		this.strength = strength;
		this.radius = radius;
		this.strength_outer = strength_outer;
		this.strength_original = strength;
		this.strength_outer_original = strength_outer;
		return this;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.tickCount = compound.getInt("Age");
		this.duration = compound.getInt("Duration");

		this.radius = compound.getFloat("radius");
		this.inner_radius = compound.getFloat("inner_radius");

		this.strength = compound.getShort("strength");
		this.strength_outer = compound.getShort("strength_outer");

		this.strength_original = compound.getShort("strength_original");
		this.strength_outer_original = compound.getShort("strength_outer_original");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("Age", this.tickCount);
		compound.putInt("Duration", this.duration);

		compound.putFloat("radius", this.radius);
		compound.putFloat("inner_radius", this.inner_radius);

		compound.putShort("strength", (short) this.strength);
		compound.putShort("strength_outer", (short) this.strength_outer);

		compound.putShort("strength_original", (short) this.strength_original);
		compound.putShort("strength_outer_original", (short) this.strength_outer_original);
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public boolean isIgnoringBlockTriggers() {
		return true;
	}

	@Override
	public void tick() {
		//1.12 onEntityUpdate (no movement)
		this.baseTick();

		if (this.level().isClientSide) {
			return;
		}

		if (this.tickCount > this.duration) {
			this.discard();
			return;
		}

		if (this.tickCount % RAD_INTERVAL == 0) {
			//apply rad to nearby entities

			//update strength;
			if (this.tickCount * 2 >= this.duration) {

				double factor = 1d - ((this.tickCount - this.duration * 0.5) / (this.duration * 0.5));
				this.strength = Math.max((int) Math.round(this.strength_original * factor), 0);
				this.strength_outer = Math.max((int) Math.round(this.strength_outer_original * factor), 0);

			}

			TGRadiationSystem.applyRadToEntities(this.level(), this.getX(), this.getY(), this.getZ(), this.radius, RAD_INTERVAL + 2, this.strength, this.inner_radius, this.strength_outer);
		}
	}

}
