package techguns.gui.widgets;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Button with a 1.12 style button id (client only). The press action is given by the GUI.
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
}
