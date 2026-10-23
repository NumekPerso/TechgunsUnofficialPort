package techguns.world;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import techguns.TGConfig;

/**
 * Random spread placement whose spacing comes from the config (1.12 StructureSpawnWeightSmall/Medium/Big: one try
 * every X chunks). 1.12 tried at chunk coordinates that are multiples of X, the port uses a random chunk of every
 * X*X chunk region (same density, works with /locate). Separation = spacing / 4.
 */
public class TGStructurePlacement extends RandomSpreadStructurePlacement {

	public static final Codec<TGStructurePlacement> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.STRING.fieldOf("size").forGetter(p -> p.size),
			Codec.INT.fieldOf("salt").forGetter(p -> p.salt()),
			ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(p -> p.exclusionZone())
	).apply(i, TGStructurePlacement::new));

	protected final String size;

	public TGStructurePlacement(String size, int salt, Optional<ExclusionZone> exclusionZone) {
		super(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 1.0f, salt, exclusionZone, getSpacing(size), Math.max(1, getSpacing(size) / 4), RandomSpreadType.LINEAR);
		this.size = size;
	}

	/**
	 * Placement of the techguns structure sets (same salts as data/techguns/worldgen/structure_set/*.json)
	 */
	public static TGStructurePlacement forSize(StructureSize size) {
		switch (size) {
		case BIG:
			return new TGStructurePlacement("big", 1612345703, Optional.empty());
		case MEDIUM:
			return new TGStructurePlacement("medium", 1612345702, Optional.empty());
		default:
			return new TGStructurePlacement("small", 1612345701, Optional.empty());
		}
	}

	public static int getSpacing(String size) {
		switch (size) {
		case "big":
			return Math.max(2, TGConfig.spawnWeightTGStructureBig);
		case "medium":
			return Math.max(2, TGConfig.spawnWeightTGStructureMedium);
		default:
			return Math.max(2, TGConfig.spawnWeightTGStructureSmall);
		}
	}

	@Override
	public StructurePlacementType<?> type() {
		return TGWorldgen.TG_PLACEMENT_TYPE;
	}
}
