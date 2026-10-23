package techguns.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

/**
 * 1.12 "basicore" block, flattened: one block per EnumOreType. Hardness and light level from the ore type,
 * harvest level through the needs_x_tool tags (see EnumOreType.getMininglevel()).
 */
public class BlockTGOre extends GenericBlock {

	protected final EnumOreType oreType;

	public BlockTGOre(EnumOreType type) {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE)
				.requiresCorrectToolForDrops().strength(type.getHardness()).lightLevel(s -> type.getLightlevel()));
		this.oreType = type;
	}

	public EnumOreType getOreType() {
		return oreType;
	}
}
