package techguns.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;

/**
 * Button with the repair (wrench) icon of the repair bench texture
 */
public class GuiButtonRepair extends GuiButtonExt {
	private static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/repair_bench_gui.png");

	public GuiButtonRepair(int id, int xPos, int yPos, int width, int height, OnPress onPress) {
		super(id, xPos, yPos, width, height, "", onPress);
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
		super.renderWidget(g, mouseX, mouseY, partial);
		if (this.visible) {
			g.blit(texture, this.getX() + 1, this.getY() + 1, 176, 0, 12, 12);
		}
	}
}
