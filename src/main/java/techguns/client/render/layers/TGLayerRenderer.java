package techguns.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.capabilities.TGExtendedPlayer;
import techguns.gui.player.TGPlayerInventory;
import techguns.items.armors.GenericArmor;

/**
 * Renders the face/back/hand slot items on the player (1.12 client.render.entities.TGLayerRendererer)
 */
public class TGLayerRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	public TGLayerRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
		super(renderer);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
			float netHeadYaw, float headPitch) {
		if (player.isInvisible()) {
			return;
		}
		TGExtendedPlayer props = TGExtendedPlayer.get(player);
		if (props == null) {
			return;
		}

		if (isSlotVisible(TGPlayerInventory.SLOT_FACE, player)) {
			this.renderSlot(props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_FACE), player, poseStack, buffer, packedLight);
		}
		if (isSlotVisible(TGPlayerInventory.SLOT_BACK, player)) {
			this.renderSlot(props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_BACK), player, poseStack, buffer, packedLight);
		}
		if (isSlotVisible(TGPlayerInventory.SLOT_HAND, player)) {
			this.renderSlot(props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_HAND), player, poseStack, buffer, packedLight);
		}
	}

	protected boolean isSlotVisible(int slot, Player ply) {
		switch (slot) {
		case TGPlayerInventory.SLOT_FACE:
			ItemStack head = ply.getItemBySlot(EquipmentSlot.HEAD);
			if (!head.isEmpty() && head.getItem() instanceof GenericArmor a) {
				return !a.isHideFaceslot();
			}
			break;
		case TGPlayerInventory.SLOT_BACK:
			ItemStack body = ply.getItemBySlot(EquipmentSlot.CHEST);
			if (!body.isEmpty() && body.getItem() instanceof GenericArmor a) {
				return !a.isHideBackslot();
			}
			break;
		case TGPlayerInventory.SLOT_HAND:
			ItemStack b = ply.getItemBySlot(EquipmentSlot.CHEST);
			if (!b.isEmpty() && b.getItem() instanceof GenericArmor a) {
				return !a.isHideGloveslot();
			}
			break;
		}
		return true;
	}

	protected void renderSlot(ItemStack slot, Player player, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		if (!slot.isEmpty()) {
			RenderAdditionalSlotItem render = AdditionalSlotRenderRegistry.getRenderForItem(slot);
			if (render != null) {
				render.render(slot, player, this.getParentModel(), poseStack, buffer, packedLight);
			}
		}
	}
}
