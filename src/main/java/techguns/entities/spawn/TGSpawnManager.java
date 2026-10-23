package techguns.entities.spawn;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.TGConfig;
import techguns.Techguns;
import techguns.entities.npcs.GenericNPC;
import techguns.entities.npcs.TGDummySpawn;
import techguns.entities.npcs.TGNpcEntities;

/**
 * Handles spawning of Techguns NPCs: the biome modifiers add the TGDummySpawn to the monster spawns (1.12 TGEntities.postInit),
 * the dummy is replaced by a NPC depending on the distance to the world spawn and the biome (1.12 TGEventHandler.onEntityJoinWorld).
 * BiomeDictionary types are replaced by the Forge / vanilla biome tags.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID)
public class TGSpawnManager {

	public static TGNpcSpawnTable spawnTableOverworld = new TGNpcSpawnTable(5);
	public static TGNpcSpawnTable spawnTableNether = new TGNpcSpawnTable(5);

	protected static Random rnd = new Random();

	/**
	 * 1.12 TGEntities.init (the dimension of a spawn is given by its table)
	 */
	public static void init() {
		spawnTableOverworld.clear();
		spawnTableNether.clear();
		spawnTableOverworld.registerSpawn(new TGNpcSpawn(() -> TGNpcEntities.ZOMBIE_SOLDIER, () -> TGConfig.spawnWeightZombieSoldier), 1);
		spawnTableOverworld.registerSpawn(new TGNpcSpawn(() -> TGNpcEntities.ZOMBIE_FARMER, () -> TGConfig.spawnWeightZombieFarmer), 0);
		spawnTableOverworld.registerSpawn(new TGNpcSpawn(() -> TGNpcEntities.ZOMBIE_MINER, () -> TGConfig.spawnWeightZombieMiner), 0);
		spawnTableOverworld.registerSpawn(new TGNpcSpawn(() -> TGNpcEntities.SKELETON_SOLDIER, () -> TGConfig.spawnWeightSkeletonSoldier), 1);

		spawnTableOverworld.registerSpawn(new TGNpcSpawn(() -> TGNpcEntities.PSYCHO_STEVE, () -> TGConfig.spawnWeightPsychoSteve), 1);

		spawnTableOverworld.registerSpawn(new TGNpcSpawn(() -> TGNpcEntities.BANDIT, () -> TGConfig.spawnWeightBandit), 2);

		spawnTableNether.registerSpawn(new TGNpcSpawn(() -> TGNpcEntities.ZOMBIE_PIGMAN_SOLDIER, () -> TGConfig.spawnWeightZombiePigmanSoldier), 0);
		spawnTableNether.registerSpawn(new TGNpcSpawn(() -> TGNpcEntities.CYBER_DEMON, () -> TGConfig.spawnWeightCyberDemon), 0);
	}

	/**
	 * 1.12 TGEventHandler.onEntityJoinWorld: the dummy never joins the level
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof TGDummySpawn && !event.getLevel().isClientSide) {
			event.setCanceled(true);
			if (!event.loadedFromDisk() && event.getLevel() instanceof ServerLevel level) {
				handleSpawn(level, event.getEntity());
			}
		}
	}

	public static void handleSpawn(ServerLevel w, Entity dummyEnt) {

		GenericNPC entNew = null;

		Holder<Biome> biome = w.getBiome(BlockPos.containing(Math.round(dummyEnt.getX()), 64, Math.round(dummyEnt.getZ())));

		if (isBlacklisted(biome)) {
			dummyEnt.discard();
			return;
		}

		int dist_danger = getDistanceDanger(w, dummyEnt);
		int biome_danger = getBiomeDanger(w, dummyEnt, biome);

		int danger = dist_danger + biome_danger;

		TGNpcSpawn chosenSpawn = null;

		TGNpcSpawnTable spawntable = getSpawnTableForDimension(w);

		int totalweight = 0;
		//calculate possible weights
		for (int d = 0; d <= danger; d++) {

			ArrayList<TGNpcSpawn> list = spawntable.get(d);
			for (int i = 0; i < list.size(); i++) {
				TGNpcSpawn spawn = list.get(i);
				totalweight += spawn.getWeightForBiome(biome);
			}

		}

		if (totalweight > 0) {

			int roll = rnd.nextInt(totalweight);

			totalweight = 0;

			for (int d = 0; d <= danger; d++) {
				ArrayList<TGNpcSpawn> list = spawntable.get(d);
				for (int i = 0; i < list.size(); i++) {
					TGNpcSpawn spawn = list.get(i);
					int weight = spawn.getWeightForBiome(biome);
					totalweight += weight;
					//1.12 used totalweight>=roll, which could pick a weight 0 entry
					if (weight > 0 && totalweight > roll) {
						chosenSpawn = spawn;
						break;
					}
				}
				if (chosenSpawn != null) {
					break;
				}
			}

			if (chosenSpawn != null) {
				try {
					entNew = chosenSpawn.getType().create(w);
					if (entNew != null) {
						setPositionsAndReplace(w, entNew, dummyEnt, danger);
					}
				} catch (Exception e) {
					Techguns.LOGGER.error("Could not spawn Techguns NPC", e);
				} finally {
					dummyEnt.discard();
				}
			} else {
				dummyEnt.discard();
			}

		} else {
			if (dummyEnt != null) {
				dummyEnt.discard();
			}
		}

	}

	protected static boolean isBlacklisted(Holder<Biome> biome) {
		for (String s : TGConfig.biomeBlacklist) {
			if (biome.unwrapKey().map(k -> k.location().toString().equals(s)).orElse(false)) {
				return true;
			}
		}
		return false;
	}

	private static int getBiomeDanger(Level w, Entity ent, Holder<Biome> biome) {

		if (biome.is(BiomeTags.IS_NETHER)) {
			return 0;
		}
		if (biome.is(BiomeTags.IS_END)) {
			return 0;
		}

		if (biome.is(Tags.Biomes.IS_WASTELAND)) {
			return 2;
		}
		if (biome.is(Tags.Biomes.IS_SANDY)) {
			return 2;
		}
		if (biome.is(Tags.Biomes.IS_SNOWY)) {
			return 2;
		}
		if (biome.is(Tags.Biomes.IS_SWAMP)) {
			return 2;
		}
		if (biome.is(Tags.Biomes.IS_SPOOKY)) {
			return 2;
		}
		if (biome.is(Tags.Biomes.IS_DEAD)) {
			return 2;
		}

		if (biome.is(Tags.Biomes.IS_COLD)) {
			return 1;
		}
		if (biome.is(Tags.Biomes.IS_SPARSE)) {
			return 1;
		}
		if (biome.is(BiomeTags.IS_SAVANNA)) {
			return 1;
		}
		if (biome.is(Tags.Biomes.IS_DRY)) {
			return 1;
		}

		return 0;
	}

	protected static TGNpcSpawnTable getSpawnTableForDimension(Level w) {
		if (w.dimension() == Level.NETHER) {
			return spawnTableNether;
		}
		return spawnTableOverworld;
	}

	private static int getDistanceDanger(ServerLevel w, Entity ent) {
		BlockPos spawnPos = w.getSharedSpawnPos();
		double dx = ent.getX() - spawnPos.getX();
		double dz = ent.getZ() - spawnPos.getZ();

		double distance = Math.sqrt(dx * dx + dz * dz);

		float factor = 1;

		//if nether distance is halfed
		if (w.dimension() == Level.NETHER) {
			factor = 0.5f;
		}

		if (distance < TGConfig.distanceSpawnLevel0 * factor) {
			return 0;
		} else if (distance < TGConfig.distanceSpawnLevel1 * factor) {
			return 1;
		} else if (distance < TGConfig.distanceSpawnLevel2 * factor) {
			return 2;
		} else {
			return 3;
		}

	}

	private static void setPositionsAndReplace(ServerLevel w, GenericNPC newNpc, Entity entity, int difficulty) {
		newNpc.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), 0.0f);
		newNpc.onSpawnByManager(difficulty);
		w.addFreshEntity(newNpc);

		entity.discard();
	}

}
