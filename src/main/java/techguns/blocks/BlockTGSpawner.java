package techguns.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import techguns.TGBlocks;
import techguns.tileentities.TGSpawnerTileEnt;

/**
 * NPC spawner of the structures (1.12 BlockTGSpawner, "tg_spawner"): unbreakable, no collision, drops nothing,
 * removed when all its NPCs are killed.
 */
public class BlockTGSpawner extends GenericBlockMetaEnum<EnumMonsterSpawnerType> implements EntityBlock {

	public static final EnumProperty<EnumMonsterSpawnerType> SPAWNER_TYPE = EnumProperty.create("type", EnumMonsterSpawnerType.class);

	protected static final VoxelShape BB = Shapes.box(2 / 16d, 0, 2 / 16d, 14 / 16d, 2 / 16d, 14 / 16d);

	public BlockTGSpawner() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(-1.0F, 3600000.0F).noLootTable()
				.noOcclusion().noCollission().pushReaction(PushReaction.BLOCK), SPAWNER_TYPE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return BB;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TGSpawnerTileEnt(pos, state);
	}

	@Nullable
	@Override
	public <E extends BlockEntity> BlockEntityTicker<E> getTicker(Level level, BlockState state, BlockEntityType<E> type) {
		if (level.isClientSide || type != TGBlocks.MONSTER_SPAWNER_TILEENT) {
			return null;
		}
		return (lvl, pos, st, be) -> ((TGSpawnerTileEnt) be).update();
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof TGSpawnerTileEnt spawner) {
			switch (state.getValue(TYPE)) {
			case HOLE:
				spawner.addMobType("zombiesoldier", 1);
				break;
			case SOLDIER_SPAWN:
				spawner.addMobType("armysoldier", 1);
				break;
			default:
				break;
			}
		}
	}
}
