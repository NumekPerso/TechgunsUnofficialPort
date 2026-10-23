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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import techguns.Techguns;
import techguns.api.render.IItemTGRenderer;
import techguns.blocks.machines.SimpleMachine;
import techguns.client.render.TGRenderHelper;
import techguns.items.guns.GenericGun;
import techguns.tileentities.GrinderTileEnt;

/**
 * Rotating grinder rolls and the item being grinded (1.12 RenderGrinder TESR)
 */
public class RenderGrinder implements BlockEntityRenderer<GrinderTileEnt> {

	protected ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/blocks/grinder_roll.png");
	protected static final double UNIT = 0.0625;

	protected static final float TEX_X = (float) (2 * UNIT);
	protected static final float TEX_Z = (float) (2 * UNIT);
	protected static double len = 12 * UNIT;

	public RenderGrinder(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public void render(GrinderTileEnt te, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {

		//Rotate y
		BlockState bs = te.getBlockState();

		if (bs.hasProperty(SimpleMachine.FACING)) {

			Direction f = bs.getValue(SimpleMachine.FACING);

			float rot = 45f;
			poseStack.pushPose();

			if (te.isWorking()) {
				rot = ((45 + (1080 * te.getProgress())) % 360);
			}

			poseStack.pushPose();

			TGRenderHelper.rotate(poseStack, f.toYRot(), 0, 1, 0);
			switch (f) {
			case NORTH:
				poseStack.translate(-1, 0, -1);
				break;
			case EAST:
				poseStack.translate(0, 0, -1);
				break;
			case WEST:
				poseStack.translate(-1, 0, 0);
				break;
			default:
				break;
			}

			VertexConsumer buf = buffers.getBuffer(RenderType.entitySolid(texture));

			//draw grinder
			poseStack.pushPose();
			poseStack.translate(0, 7.5 * UNIT, 6.5 * UNIT);
			TGRenderHelper.rotate(poseStack, rot, 1, 0, 0);
			drawGrinderRoll(poseStack, buf, packedLight, 1.5f, 2);
			poseStack.popPose();

			poseStack.pushPose();
			poseStack.translate(0, 7.5 * UNIT, 9.5 * UNIT);
			TGRenderHelper.rotate(poseStack, -rot, 1, 0, 0);
			drawGrinderRoll(poseStack, buf, packedLight, 1.5f, 2);
			poseStack.popPose();

			//RENDER ITEM
			ItemStack item = ItemStack.EMPTY;
			if (te.isWorking()) {
				item = te.currentOperation.getItemInputI(0);

				if (!item.isEmpty()) {

					boolean is3d = false;
					boolean rot90 = false;
					if (item.getItem() instanceof IItemTGRenderer) {
						is3d = ((IItemTGRenderer) item.getItem()).shouldUseRenderHack(item);
						if (item.getItem() instanceof GenericGun) {
							rot90 = true;
						}
					}

					double px = 0.5d;
					double py = 0.6d - te.getProgress() * 0.4d + (is3d ? 0.15d : 0);
					double pz = 0.5d;

					poseStack.pushPose();
					poseStack.translate(px, py, pz);

					if (is3d) {
						poseStack.scale(0.5f, 0.5f, 0.5f);
					}
					if (rot90) {
						TGRenderHelper.rotate(poseStack, 90f, 0, 1, 0);
					}

					Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, buffers, te.getLevel(), (int) te.getBlockPos().asLong());

					poseStack.popPose();
				}
			}

			poseStack.popPose();
			poseStack.popPose();
		}
	}

	protected static void vertex(PoseStack poseStack, VertexConsumer buffer, int light, double x, double y, double z, double u, double v, float nx, float ny, float nz) {
		Matrix4f pose = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		buffer.vertex(pose, (float) x, (float) y, (float) z).color(255, 255, 255, 255).uv((float) u, (float) v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, nx, ny, nz).endVertex();
	}

	protected void drawGrinderRoll(PoseStack ps, VertexConsumer buf, int l, float r, float dx) {
		double d = 2 * r;
		r = (float) (UNIT * r);

		//draw top face
		vertex(ps, buf, l, dx * UNIT, r, -r, TEX_X, TEX_Z, 0, 1, 0);
		vertex(ps, buf, l, dx * UNIT, r, r, TEX_X, TEX_Z + d * UNIT, 0, 1, 0);
		vertex(ps, buf, l, dx * UNIT + len, r, r, TEX_X + len, TEX_Z + d * UNIT, 0, 1, 0);
		vertex(ps, buf, l, dx * UNIT + len, r, -r, TEX_X + len, TEX_Z, 0, 1, 0);

		//draw side face
		vertex(ps, buf, l, dx * UNIT, r, r, TEX_X, TEX_Z, -1, 0, 0);
		vertex(ps, buf, l, dx * UNIT, -r, r, TEX_X, TEX_Z + d * UNIT, -1, 0, 0);
		vertex(ps, buf, l, dx * UNIT + len, -r, r, TEX_X + len, TEX_Z + d * UNIT, -1, 0, 0);
		vertex(ps, buf, l, dx * UNIT + len, r, r, TEX_X + len, TEX_Z, -1, 0, 0);

		//draw down face
		vertex(ps, buf, l, dx * UNIT, -r, -r, TEX_X, TEX_Z + d * UNIT, 0, -1, 0);
		vertex(ps, buf, l, dx * UNIT + len, -r, -r, TEX_X + len, TEX_Z + d * UNIT, 0, -1, 0);
		vertex(ps, buf, l, dx * UNIT + len, -r, r, TEX_X + len, TEX_Z, 0, -1, 0);
		vertex(ps, buf, l, dx * UNIT, -r, r, TEX_X, TEX_Z, 0, -1, 0);

		//draw side face2
		vertex(ps, buf, l, dx * UNIT, r, -r, TEX_X, TEX_Z + d * UNIT, 1, 0, 0);
		vertex(ps, buf, l, dx * UNIT + len, r, -r, TEX_X + len, TEX_Z + d * UNIT, 1, 0, 0);
		vertex(ps, buf, l, dx * UNIT + len, -r, -r, TEX_X + len, TEX_Z, 1, 0, 0);
		vertex(ps, buf, l, dx * UNIT, -r, -r, TEX_X, TEX_Z, 1, 0, 0);
	}
}
