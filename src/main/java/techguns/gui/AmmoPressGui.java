package techguns.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.TGItems;
import techguns.Techguns;
import techguns.gui.containers.AmmoPressContainer;
import techguns.gui.widgets.GuiButtonExt;
import techguns.tileentities.AmmoPressTileEnt;

public class AmmoPressGui extends PoweredTileEntGui<AmmoPressContainer> {
	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/ammo_press_gui.png");

	protected AmmoPressTileEnt tileent;

	public AmmoPressGui(AmmoPressContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getAmmoPress());
		this.tileent = container.getAmmoPress();
		this.tex = texture;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {

		int x = 0;
		int y = 0;
		int color = 4210752; //0xff101010;

		Component s2 = Component.empty();
		switch (this.tileent.buildPlan) {
		case 0:
			s2 = TGItems.PISTOL_ROUNDS.getDescription();
			break;
		case 1:
			s2 = TGItems.SHOTGUN_ROUNDS.getDescription();
			break;
		case 2:
			s2 = TGItems.RIFLE_ROUNDS.getDescription();
			break;
		case 3:
			s2 = TGItems.SNIPER_ROUNDS.getDescription();
			break;
		}
		g.drawString(this.font, Component.translatable(Techguns.MODID + ".gui.ammopress.build").append(":"), x + 20, y + 30, color, false);
		g.drawString(this.font, s2, x + 20, y + 40, color, false);
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);

		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		if (this.tileent.isWorking()) {
			int i1 = this.tileent.getProgressScaled(21);
			g.blit(texture, k + 119, l + 36, 176, 0, 19, i1 + 1);
		}

	}

	@Override
	protected void init() {
		super.init();
		//id, x, y, width, height, text
		this.addRenderableWidget(new GuiButtonExt(AmmoPressTileEnt.BUTTON_ID_NEXT, this.leftPos + 40, this.topPos + 50, 20, 20, ">", b -> this.actionPerformed(AmmoPressTileEnt.BUTTON_ID_NEXT)));
		this.addRenderableWidget(new GuiButtonExt(AmmoPressTileEnt.BUTTON_ID_PREV, this.leftPos + 20, this.topPos + 50, 20, 20, "<", b -> this.actionPerformed(AmmoPressTileEnt.BUTTON_ID_PREV)));
	}

}
