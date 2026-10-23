package techguns.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.Techguns;
import techguns.gui.containers.FabricatorContainer;
import techguns.tileentities.FabricatorTileEntMaster;

public class FabricatorGui extends PoweredTileEntGui<FabricatorContainer> {

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/fabricator_gui.png");

	protected FabricatorTileEntMaster tile;

	public FabricatorGui(FabricatorContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getFabricator());
		this.tile = container.getFabricator();
		this.tex = texture;
		this.upgradeSlotX = FabricatorContainer.SLOT_UPGRADE_X - 1;
		this.upgradeSlotY = FabricatorContainer.SLOT_UPGRADE_Y - 1;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		if (this.tile.isWorking()) {
			int i1 = this.tile.getProgressScaled(90);
			g.blit(texture, k + 21, l + 53, 0, 167, i1 + 1, 11);
		}

	}

}
