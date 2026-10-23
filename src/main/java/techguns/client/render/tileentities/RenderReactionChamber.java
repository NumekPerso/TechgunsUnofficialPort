package techguns.client.render.tileentities;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import techguns.Techguns;
import techguns.client.TGFluidsClient;
import techguns.client.render.TGRenderHelper;
import techguns.client.render.TGRenderTypes;
import techguns.tileentities.ReactionChamberTileEntMaster;
import techguns.tileentities.operation.ReactionBeamFocus;

/**
 * Reaction chamber (1.12 RenderReactionChamber TESR): item inside the chamber, beam of the focus while working,
 * fluid in the chamber. The chamber itself is the OBJ block model of the formed controller.
 */
public class RenderReactionChamber implements BlockEntityRenderer<ReactionChamberTileEntMaster> {

	protected static final ResourceLocation heatrayTexture = new ResourceLocation(Techguns.MODID, "textures/fx/heatray.png");

	private static final double ONE_SIXTEENTH = 0.0625;

	public RenderReactionChamber(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public boolean shouldRenderOffScreen(ReactionChamberTileEntMaster te) {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 96;
	}

	@Override
	public void render(ReactionChamberTileEntMaster te, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {

		if (te.isFormed() && te.getMultiblockDirection() != null) {
			BlockPos centerPos = te.getBlockPos().relative(te.getMultiblockDirection()).above();

			//pass 0: item
			ItemStack item = ItemStack.EMPTY;
			if (te.isWorking() && te.getCurrentReaction() != null) {
				item = te.getCurrentReaction().getItemInputI(0);
			} else {
				item = te.input.get();
			}

			if (!item.isEmpty()) {

				double px = (-te.getBlockPos().getX() + centerPos.getX()) + 0.5d;
				double py = (-te.getBlockPos().getY() + centerPos.getY()) + 0.5d;
				double pz = (-te.getBlockPos().getZ() + centerPos.getZ()) + 0.5d;

				poseStack.pushPose();
				poseStack.translate(px, py, pz);
				TGRenderHelper.rotate(poseStack, -90.0f * te.getMultiblockDirection().getOpposite().get2DDataValue(), 0f, 1f, 0f);

				int light = te.getLevel() != null ? net.minecraft.client.renderer.LevelRenderer.getLightColor(te.getLevel(), centerPos) : packedLight;
				Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GROUND, light, packedOverlay, poseStack, buffers, te.getLevel(), 0);

				poseStack.popPose();
			}

			//pass 1: Render Focus Effect
			if (te.isWorking()) {

				poseStack.pushPose();

				double px = (-te.getBlockPos().getX() + centerPos.getX()) + 0.5d;
				double py = 0.5d;
				double pz = (-te.getBlockPos().getZ() + centerPos.getZ()) + 0.5d;
				poseStack.translate(px, py, pz);

				ItemStack stack = te.getInventory().getStackInSlot(ReactionChamberTileEntMaster.SLOT_FOCUS);
				if (!stack.isEmpty()) {
					ReactionBeamFocus focus = ReactionBeamFocus.getBeamFocus(stack);
					renderBeamFocusEffect(focus, te, partialTicks, poseStack, buffers);
				}

				poseStack.popPose();
			}

			//Render Fluid
			FluidStack liquid = te.inputTank.getFluid();
			if (!liquid.isEmpty()) {

				double w = 1.875 * 0.5;
				double h1 = 0.5;
				double h2 = 2.9375;
				double level = (double) te.inputTank.getFluidAmount() / (double) te.inputTank.getCapacity();

				TextureAtlasSprite tex;
				if (te.isWorking()) {
					tex = TGFluidsClient.getFlowingSprite(liquid);
				} else {
					tex = TGFluidsClient.getStillSprite(liquid);
				}

				//set fluid color
				int clr = TGFluidsClient.getTintColor(liquid);

				double dx = 0;
				double dz = 0;

				switch (te.getMultiblockDirection()) {
				case EAST:
					dx = 0.5 + ONE_SIXTEENTH;
					dz = -0.5 + ONE_SIXTEENTH;
					break;
				case WEST:
					dx = -1.5 + ONE_SIXTEENTH;
					dz = -0.5 + ONE_SIXTEENTH;
					break;
				case NORTH:
					dx = -0.5 + ONE_SIXTEENTH;
					dz = -1.5 + ONE_SIXTEENTH;
					break;
				case SOUTH:
					dx = -0.5 + ONE_SIXTEENTH;
					dz = 0.5 + ONE_SIXTEENTH;
					break;
				default:
					break;
				}

				VertexConsumer buffer = buffers.getBuffer(TGRenderTypes.fxAlpha(InventoryMenu.BLOCK_ATLAS));
				this.drawFluidCubeWithTesselator(poseStack.last().pose(), buffer, tex, dx, h1, dz, 2 * w, h2 * level, h2, clr);
			}
		}

	}

	private void renderBeamFocusEffect(ReactionBeamFocus focus, ReactionChamberTileEntMaster tile, float ptt, PoseStack poseStack, MultiBufferSource buffers) {

		//TODO: different effect for other focus

		//----------------
		//HEAT RAY
		double d = ((double) (tile.progress % 10) + ptt) / 10.0;
		d = 0.875 + (0.125 * (1.0 - Math.cos(Math.PI * d * 2.0)));

		float w = (float) (1.875 * 0.5 * d);
		float h1 = 0.5f;
		float h2 = 2.9375f;

		Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		double x1 = cam.x;
		double z1 = cam.z;

		BlockPos pos = tile.getBlockPos().relative(tile.getMultiblockDirection());
		float angle = (float) (Math.atan2(pos.getX() + 0.5d - x1, pos.getZ() + 0.5d - z1) * 180.0 / Math.PI);

		TGRenderHelper.rotate(poseStack, angle, 0f, 1f, 0f);

		VertexConsumer buf = buffers.getBuffer(TGRenderTypes.fxAdditive(heatrayTexture));
		Matrix4f m = poseStack.last().pose();

		buf.vertex(m, -w, h1, 0f).color(1f, 1f, 1f, 1f).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
		buf.vertex(m, -w, h2, 0f).color(1f, 1f, 1f, 1f).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
		buf.vertex(m, w, h2, 0f).color(1f, 1f, 1f, 1f).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
		buf.vertex(m, w, h1, 0f).color(1f, 1f, 1f, 1f).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
	}

	protected void drawFluidCubeWithTesselator(Matrix4f m, VertexConsumer buffer, TextureAtlasSprite tex, double x, double y, double z, double width, double height, double maxHeight, int color) {

		if (tex == null) {
			return;
		}

		int interpV = 16;

		if (height < maxHeight * 0.5d) {

			double factor = height / (maxHeight * 0.5d);
			interpV = (int) (factor * 16);

			this.drawFluidTankPlane(m, buffer, x, y, z, tex, width, height, 0, interpV, color);
			this.drawFluidTankPlane(m, buffer, x, y, z, tex, 0, height, width, interpV, color);
			this.drawFluidTankPlane(m, buffer, x + width, y, z + width, tex, -width, height, 0, interpV, color);
			this.drawFluidTankPlane(m, buffer, x + width, y, z + width, tex, 0, height, -width, interpV, color);
		} else {

			float mhalf = (float) (maxHeight * 0.5f);

			this.drawFluidTankPlane(m, buffer, x, y, z, tex, width, mhalf, 0, 16, color);
			this.drawFluidTankPlane(m, buffer, x, y, z, tex, 0, mhalf, width, 16, color);
			this.drawFluidTankPlane(m, buffer, x + width, y, z + width, tex, -width, mhalf, 0, 16, color);
			this.drawFluidTankPlane(m, buffer, x + width, y, z + width, tex, 0, mhalf, -width, 16, color);

			double h = (height - mhalf);
			double factor = h / mhalf;
			interpV = (int) (factor * 16);

			this.drawFluidTankPlane(m, buffer, x, y + mhalf, z, tex, width, h, 0, interpV, color);
			this.drawFluidTankPlane(m, buffer, x, y + mhalf, z, tex, 0, h, width, interpV, color);
			this.drawFluidTankPlane(m, buffer, x + width, y + mhalf, z + width, tex, -width, h, 0, interpV, color);
			this.drawFluidTankPlane(m, buffer, x + width, y + mhalf, z + width, tex, 0, h, -width, interpV, color);

		}

		int red = (color >> 16 & 0xFF);
		int green = (color >> 8 & 0xFF);
		int blue = (color & 0xFF);
		int alpha = ((color >> 24) & 0xFF);

		float top = (float) (height + 0.5f);
		buffer.vertex(m, (float) x, top, (float) z).color(red, green, blue, alpha).uv(tex.getU0(), tex.getV0()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
		buffer.vertex(m, (float) x, top, (float) (z + width)).color(red, green, blue, alpha).uv(tex.getU0(), tex.getV1()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
		buffer.vertex(m, (float) (x + width), top, (float) (z + width)).color(red, green, blue, alpha).uv(tex.getU1(), tex.getV1()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
		buffer.vertex(m, (float) (x + width), top, (float) z).color(red, green, blue, alpha).uv(tex.getU1(), tex.getV0()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
	}

	protected void drawFluidTankPlane(Matrix4f m, VertexConsumer buffer, double x, double y, double z, TextureAtlasSprite icon, double w_x, double h, double w_z, int interp_v, int color) {
		int red = (color >> 16 & 0xFF);
		int green = (color >> 8 & 0xFF);
		int blue = (color & 0xFF);
		int alpha = ((color >> 24) & 0xFF);

		float v = icon.getV(interp_v);
		buffer.vertex(m, (float) x, (float) (y + h), (float) (z + w_z)).color(red, green, blue, alpha).uv(icon.getU0(), v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
		buffer.vertex(m, (float) (x + w_x), (float) (y + h), (float) z).color(red, green, blue, alpha).uv(icon.getU1(), v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
		buffer.vertex(m, (float) (x + w_x), (float) y, (float) z).color(red, green, blue, alpha).uv(icon.getU1(), icon.getV0()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
		buffer.vertex(m, (float) x, (float) y, (float) (z + w_z)).color(red, green, blue, alpha).uv(icon.getU0(), icon.getV0()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(TGRenderTypes.FX_LIGHT).normal(0f, 1f, 0f).endVertex();
	}
}
