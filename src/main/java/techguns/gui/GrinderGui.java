package techguns.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.Techguns;
import techguns.gui.containers.GrinderContainer;
import techguns.tileentities.GrinderTileEnt;

public class GrinderGui extends PoweredTileEntGui<GrinderContainer> {
	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/grinder_gui.png");

	protected GrinderTileEnt tile;

	public GrinderGui(GrinderContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getGrinder());
		this.tile = container.getGrinder();
		this.tex = texture;
		this.upgradeSlotX = GrinderContainer.SLOT_UPGRADE_X - 1;
		this.upgradeSlotY = GrinderContainer.SLOT_UPGRADE_Y - 1;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		if (this.tile.isWorking()) {
			int i1 = this.tile.getProgressScaled(21);
			g.blit(texture, k + 31, l + 39, 0, 167, i1 + 1, 22);

			int i2 = 20 - i1;
			g.blit(texture, k + 31 + 20 + i2, l + 39, 0 + i2, 167, i1 + 1, 22);
		}
	}
}
