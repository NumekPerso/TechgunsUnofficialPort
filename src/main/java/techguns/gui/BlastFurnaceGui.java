package techguns.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.Techguns;
import techguns.gui.containers.BlastFurnaceContainer;
import techguns.tileentities.BlastFurnaceTileEnt;

public class BlastFurnaceGui extends PoweredTileEntGui<BlastFurnaceContainer> {

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/blast_furnace_gui.png");

	protected BlastFurnaceTileEnt tile;

	public BlastFurnaceGui(BlastFurnaceContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getBlastFurnace());
		this.tile = container.getBlastFurnace();
		this.tex = texture;
		this.upgradeSlotX = BlastFurnaceContainer.SLOT_UPGRADE_X - 1;
		this.upgradeSlotY = BlastFurnaceContainer.SLOT_UPGRADE_Y - 1;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		if (this.tile.isWorking()) {
			int i1 = this.tile.getProgressScaled(90);
			g.blit(texture, k + 19, l + 53, 0, 167, i1 + 1, 11);
		}
	}

}
