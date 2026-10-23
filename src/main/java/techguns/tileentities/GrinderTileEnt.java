package techguns.tileentities;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGItems;
import techguns.TGMachines;
import techguns.TGSounds;
import techguns.gui.containers.GrinderContainer;
import techguns.items.guns.GenericGun;
import techguns.tileentities.operation.GrinderRecipes;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.tileentities.operation.MachineOperation;
import techguns.tileentities.operation.MachineOperationChance;
import techguns.tileentities.operation.MachineSlotItem;

public class GrinderTileEnt extends BasicMachineTileEnt {

	public static final int SLOT_INPUT = 0;
	public static final int SLOT_UPGRADE = 1;
	public static final int SLOT_OUTPUT0 = 2;
	public static final int SLOT_OUTPUT_LAST = SLOT_OUTPUT0 + 9;

	public MachineSlotItem input;

	public static final int POWER_PER_TICK = 5;

	public GrinderTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.GRINDER_TILE, pos, state, 11, false, 20000);

		input = new MachineSlotItem(this, SLOT_INPUT);

		this.inventory = new ItemStackHandlerPlus(11) {

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setContentsChanged(true);
			}

			@Override
			protected boolean allowItemInSlot(int slot, ItemStack stack) {
				switch (slot) {
				case SLOT_INPUT:
					return true;
				case SLOT_UPGRADE:
					return isMachineUpgrade(stack);
				}
				return false;
			}

			@Override
			protected boolean allowExtractFromSlot(int slot, int amount) {
				return slot >= SLOT_OUTPUT0 && slot <= SLOT_OUTPUT_LAST;
			}
		};
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("techguns.container.grinder");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new GrinderContainer(id, inv, this);
	}

	@Override
	protected int getNeededPower() {
		return POWER_PER_TICK;
	}

	public int getValidSlotForItemInMachine(ItemStack item) {
		if (isMachineUpgrade(item)) {
			return SLOT_UPGRADE;
		} else if (GrinderRecipes.hasRecipeForInput(item)) {
			return SLOT_INPUT;
		}
		return -1;
	}

	@Override
	protected void checkAndStartOperation() {
		this.setContentsChanged(false);

		MachineOperation op = GrinderRecipes.getOperationForInput(this.input.get(), this);

		if (op instanceof MachineOperationChance) {
			((MachineOperationChance) op).setTile(this);
		}

		if (op != null && canOutput(op)) {

			//check multiplier
			int maxStack = this.getMaxMachineUpgradeMultiplier(SLOT_UPGRADE);

			int multiplier = 1;
			//try higher stacksize
			int i;
			for (i = maxStack; i > 1; --i) {
				op.setStackMultiplier(i);
				if (canConsume(op) && this.canOutput(op)) {
					multiplier = i;
					break;
				}
			}

			op.setStackMultiplier(multiplier);

			this.input.consume(op.getNeededAmountItem(SLOT_INPUT));

			this.currentOperation = op;
			this.progress = 0;
			this.totaltime = op.getTime();

			if (!this.level.isClientSide) {
				this.needUpdate();
			}
		}
	}

	protected boolean canOutput(MachineOperation output) {
		return this.inventory.canInsertAll(SLOT_OUTPUT0, SLOT_OUTPUT_LAST, output.getOutputsWithMult());
	}

	protected boolean canConsume(MachineOperation output) {
		int multi = output.getStackMultiplier();
		ItemStack in1 = output.getInputs().get(0);

		return this.input.canConsumeWithMultiplier(in1, multi);
	}

	@Override
	protected void finishedOperation() {
		List<ItemStack> list = this.currentOperation.getOutputsWithMult();
		List<ItemStack> leftover = this.inventory.mergeAll(SLOT_OUTPUT0, SLOT_OUTPUT_LAST, list);

		//should not happen cause canOutput is checked
		if (!this.level.isClientSide) {
			leftover.forEach(s -> {
				if (!s.isEmpty()) {
					this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.5d, this.worldPosition.getZ() + 0.5d, s));
				}
			});
		}
	}

	@Override
	protected void playAmbientSound() {
		int sWork1 = Math.round((float) this.totaltime * 0.2f) + 1;
		int sWork2 = (Math.round((float) totaltime * 0.6f)) - 1;

		if (this.level.isClientSide && this.progress % 2 == 0) {
			//particle
			ItemStack it = this.currentOperation.getItemInputI(0);
			float f = 0.1f;
			float f1 = 0.05f;
			ItemStack particleItem = it.getItem() instanceof GenericGun ? new ItemStack(TGItems.PLATE_STEEL) : it;
			if (!particleItem.isEmpty()) {
				this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, particleItem), this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.6d, this.worldPosition.getZ() + 0.5d, Math.random() * f - f1, Math.random() * f + f, Math.random() * f - f1);
			}
		}
		if (this.progress == 1) {
			level.playLocalSound(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), TGSounds.GRINDER_START, SoundSource.BLOCKS, 1.0f, 1.0F, true);
		} else if (this.progress == sWork1 || this.progress == sWork2) {
			level.playLocalSound(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), TGSounds.GRINDER_WORK, SoundSource.BLOCKS, 1.0f, 1.0F, true);
		}
	}

}
