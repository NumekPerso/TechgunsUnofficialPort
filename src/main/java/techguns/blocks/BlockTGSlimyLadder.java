package techguns.blocks;

import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

/**
 * Slimy trail of the bug nests, a vanilla ladder with slime sounds (in the minecraft:climbable tag)
 */
public class BlockTGSlimyLadder extends LadderBlock implements IGenericBlock {

	public BlockTGSlimyLadder() {
		super(BlockBehaviour.Properties.of().forceSolidOff().strength(0.4F).sound(SoundType.SLIME_BLOCK).noOcclusion().pushReaction(PushReaction.DESTROY));
	}
}
