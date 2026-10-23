package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGMachines;
import techguns.Techguns;
import techguns.gui.containers.UpgradeBenchContainer;

/**
 * Armor (upgrade) bench, the block shape is EnumSimpleMachineType2.ARMOR_BENCH_SHAPE (1.12 BLOCK_BB)
 */
public class UpgradeBenchTileEnt extends BasicOwnedTileEnt {

	public UpgradeBenchTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.ARMOR_BENCH_TILE, pos, state, 0, false);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Techguns.MODID + ".container.armorbench");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new UpgradeBenchContainer(id, inv, this);
	}

}
