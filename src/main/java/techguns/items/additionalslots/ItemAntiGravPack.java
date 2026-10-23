package techguns.items.additionalslots;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import techguns.TGItems;
import techguns.api.tginventory.TGSlotType;
import techguns.items.armors.TGArmorBonus;

public class ItemAntiGravPack extends ItemTGSpecialSlotAmmo {

	private static final int CONSUMPTION_PER_TICK = 1;

	private static final float FLYBONUS = 0.25f;

	public ItemAntiGravPack(String unlocalizedName, int camoCount, int dur) {
		super(unlocalizedName, TGSlotType.BACKSLOT, camoCount, dur, TGItems.NUCLEAR_POWERCELL, TGItems.NUCLEAR_POWERCELL_EMPTY);
	}

	@Override
	public void onPlayerTick(ItemStack item, PlayerTickEvent event) {
		if (item.getDamageValue() + CONSUMPTION_PER_TICK >= item.getMaxDamage()) {
			this.tryReloadAndRepair(item, event.player);
		}
		if (event.player.getAbilities().flying && !event.player.getAbilities().instabuild) {
			if (item.getDamageValue() < item.getMaxDamage()) {
				item.setDamageValue(item.getDamageValue() + CONSUMPTION_PER_TICK);
			}
		}
	}

	@Override
	public float getBonus(TGArmorBonus type, ItemStack stack, boolean consume, Player player) {
		if (type == TGArmorBonus.CREATIVE_FLIGHT) {
			if (stack.getDamageValue() < stack.getMaxDamage()) {
				return 1;
			}
		} else if (type == TGArmorBonus.FLYSPEED) {
			if (stack.getDamageValue() < stack.getMaxDamage()) {
				return FLYBONUS;
			}
		}
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		//1.12 did not call super here
		tooltip.add(Component.translatable("techguns.armorTooltip.creativeFlight"));
		tooltip.add(Component.translatable("techguns.armorTooltip.flyspeed").append(": +" + FLYBONUS * 100.0f + "%"));
	}
}
