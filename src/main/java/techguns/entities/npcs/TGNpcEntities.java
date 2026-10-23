package techguns.entities.npcs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegisterEvent;
import techguns.Techguns;
import techguns.deatheffects.EntityDeathUtils;
import techguns.entities.spawn.TGSpawnManager;

/**
 * NPC entity types, their attributes and spawn eggs (1.12 TGEntities NPC part). Registry names are the lowercase 1.12 names.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TGNpcEntities {

	public static EntityType<NPCTurret> TURRET;
	public static EntityType<TGDummySpawn> DUMMY_SPAWN;
	public static EntityType<ZombieSoldier> ZOMBIE_SOLDIER;
	public static EntityType<ZombieFarmer> ZOMBIE_FARMER;
	public static EntityType<ZombieMiner> ZOMBIE_MINER;
	public static EntityType<ArmySoldier> ARMY_SOLDIER;
	public static EntityType<Bandit> BANDIT;
	public static EntityType<Commando> COMMANDO;
	public static EntityType<DictatorDave> DICTATOR_DAVE;
	public static EntityType<CyberDemon> CYBER_DEMON;
	public static EntityType<SkeletonSoldier> SKELETON_SOLDIER;
	public static EntityType<PsychoSteve> PSYCHO_STEVE;
	public static EntityType<StormTrooper> STORM_TROOPER;
	public static EntityType<Outcast> OUTCAST;
	public static EntityType<ZombiePigmanSoldier> ZOMBIE_PIGMAN_SOLDIER;
	public static EntityType<SuperMutantBasic> SUPER_MUTANT_BASIC;
	public static EntityType<SuperMutantElite> SUPER_MUTANT_ELITE;
	public static EntityType<SuperMutantHeavy> SUPER_MUTANT_HEAVY;
	public static EntityType<AttackHelicopter> ATTACK_HELICOPTER;
	public static EntityType<AlienBug> ALIEN_BUG;
	public static EntityType<Ghastling> GHASTLING;
	public static EntityType<ZombiePoliceman> ZOMBIE_POLICEMAN;

	/**
	 * Spawn eggs (1.12 registerModEntityWithEgg), in registration order
	 */
	public static final List<ForgeSpawnEggItem> SPAWN_EGGS = new ArrayList<>();

	/**
	 * 1.12 tracking range 80 blocks = 5 chunks, helicopter 200 blocks = 13 chunks, update frequency 3, no velocity updates
	 */
	protected static <T extends net.minecraft.world.entity.Entity> EntityType<T> build(RegisterEvent.RegisterHelper<EntityType<?>> helper, String name, EntityType.EntityFactory<T> factory, MobCategory cat, float width, float height, int trackingChunks, boolean fireImmune) {
		EntityType.Builder<T> b = EntityType.Builder.<T>of(factory, cat).sized(width, height).clientTrackingRange(trackingChunks).updateInterval(3).setShouldReceiveVelocityUpdates(false);
		if (fireImmune) {
			b = b.fireImmune();
		}
		EntityType<T> type = b.build(Techguns.MODID + ":" + name);
		helper.register(new ResourceLocation(Techguns.MODID, name), type);
		return type;
	}

	@SubscribeEvent
	public static void registerEntities(RegisterEvent event) {
		event.register(Registries.ENTITY_TYPE, helper -> {
			//1.12 "Turret": tracking range 80, update frequency 3, no velocity updates
			TURRET = EntityType.Builder.<NPCTurret>of(NPCTurret::new, MobCategory.MISC).sized(0.6f, 0.9f).clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(false).build(Techguns.MODID + ":turret");
			helper.register(new ResourceLocation(Techguns.MODID, "turret"), TURRET);

			//1.12 sizes: Entity default 0.6x1.8 (GenericNPC), SkeletonSoldier height 1.95, super mutants setSize(width, 2*modelScale)
			DUMMY_SPAWN = build(helper, "tgdummyspawn", TGDummySpawn::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			ZOMBIE_SOLDIER = build(helper, "zombiesoldier", ZombieSoldier::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			ZOMBIE_FARMER = build(helper, "zombiefarmer", ZombieFarmer::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			ZOMBIE_MINER = build(helper, "zombieminer", ZombieMiner::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			ARMY_SOLDIER = build(helper, "armysoldier", ArmySoldier::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			BANDIT = build(helper, "bandit", Bandit::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			COMMANDO = build(helper, "commando", Commando::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			DICTATOR_DAVE = build(helper, "dictatordave", DictatorDave::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			CYBER_DEMON = build(helper, "cyberdemon", CyberDemon::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, true);
			SKELETON_SOLDIER = build(helper, "skeletonsoldier", SkeletonSoldier::new, MobCategory.MONSTER, 0.6f, 1.95f, 5, false);
			PSYCHO_STEVE = build(helper, "psychosteve", PsychoSteve::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			STORM_TROOPER = build(helper, "stormtrooper", StormTrooper::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			OUTCAST = build(helper, "outcast", Outcast::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
			ZOMBIE_PIGMAN_SOLDIER = build(helper, "zombiepigmansoldier", ZombiePigmanSoldier::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, true);
			SUPER_MUTANT_BASIC = build(helper, "supermutantbasic", SuperMutantBasic::new, MobCategory.MONSTER, 1.0f, 2f * 1.35f, 5, false);
			SUPER_MUTANT_ELITE = build(helper, "supermutantelite", SuperMutantElite::new, MobCategory.MONSTER, 1.4f, 2f * 1.65f, 5, false);
			SUPER_MUTANT_HEAVY = build(helper, "supermutantheavy", SuperMutantHeavy::new, MobCategory.MONSTER, 1.2f, 2f * 1.5f, 5, false);
			ATTACK_HELICOPTER = build(helper, "attackhelicopter", AttackHelicopter::new, MobCategory.MONSTER, 4.0f, 4.0f, 13, true);
			ALIEN_BUG = build(helper, "alienbug", AlienBug::new, MobCategory.MONSTER, 1.1f, 1.2f, 5, false);
			GHASTLING = build(helper, "ghastling", Ghastling::new, MobCategory.MONSTER, 1.0f, 2.1f, 5, true);
			ZOMBIE_POLICEMAN = build(helper, "zombiepoliceman", ZombiePoliceman::new, MobCategory.MONSTER, 0.6f, 1.8f, 5, false);
		});

		event.register(Registries.ITEM, helper -> {
			egg(helper, "zombiesoldier", () -> ZOMBIE_SOLDIER, 0x757468, 0x38B038);
			egg(helper, "zombiefarmer", () -> ZOMBIE_FARMER, 0x757468, 0x38B038);
			egg(helper, "zombieminer", () -> ZOMBIE_MINER, 0x757468, 0x38B038);
			egg(helper, "armysoldier", () -> ARMY_SOLDIER, 0x74806e, 0x191512);
			egg(helper, "bandit", () -> BANDIT, 0x8f9d59, 0x2c3117);
			egg(helper, "commando", () -> COMMANDO, 0x191512, 0x74806e);
			egg(helper, "dictatordave", () -> DICTATOR_DAVE, 0x8f9d59, 0xA0A000);
			egg(helper, "cyberdemon", () -> CYBER_DEMON, 0xFF1111, 0x777777);
			egg(helper, "skeletonsoldier", () -> SKELETON_SOLDIER, 0x404040, 0xF0F0F0);
			egg(helper, "psychosteve", () -> PSYCHO_STEVE, 0x757468, 0xF0F0F0);
			egg(helper, "stormtrooper", () -> STORM_TROOPER, 0xF0F0F0, 0x404040);
			egg(helper, "outcast", () -> OUTCAST, 0x101010, 0xFF0000);
			egg(helper, "zombiepigmansoldier", () -> ZOMBIE_PIGMAN_SOLDIER, 0xFF1111, 0x770000);
			egg(helper, "supermutantbasic", () -> SUPER_MUTANT_BASIC, 0xc6a96b, 0x71552e);
			egg(helper, "supermutantelite", () -> SUPER_MUTANT_ELITE, 0xc6a96b, 0x61451e);
			egg(helper, "supermutantheavy", () -> SUPER_MUTANT_HEAVY, 0xc6a96b, 0x51350e);
			egg(helper, "attackhelicopter", () -> ATTACK_HELICOPTER, 0x373d23, 0x8ec0d7);
			egg(helper, "alienbug", () -> ALIEN_BUG, 0xc6a96b, 0x71552e);
			egg(helper, "ghastling", () -> GHASTLING, 0xaeaeae, 0xce81ff);
			egg(helper, "zombiepoliceman", () -> ZOMBIE_POLICEMAN, 0x303030, 0x0000FF);
		});
	}

	protected static void egg(RegisterEvent.RegisterHelper<Item> helper, String name, Supplier<? extends EntityType<? extends net.minecraft.world.entity.Mob>> type, int color1, int color2) {
		ForgeSpawnEggItem egg = new ForgeSpawnEggItem(type, color1, color2, new Item.Properties());
		helper.register(new ResourceLocation(Techguns.MODID, name + "_spawn_egg"), egg);
		SPAWN_EGGS.add(egg);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(TURRET, NPCTurret.createAttributes().build());
		event.put(DUMMY_SPAWN, GenericNPC.createAttributes().build());
		event.put(ZOMBIE_SOLDIER, ZombieSoldier.createAttributes().build());
		event.put(ZOMBIE_FARMER, ZombieFarmer.createAttributes().build());
		event.put(ZOMBIE_MINER, ZombieMiner.createAttributes().build());
		event.put(ARMY_SOLDIER, ArmySoldier.createAttributes().build());
		event.put(BANDIT, Bandit.createAttributes().build());
		event.put(COMMANDO, Commando.createAttributes().build());
		event.put(DICTATOR_DAVE, DictatorDave.createAttributes().build());
		event.put(CYBER_DEMON, CyberDemon.createAttributes().build());
		event.put(SKELETON_SOLDIER, SkeletonSoldier.createAttributes().build());
		event.put(PSYCHO_STEVE, PsychoSteve.createAttributes().build());
		event.put(STORM_TROOPER, StormTrooper.createAttributes().build());
		event.put(OUTCAST, Outcast.createAttributes().build());
		event.put(ZOMBIE_PIGMAN_SOLDIER, ZombiePigmanSoldier.createAttributes().build());
		event.put(SUPER_MUTANT_BASIC, SuperMutantBasic.createAttributes().build());
		event.put(SUPER_MUTANT_ELITE, SuperMutantElite.createAttributes().build());
		event.put(SUPER_MUTANT_HEAVY, SuperMutantHeavy.createAttributes().build());
		event.put(ATTACK_HELICOPTER, AttackHelicopter.createAttributes().build());
		event.put(ALIEN_BUG, AlienBug.createAttributes().build());
		event.put(GHASTLING, Ghastling.createAttributes().build());
		event.put(ZOMBIE_POLICEMAN, ZombiePoliceman.createAttributes().build());
	}

	/**
	 * Natural spawning: only the dummy spawn is added to the biomes (biome modifiers npc_*.json), it is replaced by a NPC chosen by
	 * the TGSpawnManager. 1.12 EntityMob.getCanSpawnHere = monster spawn rules.
	 */
	@SubscribeEvent
	public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
		event.register(DUMMY_SPAWN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			//1.12 EntityDeathUtils gore map
			EntityType<?>[] gore = { ZOMBIE_SOLDIER, ARMY_SOLDIER, CYBER_DEMON, ZOMBIE_PIGMAN_SOLDIER, ZOMBIE_FARMER, ZOMBIE_MINER, BANDIT, PSYCHO_STEVE,
					DICTATOR_DAVE, SKELETON_SOLDIER, ALIEN_BUG, STORM_TROOPER, SUPER_MUTANT_ELITE, SUPER_MUTANT_HEAVY, SUPER_MUTANT_BASIC, OUTCAST, COMMANDO, GHASTLING };
			for (EntityType<?> t : gore) {
				EntityDeathUtils.addEntityToDeathEffectList(t);
			}
			TGSpawnManager.init();
		});
	}

	@SubscribeEvent
	public static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			SPAWN_EGGS.forEach(event::accept);
		}
	}
}
