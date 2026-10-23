package techguns.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import techguns.client.render.TGItemRenderer;
import techguns.items.guns.GenericGun;

/**
 * Client item extensions of Techguns items: custom renderer and the "aimed bow" arm pose of guns
 * (1.12 did this in RenderLivingEvent.Pre).
 */
public class TGItemClientExtensions implements IClientItemExtensions {

	public static final TGItemClientExtensions INSTANCE = new TGItemClientExtensions();

	@Override
	public BlockEntityWithoutLevelRenderer getCustomRenderer() {
		return TGItemRenderer.getInstance();
	}

	@Override
	public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
		if (!itemStack.isEmpty() && itemStack.getItem() instanceof GenericGun gun && gun.hasBowAnim()) {
			if (hand == InteractionHand.MAIN_HAND) {
				return HumanoidModel.ArmPose.BOW_AND_ARROW;
			} else {
				ItemStack main = entityLiving.getMainHandItem();
				boolean mainHasBowAnim = !main.isEmpty() && main.getItem() instanceof GenericGun g && g.hasBowAnim();
				if (!mainHasBowAnim && ShooterValues.getIsCurrentlyUsingGun(entityLiving, true)) {
					return HumanoidModel.ArmPose.BOW_AND_ARROW;
				}
			}
		}
		return null;
	}
}
