package techguns.client.render.entities.npcs;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.models.npcs.ModelGenericNPC;
import techguns.client.models.npcs.ModelSuperMutant;
import techguns.client.models.npcs.NPCHumanoidModel;
import techguns.entities.npcs.SuperMutantBasic;

/**
 * 1.12 RenderSuperMutant: the model is scaled by getModelScale (NPCHumanoidModel) and moved up by getModelHeightOffset, the held
 * item is not scaled (1.12 postRenderArm(0.0625)), the weapon position is corrected by the NPC (LayerHeldItemTranslateGun).
 * 1.12 used the mutant model for the armor layer, the port uses the standard armor models scaled like the mutant.
 */
public class RenderSuperMutant<T extends SuperMutantBasic> extends RenderGenericNPC<T> {

	private static final ResourceLocation[] textures = { new ResourceLocation(Techguns.MODID, "textures/entity/supermutant_texture_1.png"),
			new ResourceLocation(Techguns.MODID, "textures/entity/supermutant_texture_2.png"),
			new ResourceLocation(Techguns.MODID, "textures/entity/supermutant_texture_3.png") };

	public RenderSuperMutant(EntityRendererProvider.Context ctx) {
		super(ctx, new ModelSuperMutant(), 0.5f, e -> textures[e.gettype()], true, null);
		this.addLayer(new HumanoidArmorLayer<T, NPCHumanoidModel<T>, NPCHumanoidModel<T>>(this, new NPCHumanoidModel<T>(new ModelGenericNPC(0.5f, true)), new NPCHumanoidModel<T>(new ModelGenericNPC(1.0f, true)), ctx.getModelManager()) {
			@Override
			public void render(PoseStack poseStack, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				float s = entity.getModelScale();
				poseStack.pushPose();
				poseStack.scale(s, s, s);
				super.render(poseStack, buffers, light, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
				poseStack.popPose();
			}
		});
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(0, entity.getModelHeightOffset(), 0);
		super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
		poseStack.popPose();
	}
}
