package techguns.util;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGBlocks;
import techguns.blocks.BlockTGCamoNet;
import techguns.blocks.EnumCamoNetType;
import techguns.world.BlockRotator;
import techguns.world.EnumLootType;
import techguns.world.structures.WorldgenStructure.BiomeColorType;

public class MBlockCamoNetTop extends MBlock {

	public MBlockCamoNetTop() {
		super(TGBlocks.CAMONET_TOP.defaultBlockState());
	}

	@Override
	public void setBlock(LevelAccessor w, MutableBlockPos pos, int rotation, EnumLootType loottype, BiomeColorType biome) {
		if (pos.getY() > w.getMinBuildHeight()) {
			BlockState targetState = BlockRotator.getRotatedHorizontal(getCamoTypeState(biome), rotation);
			placeState(w, pos, targetState, getPlacementFlags());
			if (this.hasTileEntity) {
				this.tileEntityPostPlacementAction(w, targetState, pos, rotation);
			}
		}
	}

	public BlockState getCamoTypeState(BiomeColorType camo) {
		BlockState s = TGBlocks.CAMONET_TOP.defaultBlockState();
		switch (camo) {
		case DESERT:
			return s.setValue(BlockTGCamoNet.CAMO_TYPE, EnumCamoNetType.DESERT);
		case SNOW:
			return s.setValue(BlockTGCamoNet.CAMO_TYPE, EnumCamoNetType.SNOW);
		case WOODLAND:
		default:
			return s;
		}
	}

}
