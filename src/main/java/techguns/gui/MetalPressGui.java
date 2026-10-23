package techguns.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.Techguns;
import techguns.gui.containers.MetalPressContainer;
import techguns.gui.widgets.GuiButtonExt;
import techguns.tileentities.MetalPressTileEnt;
import techguns.util.TextUtil;

public class MetalPressGui extends PoweredTileEntGui<MetalPressContainer> {
	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/metal_press_gui.png");

	MetalPressTileEnt tile;

	public MetalPressGui(MetalPressContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getMetalPress());
		this.tile = container.getMetalPress();
		this.tex = texture;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		int x = 0;
		int y = 0;
		int color = 4210752; // 0xff101010;

		String s2 = "";
		switch (this.tile.getAutoSplitMode()) {
		case 0:
			s2 = TextUtil.trans("techguns.container.info.off");
			break;
		case 1:
			s2 = TextUtil.trans("techguns.container.info.on");
			break;
		}
		g.drawString(this.font, TextUtil.trans("techguns.container.metalpress.autosplit") + ":", x + 20, y + 30, color, false);
		g.drawString(this.font, s2, x + 20, y + 40, color, false);

		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);

		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		if (this.tile.isWorking()) {
			int i1 = this.tile.getProgressScaled(21);
			g.blit(texture, k + 119, l + 36, 176, 0, 19, i1 + 1);
		}

	}

	@Override
	protected void init() {
		super.init();
		this.addRenderableWidget(new GuiButtonExt(MetalPressTileEnt.BUTTON_ID_AUTOSPLIT, this.leftPos + 20, this.topPos + 50, 40, 20, TextUtil.trans("techguns.button.switch"), b -> this.actionPerformed(MetalPressTileEnt.BUTTON_ID_AUTOSPLIT)));
	}

}
