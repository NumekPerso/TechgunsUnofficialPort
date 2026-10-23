package techguns.gui.containers;

import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import techguns.TGConfig;
import techguns.TGMachines;
import techguns.gui.player.SlotArmor;
import techguns.items.armors.GenericArmor;
import techguns.tileentities.UpgradeBenchTileEnt;
import techguns.tileentities.operation.UpgradeBenchRecipes;
import techguns.tileentities.operation.UpgradeBenchRecipes.UpgradeBenchRecipe;

/**
 * Upgrade (armor) bench: anvil like container, armor + upgrade item -> enchanted armor for XP
 */
public class UpgradeBenchContainer extends OwnedTileContainer {
	protected final UpgradeBenchTileEnt upgradeBench;

	/** Here comes out item you merged and/or renamed. */
	public final ResultContainer outputSlot;
	/** The 2slots where you put your items in that you want to merge and/or rename. */
	protected final SimpleContainer inputSlots;

	protected int xp_needed = 0;

	public static final int SLOT_Y = 47;
	public static final int SLOT_INPUT_X = 17;
	public static final int SLOT_UPGRADE_X = 53;
	public static final int SLOT_OUTPUT_X = 116;

	protected static final int HIGHEST_SLOT = 2;

	public UpgradeBenchContainer(int id, Inventory player, FriendlyByteBuf buf) {
		this(id, player, TGBaseContainer.<UpgradeBenchTileEnt>getClientTile(player, buf));
	}

	public UpgradeBenchContainer(int id, Inventory player, UpgradeBenchTileEnt ent) {
		super(TGMachines.ARMOR_BENCH_CONTAINER, id, player, ent);
		this.upgradeBench = ent;

		this.outputSlot = new ResultContainer();
		this.inputSlots = new SimpleContainer(2) {
			@Override
			public void setChanged() {
				super.setChanged();
				UpgradeBenchContainer.this.slotsChanged(this);
			}
		};

		this.addSlot(new SlotInput(this.inputSlots, 0, SLOT_INPUT_X, SLOT_Y));
		this.addSlot(new SlotInput(this.inputSlots, 1, SLOT_UPGRADE_X, SLOT_Y));
		this.addSlot(new Slot(this.outputSlot, 2, SLOT_OUTPUT_X, SLOT_Y) {

			@Override
			public boolean mayPlace(ItemStack stack) {
				//No inputing in output slot
				return false;
			}

			@Override
			public void onTake(Player thePlayer, ItemStack stack) {

				//remove inputs
				UpgradeBenchContainer.this.inputSlots.setItem(0, ItemStack.EMPTY);
				UpgradeBenchContainer.this.inputSlots.removeItem(1, 1);

				thePlayer.level().playSound(thePlayer, thePlayer.getX(), thePlayer.getY(), thePlayer.getZ(), SoundEvents.ARMOR_EQUIP_DIAMOND, SoundSource.PLAYERS, 1f, 1f);

				if (!thePlayer.getAbilities().instabuild) {
					thePlayer.giveExperiencePoints(-xp_needed);
				}
				UpgradeBenchContainer.this.xp_needed = 0;

				super.onTake(thePlayer, stack);
			}

			@Override
			public boolean mayPickup(Player playerIn) {
				if (this.hasItem()) {
					return playerIn.getAbilities().instabuild || playerIn.totalExperience >= UpgradeBenchContainer.this.xp_needed;
				} else {
					return false;
				}
			}

		});

		this.addPlayerInventorySlots(player);

		for (int i = 0; i < 4; ++i) {
			this.addSlot(new SlotArmor(player, 39 - i, 17 + (i * 18), 18, i, player.player));
		}

		this.addSyncedInt(() -> this.xp_needed, v -> this.xp_needed = v);
	}

	public UpgradeBenchTileEnt getUpgradeBench() {
		return upgradeBench;
	}

	public class SlotInput extends Slot {

		public SlotInput(Container inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public void onTake(Player thePlayer, ItemStack stack) {
			UpgradeBenchContainer.this.xp_needed = UpgradeBenchContainer.this.calculateXPCost();
			super.onTake(thePlayer, stack);
		}

	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		this.clearContainer(playerIn, this.inputSlots);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void slotsChanged(Container inventoryIn) {
		super.slotsChanged(inventoryIn);

		if (inventoryIn == this.inputSlots) {
			this.updateOutput();
		}
	}

	/**
	 * called when the Input Slot changes, calculates the new result and puts it in the output slot
	 */
	public void updateOutput() {

		UpgradeBenchRecipe r = UpgradeBenchRecipes.getUpgradeRecipeFor(this.inputSlots.getItem(0), this.inputSlots.getItem(1));

		if (r != null) {
			ItemStack output = this.inputSlots.getItem(0).copy();
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(this.inputSlots.getItem(0));

			this.xp_needed = this.calculateXPCost(r);

			if (map.containsKey(r.getEnch())) {
				Integer level = map.get(r.getEnch());

				if (r.getLevel() > level) {
					map.put(r.getEnch(), r.getLevel());
					EnchantmentHelper.setEnchantments(map, output);
					this.outputSlot.setItem(0, output);
				} else {
					this.outputSlot.setItem(0, ItemStack.EMPTY);
				}

			} else {
				map.put(r.getEnch(), r.getLevel());
				EnchantmentHelper.setEnchantments(map, output);
				this.outputSlot.setItem(0, output);
			}

		} else {
			this.outputSlot.setItem(0, ItemStack.EMPTY);
		}
		this.broadcastChanges();
	}

	public int getXp_needed() {
		return xp_needed;
	}

	public int calculateXPCost() {
		UpgradeBenchRecipe r = UpgradeBenchRecipes.getUpgradeRecipeFor(this.inputSlots.getItem(0), this.inputSlots.getItem(1));
		if (r != null) {
			return calculateXPCost(r);
		} else {
			return 0;
		}
	}

	public int calculateXPCost(UpgradeBenchRecipe r) {
		Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(this.inputSlots.getItem(0));

		if (map.containsKey(r.getEnch())) {
			Integer level = map.get(r.getEnch());

			if (r.getLevel() > level) {
				int dif = r.getLevel() - level;
				return TGConfig.upgrade_xp_cost * dif;
			} else {
				return 0;
			}

		} else {
			return TGConfig.upgrade_xp_cost * r.getLevel();
		}
	}

	@Override
	public ItemStack quickMoveStack(Player ply, int id) {
		int MAXSLOTS = this.slots.size();

		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(id);

		if (slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			if (!stack.isEmpty()) {

				if (id >= 0 && id <= HIGHEST_SLOT) {
					if (id == HIGHEST_SLOT && !slot.mayPickup(ply)) {
						return ItemStack.EMPTY;
					}
					if (!this.moveItemStackTo(stack1, HIGHEST_SLOT + 1, MAXSLOTS, false)) {
						return ItemStack.EMPTY;
					}
					slot.onQuickCraft(stack1, stack);
				} else if (id > HIGHEST_SLOT && id < MAXSLOTS) {

					int validslot = this.getValidSlotForItemInInventory(stack1);
					if (validslot >= 0) {

						if (!this.moveItemStackTo(stack1, validslot, validslot + 1, false)) {
							return ItemStack.EMPTY;
						}
						slot.onQuickCraft(stack1, stack);

					} else {
						return ItemStack.EMPTY;
					}

				}

				if (stack1.getCount() == 0) {
					slot.set(ItemStack.EMPTY);
				} else {
					slot.setChanged();
				}

				if (stack1.getCount() == stack.getCount()) {
					return ItemStack.EMPTY;
				}

				slot.onTake(ply, stack1);
			}
		}

		return stack;
	}

	protected int getValidSlotForItemInInventory(ItemStack stack1) {
		if (!stack1.isEmpty() && stack1.getItem() instanceof GenericArmor) {
			return 0;
		} else if (UpgradeBenchRecipes.getUpgradeRecipeForUpgradeItem(stack1) != null) {
			return 1;
		}
		return -1;
	}

}
