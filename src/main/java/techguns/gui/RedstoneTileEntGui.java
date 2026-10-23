package techguns.gui;

import static techguns.gui.ButtonConstants.BUTTON_ID_REDSTONE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import techguns.gui.containers.TGBaseContainer;
import techguns.gui.widgets.GuiButtonRedstoneState;
import techguns.tileentities.BasicRedstoneTileEnt;
import techguns.util.TextUtil;

public class RedstoneTileEntGui<T extends TGBaseContainer> extends OwnedTileEntGui<T> {

	protected BasicRedstoneTileEnt redstoneTileEnt;

	public RedstoneTileEntGui(T inventorySlotsIn, Inventory inv, Component title, BasicRedstoneTileEnt tile) {
		super(inventorySlotsIn, inv, title, tile);
		this.redstoneTileEnt = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);

		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		//draw redstone button backbround:
		g.blit(security_texture, k - 22 - 5, l + 10 - 5, 195, 0, 22 + 5, 30);

		//draw Redstone control;
		if (redstoneTileEnt.isRedstoneEnabled()) {
			g.blit(security_texture, k + 6, l + 5, 240, 0, 5, 5);
			g.blit(security_texture, k + 6 + 5, l + 5, 245, 5, 5, 5);
		} else {
			g.blit(security_texture, k + 6, l + 5, 240, 5, 5, 5);
			g.blit(security_texture, k + 6 + 5, l + 5, 245, 0, 5, 5);
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		int mx = mouseX - (this.width - this.imageWidth) / 2;
		int my = mouseY - (this.height - this.imageHeight) / 2;

		if (isInRect(mx, my, 6, 5, 10, 5) || isInRect(mx, my, -22, 10, 20, 20)) {
			List<String> tooltip = new ArrayList<String>();
			tooltip.add(TextUtil.trans("techguns.container.redstone"));
			tooltip.add(TextUtil.trans("techguns.container.redstone.mode") + ": " + TextUtil.trans(redstoneTileEnt.getRedstoneModeText()));
			tooltip.add(TextUtil.trans("techguns.container.redstone.signal") + ": " + TextUtil.trans(redstoneTileEnt.getSignalStateText()));
			this.drawHoveringText(tooltip, mx, my);
		}
	}

	@Override
	protected void init() {
		super.init();
		this.addRenderableWidget(new GuiButtonRedstoneState(BUTTON_ID_REDSTONE, this.leftPos - 22, this.topPos + 10, 20, 20, "", redstoneTileEnt, b -> this.actionPerformed(BUTTON_ID_REDSTONE)));
	}

}
