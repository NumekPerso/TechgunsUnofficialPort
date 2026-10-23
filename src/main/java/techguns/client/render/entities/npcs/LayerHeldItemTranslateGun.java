package techguns.client.render.entities.npcs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import techguns.api.npc.INPCTechgunsShooter;

/**
 * Held item layer with an additional translation of the NPC (INPCTechgunsShooter weapon position), 1.12 LayerHeldItemTranslateGun
 */
public class LayerHeldItemTranslateGun<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends ItemInHandLayer<T, M> {

	protected final ItemInHandRenderer itemRenderer;

	public LayerHeldItemTranslateGun(RenderLayerParent<T, M> renderer, ItemInHandRenderer itemRenderer) {
		super(renderer, itemRenderer);
		this.itemRenderer = itemRenderer;
	}

	@Override
	protected void renderArmWithItem(LivingEntity ent, ItemStack stack, ItemDisplayContext transformType, HumanoidArm handSide, PoseStack poseStack, MultiBufferSource buffers, int light) {
		if (!stack.isEmpty()) {
			poseStack.pushPose();
			//the 1.12 sneaking offset is part of the arm pose in 1.20
			this.getParentModel().translateToHand(handSide, poseStack);

			poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			boolean flag = handSide == HumanoidArm.LEFT;
			poseStack.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);

			this.setEntityTranslation(poseStack, ent, flag);
			this.itemRenderer.renderItem(ent, stack, transformType, flag, poseStack, buffers, light);

			poseStack.popPose();
		}
	}

	protected void setEntityTranslation(PoseStack poseStack, LivingEntity ent, boolean lefthand) {
		if (ent instanceof INPCTechgunsShooter) {
			INPCTechgunsShooter shooter = (INPCTechgunsShooter) ent;

			poseStack.translate(lefthand ? -shooter.getWeaponPosX() : shooter.getWeaponPosX(), shooter.getWeaponPosY(), shooter.getWeaponPosZ());
		}
	}
}
