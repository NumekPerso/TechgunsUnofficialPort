package techguns.blocks.machines;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.player.Player;
import techguns.TGMachines;
import techguns.blocks.ItemBlockOreDrill;

/**
 * One block per ore drill part (1.12 "oredrill" block with MACHINE_TYPE). SCAFFOLD_HIDDEN replaces the scaffold blocks
 * of the last row of a formed drill (around the rod), it drops a scaffold.
 */
public class BlockOreDrill extends MultiBlockMachine<EnumOreDrillType> {

	public BlockOreDrill(EnumOreDrillType machineType, Supplier<? extends BlockEntityType<?>> tileType) {
		super(machineType, tileType);
	}

	/**
	 * 1.12 shouldSideBeRendered: formed scaffolds don't render faces between each other
	 */
	@Override
	public boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
		if (this.machineType == EnumOreDrillType.SCAFFOLD && state.getValue(FORMED) && adjacent == state) {
			return true;
		}
		return super.skipRendering(state, adjacent, side);
	}

	@Override
	public BlockItem createItemBlock() {
		if (this.machineType == EnumOreDrillType.SCAFFOLD_HIDDEN) {
			return null;
		}
		return new ItemBlockOreDrill(this);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		if (this.machineType == EnumOreDrillType.SCAFFOLD_HIDDEN) {
			return new ItemStack(TGMachines.OREDRILL_SCAFFOLD);
		}
		return super.getCloneItemStack(state, target, level, pos, player);
	}
}
