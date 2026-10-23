package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGMachines;
import techguns.Techguns;
import techguns.blocks.machines.SimpleMachine;
import techguns.gui.containers.BlastFurnaceContainer;
import techguns.tileentities.operation.BlastFurnaceRecipes;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.tileentities.operation.MachineOperation;
import techguns.tileentities.operation.MachineSlotItem;

public class BlastFurnaceTileEnt extends BasicMachineTileEnt {

	public static final int SLOT_INPUT1 = 0;
	public static final int SLOT_INPUT2 = 1;
	public static final int SLOT_OUTPUT = 2;
	public static final int SLOT_UPGRADE = 3;

	public MachineSlotItem input1;
	public MachineSlotItem input2;

	public BlastFurnaceTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.BLAST_FURNACE_TILE, pos, state, 4, false, 40000);

		input1 = new MachineSlotItem(this, SLOT_INPUT1);
		input2 = new MachineSlotItem(this, SLOT_INPUT2);

		this.inventory = new ItemStackHandlerPlus(4) {

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setContentsChanged(true);
			}

			@Override
			protected boolean allowItemInSlot(int slot, ItemStack stack) {
				switch (slot) {
				case SLOT_INPUT1:
				case SLOT_INPUT2:
					return BlastFurnaceTileEnt.this.isItemValidForSlot(slot, stack); //qualified: ItemStackHandlerPlus has its own isItemValidForSlot that calls allowItemInSlot
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
		return Component.translatable(Techguns.MODID + ".container.blast_furnace");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new BlastFurnaceContainer(id, inv, this);
	}

	protected boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (slot == SLOT_INPUT1) {
			return BlastFurnaceRecipes.hasRecipeWithInputForSlot(stack, 0);
		} else if (slot == SLOT_INPUT2) {
			return BlastFurnaceRecipes.hasRecipeWithInputForSlot(stack, 1);
		}
		return false;
	}

	public int getValidSlotForItemInMachine(ItemStack stack) {
		if (BlastFurnaceRecipes.hasRecipeWithInputForSlot(stack, 0)) {
			return SLOT_INPUT1;
		}
		if (BlastFurnaceRecipes.hasRecipeWithInputForSlot(stack, 1)) {
			return SLOT_INPUT2;
		}
		if (isMachineUpgrade(stack)) {
			return SLOT_UPGRADE;
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

		MachineOperation op = BlastFurnaceRecipes.getOutputFor(this);

		this.setContentsChanged(false);

		if (op != null && canOutput(op)) {

			//check multiplier
			int maxStack = this.getMaxMachineUpgradeMultiplier(SLOT_UPGRADE);

			int multiplier = 1;
			//try higher stacksize
			int i;
			for (i = maxStack; i > 1; --i) {
				op.setStackMultiplier(i);
				if (this.canOutput(op) && canConsume(op)) {
					multiplier = i;
					break;
				}
			}

			op.setStackMultiplier(multiplier);

			this.input1.consume(op.getNeededAmountItem(SLOT_INPUT1));
			this.input2.consume(op.getNeededAmountItem(SLOT_INPUT2));

			this.currentOperation = op;
			this.progress = 0;
			this.totaltime = op.getTime();

			if (!this.level.isClientSide) {
				this.needUpdate();
			}
		}
	}

	protected boolean canOutput(MachineOperation output) {
		return this.canOutput(output.getItemOutput0(), SLOT_OUTPUT);
	}

	protected boolean canConsume(MachineOperation output) {
		int multi = output.getStackMultiplier();
		ItemStack in1 = output.getInputs().get(0);
		ItemStack in2 = output.getInputs().get(1);

		return this.input1.canConsumeWithMultiplier(in1, multi) && this.input2.canConsumeWithMultiplier(in2, multi);
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
			BlockPos pos = this.worldPosition;
			if (this.progress % 35 == 0) {
				level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.75f, 1f, true);
			}
			if (this.progress % 20 == 0) {
				float p = this.level.random.nextFloat() * 0.15f;
				level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.75f, 0.5f + p, true);

				BlockState state = this.getBlockState();
				if (!state.hasProperty(SimpleMachine.FACING)) {
					return;
				}
				Direction enumfacing = state.getValue(SimpleMachine.FACING);
				double d0 = (double) pos.getX() + 0.5D;
				double d1 = (double) pos.getY() + this.level.random.nextDouble() * 6.0D / 16.0D + 3.0D / 16.0D;
				double d2 = (double) pos.getZ() + 0.5D;
				double d4 = this.level.random.nextDouble() * 0.6D - 0.3D;

				switch (enumfacing) {
				case WEST:
					level.addParticle(ParticleTypes.SMOKE, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
					level.addParticle(ParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
					break;
				case EAST:
					level.addParticle(ParticleTypes.SMOKE, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
					level.addParticle(ParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
					break;
				case NORTH:
					level.addParticle(ParticleTypes.SMOKE, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
					level.addParticle(ParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
					break;
				case SOUTH:
					level.addParticle(ParticleTypes.SMOKE, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
					level.addParticle(ParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
					break;
				default:
					break;
				}
			}
		}
	}

}
