package techguns.gui.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import techguns.TGPackets;
import techguns.capabilities.TGExtendedPlayer;
import techguns.keybind.TGKeybindsID;
import techguns.packets.PacketTGKeybindPress;

public class TGGuiToggleButton extends AbstractButton {

	private byte type;

	/**
	 * @param type - 0: showTGHud, 1: Nightvision, 2: safemode, 3: stepassist, 4: toggleJetpack
	 */
	public TGGuiToggleButton(int xPos, int yPos, int type) {
		super(xPos, yPos, 11, 11, Component.empty());
		this.type = (byte) type;
	}

	@Override
	public void onPress() {
		switch (this.type) {
		case 0:
			TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.TOGGLE_HUD));
			break;
		case 1:
			TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.TOGGLE_NIGHTVISION));
			break;
		case 2:
			TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.TOGGLE_SAFEMODE));
			break;
		case 3:
			TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.TOGGLE_STEP_ASSIST));
			break;
		case 4:
			TGPackets.network.sendToServer(new PacketTGKeybindPress(TGKeybindsID.TOGGLE_JETPACK));
			break;
		}
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
		//vanilla button background (1.12 GuiUtils.drawContinuousTexturedBox with the button texture)
		int k = !this.active ? 0 : (this.isHoveredOrFocused() ? 2 : 1);
		g.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.width, this.height, 2, 2, 2, 3, 200, 20, 0, 46 + k * 20);

		TGExtendedPlayer props = TGExtendedPlayer.get(Minecraft.getInstance().player);
		if (props == null) {
			return;
		}

		byte mode = type;
		boolean value = false;
		switch (type) {
		case 0:
			value = props.showTGHudElements;
			break;
		case 1:
			value = props.enableNightVision;
			break;
		case 2:
			value = props.enableSafemode;
			break;
		case 3:
			value = props.enableStepAssist;
			break;
		case 4:
			value = props.enableJetpack;
			break;
		default:
			break;
		}
		byte state = (byte) (value ? 1 : 0);

		int y = 7 * mode;
		if (mode > 3) {
			y += 14;
		}
		g.blit(TGPlayerInventoryGui.texture, this.getX() + 2, this.getY() + 2, 242 + (7 * state), y, 7, 7);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}

}
