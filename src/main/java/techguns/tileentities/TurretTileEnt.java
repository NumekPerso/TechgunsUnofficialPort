package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import techguns.TGItems;
import techguns.TGMachines;
import techguns.Techguns;
import techguns.api.guns.IGenericGun;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;
import techguns.entities.npcs.NPCTurret;
import techguns.gui.ButtonConstants;
import techguns.gui.containers.TurretContainer;
import techguns.items.guns.GenericGun;
import techguns.items.guns.ammo.AmmoType;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.util.ItemUtil;

/**
 * Turret base: holds the gun, armor and ammo of the turret entity (NPCTurret) it spawns, repairs it when it dies.
 */
public class TurretTileEnt extends BasicPoweredTileEnt {

	public static final int SLOT_INPUT1 = 0;
	public static final int INPUTS_SIZE = 9;

	public static final int SLOT_OUTPUT1 = SLOT_INPUT1 + INPUTS_SIZE;
	public static final int OUTPUTS_SIZE = 9;

	public static final int SLOT_WEAPON = SLOT_OUTPUT1 + OUTPUTS_SIZE;
	public static final int SLOT_ARMOR = SLOT_WEAPON + 1;

	public static final int BUTTON_ID_TARGET_ANIMALS = ButtonConstants.BUTTON_ID_REDSTONE + 1;
	public static final int BUTTON_ID_PVP_SETTING = ButtonConstants.BUTTON_ID_REDSTONE + 2;

	public NPCTurret mountedTurret = null;

	public boolean turretDeath = false;
	public int repairTime = 0;
	public int turretHealTime = 0;

	public boolean attackAnimals = false;

	protected Direction facing = Direction.UP;

	/**
	 * 0: no PvP
	 * 1: attack enemies
	 * 2: attack neutrals
	 * 4: attack allies
	 * 3: attack teammembers (all but owner)
	 **/
	protected byte pvpsetting = 0;

	public TurretTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.TURRET_TILE, pos, state, SLOT_ARMOR + 1, false, 5000);
		//9 slots input, 9 slots output, 1 weaponslot, 1 armorslot

		this.inventory = new ItemStackHandlerPlus(SLOT_ARMOR + 1) {

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setContentsChanged(true);
				if (slot == SLOT_WEAPON) {
					TurretTileEnt.this.updateWeapon();
				} else if (slot == SLOT_ARMOR && level != null && !level.isClientSide) {
					//the armor is visible (texture tier)
					TurretTileEnt.this.needUpdate();
				}
			}

			@Override
			protected boolean allowItemInSlot(int slot, ItemStack stack) {
				if (slot == SLOT_WEAPON) {
					return stack.getItem() instanceof IGenericGun;
				} else if (slot == SLOT_ARMOR) {
					return stack.getItem() instanceof ITGSpecialSlot && ((ITGSpecialSlot) stack.getItem()).getSlot(stack) == TGSlotType.TURRETARMOR;
				} else if (slot >= SLOT_INPUT1 && slot < SLOT_INPUT1 + INPUTS_SIZE) {
					//1.12 accepted anything, only gun ammo now
					return AmmoType.isAmmo(stack);
				}
				return false;
			}

			@Override
			protected boolean allowExtractFromSlot(int slot, int amount) {
				return slot >= SLOT_OUTPUT1 && slot < SLOT_OUTPUT1 + OUTPUTS_SIZE;
			}
		};
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Techguns.MODID + ".container.turretbase");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new TurretContainer(id, inv, this);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition).inflate(1);
	}

	public int getRepairTimeScaled(int max) {
		int totaltime = this.getRepairTimeMax();
		return max - (this.repairTime * max / totaltime);
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		//entity ids are not persistent: only link on the client, the server turret relinks itself (NPCTurret.tick)
		if (tags.contains("mountedTurret") && this.level != null && this.level.isClientSide) {
			Entity ent = this.level.getEntity(tags.getInt("mountedTurret"));
			if (ent instanceof NPCTurret) {
				this.mountedTurret = (NPCTurret) ent;
				this.mountedTurret.setTileEnt(this);
			}
		}

		this.facing = Direction.from3DDataValue(tags.getByte("facing"));
		this.turretDeath = tags.getBoolean("turretDeath");
		this.repairTime = tags.getInt("repairTime");
		this.turretHealTime = tags.getInt("turretHealTime");
		this.attackAnimals = tags.getBoolean("attackAnimals");
		this.pvpsetting = tags.getByte("pvpsetting");

		//weapon and armor are client data (rendering), the full inventory is saved by BasicInventoryTileEnt
		if (tags.contains("weapon")) {
			this.inventory.setStackInSlot(SLOT_WEAPON, ItemStack.of(tags.getCompound("weapon")));
		}
		if (tags.contains("armor")) {
			this.inventory.setStackInSlot(SLOT_ARMOR, ItemStack.of(tags.getCompound("armor")));
		}
	}

	@Override
	public boolean hasRotation() {
		return true;
	}

	@Override
	public void rotateTile(Direction sideHit) {
		if (this.facing != sideHit && (sideHit == Direction.UP || sideHit == Direction.DOWN)) {
			this.facing = sideHit;
			if (this.mountedTurret != null) {
				float health = this.mountedTurret.getHealth();

				this.mountedTurret.setHealth(0);
				this.mountedTurret.discard();
				this.spawnTurret(this.level, this.getBlockPos(), health);
			}
			if (!this.level.isClientSide) {
				this.needUpdate();
			}
		}
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		if (this.mountedTurret != null) {
			tags.putInt("mountedTurret", this.mountedTurret.getId());
		}
		tags.putByte("facing", (byte) this.facing.get3DDataValue());
		tags.putBoolean("turretDeath", this.turretDeath);
		tags.putInt("repairTime", this.repairTime);
		tags.putInt("turretHealTime", this.turretHealTime);
		tags.putBoolean("attackAnimals", this.attackAnimals);
		tags.putByte("pvpsetting", this.pvpsetting);

		tags.put("weapon", this.inventory.getStackInSlot(SLOT_WEAPON).save(new CompoundTag()));
		tags.put("armor", this.inventory.getStackInSlot(SLOT_ARMOR).save(new CompoundTag()));
	}

	public Direction getFacing() {
		return facing;
	}

	public void setFacing(Direction facing) {
		this.facing = facing;
	}

	/**
	 * Returns the itemstack in the gun slot.
	 */
	public ItemStack getWeapon() {
		return this.inventory.getStackInSlot(SLOT_WEAPON);
	}

	public boolean consumeAmmo() {

		ItemStack gunstack = this.getWeapon();
		if (!gunstack.isEmpty() && gunstack.getItem() instanceof GenericGun) {
			GenericGun gun = (GenericGun) gunstack.getItem();

			if (gun.getCurrentAmmo(gunstack) <= 0) {
				this.doReload(gunstack);
			}

			if (gun.getCurrentAmmo(gunstack) >= 1) {
				gun.useAmmo(gunstack, 1);
				this.setChanged();
				return true;
			}
		}

		return false;
	}

	/**
	 * 1.12 InventoryUtil.canConsumeItem on the item handler: missing amount, 0 = can consume
	 */
	protected int canConsumeItem(ItemStack item, int startIndex, int endIndex) {
		int amount = item.getCount();
		for (int i = startIndex; i < endIndex; i++) {
			ItemStack s = this.inventory.getStackInSlot(i);
			if (ItemUtil.isItemEqual(s, item)) {
				amount -= s.getCount();
				if (amount <= 0) {
					return 0;
				}
			}
		}
		return amount;
	}

	/**
	 * 1.12 InventoryUtil.consumeAmmo on the item handler: consumes one item
	 */
	protected boolean consumeOne(ItemStack ammo, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			ItemStack s = this.inventory.getStackInSlot(i);
			if (!s.isEmpty() && ItemUtil.isItemEqual(s, ammo)) {
				this.inventory.extractWithoutCheck(i, 1, false);
				return true;
			}
		}
		return false;
	}

	/**
	 * 1.12 InventoryUtil.addItemToInventory on the item handler: returns the amount that did not fit
	 */
	protected int addItemToSlots(ItemStack item, int startIndex, int endIndex) {
		ItemStack left = item.copy();
		for (int i = startIndex; i < endIndex && !left.isEmpty(); i++) {
			left = this.inventory.insertItemNoCheck(i, left, false);
		}
		return left.getCount();
	}

	protected void doReload(ItemStack gunstack) {
		GenericGun gun = (GenericGun) gunstack.getItem();
		ItemStack ammo[] = gun.getAmmoType().getAmmo(gun.getCurrentAmmoVariant(gunstack));
		ItemStack emptyMag[] = gun.getAmmoType().getEmptyMag();

		boolean canConsume = true;
		for (int i = 0; i < emptyMag.length; i++) {
			if (this.canConsumeItem(ammo[i], SLOT_INPUT1, SLOT_INPUT1 + INPUTS_SIZE) > 0) {
				canConsume = false;
				break;
			}
		}

		if (canConsume) {
			for (int i = 0; i < emptyMag.length; i++) {

				if (this.consumeOne(ammo[i], SLOT_INPUT1, SLOT_INPUT1 + INPUTS_SIZE)) {
					if (!emptyMag[i].isEmpty()) {
						int tooMuch = this.addItemToSlots(TGItems.newStack(emptyMag[i], 1), SLOT_OUTPUT1, SLOT_OUTPUT1 + OUTPUTS_SIZE);
						if (!this.level.isClientSide && tooMuch > 0) {
							this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX(), this.worldPosition.getY() + 1, this.worldPosition.getZ(), TGItems.newStack(emptyMag[i], tooMuch)));
						}
					}

					if (gun.getAmmoCount() > 1) {
						int j = 1;
						while (j < gun.getAmmoCount() && this.consumeOne(ammo[i], SLOT_INPUT1, SLOT_INPUT1 + INPUTS_SIZE)) {
							j++;
						}

						if (i == 0) {
							gun.reloadAmmo(gunstack, j);
						}
					} else {
						if (i == 0) {
							gun.reloadAmmo(gunstack);
						}
					}

					if (!this.level.isClientSide) {
						this.needUpdate();
					}
				}
			}
		}
	}

	protected int getRepairTimeMax() {
		return 10 * 20;
	}

	protected int getRepairPowerAmount() {
		return 50;
	}

	protected int getOperationPowerAmount() {
		return 5;
	}

	protected int getPowerAmountHealTick() {
		return 500;
	}

	protected int getHealTickDelay() {
		return 20 * 3;
	}

	public void setMountedTurret(NPCTurret mountedTurret) {
		this.mountedTurret = mountedTurret;
	}

	public int getTurretArmorValue() {
		ItemStack armor = this.inventory.getStackInSlot(SLOT_ARMOR);
		if (!armor.isEmpty()) {
			if (armor.getItem() == TGItems.TURRET_ARMOR_IRON) {
				return 5;
			} else if (armor.getItem() == TGItems.TURRET_ARMOR_STEEL) {
				return 10;
			} else if (armor.getItem() == TGItems.TURRET_ARMOR_OBSIDIAN_STEEL) {
				return 15;
			} else if (armor.getItem() == TGItems.TURRET_ARMOR_CARBON) {
				return 20;
			}
		}
		return 0;
	}

	/**
	 * called when base block is broken
	 */
	public void breakTurret() {
		if (this.mountedTurret != null) {
			this.mountedTurret.setHealth(0);
			this.mountedTurret.discard();
		}
	}

	/**
	 * Called from the turret npc when he dies
	 */
	public void onTurretDeath() {
		if (!this.level.isClientSide) {
			this.mountedTurret = null;
			this.turretDeath = true;
			this.repairTime = this.getRepairTimeMax();
			this.needUpdate();
		}
	}

	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if (id < BUTTON_ID_TARGET_ANIMALS) {
			super.buttonClicked(id, ply, data);
		} else if ((id == BUTTON_ID_TARGET_ANIMALS) && (this.isUseableByPlayer(ply))) {
			this.attackAnimals = !this.attackAnimals;
			if (!this.level.isClientSide) {
				this.needUpdate();
			}
		} else if (id == BUTTON_ID_PVP_SETTING && (this.isOwnedByPlayer(ply))) {

			if (FTBLIB_ENABLED) {
				this.pvpsetting++;
				this.pvpsetting = (byte) (this.pvpsetting % 5);
			} else {
				if (this.pvpsetting == 0) {
					this.pvpsetting = 4;
				} else {
					this.pvpsetting = 0;
				}
			}

			if (!this.level.isClientSide) {
				this.needUpdate();
			}
		}
	}

	public void updateWeapon() {
		if (this.mountedTurret != null && this.level != null) {
			this.mountedTurret.setCombatTask();
			if (!this.level.isClientSide) {
				this.needUpdate();
			}
		}
	}

	@Override
	public void update() {

		if (!this.level.isClientSide) {

			if (this.turretDeath) {
				if (this.isRedstoneEnabled()) {
					if (this.consumePower(this.getRepairPowerAmount())) {
						this.repairTime--;
					}

					if (repairTime <= 0) {
						repairTime = 0;
						this.turretDeath = false;

						spawnTurret(this.level, this.worldPosition, 10.0f);
						this.needUpdate();
					}
				}
			} else if (this.mountedTurret != null && (this.mountedTurret.isRemoved() || !this.mountedTurret.isAlive())) {
				this.onTurretDeath();
			} else if (this.mountedTurret != null) {

				if (this.isRedstoneEnabled()) {

					if (!this.consumePower(this.getOperationPowerAmount())) {
						//power off turret
						if (this.mountedTurret.active) {
							this.mountedTurret.disable();
							this.mountedTurret.active = false;
						}

					} else if (!this.mountedTurret.active) {
						//activate turret
						this.mountedTurret.active = true;
						this.mountedTurret.setAITasks();
						this.mountedTurret.setCombatTask();
					}

					if (this.turretHealTime <= 0) {
						if (this.mountedTurret.getHealth() < this.mountedTurret.getMaxHealth()) {
							if (this.consumePower(this.getPowerAmountHealTick())) {
								this.mountedTurret.heal(1.0f);
								this.turretHealTime = this.getHealTickDelay();
							}
						}
					} else {
						this.turretHealTime--;
					}
				} else {

					if (this.mountedTurret.active) {
						this.mountedTurret.disable();
						this.mountedTurret.active = false;
					}

				}
			}
		} else {
			/**
			 * CLIENT SIDE: the turret entity links itself to the base (NPCTurret.tick)
			 */
			if (this.mountedTurret != null && this.mountedTurret.mountedTileEnt == null) {
				this.mountedTurret.setTileEnt(this);
			}
		}
	}

	public int getTurretTier() {
		ItemStack armorstack = this.inventory.getStackInSlot(SLOT_ARMOR);
		if (!armorstack.isEmpty()) {
			if (armorstack.getItem() == TGItems.TURRET_ARMOR_IRON) {
				return 1;
			} else if (armorstack.getItem() == TGItems.TURRET_ARMOR_STEEL) {
				return 2;
			} else if (armorstack.getItem() == TGItems.TURRET_ARMOR_OBSIDIAN_STEEL) {
				return 3;
			} else if (armorstack.getItem() == TGItems.TURRET_ARMOR_CARBON) {
				return 4;
			}
		}
		return 0;
	}

	public ResourceLocation getTurretBaseTexture() {
		return NPCTurret.getTexture(this.getTurretTier());
	}

	public void setTurretPosition() {
		double x = this.worldPosition.getX() + 0.5d;
		double y = this.worldPosition.getY() + 0.5d;
		double z = this.worldPosition.getZ() + 0.5d;

		switch (this.facing) {
		case DOWN:
			y -= 0.5d;
			y -= this.mountedTurret.getBbHeight();
			break;
		case EAST:
			x += 0.5d;
			break;
		case NORTH:
			z -= 0.5d;
			break;
		case SOUTH:
			z += 0.5d;
			break;
		case UP:
			y += 0.5d;
			break;
		case WEST:
			x -= 0.5d;
			break;
		default:
			break;
		}
		this.mountedTurret.setPos(x, y, z);
	}

	public void spawnTurret(Level w, BlockPos p, float health) {

		if (!w.isClientSide) {
			this.mountedTurret = new NPCTurret(w, this);
			this.mountedTurret.setTurretFacing(this.facing);
			this.setTurretPosition();
			this.mountedTurret.setHealth(health);
			this.turretDeath = false;
			w.addFreshEntity(this.mountedTurret);
			this.needUpdate();
		}
	}

	public void spawnTurret(Level w, BlockPos p) {

		if (!w.isClientSide) {
			this.mountedTurret = new NPCTurret(w, this);
			this.mountedTurret.setTurretFacing(this.facing);
			this.setTurretPosition();
			this.turretDeath = false;
			w.addFreshEntity(this.mountedTurret);
			this.needUpdate();
		}
	}

	@Override
	public void onBlockBreak() {
		super.onBlockBreak();
		if (this.mountedTurret != null) {
			this.mountedTurret.setHealth(0);
			this.mountedTurret.discard();
		}
	}

	public byte getPvpSetting() {
		return pvpsetting;
	}

	public void setPvpSetting(byte pvpsetting) {
		this.pvpsetting = pvpsetting;
	}

}
