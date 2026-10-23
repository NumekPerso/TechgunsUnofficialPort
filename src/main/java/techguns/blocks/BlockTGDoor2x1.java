package techguns.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import techguns.TGSounds;

/**
 * Bunker door: iron door that opens by hand (and the door next to it, as a double door), placed by ItemTGDoor2x1 (item_bunkerdoor)
 */
public class BlockTGDoor2x1 extends DoorBlock implements IGenericBlock {

	public BlockTGDoor2x1() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(8.0F).noOcclusion()
				.pushReaction(PushReaction.BLOCK), BlockSetType.IRON);
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
		BlockPos blockpos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
		BlockState iblockstate = pos.equals(blockpos) ? state : worldIn.getBlockState(blockpos);

		if (iblockstate.getBlock() != this) {
			return InteractionResult.PASS;
		} else {
			state = iblockstate.cycle(OPEN);

			//check if double door
			Direction doorfacing = iblockstate.getValue(FACING);
			Direction offsetfacing;
			if (iblockstate.getValue(HINGE) == DoorHingeSide.LEFT) {
				offsetfacing = doorfacing.getClockWise();
			} else {
				offsetfacing = doorfacing.getCounterClockWise();
			}
			BlockPos offsetPos = blockpos.relative(offsetfacing);
			BlockState offsetState = worldIn.getBlockState(offsetPos);
			if (offsetState.getBlock() == iblockstate.getBlock() && offsetState.getValue(HALF) == DoubleBlockHalf.LOWER && offsetState.getValue(OPEN) == iblockstate.getValue(OPEN)) {
				worldIn.setBlock(offsetPos, offsetState.setValue(OPEN, state.getValue(OPEN)), 10);
			}

			worldIn.setBlock(blockpos, state, 10);
			worldIn.playSound(playerIn, pos, TGSounds.BUNKER_DOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
			worldIn.gameEvent(playerIn, state.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
			return InteractionResult.sidedSuccess(worldIn.isClientSide);
		}
	}

	/**
	 * Placed by item_bunkerdoor
	 */
	@Override
	public BlockItem createItemBlock(String name) {
		return null;
	}
}
