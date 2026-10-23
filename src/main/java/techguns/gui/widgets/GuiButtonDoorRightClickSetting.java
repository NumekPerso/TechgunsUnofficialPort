package techguns.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import techguns.Techguns;
import techguns.tileentities.Door3x3TileEntity;
import techguns.util.TextUtil;

/**
 * Door GUI button showing the current state of a door setting (type 1 = auto close)
 */
public class GuiButtonDoorRightClickSetting extends GuiButtonExt {
	protected Door3x3TileEntity tile;
	protected int type;

	public GuiButtonDoorRightClickSetting(int id, int xPos, int yPos, int width, int height, Door3x3TileEntity tile, int type, OnPress onPress) {
		super(id, xPos, yPos, width, height, "", onPress);
		this.tile = tile;
		this.type = type;
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
		this.setMessage(Component.literal(this.getDisplayString()));
		super.renderWidget(g, mouseX, mouseY, partial);
	}

	protected String getDisplayString() {
		if (type == 1) {
			return getStateString(tile.isAutoClose());
		}
		return "";
	}

	protected String getStateString(boolean yes) {
		if (yes) {
			return TextUtil.trans(Techguns.MODID + ".yes");
		} else {
			return TextUtil.trans(Techguns.MODID + ".no");
		}
	}
}
