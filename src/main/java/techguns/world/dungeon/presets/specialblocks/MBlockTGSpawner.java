package techguns.world.dungeon.presets.specialblocks;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGBlocks;
import techguns.blocks.EnumMonsterSpawnerType;
import techguns.tileentities.TGSpawnerTileEnt;
import techguns.util.MBlock;

/**
 * Techguns NPC spawner. 1.20: the NPC types are referenced by their registry name (techguns:zombiesoldier, ...),
 * missing entity types are skipped by the spawner.
 */
public class MBlockTGSpawner extends MBlock {

	protected int mobsleft = 5;
	protected int maxActive = 2;
	protected int spawndelay = 200;
	protected int spawnrange = 2;

	protected ArrayList<String> classes = new ArrayList<>();
	protected ArrayList<Integer> weights = new ArrayList<>();

	protected ItemStack weaponOverride = ItemStack.EMPTY;

	public MBlockTGSpawner(MBlock other) {
		super(other);
		this.hasTileEntity = true;
		addMobType("skeletonsoldier", 1);
		addMobType("zombiesoldier", 1);
	}

	public MBlockTGSpawner(EnumMonsterSpawnerType type, int mobsleft, int maxactive, int spawndelay, int spawnrange) {
		super(TGBlocks.MONSTER_SPAWNER.defaultBlockState().setValue(TGBlocks.MONSTER_SPAWNER.TYPE, type));
		this.mobsleft = mobsleft;
		this.maxActive = maxactive;
		this.spawndelay = spawndelay;
		this.spawnrange = spawnrange;
		this.hasTileEntity = true;
	}

	public MBlockTGSpawner setWeaponOverride(ItemStack weapon) {
		this.weaponOverride = weapon;
		return this;
	}

	/**
	 * @param name entity registry path in the techguns namespace (lowercase 1.12 class name, e.g. "zombiesoldier")
	 */
	public MBlockTGSpawner addMobType(String name, int weight) {
		this.classes.add(name);
		this.weights.add(weight);
		return this;
	}

	@Override
	public void tileEntityPostPlacementAction(LevelAccessor w, BlockState state, BlockPos p, int rotation) {
		BlockEntity tile = w.getBlockEntity(p);
		if (tile instanceof TGSpawnerTileEnt spawner) {
			spawner.setParams(mobsleft, maxActive, spawndelay, spawnrange);

			if (!this.weaponOverride.isEmpty()) {
				spawner.setWeaponOverride(weaponOverride);
			}

			for (int i = 0; i < classes.size(); i++) {
				spawner.addMobType(classes.get(i), weights.get(i));
			}
		}
	}

}
