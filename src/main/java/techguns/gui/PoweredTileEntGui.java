package techguns.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.TGConfig;
import techguns.Techguns;
import techguns.gui.containers.TGBaseContainer;
import techguns.tileentities.BasicPoweredTileEnt;
import techguns.util.TextUtil;

public abstract class PoweredTileEntGui<T extends TGBaseContainer> extends RedstoneTileEntGui<T> {
	public static final String POWER_UNIT = "FE";

	protected BasicPoweredTileEnt poweredTileEnt;
	public static final ResourceLocation power_texture = new ResourceLocation(Techguns.MODID, "textures/gui/ammo_press_gui.png");

	protected boolean hasUpgradeSlot = true;

	protected int upgradeSlotX = 151;
	protected int upgradeSlotY = 59;

	public PoweredTileEntGui(T container, Inventory inv, Component title, BasicPoweredTileEnt tile) {
		super(container, inv, title, tile);
		this.poweredTileEnt = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);

		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		//draw power bar
		int max = this.poweredTileEnt.getEnergyStorage().getMaxEnergyStored();
		int power = max > 0 ? (int) ((long) this.poweredTileEnt.getEnergyStorage().getEnergyStored() * 48 / max) : 0;
		int diff = 48 - power;
		if (TGConfig.machinesNeedNoPower) {
			diff = 0;
		}
		g.blit(power_texture, k + 8, l + 17 + diff, 251, 1 + diff, 4, 48 - diff);

		//draw upgrade slot
		if (hasUpgradeSlot) {
			g.blit(power_texture, k + upgradeSlotX, l + upgradeSlotY, 119, 59, 20, 20);
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		int mx = mouseX - (this.width - this.imageWidth) / 2;
		int my = mouseY - (this.height - this.imageHeight) / 2;

		if (isInRect(mx, my, 7, 17, 5, 49)) {
			if (TGConfig.machinesNeedNoPower) {
				this.drawHoveringText(TextUtil.trans("techguns.container.power"), mx, my);
			} else {
				this.drawHoveringText(TextUtil.trans("techguns.container.power") + ": " + this.poweredTileEnt.getEnergyStorage().getEnergyStored() + "/" + this.poweredTileEnt.getEnergyStorage().getMaxEnergyStored() + " " + POWER_UNIT, mx, my);
			}
		}
	}

}
