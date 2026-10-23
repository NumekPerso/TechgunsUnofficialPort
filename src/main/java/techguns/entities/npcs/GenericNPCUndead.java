package techguns.entities.npcs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;

public abstract class GenericNPCUndead extends GenericNPC {

	public GenericNPCUndead(EntityType<? extends GenericNPCUndead> type, Level world) {
		super(type, world);
	}

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void aiStep() {
		if (this.level().isDay() && !this.level().isClientSide && this.shouldBurnInDay()) {
			float f = this.getLightLevelDependentMagicValue();

			if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.level().canSeeSky(BlockPos.containing(this.getX(), this.getEyeY(), this.getZ()))) {
				this.setSecondsOnFire(8);
			}
		}

		super.aiStep();
	}

	protected boolean shouldBurnInDay() {
		TGSpawnerNPCData data = this.getSpawnerData();
		if (data != null) {
			return !data.hasSpawner();
		}
		return true;
	}
}
