package techguns.deatheffects;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Server and client side, needed by server to know to send out packets
 */
public class EntityDeathUtils {

	public static HashMap<DeathType, HashSet<EntityType<?>>> entityDeathTypes;
	public static HashSet<EntityType<?>> goreMap = new HashSet<>();

	static {
		entityDeathTypes = new HashMap<>();

		//Gore
		goreMap.add(EntityType.PLAYER);
		goreMap.add(EntityType.ZOMBIE);
		goreMap.add(EntityType.SKELETON);
		goreMap.add(EntityType.ENDERMAN);
		goreMap.add(EntityType.CREEPER);
		goreMap.add(EntityType.COW);
		goreMap.add(EntityType.SHEEP);
		goreMap.add(EntityType.PIG);
		goreMap.add(EntityType.CHICKEN);
		goreMap.add(EntityType.ZOMBIFIED_PIGLIN);
		goreMap.add(EntityType.SPIDER);
		goreMap.add(EntityType.CAVE_SPIDER);
		goreMap.add(EntityType.WITCH);
		goreMap.add(EntityType.SLIME);
		goreMap.add(EntityType.HORSE);
		goreMap.add(EntityType.MOOSHROOM);
		goreMap.add(EntityType.WOLF);
		goreMap.add(EntityType.SQUID);
		goreMap.add(EntityType.VILLAGER);
		goreMap.add(EntityType.LLAMA);
		goreMap.add(EntityType.EVOKER);
		goreMap.add(EntityType.HUSK);
		goreMap.add(EntityType.POLAR_BEAR);
		goreMap.add(EntityType.MAGMA_CUBE);
		goreMap.add(EntityType.PARROT);
		goreMap.add(EntityType.RABBIT);
		goreMap.add(EntityType.STRAY);
		goreMap.add(EntityType.SILVERFISH);
		goreMap.add(EntityType.VINDICATOR);
		goreMap.add(EntityType.VEX);
		goreMap.add(EntityType.SHULKER);
		goreMap.add(EntityType.WITHER_SKELETON);
		goreMap.add(EntityType.GHAST);
		goreMap.add(EntityType.ZOMBIE_VILLAGER);
		goreMap.add(EntityType.DONKEY);
		goreMap.add(EntityType.MULE);
		//added for newer vanilla mobs
		goreMap.add(EntityType.DROWNED);
		goreMap.add(EntityType.PILLAGER);
		goreMap.add(EntityType.PIGLIN);
		goreMap.add(EntityType.PIGLIN_BRUTE);
		goreMap.add(EntityType.HOGLIN);
		goreMap.add(EntityType.ZOGLIN);
		goreMap.add(EntityType.RAVAGER);
		goreMap.add(EntityType.FOX);
		goreMap.add(EntityType.GOAT);
		goreMap.add(EntityType.WANDERING_TRADER);

		entityDeathTypes.put(DeathType.GORE, goreMap);
	}

	/**
	 * Add an entity to the gore death type list
	 */
	public static void addEntityToDeathEffectList(EntityType<?> type) {
		entityDeathTypes.get(DeathType.GORE).add(type);
	}

	public static boolean hasSpecialDeathAnim(LivingEntity entityLiving, DeathType deathtype) {
		if (deathtype == DeathType.BIO || deathtype == DeathType.LASER) return true;

		//GenericGore
		if (entityDeathTypes.get(DeathType.GORE).contains(entityLiving.getType())) {
			return true;
		}
		return false;
	}

	public enum DeathType {
		DEFAULT(0), GORE(1), BIO(2), LASER(3), DISMEMBER(4);

		int value;

		private DeathType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
