package techguns.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import techguns.Techguns;
import techguns.gui.containers.UpgradeBenchContainer;
import techguns.tileentities.UpgradeBenchTileEnt;
import techguns.util.TextUtil;

public class UpgradeBenchGui extends OwnedTileEntGui<UpgradeBenchContainer> {

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/upgrade_bench_gui.png");

	protected UpgradeBenchTileEnt tile;
	protected Inventory invplayer;
	protected UpgradeBenchContainer container;

	public UpgradeBenchGui(UpgradeBenchContainer container, Inventory player, Component title) {
		super(container, player, title, container.getUpgradeBench());
		this.container = container;
		this.tile = container.getUpgradeBench();
		this.invplayer = player;
		this.tex = texture;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		if (!this.container.outputSlot.getItem(0).isEmpty()) {
			long time = tile.getLevel().getGameTime();
			int i = 3 - (int) (time % 4);
			float hue = (time % 360) / 360f;

			int x = i >= 2 ? 0 : 89;
			int y = i % 2 == 1 ? 0 : 31;

			int c = Mth.hsvToRgb(hue, 1f, 1f);
			this.drawTexturedModalRect(g, texture, k + 37, l + 40, 0 + x, 167 + y, 89, 31, (c >> 16) & 255, (c >> 8) & 255, c & 255, 255);
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		int xp = this.container.getXp_needed();
		String s = TextUtil.transTG("gui.xpcost") + ": " + xp;

		int color = (this.minecraft.player.getAbilities().instabuild || xp <= this.minecraft.player.totalExperience) ? 8453920 : 16736352; //same colors as anvil.

		this.drawXPText(g, TextUtil.transTG("gui.xp") + ": " + this.minecraft.player.totalExperience, 95, 17, 8453920);
		this.drawXPText(g, s, 95, 17 + 10, color);
	}

	protected void drawXPText(GuiGraphics g, String s, int x, int y, int color) {
		int j = -16777216 | (color & 16579836) >> 2 | color & -16777216;

		g.drawString(this.font, s, x, y + 1, j, false);
		g.drawString(this.font, s, x + 1, y, j, false);
		g.drawString(this.font, s, x + 1, y + 1, j, false);

		g.drawString(this.font, s, x, y, color, false);
	}
}
