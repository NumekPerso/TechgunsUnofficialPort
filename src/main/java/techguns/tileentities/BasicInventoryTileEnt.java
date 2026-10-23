package techguns.tileentities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import techguns.tileentities.operation.ItemStackHandlerPlus;

/**
 * Block entity with an inventory (1.12 BasicInventoryTileEnt). Is the MenuProvider of its machine GUI: override createMenu.
 */
public class BasicInventoryTileEnt extends BasicTGTileEntity implements MenuProvider {

	protected ItemStackHandlerPlus inventory;

	protected boolean contentsChanged = true;

	/**
	 * Capability instances per side (index 6 = null side), created lazily so subclasses can replace the inventory in their constructor
	 */
	@SuppressWarnings("unchecked")
	protected LazyOptional<IItemHandler>[] itemHandlerCaps = new LazyOptional[7];

	public BasicInventoryTileEnt(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, boolean hasRotation) {
		super(type, pos, state, hasRotation);
		this.inventory = new ItemStackHandlerPlus(inventorySize) {

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setContentsChanged(true);
			}

		};
	}

	public ItemStackHandlerPlus getInventory() {
		return inventory;
	}

	/**
	 * Sets all inventory to null, called when wrenched, after inv is saved to nbt tags
	 */
	public void emptyContent() {
		for (int i = 0; i < inventory.getSlots(); ++i) {
			this.inventory.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	/**
	 * Helper method to check if ItemStack output can be put into slot
	 * @param output
	 * @param slot
	 * @return
	 */
	protected boolean canOutput(ItemStack output, int slot) {
		if (output.isEmpty()) {
			return true;
		}
		if (this.inventory.getStackInSlot(slot).isEmpty()) {
			return output.getCount() <= this.inventory.getSlotLimit(slot);
		} else {
			if (ItemStack.isSameItemSameTags(this.inventory.getStackInSlot(slot), output) && (this.inventory.getStackInSlot(slot).getCount() + output.getCount() <= this.inventory.getStackInSlot(slot).getMaxStackSize())) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * called when the block is broken
	 */
	public void onBlockBreak() {
		double x = worldPosition.getX() + 0.5d;
		double y = worldPosition.getY() + 0.5d;
		double z = worldPosition.getZ() + 0.5d;
		for (int i = 0; i < inventory.getSlots(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);
			if (!itemstack.isEmpty()) {
				Containers.dropItemStack(level, x, y, z, itemstack);
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		compound.put("inventory", inventory.serializeNBT());
		super.saveAdditional(compound);
	}

	@Override
	public void load(CompoundTag compound) {
		inventory.deserializeNBT(compound.getCompound("inventory"));
		super.load(compound);
	}

	public void writeNBTforDismantling(CompoundTag compound) {
		compound.put("inventory", inventory.serializeNBT());
		this.writeClientDataToNBT(compound);
	}

	public void readNBTfromStackTag(CompoundTag compound) {
		inventory.deserializeNBT(compound.getCompound("inventory"));
		this.readClientDataFromNBT(compound);
	}

	/**
	 * Called when a gui button is clicked, does nothing on default, override in subclasss
	 * @param id
	 * @param ply
	 * @param data
	 */
	public void buttonClicked(int id, Player ply, String data) {
	}

	/**
	 * The item handler exposed to automation on a side (null = internal / unknown side). Override for side dependent handlers.
	 */
	protected IItemHandler getItemHandlerForSide(@Nullable Direction side) {
		return this.inventory;
	}

	@Override
	public <T> @Nonnull LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER && !this.remove) {
			int index = side == null ? 6 : side.get3DDataValue();
			if (itemHandlerCaps[index] == null) {
				itemHandlerCaps[index] = LazyOptional.of(() -> this.getItemHandlerForSide(side));
			}
			return itemHandlerCaps[index].cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		for (int i = 0; i < itemHandlerCaps.length; i++) {
			if (itemHandlerCaps[i] != null) {
				itemHandlerCaps[i].invalidate();
				itemHandlerCaps[i] = null;
			}
		}
	}

	/**
	 * @param contentsChanged the contentsChanged to set
	 */
	public void setContentsChanged(boolean contentsChanged) {
		this.contentsChanged = contentsChanged;
		this.setChanged();
	}

	public boolean getContentsChanged() {
		return contentsChanged;
	}

	/**
	 * return true if a fluid interaction happened
	 */
	public boolean onFluidContainerInteract(Player player, InteractionHand hand, IFluidHandlerItem fluidhandleritem, ItemStack stack) {
		return false;
	}

	/**
	 * Container name, override in machines (1.12 getDisplayName)
	 */
	@Override
	public Component getDisplayName() {
		return this.getBlockState().getBlock().getName();
	}

	/**
	 * Creates the server side container of the machine GUI, null if this tile has no GUI
	 */
	@Override
	@Nullable
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return null;
	}
}
