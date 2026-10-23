package techguns.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.tileentities.ChemLabTileEnt;

/**
 * Button of the chem lab GUI showing which tank is drained by automation/buckets (client only)
 */
public class GuiButtonToggleDrainTank extends GuiButtonExt {
	private static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/chem_lab_gui.png");
	private ChemLabTileEnt tileent;

	public GuiButtonToggleDrainTank(int id, int xPos, int yPos, int width, int height, ChemLabTileEnt tileEnt, OnPress onPress) {
		super(id, xPos, yPos, width, height, Component.empty(), onPress);
		this.tileent = tileEnt;
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
		super.renderWidget(g, mouseX, mouseY, partial);
		if (this.visible) {
			byte mode = tileent.getDrainMode();
			g.blit(texture, this.getX() + 1, this.getY() + 1, 188, 31 + (12 * mode), 12, 12);
		}
	}
}
