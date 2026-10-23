package techguns.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import techguns.tileentities.TurretTileEnt;

public class GuiButtonPvPSetting extends GuiButtonExt {
	protected TurretTileEnt turret;

	public GuiButtonPvPSetting(int id, int xPos, int yPos, int width, int height, TurretTileEnt tile, OnPress onPress) {
		super(id, xPos, yPos, width, height, Component.empty(), onPress);
		this.turret = tile;
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
		super.renderWidget(g, mouseX, mouseY, partial);
		if (this.visible) {
			byte mode = turret.getPvpSetting();
			g.blit(GuiButtonTargetAnimals.texture, this.getX() + 2, this.getY() + 2, 193, 6 + (16 * mode), 16, 16);
		}
	}
}
