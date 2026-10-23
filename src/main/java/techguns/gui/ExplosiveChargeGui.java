package techguns.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.gui.containers.ExplosiveChargeContainer;
import techguns.gui.widgets.GuiButtonExt;
import techguns.packets.PacketGuiButtonClick;
import techguns.tileentities.ExplosiveChargeAdvTileEnt;
import techguns.tileentities.ExplosiveChargeTileEnt;

public class ExplosiveChargeGui extends TGBaseGui<ExplosiveChargeContainer> {

	protected static final int FIRST_BUTTON_ID = 0;

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/tnt_charge_gui.png");
	public static final ResourceLocation texture2 = new ResourceLocation(Techguns.MODID, "textures/gui/exp_charge_gui.png");

	protected byte guiType = 0;

	protected ExplosiveChargeTileEnt tileent;

	public ExplosiveChargeGui(ExplosiveChargeContainer container, Inventory ply, Component title) {
		super(container, ply, title);
		this.tileent = container.getCharge();
		if (tileent instanceof ExplosiveChargeAdvTileEnt) {
			this.tex = texture2;
		} else {
			this.tex = texture;
		}
		this.showInventoryText = false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		int radius = tileent.getBlastradius();
		int blastlength = tileent.getBlastlength();
		int fusetime = tileent.getFusetime();

		int color = 4210752;

		String sradius = "Radius: " + radius;
		g.drawString(this.font, sradius, 37, 40, color, false);
		String slength = "Length: " + blastlength;
		g.drawString(this.font, slength, 37, 50, color, false);
		String sfusetime = "Delay: " + fusetime / 20 + "s";
		g.drawString(this.font, sfusetime, 37, 60, color, false);
	}

	@Override
	protected void init() {
		super.init();
		int buttonid = FIRST_BUTTON_ID;
		this.addButton(buttonid++, this.leftPos + 57, this.topPos + 73, 60, 20, "Detonate");

		int left = 120;
		this.addButton(buttonid++, this.leftPos + left, this.topPos + 40, 10, 10, "+");
		this.addButton(buttonid++, this.leftPos + left + 10, this.topPos + 40, 10, 10, "-");

		this.addButton(buttonid++, this.leftPos + left, this.topPos + 50, 10, 10, "+");
		this.addButton(buttonid++, this.leftPos + left + 10, this.topPos + 50, 10, 10, "-");

		this.addButton(buttonid++, this.leftPos + left, this.topPos + 60, 10, 10, "+");
		this.addButton(buttonid++, this.leftPos + left + 10, this.topPos + 60, 10, 10, "-");
	}

	protected void addButton(int id, int x, int y, int w, int h, String text) {
		this.addRenderableWidget(new GuiButtonExt(id, x, y, w, h, text, b -> this.actionPerformed(id)));
	}

	protected void actionPerformed(int id) {
		TGPackets.network.sendToServer(new PacketGuiButtonClick(this.tileent, id));
		if (id == FIRST_BUTTON_ID) {
			Minecraft.getInstance().player.closeContainer();
		}
	}

}
