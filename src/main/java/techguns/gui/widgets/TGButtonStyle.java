package techguns.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

/**
 * 1.12 look of the buttons (Techguns 1.12 used the 1.12 vanilla button texture): bevelled gray button, blue tint when
 * hovered, yellow text when hovered. Drawn with plain colors instead of the 1.20 widgets texture (black border, white
 * outline when hovered or focused).
 */
public class TGButtonStyle {

	public static final int STATE_DISABLED = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_HOVERED = 2;

	private static final int BORDER = 0xFF000000;
	// fill, light edge (top/left), dark edge (bottom/right) per state
	private static final int[] FILL = { 0xFF2C2C2C, 0xFF6F6F6F, 0xFF7E88BF };
	private static final int[] LIGHT = { 0xFF3D3D3D, 0xFFAAAAAA, 0xFFBDC6FF };
	private static final int[] DARK = { 0xFF242424, 0xFF515151, 0xFF5A6299 };

	public static final int TEXT_DISABLED = 0xA0A0A0;
	public static final int TEXT_NORMAL = 0xE0E0E0;
	public static final int TEXT_HOVERED = 0xFFFFA0;

	/**
	 * 1.20 buttons stay focused after a mouse click (white outline), the 1.12 look only follows the mouse. The focus
	 * is still shown when navigating with the keyboard.
	 */
	public static int getState(AbstractWidget w) {
		if (!w.active) {
			return STATE_DISABLED;
		}
		boolean keyboardFocus = w.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard();
		return (w.isHovered() || keyboardFocus) ? STATE_HOVERED : STATE_NORMAL;
	}

	public static int getTextColor(int state) {
		return state == STATE_DISABLED ? TEXT_DISABLED : (state == STATE_HOVERED ? TEXT_HOVERED : TEXT_NORMAL);
	}

	public static void drawBackground(GuiGraphics g, int x, int y, int w, int h, int state) {
		g.fill(x, y, x + w, y + h, BORDER);
		if (w <= 2 || h <= 2) {
			return;
		}
		g.fill(x + 1, y + 1, x + w - 1, y + h - 1, DARK[state]);
		g.fill(x + 1, y + 1, x + w - 2, y + h - 2, LIGHT[state]);
		if (w > 4 && h > 4) {
			g.fill(x + 2, y + 2, x + w - 2, y + h - 2, FILL[state]);
		}
	}
}
