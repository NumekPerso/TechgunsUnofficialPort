package techguns.entities.npcs;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import techguns.api.npc.ITGNpcSpawner;

/**
 * Spawner link of an NPC (1.12 capability TGSpawnerNPCData). In 1.20 the data is a field of the NPC entities (see
 * {@link ITGSpawnerNPC#getSpawnerData()}), saved with the entity NBT.
 */
public class TGSpawnerNPCData {

	public static final String NBT_KEY = "techguns_spawner";

	protected BlockPos spawnerPos;

	public boolean hasSpawner() {
		return spawnerPos != null;
	}

	@Nullable
	public BlockPos getSpawnerPos() {
		return spawnerPos;
	}

	public void setSpawnerPos(BlockPos spawnerPos) {
		this.spawnerPos = spawnerPos;
	}

	@Nullable
	public static TGSpawnerNPCData get(ITGSpawnerNPC npc) {
		return npc.getSpawnerData();
	}

	public void tryRelink(Level w, ITGSpawnerNPC npc) {
		if (this.hasSpawner()) {
			BlockEntity tile = w.getBlockEntity(this.getSpawnerPos());
			if (tile instanceof ITGNpcSpawner spawner) {
				spawner.relinkNPC(npc);
			}
		}
	}

	public void saveToNBT(final CompoundTag tags) {
		if (spawnerPos != null) {
			tags.putBoolean("hasSpawner", true);
			tags.putInt("spawnerX", spawnerPos.getX());
			tags.putInt("spawnerY", spawnerPos.getY());
			tags.putInt("spawnerZ", spawnerPos.getZ());
		}
	}

	public void loadFromNBT(final CompoundTag tags) {
		if (tags.getBoolean("hasSpawner")) {
			int x = tags.getInt("spawnerX");
			int y = tags.getInt("spawnerY");
			int z = tags.getInt("spawnerZ");
			this.spawnerPos = new BlockPos(x, y, z);
		} else {
			this.spawnerPos = null;
		}
	}

	/**
	 * Entity NBT helpers (1.12: capability storage)
	 */
	public void writeToEntityNBT(CompoundTag entityTag) {
		CompoundTag tags = new CompoundTag();
		this.saveToNBT(tags);
		entityTag.put(NBT_KEY, tags);
	}

	public void readFromEntityNBT(CompoundTag entityTag) {
		if (entityTag.contains(NBT_KEY)) {
			this.loadFromNBT(entityTag.getCompound(NBT_KEY));
		}
	}
}
