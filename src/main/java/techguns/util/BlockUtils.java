package techguns.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import techguns.items.guns.GenericGunMeleeCharge;
import techguns.items.guns.IGenericGunMelee;

/**
 * Port of the mining related parts of the 1.12 BlockUtils (the structure / world gen helpers are not part of the port)
 */
public class BlockUtils {

	protected static boolean checkMiningLevels(Level world, Player ply, BlockPos b, GenericGunMeleeCharge miningtool, ItemStack stack) {
		BlockState state = world.getBlockState(b);
		String tool = IGenericGunMelee.getHarvestTool(state);

		//1.12: canCollideCheck (not air / liquid) && canHarvestBlock
		if (!state.isAir() && !state.liquid() && state.canHarvestBlock(world, b, ply)) {
			if (tool == null) {
				return true;
			} else {
				return miningtool.canHarvestWithTool(stack, state);
			}
		}
		return false;
	}

	/**
	 * Blocks of the mining plane around center (radius 1 = 3x3), blocks with a block entity are skipped
	 */
	public static List<BlockPos> getBlockPlaneAroundAxisForMining(Level world, Player ply, BlockPos center, Direction.Axis axis, int radius, boolean includeCenter, @Nullable GenericGunMeleeCharge miningtool, ItemStack stack) {
		List<BlockPos> entries = new ArrayList<BlockPos>();

		Iterable<BlockPos> blocks;
		switch (axis) {
		case X:
			blocks = BlockPos.betweenClosed(center.offset(0, -radius, -radius), center.offset(0, radius, radius));
			break;
		case Y:
			blocks = BlockPos.betweenClosed(center.offset(-radius, 0, -radius), center.offset(radius, 0, radius));
			break;
		case Z:
		default:
			blocks = BlockPos.betweenClosed(center.offset(-radius, -radius, 0), center.offset(radius, radius, 0));
			break;
		}

		for (BlockPos pos : blocks) {
			BlockPos b = pos.immutable();
			if (includeCenter || !b.equals(center)) {
				if (world.getBlockEntity(b) == null) {
					if (miningtool == null || stack.isEmpty() || checkMiningLevels(world, ply, b, miningtool, stack)) {
						entries.add(b);
					}
				}
			}
		}
		return entries;
	}
}
