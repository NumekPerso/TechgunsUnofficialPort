package techguns.client.render.item;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Port of the 1.12 IItemRenderer used by the ItemRenderHack. The pose stack is in the same state as the GL matrix was
 * in 1.12 when the hook was called (vanilla item transforms applied, no -0.5 offset, no json display transforms except ground).
 */
public interface IItemRenderer {
	void renderItem(ItemDisplayContext transform, ItemStack stack, @Nullable LivingEntity elb, boolean leftHanded, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay);
}
