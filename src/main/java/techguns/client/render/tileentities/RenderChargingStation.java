package techguns.client.render.tileentities;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import techguns.blocks.machines.SimpleMachine;
import techguns.client.render.TGRenderHelper;
import techguns.tileentities.ChargingStationTileEnt;

/**
 * Renders the item that is charged in the charging station (the station itself is an OBJ block model)
 */
public class RenderChargingStation implements BlockEntityRenderer<ChargingStationTileEnt> {

	public RenderChargingStation(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public void render(ChargingStationTileEnt te, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		if (te != null && (te.isWorking() || te.charging)) {
			ItemStack item = ItemStack.EMPTY;
			if (te.charging) {
				item = te.getChargingItem();
			} else {
				item = te.currentOperation.getItemInputI(0);
			}
			if (!item.isEmpty()) {
				BlockState bs = te.getBlockState();
				Direction dir = bs.hasProperty(SimpleMachine.FACING) ? bs.getValue(SimpleMachine.FACING) : Direction.SOUTH;

				poseStack.pushPose();
				poseStack.translate(0.5d, 0.4d, 0.5d);

				TGRenderHelper.rotate(poseStack, -90.0f * dir.get2DDataValue(), 0f, 1f, 0f);

				Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, buffers, te.getLevel(), (int) te.getBlockPos().asLong());

				poseStack.popPose();
			}
		}
	}

}
