package techguns.items.additionalslots;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.TGItems;
import techguns.api.tginventory.TGSlotType;
import techguns.util.InventoryUtil;

public abstract class ItemTGSpecialSlotAmmo extends ItemTGSpecialSlot {

	/**
	 * Items are kept and the stacks created lazily: an ItemStack of an item can not be created before the item is registered
	 */
	Item ammoItem;
	Item ammoEmptyItem;
	ItemStack ammo = null;
	ItemStack ammoEmpty = null;

	public ItemTGSpecialSlotAmmo(String unlocalizedName, TGSlotType slot, int camoCount, int dur, Item ammo, Item ammoEmpty) {
		super(unlocalizedName, slot, camoCount, dur);
		this.ammoItem = ammo;
		this.ammoEmptyItem = ammoEmpty;
	}

	public ItemStack getAmmo() {
		if (ammo == null) {
			ammo = TGItems.newStack(ammoItem, 1);
		}
		return ammo;
	}

	public ItemStack getAmmoEmpty() {
		if (ammoEmpty == null) {
			ammoEmpty = TGItems.newStack(ammoEmptyItem, 1);
		}
		return ammoEmpty;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(Component.translatable("techguns.tooltip.reloads_with").append(": ").append(this.getAmmo().getHoverName()).withStyle(ChatFormatting.AQUA));
	}

	/**
	 * Server only in the port (1.12 also ran it on the client, the inventory changes are synced from the server)
	 */
	protected void tryReloadAndRepair(ItemStack item, Player ply) {
		if (ply.level().isClientSide) {
			return;
		}
		if (InventoryUtil.consumeAmmoPlayer(ply, getAmmo())) {
			item.setDamageValue(0);
			if (InventoryUtil.addAmmoToPlayerInventory(ply, getAmmoEmpty()) > 0) {
				ply.level().addFreshEntity(new ItemEntity(ply.level(), ply.getX(), ply.getY(), ply.getZ(), TGItems.newStack(getAmmoEmpty(), 1)));
			}
		}
	}
}
