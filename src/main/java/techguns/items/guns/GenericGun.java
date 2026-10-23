package techguns.items.guns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import techguns.TGItems;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.api.guns.GunHandType;
import techguns.api.guns.IChargeableItem;
import techguns.api.guns.IGenericGun;
import techguns.api.render.IItemTGRenderer;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.ShooterValues;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GenericProjectile;
import techguns.items.GenericItem;
import techguns.items.armors.GenericArmor;
import techguns.items.armors.ICamoChangeable;
import techguns.items.armors.TGArmorBonus;
import techguns.items.guns.ammo.AmmoType;
import techguns.items.guns.ammo.AmmoTypes;
import techguns.items.guns.ammo.DamageModifier;
import techguns.packets.GunFiredMessage;
import techguns.packets.ReloadStartedMessage;
import techguns.util.InventoryUtil;
import techguns.util.MathUtil;
import techguns.util.SoundUtil;
import techguns.util.TGSoundCategory;
import techguns.util.TextUtil;

public class GenericGun extends GenericItem implements IGenericGun, IItemTGRenderer, ICamoChangeable, IChargeableItem {
	public static final float SOUND_DISTANCE = 4.0f;

	// Weapon Stats
	boolean semiAuto = false;
	int minFiretime = 4;
	int clipsize = 10;
	int reloadtime = 40; //ticks
	float damage = 2.0f;
	SoundEvent firesound = TGSounds.M4_FIRE;
	SoundEvent reloadsound = TGSounds.M4_RELOAD;
	SoundEvent firesoundStart = TGSounds.M4_FIRE;
	SoundEvent rechamberSound = null;
	int ammoCount; // ammo per reload
	float zoomMult = 1.0f;
	boolean canZoom = false;
	boolean toggleZoom = false;
	boolean fireCenteredZoomed = false;
	int ticksToLive = 40;
	float speed = 2.0f;

	float damageMin = 1.0f;
	float damageDropStart = 20f;
	float damageDropEnd = 40f;
	float penetration = 0.0f;

	AmmoType ammoType = AmmoTypes.PISTOL_ROUNDS;

	boolean shotgun = false;
	boolean burst = false;
	float spread = 0.015f;
	int bulletcount = 7;
	float accuracy = 0.0f;

	float projectileForwardOffset = 0.0f;

	int maxLoopDelay = 0;
	int recoiltime = 5; // ticks;

	int muzzleFlashtime = 5;
	boolean silenced = false;

	boolean checkRecoil = false;
	boolean checkMuzzleFlash = false;

	float AI_attackRange = 15.0F; //sqrt of actual distance
	int AI_attackTime = 60;
	int AI_burstCount = 0;
	int AI_burstAttackTime = 0;

	int camoCount = 1;

	int lockOnTicks = 0; //MaximumLockOnTime
	int lockOnPersistTicks = 0;

	EnumCrosshairStyle crossHairStyle = EnumCrosshairStyle.GUN_DYNAMIC;

	public ArrayList<ResourceLocation> textures;

	protected ProjectileSelector projectile_selector;

	GunHandType handType = GunHandType.TWO_HANDED;

	int miningAmmoConsumption = 1;

	float meleeDamagePwr = 6.0f;
	float meleeDamageEmpty = 2.0f;
	float digSpeed = 1.0f;

	/**
	 * spread multiplier while shooting zoomed
	 */
	float zoombonus = 1.0f;

	float radius = 1.0f;
	double gravity = 0.0f;

	boolean shootWithLeftClick = true;

	public float turretPosOffsetX = 0.0f;
	public float turretPosOffsetY = 0.0f;
	public float turretPosOffsetZ = 0.0f;

	public static ArrayList<GenericGun> guns = new ArrayList<>();

	/**
	 * Lightpulse parameters
	 */
	public int light_lifetime = 2;
	public float light_radius_start = 3.0f;
	public float light_radius_end = 3.0f;
	public float light_r = 1f;
	public float light_g = 0.9f;
	public float light_b = 0.2f;
	protected boolean muzzelight = true;

	boolean hasAimedBowAnim = true;

	boolean hasAmbientEffect = false;
	/**
	 * Should bind the texture on rendering?
	 */
	public boolean hasCustomTexture = true;

	protected RangeTooltipType rangeTooltipType = RangeTooltipType.DROP;

	private GenericGun(String name) {
		super(name, true, new Properties().stacksTo(1));
	}

	public GenericGun(String name, ProjectileSelector projectileSelector, boolean semiAuto, int minFiretime, int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound, int TTL, float accuracy) {
		this(true, name, projectileSelector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound, reloadsound, TTL, accuracy);
	}

	public GenericGun setMuzzleLight(float r, float g, float b) {
		this.light_r = r;
		this.light_g = g;
		this.light_b = b;
		return this;
	}

	public GenericGun setNoMuzzleLight() {
		this.muzzelight = false;
		return this;
	}

	public GenericGun setMuzzleLight(int lifetime, float radius_start, float radius_end, float r, float g, float b) {
		this.light_lifetime = lifetime;
		this.light_radius_start = radius_start;
		this.light_radius_end = radius_end;
		this.setMuzzleLight(r, g, b);
		return this;
	}

	public GenericGun setRangeTooltipType(RangeTooltipType type) {
		this.rangeTooltipType = type;
		return this;
	}

	public GenericGun setHasAmbient() {
		this.hasAmbientEffect = true;
		return this;
	}

	public boolean hasAmbientEffect() {
		return this.hasAmbientEffect;
	}

	public GenericGun setNoCustomTexture() {
		this.hasCustomTexture = false;
		return this;
	}

	public GenericGun setNoBowAnim() {
		this.hasAimedBowAnim = false;
		return this;
	}

	public boolean hasBowAnim() {
		return this.hasAimedBowAnim;
	}

	public GenericGun(boolean addToGunList, String name, ProjectileSelector projectile_selector, boolean semiAuto, int minFiretime, int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound, int TTL, float accuracy) {
		this(name);

		this.ammoType = projectile_selector.ammoType;
		this.semiAuto = semiAuto;
		this.minFiretime = minFiretime;
		this.clipsize = clipsize;
		this.reloadtime = reloadtime;
		this.damage = damage;
		this.firesound = firesound;
		this.reloadsound = reloadsound;

		this.ammoCount = 1;
		this.ticksToLive = TTL;
		this.accuracy = accuracy;

		//Defaults to no drop
		this.damageDropStart = TTL;
		this.damageDropEnd = TTL;
		this.damageMin = damage;

		this.projectile_selector = projectile_selector;

		if (addToGunList) {
			guns.add(this);
		}
	}

	public GenericGun setGravity(double grav) {
		this.gravity = grav;
		return this;
	}

	public GenericGun setAmmoCount(int count) {
		this.ammoCount = count;
		return this;
	}

	public GenericGun setZoom(float mult, boolean toggle, float bonus, boolean fireCenteredWhileZooming) {
		this.canZoom = true;
		this.zoomMult = mult;
		this.toggleZoom = toggle;
		this.zoombonus = bonus;
		this.fireCenteredZoomed = fireCenteredWhileZooming;
		return this;
	}

	public GenericGun setShotgunSpred(int count, float spread) {
		return this.setShotgunSpread(count, spread, false);
	}

	public GenericGun setShotgunSpread(int count, float spread, boolean burst) {
		this.shotgun = true;
		this.spread = spread;
		this.bulletcount = count;
		this.burst = burst;
		return this;
	}

	public GenericGun setForwardOffset(float offset) {
		this.projectileForwardOffset = offset;
		return this;
	}

	public GenericGun setBulletSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public GenericGun setPenetration(float pen) {
		this.penetration = pen;
		return this;
	}

	public GenericGun setMuzzleFlashTime(int time) {
		this.muzzleFlashtime = time;
		return this;
	}

	public GenericGun setFiresoundStart(SoundEvent firesoundStart) {
		this.firesoundStart = firesoundStart;
		return this;
	}

	public GenericGun setMaxLoopDelay(int maxLoopDelay) {
		this.maxLoopDelay = maxLoopDelay;
		return this;
	}

	public GenericGun setRecoiltime(int recoiltime) {
		this.recoiltime = recoiltime;
		return this;
	}

	public GenericGun setRechamberSound(SoundEvent rechamberSound) {
		this.rechamberSound = rechamberSound;
		return this;
	}

	/**
	 * @param x - Offset sideways, +right -left
	 * @param y - Offset height
	 * @param z - offset forward/backward
	 */
	public GenericGun setTurretPosOffset(float x, float y, float z) {
		this.turretPosOffsetX = x;
		this.turretPosOffsetY = y;
		this.turretPosOffsetZ = z;
		return this;
	}

	protected int getScaledTTL() {
		return (int) Math.ceil(this.ticksToLive / this.speed);
	}

	/**
	 * @param ticks          Time required to lock target
	 * @param lockExtraTicks Extra time that is added when the lock is complete
	 */
	public GenericGun setLockOn(int ticks, int lockExtraTicks) {
		this.lockOnTicks = ticks;
		this.lockOnPersistTicks = lockExtraTicks;
		return this;
	}

	public int getLockOnTicks() {
		return lockOnTicks;
	}

	public int getLockOnPersistTicks() {
		return lockOnPersistTicks;
	}

	/**
	 * Called only clientside!, requires packets for actions other than zoom (clientside
	 */
	public boolean gunSecondaryAction(Player player, ItemStack stack) {
		if (player.level().isClientSide && canZoom && this.toggleZoom && !ShooterValues.getPlayerIsReloading(player, false)) {
			if (Techguns.proxy.getPlayerZoom() != 1.0f) {
				Techguns.proxy.setPlayerZoom(1.0f);
			} else {
				Techguns.proxy.setPlayerZoom(this.zoomMult);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		if (this.shootWithLeftClick) {
			return true;
		} else {
			return this.getCurrentAmmo(stack) < this.miningAmmoConsumption;
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		this.gunSecondaryAction(playerIn, playerIn.getItemInHand(handIn));
		return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}

	public ItemStack[] getReloadItem(ItemStack stack) {
		return this.ammoType.getAmmo(this.getCurrentAmmoVariant(stack));
	}

	public int getAmmoCount() {
		return ammoCount;
	}

	public int getAmmoLeftCountTooltip(ItemStack item) {
		int ammo = this.getCurrentAmmo(item);
		if (this.burst) {
			return ammo * (this.bulletcount + 1);
		} else {
			return ammo;
		}
	}

	public int getClipsizeTooltip() {
		if (this.burst) {
			return clipsize * (this.bulletcount + 1);
		} else {
			return clipsize;
		}
	}

	public int getAmmoLeftCount(int mags) {
		int count = (this.clipsize / this.ammoCount) * mags;
		if (this.burst) {
			return count * (this.bulletcount + 1);
		} else {
			return count;
		}
	}

	protected void spawnProjectile(final Level world, final LivingEntity player, final ItemStack itemstack, float spread, float offset, float damagebonus, EnumBulletFirePos firePos, Entity target) {

		IProjectileFactory<GenericProjectile> projectile = this.projectile_selector.getFactoryForType(this.getCurrentAmmoVariantKey(itemstack));

		GenericProjectile proj = projectile.createProjectile(this, world, player, damage * damagebonus, speed, this.getScaledTTL(), spread, this.damageDropStart,
				this.damageDropEnd, this.damageMin * damagebonus, this.penetration, getDoBlockDamage(player), firePos, radius, gravity);

		float f = 1.0f;
		if (this.muzzelight) {
			Vec3 look = player.getLookAngle();
			Techguns.proxy.createLightPulse(proj.getX() + look.x * f, proj.getY() + look.y * f, proj.getZ() + look.z * f, this.light_lifetime, this.light_radius_start, this.light_radius_end, this.light_r, this.light_g, this.light_b);
		}
		if (silenced) {
			proj.setSilenced();
		}
		if (offset > 0.0f) {
			proj.shiftForward(offset / speed);
		}
		world.addFreshEntity(proj);
	}

	public static boolean getDoBlockDamage(LivingEntity elb) {
		boolean blockdamage = false;
		if (elb instanceof Player) {
			TGExtendedPlayer caps = TGExtendedPlayer.get((Player) elb);
			if (caps != null) {
				blockdamage = !caps.enableSafemode;
			}
		}
		return blockdamage;
	}

	@Override
	public boolean isShootWithLeftClick() {
		return this.shootWithLeftClick;
	}

	@Override
	public boolean isSemiAuto() {
		return this.semiAuto;
	}

	public GenericGun setCheckRecoil() {
		this.checkRecoil = true;
		return this;
	}

	public GenericGun setCheckMuzzleFlash() {
		this.checkMuzzleFlash = true;
		return this;
	}

	@Override
	public boolean isZooming() {
		return Techguns.proxy.getPlayerZoom() == this.zoomMult;
	}

	protected static boolean isCreative(LivingEntity ent) {
		return ent instanceof Player && ((Player) ent).getAbilities().instabuild;
	}

	@Override
	public void shootGunPrimary(ItemStack stack, Level world, Player player, boolean zooming, InteractionHand hand, Entity target) {

		int ammo = this.getCurrentAmmo(stack);

		byte ATTACK_TYPE = 0;
		TGExtendedPlayer extendedPlayer = TGExtendedPlayer.get(player);
		if (extendedPlayer == null) {
			return;
		}
		if (ammo > 0) {
			//bullets left
			int firedelay = extendedPlayer.getFireDelay(hand);

			if (firedelay <= 0) {

				extendedPlayer.setFireDelay(hand, this.minFiretime);

				if (!isCreative(player)) {
					this.useAmmo(stack, 1);
				}

				if (!world.isClientSide) {
					/*
					 * If SERVER, create projectile
					 */
					float accuracybonus = MathUtil.clamp(1.0f - GenericArmor.getArmorBonusForPlayer(player, TGArmorBonus.GUN_ACCURACY, false), 0f, 1f);

					//add spread penalty when shooting while blocking
					if (hand == InteractionHand.MAIN_HAND && player.isBlocking()) {
						if (this.handType == GunHandType.ONE_HANDED) {
							accuracybonus *= 4.0f;
						} else {
							accuracybonus *= 8.0f;
						}
					}

					EnumBulletFirePos firePos;
					if ((hand == InteractionHand.MAIN_HAND && player.getMainArm() == HumanoidArm.RIGHT) || (hand == InteractionHand.OFF_HAND && player.getMainArm() == HumanoidArm.LEFT)) {
						firePos = EnumBulletFirePos.RIGHT;
					} else {
						firePos = EnumBulletFirePos.LEFT;
					}

					if (zooming) {
						accuracybonus *= this.zoombonus;
						if (fireCenteredZoomed) {
							firePos = EnumBulletFirePos.CENTER;
						}
					}
					this.shootGun(world, player, stack, accuracybonus, 1.0f, ATTACK_TYPE, hand, firePos, target);

				} else {
					/*
					 * If CLIENT, do Effects
					 */
					int recoiltime_l = (int) ((((float) recoiltime / 20.0f) * 1000.0f));
					int muzzleFlashtime_l = (int) ((((float) muzzleFlashtime / 20.0f) * 1000.0f));

					if (!checkRecoil || !ShooterValues.isStillRecoiling(player, hand == InteractionHand.OFF_HAND, ATTACK_TYPE)) {
						ShooterValues.setRecoiltime(player, hand == InteractionHand.OFF_HAND, System.currentTimeMillis() + recoiltime_l, recoiltime_l, ATTACK_TYPE);
					}

					if (!checkMuzzleFlash || ShooterValues.getMuzzleFlashTime(player, hand == InteractionHand.OFF_HAND) <= System.currentTimeMillis()) {
						ShooterValues.setMuzzleFlashTime(player, hand == InteractionHand.OFF_HAND, System.currentTimeMillis() + muzzleFlashtime_l, muzzleFlashtime_l);
						Techguns.proxy.randomizeMuzzleFlashJitter(world.random);
					}

					client_weaponFired();
				}
				/*
				 * Do sounds on Client & Server
				 */
				if (maxLoopDelay > 0 && extendedPlayer.getLoopSoundDelay(hand) <= 0) {

					SoundUtil.playSoundOnEntityGunPosition(world, player, this.firesoundStart, SOUND_DISTANCE, 1.0F, false, false, TGSoundCategory.GUN_FIRE);

					extendedPlayer.setLoopSoundDelay(hand, this.maxLoopDelay);

				} else {

					SoundUtil.playSoundOnEntityGunPosition(world, player, this.firesound, SOUND_DISTANCE, 1.0F, false, false, TGSoundCategory.GUN_FIRE);
					if (this.maxLoopDelay > 0) {
						extendedPlayer.setLoopSoundDelay(hand, this.maxLoopDelay);
					}
				}

				if (!(rechamberSound == null)) {
					SoundUtil.playSoundOnEntityGunPosition(world, player, rechamberSound, 1.0F, 1.0F, false, false, TGSoundCategory.RELOAD);
				}
			}

		} else {
			//mag empty, reload needed

			//look for ammo, creative players reload for free
			boolean creative = isCreative(player);
			if (creative || InventoryUtil.consumeAmmoPlayer(player, this.ammoType.getAmmo(this.getCurrentAmmoVariant(stack)))) {

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

				//stop toggle zooming when reloading
				if (world.isClientSide) {
					if (canZoom && this.toggleZoom) {
						if (Techguns.proxy.getPlayerZoom() != 1.0f) {
							Techguns.proxy.setPlayerZoom(1.0f);
						}
					}
				}

				//START RELOAD
				extendedPlayer.setFireDelay(hand, this.reloadtime - this.minFiretime);

				if (ammoCount > 1) {
					int i = 1;
					while (i < ammoCount && (creative || InventoryUtil.consumeAmmoPlayer(player, this.ammoType.getAmmo(this.getCurrentAmmoVariant(stack))))) {
						i++;
					}
					this.reloadAmmo(stack, i);
				} else {
					this.reloadAmmo(stack);
				}

				SoundUtil.playReloadSoundOnEntity(world, player, reloadsound, 1.0F, 1.0F, false, true, TGSoundCategory.RELOAD);

				if (world.isClientSide) {
					int time = (int) (((float) reloadtime / 20.0f) * 1000);
					ShooterValues.setReloadtime(player, hand == InteractionHand.OFF_HAND, System.currentTimeMillis() + time, time, ATTACK_TYPE);
					client_startReload();
				} else {
					//send reloadpacket to clients
					int msg_reloadtime = ((int) (((float) reloadtime / 20.0f) * 1000.0f));
					TGPackets.network.sendToAllAround(new ReloadStartedMessage(player, hand, msg_reloadtime, ATTACK_TYPE), TGPackets.targetPointAroundEnt(player, 100.0));
				}
			}
		}
	}

	/**
	 * for extra actions in subclass
	 */
	protected void client_weaponFired() {
	}

	/**
	 * for extra actions in subclass
	 */
	protected void client_startReload() {
	}

	public GenericGun setAIStats(float attackRange, int attackTime, int burstCount, int burstAttackTime) {
		this.AI_attackRange = attackRange;
		this.AI_attackTime = attackTime;
		this.AI_burstCount = burstCount;
		this.AI_burstAttackTime = burstAttackTime;
		return this;
	}

	public GenericGun setTexture(String path) {
		return setTextures(path, 1);
	}

	/**
	 * Texture paths are lowercased, all assets of the port are lowercase
	 */
	public GenericGun setTextures(String path, int variations) {
		return setTextures(new ResourceLocation(Techguns.MODID, path.toLowerCase()), variations);
	}

	public GenericGun setTexture(ResourceLocation path) {
		return setTextures(path, 1);
	}

	public GenericGun setTextures(ResourceLocation path, int variations) {
		this.textures = new ArrayList<ResourceLocation>();
		for (int i = 0; i < variations; i++) {
			this.textures.add(new ResourceLocation(path.getNamespace(), path.getPath() + (i != 0 ? ("_" + i) : "") + ".png"));
		}
		this.camoCount = variations;
		return this;
	}

	@Override
	public ResourceLocation getCurrentTexture(ItemStack stack) {
		int camo = this.getCurrentCamoIndex(stack);
		if (camo < this.textures.size()) {
			return textures.get(camo);
		}
		return this.textures.get(0);
	}

	protected void shootGun(Level world, LivingEntity player, ItemStack itemstack, float accuracybonus, float damagebonus, int attackType, InteractionHand hand, EnumBulletFirePos firePos, Entity target) {

		//send pakets to clients
		if (!world.isClientSide) {
			int msg_recoiltime = ((int) (((float) recoiltime / 20.0f) * 1000.0f));
			int msg_muzzleflashtime = ((int) (((float) muzzleFlashtime / 20.0f) * 1000.0f));
			TGPackets.network.sendToAllAround(new GunFiredMessage(player, msg_recoiltime, msg_muzzleflashtime, attackType, checkRecoil, hand == InteractionHand.OFF_HAND), TGPackets.targetPointAroundEnt(player, 100.0f));
		}

		spawnProjectile(world, player, itemstack, accuracy * accuracybonus, projectileForwardOffset, damagebonus, firePos, target);

		if (shotgun) {
			float offset = 0;
			if (this.burst) {
				offset = this.speed / (this.bulletcount);
			}

			for (int i = 0; i < bulletcount; i++) {
				spawnProjectile(world, player, itemstack, spread * accuracybonus, projectileForwardOffset + offset * (i + 1.0f), damagebonus, firePos, target);
			}
		}
	}

	/**
	 * Creates the gun NBT (replaces 1.12 onCreated)
	 */
	public void initTags(ItemStack stack) {
		CompoundTag tags = stack.getTag();
		if (tags == null) {
			tags = new CompoundTag();
			tags.putByte("camo", (byte) 0);
			tags.putString("ammovariant", AmmoTypes.TYPE_DEFAULT);
			tags.putShort("ammo", (short) this.clipsize);
			this.addInitialTags(tags);
			stack.setTag(tags);
		} else if (!tags.contains("ammo")) {
			tags.putShort("ammo", (short) this.clipsize);
			if (!tags.contains("ammovariant")) {
				tags.putString("ammovariant", AmmoTypes.TYPE_DEFAULT);
			}
			this.addInitialTags(tags);
		}
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level world, Player player) {
		this.initTags(stack);
	}

	@Override
	public ItemStack getDefaultInstance() {
		ItemStack stack = new ItemStack(this);
		this.initTags(stack);
		return stack;
	}

	/**
	 * Add subclass tags here
	 */
	protected void addInitialTags(CompoundTag tags) {
	}

	public int getCurrentAmmo(ItemStack stack) {
		CompoundTag tags = stack.getTag();
		if (tags == null || !tags.contains("ammo")) {
			this.initTags(stack);
			tags = stack.getTag();
		}
		return tags.getShort("ammo");
	}

	public int getCurrentAmmoVariant(ItemStack stack) {
		String variant = this.getCurrentAmmoVariantKey(stack);
		return this.getAmmoType().getIDforVariantKey(variant);
	}

	public String getCurrentAmmoVariantKey(ItemStack stack) {
		CompoundTag tags = stack.getTag();
		if (tags == null) {
			this.initTags(stack);
			tags = stack.getTag();
		}
		String var = tags.getString("ammovariant");
		if (var == null || var.equals("")) return AmmoTypes.TYPE_DEFAULT;
		return var;
	}

	/**
	 * Reduces ammo by amount, use positive values! amount=3 means ammo-=3;
	 *
	 * @return actually consumed ammo
	 */
	public int useAmmo(ItemStack stack, int amount) {
		int ammo = this.getCurrentAmmo(stack);
		CompoundTag tags = stack.getTag();
		if (ammo - amount >= 0) {
			tags.putShort("ammo", (short) (ammo - amount));
			return amount;
		} else {
			tags.putShort("ammo", (short) 0);
			return ammo;
		}
	}

	public void reloadAmmo(ItemStack stack) {
		this.reloadAmmo(stack, this.clipsize);
	}

	public void reloadAmmo(ItemStack stack, int amount) {
		int ammo = this.getCurrentAmmo(stack);
		CompoundTag tags = stack.getTag();
		tags.putShort("ammo", (short) (ammo + amount));
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round((float) (13.0 * getPercentAmmoLeft(stack)));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		float f = Math.max(0.0F, (float) getPercentAmmoLeft(stack));
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}

	public double getPercentAmmoLeft(ItemStack stack) {
		return Mth.clamp(((double) this.getCurrentAmmo(stack)) / ((double) this.clipsize), 0.0, 1.0);
	}

	@Override
	public int getAmmoLeft(ItemStack stack) {
		return this.getCurrentAmmo(stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || oldStack.getItem() != newStack.getItem();
	}

	@Override
	public boolean isRepairable(ItemStack stack) {
		return false;
	}

	public GenericGun setDamageDrop(float start, float end, float minDamage) {
		this.damageDropStart = start;
		this.damageDropEnd = end;
		this.damageMin = minDamage;
		return this;
	}

	public GenericGun setSilenced(boolean s) {
		this.silenced = s;
		return this;
	}

	@Override
	public GunHandType getGunHandType() {
		return this.handType;
	}

	public GenericGun setHandType(GunHandType type) {
		this.handType = type;
		return this;
	}

	@Override
	public boolean isHoldZoom() {
		return !this.toggleZoom;
	}

	@Override
	public float getZoomMult() {
		return this.zoomMult;
	}

	protected String getTooltipTextDmg(ItemStack stack, boolean expanded) {

		DamageModifier mod = this.projectile_selector.getFactoryForType(this.getCurrentAmmoVariantKey(stack)).getDamageModifier();
		float dmg = mod.getDamage(this.damage);
		if (dmg == this.damage) {
			return "" + this.damage + (this.damageMin < this.damage ? "-" + this.damageMin : "");
		} else {
			float dmgmin = mod.getDamage(this.damageMin);
			ChatFormatting prefix = ChatFormatting.GREEN;
			String sgn = "+";
			if (dmg < this.damage) {
				prefix = ChatFormatting.RED;
				sgn = "-";
			}

			String suffix = "";

			if (expanded) {
				if (mod.getDmgMul() != 1f) {
					float f = mod.getDmgMul() - 1f;
					String x = String.format("%.0f", f * 100f);
					suffix += " (" + sgn + x + "%)";
				}
				if (mod.getDmgAdd() != 0f) {
					float add = mod.getDmgAdd();
					suffix += " (" + (add > 0 ? "+" : "") + String.format("%.1f", add) + ")";
				}
			}

			String sd = String.format("%.1f", dmg);
			String sm = String.format("%.1f", dmgmin);

			return prefix + "" + sd + (dmgmin < dmg ? "-" + sm : "") + suffix;
		}
	}

	protected String getTooltipTextRange(ItemStack stack) {
		DamageModifier mod = this.projectile_selector.getFactoryForType(this.getCurrentAmmoVariantKey(stack)).getDamageModifier();

		int ttl = mod.getTTL(this.ticksToLive);
		float rangeStart = mod.getRange(this.damageDropStart);
		float rangeEnd = mod.getRange(this.damageDropEnd);

		String prefix = "";
		String suffix = "";
		if (rangeStart != this.damageDropStart) {

			String sgn = "+";
			if (rangeStart > this.damageDropStart) {
				prefix = ChatFormatting.GREEN.toString();
			} else {
				prefix = ChatFormatting.RED.toString();
				sgn = "-";
			}

			if (mod.getRangeMul() != 1f) {
				float f = mod.getRangeMul() - 1f;
				String x = String.format("%.0f", f * 100f);
				suffix += " (" + sgn + x + "%)";
			}
			if (mod.getRangeAdd() != 0f) {
				float add = mod.getRangeAdd();
				suffix += " (" + (add > 0 ? "+" : "") + String.format("%.1f", add) + ")";
			}
		}

		String sStart = String.format("%.1f", rangeStart);
		String sEnd = String.format("%.1f", rangeEnd);

		if (this.rangeTooltipType == RangeTooltipType.DROP) {
			return TextUtil.trans("techguns.gun.tooltip.range") + ": " + prefix + sStart + "," + sEnd + "," + ttl + suffix;
		} else if (this.rangeTooltipType == RangeTooltipType.NO_DROP) {
			return TextUtil.trans("techguns.gun.tooltip.range") + ": " + prefix + sStart + suffix;
		} else {
			return TextUtil.trans("techguns.gun.tooltip.radius") + ": " + prefix + sStart + "-" + sEnd + suffix;
		}
	}

	protected String getTooltipTextVelocity(ItemStack stack) {
		DamageModifier mod = this.projectile_selector.getFactoryForType(this.getCurrentAmmoVariantKey(stack)).getDamageModifier();

		float velocity = mod.getVelocity(this.speed);

		String prefix = "";
		String suffix = "";
		if (velocity != this.speed) {

			String sgn = "+";
			if (velocity >= this.speed) {
				prefix = ChatFormatting.GREEN.toString();
			} else {
				prefix = ChatFormatting.RED.toString();
				sgn = "";
			}

			if (mod.getVelocityMul() != 1f) {
				float f = mod.getVelocityMul() - 1f;
				String x = String.format("%.0f", f * 100f);
				suffix += " (" + sgn + x + "%)";
			}
			if (mod.getVelocityAdd() != 0f) {
				float add = mod.getVelocityAdd();
				suffix += " (" + (add > 0 ? "+" : "") + String.format("%.1f", add) + ")";
			}
		}

		String sVelocity = String.format("%.1f", velocity);

		return TextUtil.trans("techguns.gun.tooltip.velocity") + ": " + prefix + sVelocity + suffix;
	}

	protected void addMiningTooltip(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flagIn, boolean longTooltip) {
	}

	protected static void addLine(List<Component> list, String text) {
		list.add(Component.literal(text).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, list, flagIn);
		if (Techguns.proxy.isShiftKeyDown()) {
			list.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.handtype") + ": ").withStyle(ChatFormatting.GRAY).append(this.getGunHandType().getDisplayComponent()));

			ItemStack[] ammo = this.ammoType.getAmmo(this.getCurrentAmmoVariant(stack));
			for (int i = 0; i < ammo.length; i++) {
				addLine(list, TextUtil.trans("techguns.gun.tooltip.ammo") + ": " + (this.ammoCount > 1 ? this.ammoCount + "x " : "") + ChatFormatting.WHITE + ammo[i].getHoverName().getString());
			}
			this.addMiningTooltip(stack, worldIn, list, flagIn, true);
			list.add(Component.literal(TextUtil.trans("techguns.gun.tooltip.damageType") + ": ").withStyle(ChatFormatting.GRAY).append(this.getDamageType(stack).getDisplayComponent()));
			addLine(list, TextUtil.trans("techguns.gun.tooltip.damage") + (this.shotgun ? ("(x" + (this.bulletcount + 1) + ")") : "") + ": " + getTooltipTextDmg(stack, true));
			addLine(list, getTooltipTextRange(stack));
			addLine(list, getTooltipTextVelocity(stack));
			addLine(list, TextUtil.trans("techguns.gun.tooltip.spread") + ": " + this.accuracy + (this.zoombonus != 1.0 ? (" Z:" + this.zoombonus * this.accuracy) : ""));
			addLine(list, TextUtil.trans("techguns.gun.tooltip.clipsize") + ": " + this.clipsize);
			addLine(list, TextUtil.trans("techguns.gun.tooltip.reloadTime") + ": " + this.reloadtime * 0.05f + "s");
			if (this.penetration > 0.0f) {
				addLine(list, TextUtil.trans("techguns.gun.tooltip.armorPen") + ": " + String.format("%.1f", this.penetration));
			}
			if (this.canZoom) {
				addLine(list, TextUtil.trans("techguns.gun.tooltip.zoom") + ":" + (this.toggleZoom ? "(" + TextUtil.trans("techguns.gun.tooltip.zoom.toogle") + ")" : "(" + TextUtil.trans("techguns.gun.tooltip.zoom.hold") + ")") + " " + TextUtil.trans("techguns.gun.tooltip.zoom.multiplier") + ":" + this.zoomMult);
			}

		} else {
			ItemStack[] ammo = this.ammoType.getAmmo(this.getCurrentAmmoVariant(stack));
			for (int i = 0; i < ammo.length; i++) {
				addLine(list, TextUtil.trans("techguns.gun.tooltip.ammo") + ": " + (this.ammoCount > 1 ? this.ammoCount + "x " : "") + ChatFormatting.WHITE + ammo[i].getHoverName().getString());
			}
			this.addMiningTooltip(stack, worldIn, list, flagIn, false);
			addLine(list, TextUtil.trans("techguns.gun.tooltip.damage") + (this.shotgun ? ("(x" + (this.bulletcount + 1) + ")") : "") + ": " + getTooltipTextDmg(stack, false));
			addLine(list, TextUtil.trans("techguns.gun.tooltip.shift1") + " " + ChatFormatting.GREEN + TextUtil.trans("techguns.gun.tooltip.shift2") + " " + ChatFormatting.GRAY + TextUtil.trans("techguns.gun.tooltip.shift3"));
		}
	}

	public DamageType getDamageType(ItemStack stack) {
		return this.projectile_selector.getFactoryForType(this.getCurrentAmmoVariantKey(stack)).getDamageType();
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

	/**
	 * Should this weapon shoot with left click instead of mine, defaults to true
	 */
	public GenericGun setShootWithLeftClick(boolean shootWithLeftClick) {
		this.shootWithLeftClick = shootWithLeftClick;
		return this;
	}

	public GenericGun setMiningAmmoConsumption(int ammo) {
		this.miningAmmoConsumption = ammo;
		return this;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity targetEntity) {

		if (this.shootWithLeftClick) {
			return true;
		} else {
			if (player.level().isClientSide) {
				int time = (int) (((float) this.recoiltime / 20.0f) * 1000);
				ShooterValues.setRecoiltime(player, false, System.currentTimeMillis() + time, time, (byte) 0);
			}

			/**
			 * COPY FROM Player.attack
			 */
			if (targetEntity.isAttackable() && GenericProjectile.BULLET_TARGETS.test(targetEntity)) {
				if (!targetEntity.skipAttackInteraction(player)) {
					float f = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
					float f1;

					if (targetEntity instanceof LivingEntity) {
						f1 = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), ((LivingEntity) targetEntity).getMobType());
					} else {
						f1 = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), MobType.UNDEFINED);
					}

					float f2 = player.getAttackStrengthScale(0.5F);
					f = f * (0.2F + f2 * f2 * 0.8F);
					f1 = f1 * f2;
					player.resetAttackStrengthTicker();

					if (f > 0.0F || f1 > 0.0F) {
						boolean flag = f2 > 0.9F;
						boolean flag1 = false;
						int i = 0;
						i = i + EnchantmentHelper.getKnockbackBonus(player);

						if (player.isSprinting() && flag) {
							player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, player.getSoundSource(), 1.0F, 1.0F);
							++i;
							flag1 = true;
						}

						boolean flag2 = flag && player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable() && !player.isInWater()
								&& !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() && targetEntity instanceof LivingEntity;
						flag2 = flag2 && !player.isSprinting();

						CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, targetEntity, flag2, flag2 ? 1.5F : 1.0F);
						flag2 = hitResult != null;
						if (flag2) {
							f *= hitResult.getDamageModifier();
						}

						f = f + f1;
						boolean flag3 = false;
						double d0 = (double) (player.walkDist - player.walkDistO);

						if (flag && !flag2 && !flag1 && player.onGround() && d0 < (double) player.getSpeed()) {
							flag3 = this.hasSwordSweep() && this.getAmmoLeft(stack) > 0;
						}

						float f4 = 0.0F;
						boolean flag4 = false;
						int j = EnchantmentHelper.getFireAspect(player);

						if (targetEntity instanceof LivingEntity) {
							f4 = ((LivingEntity) targetEntity).getHealth();

							if (j > 0 && !targetEntity.isOnFire()) {
								flag4 = true;
								targetEntity.setSecondsOnFire(1);
							}
						}

						Vec3 vec3 = targetEntity.getDeltaMovement();

						//EDIT: use Techguns damage source
						TGDamageSource src = getMeleeDamageSource(player, stack);
						boolean flag5 = targetEntity.hurt(src, f);

						if (flag5) {
							this.consumeAmmoOnMeleeHit(player, stack);

							if (i > 0) {
								if (targetEntity instanceof LivingEntity) {
									((LivingEntity) targetEntity).knockback((double) ((float) i * 0.5F), (double) Mth.sin(player.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(player.getYRot() * ((float) Math.PI / 180F))));
								} else {
									targetEntity.push((double) (-Mth.sin(player.getYRot() * ((float) Math.PI / 180F)) * (float) i * 0.5F), 0.1D, (double) (Mth.cos(player.getYRot() * ((float) Math.PI / 180F)) * (float) i * 0.5F));
								}

								player.setDeltaMovement(player.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
								player.setSprinting(false);
							}

							if (flag3) {
								float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * f;

								for (LivingEntity entitylivingbase : player.level().getEntitiesOfClass(LivingEntity.class, targetEntity.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
									if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isAlliedTo(entitylivingbase)
											&& (!(entitylivingbase instanceof ArmorStand) || !((ArmorStand) entitylivingbase).isMarker())
											&& player.distanceToSqr(entitylivingbase) < 9.0D) {
										entitylivingbase.knockback((double) 0.4F, (double) Mth.sin(player.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(player.getYRot() * ((float) Math.PI / 180F))));
										TGDamageSource dmgsrc = getMeleeDamageSource(player, stack);
										entitylivingbase.hurt(dmgsrc, f3);
										if (dmgsrc.wasSuccessful()) {
											this.onMeleeHitTarget(stack, entitylivingbase);
										}
									}
								}

								this.doSweepAttackEffect(player);
							}

							/**
							 * Extra hit effect
							 */
							this.onMeleeHitTarget(stack, targetEntity);

							if (targetEntity instanceof ServerPlayer && targetEntity.hurtMarked) {
								((ServerPlayer) targetEntity).connection.send(new ClientboundSetEntityMotionPacket(targetEntity));
								targetEntity.hurtMarked = false;
								targetEntity.setDeltaMovement(vec3);
							}

							if (flag2) {
								player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
								player.crit(targetEntity);
							}

							if (!flag2 && !flag3) {
								if (flag) {
									player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, player.getSoundSource(), 1.0F, 1.0F);
								} else {
									player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, player.getSoundSource(), 1.0F, 1.0F);
								}
							}

							if (f1 > 0.0F) {
								player.magicCrit(targetEntity);
							}

							player.setLastHurtMob(targetEntity);

							if (targetEntity instanceof LivingEntity) {
								EnchantmentHelper.doPostHurtEffects((LivingEntity) targetEntity, player);
							}

							EnchantmentHelper.doPostDamageEffects(player, targetEntity);
							ItemStack itemstack1 = player.getMainHandItem();
							Entity entity = targetEntity;

							if (targetEntity instanceof PartEntity) {
								entity = ((PartEntity<?>) targetEntity).getParent();
							}

							if (!player.level().isClientSide && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
								ItemStack beforeHitCopy = itemstack1.copy();
								itemstack1.hurtEnemy((LivingEntity) entity, player);

								if (itemstack1.isEmpty()) {
									ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, InteractionHand.MAIN_HAND);
									player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
								}
							}

							if (targetEntity instanceof LivingEntity) {
								float f5 = f4 - ((LivingEntity) targetEntity).getHealth();
								player.awardStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));

								if (j > 0) {
									targetEntity.setSecondsOnFire(j * 4);
								}

								if (player.level() instanceof ServerLevel && f5 > 2.0F) {
									int k = (int) ((double) f5 * 0.5D);
									((ServerLevel) player.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getX(), targetEntity.getY(0.5D), targetEntity.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
								}
							}

							player.causeFoodExhaustion(0.1F);
						} else {
							player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, player.getSoundSource(), 1.0F, 1.0F);

							if (flag4) {
								targetEntity.clearFire();
							}
						}
					}
				}
			}

			return true;
		}
	}

	protected void onMeleeHitTarget(ItemStack stack, Entity target) {
	}

	protected void consumeAmmoOnMeleeHit(LivingEntity elb, ItemStack stack) {
		if (isCreative(elb)) return;
		this.useAmmo(stack, 1);
	}

	protected void doSweepAttackEffect(Player player) {
		if (!player.level().isClientSide) {
			double d0 = (double) (-Mth.sin(player.getYRot() * 0.017453292F));
			double d1 = (double) Mth.cos(player.getYRot() * 0.017453292F);
			double x = player.getX() + d0;
			double y = player.getY() + player.getBbHeight() * 0.8d;
			double z = player.getZ() + d1;
			this.spawnSweepParticle(player.level(), x, y, z, d0, 0, d1);
			this.playSweepSoundEffect(player);
		}
	}

	protected void playSweepSoundEffect(Player player) {
		player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
	}

	protected void spawnSweepParticle(Level w, double x, double y, double z, double motionX, double motionY, double motionZ) {
	}

	/**
	 * Override in Subclass to define damagesource used for player melee attacks, only relevant if "shootWithLeftClick" is not set
	 */
	protected TGDamageSource getMeleeDamageSource(Player player, ItemStack stack) {
		return new TGDamageSource("player", player, player, DamageType.PHYSICAL, DeathType.GORE);
	}

	protected boolean hasSwordSweep() {
		return true;
	}

	public int getMiningAmmoConsumption() {
		return miningAmmoConsumption;
	}

	@Override
	public boolean isModelBase(ItemStack stack) {
		return this.hasCustomTexture;
	}

	public AmmoType getAmmoType() {
		return ammoType;
	}

	public float getAI_attackRange() {
		return AI_attackRange;
	}

	/**
	 * Ranged attack goal of NPCs (and turrets) using this gun
	 */
	public techguns.entities.ai.EntityAIRangedAttack getAIAttack(net.minecraft.world.entity.monster.RangedAttackMob shooter) {
		return new techguns.entities.ai.EntityAIRangedAttack(shooter, 1.0D, this.AI_attackTime / 3, this.AI_attackTime, this.AI_attackRange, this.AI_burstCount, this.AI_burstAttackTime);
	}

	public boolean isFullyLoaded(ItemStack stack) {
		return this.clipsize == this.getCurrentAmmo(stack);
	}

	public boolean hasRightClickAction() {
		return this.getGunHandType() == GunHandType.TWO_HANDED && this.canZoom;
	}

	/**
	 * Weapon is used by NPC
	 */
	public void fireWeaponFromNPC(LivingEntity shooter, float dmgscale, float accscale) {
		SoundUtil.playSoundOnEntityGunPosition(shooter.level(), shooter, firesound, SOUND_DISTANCE, 1.0F, false, false, TGSoundCategory.GUN_FIRE);

		EnumBulletFirePos firePos = EnumBulletFirePos.RIGHT;

		//lot J: 1.12 turrets fire from the center
		if (shooter instanceof techguns.entities.npcs.NPCTurret) {
			firePos = EnumBulletFirePos.CENTER;
		}

		if (!shooter.level().isClientSide) {
			this.shootGun(shooter.level(), shooter, shooter.getMainHandItem(), this.zoombonus * accscale, dmgscale, 0, InteractionHand.MAIN_HAND, firePos, null);
		}
	}

	/**
	 * Get all ammo and magazines the gun currently holds is retrievable
	 */
	public List<ItemStack> getAmmoOnUnload(ItemStack stack) {
		List<ItemStack> items = new ArrayList<>();

		int ammo = this.getCurrentAmmo(stack);

		if (this.ammoCount > 1 && this.getAmmoLeft(stack) > 0) {
			for (ItemStack s : this.getAmmoType().getBullet(this.getCurrentAmmoVariant(stack))) {
				items.add(TGItems.newStack(s, this.getAmmoLeft(stack)));
			}
		} else {
			if (!this.isFullyLoaded(stack)) {
				int amount = this.ammoType.getEmptyMag().length;

				for (int i = 0; i < amount; i++) {
					int bulletsBack = (int) Math.floor(ammo / this.ammoType.getShotsPerBullet(clipsize, ammo));
					if (bulletsBack > 0) {
						items.add(TGItems.newStack(this.getAmmoType().getBullet(this.getCurrentAmmoVariant(stack))[i], bulletsBack));
					}
					if (!this.ammoType.getEmptyMag()[i].isEmpty()) {
						items.add(TGItems.newStack(this.ammoType.getEmptyMag()[i], 1));
					}
				}
			} else {
				int amount = this.ammoType.getEmptyMag().length;
				for (int i = 0; i < amount; i++) {
					items.add(TGItems.newStack(this.ammoType.getAmmo(this.getCurrentAmmoVariant(stack))[i], 1));
				}
			}
		}

		return items;
	}

	/**
	 * try to force reload the gun, might lose some ammo. Creative players reload for free.
	 */
	public void tryForcedReload(ItemStack item, Level world, Player player, InteractionHand hand) {
		TGExtendedPlayer extendedPlayer = TGExtendedPlayer.get(player);
		if (extendedPlayer == null) {
			return;
		}

		if (extendedPlayer.getFireDelay(hand) <= 0 && !this.isFullyLoaded(item)) {

			int oldAmmo = this.getCurrentAmmo(item);
			boolean creative = isCreative(player);

			//look for ammo
			if (creative || InventoryUtil.consumeAmmoPlayer(player, this.getReloadItem(item))) {

				//empty gun and do reload if we can't put in ammo individual
				if (ammoCount <= 1) {
					this.useAmmo(item, oldAmmo);
				}

				if (!creative) {
					int ammos = this.getAmmoType().getEmptyMag().length;
					for (int i = 0; i < ammos; i++) {
						if (!this.ammoType.getEmptyMag()[i].isEmpty()) {

							int amount = InventoryUtil.addAmmoToPlayerInventory(player, TGItems.newStack(this.ammoType.getEmptyMag()[i], 1));
							if (amount > 0 && !world.isClientSide) {
								player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), TGItems.newStack(this.ammoType.getEmptyMag()[i], amount)));
							}

							int bulletsBack = (int) Math.floor(oldAmmo / this.ammoType.getShotsPerBullet(clipsize, oldAmmo));
							if (bulletsBack > 0) {
								int amount2 = InventoryUtil.addAmmoToPlayerInventory(player, TGItems.newStack(this.ammoType.getBullet(this.getCurrentAmmoVariant(item))[i], bulletsBack));
								if (amount2 > 0 && !world.isClientSide) {
									player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), TGItems.newStack(this.ammoType.getBullet(this.getCurrentAmmoVariant(item))[i], amount2)));
								}
							}
						}
					}
				}

				//stop toggle zooming when reloading
				if (world.isClientSide) {
					if (canZoom && this.toggleZoom) {
						if (Techguns.proxy.getPlayerZoom() != 1.0f) {
							Techguns.proxy.setPlayerZoom(1.0f);
						}
					}
				}

				extendedPlayer.setFireDelay(hand, this.reloadtime - this.minFiretime);

				if (ammoCount > 1) {
					int i = 1;
					while (i < (ammoCount - oldAmmo) && (creative || InventoryUtil.consumeAmmoPlayer(player, this.ammoType.getAmmo(this.getCurrentAmmoVariant(item))))) {
						i++;
					}
					this.reloadAmmo(item, i);
				} else {
					this.reloadAmmo(item);
				}
				SoundUtil.playReloadSoundOnEntity(world, player, reloadsound, 1.0F, 1.0F, false, true, TGSoundCategory.RELOAD);

				if (world.isClientSide) {
					int time = (int) (((float) reloadtime / 20.0f) * 1000);
					ShooterValues.setReloadtime(player, hand == InteractionHand.OFF_HAND, System.currentTimeMillis() + time, time, (byte) 0);
					client_startReload();
				} else {
					int msg_reloadtime = ((int) (((float) reloadtime / 20.0f) * 1000.0f));
					TGPackets.network.sendToAllAround(new ReloadStartedMessage(player, hand, msg_reloadtime, 0), TGPackets.targetPointAroundEnt(player, 100.0f));
				}
			}
		}
	}

	public int getClipsize() {
		return clipsize;
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
		return !(this.handType == GunHandType.TWO_HANDED);
	}

	public boolean canClickBlock(Level world, Player player, InteractionHand hand) {
		BlockHitResult raytraceresult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
		return raytraceresult != null && raytraceresult.getType() == HitResult.Type.BLOCK;
	}

	public float getSpread() {
		return spread;
	}

	public GunHandType getHandType() {
		return handType;
	}

	public float getZoombonus() {
		return zoombonus;
	}

	public EnumCrosshairStyle getCrossHairStyle() {
		return this.crossHairStyle;
	}

	public GenericGun setCrossHair(EnumCrosshairStyle crosshair) {
		this.crossHairStyle = crosshair;
		return this;
	}

	public float getDamage() {
		return damage;
	}

	public int getRecoiltime() {
		return recoiltime;
	}

	public int getReloadtime() {
		return reloadtime;
	}

	public int getMinFiretime() {
		return minFiretime;
	}

	public boolean canZoom() {
		return canZoom;
	}

	/**
	 * Ticks for a full charge, overridden by charging guns (GenericGunCharge.fullChargeTime), used by the renderer
	 */
	@Override
	public float getFullChargeTime() {
		return 1.0f;
	}

	/**
	 * Mining head upgrade level, overridden by GenericGunMeleeCharge (used by the renderers of mining tools)
	 */
	public int getMiningHeadLevel(ItemStack stack) {
		return 0;
	}

	/**
	 * Mining radius, overridden by GenericGunMeleeCharge
	 */
	public int getMiningRadius(ItemStack stack) {
		return 0;
	}
}
