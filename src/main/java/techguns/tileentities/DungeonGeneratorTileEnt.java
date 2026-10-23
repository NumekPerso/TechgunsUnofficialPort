package techguns.tileentities;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import techguns.TGMachines;
import techguns.gui.ButtonConstants;
import techguns.gui.containers.DungeonGeneratorContainer;
import techguns.world.dungeon.Dungeon;
import techguns.world.dungeon.DungeonTemplate;
import techguns.world.dungeon.presets.IDungeonPreset;
import techguns.world.dungeon.presets.PresetTemplateTest;

/**
 * Dungeon generator (debug machine, 1.12 basicmachine meta 5): generates a dungeon of a template (or the nether
 * dungeon preset without a valid name; 1.20: "castle", "techfortress", "nether" generate those presets)
 */
public class DungeonGeneratorTileEnt extends BasicOwnedTileEnt {

	public static final int BUTTON_ID_GHOST = ButtonConstants.BUTTON_ID_SECURITY + 1;

	public static final int BUTTON_ID_GENERATE = ButtonConstants.BUTTON_ID_SECURITY + 2;

	public static final int BUTTON_ID_CLEAR = ButtonConstants.BUTTON_ID_SECURITY + 8;

	public int sizeX = 48;
	public int sizeY = 26;
	public int sizeZ = 48;

	public boolean showGhost = true;

	public DungeonGeneratorTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.DUNGEON_GENERATOR_TILE, pos, state, 0, false);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("techguns.container.dungeongenerator");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new DungeonGeneratorContainer(id, inv, this);
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		if (tags.contains("sizeX")) {
			this.sizeX = tags.getInt("sizeX");
			this.sizeY = tags.getInt("sizeY");
			this.sizeZ = tags.getInt("sizeZ");
		}
		this.showGhost = tags.getBoolean("showGhost");
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putInt("sizeX", sizeX);
		tags.putInt("sizeY", sizeY);
		tags.putInt("sizeZ", sizeZ);
		tags.putBoolean("showGhost", showGhost);
	}

	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if (!isUseableByPlayer(ply))
			return;
		switch (id) {
		case BUTTON_ID_GHOST:
			this.showGhost = !this.showGhost;
			break;
		case BUTTON_ID_CLEAR:
			this.clearBlocks();
			break;
		case BUTTON_ID_GENERATE:
			this.generateDungeon(data);
			break;
		default:
			super.buttonClicked(id, ply, data);
			break;
		}
		this.needUpdate();
	}

	private void generateDungeon(String name) {
		Dungeon dungeon;
		if ("castle".equals(name) || "techfortress".equals(name) || "nether".equals(name)) {
			dungeon = new Dungeon(IDungeonPreset.Presets.get(name));
		} else if (name == null || name.isEmpty() || DungeonTemplate.get(name) == null) {
			dungeon = new Dungeon(IDungeonPreset.Presets.nether());
		} else {
			dungeon = new Dungeon(new PresetTemplateTest(DungeonTemplate.get(name)));
		}

		dungeon.generate(this.level, this.worldPosition.getX() + 1, this.worldPosition.getY(), this.worldPosition.getZ() + 1, this.sizeX, this.sizeY, this.sizeZ,
				new Random(this.level.getRandom().nextLong()));
	}

	private void clearBlocks() {
		for (int x = 1; x < sizeX + 1; x++) {
			for (int y = 0; y < sizeY; y++) {
				for (int z = 1; z < sizeZ + 1; z++) {
					BlockPos bpos = this.worldPosition.offset(x, y, z);
					this.level.setBlock(bpos, Blocks.AIR.defaultBlockState(), 2);
				}
			}
		}
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition, worldPosition.offset(sizeX + 1, sizeY, sizeZ + 1));
	}

}
