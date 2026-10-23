package techguns.gui.player;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The additional Techguns player slots (face, back, hand, auto food, heal, ammo).
 * The 1.12 version also wrote the face/back/hand slots into synced entity data parameters, the port syncs them with
 * PacketTGExtendedPlayerSync (see AdditionalSlotEventHandler).
 */
public class TGPlayerInventory implements Container {
	private static final String name = "TechgunsPlayerInventory";

	public static final int NUMSLOTS = 15;

	public static final int SLOT_FACE = 0;
	public static final int SLOT_BACK = 1;
	public static final int SLOT_HAND = 2;
	public static final int SLOTS_AUTOFOOD_START = 3;
	public static final int SLOTS_AUTOFOOD_END = 5;
	public static final int SLOT_AUTOHEAL = 6;
	public static final int SLOTS_AMMO_START = 7;
	public static final int SLOTS_AMMO_END = 14;

	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(NUMSLOTS, ItemStack.EMPTY);

	public boolean dirty = false;
	public Player player;

	public TGPlayerInventory(Player player) {
		this.player = player;
	}

	public void saveNBTData(CompoundTag tags) {
		ContainerHelper.saveAllItems(tags, this.inventory);
	}

	public void loadNBTData(CompoundTag tags) {
		this.inventory.clear();
		ContainerHelper.loadAllItems(tags, this.inventory);
	}

	@Override
	public int getContainerSize() {
		return NUMSLOTS;
	}

	@Override
	public ItemStack getItem(int slotid) {
		return slotid >= 0 && slotid < this.inventory.size() ? this.inventory.get(slotid) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack = ContainerHelper.removeItem(this.inventory, index, count);
		if (!stack.isEmpty()) {
			this.setChanged();
		}
		return stack;
	}

	@Override
	public void setItem(int slotid, ItemStack itemstack) {
		this.inventory.set(slotid, itemstack);
		this.setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void setChanged() {
		this.dirty = true;
	}

	@Override
	public boolean canPlaceItem(int slotid, ItemStack itemstack) {
		return true;
	}

	public String getName() {
		return name;
	}

	public Component getDisplayName() {
		return Component.translatable("techguns.extendedinventory");
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack1 : this.inventory) {
			if (!itemstack1.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.inventory, index);
	}

	@Override
	public void clearContent() {
		this.inventory.clear();
	}

	@Override
	public boolean stillValid(Player player) {
		if (!this.player.isAlive()) {
			return false;
		} else {
			return player.distanceToSqr(this.player) <= 64.0D;
		}
	}

}
