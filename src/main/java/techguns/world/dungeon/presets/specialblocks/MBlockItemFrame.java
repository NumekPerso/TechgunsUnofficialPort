package techguns.world.dungeon.presets.specialblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import techguns.util.MBlock;

public class MBlockItemFrame extends MBlock {

	ItemStack itemToDisplay = ItemStack.EMPTY;

	Direction face = Direction.SOUTH;

	public MBlockItemFrame(ItemStack itemToDisplay, Direction facing) {
		super(Blocks.AIR.defaultBlockState());
		this.hasTileEntity = true;
		this.layer = 1;
		this.itemToDisplay = itemToDisplay;
		this.face = facing;
	}

	@Override
	protected int getPlacementFlags() {
		return 3;
	}

	@Override
	public void tileEntityPostPlacementAction(LevelAccessor w, BlockState state, BlockPos p, int rotation) {
		if (!w.isClientSide() && w instanceof net.minecraft.world.level.ServerLevelAccessor sla) {
			ServerLevel level = sla.getLevel();
			ItemFrame frame = new ItemFrame(level, p, rotate(face, rotation));
			if (!itemToDisplay.isEmpty()) {
				frame.setItem(itemToDisplay.copy(), false);
			}
			w.addFreshEntity(frame);
		}
	}

	private Direction rotate(Direction facing, int rotations) {
		for (int i = 0; i < rotations; i++) {
			facing = facing.getCounterClockWise();
		}
		return facing;
	}
}
