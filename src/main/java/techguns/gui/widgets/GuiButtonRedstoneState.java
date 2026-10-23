package techguns.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;
import techguns.tileentities.BasicRedstoneTileEnt;

public class GuiButtonRedstoneState extends GuiButtonExt {
	protected BasicRedstoneTileEnt tileent;
	public static final ResourceLocation texture_redstonestates = new ResourceLocation(Techguns.MODID, "textures/gui/ammo_press_gui.png");

	public GuiButtonRedstoneState(int id, int xPos, int yPos, int width, int height, String displayString, BasicRedstoneTileEnt tile, OnPress onPress) {
		super(id, xPos, yPos, width, height, Component.literal(displayString), onPress);
		this.tileent = tile;
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
		super.renderWidget(g, mouseX, mouseY, partial);
		if (this.visible) {
			byte mode = tileent.getRedstoneBehaviour();
			g.blit(texture_redstonestates, this.getX() + 2, this.getY() + 2, 234, 10 + (16 * mode), 16, 16);
		}
	}
}
