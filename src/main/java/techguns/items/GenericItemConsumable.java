package techguns.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Item that is "eaten" (RadAway, Rad pills, ...), port of the 1.12 GenericItemConsumable
 */
public class GenericItemConsumable extends GenericItem {

	/** Number of ticks to run while using until result. */
	public final int itemUseDuration;
	protected boolean ignoreHunger = true;

	public GenericItemConsumable(String name, int useDuration) {
		super(name);
		this.itemUseDuration = useDuration;
	}

	public GenericItemConsumable(String name, int useDuaration, boolean addToItemList) {
		super(name, addToItemList);
		this.itemUseDuration = useDuaration;
	}

	public GenericItemConsumable(String name, int useDuaration, boolean addToItemList, Properties props) {
		super(name, addToItemList, props);
		this.itemUseDuration = useDuaration;
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		if (entityLiving instanceof Player entityplayer) {
			worldIn.playSound((Player) null, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(), this.getConsumedSound(), SoundSource.PLAYERS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
			this.onConsumed(stack, worldIn, entityplayer);
			entityplayer.awardStat(Stats.ITEM_USED.get(this));

			if (entityplayer instanceof ServerPlayer serverPlayer) {
				CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
			}
		}

		stack.shrink(1);
		return stack;
	}

	protected SoundEvent getConsumedSound() {
		return SoundEvents.PLAYER_BURP;
	}

	protected void onConsumed(ItemStack stack, Level worldIn, Player player) {

	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getUseDuration(ItemStack stack) {
		return this.itemUseDuration;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.EAT;
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);

		if (playerIn.canEat(this.ignoreHunger)) {
			playerIn.startUsingItem(handIn);
			return InteractionResultHolder.success(itemstack);
		} else {
			return InteractionResultHolder.fail(itemstack);
		}
	}

}
