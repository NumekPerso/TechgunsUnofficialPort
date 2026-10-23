package techguns.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.Techguns;
import techguns.api.render.IItemTGRenderer;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;
import techguns.util.InventoryUtil;

/**
 * Replaces the ammo variants of the 1.12 GenericItemShared (metadata based shared item), one item per ammo type.
 * All of them used the AMMOSLOT slot type (they fit into the ammo slots of the Techguns player inventory).
 */
public class TGAmmoItem extends GenericItem implements IItemTGRenderer, ITGSpecialSlot {

	protected final boolean useRenderHack;

	public TGAmmoItem(String name, boolean useRenderHack, int maxStackSize) {
		super(name, true, new Properties().stacksTo(maxStackSize));
		this.useRenderHack = useRenderHack;
	}

	public TGAmmoItem(String name, boolean useRenderHack) {
		this(name, useRenderHack, 64);
	}

	public TGAmmoItem(String name) {
		this(name, false);
	}

	@Override
	public boolean shouldUseRenderHack(ItemStack stack) {
		return this.useRenderHack;
	}

	@Override
	public TGSlotType getSlot(ItemStack item) {
		return TGSlotType.AMMOSLOT;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(Component.translatable(Techguns.MODID + ".tooltip.slot").append(": ").append(Component.literal(this.getSlot(stack).toString())).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(Techguns.MODID + ".tooltip.rightclickammoslot").withStyle(ChatFormatting.GRAY));
	}

	/**
	 * 1.12 GenericItemShared.onItemRightClick: [Right Click] moves the stack to the ammo slots of the Techguns inventory
	 */
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown()) {
			ItemStack newStack = stack.copy();
			int amount = InventoryUtil.addAmmoToAmmoInventory(player, newStack);
			stack.setCount(amount);
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
		}
		return super.use(level, player, hand);
	}
}
