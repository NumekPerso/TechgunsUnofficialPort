package techguns.client.render.entities.npcs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.models.TGObjModel;
import techguns.client.render.TGRenderHelper;
import techguns.entities.npcs.AttackHelicopter;

/**
 * 1.12 RenderAttackHelicopter (RenderLiving with doRender fully overridden: no name tag, no death rotation). The three OBJ parts
 * (body, rotor, gun) are read with TGObjModel (1.12 blockstates helicopter0-2: flip-v).
 */
public class RenderAttackHelicopter extends EntityRenderer<AttackHelicopter> {

	public static ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/entity/apache.png");

	private static final TGObjModel helicopter_main = new TGObjModel(new ResourceLocation(Techguns.MODID, "models/item/npc/helicopter0.obj"), true);
	private static final TGObjModel helicopter_rotor = new TGObjModel(new ResourceLocation(Techguns.MODID, "models/item/npc/helicopter1.obj"), true);
	private static final TGObjModel helicopter_gun = new TGObjModel(new ResourceLocation(Techguns.MODID, "models/item/npc/helicopter2.obj"), true);

	public RenderAttackHelicopter(EntityRendererProvider.Context ctx) {
		super(ctx);
		this.shadowRadius = 5.0f;
	}

	@Override
	public ResourceLocation getTextureLocation(AttackHelicopter entity) {
		return texture;
	}

	@Override
	protected boolean shouldShowName(AttackHelicopter entity) {
		return false;
	}

	@Override
	public void render(AttackHelicopter entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		if (entity.deathTime >= AttackHelicopter.MAX_DEATH_TIME) {
			return;
		}
		poseStack.pushPose();
		TGRenderHelper.rotate(poseStack, -interp(entity.getYRot(), entity.yRotO, partialTicks) - 90f, 0.0F, 1.0F, 0.0F);

		float scale = 2.5f;
		poseStack.scale(scale, scale, scale);

		VertexConsumer vc = buffers.getBuffer(RenderType.entityCutout(texture));
		int overlay = OverlayTexture.NO_OVERLAY;
		long time = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0;

		if (entity.deathTime == 0) {
			helicopter_main.render(poseStack, vc, packedLight, overlay);

			poseStack.pushPose();
			float angle = ((float) ((time % 60) + partialTicks) * 24.0f);
			TGRenderHelper.rotate(poseStack, angle, 0f, 1f, 0f);
			helicopter_rotor.render(poseStack, vc, packedLight, overlay);
			poseStack.popPose();

			poseStack.pushPose();
			float offsetX = 1.2f;
			poseStack.translate(offsetX, 0, 0);
			TGRenderHelper.rotate(poseStack, -interp(entity.getXRot(), entity.xRotO, partialTicks), 0f, 0f, 1f);
			poseStack.translate(-offsetX, 0, 0);
			helicopter_gun.render(poseStack, vc, packedLight, overlay);
			poseStack.popPose();

		} else {
			float deathProgress = ((float) entity.deathTime + partialTicks) / (float) AttackHelicopter.MAX_DEATH_TIME;
			float p = (float) Math.pow(deathProgress, 2);
			poseStack.translate(0, p * -4f, 0);
			float angle = p * 720.0f;
			//1.12 rotated twice (GL11.glRotatef + GlStateManager.rotate)
			TGRenderHelper.rotate(poseStack, angle, 0, 1.0f, 0);
			TGRenderHelper.rotate(poseStack, angle, 0, 1f, 0);
			helicopter_main.render(poseStack, vc, packedLight, overlay);
			helicopter_gun.render(poseStack, vc, packedLight, overlay);
			angle = ((float) ((time % 60) + partialTicks) * 12.0f);
			TGRenderHelper.rotate(poseStack, angle, 0, 1f, 0);
			helicopter_rotor.render(poseStack, vc, packedLight, overlay);
		}

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
	}

	public float interp(float val, float prev_value, float ptt) {
		return prev_value + (val - prev_value) * ptt;
	}
}
