package techguns.client.render.tileentities;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import techguns.blocks.machines.SimpleMachine;
import techguns.client.render.TGRenderHelper;
import techguns.tileentities.BlastFurnaceTileEnt;

/**
 * Draws the glowing lava pane in the front opening of a working blast furnace (1.12 RenderBlastfurnace TESR)
 */
public class RenderBlastfurnace implements BlockEntityRenderer<BlastFurnaceTileEnt> {

	private static final float UNIT = 1f / 16f;
	private static final ResourceLocation LAVA_FLOW = new ResourceLocation("minecraft", "block/lava_flow");

	public RenderBlastfurnace(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public void render(BlastFurnaceTileEnt te, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		if (te.isWorking()) {
			BlockState bs = te.getBlockState();
			if (!bs.hasProperty(SimpleMachine.FACING)) {
				return;
			}
			Direction rot = bs.getValue(SimpleMachine.FACING);

			TextureAtlasSprite tex = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(LAVA_FLOW);

			float nx, ny, nz;
			nx = -rot.getStepX();
			ny = -rot.getStepY();
			nz = -rot.getStepZ();

			VertexConsumer buffer = buffers.getBuffer(RenderType.entitySolid(InventoryMenu.BLOCK_ATLAS));
			double x = 0, y = 0, z = 0;
			switch (rot) {
			case NORTH:
				drawGlowPane(poseStack, buffer, x + UNIT * 3, y + 3 * UNIT, z + 0.06f, tex, UNIT * 10, 6 * UNIT, 0, 16, nx, ny, nz, false);
				break;
			case EAST:
				drawGlowPane(poseStack, buffer, x + UNIT * 15f + 0.0025f, y + 3 * UNIT, z + UNIT * 3, tex, 0, 6 * UNIT, UNIT * 10, 16, nx, ny, nz, true);
				break;
			case WEST:
				drawGlowPane(poseStack, buffer, x + 0.06f, y + 3 * UNIT, z + UNIT * 3, tex, 0, 6 * UNIT, UNIT * 10, 16, nx, ny, nz, false);
				break;
			case SOUTH:
				drawGlowPane(poseStack, buffer, x + UNIT * 3, y + 3 * UNIT, z + 15 * UNIT + 0.0025f, tex, UNIT * 10, 6 * UNIT, 0, 16, nx, ny, nz, true);
				break;
			default:
				break;
			}
		}
	}

	protected static void vertex(PoseStack poseStack, VertexConsumer buffer, double x, double y, double z, float u, float v, float nx, float ny, float nz) {
		Matrix4f pose = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		buffer.vertex(pose, (float) x, (float) y, (float) z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderHelper.FULLBRIGHT).normal(normal, nx, ny, nz).endVertex();
	}

	protected void drawGlowPane(PoseStack poseStack, VertexConsumer buffer, double x, double y, double z, TextureAtlasSprite icon, double w_x, double h, double w_z, int interp_v, float nx, float ny, float nz, boolean flip) {
		float vInterp = icon.getV(interp_v);
		if (flip) {
			vertex(poseStack, buffer, x + 0, y + 0, z + w_z, icon.getU0(), icon.getV0(), nx, ny, nz);
			vertex(poseStack, buffer, x + w_x, y + 0, z, icon.getU1(), icon.getV0(), nx, ny, nz);
			vertex(poseStack, buffer, x + w_x, y + h, z, icon.getU1(), vInterp, nx, ny, nz);
			vertex(poseStack, buffer, x + 0, y + h, z + w_z, icon.getU0(), vInterp, nx, ny, nz);
		} else {
			vertex(poseStack, buffer, x + 0, y + h, z + w_z, icon.getU0(), vInterp, nx, ny, nz);
			vertex(poseStack, buffer, x + w_x, y + h, z, icon.getU1(), vInterp, nx, ny, nz);
			vertex(poseStack, buffer, x + w_x, y + 0, z, icon.getU1(), icon.getV0(), nx, ny, nz);
			vertex(poseStack, buffer, x + 0, y + 0, z + w_z, icon.getU0(), icon.getV0(), nx, ny, nz);
		}
	}

}
