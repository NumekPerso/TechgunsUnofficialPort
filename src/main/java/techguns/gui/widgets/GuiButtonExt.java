package techguns.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Button with a 1.12 style button id (client only). The press action is given by the GUI. Drawn with the 1.12 look (TGButtonStyle).
 */
public class GuiButtonExt extends Button {

	public final int id;

	public GuiButtonExt(int id, int xPos, int yPos, int width, int height, Component displayString, OnPress onPress) {
		super(xPos, yPos, width, height, displayString, onPress, DEFAULT_NARRATION);
		this.id = id;
	}

	public GuiButtonExt(int id, int xPos, int yPos, int width, int height, String displayString, OnPress onPress) {
		this(id, xPos, yPos, width, height, Component.literal(displayString), onPress);
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
		int state = TGButtonStyle.getState(this);
		TGButtonStyle.drawBackground(g, this.getX(), this.getY(), this.width, this.height, state);
		this.renderString(g, Minecraft.getInstance().font, TGButtonStyle.getTextColor(state) | 0xFF000000);
	}
}
