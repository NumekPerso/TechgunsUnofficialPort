package techguns.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import techguns.Techguns;
import techguns.client.TGFluidsClient;
import techguns.gui.containers.ChemLabContainer;
import techguns.gui.widgets.GuiButtonExt;
import techguns.gui.widgets.GuiButtonToggleDrainTank;
import techguns.tileentities.ChemLabTileEnt;
import techguns.util.TextUtil;

public class ChemLabGui extends PoweredTileEntGui<ChemLabContainer> {

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/chem_lab_gui.png");

	public static final int INPUT_TANK_X = 18;
	public static final int TANK_Y = 17;
	public static final int OUTPUT_TANK_X = 157;

	public static final int TANK_W = 10;
	public static final int TANK_H = 50;

	protected ChemLabTileEnt tile;

	public ChemLabGui(ChemLabContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getChemLab());
		this.tile = container.getChemLab();
		this.tex = texture;
		this.upgradeSlotX = ChemLabContainer.SLOT_UPGRADE_X - 1;
		this.upgradeSlotY = ChemLabContainer.SLOT_UPGRADE_Y - 1;
	}

	/**
	 * Draws the content of a fluid tank (fluid still texture tinted with the fluid color), absolute coordinates
	 */
	public static void drawFluidTank(TGBaseGui<?> gui, GuiGraphics g, FluidTank tank, int x, int y, int w, int h) {
		if (tank.getFluidAmount() > 0) {
			int px = tank.getFluidAmount() * h / tank.getCapacity();
			FluidStack fluid = tank.getFluid();
			int clr = TGFluidsClient.getTintColor(fluid);
			RenderSystem.setShaderColor((clr >> 16 & 0xFF) / 255f, (clr >> 8 & 0xFF) / 255f, (clr & 0xFF) / 255f, 1f);
			gui.drawFluidWithTesselator(g, TGFluidsClient.getStillSprite(fluid), x, y, w, h, px);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		}
	}

	/**
	 * Tooltip lines of a fluid tank (name, amount/capacity)
	 */
	public static List<Component> getTankTooltip(FluidTank tank) {
		List<Component> tooltip = new ArrayList<>();
		FluidStack fluid = tank.getFluid();
		tooltip.add(!fluid.isEmpty() ? fluid.getDisplayName() : Component.literal(TextUtil.trans("techguns.gui.empty")));
		tooltip.add(Component.literal(tank.getFluidAmount() + "/" + tank.getCapacity() + "mB"));
		return tooltip;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);

		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		drawFluidTank(this, g, this.tile.inputTank, k + INPUT_TANK_X + 1, l + TANK_Y, TANK_W, TANK_H);
		drawFluidTank(this, g, this.tile.outputTank, k + OUTPUT_TANK_X + 1, l + TANK_Y, TANK_W, TANK_H);

		g.blit(texture, k + INPUT_TANK_X, l + TANK_Y, 176, 32, TANK_W + 2, TANK_H + 2);
		g.blit(texture, k + OUTPUT_TANK_X, l + TANK_Y, 176, 32, TANK_W + 2, TANK_H + 2);

		if (this.tile.isWorking()) {

			float prog = this.tile.getProgress();
			int i1 = (int) ((prog < 0.2f ? (prog * 5) : 1.0f) * 25);
			g.blit(texture, k + 81, l + 19, 178, 5, 8, i1);

			if (prog >= 0.2f) {
				int i2 = (int) ((prog < 0.4f ? (prog - 0.2f) * 5 : 1.0f) * 9);
				g.blit(texture, k + 88, l + 39, 186, 25, i2, 1);
			}

			if (prog >= 0.4f) {
				int i3 = (int) ((prog < 0.6f ? (prog - 0.4f) * 5 : 1.0f) * 20);
				g.blit(texture, k + 97, l + 23 + (20 - i3), 194, 9 + (20 - i3), 14, i3);
			}

			if (prog >= 0.6f) {
				int i4 = (int) ((prog < 0.8f ? (prog - 0.6f) * 5 : 1.0f) * 16);
				g.blit(texture, k + 108, l + 17, 205, 3, i4, 6);
			}
			if (prog >= 0.8f) {
				int i5 = (int) (((prog - 0.8f) * 5) * 24);
				g.blit(texture, k + 117, l + 20, 214, 6, 14, i5 + 1);
			}
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		int mx = mouseX - (this.width - this.imageWidth) / 2;
		int my = mouseY - (this.height - this.imageHeight) / 2;

		if (isInRect(mx, my, INPUT_TANK_X, TANK_Y - 1, TANK_W + 1, TANK_H + 1)) {

			this.drawHoveringTextComponents(getTankTooltip(this.tile.inputTank), mx, my);

		} else if (isInRect(mx, my, OUTPUT_TANK_X, TANK_Y - 1, TANK_W + 1, TANK_H + 1)) {

			this.drawHoveringTextComponents(getTankTooltip(this.tile.outputTank), mx, my);

		} else if (isInRect(mx, my, 20, 7, 8, 8)) {

			List<String> tooltip = new ArrayList<String>();
			tooltip.add(TextUtil.trans("techguns.chemlab.dumpinput"));
			tooltip.add(TextUtil.trans("techguns.chemlab.dumpinput.tooltip"));
			this.drawHoveringText(tooltip, mx, my);

		} else if (isInRect(mx, my, 159, 7, 8, 8)) {

			List<String> tooltip = new ArrayList<String>();
			tooltip.add(TextUtil.trans("techguns.chemlab.dumpoutput"));
			tooltip.add(TextUtil.trans("techguns.chemlab.dumpoutput.tooltip"));
			this.drawHoveringText(tooltip, mx, my);

		} else if (isInRect(mx, my, 156, 68, 14, 14)) {

			List<String> tooltip = new ArrayList<String>();
			tooltip.add(TextUtil.trans("techguns.chemlab.toogledrain"));
			tooltip.add(TextUtil.trans("techguns.chemlab.toogledrain.tooltip." + tile.getDrainMode()));
			this.drawHoveringText(tooltip, mx, my);
		}
	}

	@Override
	protected void init() {
		super.init();
		this.addRenderableWidget(new GuiButtonExt(ChemLabTileEnt.BUTTON_ID_DUMP_INPUT, this.leftPos + 20, this.topPos + 7, 8, 8, "x", b -> this.actionPerformed(ChemLabTileEnt.BUTTON_ID_DUMP_INPUT)));
		this.addRenderableWidget(new GuiButtonExt(ChemLabTileEnt.BUTTON_ID_DUMP_OUTPUT, this.leftPos + 159, this.topPos + 7, 8, 8, "x", b -> this.actionPerformed(ChemLabTileEnt.BUTTON_ID_DUMP_OUTPUT)));
		this.addRenderableWidget(new GuiButtonToggleDrainTank(ChemLabTileEnt.BUTTON_ID_TOGGLE_DRAIN, this.leftPos + 156, this.topPos + 68, 14, 14, this.tile, b -> this.actionPerformed(ChemLabTileEnt.BUTTON_ID_TOGGLE_DRAIN)));
	}

}
