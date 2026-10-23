package techguns.gui;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.gui.containers.DungeonGeneratorContainer;
import techguns.gui.widgets.GuiButtonExt;
import techguns.packets.PacketGuiButtonClick;
import techguns.tileentities.DungeonGeneratorTileEnt;
import techguns.world.dungeon.DungeonTemplate;

public class DungeonGeneratorGui extends OwnedTileEntGui<DungeonGeneratorContainer> {

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/dungeon_scanner_gui.png");

	protected DungeonGeneratorTileEnt tileEnt;

	private String textInput = "";

	protected List<String> templateNames;

	public DungeonGeneratorGui(DungeonGeneratorContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getTile());
		this.tex = texture;
		this.tileEnt = container.getTile();
		this.templateNames = DungeonTemplate.getTemplateNames();
		// 1.20: the dungeon presets can be generated too
		this.templateNames.add(0, "castle");
		this.templateNames.add(1, "techfortress");
		this.templateNames.add(2, "nether");
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		int color = 4210752;
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
		return true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		int dy = 0;
		for (String template : templateNames) {
			if (isInRect((int) mouseX, (int) mouseY, this.leftPos + 75, this.topPos + 18 + dy, 93, 8)) {
				this.textInput = template;
				return true;
			}
			dy += 8;
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
	}

	@Override
	protected void actionPerformed(int buttonId) {
		if (textInput.length() > 0 && buttonId == DungeonGeneratorTileEnt.BUTTON_ID_GENERATE) {
			TGPackets.network.sendToServer(new PacketGuiButtonClick(this.ownedTile, buttonId, textInput));
		} else {
			super.actionPerformed(buttonId);
		}
	}

	@Override
	protected void init() {
		super.init();
		this.addRenderableWidget(new GuiButtonExt(DungeonGeneratorTileEnt.BUTTON_ID_GHOST, this.leftPos + 5, this.topPos + 5, 12, 12, "G", b -> this.actionPerformed(DungeonGeneratorTileEnt.BUTTON_ID_GHOST)));
		this.addRenderableWidget(new GuiButtonExt(DungeonGeneratorTileEnt.BUTTON_ID_GENERATE, this.leftPos + 5, this.topPos + 81, 64, 12, "generate", b -> this.actionPerformed(DungeonGeneratorTileEnt.BUTTON_ID_GENERATE)));
		this.addRenderableWidget(new GuiButtonExt(DungeonGeneratorTileEnt.BUTTON_ID_CLEAR, this.leftPos + 5, this.topPos + 149, 12, 12, "C", b -> this.actionPerformed(DungeonGeneratorTileEnt.BUTTON_ID_CLEAR)));
	}
}
