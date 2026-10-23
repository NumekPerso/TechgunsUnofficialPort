package techguns.world;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import techguns.world.structures.WorldgenStructure;

/**
 * Spawn entry of a structure (1.12 TGStructureSpawn).
 * <p>
 * 1.20: the 1.12 weights, biome whitelists, sizes and dimensions are datapack values: structure set
 * data/techguns/worldgen/structure_set/{small,medium,big}.json (weights, spacing from the config, see
 * TGStructurePlacement), structure data/techguns/worldgen/structure/NAME.json (biome tag
 * #techguns:has_structure/..., which replaces biome whitelist + dimension + land/water type).
 */
public class TGStructureSpawn {
	public final String name;
	protected final Supplier<WorldgenStructure> factory;
	public final int spawnWeight;
	public final StructureSize size;
	public final boolean nether;
	public final StructureLandType landType;
	protected final BooleanSupplier enabled;
	/**
	 * Is the structure in its structure set (data/techguns/worldgen/structure_set/SIZE.json, same order and weights)
	 */
	public boolean inSet;

	public TGStructureSpawn(String name, Supplier<WorldgenStructure> factory, int spawnWeight, boolean nether, StructureLandType landType, StructureSize size, BooleanSupplier enabled) {
		this.name = name;
		this.factory = factory;
		this.spawnWeight = spawnWeight;
		this.nether = nether;
		this.landType = landType;
		this.size = size;
		this.enabled = enabled;
		this.inSet = spawnWeight > 0;
	}

	/**
	 * New instance of the structure for one generation
	 */
	public WorldgenStructure create() {
		return factory.get();
	}

	public boolean isEnabled() {
		return enabled.getAsBoolean();
	}
}
