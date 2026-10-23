package techguns.client;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.Techguns;
import techguns.items.guns.GenericGrenade;
import techguns.items.guns.GenericGunCharge;
import techguns.items.guns.GenericGunMeleeCharge;
import techguns.util.BlockUtils;

/**
 * Client side gun events of the 1.12 TGEventHandler: mining tool block highlight and the equip progress fix for charging guns / grenades.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID, value = Dist.CLIENT)
public class TGGunClientEvents {

	/**
	 * 1.12 onRenderHand: keep the hand item fully equipped while charging a gun / grenade (using the item resets the equip progress).
	 * 1.20 fires RenderHandEvent after the equip progress of the frame is computed, the fields are set before rendering instead.
	 */
	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase != TickEvent.Phase.START) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer ply = mc.player;
		if (ply == null || mc.gameRenderer == null) {
			return;
		}
		float t = 1.0f;
		ItemStack stack = ply.getUseItem();

		if (!stack.isEmpty() && ((stack.getItem() instanceof GenericGunCharge && ((GenericGunCharge) stack.getItem()).hasRightClickAction()) || stack.getItem() instanceof GenericGrenade)) {
			InteractionHand hand = ply.getUsedItemHand();

			ItemInHandRenderer itemrenderer = mc.gameRenderer.itemInHandRenderer;
			if (hand == InteractionHand.MAIN_HAND) {
				if (itemrenderer.mainHandHeight < t) {
					itemrenderer.mainHandHeight = t;
					itemrenderer.oMainHandHeight = t;
				}
			} else {
				if (itemrenderer.offHandHeight < t) {
					itemrenderer.offHandHeight = t;
					itemrenderer.oOffHandHeight = t;
				}
			}
		}
	}

	/**
	 * 1.12 onBlockHighlight: draw the selection box of all blocks a mining tool with mining radius will break
	 */
	@SubscribeEvent
	public static void onBlockHighlight(RenderHighlightEvent.Block event) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer ply = mc.player;
		if (ply == null || mc.level == null) {
			return;
		}
		ItemStack item = ply.getMainHandItem();
		if (!ply.isShiftKeyDown() && !item.isEmpty() && item.getItem() instanceof GenericGunMeleeCharge g) {
			if (g.getMiningRadius(item) > 0 && g.getAmmoLeft(item) > 0) {
				BlockHitResult target = event.getTarget();
				if (target != null && target.getType() == HitResult.Type.BLOCK) {
					BlockPos p = target.getBlockPos();
					List<BlockPos> otherblocks = BlockUtils.getBlockPlaneAroundAxisForMining(mc.level, ply, p, target.getDirection().getAxis(), g.getMiningRadius(item), false, g, item);

					Vec3 cam = event.getCamera().getPosition();
					PoseStack poseStack = event.getPoseStack();
					VertexConsumer buffer = event.getMultiBufferSource().getBuffer(RenderType.lines());

					for (BlockPos b : otherblocks) {
						BlockState state = mc.level.getBlockState(b);
						if (!state.isAir() && mc.level.getWorldBorder().isWithinBounds(b)) {
							VoxelShape shape = state.getShape(mc.level, b, CollisionContext.of(ply));
							for (AABB box : shape.toAabbs()) {
								LevelRenderer.renderLineBox(poseStack, buffer, box.move(b.getX() - cam.x, b.getY() - cam.y, b.getZ() - cam.z), 0.0f, 0.0f, 0.0f, 0.4f);
							}
						}
					}
				}
			}
		}
	}
}
