package techguns.client.render.tileentities;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import techguns.Techguns;
import techguns.client.models.machines.ModelFabricator;
import techguns.client.render.TGRenderHelper;
import techguns.tileentities.FabricatorTileEntMaster;

/**
 * Renders the formed fabricator (the formed blocks have empty models) and the item being worked on
 */
public class RenderFabricator implements BlockEntityRenderer<FabricatorTileEntMaster> {
	protected ModelFabricator model = new ModelFabricator();
	protected ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/blocks/fabricator.png");

	public RenderFabricator(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public void render(FabricatorTileEntMaster te, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {

		if (te != null && te.isFormed()) {

			float progress = te.getProgress();
			Direction direction = te.getMultiblockDirection();

			poseStack.pushPose();
			poseStack.translate(0.5d, 1.5d, 0.5d);

			poseStack.pushPose();
			TGRenderHelper.rotate(poseStack, 180F, 0.0F, 0.0F, 1.0F);
			int dir = 0;
			if (direction != null) {
				dir = direction.get2DDataValue();
			}
			TGRenderHelper.rotate(poseStack, 180.0f + dir * 90.0f, 0.0F, 1.0F, 0.0F);

			//Render Item
			ItemStack itemStack = ItemStack.EMPTY;
			if (te.currentOperation != null) {

				if (progress < 0.8f) {
					itemStack = te.currentOperation.getItemInputI(FabricatorTileEntMaster.SLOT_PLATE);
					if (itemStack.isEmpty()) {
						itemStack = te.currentOperation.getItemInputI(FabricatorTileEntMaster.SLOT_INPUT1);
					}
				} else {
					itemStack = te.currentOperation.getItemOutput0();
				}
			}

			this.model.render(poseStack, buffers.getBuffer(RenderType.entityCutout(texture)), packedLight, packedOverlay, 0.0625f, progress);

			if (!itemStack.isEmpty()) {
				itemStack = itemStack.copy();
				itemStack.setCount(1);

				//1.12 rendered an EntityItem (hover start 0) at -0.5, 0, -0.6 in this frame
				poseStack.pushPose();
				TGRenderHelper.rotate(poseStack, +90, 1, 0, 0);
				poseStack.translate(-0.5f, 0.1f, -0.6f);
				poseStack.translate(0f, 0.125f, 0f);
				Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, buffers, te.getLevel(), (int) te.getBlockPos().asLong());
				poseStack.popPose();
			}

			//render glass last
			this.model.renderGlass(poseStack, buffers.getBuffer(RenderType.entityTranslucent(texture)), packedLight, packedOverlay, 0.0625f);

			poseStack.popPose();
			poseStack.popPose();

		}

	}

	@Override
	public boolean shouldRenderOffScreen(FabricatorTileEntMaster te) {
		return te.isFormed();
	}

}
