package techguns.client.render.entities.npcs;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import techguns.client.models.npcs.NPCHumanoidModel;
import techguns.entities.npcs.ZombiePigmanSoldier;

/**
 * 1.12 RenderZombiePigmanSoldier used the vanilla zombie pigman texture with a biped model. The zombie pigman texture doesn't
 * exist anymore in 1.20, the zombified piglin model + texture are used instead (with the NPC arm poses).
 */
public class RenderZombiePigmanSoldier extends HumanoidMobRenderer<ZombiePigmanSoldier, RenderZombiePigmanSoldier.PigmanSoldierModel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/piglin/zombified_piglin.png");

	public static class PigmanSoldierModel extends PiglinModel<ZombiePigmanSoldier> {
		public PigmanSoldierModel(ModelPart root) {
			super(root);
		}

		@Override
		public void prepareMobModel(ZombiePigmanSoldier entity, float limbSwing, float limbSwingAmount, float partialTick) {
			super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
			NPCHumanoidModel.setNPCArmPoses(this, entity);
		}
	}

	public RenderZombiePigmanSoldier(EntityRendererProvider.Context ctx) {
		super(ctx, new PigmanSoldierModel(ctx.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN)), 0.5f, 1.0019531F, 1.0F, 1.0019531F);
		this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR)), new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR)), ctx.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(ZombiePigmanSoldier entity) {
		return TEXTURE;
	}
}
