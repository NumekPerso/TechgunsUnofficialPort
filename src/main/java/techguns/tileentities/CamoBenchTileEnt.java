package techguns.tileentities;

import static techguns.gui.ButtonConstants.BUTTON_ID_SECURITY;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGMachines;
import techguns.Techguns;
import techguns.capabilities.TGExtendedPlayer;
import techguns.gui.containers.CamoBenchContainer;
import techguns.gui.player.TGPlayerInventory;
import techguns.items.armors.ICamoChangeable;
import techguns.tileentities.operation.CamoBenchRecipes;
import techguns.tileentities.operation.CamoBenchRecipes.CamoBenchRecipe;

public class CamoBenchTileEnt extends BasicOwnedTileEnt {

	public CamoBenchTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.CAMO_BENCH_TILE, pos, state, 1, false);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Techguns.MODID + ".container.camobench");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new CamoBenchContainer(id, inv, this);
	}

	/**
	 * Returns current ItemStack if it has changeable camo or EMPTY
	 * @return
	 */
	public ItemStack getItem() {
		ItemStack stack = this.inventory.getStackInSlot(0);
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof ICamoChangeable) {
				return stack;
			} else {
				CamoBenchRecipe r = CamoBenchRecipes.getRecipeFor(stack.getItem());
				if (r != null) {
					return stack;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	/**
	 * The armor buttons are pairs (+/-) from head to feet, then back and face slot (1.12 button ids)
	 */
	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if (this.isUseableByPlayer(ply)) {

			if (id <= BUTTON_ID_SECURITY) {
				super.buttonClicked(id, ply, data);
			} else {

				int odd_even = BUTTON_ID_SECURITY % 2;

				if (id < BUTTON_ID_SECURITY + 3) {
					ItemStack item = this.getItem();
					if (!item.isEmpty() && item.getItem() instanceof ICamoChangeable) {
						ICamoChangeable camoitem = (ICamoChangeable) item.getItem();
						camoitem.switchCamo(item, id == BUTTON_ID_SECURITY + 2);
						this.inventory.setStackInSlot(0, item);
						this.needUpdate();
					} else if (!item.isEmpty()) {
						CamoBenchRecipe r = CamoBenchRecipes.getRecipeFor(item.getItem());
						if (r != null) {
							this.inventory.setStackInSlot(0, r.switchCamo(item, id == BUTTON_ID_SECURITY + 2));
							this.needUpdate();
						}
					}
				} else if (id < BUTTON_ID_SECURITY + 11) {

					int slotid = 3 - (((int) (Math.ceil((id - (BUTTON_ID_SECURITY + 2)) * 0.5))) - 1);

					//armor slots are part of the camo bench container, the change is synced by the container
					ItemStack item = ply.getInventory().armor.get(slotid);
					if (!item.isEmpty() && item.getItem() instanceof ICamoChangeable) {
						boolean back = id % 2 == odd_even;

						ICamoChangeable camoitem = (ICamoChangeable) item.getItem();
						camoitem.switchCamo(item, back);
					}

				} else if (id < BUTTON_ID_SECURITY + 13) {
					boolean back = id % 2 == odd_even;
					TGExtendedPlayer props = TGExtendedPlayer.get(ply);
					if (props != null) {
						ItemStack item = props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_BACK);
						if (!item.isEmpty() && item.getItem() instanceof ICamoChangeable) {
							ICamoChangeable camoitem = (ICamoChangeable) item.getItem();
							camoitem.switchCamo(item, back);
						}
					}
				} else if (id < BUTTON_ID_SECURITY + 15) {
					boolean back = id % 2 == odd_even;
					TGExtendedPlayer props = TGExtendedPlayer.get(ply);
					if (props != null) {
						ItemStack item = props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_FACE);
						if (!item.isEmpty() && item.getItem() instanceof ICamoChangeable) {
							ICamoChangeable camoitem = (ICamoChangeable) item.getItem();
							camoitem.switchCamo(item, back);
						}
					}
				}
			}
		}
	}

}
