package techguns.items.guns;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns.TGItems;
import techguns.TGSounds;

public class Chainsaw extends GenericGunMeleeCharge {

	@SuppressWarnings("rawtypes")
	public Chainsaw(String name, ChargedProjectileSelector projectile_selector, boolean semiAuto, int minFiretime,
			int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound, int TTL,
			float accuracy, float fullChargeTime, int ammoConsumedOnFullCharge) {
		super(name, projectile_selector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound, reloadsound, TTL,
				accuracy, fullChargeTime, ammoConsumedOnFullCharge);
		this.setMiningHeads(TGItems.CHAINSAWBLADES_OBSIDIAN, TGItems.CHAINSAWBLADES_CARBON);
	}

	@Override
	public float getExtraDigSpeed(ItemStack stack) {
		int headlevel = this.getMiningHeadLevel(stack);
		return 3.0f * headlevel;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
		ItemStack stack = player.getItemInHand(handIn);
		this.shootGunPrimary(stack, worldIn, player, false, handIn, null);
		return InteractionResultHolder.fail(stack);
	}

	@Override
	protected SoundEvent getSwingSound() {
		return TGSounds.CHAINSAW_LOOP;
	}

	@Override
	protected SoundEvent getBlockBreakSound() {
		return TGSounds.CHAINSAW_HIT;
	}

	@Override
	protected void playSweepSoundEffect(Player player) {
		player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), TGSounds.CHAINSAW_HIT, player.getSoundSource(), melee_sound_volume, 1.0F);
	}
}
