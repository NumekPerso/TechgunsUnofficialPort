package techguns.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.Techguns;
import techguns.gui.containers.TurretContainer;
import techguns.gui.widgets.GuiButtonPvPSetting;
import techguns.gui.widgets.GuiButtonTargetAnimals;
import techguns.tileentities.TurretTileEnt;
import techguns.util.TextUtil;

public class TurretGui extends PoweredTileEntGui<TurretContainer> {
	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/turret_base_gui.png");

	protected TurretTileEnt tile;

	public TurretGui(TurretContainer container, Inventory player, Component title) {
		super(container, player, title, container.getTurret());
		this.tile = container.getTurret();
		this.tex = texture;
		this.hasUpgradeSlot = false;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);

		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		//draw animal targeting button background:
		g.blit(security_texture, k - 22 - 5, l + 70 - 5, 195, 0, 22 + 5, 30);

		//draw pvp targeting button background:
		g.blit(security_texture, k - 22 - 5, l + 100 - 5, 195, 0, 22 + 5, 30);

		if (this.tile.turretDeath) {
			int i1 = this.tile.getRepairTimeScaled(52);
			g.blit(texture, k + 116, l + 74, 177, 1, i1 + 1, 4);
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		int mx = mouseX - (this.width - this.imageWidth) / 2;
		int my = mouseY - (this.height - this.imageHeight) / 2;

		if (isInRect(mx, my, 115, 73, 53, 5)) {
			if (this.tile.turretDeath) {
				this.drawHoveringText(TextUtil.trans("techguns.container.repair") + " " + this.tile.repairTime / 20 + "s", mx, my);
			} else {
				if (tile.mountedTurret != null) {
					this.drawHoveringText(TextUtil.trans("techguns.turret.health") + ": " + tile.mountedTurret.getHealth() + "/" + tile.mountedTurret.getMaxHealth(), mx, my);
				}
			}
		}

		if (isInRect(mx, my, -22, 70, 20, 20)) {
			this.drawHoveringText(TextUtil.trans("techguns.turret.targetAnimals") + ": " + (this.tile.attackAnimals ? ChatFormatting.RED + TextUtil.trans("techguns.yes") : ChatFormatting.GREEN + TextUtil.trans("techguns.no")), mx, my);
		} else if (isInRect(mx, my, -22, 100, 20, 20)) {
			List<String> tooltip = new ArrayList<String>(2);
			tooltip.add(TextUtil.trans("techguns.turret.pvpsetting") + ": " + TextUtil.trans("techguns.turret.pvpsetting." + tile.getPvpSetting()));
			tooltip.add(TextUtil.trans("techguns.container.security.tooltip.descr2"));
			this.drawHoveringText(tooltip, mx, my);
		}
	}

	@Override
	protected void init() {
		super.init();
		this.addRenderableWidget(new GuiButtonTargetAnimals(TurretTileEnt.BUTTON_ID_TARGET_ANIMALS, this.leftPos - 22, this.topPos + 70, 20, 20, this.tile, b -> this.actionPerformed(TurretTileEnt.BUTTON_ID_TARGET_ANIMALS)));
		this.addRenderableWidget(new GuiButtonPvPSetting(TurretTileEnt.BUTTON_ID_PVP_SETTING, this.leftPos - 22, this.topPos + 100, 20, 20, this.tile, b -> this.actionPerformed(TurretTileEnt.BUTTON_ID_PVP_SETTING)));
	}

}
