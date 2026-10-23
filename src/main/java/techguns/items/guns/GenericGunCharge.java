package techguns.items.guns;

import java.util.Arrays;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns.TGItems;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.api.guns.GunHandType;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.ShooterValues;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GenericProjectile;
import techguns.packets.PacketSpawnParticleOnEntity;
import techguns.util.EntityCondition;
import techguns.util.InventoryUtil;
import techguns.util.SoundUtil;
import techguns.util.TGSoundCategory;

/**
 * Gun that is charged by holding the use key, the charged shot is fired when the key is released (releaseUsing).
 */
public class GenericGunCharge extends GenericGun {

	/**
	 * In ticks
	 */
	public float fullChargeTime = 20.0f;
	public int ammoConsumedOnFullCharge = 10;

	protected ChargedProjectileSelector<?> chargedProjectile_selector;

	public boolean hasChargedFireAnim = true;
	public boolean canFireWhileCharging = false;

	SoundEvent startChargeSound = null;
	String chargeFX = null;
	private float chargeFXoffsetX = 0.0f;
	private float chargeFXoffsetY = 0.0f;
	private float chargeFXoffsetZ = 0.0f;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GenericGunCharge(String name, ChargedProjectileSelector projectile_selector, boolean semiAuto, int minFiretime, int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound,
			int TTL, float accuracy, float fullChargeTime, int ammoConsumedOnFullCharge) {
		super(name, projectile_selector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound, reloadsound, TTL, accuracy);
		this.fullChargeTime = fullChargeTime;
		this.ammoConsumedOnFullCharge = ammoConsumedOnFullCharge;
		this.chargedProjectile_selector = projectile_selector;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {

		TGExtendedPlayer extendedPlayer = TGExtendedPlayer.get(player);
		ItemStack item = player.getItemInHand(handIn);
		Level world = player.level();

		if (extendedPlayer == null) {
			return InteractionResultHolder.pass(item);
		}

		/*
		 * Check if player wants to zoom
		 */
		if (canZoom && player.isShiftKeyDown() && this.toggleZoom) {
			if (world.isClientSide) {
				if (Techguns.proxy.getPlayerZoom() != 1.0f) {
					Techguns.proxy.setPlayerZoom(1.0f);
				} else {
					Techguns.proxy.setPlayerZoom(this.zoomMult);
				}
			}

		} else {
			int ammo = this.getCurrentAmmo(item);

			if (ammo > 0) {
				// bullets left
				int firedelay = extendedPlayer.getFireDelay(handIn);

				if (firedelay <= 0) {

					extendedPlayer.setFireDelay(handIn, this.minFiretime);

					player.startUsingItem(handIn);

					this.startCharge(item, world, player);
					extendedPlayer.setChargingWeapon(true);

					if (this.startChargeSound != null) {
						SoundUtil.playSoundOnEntityGunPosition(world, player, this.startChargeSound, SOUND_DISTANCE, 1.0F, false, true, false, TGSoundCategory.GUN_FIRE, EntityCondition.CHARGING_WEAPON);
					}

					if (this.chargeFX != null && !world.isClientSide) {
						float x;
						if (player.getMainArm() == HumanoidArm.RIGHT) x = this.chargeFXoffsetX;
						else x = -this.chargeFXoffsetX;
						TGPackets.network.sendToAllAround(new PacketSpawnParticleOnEntity(this.chargeFX, player, x, this.chargeFXoffsetY, this.chargeFXoffsetZ, true, EntityCondition.CHARGING_WEAPON), TGPackets.targetPointAroundEnt(player, 25.0f));
					}
					return InteractionResultHolder.consume(item);
				}

			} else {
				// mag empty, reload needed, creative players reload for free
				boolean creative = isCreative(player);
				if (creative || InventoryUtil.consumeAmmoPlayer(player, this.ammoType.getAmmo(this.getCurrentAmmoVariant(item)))) {

					if (!creative) {
						Arrays.stream(this.ammoType.getEmptyMag()).forEach(e -> {
							if (!e.isEmpty()) {
								int amount = InventoryUtil.addAmmoToPlayerInventory(player, TGItems.newStack(e, 1));
								if (amount > 0 && !world.isClientSide) {
									player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), TGItems.newStack(e, amount)));
								}
							}
						});
					}

					// stop toggle zooming when reloading
					if (world.isClientSide) {
						if (canZoom && this.toggleZoom) {
							if (Techguns.proxy.getPlayerZoom() != 1.0f) {
								Techguns.proxy.setPlayerZoom(1.0f);
							}
						}
					}

					extendedPlayer.setFireDelay(handIn, this.reloadtime - this.minFiretime);

					if (ammoCount > 1) {
						int i = 1;
						while (i < ammoCount && (creative || InventoryUtil.consumeAmmoPlayer(player, this.ammoType.getAmmo(this.getCurrentAmmoVariant(item))))) {
							i++;
						}
						this.reloadAmmo(item, i);
					} else {
						this.reloadAmmo(item);
					}

					SoundUtil.playSoundOnEntityGunPosition(world, player, reloadsound, 1.0f, 1.0f, false, true, TGSoundCategory.RELOAD);

					if (world.isClientSide) {
						int time = (int) (((float) reloadtime / 20.0f) * 1000);
						ShooterValues.setReloadtime(player, handIn == InteractionHand.OFF_HAND, System.currentTimeMillis() + time, time, (byte) 1);
						client_startReload();
					}
				}
			}
		}

		return InteractionResultHolder.pass(item);
	}

	/**
	 * Override for onCharge code.
	 */
	protected void startCharge(ItemStack item, Level world, Player player) {
	}

	protected static boolean isChargingWith(LivingEntity player, ItemStack stack, InteractionHand hand) {
		return player.isUsingItem() && player.getUsedItemHand() == hand && player.getUseItem().getItem() == stack.getItem();
	}

	@Override
	public void shootGunPrimary(ItemStack stack, Level world, Player player, boolean zooming, InteractionHand hand, Entity target) {
		if (this.canFireWhileCharging || !isChargingWith(player, stack, hand)) {
			super.shootGunPrimary(stack, world, player, zooming, hand, target);
		}
	}

	@Override
	public void releaseUsing(ItemStack item, Level world, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof Player player) {

			TGExtendedPlayer txp = TGExtendedPlayer.get(player);
			if (txp != null) {
				txp.setChargingWeapon(false);
			}

			int j = this.getUseDuration(item) - timeLeft;

			float f = j / this.fullChargeTime;

			if (f > 1.0F) {
				f = 1.0F;
			}

			int ammoConsumed = this.consumeAmmoCharge(item, f, isCreative(player));

			//reduce charge value if ammo is low
			if (ammoConsumed < (int) Math.ceil(f * this.ammoConsumedOnFullCharge)) {
				f = (float) ammoConsumed / (float) this.ammoConsumedOnFullCharge;
			}

			if (!world.isClientSide) {

				// If SERVER, create projectile
				EnumBulletFirePos firePos;
				if (player.getMainArm() == HumanoidArm.RIGHT) {
					firePos = EnumBulletFirePos.RIGHT;
				} else {
					firePos = EnumBulletFirePos.LEFT;
				}

				//Charged shot has to be from main hand!
				spawnChargedProjectile(world, player, item, accuracy, f, ammoConsumed, firePos);
				if (shotgun) {
					for (int i = 0; i < bulletcount; i++) {
						spawnChargedProjectile(world, player, item, spread, f, ammoConsumed, firePos);
					}
				}

				if (this.hasChargedFireAnim) {
					this.playChargedFiresound(item, world, player, f);
				}

			} else {

				if (this.hasChargedFireAnim) {
					// If CLIENT, do Effects
					int recoiltime_l = getRecoilTime(f);
					int muzzleFlashtime_l = getMuzzleFlashTime(f);

					ShooterValues.setRecoiltime(player, player.getUsedItemHand() == InteractionHand.OFF_HAND, System.currentTimeMillis() + recoiltime_l, recoiltime_l, (byte) 1, f);
					ShooterValues.setMuzzleFlashTime(player, player.getUsedItemHand() == InteractionHand.OFF_HAND, System.currentTimeMillis() + muzzleFlashtime_l, muzzleFlashtime_l);
				}
				client_weaponFired();
			}
		}
	}

	protected void playChargedFiresound(ItemStack item, Level world, Player player, float chargeProgress) {
		SoundUtil.playSoundOnEntityGunPosition(world, player, firesound, SOUND_DISTANCE, 1.0f, false, false, true, TGSoundCategory.GUN_FIRE);

		if (!(rechamberSound == null)) {
			SoundUtil.playSoundOnEntityGunPosition(world, player, rechamberSound, 1.0f, 1.0f, false, false, true, TGSoundCategory.GUN_FIRE);
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 288000;
	}

	@Override
	public float getFullChargeTime() {
		return this.fullChargeTime;
	}

	public int getRecoilTime(float charge) {
		return ((int) (((float) recoiltime / 20.0f) * 1000.0f));
	}

	public int getMuzzleFlashTime(float charge) {
		return ((int) (((float) muzzleFlashtime / 20.0f) * 1000.0f));
	}

	/**
	 * consume ammo from NBTTag
	 *
	 * @param f charge amount
	 * @return ammount of ammo actually consumed
	 */
	public int consumeAmmoCharge(ItemStack item, float f, boolean creative) {

		int amount = (int) Math.ceil(f * this.ammoConsumedOnFullCharge);

		if (!creative) {
			amount = this.useAmmo(item, amount);
		}
		return amount;
	}

	public void spawnChargedProjectile(final Level world, final LivingEntity player, ItemStack itemStack, float spread, float charge, int ammoConsumed, EnumBulletFirePos firePos) {
		IChargedProjectileFactory<?> fact = this.chargedProjectile_selector.getFactoryForType(this.getCurrentAmmoVariantKey(itemStack));
		GenericProjectile proj = fact.createChargedProjectile(world, player, damage, speed, this.getScaledTTL(), spread, this.damageDropStart, damageDropEnd, this.damageMin, penetration, getDoBlockDamage(player), firePos, radius, gravity, charge, ammoConsumed);
		if (proj != null) world.addFreshEntity(proj);
	}

	@Override
	public boolean canCharge() {
		return true;
	}

	public GenericGunCharge setChargeFireAnims(boolean hasAnims) {
		this.hasChargedFireAnim = hasAnims;
		return this;
	}

	public GenericGunCharge setFireWhileCharging(boolean canFire) {
		this.canFireWhileCharging = canFire;
		return this;
	}

	@Override
	public boolean hasRightClickAction() {
		return this.getGunHandType() == GunHandType.TWO_HANDED;
	}

	public GenericGunCharge setChargeSound(SoundEvent startChargeSound) {
		this.startChargeSound = startChargeSound;
		return this;
	}

	public GenericGunCharge setChargeFX(String fx, float offsetX, float offsetY, float offsetZ) {
		this.chargeFX = fx;
		this.chargeFXoffsetX = offsetX;
		this.chargeFXoffsetY = offsetY;
		this.chargeFXoffsetZ = offsetZ;
		return this;
	}
}
