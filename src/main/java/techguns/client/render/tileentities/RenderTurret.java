package techguns.client.render.tileentities;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.models.ModelMultipart;
import techguns.client.models.machines.ModelTurretBase;
import techguns.client.render.TGRenderHelper;
import techguns.tileentities.BasicMachineTileEnt;
import techguns.tileentities.TurretTileEnt;

/**
 * Renders the turret base block, rotated to the side the turret is mounted on. The texture depends on the turret armor.
 */
public class RenderTurret implements BlockEntityRenderer<TurretTileEnt> {

	protected static final ResourceLocation defaultTexture = new ResourceLocation(Techguns.MODID, "textures/blocks/turret_base.png");
	protected ModelMultipart model;

	public RenderTurret(ModelMultipart model) {
		super();
		this.model = model;
	}

	public RenderTurret() {
		this(new ModelTurretBase());
	}

	@Override
	public void render(TurretTileEnt te, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		this.renderBase(te, poseStack, buffers, packedLight, packedOverlay);
	}

	/**
	 * @param te null for the item
	 */
	public void renderBase(@Nullable TurretTileEnt te, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		ResourceLocation tex = te != null ? te.getTurretBaseTexture() : defaultTexture;

		//default up for TESR itemstack
		Direction facing = Direction.UP;
		if (te != null) {
			facing = te.getFacing();
		}

		poseStack.pushPose();
		switch (facing) {
		case DOWN:
			poseStack.translate(0.5d, -0.5d, 0.5d);
			TGRenderHelper.rotate(poseStack, 180F, 0.0F, 1.0F, 0.0F);
			break;
		case EAST:
			poseStack.translate(1.5d, 0.5d, 0.5d);
			TGRenderHelper.rotate(poseStack, 90F, 0.0F, 0.0F, 1.0F);
			break;
		case NORTH:
			poseStack.translate(0.5d, 0.5d, -0.5d);
			TGRenderHelper.rotate(poseStack, 90F, 1.0F, 0.0F, 0.0F);
			break;
		case SOUTH:
			poseStack.translate(0.5d, 0.5d, 1.5d);
			TGRenderHelper.rotate(poseStack, -90F, 1.0F, 0.0F, 0.0F);
			break;
		case WEST:
			poseStack.translate(-0.5d, 0.5d, 0.5d);
			TGRenderHelper.rotate(poseStack, -90F, 0.0F, 0.0F, 1.0F);
			break;
		case UP:
		default:
			poseStack.translate(0.5d, 1.5d, 0.5d);
			TGRenderHelper.rotate(poseStack, 180F, 0.0F, 0.0F, 1.0F);
			TGRenderHelper.rotate(poseStack, 180F, 0.0F, 1.0F, 0.0F);
			break;
		}

		this.model.render(poseStack, buffers.getBuffer(RenderType.entityCutout(tex)), packedLight, packedOverlay, null, 0, 0, 0, 0, 0, 0.0625f);

		poseStack.popPose();
	}

	/**
	 * Item rendering through TGMachineItemRenderer (which renders RenderMachines)
	 */
	public RenderMachine asItemRenderer() {
		final RenderTurret self = this;
		return new RenderMachine(null, defaultTexture) {
			@Override
			public void renderMachine(@Nullable BasicMachineTileEnt te, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
				self.renderBase(null, poseStack, buffers, packedLight, packedOverlay);
			}
		};
	}

}
