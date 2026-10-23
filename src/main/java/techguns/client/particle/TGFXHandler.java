package techguns.client.particle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import techguns.client.TGExtendedPlayerClient;
import techguns.client.render.FirstPersonFXCorrection;
import techguns.client.render.ItemRenderHack;
import techguns.client.render.entities.projectiles.DeathEffectEntityRenderer;
import techguns.client.render.entities.projectiles.RenderFlyingGibs;
import techguns.client.render.item.IItemRenderer;
import techguns.client.render.item.RenderItemBase;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.entities.projectiles.FlyingGibs;
import techguns.items.guns.GenericGun;
import techguns.util.EntityCondition;

/**
 * Facade between ClientProxy / renderers / client events and the Techguns particle system (TGFX, TGParticleManager,
 * LightPulse, DeathEffect). Holds the client state that was in the 1.12 ClientProxy (particleManager, activeLightPulses,
 * entity death types).
 */
public class TGFXHandler {

	public static final TGParticleManager particleManager = new TGParticleManager();

	public static final LinkedList<LightPulse> activeLightPulses = new LinkedList<>();

	/**
	 * Attached particles of non player entities (1.12: TGShooterValues capability of NPCs)
	 */
	protected static final Map<LivingEntity, EntityParticleAttachments> entityAttachments = new WeakHashMap<>();

	/**
	 * Death types of dying entities (1.12: TGDeathTypeCap capability, only used client side)
	 */
	protected static final Map<LivingEntity, DeathType> entityDeathTypes = new WeakHashMap<>();

	/**
	 * Buffers of the world particles, batched per render type (one draw call per type and frame)
	 */
	private static TGParticleBufferSource worldBufferSource = null;

	private static ClientLevel lastLevel = null;

	/**
	 * Mod bus listeners of the particle system: FX file loading and the FlyingGibs renderer.
	 * Hooked from TGClientSetup.registerModBusListeners
	 */
	public static void registerModBusListeners(IEventBus modBus) {
		modBus.addListener(TGFXHandler::onRegisterReloadListeners);
		modBus.addListener(TGFXHandler::onRegisterRenderers);
	}

	private static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) manager -> {
			TGFX.loadFXList(manager);
			DeathEffect.postInit();
		});
	}

	private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		if (FlyingGibs.TYPE != null) {
			event.registerEntityRenderer(FlyingGibs.TYPE, RenderFlyingGibs::new);
		}
	}

	protected static void addSystems(List<TGParticleSystem> systems) {
		if (systems != null) {
			systems.forEach(s -> particleManager.addEffect(s));
		}
	}

	public static void createFX(String name, Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		List<TGParticleSystem> systems = TGFX.createFX(world, name, posX, posY, posZ, motionX, motionY, motionZ);
		addSystems(systems);
	}

	public static void createFX(String name, Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float pitch, float yaw) {
		List<TGParticleSystem> systems = TGFX.createFX(world, name, posX, posY, posZ, motionX, motionY, motionZ);
		if (systems!=null) {
			for (TGParticleSystem s : systems) {
				s.rotationPitch = pitch;
				s.rotationYaw = yaw;
				particleManager.addEffect(s);
			}
		}
	}

	public static void createFX(String name, Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float scale) {
		List<TGParticleSystem> systems = TGFX.createFX(world, name, posX, posY, posZ, motionX, motionY, motionZ);
		if (systems!=null) {
			for (TGParticleSystem s : systems) {
				s.scale = scale;
				particleManager.addEffect(s);
			}
		}
	}

	public static void createFXOnEntity(String name, Entity ent) {
		List<TGParticleSystem> systems = TGFX.createFXOnEntity(ent, name);
		if (systems!=null) {
			systems.forEach(s -> {
				s.condition=EntityCondition.ENTITY_ALIVE;
				particleManager.addEffect(s);
			});
		}
	}

	public static void createFXOnEntity(String name, Entity ent, float scale) {
		List<TGParticleSystem> systems = TGFX.createFXOnEntity(ent, name);
		if (systems!=null) {
			for (TGParticleSystem s : systems) {
				s.scale = scale;
				s.condition=EntityCondition.ENTITY_ALIVE;
				particleManager.addEffect(s);
			}
		}
	}

	public static void createFXOnEntityWithOffset(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead, EntityCondition condition) {
		List<TGParticleSystem> systems = TGFX.createFXOnEntity(ent, name);
		if (systems!=null) {
			for (TGParticleSystem s : systems) {
				s.entityOffset = new Vec3(offsetX, offsetY, offsetZ);
				s.attachToHead = attachToHead;
				s.condition = condition;
				particleManager.addEffect(s);
			}
		}
	}

	public static void createLightPulse(double x, double y, double z, int lifetime, float rad_start, float rad_end, float r, float g, float b) {
		activeLightPulses.add(new LightPulse(x, y, z, lifetime, rad_start, rad_end, r, g, b));
	}

	public static void createLightPulse(double x, double y, double z, int fadeIn, int fadeOut, float rad_large, float rad_small, float r, float g, float b) {
		activeLightPulses.add(new LightPulse(x, y, z, fadeIn, fadeOut, rad_large, rad_small, r, g, b));
	}

	/**
	 * 1.12 PacketEntityDeathType handler: ClientProxy.setEntityDeathType + DeathEffect.createDeathEffect
	 */
	public static void handleEntityDeathType(LivingEntity entity, DeathType deathtype, float motionX, float motionY, float motionZ) {
		setEntityDeathType(entity, deathtype);
		DeathEffect.createDeathEffect(entity, deathtype, motionX, motionY, motionZ);
	}

	public static void setEntityDeathType(LivingEntity entity, DeathType deathtype) {
		if (deathtype == null || deathtype == DeathType.DEFAULT) {
			entityDeathTypes.remove(entity);
		} else {
			entityDeathTypes.put(entity, deathtype);
		}
	}

	public static DeathType getEntityDeathType(LivingEntity entity) {
		DeathType dt = entityDeathTypes.get(entity);
		return dt != null ? dt : DeathType.DEFAULT;
	}

	/**
	 * Attached particle lists of an entity: TGExtendedPlayerClient for players, a client map for other entities
	 */
	public static ITGParticleAttachments getParticleAttachments(LivingEntity elb) {
		if (elb instanceof Player) {
			return TGExtendedPlayerClient.get((Player) elb);
		}
		return entityAttachments.computeIfAbsent(elb, e -> new EntityParticleAttachments());
	}

	/**
	 * Attached particles if they exist, never creates an entry
	 */
	protected static ITGParticleAttachments getParticleAttachmentsIfPresent(LivingEntity elb) {
		if (elb instanceof Player) {
			return TGExtendedPlayerClient.get((Player) elb);
		}
		return entityAttachments.get(elb);
	}

	/**
	 * 1.12 PacketNotifyAmbientEffectHandler: (re)starts the ambient item attached FX of the gun held in the hand
	 */
	public static void handleAmbientEffectChange(LivingEntity elb, InteractionHand hand) {
		ItemStack stack = elb.getItemInHand(hand);

		if(!stack.isEmpty() && stack.getItem() instanceof GenericGun) {

			if ( ((GenericGun)stack.getItem()).getAmmoLeft(stack)>0) {
				IItemRenderer renderer = ItemRenderHack.getRendererForItem(stack.getItem());
				if(renderer!=null && renderer instanceof RenderItemBase) {
					RenderItemBase itemRenderer = (RenderItemBase) renderer;

					String fxlist = itemRenderer.getAmbientParticleFX();
					if(fxlist!=null) {
						List<TGParticleSystem> systems = TGFX.createFXOnEntityItemAttached(elb, hand, fxlist);
						ITGParticleAttachments props = getParticleAttachments(elb);
						if (props != null) {
							props.addSystemsHand(hand, systems != null ? systems : new ArrayList<>());
						}
					} else {
						clearItemParticleSystemsHand(elb, hand);
					}
				} else {
					clearItemParticleSystemsHand(elb, hand);
				}
			} else {
				clearItemParticleSystemsHand(elb, hand);
			}
		} else {
			clearItemParticleSystemsHand(elb, hand);
		}
	}

	public static void clearItemParticleSystemsHand(LivingEntity entity, InteractionHand hand) {
		ITGParticleAttachments props = getParticleAttachmentsIfPresent(entity);
		if (props != null) {
			props.clearAttachedSystemsHand(hand);
		}
	}

	/**
	 * Renders the particles attached to the item held in the given hand, called by the item renderers with the pose of the hand
	 * (1.12 RenderItemBase.renderItemParticles)
	 */
	public static void renderItemAttachedParticles(LivingEntity ent, InteractionHand hand, PoseStack poseStack, MultiBufferSource buffers, float partialTicks) {
		ITGParticleAttachments props = getParticleAttachmentsIfPresent(ent);
		if (props == null) {
			return;
		}
		List<ITGParticle> particles = props.getEntityParticlesHand(hand);

		if (particles != null && !particles.isEmpty()) {
			for (ITGParticle p : particles) {
				//Align to Weapon
				p.doRender(poseStack, buffers, ent, partialTicks, 1.0f, 1.0f, 0, 0, 0);
			}
		}
	}

	/**
	 * Called at the end of every client tick (tick particle systems, light pulses, entity attached particles)
	 */
	public static void clientTick() {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;

		if (level != lastLevel) {
			//level changed / left: drop everything of the old level
			lastLevel = level;
			particleManager.clear();
			activeLightPulses.clear();
			entityAttachments.clear();
			entityDeathTypes.clear();
		}
		if (level == null || mc.isPaused()) {
			return;
		}

		//Light pulses
		Iterator<LightPulse> iter = activeLightPulses.iterator();
		while(iter.hasNext()) {
			if (!iter.next().updateGameTick()) {
				iter.remove();
			}
		}

		//Entity attached particles (1.12 TGTickHandler.TickParticleSystems, START phase)
		for (Player p : level.players()) {
			TGExtendedPlayerClient props = TGExtendedPlayerClient.get(p);
			if (props != null) {
				props.tickParticles();
			}
		}
		Iterator<Map.Entry<LivingEntity, EntityParticleAttachments>> it = entityAttachments.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<LivingEntity, EntityParticleAttachments> e = it.next();
			if (e.getKey().isRemoved()) {
				it.remove();
			} else {
				e.getValue().tickParticles();
			}
		}

		particleManager.tickParticles();
	}

	/**
	 * Called from RenderLevelStageEvent (stage AFTER_PARTICLES), renders the world particles (1.12: RenderWorldLastEvent)
	 */
	public static void renderWorldParticles(RenderLevelStageEvent event) {
		if (Minecraft.getInstance().level == null) {
			return;
		}
		if (worldBufferSource == null) {
			worldBufferSource = new TGParticleBufferSource();
		}
		//effects attached to the first person gun must match the fixed FOV of the held item (applied per vertex in TGParticle)
		FirstPersonFXCorrection.particleK = FirstPersonFXCorrection.computeK();
		try {
			particleManager.renderParticles(event.getCamera(), event.getPoseStack(), worldBufferSource, event.getFrustum(), event.getPartialTick());
		} finally {
			FirstPersonFXCorrection.particleK = 1.0f;
		}
		worldBufferSource.endBatch();
	}

	/**
	 * Called from RenderLivingEvent.Pre, death effects cancel the normal rendering of dying entities
	 */
	public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
		DeathType dt = getEntityDeathType(event.getEntity());
		switch (dt) {
		case GORE:
			event.setCanceled(true);
			break;
		case DISMEMBER:
		case BIO:
		case LASER:
			event.setCanceled(true);
			DeathEffectEntityRenderer.doRender(event.getRenderer(), event.getEntity(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick(), dt);
			break;
		case DEFAULT:
		default:
			break;
		}
	}
}
