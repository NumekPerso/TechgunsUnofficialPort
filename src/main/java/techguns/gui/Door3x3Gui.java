package techguns.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.Techguns;
import techguns.gui.containers.Door3x3Container;
import techguns.gui.widgets.GuiButtonDoorRightClickSetting;
import techguns.gui.widgets.GuiButtonExt;
import techguns.tileentities.Door3x3TileEntity;
import techguns.util.TextUtil;

public class Door3x3Gui extends RedstoneTileEntGui<Door3x3Container> {
	/**
	 * 1.12 DungeonGeneratorGui.texture
	 */
	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/dungeon_scanner_gui.png");

	Door3x3TileEntity tile;

	public Door3x3Gui(Door3x3Container container, Inventory player, Component title) {
		super(container, player, title, container.getDoor());
		this.tile = container.getDoor();
		this.tex = texture;
		this.showInventoryText = false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		int color = 4210752; //0xff101010;

		byte dm = tile.getDoormode();

		g.drawString(this.font, TextUtil.trans(Techguns.MODID + ".container.door3x3.mode." + dm), 44, 20, color, false);

		g.drawString(this.font, (tile.getDoormode() == 0 ? "" : ChatFormatting.STRIKETHROUGH.toString()) + TextUtil.trans(Techguns.MODID + ".container.door3x3.autoclose"), 44, 20 + 20, color, false);

		if (tile.getDoormode() == 1 && tile.getRedstoneBehaviour() == 0) {
			g.drawString(this.font, ChatFormatting.RED + TextUtil.trans(Techguns.MODID + ".container.door3x3.redstonewarning"), 44, 20 + 40, color, false);
		}

		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);
	}

	@Override
	protected void init() {
		super.init();
		//id, x, y, width, height, text
		this.addRenderableWidget(new GuiButtonExt(Door3x3TileEntity.BUTTON_ID_DOORMODE, this.leftPos + 8, this.topPos + 16, 32, 16, TextUtil.trans(Techguns.MODID + ".mode"), b -> this.actionPerformed(Door3x3TileEntity.BUTTON_ID_DOORMODE)));
		this.addRenderableWidget(new GuiButtonDoorRightClickSetting(Door3x3TileEntity.BUTTON_ID_AUTOCLOSE, this.leftPos + 8, this.topPos + 16 + 20, 32, 16, tile, 1, b -> this.actionPerformed(Door3x3TileEntity.BUTTON_ID_AUTOCLOSE)));
	}
}
