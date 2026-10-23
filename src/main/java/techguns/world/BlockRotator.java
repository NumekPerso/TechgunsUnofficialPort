package techguns.world;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import techguns.blocks.BlockTGDoor3x3;

/**
 * Rotates block states with the structure (1.12: "times" counter clockwise steps).
 * 1.20: BlockState.rotate (vanilla blocks rotate facings, axes, stairs, rails, ...), blocks without rotate support
 * get their "facing" property rotated, the 3x3 door its ZPLANE.
 */
public class BlockRotator {

	public static BlockState getRotatedHorizontal(BlockState state, int times) {
		times = ((times % 4) + 4) % 4;
		if (times == 0) {
			return state;
		}
		Rotation rot = times == 1 ? Rotation.COUNTERCLOCKWISE_90 : (times == 2 ? Rotation.CLOCKWISE_180 : Rotation.CLOCKWISE_90);

		if (state.hasProperty(BlockTGDoor3x3.ZPLANE)) {
			return rotateBooleanPlane(state, times);
		}

		BlockState rotated = state.rotate(rot);
		if (rotated != state) {
			return rotated;
		}
		for (Property<?> p : state.getProperties()) {
			if (p instanceof DirectionProperty dp && p.getName().equals("facing")) {
				Direction facing = state.getValue(dp);
				if (facing.getAxis().isHorizontal()) {
					Direction target = rot.rotate(facing);
					if (dp.getPossibleValues().contains(target)) {
						return state.setValue(dp, target);
					}
				}
				return state;
			}
		}
		if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS) && (times % 2) == 1) {
			Direction.Axis a = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
			return state.setValue(BlockStateProperties.HORIZONTAL_AXIS, a == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
		}
		return state;
	}

	public static BlockState getWithFacing(BlockState state, Direction target) {
		return LegacyBlocks.setFacing(state, target);
	}

	protected static BlockState rotateBooleanPlane(BlockState state, int times) {
		boolean plane = state.getValue(BlockTGDoor3x3.ZPLANE);
		if ((times % 2) == 0) {
			return state;
		} else {
			return state.setValue(BlockTGDoor3x3.ZPLANE, !plane);
		}
	}
}
