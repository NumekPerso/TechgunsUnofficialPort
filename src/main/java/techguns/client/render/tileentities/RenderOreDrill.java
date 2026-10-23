package techguns.client.render.tileentities;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.client.render.TGRenderHelper;
import techguns.tileentities.OreDrillTileEntMaster;

/**
 * Renders the rotating drill head (or the rod without drill head) of a formed ore drill
 */
public class RenderOreDrill implements BlockEntityRenderer<OreDrillTileEntMaster> {
	private static final int DRILLERCUBESPERBLOCK = 4;

	protected ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/blocks/drillhead.png");
	protected OreDrillCube tessellatorcube = new OreDrillCube(texture);

	protected ResourceLocation texture2 = new ResourceLocation(Techguns.MODID, "textures/blocks/drillhead_obsidian.png");
	private OreDrillCube tessellatorcube2 = new OreDrillCube(texture2);

	protected ResourceLocation texture3 = new ResourceLocation(Techguns.MODID, "textures/blocks/drillhead_carbon.png");
	private OreDrillCube tessellatorcube3 = new OreDrillCube(texture3);

	public RenderOreDrill(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public void render(OreDrillTileEntMaster tile, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {

		if (tile.isFormed() && tile.getDrill_direction() != null) {

			Direction drill_direction = tile.getDrill_direction();

			int[] vec = getNextBlockInDirOffset(0, 0, 0, drill_direction, tile.getEngines() + 1);
			int[] dir = getNextBlockInDirOffset(0, 0, 0, drill_direction, 1);

			poseStack.pushPose();
			//This is setting the initial location.
			poseStack.translate(0.5f - vec[0], 1.25f - vec[1], 0.5f - vec[2]);

			poseStack.pushPose();
			int rot = (int) (tile.getLevel().getGameTime() % 360);

			if (dir[1] < 0) {
				TGRenderHelper.rotate(poseStack, 180F, 0.0F, 0.0F, 1.0F);
			} else if (dir[1] > 0) {
				poseStack.translate(0, -1.5f, 0);
			} else if (dir[0] != 0) {
				TGRenderHelper.rotate(poseStack, -90F * dir[0], 0.0F, 0.0F, 1.0F);
				poseStack.translate(dir[0] * 0.75f, 0, 0);
				poseStack.translate(0, -0.75f, 0);
			} else if (dir[2] != 0) {
				TGRenderHelper.rotate(poseStack, 90F * dir[2], 1.0F, 0.0F, 0.0F);
				poseStack.translate(0, 0, dir[2] * 0.75f);
				poseStack.translate(0, -0.75f, 0);
			}
			poseStack.translate(0, 2 * (tile.getEngines() + 1), 0);

			if (tile.hasDrill() && tile.isRedstoneEnabled() && tile.hasPower()) {
				TGRenderHelper.rotate(poseStack, rot * 10f + ((1f / 36f) * partialTick), 0, 1, 0);
			}

			if (tile.hasDrill()) {
				if (tile.getDrillType() == 1) {
					drawDriller(tile, tessellatorcube, poseStack, buffers, light, overlay);
				} else if (tile.getDrillType() == 2) {
					drawDriller(tile, tessellatorcube2, poseStack, buffers, light, overlay);
				} else if (tile.getDrillType() == 3) {
					drawDriller(tile, tessellatorcube3, poseStack, buffers, light, overlay);
				}
			} else {
				drawDrillRod(tile, poseStack, buffers, light, overlay);
			}

			poseStack.popPose();
			poseStack.popPose();
		}
	}

	public static int[] getNextBlockInDirOffset(int x, int y, int z, Direction dir, int offset) {
		int[] pos = new int[] { x, y, z };
		if (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
			pos[dir.getAxis().ordinal()] += offset;
		} else {
			pos[dir.getAxis().ordinal()] -= offset;
		}
		return pos;
	}

	@Override
	public boolean shouldRenderOffScreen(OreDrillTileEntMaster te) {
		return te.isFormed();
	}

	@Override
	public int getViewDistance() {
		return 96;
	}

	protected void drawDriller(OreDrillTileEntMaster tile, OreDrillCube cube, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {

		int length = tile.getRods();

		poseStack.pushPose();

		int[] axis = new int[] { 0, 1, 0 };
		float height = 1.0f / DRILLERCUBESPERBLOCK;
		float pos = height;

		float max = (float) Math.log((length * DRILLERCUBESPERBLOCK + 1.0f));
		float radiusfactor = 1.0f;
		switch (tile.getDrillRadius()) {
		case 0:
		case 1:
			radiusfactor = 0.35f;
			break;
		case 2:
			radiusfactor = 0.5f;
			break;
		case 3:
			radiusfactor = 0.55f;
			break;
		case 4:
			radiusfactor = 0.60f;
			break;
		}

		for (int i = 0; i < length * DRILLERCUBESPERBLOCK; i++) {
			boolean rotate = false;
			if (i % 2 == 1) {
				rotate = true;
			}
			float factor = (float) Math.log((length * DRILLERCUBESPERBLOCK) - i + 1.0f);
			float size = ((tile.getDrillRadius() == 0 ? 1.0f : tile.getDrillRadius() * 1.0f) * factor) * (1.0f / max) * radiusfactor;

			poseStack.translate(axis[0] * height, axis[1] * height, axis[2] * height);

			if (rotate) {
				poseStack.pushPose();
				TGRenderHelper.rotate(poseStack, 45.0f, Math.abs(axis[0]), Math.abs(axis[1]), Math.abs(axis[2]));
			}

			cube.drawCube(axis[0] == 0 ? size : pos, (axis[1] == 0 ? size : pos), (axis[2] == 0 ? size : pos), poseStack, buffers, light, overlay);

			if (rotate) {
				poseStack.popPose();
			}
		}
		poseStack.popPose();
	}

	protected void drawDrillRod(OreDrillTileEntMaster tile, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {

		int length = tile.getRods();

		poseStack.pushPose();

		int[] axis = new int[] { 0, 1, 0 };
		float height = 0.5f;
		float pos = height;

		float f = -0.4f;
		poseStack.translate(axis[0] * height * f, axis[1] * height * f, axis[2] * height * f);
		for (int i = 0; i < length; i++) {
			float size = 0.2f;

			poseStack.translate(axis[0] * height * 2, axis[1] * height * 2, axis[2] * height * 2);

			poseStack.pushPose();
			TGRenderHelper.rotate(poseStack, 45.0f, Math.abs(axis[0]), Math.abs(axis[1]), Math.abs(axis[2]));

			tessellatorcube.drawCube(axis[0] == 0 ? size : pos, (axis[1] == 0 ? size : pos), (axis[2] == 0 ? size : pos), poseStack, buffers, light, overlay);

			poseStack.popPose();

		}
		poseStack.popPose();
	}
}
