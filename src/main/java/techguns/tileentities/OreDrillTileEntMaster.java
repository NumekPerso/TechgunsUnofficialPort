package techguns.tileentities;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import techguns.TGConfig;
import techguns.TGFluids;
import techguns.TGMachines;
import techguns.TGOreClusters;
import techguns.TGOreClusters.OreCluster;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;
import techguns.blocks.BlockOreCluster;
import techguns.blocks.machines.multiblocks.OreDrillDefinition;
import techguns.gui.containers.OreDrillContainer;
import techguns.items.TGSharedItem;
import techguns.packets.PacketPlaySound;
import techguns.tileentities.operation.FluidTankPlus;
import techguns.tileentities.operation.ITileEntityFluidTanks;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.util.TGSoundCategory;

public class OreDrillTileEntMaster extends MultiBlockMachineTileEntMaster implements ITileEntityFluidTanks {

	protected static final float SOUND_VOLUME = 0.5f;

	protected int engines;
	protected int rods;
	protected int radius;
	protected Direction drill_direction;
	protected boolean hasPower = false;
	protected boolean hasDrill = false;
	protected int drillType = 0;

	//amount of internalFuel
	protected int currentFuelBufferMax = 0;
	protected int fuelBuffer = 0;

	protected int soundLoopDelay = 0;

	public ItemStack lastDrill = ItemStack.EMPTY;

	protected static final int useRangeSquared = 144;

	public static final int CAPACITY_INPUT_TANK = 16 * FluidType.BUCKET_VOLUME;
	public static final int CAPACITY_OUTPUT_TANK = 32 * FluidType.BUCKET_VOLUME;

	public FluidTankPlus inputTank;
	public FluidTankPlus outputTank;

	protected OreDrillFluidHandler fluidHandler;
	protected LazyOptional<IFluidHandler> fluidCap;

	public static final int SLOT_DRILL = 0;
	public static final int SLOT_FURNACE = 1;
	public static final int SLOT_OUTPUT1 = 2;
	public static final int SLOT_OUTPUT_COUNT = 9;

	public OreDrillTileEntMaster(BlockPos pos, BlockState state) {
		super(TGMachines.OREDRILL_TILE_MASTER, pos, state, 11, 500000);

		this.inventory = new ItemStackHandlerPlus(11) {

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setContentsChanged(true);
			}

			@Override
			protected boolean allowItemInSlot(int slot, ItemStack stack) {
				if (slot <= SLOT_FURNACE) {
					return OreDrillTileEntMaster.this.isItemValidForSlot(slot, stack); //qualified: ItemStackHandlerPlus has its own isItemValidForSlot that calls allowItemInSlot
				} else {
					return false; //no inputing to output slots
				}
			}

			@Override
			protected boolean allowExtractFromSlot(int slot, int amount) {
				return slot >= SLOT_OUTPUT1 && slot < SLOT_OUTPUT1 + SLOT_OUTPUT_COUNT;
			}
		};

		this.hasDrill = false;

		this.inputTank = new FluidTankPlus(this, CAPACITY_INPUT_TANK);
		this.outputTank = new FluidTankPlus(this, CAPACITY_OUTPUT_TANK);

		this.fluidHandler = new OreDrillFluidHandler(this);
		this.fluidCap = LazyOptional.of(() -> this.fluidHandler);
	}

	protected boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (stack.isEmpty()) return false;
		if (slot == SLOT_DRILL && stack.getItem() instanceof ITGSpecialSlot) {
			TGSlotType slotType = ((ITGSpecialSlot) stack.getItem()).getSlot(stack);
			return slotType == TGSlotType.DRILL_SMALL || slotType == TGSlotType.DRILL_MEDIUM || slotType == TGSlotType.DRILL_LARGE;
		} else if (slot == SLOT_FURNACE) {
			return ForgeHooks.getBurnTime(stack, null) > 0;
		}
		return false;
	}

	public void formOreDrill(Direction direction, int engines, int rods, int radius, Direction drill_direction) {
		this.engines = engines;
		this.rods = rods;
		this.radius = radius;
		this.drill_direction = drill_direction;

		super.form(direction);
	}

	public void setFuelBuffer(int fuelBuffer) {
		this.fuelBuffer = fuelBuffer;
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Techguns.MODID + ".container.oredrill");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new OreDrillContainer(id, inv, this);
	}

	@Override
	public AABB getRenderBoundingBox() {
		if (this.isFormed() && drill_direction != null) {
			BlockPos p = this.getBlockPos();
			BlockPos rod_end = p.relative(drill_direction, rods + engines + 1);

			BlockPos other = rod_end.relative(OreDrillDefinition.getSide1(drill_direction), +(radius + 2)).relative(OreDrillDefinition.getSide2(drill_direction), +(radius + 1));
			BlockPos first = p.relative(OreDrillDefinition.getSide1(drill_direction), -(radius + 2)).relative(OreDrillDefinition.getSide2(drill_direction), -(radius + 1));
			return new AABB(first, other).inflate(1);
		} else {
			return super.getRenderBoundingBox();
		}
	}

	/**
	 * If currently a drill item is in the slot
	 */
	public boolean hasDrill() {
		return hasDrill;
	}

	/**
	 * Equipped Drilltype
	 * 0-no drill
	 * 1-steel
	 * 2-obsidian
	 * 3-carbon
	 */
	protected int getDrillHeadType() {
		int mininglevel = this.getDrillItemMiningLevel();
		if (mininglevel < 0) return 0;
		return mininglevel;
	}

	public int getDrillType() {
		return drillType;
	}

	public boolean hasPower() {
		return hasPower;
	}

	@Override
	public void unform() {
		super.unform();
		this.engines = 0;
		this.rods = 0;
		this.radius = 0;
		this.drill_direction = null;
	}

	public int getEngines() {
		return engines;
	}

	public int getRods() {
		return rods;
	}

	public int getRadius() {
		return radius;
	}

	/**
	 * The "radius" of the drill including the middle block, so 1 for 1x1xN, 2 for 3x3xN. 3 for 5x5xN and 4 for 7x7xN
	 * 1x1x1 drill with only controller has radius 0
	 */
	public int getDrillRadius() {
		if (this.engines == 0) return 0;
		return radius + 1;
	}

	public Direction getDrill_direction() {
		return drill_direction;
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		if (this.formed) {
			this.drill_direction = Direction.from3DDataValue(tags.getByte("drill_direction"));
			this.engines = tags.getByte("engines");
			this.rods = tags.getByte("rods");
			this.radius = tags.getByte("radius");
			this.hasPower = tags.getBoolean("haspower");
			this.hasDrill = tags.getBoolean("hasDrill");
			this.drillType = tags.getByte("drillType");
		}

		this.fuelBuffer = tags.getInt("fuelBuffer");
		this.currentFuelBufferMax = tags.getInt("fuelBufferMax");

		this.loadTanksFromNBT(tags);
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		if (this.formed && this.drill_direction != null) {
			tags.putByte("drill_direction", (byte) this.drill_direction.get3DDataValue());
			tags.putByte("engines", (byte) engines);
			tags.putByte("rods", (byte) rods);
			tags.putByte("radius", (byte) radius);
			tags.putBoolean("haspower", this.hasPower);
			tags.putBoolean("hasDrill", this.hasDrill);
			tags.putByte("drillType", (byte) this.drillType);
		}

		tags.putInt("fuelBuffer", this.fuelBuffer);
		tags.putInt("fuelBufferMax", this.currentFuelBufferMax);

		this.saveTanksToNBT(tags);
	}

	@Override
	public AABB getBBforSlave(BlockPos slavePos) {
		return new AABB(0, 0, 0, 1, 1, 1);
	}

	@Override
	protected int getNeededPower() {
		if (this.currentOperation != null) {
			return this.currentOperation.getPowerPerTick();
		}
		return 0;
	}

	@Override
	protected boolean consumePower(int amount) {
		if (TGConfig.machinesNeedNoPower) {
			return true;
		}

		float FACTOR = TGConfig.oreDrillMultiplierFuel;

		//check fuel buffer
		if (this.fuelBuffer * FACTOR >= amount) {
			this.fuelBuffer -= Math.max(amount / FACTOR, 1);
			return true;
		} else {
			amount -= this.fuelBuffer * FACTOR;
			this.fuelBuffer = 0;
			this.currentFuelBufferMax = 0;
			//check fuelBuffer refill;

			int fuelValue = 0;
			ItemStack fuelStack = this.inventory.getStackInSlot(SLOT_FURNACE);
			if (!fuelStack.isEmpty()) {
				fuelValue = ForgeHooks.getBurnTime(fuelStack, null);
			}

			if (fuelValue > 0) {
				ItemStack container = fuelStack.hasCraftingRemainingItem() ? fuelStack.getCraftingRemainingItem() : ItemStack.EMPTY;

				if (!container.isEmpty()) {
					ItemStack leftover = this.addItemToOutputSlots(container.copy());
					if (!leftover.isEmpty() && !this.level.isClientSide) {
						this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, leftover));
					}
				}

				//consume 1 fuel
				this.inventory.extractWithoutCheck(SLOT_FURNACE, 1, false);

				this.fuelBuffer = fuelValue;
				this.currentFuelBufferMax = fuelValue;
			} else {

				//check input tank
				float fuelvalue = this.getFuelValueFromInputTank();
				if (fuelvalue > 0) {

					FluidStack drainedFluid = this.inputTank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
					this.fuelBuffer = (int) (drainedFluid.getAmount() * fuelvalue);
					this.currentFuelBufferMax = (int) (drainedFluid.getAmount() * fuelvalue);

				}
			}
		}

		if (this.fuelBuffer * FACTOR >= amount) {
			this.fuelBuffer -= Math.max(amount / FACTOR, 1);
			return true;
		}

		if (amount > 0 && this.energy.getEnergyStored() >= amount) {
			this.energy.extractEnergy(amount, false);
			return true;
		} else if (amount == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 1.12 InventoryUtil.addItemToInventory on the output slots, returns what did not fit
	 */
	protected ItemStack addItemToOutputSlots(ItemStack stack) {
		ItemStack left = stack;
		for (int s = SLOT_OUTPUT1; s < SLOT_OUTPUT1 + SLOT_OUTPUT_COUNT && !left.isEmpty(); s++) {
			left = this.inventory.insertItemNoCheck(s, left, false);
		}
		return left;
	}

	protected float getFuelValueFromInputTank() {
		FluidStack fluid = this.inputTank.getFluid();
		if (!fluid.isEmpty()) {
			if (fluid.getFluid() == TGFluids.LAVA) {
				return 20F; //Same as LAVA BUCKET /1000
			} else if (TGFluids.isFuel(fluid)) {
				return TGConfig.oreDrillFuelValueFuel;
			}
		}

		return 0F;
	}

	protected boolean checkDrill() {
		if (this.level.getGameTime() % 20 == 0) {
			if (this.lastDrill.isEmpty() && this.inventory.getStackInSlot(SLOT_DRILL).isEmpty()) {
				this.hasDrill = false;
				this.needUpdate();
				return false;
			} else if (this.lastDrill.isEmpty()) {
				this.lastDrill = this.inventory.getStackInSlot(SLOT_DRILL);
				return true;
			} else {
				if (this.inventory.getStackInSlot(SLOT_DRILL).isEmpty() || !ItemStack.isSameItem(this.lastDrill, this.inventory.getStackInSlot(SLOT_DRILL))) {
					this.lastDrill = this.inventory.getStackInSlot(SLOT_DRILL).copy();
					this.currentOperation = null;
					this.progress = 0;
					this.totaltime = 0;
					this.needUpdate();
					return false;
				}

			}
		}
		return true;
	}

	@Override
	public void update() {
		if (this.isRedstoneEnabled()) {
			if (this.currentOperation != null) {

				if (this.level.isClientSide || this.checkDrill()) { //check drill on server
					if (this.consumePower(this.getNeededPower() * this.currentOperation.getStackMultiplier())) {
						this.progress++;

						playAmbientSound();

						if (!this.level.isClientSide && !this.hasPower) {
							this.hasPower = true;
							this.needUpdate();
						}

						if (progress >= totaltime) {

							if (!this.level.isClientSide) {
								this.finishedOperation();
							}
							this.progress = 0;
							this.totaltime = 0;
							this.currentOperation = null;

							if (!this.level.isClientSide) {
								checkAndStartOperation();
								this.needUpdate();
							}
						} else if (!this.level.isClientSide) {
							this.setChanged();
						}
					} else {
						//power consume failed
						if (!this.level.isClientSide && this.hasPower) {
							this.hasPower = false;
							this.needUpdate();
						}
					}
				}

			} else {

				if (!this.level.isClientSide && this.contentsChanged) {
					checkAndStartOperation();
				}

			}
		}

	}

	@Override
	protected void checkAndStartOperation() {
		if (this.isFormed() && this.drill_direction != null) {
			this.setContentsChanged(false);
			//look for cluster
			int distance = this.rods + this.engines + 1;
			BlockPos targetPos = this.getBlockPos().relative(drill_direction, distance);

			if (OreDrillDefinition.isOreCluster(this.level, targetPos)) {

				BlockState bs = this.level.getBlockState(targetPos);
				BlockOreCluster<?> clusterblock = (BlockOreCluster<?>) bs.getBlock();

				OreCluster cluster = TGOreClusters.orecluster.getClusterForType(clusterblock.getClusterType());

				int drillheadmininglevel = this.getDrillItemMiningLevel();
				if (drillheadmininglevel > 0 && cluster != null) {
					this.hasDrill = true;
					int effectivemininglevel = drillheadmininglevel + this.getDrillRadius() - cluster.getMininglevel();
					this.drillType = this.getDrillHeadType();

					if (effectivemininglevel >= 0) {

						int clustersize = this.getClusterSize(0, targetPos, new ArrayList<BlockPos>(), bs);
						int effectiveclustersize = Math.min(clustersize, this.getTotalDrillLength());

						double orePerHour = ((effectiveclustersize * 3) + (effectiveclustersize * effectivemininglevel * 0.5)) * TGConfig.oreDrillMultiplierOres * cluster.getMultiplier_amount();

						this.currentOperation = cluster.getNewOperation(this.level, orePerHour, this.getDrillRadius(), cluster.getMultiplier_power());
						this.progress = 0;

						this.totaltime = (int) (60.0 / orePerHour * 1200.0);
					} else {
						this.currentOperation = cluster.getCobbleStoneOperation();

						this.progress = 0;
						this.totaltime = 20 * 20;
					}
				} else {
					//no drillhead, broken
					this.hasDrill = false;
					this.drillType = 0;
				}
				this.needUpdate();

			} else {
				//There is no ore Cluster at drill point, multiblock broken
				this.onMultiBlockBreak();
				this.formed = false;
				this.needUpdate();
			}
		}
	}

	public int getTotalDrillLength() {
		return this.engines + this.rods;
	}

	/**
	 * Mining level of the drill head (TGSharedItem.getOreDrillMiningLevel), -1 if the drill head does not fit the drill size
	 */
	public int getDrillItemMiningLevel() {
		ItemStack drill = this.inventory.getStackInSlot(SLOT_DRILL);
		if (!drill.isEmpty() && this.isFormed() && drill.getItem() instanceof TGSharedItem shared && shared.getOreDrillMiningLevel() > 0) {

			TGSlotType type = shared.getSlot(drill);
			int r = this.getDrillRadius();
			if ((r == 0 && type == TGSlotType.DRILL_SMALL) || ((r == 1 || r == 2) && type == TGSlotType.DRILL_MEDIUM) || ((r == 3 || r == 4) && type == TGSlotType.DRILL_LARGE)) {
				return shared.getOreDrillMiningLevel();
			}
			return -1;
		}

		return -1;
	}

	@Override
	protected void finishedOperation() {
		ItemStack output = this.currentOperation.getItemOutput0();
		if (!output.isEmpty()) {

			ItemStack out = this.addItemToOutputSlots(output);

			if (!out.isEmpty()) {
				this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.5d, this.worldPosition.getZ() + 0.5d, out));
			}

		}

		FluidStack fluidOut = this.currentOperation.getFluidOutput0();
		if (fluidOut != null && !fluidOut.isEmpty()) {
			this.outputTank.fillInternal(fluidOut, IFluidHandler.FluidAction.EXECUTE);
		}

	}

	@Override
	protected void playAmbientSound() {
		if (drill_direction == null) {
			return;
		}
		BlockPos soundPos = this.getBlockPos().relative(drill_direction, engines).relative(drill_direction, (int) (rods * 0.5));
		if (!this.level.isClientSide && soundLoopDelay-- <= 0) {
			net.minecraft.sounds.SoundEvent sound;
			if (this.radius == 0) {
				sound = TGSounds.ORE_DRILL_WORK_SMALL;
			} else if (this.radius <= 2) {
				sound = TGSounds.ORE_DRILL_WORK_MEDIUM;
			} else {
				sound = TGSounds.ORE_DRILL_WORK_LARGE;
			}
			TGPackets.network.sendToAllAround(new PacketPlaySound(sound, soundPos.getX(), soundPos.getY(), soundPos.getZ(), SOUND_VOLUME, 1.0f, false, TGSoundCategory.MACHINE), TGPackets.targetPointAroundBlockPos(this.level.dimension(), soundPos, 32.0f));
			soundLoopDelay = 61; //62 ticks = 3.1 sec
		}
	}

	@Override
	public void onMultiBlockBreak() {
		super.onMultiBlockBreak();
		this.currentOperation = null;
		this.progress = 0;
		this.totaltime = 0;
		this.needUpdate();
	}

	public int getClusterSize(int count, BlockPos startPos, ArrayList<BlockPos> visited, BlockState clusterMeta) {

		BlockState targetState = this.level.getBlockState(startPos);

		if (targetState == clusterMeta && !visited.contains(startPos)) {
			count++;
			visited.add(startPos);

			count = getClusterSize(count, startPos.offset(1, 0, 0), visited, clusterMeta);
			count = getClusterSize(count, startPos.offset(-1, 0, 0), visited, clusterMeta);
			count = getClusterSize(count, startPos.offset(0, 1, 0), visited, clusterMeta);
			count = getClusterSize(count, startPos.offset(0, -1, 0), visited, clusterMeta);
			count = getClusterSize(count, startPos.offset(0, 0, 1), visited, clusterMeta);
			count = getClusterSize(count, startPos.offset(0, 0, -1), visited, clusterMeta);

			return count;

		} else {
			return count;
		}
	}

	public int getCurrentFuelBufferMax() {
		return currentFuelBufferMax;
	}

	public void setCurrentFuelBufferMax(int currentFuelBufferMax) {
		this.currentFuelBufferMax = currentFuelBufferMax;
	}

	public int getFuelBufferScaled(int limit) {
		if (this.currentFuelBufferMax > 0) {
			return this.fuelBuffer * limit / this.currentFuelBufferMax;
		} else {
			return 0;
		}
	}

	public int getFuelBuffer() {
		return fuelBuffer;
	}

	@Override
	public void saveTanksToNBT(CompoundTag tags) {
		CompoundTag inputTankTags = new CompoundTag();
		this.inputTank.writeToNBT(inputTankTags);
		tags.put("inputTank", inputTankTags);

		CompoundTag outputTankTags = new CompoundTag();
		this.outputTank.writeToNBT(outputTankTags);
		tags.put("outputTank", outputTankTags);
	}

	@Override
	public void loadTanksFromNBT(CompoundTag tags) {
		CompoundTag inputTank = tags.getCompound("inputTank");
		this.inputTank.readFromNBT(inputTank);

		CompoundTag outputTank = tags.getCompound("outputTank");
		this.outputTank.readFromNBT(outputTank);
	}

	public int getPowerPerTick() {
		if (this.currentOperation != null) {
			return this.currentOperation.getPowerPerTick();
		}
		return 0;
	}

	public int getValidSlotForItemInMachine(ItemStack stack) {
		if (this.isItemValidForSlot(SLOT_DRILL, stack)) return SLOT_DRILL;
		if (this.isItemValidForSlot(SLOT_FURNACE, stack)) return SLOT_FURNACE;
		return -1;
	}

	@Override
	protected int getUseRangeSquared() {
		return useRangeSquared;
	}

	@Override
	public <T> @Nonnull LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER && !this.remove) {
			return fluidCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fluidCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fluidCap = LazyOptional.of(() -> this.fluidHandler);
	}

	/**
	 * Fill goes to the input (fuel) tank, drain from the output tank
	 */
	public static class OreDrillFluidHandler implements IFluidHandler {

		private OreDrillTileEntMaster tile;

		public OreDrillFluidHandler(OreDrillTileEntMaster tile) {
			super();
			this.tile = tile;
		}

		@Override
		public int getTanks() {
			return 2;
		}

		@Override
		public @Nonnull FluidStack getFluidInTank(int tank) {
			return tank == 0 ? tile.inputTank.getFluid() : tile.outputTank.getFluid();
		}

		@Override
		public int getTankCapacity(int tank) {
			return tank == 0 ? tile.inputTank.getCapacity() : tile.outputTank.getCapacity();
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
			return tank == 0;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return tile.inputTank.fill(resource, action);
		}

		@Override
		public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) {
			return tile.outputTank.drain(resource, action);
		}

		@Override
		public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
			return tile.outputTank.drain(maxDrain, action);
		}
	}
}
