package techguns.entities.spawn;

import java.util.ArrayList;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import techguns.entities.npcs.GenericNPC;

public class TGNpcSpawn {

	protected Supplier<? extends EntityType<? extends GenericNPC>> type;
	/**
	 * read when spawning, so config changes apply directly (1.12: int read at init)
	 */
	protected IntSupplier spawnWeight;

	/**
	 * Restricted to these biomes, null = all biomes
	 */
	ArrayList<ResourceKey<Biome>> biomeWhitelist;

	ArrayList<ResourceKey<Level>> dimensionIDs;

	public TGNpcSpawn(Supplier<? extends EntityType<? extends GenericNPC>> type, IntSupplier spawnWeight) {
		this.type = type;
		this.spawnWeight = spawnWeight;
		this.biomeWhitelist = null;
		this.dimensionIDs = new ArrayList<>(1);
		this.dimensionIDs.add(Level.OVERWORLD);
	}

	@SafeVarargs
	public TGNpcSpawn(Supplier<? extends EntityType<? extends GenericNPC>> type, IntSupplier spawnWeight, ResourceKey<Biome>... biomes) {
		this(type, spawnWeight);
		this.biomeWhitelist = new ArrayList<>(biomes.length);
		for (int i = 0; i < biomes.length; i++) {
			this.biomeWhitelist.add(biomes[i]);
		}
	}

	public TGNpcSpawn(Supplier<? extends EntityType<? extends GenericNPC>> type, IntSupplier spawnWeight, ArrayList<ResourceKey<Level>> dimensions) {
		this(type, spawnWeight);
		this.dimensionIDs = dimensions;
	}

	public int getSpawnWeight() {
		return Math.max(0, this.spawnWeight.getAsInt());
	}

	public EntityType<? extends GenericNPC> getType() {
		return this.type.get();
	}

	public int getWeightForBiome(Holder<Biome> biome) {
		if (this.biomeWhitelist == null) {
			return this.getSpawnWeight();
		} else {
			for (ResourceKey<Biome> key : this.biomeWhitelist) {
				if (biome.is(key)) {
					return this.getSpawnWeight();
				}
			}
			return 0;
		}
	}

	public boolean dimensionMatches(Level w) {
		if (this.dimensionIDs == null || this.dimensionIDs.contains(w.dimension())) {
			return true;
		}
		return false;
	}
}
