package techguns.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.tileentities.TurretTileEnt;

public class GuiButtonTargetAnimals extends GuiButtonExt {

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/turret_base_gui.png");

	protected TurretTileEnt tile;

	public GuiButtonTargetAnimals(int id, int xPos, int yPos, int width, int height, TurretTileEnt tile, OnPress onPress) {
		super(id, xPos, yPos, width, height, Component.empty(), onPress);
		this.tile = tile;
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
		super.renderWidget(g, mouseX, mouseY, partial);
		if (this.visible) {
			byte mode = (byte) (tile.attackAnimals ? 1 : 0);
			g.blit(texture, this.getX() + 2, this.getY() + 2, 176, 6 + (16 * mode), 16, 16);
		}
	}

}
