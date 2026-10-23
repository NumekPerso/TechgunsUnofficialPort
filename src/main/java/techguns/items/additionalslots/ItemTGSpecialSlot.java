package techguns.items.additionalslots;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.Techguns;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;
import techguns.capabilities.TGExtendedPlayer;
import techguns.gui.player.TGPlayerInventory;
import techguns.items.GenericItem;
import techguns.items.armors.ICamoChangeable;

public abstract class ItemTGSpecialSlot extends GenericItem implements ITGSpecialSlot, ICamoChangeable {
	protected byte camoCount;

	protected TGSlotType slot;

	public ItemTGSpecialSlot(String unlocalizedName, TGSlotType slot, int camoCount, int dur) {
		super(unlocalizedName, true, dur > 0 ? new Properties().durability(dur) : new Properties().stacksTo(1));
		this.camoCount = (byte) camoCount;
		this.slot = slot;
	}

	@Override
	public int getCamoCount() {
		return this.camoCount;
	}

	@Override
	public Component getCurrentCamoName(ItemStack item) {
		CompoundTag tags = item.getTag();
		byte camoID = 0;
		if (tags != null && tags.contains("camo")) {
			camoID = tags.getByte("camo");
		}
		if (camoID > 0) {
			return Component.translatable(this.getDescriptionId() + ".camoname." + camoID);
		} else {
			return Component.translatable("techguns.item.defaultcamo");
		}
	}

	@Override
	public TGSlotType getSlot(ItemStack item) {
		return slot;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(Component.translatable(Techguns.MODID + ".tooltip.slot").append(": ").append(Component.literal(this.getSlot(stack).toString())).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		TGExtendedPlayer props = TGExtendedPlayer.get(playerIn);
		if (props != null) {
			int slotIndex = -1;
			if (this.getSlot(stack) == TGSlotType.BACKSLOT) {
				slotIndex = TGPlayerInventory.SLOT_BACK;
			} else if (this.getSlot(stack) == TGSlotType.FACESLOT) {
				slotIndex = TGPlayerInventory.SLOT_FACE;
			} else if (this.getSlot(stack) == TGSlotType.HANDSLOT) {
				slotIndex = TGPlayerInventory.SLOT_HAND;
			}
			if (slotIndex >= 0 && props.tg_inventory.inventory.get(slotIndex).isEmpty()) {
				props.tg_inventory.inventory.set(slotIndex, stack.copy());
				stack.setCount(0);
				return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
			}
		}
		return super.use(level, playerIn, handIn);
	}

}
