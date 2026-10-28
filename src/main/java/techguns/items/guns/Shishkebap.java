package techguns.items.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.packets.PacketNotifyAmbientEffectChange;
import techguns.packets.PacketSpawnParticle;

public class Shishkebap extends GenericGunMeleeCharge {

	public static final TagKey<Block> MINEABLE = BlockTags.create(new ResourceLocation(Techguns.MODID, "mineable/shishkebap"));

	@SuppressWarnings("rawtypes")
	public Shishkebap(String name, ChargedProjectileSelector projectile_selector, boolean semiAuto, int minFiretime,
			int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound, int TTL,
			float accuracy, float fullChargeTime, int ammoConsumedOnFullCharge) {
		super(name, projectile_selector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound, reloadsound, TTL,
				accuracy, fullChargeTime, ammoConsumedOnFullCharge);
		this.setHasCustomAnim(false);
		this.setNoBowAnim();
	}

	@Override
	public DamageType getDamageType(ItemStack stack) {
		return DamageType.FIRE;
	}

	@Override
	protected TGDamageSource getMeleeDamageSource(Player player, ItemStack stack) {
		TGDamageSource src = new TGDamageSource("player", player, player, DamageType.FIRE, DeathType.GORE);
		if (this.getCurrentAmmo(stack) > 0) {
			src.goreChance = 0.25f;
			src.armorPenetration = this.penetration;
			src.knockbackMultiplier = 1f;
		} else {
			src.deathType = DeathType.DEFAULT;
		}
		return src;
	}

	@Override
	protected void spawnSweepParticle(Level w, double x, double y, double z, double motionX, double motionY, double motionZ) {
		TGPackets.network.sendToAllAround(new PacketSpawnParticle("PowerhammerImpact", x, y, z), TGPackets.targetPoint(w, x, y, z, 32.0f));
	}

	@Override
	protected void consumeAmmoOnMeleeHit(LivingEntity elb, ItemStack stack) {
		boolean empty = this.getAmmoLeft(stack) <= 0;
		super.consumeAmmoOnMeleeHit(elb, stack);
		if (!elb.level().isClientSide && !empty && this.getAmmoLeft(stack) <= 0) {
			TGPackets.network.sendToDimension(new PacketNotifyAmbientEffectChange(elb, InteractionHand.MAIN_HAND), elb.level().dimension());
		}
	}

	@Override
	protected void onMeleeHitTarget(ItemStack stack, Entity target) {
		if (this.getAmmoLeft(stack) > 0) {
			target.setSecondsOnFire(3);
		}
	}

	@Override
	protected SoundEvent getSwingSound() {
		return TGSounds.SHISHKEBAP_SWING;
	}

	@Override
	protected SoundEvent getBlockBreakSound() {
		return null;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
		return InteractionResultHolder.pass(player.getItemInHand(handIn));
	}

	/**
	 * 1.12 bricks had no harvest tool, every tool was effective on them and the Shishkebap cut through them. In 1.20 they
	 * are mineable with pickaxe only: the blocks of the tag stay effective and harvestable (with fuel).
	 */
	@Override
	public boolean isEffectiveToolForState(ItemStack stack, BlockState state) {
		if (state.is(MINEABLE) && this.getCurrentAmmo(stack) >= this.getMiningAmmoConsumption()) {
			return true;
		}
		return super.isEffectiveToolForState(stack, state);
	}

	@Override
	public boolean canHarvestWithTool(ItemStack stack, BlockState state) {
		if (state.is(MINEABLE) && this.getCurrentAmmo(stack) >= this.getMiningAmmoConsumption()) {
			return true;
		}
		return super.canHarvestWithTool(stack, state);
	}

	@Override
	public boolean canCharge() {
		return false;
	}

	@Override
	public void releaseUsing(ItemStack item, Level world, LivingEntity entityLiving, int timeLeft) {
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 0;
	}
}
