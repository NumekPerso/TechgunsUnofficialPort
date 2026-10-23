package techguns.client.render.entities.npcs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import techguns.client.models.ModelMultipart;
import techguns.client.models.npcs.ModelTurret;
import techguns.client.render.TGRenderHelper;
import techguns.entities.npcs.NPCTurret;
import techguns.items.guns.GenericGun;

/**
 * Renders the turret: stand (part 0), mount rotated with the pitch (part 1) and the gun of the turret block.
 * 1.12 RenderLiving with doRender fully overridden (no name tag, no death rotation).
 */
public class RenderNPCTurret extends EntityRenderer<NPCTurret> {
	private static final ModelMultipart model = new ModelTurret();
	private float gunOffsetY = 0.85f;

	public RenderNPCTurret(EntityRendererProvider.Context ctx) {
		super(ctx);
		this.shadowRadius = 0f;
	}

	@Override
	public ResourceLocation getTextureLocation(NPCTurret entity) {
		return entity.getTexture();
	}

	@Override
	public void render(NPCTurret turret, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		poseStack.pushPose();

		Direction facing = Direction.UP;
		if (turret.mountedTileEnt != null) {
			facing = turret.mountedTileEnt.getFacing();
		}
		float mirrorYaw = 1.0f;
		float mirrorPitch = 1.0f;
		float yawOffset = 0f;

		if (facing == Direction.DOWN) {
			mirrorYaw = -1f;
			mirrorPitch = -1f;
			yawOffset = 180.0f;
		}

		int overlay = OverlayTexture.pack(0f, turret.hurtTime > 0 || turret.deathTime > 0);
		VertexConsumer vc = buffers.getBuffer(RenderType.entityCutout(turret.getTexture()));

		this.translate(poseStack, facing, turret);
		this.rotatetoBase(poseStack, facing);

		float headYaw = Mth.rotLerp(partialTicks, turret.yHeadRotO, turret.yHeadRot);
		float pitch = Mth.lerp(partialTicks, turret.xRotO, turret.getXRot());

		TGRenderHelper.rotate(poseStack, headYaw * mirrorYaw + yawOffset, 0, 1f, 0);

		model.render(poseStack, vc, packedLight, overlay, turret, 0, 0, 0, 0, 0, 0.0625F, 0, 0.0f, ItemDisplayContext.FIXED, 0, 0.0f, 0f);

		float rotoffset = 0.9f;

		poseStack.translate(0, rotoffset, 0);
		TGRenderHelper.rotate(poseStack, pitch * mirrorPitch, 1f, 0, 0);
		poseStack.translate(0, -rotoffset, 0);
		model.render(poseStack, vc, packedLight, overlay, turret, 0, 0, 0, 0, 0, 0.0625F, 0, 0.0f, ItemDisplayContext.FIXED, 1, 0.0f, 0f);

		this.renderEquippedItems(poseStack, buffers, packedLight, turret);

		poseStack.popPose();
		super.render(turret, entityYaw, partialTicks, poseStack, buffers, packedLight);
	}

	@Override
	protected boolean shouldShowName(NPCTurret entity) {
		return false;
	}

	protected void rotatetoBase(PoseStack poseStack, Direction facing) {
		if (facing == Direction.UP) {
			TGRenderHelper.rotate(poseStack, 180.0f, 1f, 0, 0);
		}
	}

	protected void translate(PoseStack poseStack, Direction facing, NPCTurret turret) {
		if (facing == Direction.UP) {
			poseStack.translate(0, 1.5f, 0);
		} else if (facing == Direction.DOWN) {
			poseStack.translate(0, -0.6f, 0);
		}
	}

	protected void renderEquippedItems(PoseStack poseStack, MultiBufferSource buffers, int light, NPCTurret turret) {
		ItemStack gunstack = turret.getMainHandItem();
		if (!gunstack.isEmpty() && gunstack.getItem() instanceof GenericGun) {
			GenericGun gun = (GenericGun) gunstack.getItem();

			poseStack.pushPose();

			poseStack.translate(gun.turretPosOffsetX, gun.turretPosOffsetY + this.gunOffsetY, gun.turretPosOffsetZ);
			TGRenderHelper.rotate(poseStack, 180.0F, 1.0F, 0.0F, 0.0F);
			TGRenderHelper.rotate(poseStack, 180.0F, 0.0F, 1.0F, 0.0F);
			Minecraft.getInstance().getItemRenderer().renderStatic(turret, gunstack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, buffers, turret.level(), light, OverlayTexture.NO_OVERLAY, turret.getId());
			poseStack.popPose();
		}
	}

}
