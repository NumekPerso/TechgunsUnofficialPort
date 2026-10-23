package techguns.client.render.tileentities;

import java.util.HashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Item rendering of machines drawn by a block entity renderer (1.12 ForgeHooksClient.registerTESRItemStack).
 * The item model (builtin/entity) keeps the 1.12 display transforms, the pose is the same as for the 1.12 TESR item.
 */
public class TGMachineItemRenderer extends BlockEntityWithoutLevelRenderer {

	private static TGMachineItemRenderer INSTANCE;

	protected static final HashMap<Item, RenderMachine> RENDERERS = new HashMap<>();

	public TGMachineItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	public static TGMachineItemRenderer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TGMachineItemRenderer();
		}
		return INSTANCE;
	}

	public static void registerMachineItemRenderer(ItemLike item, RenderMachine renderer) {
		RENDERERS.put(item.asItem(), renderer);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		RenderMachine renderer = RENDERERS.get(stack.getItem());
		if (renderer != null) {
			renderer.renderMachine(null, poseStack, buffers, packedLight, packedOverlay);
		}
	}
}
