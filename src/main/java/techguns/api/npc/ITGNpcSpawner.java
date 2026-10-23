package techguns.api.npc;

import techguns.entities.npcs.ITGSpawnerNPC;

/**
 * Implemented by the block entity of the Techguns NPC spawner (1.12 TGSpawnerTileEnt): the NPCs it spawned notify it when they
 * die, despawn or are loaded again (relink). The NPC side is {@link ITGSpawnerNPC} / {@link techguns.entities.npcs.TGSpawnerNPCData}:
 * after spawning an NPC, the spawner calls {@code TGSpawnerNPCData.get(npc).setSpawnerPos(pos)}.
 */
public interface ITGNpcSpawner {

	/**
	 * The NPC despawned (1.12 despawnedEntity)
	 */
	public void despawnedEntity(ITGSpawnerNPC ent);

	/**
	 * The NPC was killed (1.12 killedEntity)
	 */
	public void killedEntity(ITGSpawnerNPC ent);

	/**
	 * The NPC was loaded from disk and links itself to its spawner again (1.12 relinkNPC)
	 */
	public void relinkNPC(ITGSpawnerNPC ent);
}
