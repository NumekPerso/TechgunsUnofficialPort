package techguns.client.particle;


import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.client.models.gibs.ModelGibs;
import techguns.client.models.gibs.ModelGibsBiped;
import techguns.client.models.gibs.ModelGibsGeneric;
import techguns.client.models.gibs.ModelGibsHorse;
import techguns.client.models.gibs.ModelGibsIllager;
import techguns.client.models.gibs.ModelGibsQuadruped;
import techguns.client.models.gibs.ModelGibsSlime;
import techguns.client.models.gibs.ModelGibsVillager;
import techguns.client.render.entities.projectiles.DeathEffectEntityRenderer;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.util.TGSoundCategory;

/**
 * Client side death effects (gore, bio, laser).
 *
 * 1.20: goreStats are keyed by EntityType instead of the entity class. The gib models are created lazily (the baked
 * vanilla model layers are only available after the resource reload). The Techguns NPCs of 1.12 are not ported.
 */
public class DeathEffect {

	public static HashMap<EntityType<?>, GoreData> goreStats = new HashMap<EntityType<?>, GoreData>();

	private static GoreData genericGore;

	/**
	 * Client side entity ids of the FlyingGibs (negative so they never collide with ids sent by the server)
	 */
	private static int nextGibsId = -1000000;

	static {
		goreStats.put(EntityType.PLAYER, (new GoreData(biped(ModelLayers.PLAYER), 160,21,31)));
		goreStats.put(EntityType.ZOMBIE, (new GoreData(biped(ModelLayers.ZOMBIE), 110,21,41)));
		goreStats.put(EntityType.SKELETON, (new GoreData(biped(ModelLayers.SKELETON), 255,255,255)));
		goreStats.put(EntityType.VILLAGER, (new GoreData(villager(ModelLayers.VILLAGER), 150,21,51)));
		goreStats.put(EntityType.COW, (new GoreData(quadruped(ModelLayers.COW), 170,26,37)));
		goreStats.put(EntityType.SHEEP, (new GoreData(quadruped(ModelLayers.SHEEP), 170,26,37)).setFXscale(0.8f));
		goreStats.put(EntityType.CHICKEN, (new GoreData(generic(ModelLayers.CHICKEN), 170,26,37)).setFXscale(0.5f));
		goreStats.put(EntityType.CREEPER, (new GoreData(generic(ModelLayers.CREEPER), 50,175,57)));
		goreStats.put(EntityType.ENDERMAN, (new GoreData(biped(ModelLayers.ENDERMAN),160,36,167)));
		goreStats.put(EntityType.PIG, (new GoreData(quadruped(ModelLayers.PIG), 170,26,37)).setFXscale(0.8f));
		goreStats.put(EntityType.SPIDER, (new GoreData(generic(ModelLayers.SPIDER), 85,156,17)));
		goreStats.put(EntityType.CAVE_SPIDER, (new GoreData(generic(ModelLayers.CAVE_SPIDER), 85,156,17)).setFXscale(0.7f));

		goreStats.put(EntityType.ZOMBIFIED_PIGLIN, (new GoreData(biped(ModelLayers.ZOMBIFIED_PIGLIN), 110,51,11)));

		goreStats.put(EntityType.WITCH, (new GoreData(villager(ModelLayers.WITCH), 160,21,31)));
		goreStats.put(EntityType.SLIME, (new GoreData(ModelGibsSlime::new,40,255,40)));

		goreStats.put(EntityType.LLAMA, (new GoreData(quadruped(ModelLayers.LLAMA), 170,26,37)));
		goreStats.put(EntityType.EVOKER, (new GoreData(illager(ModelLayers.EVOKER), 110,21,41)));
		goreStats.put(EntityType.HUSK, (new GoreData(biped(ModelLayers.HUSK), 110,21,41)));
		goreStats.put(EntityType.POLAR_BEAR, (new GoreData(quadruped(ModelLayers.POLAR_BEAR), 170,26,37)).setFXscale(0.8f));
		goreStats.put(EntityType.MAGMA_CUBE, (new GoreData(ModelGibsSlime::new,92,26,0)));
		goreStats.put(EntityType.PARROT, (new GoreData(generic(ModelLayers.PARROT), 170,26,37)).setFXscale(0.5f));
		goreStats.put(EntityType.RABBIT, (new GoreData(generic(ModelLayers.RABBIT), 170,26,37)).setFXscale(0.3f));
		goreStats.put(EntityType.STRAY, (new GoreData(biped(ModelLayers.STRAY), 255,255,255)));
		goreStats.put(EntityType.SILVERFISH, (new GoreData(generic(ModelLayers.SILVERFISH), 90,16,27)).setFXscale(0.4f));
		goreStats.put(EntityType.VINDICATOR, (new GoreData(illager(ModelLayers.VINDICATOR), 110,21,41)));
		goreStats.put(EntityType.VEX, (new GoreData(ModelGibsSlime::new, 215,215,215)).setFXscale(0.4f));
		goreStats.put(EntityType.SHULKER, (new GoreData(ModelGibsSlime::new, 125,0,106)).setFXscale(1.5f));
		goreStats.put(EntityType.WITHER_SKELETON, (new GoreData(biped(ModelLayers.WITHER_SKELETON), 50,50,50).setFXscale(1.4f)));
		goreStats.put(EntityType.GHAST, (new GoreData(ModelGibsSlime::new, 255,255,255)).setFXscale(3.5f));
		goreStats.put(EntityType.ZOMBIE_VILLAGER, (new GoreData(biped(ModelLayers.ZOMBIE_VILLAGER), 110,21,41)));
		goreStats.put(EntityType.HORSE, (new GoreData(horse(ModelLayers.HORSE), 170,26,37)).setFXscale(1.0f));
		goreStats.put(EntityType.DONKEY, (new GoreData(horse(ModelLayers.DONKEY), 170,26,37)).setFXscale(1.0f));
		goreStats.put(EntityType.MULE, (new GoreData(horse(ModelLayers.MULE), 170,26,37)).setFXscale(1.0f));

		//mobs added after 1.12 (EntityDeathUtils.goreMap)
		goreStats.put(EntityType.MOOSHROOM, (new GoreData(quadruped(ModelLayers.MOOSHROOM), 170,26,37)));
		goreStats.put(EntityType.WOLF, (new GoreData(generic(ModelLayers.WOLF), 170,26,37)).setFXscale(0.7f));
		goreStats.put(EntityType.SQUID, (new GoreData(generic(ModelLayers.SQUID), 20,20,60)));
		goreStats.put(EntityType.DROWNED, (new GoreData(biped(ModelLayers.DROWNED), 110,21,41)));
		goreStats.put(EntityType.PILLAGER, (new GoreData(illager(ModelLayers.PILLAGER), 110,21,41)));
		goreStats.put(EntityType.PIGLIN, (new GoreData(biped(ModelLayers.PIGLIN), 160,21,31)));
		goreStats.put(EntityType.PIGLIN_BRUTE, (new GoreData(biped(ModelLayers.PIGLIN_BRUTE), 160,21,31)));
		goreStats.put(EntityType.HOGLIN, (new GoreData(generic(ModelLayers.HOGLIN), 170,26,37)).setFXscale(1.2f));
		goreStats.put(EntityType.ZOGLIN, (new GoreData(generic(ModelLayers.ZOGLIN), 110,51,11)).setFXscale(1.2f));
		goreStats.put(EntityType.RAVAGER, (new GoreData(generic(ModelLayers.RAVAGER), 170,26,37)).setFXscale(1.5f));
		goreStats.put(EntityType.FOX, (new GoreData(generic(ModelLayers.FOX), 170,26,37)).setFXscale(0.6f));
		goreStats.put(EntityType.GOAT, (new GoreData(quadruped(ModelLayers.GOAT), 170,26,37)).setFXscale(0.8f));
		goreStats.put(EntityType.WANDERING_TRADER, (new GoreData(villager(ModelLayers.WANDERING_TRADER), 150,21,51)));

		genericGore = (new GoreData(biped(ModelLayers.ZOMBIE), 160,21,31)).setTexture(new ResourceLocation(Techguns.MODID,"textures/entity/gore.png"));
		genericGore.setRandomScale(0.5f, 0.8f);
	}

	/**
	 * Baked vanilla model layer (1.12: new ModelXXX())
	 */
	public static ModelPart bakeLayer(ModelLayerLocation layer) {
		return Minecraft.getInstance().getEntityModels().bakeLayer(layer);
	}

	public static Supplier<ModelGibs> biped(ModelLayerLocation layer) {
		return () -> new ModelGibsBiped(bakeLayer(layer));
	}

	public static Supplier<ModelGibs> quadruped(ModelLayerLocation layer) {
		return () -> new ModelGibsQuadruped(bakeLayer(layer));
	}

	public static Supplier<ModelGibs> villager(ModelLayerLocation layer) {
		return () -> new ModelGibsVillager(bakeLayer(layer));
	}

	public static Supplier<ModelGibs> illager(ModelLayerLocation layer) {
		return () -> new ModelGibsIllager(bakeLayer(layer));
	}

	public static Supplier<ModelGibs> generic(ModelLayerLocation layer) {
		return () -> new ModelGibsGeneric(bakeLayer(layer));
	}

	public static Supplier<ModelGibs> horse(ModelLayerLocation layer) {
		return () -> new ModelGibsHorse(bakeLayer(layer));
	}

	/**
	 * Use this method to put GoreData into the map, call this BEFORE the client resources are loaded
	 * @param entityType
	 * @param data
	 */
	public static void addGoreData(EntityType<?> entityType, GoreData data ) {
		goreStats.put(entityType, data);
	}

	/**
	 * Called after the FX files were (re)loaded (1.12: from ClientProxy in postInit)
	 */
	public static void postInit() {
		goreStats.values().forEach(stat -> stat.init());
		genericGore.init();
	}

	public static GoreData getGoreData(EntityType<?> entityType) {
		GoreData data = DeathEffect.goreStats.get(entityType);
		if (data != null) {
			return data;
		}else {
			data = new GoreData();
			data.bloodColorR = genericGore.bloodColorR;
			data.bloodColorG = genericGore.bloodColorG;
			data.bloodColorB = genericGore.bloodColorB;
			data.type_main = genericGore.type_main;
			data.type_trail = genericGore.type_trail;
			data.sound = genericGore.sound;
			data.numGibs = -1; //TODO
			goreStats.put(entityType, data);
			return data;
		}
	}

	/**
	 * Gib model for entities without own gore data, from the model of their renderer
	 * (1.12: new instance of the ModelBiped/ModelQuadruped/ModelVillager class of the renderer, else the generic gore model)
	 */
	protected static void initModelFromRenderer(GoreData data, LivingEntity entity) {
		EntityRenderer<?> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
		if (render instanceof LivingEntityRenderer) {
			EntityModel<?> mainModel = ((LivingEntityRenderer<?, ?>) render).getModel();
			if (mainModel instanceof HumanoidModel) {
				data.model = new ModelGibsBiped((HumanoidModel<?>) mainModel);
				return;
			}
			List<ModelPart> parts = DeathEffectEntityRenderer.getTopLevelParts(mainModel);
			if (parts != null && !parts.isEmpty()) {
				data.model = new ModelGibsGeneric(parts);
				return;
			}
		}
		data.model = genericGore.getModel();
		data.texture = genericGore.texture;
	}

	public static void createDeathEffect(LivingEntity entity, DeathType deathtype, float motionX, float motionY, float motionZ) {

		double x = entity.getX();
		double y = entity.getY()+(entity.getBbHeight()/2.0f);
		double z = entity.getZ();

		if (deathtype == DeathType.GORE) {

			GoreData data = DeathEffect.getGoreData(entity.getType());

			try {
				if (data.getModel() == null) {
					initModelFromRenderer(data, entity);
				}
			} catch (Exception e) {
				Techguns.LOGGER.error("Could not create gib model for "+entity, e);
			}

			Techguns.proxy.playSoundOnPosition(data.sound, (float)x,(float)y,(float)z, 1.0f, 1.0f, false, TGSoundCategory.DEATHEFFECT);

			//Spawn MainFX
			if (data.type_main != null) {
				Vec3 motion = entity.getDeltaMovement();
				TGParticleSystem sys = new TGParticleSystem(entity.level(), data.type_main, x, entity.getY(), z, motion.x, motion.y, motion.z);
				TGFXHandler.particleManager.addEffect(sys);
			}

			int count;
			if (data.numGibs >= 0) {
				count = data.numGibs;
			} else {
				if(data.getModel()==null) {
					return;
				}
				count = data.getModel().getNumGibs();
			}

			if (!(entity.level() instanceof ClientLevel)) {
				return;
			}
			ClientLevel level = (ClientLevel) entity.level();

			for (int i = 0; i < count; i++) {
				double vx = (0.5-level.random.nextDouble()) * 0.35;
				double vy;
				if (entity.onGround()) {
					vy = (level.random.nextDouble()) * 0.35;
				}else {
					vy = (0.5-level.random.nextDouble()) * 0.35;
				}
				double vz = (0.5-level.random.nextDouble()) * 0.35;

				FlyingGibsClient ent = new FlyingGibsClient(level, entity, data, x,y,z, motionX*0.35+vx, motionY*0.35+vy, motionZ*0.35+vz, (entity.getBbWidth()+entity.getBbHeight())/2.0f, i);

				//client only entity
				ent.setId(nextGibsId--);
				level.putNonPlayerEntity(ent.getId(), ent);
			}
		}else if (deathtype == DeathType.BIO) {
			TGFXHandler.createFX("biodeath", entity.level(), x,y,z, (double)motionX, (double)motionY, (double)motionZ);
			Techguns.proxy.playSoundOnPosition(TGSounds.DEATH_BIO, (float)x,(float)y,(float)z, 1.0f, 1.0f, false, TGSoundCategory.DEATHEFFECT);
		}else if (deathtype == DeathType.LASER) {
			TGFXHandler.createFX("laserdeathFire", entity.level(), x,y,z, (double)motionX, 0, (double)motionZ);
			TGFXHandler.createFX("laserdeathAsh", entity.level(), x,y,z, (double)motionX, 0, (double)motionZ);
			Techguns.proxy.playSoundOnPosition(TGSounds.DEATH_LASER, (float)x,(float)y,(float)z, 1.0f, 1.0f, false, TGSoundCategory.DEATHEFFECT);
		}
	}

	public static class GoreData {
		public ModelGibs model = null;
		/**
		 * Creates the model on first use (baked model layers are not available at class loading)
		 */
		protected Supplier<ModelGibs> modelFactory = null;
		public ResourceLocation texture = null;
		int numGibs = -1;
		public float particleScale = 1.0f;
		public float modelScale = 1.0f;

		int bloodColorR;
		int bloodColorG;
		int bloodColorB;

		String fx_main = "GoreFX_Blood";
		String fx_trail ="GoreTrailFX_Blood";
		public SoundEvent sound = TGSounds.DEATH_GORE;

		public TGParticleSystemType type_main;
		public TGParticleSystemType type_trail;

		public float minPartScale = 1.0f;
		public float maxPartScale = 1.0f;

		public GoreData() {}

		public GoreData(Supplier<ModelGibs> model, int bloodColorR, int bloodColorG, int bloodColorB) {
			this.modelFactory = model;
			this.bloodColorR = bloodColorR;
			this.bloodColorG = bloodColorG;
			this.bloodColorB = bloodColorB;
		}

		public GoreData(ModelGibs model, int bloodColorR, int bloodColorG, int bloodColorB) {
			this.model = model;
			this.bloodColorR = bloodColorR;
			this.bloodColorG = bloodColorG;
			this.bloodColorB = bloodColorB;
		}

		/**
		 * The gib model, created on first use
		 */
		public ModelGibs getModel() {
			if (this.model == null && this.modelFactory != null) {
				try {
					this.model = this.modelFactory.get();
				} catch (Exception e) {
					Techguns.LOGGER.error("Could not create gib model", e);
				}
				this.modelFactory = null;
			}
			return this.model;
		}

		public GoreData setNumGibs(int gibs) {
			this.numGibs = gibs;
			return this;
		}

		public GoreData setTexture(ResourceLocation texture) {
			this.texture = texture;
			return this;
		}

		public GoreData setFXscale(float scale) {
			this.particleScale = scale;
			return this;
		}

		public GoreData setFX(String fx_main, String fx_trail) {
			this.fx_main = fx_main;
			this.fx_trail = fx_trail;
			return this;
		}

		public GoreData setSound(SoundEvent sound) {
			this.sound = sound;
			return this;
		}

		public void init() {
			type_main = new TGParticleSystemType();

			if (TGFX.FXList.containsKey(fx_main.toLowerCase())) {
				TGFXType fxtype_main = TGFX.FXList.get(fx_main.toLowerCase());
				if (fxtype_main instanceof TGParticleSystemType) {
					this.type_main = getExtendedType((TGParticleSystemType) fxtype_main);
				}else {
					this.type_main = null;
				}
			}else {
				this.type_main = null;
			}

			type_trail = new TGParticleSystemType();

			if (TGFX.FXList.containsKey(fx_trail.toLowerCase())) {
				TGFXType fxtype_trail = TGFX.FXList.get(fx_trail.toLowerCase());
				if (fxtype_trail instanceof TGParticleSystemType) {
					this.type_trail = getExtendedType((TGParticleSystemType) fxtype_trail);
				}else {
					this.type_trail = null;
				}
			}else {
				this.type_trail = null;
			}
		}

		/**
		 * Add a random scale to individual gibs.
		 * @param f
		 * @param g
		 */
		public void setRandomScale(float min, float max) {
			minPartScale = min;
			maxPartScale = max;
		}


		private TGParticleSystemType getExtendedType(TGParticleSystemType supertype) {
			TGParticleSystemType type = new TGParticleSystemType();
			type.extend(supertype);
			if (type.colorEntries.size() >= 1) {
				type.colorEntries.get(0).r = (float)this.bloodColorR /255.0f;
				type.colorEntries.get(0).g = (float)this.bloodColorG /255.0f;
				type.colorEntries.get(0).b = (float)this.bloodColorB /255.0f;
			}
			type.sizeMin *= particleScale;
			type.sizeMax *= particleScale;
			type.sizeRateMin *= particleScale;
			type.sizeRateMax *= particleScale;
			type.startSizeRateDampingMin *= particleScale;
			type.startSizeRateMin *= particleScale;
			type.startSizeRateMax *= particleScale;
			for (int i = 0; i < type.volumeData.length; i++) {
				type.volumeData[i]*=particleScale;
			}
			return type;
		}
	}

}
