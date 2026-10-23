package techguns.tileentities;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.PacketDistributor;
import techguns.TGMachines;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;
import techguns.blocks.machines.multiblocks.ReactionChamberDefinition;
import techguns.blocks.machines.multiblocks.SlavePos;
import techguns.gui.ButtonConstants;
import techguns.gui.containers.ReactionChamberContainer;
import techguns.packets.PacketSpawnParticle;
import techguns.packets.PacketUpdateTileEntTanks;
import techguns.tileentities.operation.FluidTankPlus;
import techguns.tileentities.operation.ITileEntityFluidTanks;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.tileentities.operation.MachineSlotItem;
import techguns.tileentities.operation.ReactionBeamFocus;
import techguns.tileentities.operation.ReactionChamberOperation;
import techguns.tileentities.operation.ReactionChamberRecipe;
import techguns.tileentities.operation.ReactionChamberRecipe.RiskType;

/**
 * Controller of the reaction chamber multiblock (1.12 ReactionChamberTileEntMaster, OpenComputers integration not ported)
 */
public class ReactionChamberTileEntMaster extends MultiBlockMachineTileEntMaster implements ITileEntityFluidTanks {

	public ReactionChamberFluidTank inputTank;
	public static final int CAPACITY_INPUT_TANK = 10 * FluidType.BUCKET_VOLUME;

	public static final int SLOT_INPUT = 0;
	public static final int SLOT_FOCUS = 1;
	public static final int SLOT_OUTPUT = 2;
	public static final int OUTPUT_SLOTS_COUNT = 4;

	public static final int BUTTON_ID_LIQUIDLEVEL_INC = ButtonConstants.BUTTON_ID_REDSTONE + 1;
	public static final int BUTTON_ID_LIQUIDLEVEL_DEC = ButtonConstants.BUTTON_ID_REDSTONE + 2;
	public static final int BUTTON_ID_INTENSITY_INC = ButtonConstants.BUTTON_ID_REDSTONE + 3;
	public static final int BUTTON_ID_INTENSTIY_DEC = ButtonConstants.BUTTON_ID_REDSTONE + 4;
	public static final int BUTTON_ID_DUMPTANK = ButtonConstants.BUTTON_ID_REDSTONE + 5;

	protected byte intensity = 0;
	protected byte liquidLevel = 0;

	public MachineSlotItem input;

	public boolean fluidsChanged = false;

	protected LazyOptional<IFluidHandler> fluidCap;

	public ReactionChamberTileEntMaster(BlockPos pos, BlockState state) {
		super(TGMachines.REACTIONCHAMBER_MASTER_TILE, pos, state, 6, 1000000);

		this.inputTank = new ReactionChamberFluidTank(this, CAPACITY_INPUT_TANK);
		this.fluidCap = LazyOptional.of(() -> this.inputTank);

		this.inventory = new ItemStackHandlerPlus(6) {

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setContentsChanged(true);
			}

			@Override
			protected boolean allowItemInSlot(int slot, ItemStack stack) {
				if (slot <= SLOT_FOCUS) {
					return ReactionChamberTileEntMaster.this.isItemValidForSlot(slot, stack); //qualified: ItemStackHandlerPlus has its own isItemValidForSlot that calls allowItemInSlot
				} else {
					return false;
				}
			}

			@Override
			protected boolean allowExtractFromSlot(int slot, int amount) {
				return slot >= SLOT_OUTPUT && slot < SLOT_OUTPUT + OUTPUT_SLOTS_COUNT;
			}
		};

		input = new MachineSlotItem(this, SLOT_INPUT);

	}

	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (slot == SLOT_INPUT) {
			return true;
		} else if (slot == SLOT_FOCUS) {
			return ReactionBeamFocus.getBeamFocus(item) != null;
		}
		return false;
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
		fluidCap = LazyOptional.of(() -> this.inputTank);
	}

	@Override
	public AABB getRenderBoundingBox() {
		if (this.isFormed() && this.multiblockDirection != null) {
			BlockPos p = this.getBlockPos();
			Direction left = multiblockDirection.getClockWise();

			BlockPos other = p.relative(multiblockDirection, 3).relative(left, 2).relative(Direction.UP, 4);
			BlockPos first = p.relative(multiblockDirection.getOpposite(), 1).relative(left.getOpposite(), 2).relative(Direction.DOWN, 1);
			return new AABB(first, other);
		} else {
			return super.getRenderBoundingBox();
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new ReactionChamberContainer(id, inv, this);
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);

		this.loadTanksFromNBT(tags);

		this.intensity = tags.getByte("intensity");
		this.liquidLevel = tags.getByte("liquidLevel");

		if (tags.contains("inputSlot")) {
			ItemStack inputSlot = ItemStack.of(tags.getCompound("inputSlot"));
			this.inventory.setStackInSlot(SLOT_INPUT, inputSlot);
		} else {
			this.inventory.setStackInSlot(SLOT_INPUT, ItemStack.EMPTY);
		}

		if (tags.contains("focusSlot")) {
			ItemStack focusSlot = ItemStack.of(tags.getCompound("focusSlot"));
			this.inventory.setStackInSlot(SLOT_FOCUS, focusSlot);
		} else {
			this.inventory.setStackInSlot(SLOT_FOCUS, ItemStack.EMPTY);
		}
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);

		this.saveTanksToNBT(tags);

		tags.putByte("intensity", this.intensity);
		tags.putByte("liquidLevel", this.liquidLevel);

		if (!this.input.get().isEmpty()) {
			tags.put("inputSlot", this.input.get().save(new CompoundTag()));
		}
		if (!this.inventory.getStackInSlot(SLOT_FOCUS).isEmpty()) {
			tags.put("focusSlot", this.inventory.getStackInSlot(SLOT_FOCUS).save(new CompoundTag()));
		}
	}

	@Override
	public void readOperationFromNBT(CompoundTag tags) {
		if (tags.contains("operation")) {
			this.currentOperation = new ReactionChamberOperation(tags, this);
		} else {
			this.currentOperation = null;
		}
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Techguns.MODID + ".container.reactionchamber");
	}

	@Override
	protected int getNeededPower() {
		return 0;
	}

	@Override
	public void update() {
		if (!level.isClientSide && this.fluidsChanged) {
			this.fluidsChanged = false;
			this.needFluidUpdate();
		}
		if (this.isRedstoneEnabled()) {
			ReactionChamberOperation currentReaction = this.getCurrentReaction();
			if (currentReaction != null && currentReaction.getRecipe() != null) {

				this.progress++;
				boolean state = currentReaction.tick(this.intensity, this.liquidLevel, this.level.isClientSide, this, currentReaction.getRecipe().RFTick);

				if (!this.level.isClientSide && state) {
					if (currentReaction.isSuccess()) {

						this.finishedOperation();
						this.progress = 0;
						this.totaltime = 0;
						this.currentOperation = null;

						checkAndStartOperation();

					} else if (currentReaction.isFailure(this)) {

						RiskType risk = currentReaction.getRecipe().risk;

						this.progress = 0;
						this.totaltime = 0;
						this.currentOperation = null;

						if (risk == RiskType.EXPLOSION_LOW) {
							this.explode(0);
						} else if (risk == RiskType.EXPLOSION_MEDIUM) {
							this.explode(1);
						}

						if (!RiskType.isFatal(risk)) {
							checkAndStartOperation();
						}

					}
					this.needUpdate();
				}
				if (this.level.isClientSide && this.getCurrentReaction() != null) {
					if (this.getCurrentReaction().nextTick == ReactionChamberOperation.RECIPE_TICKRATE - 1) {
						this.playReactionTickSound(currentReaction.required_intensity == this.intensity);
					}
				}

			} else {

				if (!this.level.isClientSide && this.contentsChanged) {
					checkAndStartOperation();
					if (this.currentOperation != null) {
						this.needUpdate();
					}
				}

			}
		}
	}

	@Override
	protected void checkAndStartOperation() {
		this.contentsChanged = false;

		ReactionChamberRecipe rec = ReactionChamberRecipe.getMatchingRecipe(this.inventory.getStackInSlot(SLOT_INPUT), this.inventory.getStackInSlot(SLOT_FOCUS), this.inputTank.getFluid(), this.liquidLevel, this.intensity);
		if (rec != null) {

			if (rec.RFTick <= this.energy.getEnergyStored()) {

				this.currentOperation = new ReactionChamberOperation(rec, this);

				this.input.consume(1);
				this.totaltime = rec.ticks * ReactionChamberOperation.RECIPE_TICKRATE;
				this.progress = 0;

			} else {
				this.contentsChanged = true; //need check start next time
			}
		}
	}

	@Override
	protected void finishedOperation() {
		this.mergeOutput();
	}

	private void mergeOutput() {
		ArrayList<ItemStack> outputs = this.getCurrentReaction().getRecipe().outputs;

		for (int i = 0; i < outputs.size(); i++) {
			ItemStack output = outputs.get(i).copy();

			int leftover = output.getCount();
			int s = SLOT_OUTPUT;
			ItemStack out = ItemStack.EMPTY;
			while (leftover > 0 && s < SLOT_OUTPUT + OUTPUT_SLOTS_COUNT) {
				out = this.inventory.insertItemNoCheck(s, output, false);
				if (!out.isEmpty()) {
					leftover = out.getCount();
					if (leftover > 0) {
						output = out;
						s++;
					}
				} else {
					leftover = 0;
				}
			}

			if (leftover > 0) {
				this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.5d, this.worldPosition.getZ() + 0.5d, out));
			}

		}

		if (this.getCurrentReaction().getRecipe().liquidConsumtion > 0) {
			this.inputTank.removeInternal(this.getCurrentReaction().getRecipe().liquidConsumtion);
		}

	}

	@Override
	protected void playAmbientSound() {
	}

	protected void playReactionTickSound(boolean goodTick) {

		if (multiblockDirection == null) {
			return;
		}
		ItemStack focusitem = this.inventory.getStackInSlot(SLOT_FOCUS);
		BlockPos pos = this.worldPosition.relative(multiblockDirection);

		ReactionBeamFocus focus = ReactionBeamFocus.getBeamFocus(focusitem);
		if (focus != null) {
			level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), focus.getSound(), SoundSource.BLOCKS, 1.0F, 1.0F, true);
		}

		Random rng = new Random();
		for (int i = 0; i < 4; i++) {
			//1.12 EnumParticleTypes.SPELL
			level.addParticle(ParticleTypes.EFFECT, pos.getX() + rng.nextFloat(), pos.getY() + rng.nextFloat(), pos.getZ() + rng.nextFloat(), 0, 1, 0);
		}

		if (goodTick) {
			level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), TGSounds.REACTION_CHAMBER_BEEP, SoundSource.BLOCKS, 1.0F, 1.0F, true);
		} else {
			level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), TGSounds.REACTION_CHAMBER_WARNING, SoundSource.BLOCKS, 1.0F, 1.0F, true);
		}
	}

	@Override
	public AABB getBBforSlave(BlockPos slavePos) {
		if (multiblockDirection != null) {
			BlockPos center = this.getBlockPos().relative(multiblockDirection, 1);
			SlavePos sp = new SlavePos(slavePos, center);
			AABB bb = ReactionChamberDefinition.boundingBoxes.get(sp);
			if (bb != null) {
				return bb;
			}
		}
		return new AABB(0, 0, 0, 1, 1, 1);
	}

	public int getValidSlotForItemInMachine(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ITGSpecialSlot) {
			ITGSpecialSlot itm = (ITGSpecialSlot) stack.getItem();
			if (itm.getSlot(stack) == TGSlotType.REACTION_CHAMBER_FOCUS && ReactionBeamFocus.getBeamFocus(stack) != null) {
				return SLOT_FOCUS;
			}
		}
		return SLOT_INPUT;
	}

	@Override
	public boolean consumePower(int amount) {
		return super.consumePower(amount);
	}

	public byte getIntensity() {
		return intensity;
	}

	public byte getLiquidLevel() {
		return liquidLevel;
	}

	public void setIntensity(byte intensity) {
		this.intensity = intensity;
	}

	public void setLiquidLevel(byte liquidLevel) {
		this.liquidLevel = liquidLevel;
	}

	public ReactionChamberOperation getCurrentReaction() {
		if (this.currentOperation == null) {
			return null;
		}
		return (ReactionChamberOperation) this.currentOperation;
	}

	protected void explode(int type) {
		Direction dir = this.multiblockDirection;

		if (!this.level.isClientSide && dir != null) {

			Block fluidBlock = null;
			if (!this.inputTank.getFluid().isEmpty()) {
				BlockState fs = this.inputTank.getFluid().getFluid().defaultFluidState().createLegacyBlock();
				if (!fs.isAir()) {
					fluidBlock = fs.getBlock();
				}
			}

			BlockPos masterPos = this.getBlockPos().immutable();
			BlockPos centerPos = masterPos.relative(dir).above();

			this.onMultiBlockBreak();

			this.level.removeBlock(centerPos, false);

			ArrayList<BlockPos> blocksToRemove = new ArrayList<>();

			if (type == 0) {

				blocksToRemove.add(centerPos.relative(Direction.SOUTH));
				blocksToRemove.add(centerPos.relative(Direction.NORTH));
				blocksToRemove.add(centerPos.relative(Direction.WEST));
				blocksToRemove.add(centerPos.relative(Direction.EAST));

			} else if (type == 1) {
				BlockPos pos = masterPos;

				Direction left = dir.getClockWise();
				BlockPos pos1 = pos.relative(left).above();

				BlockPos pos2 = pos.relative(left.getOpposite()).relative(dir, 2).above(2);

				BlockPos.betweenClosed(pos1, pos2).forEach(p -> blocksToRemove.add(p.immutable()));
			}

			if (type == 0 && fluidBlock != null) {
				this.level.setBlock(centerPos, fluidBlock.defaultBlockState(), 3);
			} else if (type == 0) {
				this.level.setBlock(centerPos, Blocks.AIR.defaultBlockState(), 3);
			}

			blocksToRemove.forEach(b -> {
				this.level.setBlock(b, Blocks.AIR.defaultBlockState(), 3);
			});

			if (type == 0) {

				TGPackets.network.sendToAllAround(new PacketSpawnParticle("FragGrenadeExplosion", centerPos.getX() + 0.5d, centerPos.getY() + 0.5d, centerPos.getZ() + 0.5d), TGPackets.targetPoint(this.level, centerPos.getX() + 0.5d, centerPos.getY() + 0.5d, centerPos.getZ() + 0.5d, 50.0f));
				this.level.playSound((Player) null, centerPos.getX() + 0.5d, centerPos.getY() + 0.5d, centerPos.getZ() + 0.5d, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);

			} else if (type == 1) {
				this.level.explode(null, centerPos.getX() + 0.5d, centerPos.getY() + 0.5d, centerPos.getZ() + 0.5d, 4, Level.ExplosionInteraction.BLOCK);
			}

			if (type == 1 && fluidBlock != null) {
				this.level.setBlock(centerPos, fluidBlock.defaultBlockState(), 3);
				this.level.setBlock(centerPos.above(), Blocks.AIR.defaultBlockState(), 3);
				this.level.setBlock(centerPos.below(), Blocks.GRAVEL.defaultBlockState(), 3);
			}

			this.energy.setEnergyStored(0);
			this.inputTank.setFluid(FluidStack.EMPTY);

			ItemStack content = this.input.get();
			if (!content.isEmpty()) {
				this.inventory.setStackInSlot(SLOT_INPUT, ItemStack.EMPTY);
				this.level.addFreshEntity(new ItemEntity(this.level, centerPos.getX() + 0.5d, centerPos.getY() + 0.5d, centerPos.getZ() + 0.5d, content));
			}

			this.needUpdate();
		}
	}

	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if (id <= ButtonConstants.BUTTON_ID_REDSTONE) {
			super.buttonClicked(id, ply, data);
		} else {
			if (this.isUseableByPlayer(ply)) {
				switch (id) {
				case BUTTON_ID_LIQUIDLEVEL_INC:
					if (this.liquidLevel < 10) {
						this.liquidLevel++;
						this.contentsChanged = true;
					}
					break;
				case BUTTON_ID_LIQUIDLEVEL_DEC:
					if (this.liquidLevel > 0) {
						this.liquidLevel--;
						this.contentsChanged = true;
					}
					break;
				case BUTTON_ID_INTENSITY_INC:
					if (this.intensity < 10) {
						this.intensity++;
						this.contentsChanged = true;
					}
					break;
				case BUTTON_ID_INTENSTIY_DEC:
					if (this.intensity > 0) {
						this.intensity--;
						this.contentsChanged = true;
					}
					break;
				case BUTTON_ID_DUMPTANK: //drain input tank
					dumpLiquid();
					break;
				}
				this.setChanged();
			}
		}
	}

	private void dumpLiquid() {
		this.inputTank.setFluid(FluidStack.EMPTY);
		this.contentsChanged = true;
		this.needUpdate();
	}

	/**
	 * 1.20 addition: buckets/fluid containers fill (or drain) the input tank (1.12 needed pipes from other mods)
	 */
	@Override
	public boolean onFluidContainerInteract(Player player, InteractionHand hand, IFluidHandlerItem fluidhandleritem, ItemStack stack) {
		if (!this.isUseableByPlayer(player) || !this.isFormed()) return false;
		boolean interacted = FluidUtil.interactWithFluidHandler(player, hand, this.inputTank);
		if (interacted) {
			this.needUpdate();
		}
		return interacted;
	}

	/**
	 * Input tank: can only be filled up to the liquid level (in buckets), only the fluid above the liquid level can be drained
	 */
	public class ReactionChamberFluidTank extends FluidTankPlus {

		protected ReactionChamberTileEntMaster tile;

		public ReactionChamberFluidTank(ReactionChamberTileEntMaster tile, int capacity) {
			super(tile, capacity);
			this.tile = tile;
		}

		private int getFillCapacity() {
			return tile.liquidLevel * FluidType.BUCKET_VOLUME;
		}

		@Override
		protected void onContentsChanged() {
			super.onContentsChanged();
			tile.fluidsChanged = true;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return this.fillInternal(resource, action);
		}

		@Override
		public int fillInternal(FluidStack resource, FluidAction action) {
			int fillCapacity = getFillCapacity();
			if (resource.isEmpty() || !isFluidValid(resource)) {
				return 0;
			}

			if (action.simulate()) {
				if (fluid.isEmpty()) {
					return Math.max(0, Math.min(fillCapacity, resource.getAmount()));
				}

				if (!fluid.isFluidEqual(resource)) {
					return 0;
				}

				return Math.max(0, Math.min(fillCapacity - fluid.getAmount(), resource.getAmount()));
			}

			if (fluid.isEmpty()) {
				int amount = Math.min(fillCapacity, resource.getAmount());
				if (amount <= 0) {
					return 0;
				}
				fluid = new FluidStack(resource, amount);
				onContentsChanged();
				return fluid.getAmount();
			}

			if (!fluid.isFluidEqual(resource)) {
				return 0;
			}
			int filled = fillCapacity - fluid.getAmount();

			if (filled <= 0) {
				return 0;
			}

			if (resource.getAmount() < filled) {
				fluid.grow(resource.getAmount());
				filled = resource.getAmount();
			} else {
				fluid.setAmount(fillCapacity);
			}

			if (filled > 0) {
				onContentsChanged();
			}
			return filled;
		}

		/**
		 * remove passed amount from the tank
		 * @param amount
		 */
		void removeInternal(int amount) {
			if (!fluid.isEmpty()) {
				fluid.shrink(amount);
				if (fluid.getAmount() <= 0) {
					fluid = FluidStack.EMPTY;
				}
				onContentsChanged();
			}
		}

		@Override
		public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
			if (fluid.isEmpty()) {
				return FluidStack.EMPTY;
			}
			int maxDrainAmount = Math.max(fluid.getAmount() - tile.liquidLevel * FluidType.BUCKET_VOLUME, 0);
			int drainAmount = Math.min(maxDrainAmount, maxDrain);

			if (drainAmount <= 0) {
				return FluidStack.EMPTY;
			}

			int drained = drainAmount;
			if (fluid.getAmount() < drained) {
				drained = fluid.getAmount();
			}

			FluidStack stack = new FluidStack(fluid, drained);
			if (action.execute()) {
				fluid.shrink(drained);
				if (fluid.getAmount() <= 0) {
					fluid = FluidStack.EMPTY;
				}
				this.onContentsChanged();
			}
			return stack;
		}

	}

	@Override
	public void saveTanksToNBT(CompoundTag tags) {
		tags.put("inputTank", this.inputTank.writeToNBT(new CompoundTag()));
	}

	@Override
	public void loadTanksFromNBT(CompoundTag tags) {
		this.inputTank.setFluid(FluidStack.loadFluidStackFromNBT(tags.getCompound("inputTank")));
	}

	/**
	 * Sends the tank to the players tracking the chunk (1.12 PlayerChunkMapEntry players)
	 */
	public void needFluidUpdate() {
		if (this.level != null && !this.level.isClientSide) {
			TGPackets.network.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)), new PacketUpdateTileEntTanks(this, this.getBlockPos()));
		}
	}

}
