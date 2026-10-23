package techguns.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGConfig;
import techguns.TGMachines;
import techguns.TGSounds;

public class ExplosiveChargeAdvTileEnt extends ExplosiveChargeTileEnt {

	public ExplosiveChargeAdvTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.EXPLOSIVE_CHARGE_ADVANCED_TILE, pos, state);
		this.maxBlastradius = 7;
		this.maxBlastLength = 10;

		this.blastradius = 5;
		this.blastlength = 10;
	}

	@Override
	public SoundEvent getTickSound() {
		return TGSounds.C4_TICK;
	}

	@Override
	public SoundEvent getSoundPlant() {
		return TGSounds.C4_PLANT;
	}

	@Override
	public SoundEvent getInitSound() {
		return TGSounds.C4_INIT;
	}

	@Override
	protected boolean canBreakBlockHardness(float hardness) {
		return hardness >= 0 && hardness <= TGConfig.explosiveChargeAdvancedMaxBlockHardness;
	}
}
