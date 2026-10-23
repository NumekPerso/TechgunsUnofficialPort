package techguns.tileentities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import techguns.TGConfig;
import techguns.TGMachines;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.blocks.machines.SimpleMachine;
import techguns.gui.containers.ChargingStationContainer;
import techguns.items.armors.ArmorPowerType;
import techguns.items.armors.PoweredArmor;
import techguns.tileentities.operation.ChargingStationRecipe;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.tileentities.operation.MachineOperation;
import techguns.tileentities.operation.MachineSlotItem;

/**
 * Charges items: recipe items (empty energy cell / redstone battery -> charged one) and items with Forge Energy
 * (ForgeCapabilities.ENERGY), charged items are moved to the output slot when full.
 * Port addition: RF powered armor (power armor, exo suit, miner, praetor) is charged like an energy item, see PoweredArmorEnergy.
 */
public class ChargingStationTileEnt extends BasicMachineTileEnt {

	protected static final float SOUND_VOLUME = 0.35f;

	public static final int SLOT_INPUT = 0;
	public static final int SLOT_OUTPUT = 1;
	public static final int SLOT_UPGRADE = 2;

	protected int lastsound = 0;

	MachineSlotItem input;

	public static final int CHARGERATE = 800;
	public static final int ITEMCHARGERATE = 1600;

	public boolean charging = false;

	public ChargingStationTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.CHARGING_STATION_TILE, pos, state, 3, false, 100000);
		input = new MachineSlotItem(this, SLOT_INPUT);

		this.inventory = new ItemStackHandlerPlus(3) {

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setContentsChanged(true);
			}

			@Override
			protected boolean allowItemInSlot(int slot, ItemStack stack) {
				switch (slot) {
				case SLOT_INPUT:
					return ChargingStationTileEnt.this.isItemValidForSlot(slot, stack); //qualified: ItemStackHandlerPlus has its own isItemValidForSlot that calls allowItemInSlot
				case SLOT_OUTPUT:
					return false;
				case SLOT_UPGRADE:
					return isMachineUpgrade(stack);
				}
				return false;
			}

			@Override
			protected boolean allowExtractFromSlot(int slot, int amount) {
				return slot == SLOT_OUTPUT;
			}
		};

	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Techguns.MODID + ".container.chargingstation");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new ChargingStationContainer(id, inv, this);
	}

	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return slot == this.getValidSlotForItemInMachine(item);
	}

	/**
	 * Energy storage of a chargeable item: Forge Energy capability, or the power of RF powered armor
	 */
	@Nullable
	public static IEnergyStorage getItemEnergy(ItemStack item) {
		if (item.isEmpty()) {
			return null;
		}
		IEnergyStorage cap = item.getCapability(ForgeCapabilities.ENERGY).orElse(null);
		if (cap != null) {
			return cap;
		}
		return PoweredArmorEnergy.create(item);
	}

	public int getValidSlotForItemInMachine(ItemStack item) {
		if (isMachineUpgrade(item)) {
			return SLOT_UPGRADE;
		}
		if (getItemEnergy(item) != null) {
			return SLOT_INPUT;
		}
		if (ChargingStationRecipe.getRecipeFor(item) != null) {
			return SLOT_INPUT;
		}
		return -1;
	}

	@Override
	protected int getNeededPower() {
		if (this.currentOperation != null) {
			return this.currentOperation.getPowerPerTick();
		}
		return 0;
	}

	@Override
	protected void checkAndStartOperation() {
		this.contentsChanged = false;
		if (!this.input.get().isEmpty()) {

			ChargingStationRecipe rec = ChargingStationRecipe.getRecipeFor(this.input.get());

			if (rec != null) {
				int stackMultiplier = this.getMaxMachineUpgradeMultiplier(SLOT_UPGRADE);

				stackMultiplier = Math.min(stackMultiplier, this.input.get().getCount() / rec.input.stackSize);

				stackMultiplier = this.getMaxOutputAmount(rec.output, stackMultiplier);

				if (stackMultiplier > 0) {

					ItemStack input = this.input.get().copy();
					input.setCount(rec.input.stackSize);
					MachineOperation op = new MachineOperation(rec.output.copy(), input);
					op.setStackMultiplier(stackMultiplier);
					op.setPowerPerTick(this.getChargeRate());

					this.input.consume(rec.input.stackSize * stackMultiplier);

					this.currentOperation = op;
					this.totaltime = (int) ((double) rec.chargeAmount / ((double) this.getChargeRate()));
					this.progress = 0;
					if (!this.level.isClientSide) {
						this.needUpdate();
					}
				}

			}

		}

	}

	protected int getChargeRate() {
		return CHARGERATE;
	}

	protected int getItemChargeRate() {
		return ITEMCHARGERATE;
	}

	protected int getMaxOutputAmount(ItemStack item, int maxMulti) {
		if (this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
			//1.12 returned min(maxStackSize, count*maxMulti) here, which is not a multiplier: keep the multiplier within the stack size
			int multi = maxMulti;
			while (multi > 0 && item.getCount() * multi > item.getMaxStackSize()) {
				multi--;
			}
			return multi;
		} else {

			if (ItemStack.isSameItemSameTags(this.inventory.getStackInSlot(SLOT_OUTPUT), item)) {
				int multi = maxMulti;
				while (multi > 0 && (inventory.getStackInSlot(SLOT_OUTPUT).getCount() + item.getCount() * multi) > inventory.getStackInSlot(SLOT_OUTPUT).getMaxStackSize()) {
					multi--;
				}
				return multi;
			}
			return 0;
		}
	}

	@Override
	protected void finishedOperation() {
		if (this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
			this.inventory.setStackInSlot(SLOT_OUTPUT, currentOperation.getItemOutput0());
		} else {
			this.inventory.insertItemNoCheck(SLOT_OUTPUT, currentOperation.getItemOutput0(), false);
		}
	}

	@Override
	protected void playAmbientSound() {
		if (this.level.isClientSide) {

			Player ply = Techguns.proxy.getPlayerClient();
			if (ply != null) {
				double distSq = ply.distanceToSqr(this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.5d, this.worldPosition.getZ() + 0.5d);

				if (distSq < 100) {

					BlockState state = this.getBlockState();
					int blockrot = state.hasProperty(SimpleMachine.FACING) ? state.getValue(SimpleMachine.FACING).get2DDataValue() : 0;

					double x = this.worldPosition.getX();
					double y = this.worldPosition.getY();
					double z = this.worldPosition.getZ();
					if (blockrot != 2) {
						Techguns.proxy.createFX("ChargeFlare2", this.level, x + 0.5d, y + 0.5d, z + 0.3d, 0, 0, 0);
					}
					if (blockrot != 0) {
						Techguns.proxy.createFX("ChargeFlare2", this.level, x + 0.5d, y + 0.5d, z + 0.7d, 0, 0, 0);
					}
					if (blockrot != 1) {
						Techguns.proxy.createFX("ChargeFlare2", this.level, x + 0.3d, y + 0.5d, z + 0.5d, 0, 0, 0);
					}
					if (blockrot != 3) {
						Techguns.proxy.createFX("ChargeFlare2", this.level, x + 0.7d, y + 0.5d, z + 0.5d, 0, 0, 0);
					}
				}
			}

			if (this.lastsound > 0) {
				this.lastsound--;
			}
			if (this.lastsound <= 0) {
				this.lastsound = 20;
				level.playLocalSound(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), TGSounds.CHARGING_STATION_WORK, SoundSource.BLOCKS, SOUND_VOLUME, 1.0F, false);
			}

		}
	}

	/**
	 * Item being charged, synced with the tile data so the renderer can show it without an open GUI
	 */
	public ItemStack getChargingItem() {
		return this.chargingItem;
	}

	protected ItemStack chargingItem = ItemStack.EMPTY;

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		this.charging = tags.getBoolean("charging");
		if (tags.contains("chargingItem")) {
			this.chargingItem = ItemStack.of(tags.getCompound("chargingItem"));
		} else {
			this.chargingItem = ItemStack.EMPTY;
		}
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putBoolean("charging", this.charging);
		if (this.charging && !this.inventory.getStackInSlot(SLOT_INPUT).isEmpty()) {
			tags.put("chargingItem", this.inventory.getStackInSlot(SLOT_INPUT).save(new CompoundTag()));
		}
	}

	@Override
	public void update() {

		if (this.isRedstoneEnabled()) {

			if (this.currentOperation != null) {

				if (this.consumePower(this.currentOperation.getPowerPerTick())) {
					this.progress++;

					playAmbientSound();

					if (!this.level.isClientSide && progress >= totaltime) {

						this.finishedOperation();

						this.progress = 0;
						this.totaltime = 0;
						this.currentOperation = null;

						checkAndStartOperation();
						this.needUpdate();
					} else if (!this.level.isClientSide) {
						this.setChanged();
					}
				}

			} else if (this.level.isClientSide) {
				//the client inventory is only synced while the GUI is open, the "charging" flag is synced with the tile
				if (this.charging) {
					playAmbientSound();
				}

			} else if (!this.input.get().isEmpty() && getItemEnergy(this.input.get()) != null) {

				IEnergyStorage itm = getItemEnergy(this.input.get());

				if (itm.receiveEnergy(this.getItemChargeRate(), true) > 0) {

					//port: with machinesNeedNoPower the items charge without using the internal storage
					int maxcharge = TGConfig.machinesNeedNoPower ? this.getItemChargeRate() : Math.min(this.energy.getEnergyStored(), this.getItemChargeRate());

					int pwr = itm.receiveEnergy(maxcharge, false);
					if (!TGConfig.machinesNeedNoPower) {
						this.energy.extractEnergy(pwr, false);
					}
					if (pwr > 0) {
						this.setChanged();
					}

					boolean nowCharging = pwr > 0;
					if (this.charging != nowCharging) {
						this.charging = nowCharging;
						this.needUpdate();
					}

				} else {
					if (!this.level.isClientSide) {
						if (this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
							this.inventory.setStackInSlot(SLOT_OUTPUT, this.inventory.getStackInSlot(SLOT_INPUT));
							this.inventory.setStackInSlot(SLOT_INPUT, ItemStack.EMPTY);
						}
						if (this.charging) {
							this.charging = false;
							this.needUpdate();
						}
					}
				}

			} else {
				if (!this.level.isClientSide) {
					if (this.charging) {
						this.charging = false;
						this.needUpdate();
					}
					if (this.contentsChanged) {
						this.checkAndStartOperation();
					}
				}
			}
		}

	}

	/**
	 * Port addition: Forge Energy view of the power of RF powered armor (1.12 powered armor had no energy capability and
	 * could only be recharged with batteries). A full charge costs as much energy as charging the battery the armor uses
	 * (energy cell: 50000 FE), armor without a rechargeable battery (steam armor, helmets/legs/boots) is not chargeable.
	 */
	public static class PoweredArmorEnergy implements IEnergyStorage {
		protected final ItemStack armor;
		protected final PoweredArmor item;
		protected final int energyPerPower;

		protected PoweredArmorEnergy(ItemStack armor, PoweredArmor item, int energyPerPower) {
			this.armor = armor;
			this.item = item;
			this.energyPerPower = Math.max(1, energyPerPower);
		}

		@Nullable
		public static PoweredArmorEnergy create(ItemStack stack) {
			if (stack.getItem() instanceof PoweredArmor armor && armor.getPowerType() == ArmorPowerType.RF && armor.maxpower > 0 && stack.getCount() == 1) {
				ItemStack battery = armor.getBattery();
				if (!battery.isEmpty()) {
					ChargingStationRecipe rec = ChargingStationRecipe.getRecipeWithOutput(battery.getItem());
					if (rec != null) {
						return new PoweredArmorEnergy(stack, armor, (int) Math.ceil((double) rec.chargeAmount / (double) armor.maxpower));
					}
				}
			}
			return null;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			int missing = item.maxpower - PoweredArmor.getPower(armor);
			int units = Math.min(missing, maxReceive / energyPerPower);
			if (units <= 0) {
				return 0;
			}
			if (!simulate) {
				units = item.setPowered(armor, units);
			}
			return units * energyPerPower;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return PoweredArmor.getPower(armor) * energyPerPower;
		}

		@Override
		public int getMaxEnergyStored() {
			return item.maxpower * energyPerPower;
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			return true;
		}
	}

}
