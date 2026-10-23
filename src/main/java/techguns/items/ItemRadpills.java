package techguns.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.TGRadiationSystem;
import techguns.util.TextUtil;

public class ItemRadpills extends GenericItemConsumable {
	public ItemRadpills(String name) {
		super(name, 32);
	}

	public ItemRadpills(String name, boolean addToItemList) {
		super(name, 32, addToItemList);
	}

	@Override
	protected void onConsumed(ItemStack stack, Level worldIn, Player player) {
		if (!worldIn.isClientSide) {
			player.addEffect(new MobEffectInstance(TGRadiationSystem.radregen_effect, 500, 1, false, false));
			player.addEffect(new MobEffectInstance(TGRadiationSystem.radresistance_effect, 3600, 2, false, false));

			ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
			if (!player.getInventory().add(bottle)) {
				player.drop(bottle, false);
			}
		}
	}

	@Override
	protected SoundEvent getConsumedSound() {
		return SoundEvents.PLAYER_BREATH;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(Component.literal(ChatFormatting.BLUE + TextUtil.trans("techguns.radresistance") + " " + TextUtil.trans("potion.potency.2") + " (180s)"));
		tooltip.add(Component.literal(ChatFormatting.BLUE + TextUtil.trans("techguns.radregeneration") + " " + TextUtil.trans("potion.potency.1") + " (-50)"));
	}
}
