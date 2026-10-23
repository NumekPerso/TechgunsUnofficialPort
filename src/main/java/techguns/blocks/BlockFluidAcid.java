package techguns.blocks;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.entities.projectiles.GenericProjectile;

/**
 * Creeper acid fluid block (1.12 BlockFluidAcid extends BlockFluidClassic): damages living entities inside
 */
public class BlockFluidAcid extends LiquidBlock {

	public BlockFluidAcid(Supplier<? extends FlowingFluid> fluid, Properties properties) {
		super(fluid, properties);
	}

	@Override
	public void entityInside(BlockState state, Level w, BlockPos pos, Entity ent) {
		if (!w.isClientSide && ent instanceof LivingEntity && GenericProjectile.BULLET_TARGETS.test(ent)) {
			TGDamageSource acidDamage = TGDamageSource.causePoisonDamage(null, null, DeathType.BIO);
			acidDamage.goreChance = 1.0f;
			acidDamage.ignoreHurtresistTime = false;
			acidDamage.knockbackMultiplier = 0.0f;
			ent.hurt(acidDamage, 2.0f);
		}
	}

}
