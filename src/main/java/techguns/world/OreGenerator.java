package techguns.world;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import techguns.TGConfig;

/**
 * 1.12 OreGenerator (IWorldGenerator) is now datapack world generation:
 * data/techguns/worldgen/configured_feature/ore_*.json (vein size), data/techguns/worldgen/placed_feature/ore_*.json
 * (tries per chunk, height range) and the forge biome modifier data/techguns/forge/biome_modifier/ores.json (overworld biomes).
 * <p>
 * 1.12 values: copper 5-7 blocks x12 tries Y 5-80, tin 4-6 x10 Y 5-60, lead 4-5 x8 Y 5-50, uranium 4-5 x4 Y 4-24,
 * titanium 4-7 x5 Y 4-32. The port uses the average vein size and the same tries and heights (all inside the
 * stone layer of the 1.18+ world, so the rarity does not change).
 * <p>
 * The doOreGenX config values are checked by the placement modifier "techguns:ore_config" (ConfigFilter).
 */
public class OreGenerator {

	public static final PlacementModifierType<ConfigFilter> CONFIG_FILTER = () -> ConfigFilter.CODEC;

	/**
	 * Placement filter that only places the feature if the given ore is enabled in the config
	 */
	public static class ConfigFilter extends PlacementFilter {

		public static final Codec<ConfigFilter> CODEC = Codec.STRING.fieldOf("ore").xmap(ConfigFilter::new, f -> f.ore).codec();

		protected final String ore;

		public ConfigFilter(String ore) {
			this.ore = ore;
		}

		@Override
		protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
			return isOreEnabled(this.ore);
		}

		@Override
		public PlacementModifierType<?> type() {
			return CONFIG_FILTER;
		}
	}

	public static boolean isOreEnabled(String ore) {
		switch (ore) {
		case "copper":
			return TGConfig.doOreGenCopper;
		case "tin":
			return TGConfig.doOreGenTin;
		case "lead":
			return TGConfig.doOreGenLead;
		case "uranium":
			return TGConfig.doOreGenUranium;
		case "titanium":
			return TGConfig.doOreGenTitanium;
		default:
			return true;
		}
	}
}
