package techguns.items.additionalslots;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import techguns.TGItems;
import techguns.api.tginventory.TGSlotType;
import techguns.capabilities.TGExtendedPlayer;
import techguns.items.armors.TGArmorBonus;

public class ItemTacticalMask extends ItemTGSpecialSlotAmmo {

	public ItemTacticalMask(String unlocalizedName, int camoCount, int dur) {
		super(unlocalizedName, TGSlotType.FACESLOT, camoCount, dur, TGItems.ENERGY_CELL, TGItems.ENERGY_CELL_EMPTY);
	}

	@Override
	public void onPlayerTick(ItemStack item, PlayerTickEvent event) {
		if (item.getDamageValue() < item.getMaxDamage()) {
			if (TGExtendedPlayer.get(event.player).enableNightVision) {
				item.setDamageValue(item.getDamageValue() + 1);
			}
		} else {
			this.tryReloadAndRepair(item, event.player);
		}

		if (event.player.hasEffect(MobEffects.POISON) && item.getDamageValue() < item.getMaxDamage()) {
			event.player.removeEffect(MobEffects.POISON);
			item.setDamageValue(item.getDamageValue() + 1);
		}
		if (event.player.hasEffect(MobEffects.WITHER) && item.getDamageValue() < item.getMaxDamage()) {
			event.player.removeEffect(MobEffects.WITHER);
			item.setDamageValue(item.getDamageValue() + 1);
		}
	}

	@Override
	public float getBonus(TGArmorBonus type, ItemStack stack, boolean consume, Player player) {
		if (type == TGArmorBonus.NIGHTVISION || type == TGArmorBonus.OXYGEN_GEAR) {
			if (stack.getDamageValue() < stack.getMaxDamage()) {
				return 1.0f;
			}
		}
		return 0.0f;
	}
}
