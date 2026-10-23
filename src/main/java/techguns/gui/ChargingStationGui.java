package techguns.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.Techguns;
import techguns.gui.containers.ChargingStationContainer;
import techguns.tileentities.ChargingStationTileEnt;

public class ChargingStationGui extends PoweredTileEntGui<ChargingStationContainer> {

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/charging_station_gui.png");

	ChargingStationTileEnt tile;

	public ChargingStationGui(ChargingStationContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getChargingStation());
		this.tile = container.getChargingStation();
		this.tex = texture;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		if (this.tile.isWorking()) {
			int i1 = this.tile.getProgressScaled(26);
			g.blit(texture, k + 39, l + 19, 0, 167, i1 + 1, 10);
		}
	}

}
