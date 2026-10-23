package techguns.items.guns;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns.TGPackets;
import techguns.TGItems;
import techguns.TGSounds;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GenericProjectile;
import techguns.packets.PacketSpawnParticle;

public class PowerHammer extends GenericGunMeleeCharge {

	@SuppressWarnings("rawtypes")
	public PowerHammer(String name, ChargedProjectileSelector projectile_selector, boolean semiAuto, int minFiretime,
			int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound, int TTL,
			float accuracy, float fullChargeTime, int ammoConsumedOnFullCharge) {
		super(name, projectile_selector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound, reloadsound, TTL,
				accuracy, fullChargeTime, ammoConsumedOnFullCharge);
		this.setMiningHeads(TGItems.POWERHAMMERHEAD_OBSIDIAN, TGItems.POWERHAMMERHEAD_CARBON);
	}

	@Override
	protected TGDamageSource getMeleeDamageSource(Player player, ItemStack stack) {
		TGDamageSource src = new TGDamageSource("player", player, player, DamageType.PHYSICAL, DeathType.GORE);
		if (this.getCurrentAmmo(stack) > 0) {
			src.goreChance = 1.0f;
			src.armorPenetration = this.penetration;
			src.knockbackMultiplier = 1.5f;
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
	public void spawnChargedProjectile(final Level world, final LivingEntity player, ItemStack itemStack, float spread, float charge, int ammoConsumed, EnumBulletFirePos firePos) {
		int level = this.getMiningHeadLevel(itemStack);
		float extraDmg = 0.5f * level;

		IChargedProjectileFactory<?> fact = this.chargedProjectile_selector.getFactoryForType(this.getCurrentAmmoVariantKey(itemStack));
		GenericProjectile proj = fact.createChargedProjectile(world, player, damage + extraDmg, speed, this.ticksToLive, spread, this.damageDropStart, damageDropEnd, this.damageMin + extraDmg, penetration, getDoBlockDamage(player), firePos, radius, gravity, charge, ammoConsumed);
		if (proj != null) world.addFreshEntity(proj);
	}

	@Override
	public float getExtraDigSpeed(ItemStack stack) {
		int headlevel = this.getMiningHeadLevel(stack);
		return 2.0f * headlevel;
	}

	@Override
	protected void playSweepSoundEffect(Player player) {
		player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), TGSounds.POWERHAMMER_IMPACT, player.getSoundSource(), 1.0F, 1.0F);
	}
}
