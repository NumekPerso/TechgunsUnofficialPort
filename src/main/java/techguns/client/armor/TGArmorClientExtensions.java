package techguns.client.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import techguns.TGArmors;
import techguns.client.render.TGItemRenderer;
import techguns.items.armors.GenericArmor;

/**
 * Client item extensions of the Techguns armors: custom 3D armor model (1.12 GenericArmor.getArmorModel) and the 3D inventory
 * renderer of the power armors (items with a builtin/entity model, see TGArmorClientSetup).
 */
public class TGArmorClientExtensions implements IClientItemExtensions {

	public static final TGArmorClientExtensions INSTANCE = new TGArmorClientExtensions();

	@Override
	public BlockEntityWithoutLevelRenderer getCustomRenderer() {
		return TGItemRenderer.getInstance();
	}

	@Override
	public Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
		if (!itemStack.isEmpty() && itemStack.getItem() instanceof GenericArmor armor && armor.getArmorModelKey() != null) {
			TGArmorModel model = TGArmorModels.getArmorModel(armor.getArmorModelKey());
			if (model != null) {
				boolean showHeadwear = itemStack.getItem() != TGArmors.t2_beret;
				return model.setup(original, equipmentSlot, showHeadwear);
			}
		}
		return original;
	}
}
