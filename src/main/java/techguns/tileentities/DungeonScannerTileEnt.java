package techguns.tileentities;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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
import techguns.gui.containers.DungeonScannerContainer;
import techguns.world.dungeon.DungeonSegment;
import techguns.world.dungeon.DungeonTemplate;
import techguns.world.dungeon.TemplateSegment;
import techguns.world.dungeon.TemplateSegment.SegmentType;

/**
 * Dungeon template scanner (debug machine, 1.12 basicmachine meta 4): the segments of a template are built in a grid
 * next to the scanner (see TemplateSegment row/col), "scan" saves them to ./templates/NAME.txt (1.20 text format, copy
 * them to assets/techguns/dungeons to ship them), "load" places a loaded template.
 */
public class DungeonScannerTileEnt extends BasicOwnedTileEnt {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final int BUTTON_ID_GHOST = ButtonConstants.BUTTON_ID_SECURITY + 1;

	public static final int BUTTON_ID_SIZE_XZ_PLUS = ButtonConstants.BUTTON_ID_SECURITY + 2;
	public static final int BUTTON_ID_SIZE_XZ_MINUS = ButtonConstants.BUTTON_ID_SECURITY + 3;

	public static final int BUTTON_ID_SIZE_Y_PLUS = ButtonConstants.BUTTON_ID_SECURITY + 4;
	public static final int BUTTON_ID_SIZE_Y_MINUS = ButtonConstants.BUTTON_ID_SECURITY + 5;

	public static final int BUTTON_ID_SCAN = ButtonConstants.BUTTON_ID_SECURITY + 6;
	public static final int BUTTON_ID_PLACE = ButtonConstants.BUTTON_ID_SECURITY + 7;

	public static final int BUTTON_ID_CLEAR = ButtonConstants.BUTTON_ID_SECURITY + 8;

	public static final int BUTTON_ID_COPY_FIRST = ButtonConstants.BUTTON_ID_SECURITY + 9;

	public static final int SPACING = 1;

	public int sizeXZ = 5;
	public int sizeY = 4;

	public boolean showGhost = true;

	public EnumMap<SegmentType, Boolean> template_filter = new EnumMap<>(SegmentType.class);

	public DungeonScannerTileEnt(BlockPos pos, BlockState state) {
		super(TGMachines.DUNGEON_SCANNER_TILE, pos, state, 0, false);
		for (SegmentType type : TemplateSegment.templateSegments.keySet()) {
			this.template_filter.put(type, true);
		}
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("techguns.container.dungeonscanner");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new DungeonScannerContainer(id, inv, this);
	}

	@Override
	public void readClientDataFromNBT(CompoundTag tags) {
		super.readClientDataFromNBT(tags);
		this.sizeXZ = Math.max(1, tags.getInt("sizeXZ"));
		this.sizeY = Math.max(1, tags.getInt("sizeY"));
		this.showGhost = tags.getBoolean("showGhost");
	}

	@Override
	public void writeClientDataToNBT(CompoundTag tags) {
		super.writeClientDataToNBT(tags);
		tags.putInt("sizeXZ", sizeXZ);
		tags.putInt("sizeY", sizeY);
		tags.putBoolean("showGhost", showGhost);
	}

	@Override
	public void buttonClicked(int id, Player ply, String data) {
		if (!isUseableByPlayer(ply))
			return;
		switch (id) {
		case BUTTON_ID_GHOST:
			showGhost = !showGhost;
			break;
		case BUTTON_ID_SIZE_XZ_MINUS:
			sizeXZ = Math.max(1, sizeXZ - 1);
			break;
		case BUTTON_ID_SIZE_XZ_PLUS:
			sizeXZ = Math.min(32, sizeXZ + 1);
			break;
		case BUTTON_ID_SIZE_Y_MINUS:
			sizeY = Math.max(1, sizeY - 1);
			break;
		case BUTTON_ID_SIZE_Y_PLUS:
			sizeY = Math.min(32, sizeY + 1);
			break;
		case BUTTON_ID_SCAN:
			this.scanTemplate(data);
			break;
		case BUTTON_ID_PLACE:
			this.placeTemplate(data);
			break;
		case BUTTON_ID_CLEAR:
			this.clearBlocks();
			break;
		case BUTTON_ID_COPY_FIRST:
			this.copyFirstSegment();
			break;
		default:
			super.buttonClicked(id, ply, data);
			break;
		}
		this.needUpdate();
	}

	private void copyFirstSegment() {
		DungeonTemplate template = new DungeonTemplate(this.sizeXZ, this.sizeY);
		DungeonSegment firstSegment = new DungeonSegment(template, SegmentType.STRAIGHT);
		firstSegment.scanBlocks(level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ());
		for (SegmentType type : TemplateSegment.templateSegments.keySet()) {
			if (type != SegmentType.STRAIGHT) {
				firstSegment.type = type;
				firstSegment.placeTemplateSegment(level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), 0);
			}
		}
	}

	private void clearBlocks() {
		Vec3i max = TemplateSegment.getMaxExtents(this.sizeXZ, this.sizeY);
		for (int x = SPACING; x < max.getX(); x++) {
			for (int y = 0; y < max.getY(); y++) {
				for (int z = SPACING; z < max.getZ(); z++) {
					BlockPos bpos = this.worldPosition.offset(x, y, z);
					this.level.setBlock(bpos, Blocks.AIR.defaultBlockState(), 2);
				}
			}
		}
	}

	private void scanTemplate(String name) {
		if (name == null || name.isEmpty())
			return;
		DungeonTemplate template = DungeonTemplate.scanTemplate(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.sizeXZ, this.sizeY, name);
		try {
			template.save(new File(DungeonTemplate.SCAN_DIR + name + ".txt"));
		} catch (IOException e) {
			LOGGER.error("Techguns: could not save dungeon template {}", name, e);
		}
	}

	private void placeTemplate(String name) {
		if (name == null || name.isEmpty())
			return;
		DungeonTemplate template = DungeonTemplate.get(name);
		if (template != null) {
			template.placeTemplate(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ());
		}
	}

	@Override
	public AABB getRenderBoundingBox() {
		int renderbounds = SPACING + (sizeXZ + SPACING) * 30;
		return new AABB(worldPosition, worldPosition.offset(renderbounds, sizeY * 2, renderbounds));
	}

}
