package techguns.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.client.models.additionalslots.ModelAdditionalSlotBase;
import techguns.items.armors.ICamoChangeable;

/**
 * Renders the model of an additional slot item on the player (1.12 client.render.RenderAdditionalSlotItem)
 */
public class RenderAdditionalSlotItem {

	protected ModelAdditionalSlotBase model;
	protected ModelAdditionalSlotBase model_slim = null;
	protected ResourceLocation[] textures;
	protected ResourceLocation[] textures_slim;

	public RenderAdditionalSlotItem(ModelAdditionalSlotBase model, String modid, String texture_name, int count) {
		this.model = model;
		this.textures = new ResourceLocation[count];
		for (int i = 0; i < count; i++) {
			textures[i] = new ResourceLocation(modid, texture_name + (i == 0 ? "" : ("_" + i)) + ".png");
		}
	}

	public RenderAdditionalSlotItem(ModelAdditionalSlotBase model, ResourceLocation texture) {
		this(model, null, texture, null);
	}

	public RenderAdditionalSlotItem(ModelAdditionalSlotBase model, ModelAdditionalSlotBase modelslim, ResourceLocation texture, ResourceLocation texture_slim) {
		this.model = model;
		this.model_slim = modelslim;
		this.textures = new ResourceLocation[] { texture };
		this.textures_slim = new ResourceLocation[] { texture_slim };
	}

	public void render(ItemStack slot, Player player, HumanoidModel<?> parentModel, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		boolean slim = false;
		if (model_slim != null && player instanceof AbstractClientPlayer acp) {
			if ("slim".equals(acp.getModelName())) {
				slim = true;
			}
		}

		ModelAdditionalSlotBase m = slim ? model_slim : model;

		int tex_index = 0;
		if (slot.getItem() instanceof ICamoChangeable) {
			tex_index = ((ICamoChangeable) slot.getItem()).getCurrentCamoIndex(slot);
		}
		ResourceLocation[] tex = slim ? textures_slim : textures;
		if (tex_index < 0 || tex_index >= tex.length) {
			tex_index = 0;
		}

		VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutoutNoCull(tex[tex_index]));
		m.render(player, parentModel, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 0.0625f);
	}
}
