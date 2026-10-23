package techguns.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.Techguns;
import techguns.gui.containers.ReactionChamberContainer;
import techguns.gui.widgets.GuiButtonExt;
import techguns.tileentities.ReactionChamberTileEntMaster;
import techguns.tileentities.operation.ReactionChamberOperation;
import techguns.util.TextUtil;

public class ReactionChamberGui extends PoweredTileEntGui<ReactionChamberContainer> {
	ReactionChamberTileEntMaster tile;

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/reaction_chamber_gui.png");

	public ReactionChamberGui(ReactionChamberContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getReactionChamber());
		this.tile = container.getReactionChamber();
		this.tex = texture;
		this.hasUpgradeSlot = false;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);

		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		ChemLabGui.drawFluidTank(this, g, this.tile.inputTank, k + ChemLabGui.INPUT_TANK_X + 1, l + ChemLabGui.TANK_Y, ChemLabGui.TANK_W, ChemLabGui.TANK_H);

		int px_height = 50 - this.tile.getLiquidLevel() * 5;

		g.blit(texture, k + 18, l + 17, 176, 32, 12, 52);

		g.blit(texture, k + 19, l + 17 + px_height, 177, 32, 7, 1);

		ReactionChamberOperation reaction = this.tile.getCurrentReaction();
		if (this.tile.isWorking() && reaction != null && reaction.getRecipe() != null) {

			int compl = reaction.completion;
			int req_compl = reaction.getRecipe().requiredCompletion;

			int compl_px = Math.round(((compl * 1.0f) / (req_compl * 1.0f)) * 100f);

			int dur = this.tile.progress;
			int maxdur = this.tile.totaltime;

			int dur_px = maxdur > 0 ? Math.round(((dur * 1.0f) / (maxdur * 1.0f)) * 100f) : 0;

			g.blit(texture, k + 67, l + 61, 0, 167, compl_px, 4);

			g.blit(texture, k + 67, l + 69, 0, 175, dur_px, 4);

			//draw intensity
			int px = (10 - this.tile.getIntensity()) * 4;

			if (reaction.required_intensity != this.tile.getIntensity()) {
				g.blit(texture, k + 114, l + 16 + px, 198, px, 5, 40 - px);
			} else {
				g.blit(texture, k + 114, l + 16 + px, 190, px, 5, 40 - px);
			}

			int h = 40 - reaction.required_intensity * 4;
			g.blit(texture, k + 112, l + 15 + h, 178, 22, 5, 3);

		} else {
			//draw intensity
			int px = (10 - this.tile.getIntensity()) * 4;
			g.blit(texture, k + 114, l + 16 + px, 190, px, 5, 40 - px);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		int mx = mouseX - (this.width - this.imageWidth) / 2;
		int my = mouseY - (this.height - this.imageHeight) / 2;

		int fluidLevel = this.tile.getLiquidLevel() * 10;
		int intensity = this.tile.getIntensity();

		g.drawString(this.font, fluidLevel + "%", 34, 50, 4210752, false);
		g.drawString(this.font, intensity + "", 121, 18, 4210752, false);

		if (isInRect(mx, my, 18, 16, 11, 51)) {

			this.drawHoveringTextComponents(ChemLabGui.getTankTooltip(this.tile.inputTank), mx, my);

		} else if (isInRect(mx, my, 20, 7, 8, 8)) {
			List<String> tooltip = new ArrayList<>();
			tooltip.add(TextUtil.trans("techguns.chemlab.dumpinput"));
			tooltip.add(TextUtil.trans("techguns.chemlab.dumpinput.tooltip"));

			this.drawHoveringText(tooltip, mx, my);
		}

	}

	@Override
	protected void init() {
		super.init();

		int left = 33;
		this.addRenderableWidget(new GuiButtonExt(ReactionChamberTileEntMaster.BUTTON_ID_LIQUIDLEVEL_INC, this.leftPos + left, this.topPos + 58, 10, 10, "+", b -> this.actionPerformed(ReactionChamberTileEntMaster.BUTTON_ID_LIQUIDLEVEL_INC)));
		this.addRenderableWidget(new GuiButtonExt(ReactionChamberTileEntMaster.BUTTON_ID_LIQUIDLEVEL_DEC, this.leftPos + left + 10, this.topPos + 58, 10, 10, "-", b -> this.actionPerformed(ReactionChamberTileEntMaster.BUTTON_ID_LIQUIDLEVEL_DEC)));

		left = 91;
		this.addRenderableWidget(new GuiButtonExt(ReactionChamberTileEntMaster.BUTTON_ID_INTENSITY_INC, this.leftPos + left, this.topPos + 36, 10, 10, "+", b -> this.actionPerformed(ReactionChamberTileEntMaster.BUTTON_ID_INTENSITY_INC)));
		this.addRenderableWidget(new GuiButtonExt(ReactionChamberTileEntMaster.BUTTON_ID_INTENSTIY_DEC, this.leftPos + left + 10, this.topPos + 36, 10, 10, "-", b -> this.actionPerformed(ReactionChamberTileEntMaster.BUTTON_ID_INTENSTIY_DEC)));

		this.addRenderableWidget(new GuiButtonExt(ReactionChamberTileEntMaster.BUTTON_ID_DUMPTANK, this.leftPos + 20, this.topPos + 7, 8, 8, "x", b -> this.actionPerformed(ReactionChamberTileEntMaster.BUTTON_ID_DUMPTANK)));
	}

}
