package techguns.client.render.item;

import java.util.HashMap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import techguns.client.models.armor.IShieldModel;
import techguns.items.armors.GenericShield;

/**
 * 3D rendering of the Techguns shields (1.12 TileEntityItemRendererTGShield, one instance per shield). In 1.20 one
 * BlockEntityWithoutLevelRenderer dispatches to the model/textures registered for the shield item. The shield item models
 * use the display transforms of the vanilla shield, like in 1.12.
 */
public class TileEntityItemRendererTGShield extends BlockEntityWithoutLevelRenderer {

	private static TileEntityItemRendererTGShield INSTANCE;

	protected static final HashMap<Item, ShieldRenderInfo> SHIELDS = new HashMap<>();

	protected static class ShieldRenderInfo {
		protected final IShieldModel model;
		protected final ResourceLocation[] textures;

		protected ShieldRenderInfo(IShieldModel model, ResourceLocation[] textures) {
			this.model = model;
			this.textures = textures;
		}
	}

	public TileEntityItemRendererTGShield() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	public static TileEntityItemRendererTGShield getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TileEntityItemRendererTGShield();
		}
		return INSTANCE;
	}

	public static void registerShield(Item shield, IShieldModel model, ResourceLocation... textures) {
		SHIELDS.put(shield, new ShieldRenderInfo(model, textures));
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		if (stack.getItem() instanceof GenericShield) {
			GenericShield s = (GenericShield) stack.getItem();
			ShieldRenderInfo info = SHIELDS.get(s);
			if (info == null) {
				return;
			}
			int index = s.getCurrentCamoIndex(stack);

			if (index >= 0 && index < info.textures.length) {
				//translucent: the visors / windows of the shield textures are half transparent (1.12 rendered items with blending)
				VertexConsumer vc = ItemRenderer.getFoilBufferDirect(buffers, RenderType.entityTranslucent(info.textures[index]), false, stack.hasFoil());
				poseStack.pushPose();
				poseStack.scale(1.0F, -1.0F, -1.0F);
				info.model.render(poseStack, vc, packedLight, packedOverlay, 0.0625F);
				poseStack.popPose();
			}
		}
	}
}
