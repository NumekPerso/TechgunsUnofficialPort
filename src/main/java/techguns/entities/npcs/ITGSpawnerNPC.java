package techguns.entities.npcs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import techguns.api.npc.ITGNpcSpawner;

/**
 * Interface with default methods to emulate multi-inheritance for NPC classes
 */
public interface ITGSpawnerNPC {

	boolean getTryLink();

	void setTryLink(boolean value);

	/**
	 * Spawner data of this NPC (1.12: capability)
	 */
	TGSpawnerNPCData getSpawnerData();

	/**
	 * call this in Entity tick()
	 */
	default void onUpdateSpawner(Level w) {
		if (getTryLink()) {
			setTryLink(false);
			if (!w.isClientSide) {
				TGSpawnerNPCData dat = TGSpawnerNPCData.get(this);
				if (dat != null) {
					dat.tryRelink(w, this);
				}
			}
		}
	}

	/**
	 * call this in die()
	 */
	default void onDeathSpawner(Level w, boolean dead) {
		if (!w.isClientSide && dead) {
			TGSpawnerNPCData dat = TGSpawnerNPCData.get(this);
			if (dat != null) {
				BlockPos spawner = dat.getSpawnerPos();
				if (spawner != null) {
					BlockEntity tile = w.getBlockEntity(spawner);
					if (tile instanceof ITGNpcSpawner s) {
						s.killedEntity(this);
					}
				}
			}
		}
	}

	/**
	 * call this in checkDespawn()
	 */
	default void despawnEntitySpawner(Level w, boolean dead) {
		if (!w.isClientSide && dead) {
			TGSpawnerNPCData dat = TGSpawnerNPCData.get(this);
			if (dat != null) {
				BlockPos spawner = dat.getSpawnerPos();
				if (spawner != null) {
					BlockEntity tile = w.getBlockEntity(spawner);
					if (tile instanceof ITGNpcSpawner s) {
						s.despawnedEntity(this);
					}
				}
			}
		}
	}
}
