package techguns.blocks.machines.multiblocks;

import java.util.HashMap;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import techguns.tileentities.MultiBlockMachineTileEntMaster;

/**
 * Multiblock definitions per master tile entity class. 1.12 registered FabricatorDefinition, ReactionChamberDefinition and
 * OreDrillDefinition here: register them with MultiBlockRegister.register when these machines are ported.
 */
public class MultiBlockRegister {
	public static HashMap<Class<? extends BlockEntity>, MultiBlockMachineSchematic> REGISTER = new HashMap<>();

	public static void register(Class<? extends MultiBlockMachineTileEntMaster> masterClass, MultiBlockMachineSchematic schematic) {
		REGISTER.put(masterClass, schematic);
	}

	public static boolean canFormFromSide(BlockEntity tile, Direction sideHit) {
		MultiBlockMachineSchematic multiblock = REGISTER.get(tile.getClass());
		if (multiblock != null) {
			return multiblock.canFormFromSide(sideHit);
		}
		return false;
	}

	public static boolean canForm(BlockEntity tile, Player ply, Direction sideHit) {

		MultiBlockMachineSchematic multiblock = REGISTER.get(tile.getClass());
		if (multiblock != null) {
			return multiblock.checkForm(tile.getLevel(), ply, tile.getBlockPos(), sideHit);
		}

		return false;
	}

	public static boolean form(BlockEntity tile, Player ply, Direction sideHit) {
		MultiBlockMachineSchematic multiblock = REGISTER.get(tile.getClass());
		if (multiblock != null) {
			return multiblock.form(tile.getLevel(), ply, tile.getBlockPos(), sideHit);
		}
		return false;
	}
}
