package techguns.items.additionalslots;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import techguns.TGItems;
import techguns.api.tginventory.TGSlotType;
import techguns.items.armors.GenericArmor;
import techguns.items.armors.TGArmorBonus;

public class ItemScubaTanks extends ItemTGSpecialSlotAmmo {

	public ItemScubaTanks(String unlocalizedName, int camoCount, int dur) {
		super(unlocalizedName, TGSlotType.BACKSLOT, camoCount, dur, TGItems.COMPRESSED_AIR_TANK, TGItems.COMPRESSED_AIR_TANK_EMPTY);
	}

	@Override
	public void onPlayerTick(ItemStack item, PlayerTickEvent event) {

		if (item.getDamageValue() >= item.getMaxDamage()) {
			tryReloadAndRepair(item, event.player);
		}

		if (item.getDamageValue() < item.getMaxDamage()) {
			if (event.player.getAirSupply() <= 280) {
				if (GenericArmor.getArmorBonusForPlayer(event.player, TGArmorBonus.OXYGEN_GEAR, false) > 0) {
					event.player.setAirSupply(event.player.getAirSupply() + 20);
					item.setDamageValue(item.getDamageValue() + 1);
				}
			}
		}
	}

}
