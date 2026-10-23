package techguns.client.render.entities.projectiles;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import techguns.Techguns;
import techguns.client.models.gibs.ModelGibs;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.TGRenderTypes;
import techguns.deatheffects.EntityDeathUtils.DeathType;

/**
 * Renders dying entities with the bio / laser death effects instead of the normal LivingEntityRenderer render.
 *
 * 1.12 accessed the renderer internals by reflection (mainModel, preRenderCallback, bindEntityTexture). In 1.20
 * getModel() and getTextureLocation() are public, the scale callback (LivingEntityRenderer.scale) and the part lists of
 * the models are still accessed by reflection.
 */
public class DeathEffectEntityRenderer {

	private static final ResourceLocation RES_BIO_EFFECT = new ResourceLocation(Techguns.MODID,"textures/fx/bio.png");
	private static final ResourceLocation RES_LASER_EFFECT = new ResourceLocation(Techguns.MODID,"textures/fx/laserdeath.png");
	private static final int MAX_DEATH_TIME = 20;

	protected static Method RLB_preRenderCallback = findMethod(LivingEntityRenderer.class, "m_7546_", LivingEntity.class, PoseStack.class, float.class);
	protected static Method ALM_headParts = findMethod(AgeableListModel.class, "m_5607_");
	protected static Method ALM_bodyParts = findMethod(AgeableListModel.class, "m_5608_");
	protected static Method LM_parts = findMethod(ListModel.class, "m_6195_");

	private static Method findMethod(Class<?> clazz, String srgName, Class<?>... params) {
		try {
			return ObfuscationReflectionHelper.findMethod(clazz, srgName, params);
		} catch (Exception e) {
			Techguns.LOGGER.error("DeathEffectEntityRenderer: can't find method "+srgName+" in "+clazz.getName(), e);
			return null;
		}
	}

	/**
	 * 1.12 preRenderCallback: entity specific scaling of the renderer (LivingEntityRenderer.scale)
	 */
	public static void preRenderCallback(LivingEntityRenderer<?, ?> renderer, LivingEntity elb, PoseStack poseStack, float ptt) {
		if (RLB_preRenderCallback != null) {
			try {
				RLB_preRenderCallback.invoke(renderer, elb, poseStack, ptt);
			} catch (Exception e) {
				Techguns.LOGGER.error("preRenderCallback failed", e);
			}
		}
	}

	/**
	 * 1.12 bindEntityTexture
	 */
	@SuppressWarnings("unchecked")
	public static ResourceLocation getEntityTexture(EntityRenderer<?> renderer, LivingEntity entity) {
		return ((EntityRenderer<LivingEntity>) renderer).getTextureLocation(entity);
	}

	/**
	 * Top level parts of a model (1.12: boxes of the boxList which are no children), null if unknown
	 */
	@SuppressWarnings("unchecked")
	public static List<ModelPart> getTopLevelParts(EntityModel<?> model) {
		try {
			if (model instanceof HierarchicalModel) {
				return ModelGibs.getChildren(((HierarchicalModel<?>) model).root());
			} else if (model instanceof AgeableListModel && ALM_headParts != null && ALM_bodyParts != null) {
				List<ModelPart> parts = new ArrayList<>();
				((Iterable<ModelPart>) ALM_headParts.invoke(model)).forEach(parts::add);
				((Iterable<ModelPart>) ALM_bodyParts.invoke(model)).forEach(parts::add);
				return parts;
			} else if (model instanceof ListModel && LM_parts != null) {
				List<ModelPart> parts = new ArrayList<>();
				((Iterable<ModelPart>) LM_parts.invoke(model)).forEach(parts::add);
				return parts;
			}
		} catch (Exception e) {
			Techguns.LOGGER.error("Can't get parts of model "+model, e);
		}
		return null;
	}

	/**
	 * Called from RenderLivingEvent.Pre (pose stack translated to the entity position), replaces the normal render
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void doRender(LivingEntityRenderer<?, ?> renderer, LivingEntity entity, PoseStack poseStack, MultiBufferSource buffers, int packedLight, float ptt, DeathType deathType)
	{
		poseStack.pushPose();

		EntityModel mainModel = renderer.getModel();

		try
		{
			mainModel.young = entity.isBaby();
			mainModel.riding = entity.isPassenger();
			mainModel.attackTime = entity.getAttackAnim(ptt);

			float f2 = Mth.rotLerp(ptt, entity.yBodyRotO, entity.yBodyRot);
			float f3 = Mth.rotLerp(ptt, entity.yHeadRotO, entity.yHeadRot);
			float f4;

			float f13 = Mth.lerp(ptt, entity.xRotO, entity.getXRot());

			f4 = (float)entity.tickCount + ptt;

			//vanilla LivingEntityRenderer transformation (1.12 Techguns: scale(-1,-1,-1) and rotate(rotationYaw))
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - f2));
			poseStack.scale(-1.0f, -1.0f, 1.0f);

			preRenderCallback(renderer, entity, poseStack, ptt);

			poseStack.translate(0.0F, -1.501F, 0.0F);

			float f6 = 0.0F;
			float f7 = 0.0F;
			if (!entity.isPassenger() && entity.isAlive()) {
				f6 = entity.walkAnimation.speed(ptt);
				f7 = entity.walkAnimation.position(ptt);
				if (entity.isBaby()) {
					f7 *= 3.0F;
				}

				if (f6 > 1.0F) {
					f6 = 1.0F;
				}
			}

			ResourceLocation texture = getEntityTexture(renderer, entity);

			switch(deathType) {
			case BIO:
				mainModel.prepareMobModel(entity, f7, f6, ptt);
				renderModelDeathBio(mainModel, texture, entity, poseStack, buffers, packedLight, f7, f6, f4, f3 - f2, f13);
				break;
			case LASER:
				mainModel.prepareMobModel(entity, f7, f6, ptt);
				renderModelDeathLaser(mainModel, texture, entity, poseStack, buffers, packedLight, f7, f6, f4, f3 - f2, f13);
				break;
			case GORE:
			case DEFAULT:
			default:
				break;

			}

		}catch (Exception exception)
		{
			Techguns.LOGGER.error("Couldn't render death effect of "+entity, exception);
		}

		poseStack.popPose();
	}

	//------------

	/**
	 * Renders the model in RenderLiving
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void renderModelDeathBio(EntityModel mainModel, ResourceLocation texture, LivingEntity entity, PoseStack poseStack, MultiBufferSource buffers, int packedLight, float f7, float f6, float f4, float p_77036_5_, float f13)
	{
		float prog = ((float)entity.deathTime / (float)MAX_DEATH_TIME);

		Random rand = new Random(entity.getId());

		mainModel.setupAnim(entity, f7, f6, f4, p_77036_5_, f13);

		double mainColor = 1.0-Math.pow(prog, 2.0);
		double mainAlpha = Math.pow(1.0-prog, 2.0);
		float overlayColor = (float) (0.5+(Math.sin((Math.sqrt(prog)+0.75)*2.0*Math.PI)/2));

		VertexConsumer vcMain = buffers.getBuffer(RenderType.entityTranslucent(texture));

		List<ModelPart> parts = getTopLevelParts(mainModel);
		if (parts == null) {
			//unknown model type: render as a whole
			mainModel.renderToBuffer(poseStack, vcMain, packedLight, OverlayTexture.NO_OVERLAY, (float)mainColor, 1.0f, (float)mainColor, (float)mainAlpha);
			VertexConsumer vcFX = buffers.getBuffer(TGRenderTypes.entityAdditive(RES_BIO_EFFECT));
			mainModel.renderToBuffer(poseStack, vcFX, TGRenderHelper.FULLBRIGHT, OverlayTexture.NO_OVERLAY, overlayColor, overlayColor, overlayColor, 1.0f);
			return;
		}

		for (ModelPart box : parts) {
			if (box.visible) {
				float scale = 1.0f+(rand.nextFloat()*prog);
				poseStack.pushPose();
				poseStack.scale(scale, scale, scale);
				vcMain = buffers.getBuffer(RenderType.entityTranslucent(texture));
				box.render(poseStack, vcMain, packedLight, OverlayTexture.NO_OVERLAY, (float)mainColor, 1.0f, (float)mainColor, (float)mainAlpha);
				VertexConsumer vcFX = buffers.getBuffer(TGRenderTypes.entityAdditive(RES_BIO_EFFECT));
				box.render(poseStack, vcFX, TGRenderHelper.FULLBRIGHT, OverlayTexture.NO_OVERLAY, overlayColor, overlayColor, overlayColor, 1.0f);
				poseStack.popPose();
			}
		}
	}

	/**
	 * Renders the model in RenderLiving
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void renderModelDeathLaser(EntityModel mainModel, ResourceLocation texture, LivingEntity entity, PoseStack poseStack, MultiBufferSource buffers, int packedLight, float f7, float f6, float f4, float p_77036_5_, float f13)
	{
		float prog = ((float)entity.deathTime / (float)MAX_DEATH_TIME);

		mainModel.setupAnim(entity, f7, f6, f4, p_77036_5_, f13);

		double mainColor = 1.0-Math.pow(prog, 2.0);
		double mainAlpha = Math.pow(1.0-prog, 2.0);
		VertexConsumer vcMain = buffers.getBuffer(RenderType.entityTranslucent(texture));
		mainModel.renderToBuffer(poseStack, vcMain, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, (float)mainColor, (float)mainColor, (float)mainAlpha);

		float overlayColor = (float) (0.5+(Math.sin((Math.sqrt(prog)+0.75)*2.0*Math.PI)/2));
		VertexConsumer vcFX = buffers.getBuffer(TGRenderTypes.entityAdditive(RES_LASER_EFFECT));
		mainModel.renderToBuffer(poseStack, vcFX, TGRenderHelper.FULLBRIGHT, OverlayTexture.NO_OVERLAY, overlayColor, overlayColor, overlayColor, 1.0f);
	}

}
