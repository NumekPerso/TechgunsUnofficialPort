package techguns.capabilities;

import java.util.BitSet;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import techguns.TGConfig;
import techguns.api.capabilities.AttackTime;
import techguns.api.capabilities.ITGExtendedPlayer;
import techguns.api.guns.IGenericGun;
import techguns.gui.player.TGPlayerInventory;
import techguns.util.DataUtil;

public class TGExtendedPlayer implements ITGExtendedPlayer {

	public static final Capability<TGExtendedPlayer> TG_EXTENDED_PLAYER = CapabilityManager.get(new CapabilityToken<>() {});

	public static final int MAX_RADIATION = 1000;

	public int fireDelayMainhand = 0;
	public int fireDelayOffhand = 0;

	public int loopSoundDelayMainhand = 0;
	public int loopSoundDelayOffhand = 0;

	public Player entity;

	protected AttackTime[] attackTimes = { new AttackTime(), new AttackTime() };

	public int swingSoundDelay = 0;

	public boolean gotCreativeFlightLastTick = false;
	public boolean wasFlying = false;

	public ItemStack gunMainHand = ItemStack.EMPTY;
	public ItemStack gunOffHand = ItemStack.EMPTY;

	public Entity lockOnEntity; //Guided Missile tracking target
	public int lockOnTicks; //number of ticks the tracked target has been locked on.

	/**
	 * Used by server only, needed for jetpack, not saved to nbt
	 */
	protected boolean isJumpkeyPressed = false;
	/**
	 * Used by server only, needed for jetpack, not saved to nbt
	 */
	public boolean isForwardKeyPressed = false;

	/**
	 * only used by client, not synced
	 */
	public boolean isGliding = false;

	/**
	 * Server only, not saved: copies of the TG inventory slots last sent to the clients (see AdditionalSlotEventHandler).
	 * Replaces the 1.12 synced entity data parameters of the face/back/hand slots.
	 */
	public ItemStack[] lastSyncedSlots = null;

	/**
	 * Server only, not saved: RAD level last sent to the client (radiation.RadiationEventHandler)
	 */
	public int lastSyncedRadlevel = -1;

	/**
	 * SAVED FIELDS
	 */
	public TGPlayerInventory tg_inventory;

	public boolean enableJetpack = true;
	public boolean enableStepAssist = true;
	public boolean enableNightVision = false;
	public boolean enableSafemode = false;
	/**
	 * for jetpack
	 */
	public boolean enableHovermode = false;

	public int radlevel = 0;

	public short foodleft = 0;
	public float lastSaturation = 1.0f;

	/**
	 * used by client only, but saved serverside
	 */
	public boolean showTGHudElements = true;

	public TGExtendedPlayer(Player entity) {
		this.entity = entity;
		this.tg_inventory = new TGPlayerInventory(entity);
	}

	@Override
	public Player getEntity() {
		return entity;
	}

	public void copyFrom(TGExtendedPlayer other) {
		this.fireDelayMainhand = other.fireDelayMainhand;
		this.fireDelayOffhand = other.fireDelayOffhand;
		this.loopSoundDelayMainhand = other.loopSoundDelayMainhand;
		this.loopSoundDelayOffhand = other.loopSoundDelayOffhand;
		this.attackTimes = other.attackTimes;
		this.swingSoundDelay = other.swingSoundDelay;
		this.gotCreativeFlightLastTick = other.gotCreativeFlightLastTick;
		this.wasFlying = other.wasFlying;
		this.gunMainHand = other.gunMainHand;
		this.gunOffHand = other.gunOffHand;
		this.isJumpkeyPressed = other.isJumpkeyPressed;
		this.isForwardKeyPressed = other.isForwardKeyPressed;
		this.isGliding = other.isGliding;
		//1.12 copied the inventory object (which kept the old player), the port copies the content
		for (int i = 0; i < this.tg_inventory.inventory.size(); i++) {
			this.tg_inventory.inventory.set(i, other.tg_inventory.inventory.get(i));
		}
		this.enableJetpack = other.enableJetpack;
		this.enableStepAssist = other.enableStepAssist;
		this.enableNightVision = other.enableNightVision;
		this.enableSafemode = other.enableSafemode;
		this.enableHovermode = other.enableHovermode;
		this.radlevel = other.radlevel;
		this.foodleft = other.foodleft;
		this.lastSaturation = other.lastSaturation;
		this.showTGHudElements = other.showTGHudElements;
	}

	public static TGExtendedPlayer get(Player ply) {
		if (ply == null) return null;
		return ply.getCapability(TG_EXTENDED_PLAYER).orElse(null);
	}

	public static boolean canUseUnsafeMode(Player ply) {
		if (TGConfig.limitUnsafeModeToOP) {
			return ply.hasPermissions(2);
		}
		return true;
	}

	@Override
	public AttackTime getAttackTime(boolean offHand) {
		return attackTimes[offHand ? 1 : 0];
	}

	@Override
	public int getFireDelay(InteractionHand hand) {
		return hand == InteractionHand.OFF_HAND ? fireDelayOffhand : fireDelayMainhand;
	}

	@Override
	public void setFireDelay(InteractionHand hand, int delay) {
		if (hand == InteractionHand.OFF_HAND) {
			this.fireDelayOffhand = delay;
		} else {
			this.fireDelayMainhand = delay;
		}
	}

	public int getLoopSoundDelay(InteractionHand hand) {
		return hand == InteractionHand.OFF_HAND ? loopSoundDelayOffhand : loopSoundDelayMainhand;
	}

	public void setLoopSoundDelay(InteractionHand hand, int delay) {
		if (hand == InteractionHand.OFF_HAND) {
			this.loopSoundDelayOffhand = delay;
		} else {
			this.loopSoundDelayMainhand = delay;
		}
	}

	@Override
	public boolean isRecoiling(boolean offHand) {
		return getAttackTime(offHand).isRecoiling();
	}

	@Override
	public boolean isReloading(boolean offHand) {
		return getAttackTime(offHand).isReloading();
	}

	public void swapAttackTimes() {
		AttackTime a = this.attackTimes[0];
		this.attackTimes[0] = this.attackTimes[1];
		this.attackTimes[1] = a;

		int i = fireDelayMainhand;
		this.fireDelayMainhand = fireDelayOffhand;
		this.fireDelayOffhand = i;
	}

	@Override
	public TGPlayerInventory getTGInventory() {
		return this.tg_inventory;
	}

	@Override
	public void saveToNBT(CompoundTag tags) {
		this.tg_inventory.saveNBTData(tags);

		byte data = DataUtil.compress(this.enableJetpack, this.enableNightVision, this.enableSafemode, this.enableStepAssist, this.showTGHudElements, this.enableHovermode);
		tags.putByte("states", data);
		tags.putShort("foodLeft", this.foodleft);
		tags.putFloat("lastSaturation", this.lastSaturation);
		tags.putInt("radlevel", this.radlevel);
	}

	@Override
	public void loadFromNBT(CompoundTag tags) {
		this.tg_inventory.loadNBTData(tags);
		if (!tags.contains("states")) {
			return;
		}
		BitSet states = DataUtil.uncompress(tags.getByte("states"));
		this.enableJetpack = states.get(0);
		this.enableNightVision = states.get(1);
		this.enableSafemode = states.get(2);
		if (entity != null && !canUseUnsafeMode(entity)) {
			this.enableSafemode = true;
		}
		this.enableStepAssist = states.get(3);
		this.showTGHudElements = states.get(4);
		this.enableHovermode = states.get(5);

		this.foodleft = tags.getShort("foodLeft");
		this.lastSaturation = tags.getFloat("lastSaturation");
		this.radlevel = tags.getInt("radlevel");
	}

	public boolean isJumpkeyPressed() {
		return isJumpkeyPressed;
	}

	/**
	 * Overridden by the client version (jetpack sounds, 1.12 TGExtendedPlayerClient)
	 */
	public void setJumpkeyPressed(boolean isJumpkeyPressed) {
		this.isJumpkeyPressed = isJumpkeyPressed;
	}

	/**
	 * Called every player tick on both sides after the additional slots ticked. The client version plays the glider sound
	 * (1.12 ClientProxy.handlePlayerGliding), no proxy method needed since the client capability is a TGExtendedPlayerClient.
	 */
	public void handlePlayerGliding() {
	}

	/**
	 * Drops to world (player death)
	 */
	public void dropInventory(Player player) {
		if (!player.level().isClientSide) {
			if (!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
				for (int i = 0; i < this.tg_inventory.inventory.size(); ++i) {
					if (!this.tg_inventory.inventory.get(i).isEmpty()) {
						player.level().addFreshEntity(createDrop(player, this.tg_inventory.inventory.get(i)));
						this.tg_inventory.inventory.set(i, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	/**
	 * Adds the drops to the drop list of the LivingDropsEvent (1.12 PlayerDropsEvent)
	 */
	public void addDropsToList(Player player, List<ItemEntity> list) {
		if (!player.level().isClientSide) {
			if (!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
				for (int i = 0; i < this.tg_inventory.inventory.size(); ++i) {
					if (!this.tg_inventory.inventory.get(i).isEmpty()) {
						list.add(createDrop(player, this.tg_inventory.inventory.get(i)));
						this.tg_inventory.inventory.set(i, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	/**
	 * 1.12 player.dropItem(stack, true, false) without spawning the entity
	 */
	protected static ItemEntity createDrop(Player player, ItemStack stack) {
		ItemEntity itementity = new ItemEntity(player.level(), player.getX(), player.getEyeY() - 0.3F, player.getZ(), stack);
		itementity.setPickUpDelay(40);
		float f = player.getRandom().nextFloat() * 0.5F;
		float f1 = player.getRandom().nextFloat() * (float) (Math.PI * 2);
		itementity.setDeltaMovement((double) (-Mth.sin(f1) * f), 0.2F, (double) (Mth.cos(f1) * f));
		return itementity;
	}

	/**
	 * The 1.12 version used a synced data parameter, vanilla already syncs the item use state, so it is derived from it.
	 */
	public boolean isChargingWeapon() {
		if (this.entity == null || !this.entity.isUsingItem()) {
			return false;
		}
		ItemStack use = this.entity.getUseItem();
		return !use.isEmpty() && use.getItem() instanceof IGenericGun && ((IGenericGun) use.getItem()).canCharge();
	}

	/**
	 * Kept for API compatibility, charging state is derived from the item use state.
	 */
	public void setChargingWeapon(boolean charging) {
	}

	public void addRadiation(int amount) {
		if (!techguns.TGRadiationSystem.isEnabled()) {
			amount = 0;
		}
		this.radlevel += amount;
		if (this.radlevel < 0) {
			this.radlevel = 0;
		} else if (this.radlevel > MAX_RADIATION) {
			this.radlevel = MAX_RADIATION;
		}
	}
}
