package techguns.items.guns;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGPackets;
import techguns.TGItems;
import techguns.TGSounds;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.packets.PacketSpawnParticle;
import techguns.util.TextUtil;

public class MiningDrill extends GenericGunMeleeCharge {

	@SuppressWarnings("rawtypes")
	public MiningDrill(String name, ChargedProjectileSelector projectile_selector, boolean semiAuto, int minFiretime,
			int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound, int TTL,
			float accuracy, float fullChargeTime, int ammoConsumedOnFullCharge) {
		super(name, projectile_selector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound, reloadsound, TTL,
				accuracy, fullChargeTime, ammoConsumedOnFullCharge);
		this.setMiningHeads(TGItems.MININGDRILLHEAD_CARBON);
	}

	@Override
	protected TGDamageSource getMeleeDamageSource(Player player, ItemStack stack) {
		TGDamageSource src = new TGDamageSource("player", player, player, DamageType.PHYSICAL, DeathType.GORE);
		if (this.getCurrentAmmo(stack) > 0) {
			src.goreChance = 0.5f;
			src.armorPenetration = this.penetration;
			src.knockbackMultiplier = 1f;
		} else {
			src.deathType = DeathType.DEFAULT;
		}
		return src;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
		ItemStack stack = player.getItemInHand(handIn);
		this.shootGunPrimary(stack, worldIn, player, false, handIn, null);
		return InteractionResultHolder.fail(stack);
	}

	@Override
	public float getExtraDigSpeed(ItemStack stack) {
		int headlevel = this.getMiningHeadLevel(stack);
		return 2.0f * headlevel;
	}

	@Override
	protected void addMiningTooltip(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flagIn, boolean longTooltip) {
		super.addMiningTooltip(stack, world, list, flagIn, longTooltip);
		if (longTooltip) {
			addLine(list, TextUtil.trans("techguns.tooltip.sneaksilktouch"));
		}
	}

	@Override
	public int getExtraMiningRadius(ItemStack stack) {
		return 0;
	}

	@Override
	protected SoundEvent getSwingSound() {
		return TGSounds.DRILLER_SWING;
	}

	@Override
	protected SoundEvent getBlockBreakSound() {
		return TGSounds.DRILLER_BREAK;
	}

	@Override
	protected void spawnSweepParticle(Level w, double x, double y, double z, double motionX, double motionY, double motionZ) {
		TGPackets.network.sendToAllAround(new PacketSpawnParticle("PowerhammerImpact", x, y, z), TGPackets.targetPoint(w, x, y, z, 32.0f));
	}

	@Override
	protected void playSweepSoundEffect(Player player) {
		player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), TGSounds.DRILLER_HIT, player.getSoundSource(), 1.0F, 1.0F);
	}

	/**
	 * Sneaking with a powered drill harvests like silk touch without dropping XP (1.12 did this in the HarvestDropsEvent / BreakEvent,
	 * 1.20 has no drops event, the block is broken here with a silk touch copy of the drill).
	 */
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
		Level w = player.level();
		if (!w.isClientSide && player.isShiftKeyDown() && !player.getAbilities().instabuild && this.getAmmoLeft(itemstack) > 0) {
			BlockState state = w.getBlockState(pos);
			if (!state.isAir() && state.canHarvestBlock(w, pos, player)) {
				ItemStack silkTool = itemstack.copy();
				silkTool.enchant(Enchantments.SILK_TOUCH, 1);

				BlockEntity tile = w.getBlockEntity(pos);
				itemstack.mineBlock(w, state, pos, player);

				Block block = state.getBlock();
				block.playerWillDestroy(w, pos, state, player);
				if (state.onDestroyedByPlayer(w, pos, player, true, w.getFluidState(pos))) {
					block.destroy(w, pos, state);
					block.playerDestroy(w, player, pos, state, tile, silkTool);
				}
				return true;
			}
		}
		return super.onBlockStartBreak(itemstack, pos, player);
	}
}
