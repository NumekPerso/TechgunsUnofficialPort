package techguns.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import techguns.Techguns;
import techguns.client.models.gibs.ModelGibsBiped;
import techguns.client.models.gibs.ModelGibsGeneric;
import techguns.client.models.gibs.ModelGibsSlime;
import techguns.client.models.npcs.ModelAlienBug;
import techguns.client.models.npcs.ModelCyberDemon;
import techguns.client.models.npcs.ModelGenericNPC;
import techguns.client.models.npcs.ModelGhastling;
import techguns.client.models.npcs.ModelSkeletonSoldier;
import techguns.client.models.npcs.ModelSuperMutant;
import techguns.client.models.npcs.NPCHumanoidModel;
import techguns.client.models.npcs.TGEntityModel;
import techguns.client.particle.DeathEffect;
import techguns.client.particle.DeathEffect.GoreData;
import techguns.client.particle.TGFX;
import techguns.client.render.entities.npcs.RenderAttackHelicopter;
import techguns.client.render.entities.npcs.RenderGenericNPC;
import techguns.client.render.entities.npcs.RenderSuperMutant;
import techguns.client.render.entities.npcs.RenderTGEntityModel;
import techguns.client.render.entities.npcs.RenderZombiePigmanSoldier;
import techguns.entities.npcs.TGNpcEntities;

/**
 * Client side of the NPCs (lot J): renderers (1.12 ClientProxy NPC renderers) and gore data (1.12 DeathEffect NPC entries).
 * The turret renderer is registered by TGMachinesClient.
 */
public class TGNpcClient {

	public static void registerModBusListeners(IEventBus modBus) {
		modBus.addListener(TGNpcClient::registerRenderers);
		modBus.addListener(TGNpcClient::onClientSetup);
	}

	private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TGNpcEntities.DUMMY_SPAWN, NoopRenderer::new);

		event.registerEntityRenderer(TGNpcEntities.ZOMBIE_SOLDIER, RenderGenericNPC.generic("zombie_soldier"));
		event.registerEntityRenderer(TGNpcEntities.ZOMBIE_FARMER, RenderGenericNPC.generic("zombie_soldier"));
		event.registerEntityRenderer(TGNpcEntities.ZOMBIE_MINER, RenderGenericNPC.generic("zombie_soldier"));
		event.registerEntityRenderer(TGNpcEntities.ARMY_SOLDIER, RenderGenericNPC.generic("army_soldier"));
		event.registerEntityRenderer(TGNpcEntities.BANDIT, RenderGenericNPC.generic("bandit"));
		event.registerEntityRenderer(TGNpcEntities.COMMANDO, RenderGenericNPC.generic("army_soldier"));
		event.registerEntityRenderer(TGNpcEntities.DICTATOR_DAVE, RenderGenericNPC.generic("dictator"));
		event.registerEntityRenderer(TGNpcEntities.CYBER_DEMON, RenderGenericNPC.of(ModelCyberDemon::new, new ResourceLocation(Techguns.MODID, "textures/entity/cyberdemon.png"), true, null));
		event.registerEntityRenderer(TGNpcEntities.SKELETON_SOLDIER, RenderGenericNPC.of(ModelSkeletonSoldier::new, new ResourceLocation("textures/entity/skeleton/skeleton.png"), true, ModelSkeletonSoldier::new));
		event.registerEntityRenderer(TGNpcEntities.PSYCHO_STEVE, RenderGenericNPC.generic("army_soldier"));
		event.registerEntityRenderer(TGNpcEntities.STORM_TROOPER, RenderGenericNPC.generic("army_soldier"));
		event.registerEntityRenderer(TGNpcEntities.OUTCAST, RenderGenericNPC.generic("army_soldier"));
		event.registerEntityRenderer(TGNpcEntities.ZOMBIE_PIGMAN_SOLDIER, RenderZombiePigmanSoldier::new);
		event.registerEntityRenderer(TGNpcEntities.SUPER_MUTANT_BASIC, RenderSuperMutant::new);
		event.registerEntityRenderer(TGNpcEntities.SUPER_MUTANT_ELITE, RenderSuperMutant::new);
		event.registerEntityRenderer(TGNpcEntities.SUPER_MUTANT_HEAVY, RenderSuperMutant::new);
		event.registerEntityRenderer(TGNpcEntities.ATTACK_HELICOPTER, RenderAttackHelicopter::new);
		ResourceLocation bugTex = new ResourceLocation(Techguns.MODID, "textures/entity/alienbug.png");
		event.registerEntityRenderer(TGNpcEntities.ALIEN_BUG, ctx -> new RenderTGEntityModel<>(ctx, new ModelAlienBug(), 0.8f, e -> bugTex));
		ResourceLocation ghast = new ResourceLocation("textures/entity/ghast/ghast.png");
		ResourceLocation ghastShooting = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");
		event.registerEntityRenderer(TGNpcEntities.GHASTLING, ctx -> new RenderTGEntityModel<>(ctx, new ModelGhastling(), 0.25f, e -> e.isAttacking() ? ghastShooting : ghast));
		event.registerEntityRenderer(TGNpcEntities.ZOMBIE_POLICEMAN, RenderGenericNPC.generic("zombie_policeman"));
	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(TGNpcClient::addGoreData);
	}

	/**
	 * 1.12 DeathEffect gore stats of the NPCs (the Outcast had none)
	 */
	private static void addGoreData() {
		List<GoreData> added = new ArrayList<>();
		add(added, TGNpcEntities.ZOMBIE_SOLDIER, new GoreData(DeathEffectModels::biped, 110, 21, 41));
		add(added, TGNpcEntities.ARMY_SOLDIER, new GoreData(DeathEffectModels::biped, 160, 21, 31));
		add(added, TGNpcEntities.ZOMBIE_PIGMAN_SOLDIER, new GoreData(DeathEffect.biped(ModelLayers.ZOMBIFIED_PIGLIN), 110, 51, 11));
		add(added, TGNpcEntities.CYBER_DEMON, new GoreData(() -> new ModelGibsBiped(new NPCHumanoidModel<>(new ModelCyberDemon())), 85, 156, 17));
		add(added, TGNpcEntities.SUPER_MUTANT_BASIC, new GoreData(DeathEffectModels::superMutant, 109, 60, 25).setFXscale(1.2f));
		add(added, TGNpcEntities.SUPER_MUTANT_ELITE, new GoreData(DeathEffectModels::superMutant, 109, 60, 25).setFXscale(1.2f));
		add(added, TGNpcEntities.SUPER_MUTANT_HEAVY, new GoreData(DeathEffectModels::superMutant, 109, 60, 25).setFXscale(1.2f));
		add(added, TGNpcEntities.STORM_TROOPER, new GoreData(DeathEffectModels::biped, 160, 21, 31));
		add(added, TGNpcEntities.COMMANDO, new GoreData(DeathEffectModels::biped, 160, 21, 31));
		add(added, TGNpcEntities.DICTATOR_DAVE, new GoreData(DeathEffectModels::biped, 160, 21, 31));
		add(added, TGNpcEntities.PSYCHO_STEVE, new GoreData(DeathEffectModels::biped, 160, 21, 31));
		add(added, TGNpcEntities.ZOMBIE_FARMER, new GoreData(DeathEffectModels::biped, 110, 21, 41));
		add(added, TGNpcEntities.ZOMBIE_MINER, new GoreData(DeathEffectModels::biped, 110, 21, 41));
		add(added, TGNpcEntities.BANDIT, new GoreData(DeathEffectModels::biped, 160, 21, 31));
		add(added, TGNpcEntities.SKELETON_SOLDIER, new GoreData(DeathEffect.biped(ModelLayers.SKELETON), 255, 255, 255));
		add(added, TGNpcEntities.ALIEN_BUG, new GoreData(() -> new ModelGibsGeneric(new TGEntityModel<>(new ModelAlienBug()).root()), 235, 255, 70));
		add(added, TGNpcEntities.GHASTLING, new GoreData(ModelGibsSlime::new, 255, 255, 255).setFXscale(1.0f));
		//the FX list may already be loaded (DeathEffect.postInit only initializes the gore data present at that time)
		if (!TGFX.FXList.isEmpty()) {
			added.forEach(GoreData::init);
		}
	}

	private static void add(List<GoreData> added, EntityType<?> type, GoreData data) {
		DeathEffect.addGoreData(type, data);
		added.add(data);
	}

	/**
	 * Gib models (1.12 new ModelGibsBiped(new ModelBiped(0,0,64,64)) / new ModelSuperMutant())
	 */
	protected static class DeathEffectModels {
		static techguns.client.models.gibs.ModelGibs biped() {
			return new ModelGibsBiped(new NPCHumanoidModel<>(new ModelGenericNPC()));
		}

		static techguns.client.models.gibs.ModelGibs superMutant() {
			return new ModelGibsBiped(new NPCHumanoidModel<>(new ModelSuperMutant()));
		}
	}
}
