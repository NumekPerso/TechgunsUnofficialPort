package techguns.entities.npcs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Placeholder entity of the natural spawning (biome modifiers): replaced by a Techguns NPC chosen by the TGSpawnManager when it
 * joins the level (TGNpcSpawnEvents), never actually added to the world.
 */
public class TGDummySpawn extends GenericNPC {

	public TGDummySpawn(EntityType<? extends TGDummySpawn> type, Level world) {
		super(type, world);
	}

}
