package techguns.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.TGConfig;
import techguns.Techguns;
import techguns.gui.containers.OreDrillContainer;
import techguns.tileentities.OreDrillTileEntMaster;
import techguns.util.TextUtil;

public class OreDrillGui extends PoweredTileEntGui<OreDrillContainer> {
	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/ore_drill_gui.png");

	public static final int INPUT_TANK_X = 15;
	public static final int TANK_Y = 17;
	public static final int OUTPUT_TANK_X = 157;

	public static final int TANK_W = 10;
	public static final int TANK_H = 50;

	protected OreDrillTileEntMaster tile;

	public OreDrillGui(OreDrillContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getOreDrill());
		this.tex = texture;
		this.tile = container.getOreDrill();
		this.hasUpgradeSlot = false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		int mx = mouseX - (this.width - this.imageWidth) / 2;
		int my = mouseY - (this.height - this.imageHeight) / 2;

		int red = 0xFFFF2020;
		if (!tile.isFormed()) {
			String s1 = TextUtil.trans("techguns.container.oredrill.broken");
			int startx = this.imageWidth / 2 - (this.font.width(s1) / 2);
			g.drawString(this.font, s1, startx - 16 + 30, 44 - 20, red, false);
		} else {
			if (tile.getDrillItemMiningLevel() < 0) {

				int rad = tile.getDrillRadius();
				String s1 = "2";
				if (rad == 0) {
					s1 = "0";
				} else if (rad == 1 || rad == 2) {
					s1 = "1";
				}
				String s2 = TextUtil.trans("techguns.container.oredrill.drillrequired." + s1);

				int startx = this.imageWidth / 2 - (this.font.width(s2) / 2);
				g.drawString(this.font, s2, startx - 16 + 30, 44 - 20, red, false);
			}
		}

		if (isInRect(mx, my, 74, 16, 27, 39)) {
			List<String> tooltip = new ArrayList<String>();

			if (this.tile.totaltime > 0) {
				tooltip.add(String.format("%.2f", (this.tile.progress * 1.0f / this.tile.totaltime * 100.0f)) + "%");
				tooltip.add(TextUtil.trans("techguns.gui.oredrill.orehour") + ": " + String.format("%.2f", 1200.0 / this.tile.totaltime * 60.0));
			}
			if (this.tile.getPowerPerTick() > 0) {
				tooltip.add(TextUtil.trans("techguns.gui.oredrill.rftick") + ": " + this.tile.getPowerPerTick());
			}
			tooltip.add("");
			tooltip.add(TextUtil.trans("techguns.oredrill.drillsize") + ": " + (tile.getRods() + tile.getEngines()));
			tooltip.add(TextUtil.trans("techguns.oredrill.drillradius") + ": " + tile.getDrillRadius());

			this.drawHoveringText(tooltip, mx, my);

		} else if (isInRect(mx, my, INPUT_TANK_X, TANK_Y - 1, TANK_W + 1, TANK_H + 1)) {
			this.drawHoveringTextComponents(ChemLabGui.getTankTooltip(this.tile.inputTank), mx, my);

		} else if (isInRect(mx, my, OUTPUT_TANK_X, TANK_Y - 1, TANK_W + 1, TANK_H + 1)) {
			this.drawHoveringTextComponents(ChemLabGui.getTankTooltip(this.tile.outputTank), mx, my);

		} else if (isInRect(mx, my, 34, 36, 8, 14)) {
			if (this.tile.getCurrentFuelBufferMax() > 0) {
				int buf = this.tile.getCurrentFuelBufferMax();
				int fuel = this.tile.getFuelBuffer();

				int pwrtick = this.tile.getPowerPerTick();
				float FACTOR = TGConfig.oreDrillMultiplierFuel;

				int ticksLeft = 0;
				if (FACTOR <= 0 || pwrtick <= 0) {
					ticksLeft = -1;
				} else {
					ticksLeft = (int) (fuel / (pwrtick / FACTOR));
				}
				ArrayList<String> tooltip = new ArrayList<String>(2);
				tooltip.add(TextUtil.trans("techguns.oredrill.currentfuel") + " " + fuel + "/" + buf);
				tooltip.add(TextUtil.trans("techguns.oredrill.remaining") + ": " + ticksLeft + " " + TextUtil.trans("techguns.oredrill.ticks"));

				this.drawHoveringText(tooltip, mx, my);
			} else {
				this.drawHoveringText(TextUtil.trans("techguns.oredrill.nofuel"), mx, my);
			}
		}

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		ChemLabGui.drawFluidTank(this, g, this.tile.inputTank, k + INPUT_TANK_X + 1, l + TANK_Y, TANK_W, TANK_H);
		ChemLabGui.drawFluidTank(this, g, this.tile.outputTank, k + OUTPUT_TANK_X + 1, l + TANK_Y, TANK_W, TANK_H);

		g.blit(texture, k + INPUT_TANK_X, l + TANK_Y, 176, 40, TANK_W + 2, TANK_H + 2);
		g.blit(texture, k + OUTPUT_TANK_X, l + TANK_Y, 176, 40, TANK_W + 2, TANK_H + 2);

		if (this.tile.isWorking()) {
			int i1 = this.tile.getProgressScaled(36);
			g.blit(texture, k + 73, l + 17, 177, 1, 25, i1 + 1);
		}

		if (this.tile.getCurrentFuelBufferMax() > 0) {
			int s = 12 - this.tile.getFuelBufferScaled(12);
			g.blit(texture, k + 35, l + 36 + s, 188, 39 + s, 6, 12 - s);
		}
	}

}
