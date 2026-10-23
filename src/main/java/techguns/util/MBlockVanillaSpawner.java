package techguns.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import techguns.Techguns;
import techguns.world.TGStructureLevel;

/**
 * Vanilla mob spawner. 1.20: entity type by registry name (resolved when placed, unknown types leave an empty spawner)
 */
public class MBlockVanillaSpawner extends MBlock {

	ResourceLocation entityId;

	public MBlockVanillaSpawner(String entityName) {
		super(Blocks.SPAWNER.defaultBlockState());
		this.hasTileEntity = true;
		this.entityId = entityName.contains(":") ? new ResourceLocation(entityName) : new ResourceLocation(Techguns.MODID, entityName);
	}

	@Override
	public void tileEntityPostPlacementAction(LevelAccessor w, BlockState state, BlockPos p, int rotation) {
		BlockEntity tile = w.getBlockEntity(p);
		if (tile instanceof SpawnerBlockEntity spawner && ForgeRegistries.ENTITY_TYPES.containsKey(entityId)) {
			EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(entityId);
			spawner.setEntityId(type, TGStructureLevel.posRandomSource(w, p));
		}
	}

}
