package techguns.gui;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.gui.containers.DungeonScannerContainer;
import techguns.gui.widgets.GuiButtonExt;
import techguns.packets.PacketGuiButtonClick;
import techguns.tileentities.DungeonScannerTileEnt;
import techguns.world.dungeon.DungeonTemplate;
import techguns.world.dungeon.TemplateSegment;
import techguns.world.dungeon.TemplateSegment.SegmentType;

public class DungeonScannerGui extends OwnedTileEntGui<DungeonScannerContainer> {

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/dungeon_scanner_gui.png");

	protected DungeonScannerTileEnt tileEnt;

	protected String textInput = "";

	protected List<String> templateNames;

	private static final int tb_size = 4; //tb = template button
	private static final int tb_spacing = 2;
	private static final int tb_x = 5;
	private static final int tb_y = 114;

	public DungeonScannerGui(DungeonScannerContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getTile());
		this.tex = texture;
		this.tileEnt = container.getTile();
		this.templateNames = DungeonTemplate.getTemplateNames();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		int color = 4210752;

		g.drawString(this.font, "sizeXZ: " + this.tileEnt.sizeXZ, 5, 19, color, false);
		g.drawString(this.font, "sizeY: " + this.tileEnt.sizeY, 5, 41, color, false);
		g.drawString(this.font, textInput, 6, 70, 0xffffffff, false);

		int dy = 0;
		for (String template : templateNames) {
			g.drawString(this.font, template, 75, 18 + dy, color, false);
			dy += 8;
		}
	}

	@Override
	public boolean charTyped(char typedChar, int modifiers) {
		if ((Character.isLetterOrDigit(typedChar) || typedChar == '_') && textInput.length() < 16) {
			textInput += Character.toLowerCase(typedChar);
			return true;
		}
		return super.charTyped(typedChar, modifiers);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 259) { // backspace
			if (textInput.length() > 0)
				textInput = textInput.substring(0, textInput.length() - 1);
			return true;
		}
		if (keyCode == 256) { // escape
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
		// no inventory key closing, typing letters
		return true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		int k = this.leftPos;
		int l = this.topPos;
		int mx = (int) mouseX;
		int my = (int) mouseY;

		int dy = 0;
		for (String template : templateNames) {
			if (isInRect(mx, my, k + 75, l + 18 + dy, 93, 8)) {
				this.textInput = template;
				return true;
			}
			dy += 8;
		}

		for (SegmentType type : TemplateSegment.templateSegments.keySet()) {
			TemplateSegment seg = TemplateSegment.templateSegments.get(type);
			int x_ = tb_x + seg.col * (tb_size + tb_spacing);
			int y_ = tb_y + seg.row * (tb_size + tb_spacing);
			if (isInRect(mx, my, k + x_, l + y_, tb_size, tb_size)) {
				this.tileEnt.template_filter.put(type, !this.tileEnt.template_filter.get(type));
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);
		int k = this.leftPos;
		int l = this.topPos;
		g.fill(k + 5, l + 69, k + 69, l + 79, 0xff000000);
		g.fill(k + 74, l + 17, k + 169, l + 158, 0xff888888);

		int dy = 0;
		for (String template : templateNames) {
			if (isInRect(mouseX, mouseY, k + 75, l + 18 + dy, 93, 8)) {
				g.fill(k + 75, l + 18 + dy, k + 75 + 93, l + 18 + dy + 8, 0xFFA0A0A0);
				break;
			}
			dy += 8;
		}

		for (SegmentType type : TemplateSegment.templateSegments.keySet()) {
			this.drawSegmentButton(g, type, k, l, mouseX, mouseY);
		}
	}

	private void drawSegmentButton(GuiGraphics g, SegmentType type, int k, int l, int mouseX, int mouseY) {
		TemplateSegment seg = TemplateSegment.templateSegments.get(type);
		int x_ = tb_x + seg.col * (tb_size + tb_spacing);
		int y_ = tb_y + seg.row * (tb_size + tb_spacing);
		boolean on = this.tileEnt.template_filter.getOrDefault(type, true);

		g.fill(k + x_, l + y_, k + x_ + tb_size, l + y_ + tb_size, on ? 0xff008000 : 0xff323232);
		int color = on ? 0xff00ff00 : 0xff646464;
		g.fill(k + x_ + 1, l + y_ + 1, k + x_ + tb_size - 1, l + y_ + tb_size - 1, color);

		if (seg.pattern != null && seg.pattern.length >= 8) {
			if (seg.pattern[0]) g.fill(k + x_, l + y_, k + x_ + 1, l + y_ + 1, color);
			if (seg.pattern[1]) g.fill(k + x_ + 1, l + y_, k + x_ + 3, l + y_ + 1, color);
			if (seg.pattern[2]) g.fill(k + x_ + 3, l + y_, k + x_ + 4, l + y_ + 1, color);
			if (seg.pattern[3]) g.fill(k + x_ + 3, l + y_ + 1, k + x_ + 4, l + y_ + 3, color);
			if (seg.pattern[4]) g.fill(k + x_ + 3, l + y_ + 3, k + x_ + 4, l + y_ + 4, color);
			if (seg.pattern[5]) g.fill(k + x_ + 1, l + y_ + 3, k + x_ + 3, l + y_ + 4, color);
			if (seg.pattern[6]) g.fill(k + x_, l + y_ + 3, k + x_ + 1, l + y_ + 4, color);
			if (seg.pattern[7]) g.fill(k + x_, l + y_ + 1, k + x_ + 1, l + y_ + 3, color);
		}

		if (isInRect(mouseX, mouseY, k + x_, l + y_, tb_size, tb_size)) {
			g.fill(k + x_, l + y_, k + x_ + tb_size, l + y_ + tb_size, 0x80ffffff);
		}
	}

	@Override
	protected void actionPerformed(int buttonId) {
		if (textInput.length() > 0 && (buttonId == DungeonScannerTileEnt.BUTTON_ID_SCAN || buttonId == DungeonScannerTileEnt.BUTTON_ID_PLACE)) {
			TGPackets.network.sendToServer(new PacketGuiButtonClick(this.ownedTile, buttonId, textInput));
			if (buttonId == DungeonScannerTileEnt.BUTTON_ID_SCAN && !templateNames.contains(textInput)) {
				templateNames.add(textInput);
			}
		} else {
			super.actionPerformed(buttonId);
		}
	}

	protected void addButton(int id, int x, int y, int w, int h, String text) {
		this.addRenderableWidget(new GuiButtonExt(id, this.leftPos + x, this.topPos + y, w, h, text, b -> this.actionPerformed(id)));
	}

	@Override
	protected void init() {
		super.init();
		addButton(DungeonScannerTileEnt.BUTTON_ID_GHOST, 5, 5, 12, 12, "G");
		addButton(DungeonScannerTileEnt.BUTTON_ID_SIZE_XZ_MINUS, 5, 27, 12, 12, "-");
		addButton(DungeonScannerTileEnt.BUTTON_ID_SIZE_XZ_PLUS, 18, 27, 12, 12, "+");
		addButton(DungeonScannerTileEnt.BUTTON_ID_SIZE_Y_MINUS, 5, 50, 12, 12, "-");
		addButton(DungeonScannerTileEnt.BUTTON_ID_SIZE_Y_PLUS, 18, 50, 12, 12, "+");
		addButton(DungeonScannerTileEnt.BUTTON_ID_SCAN, 5, 81, 31, 12, "scan");
		addButton(DungeonScannerTileEnt.BUTTON_ID_PLACE, 38, 81, 31, 12, "load");
		addButton(DungeonScannerTileEnt.BUTTON_ID_CLEAR, 5, 149, 12, 12, "C");
		addButton(DungeonScannerTileEnt.BUTTON_ID_COPY_FIRST, 5, 97, 64, 12, "Copy 1st Segment");
	}

}
