package techguns.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import techguns.items.GenericItem;
import techguns.tileentities.Door3x3TileEntity;

/**
 * Places a 3x3 door. 1.12 had one item with a metadata per door type (item_door3x3, item_door3x3_1...), now one item per type.
 */
public class ItemTGDoor3x3 extends GenericItem {

	protected final BlockTGDoor3x3 block;
	protected final EnumDoorType doorType;

	public ItemTGDoor3x3(String name, BlockTGDoor3x3 block, EnumDoorType type) {
		super(name);
		this.block = block;
		this.doorType = type;
	}

	public EnumDoorType getDoorType() {
		return doorType;
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Direction facing = context.getClickedFace();
		if (facing != Direction.UP) {
			return InteractionResult.FAIL;
		} else {
			Level worldIn = context.getLevel();
			BlockPos pos = context.getClickedPos();
			Player player = context.getPlayer();
			BlockState iblockstate = worldIn.getBlockState(pos);

			if (!iblockstate.canBeReplaced(new BlockPlaceContext(context))) {
				pos = pos.relative(facing);
			}

			ItemStack itemstack = context.getItemInHand();
			Direction enumfacing = player != null ? Direction.fromYRot(player.getYRot()) : Direction.NORTH;

			if ((player == null || player.mayUseItemAt(pos, facing, itemstack)) && this.block.canPlaceDoor(worldIn, pos, enumfacing)) {
				if (!worldIn.isClientSide) {
					placeDoor(worldIn, pos, enumfacing, player);
				}
				SoundType soundtype = this.block.defaultBlockState().getSoundType(worldIn, pos, player);
				worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				if (player == null || !player.getAbilities().instabuild) {
					itemstack.shrink(1);
				}
				return InteractionResult.sidedSuccess(worldIn.isClientSide);
			} else {
				return InteractionResult.FAIL;
			}
		}
	}

	public void placeDoor(Level worldIn, BlockPos pos, Direction facing, Player player) {
		boolean zplane = (facing == Direction.EAST || facing == Direction.WEST);

		BlockState slavestate = this.block.defaultBlockState().setValue(BlockTGDoor3x3.ZPLANE, zplane).setValue(BlockTGDoor3x3.MASTER, false)
				.setValue(BlockTGDoor3x3.TYPE, this.doorType);
		BlockState masterstate = slavestate.setValue(BlockTGDoor3x3.MASTER, true);

		worldIn.setBlock(pos, slavestate, 3);
		worldIn.setBlock(pos.above(), masterstate, 3);
		worldIn.setBlock(pos.above(2), slavestate, 3);

		if (zplane) {
			worldIn.setBlock(pos.north(), slavestate, 3);
			worldIn.setBlock(pos.north().above(), slavestate, 3);
			worldIn.setBlock(pos.north().above(2), slavestate, 3);

			worldIn.setBlock(pos.south(), slavestate, 3);
			worldIn.setBlock(pos.south().above(), slavestate, 3);
			worldIn.setBlock(pos.south().above(2), slavestate, 3);
		} else {
			worldIn.setBlock(pos.east(), slavestate, 3);
			worldIn.setBlock(pos.east().above(), slavestate, 3);
			worldIn.setBlock(pos.east().above(2), slavestate, 3);

			worldIn.setBlock(pos.west(), slavestate, 3);
			worldIn.setBlock(pos.west().above(), slavestate, 3);
			worldIn.setBlock(pos.west().above(2), slavestate, 3);
		}

		BlockEntity tile = worldIn.getBlockEntity(pos.above());
		if (tile instanceof Door3x3TileEntity door && player != null) {
			door.setOwner(player);
		}
	}
}
