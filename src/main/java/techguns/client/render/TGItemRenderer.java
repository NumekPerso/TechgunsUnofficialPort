package techguns.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import techguns.client.render.item.IItemRenderer;

/**
 * Dispatches the rendering of Techguns items with a builtin/entity model to their IItemRenderer.
 * Vanilla applies the json display transforms and translate(-0.5,-0.5,-0.5) before calling this, the item models of the
 * port have identity display transforms (except ground) and the offset is undone here, so the pose is the same as in the
 * 1.12 ItemRenderHack.
 */
public class TGItemRenderer extends BlockEntityWithoutLevelRenderer {

	private static TGItemRenderer INSTANCE;

	public TGItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	public static TGItemRenderer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TGItemRenderer();
		}
		return INSTANCE;
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		IItemRenderer renderer = ItemRenderHack.getRendererForItem(stack.getItem());
		if (renderer == null) {
			return;
		}
		LivingEntity elb = ItemRenderHack.getEntityForContext(ctx);
		if (!ItemRenderHack.shouldRenderItem(stack, elb, ctx)) {
			return;
		}
		boolean leftHanded = ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;

		poseStack.pushPose();
		poseStack.translate(0.5f, 0.5f, 0.5f);
		renderer.renderItem(ctx, stack, elb, leftHanded, poseStack, buffers, packedLight, packedOverlay);
		poseStack.popPose();
	}
}
