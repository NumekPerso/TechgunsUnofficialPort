package techguns.gui;

import static techguns.gui.ButtonConstants.BUTTON_ID_SECURITY;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.gui.containers.TGBaseContainer;
import techguns.gui.widgets.GuiButtonSecurity;
import techguns.packets.PacketGuiButtonClick;
import techguns.tileentities.BasicOwnedTileEnt;
import techguns.util.TextUtil;

public abstract class OwnedTileEntGui<T extends TGBaseContainer> extends TGBaseGui<T> {

	protected BasicOwnedTileEnt ownedTile;

	protected int heightSecurityButton = 40;

	public static final ResourceLocation security_texture = new ResourceLocation(Techguns.MODID, "textures/gui/ammo_press_gui.png");

	public OwnedTileEntGui(T inventorySlotsIn, Inventory inv, Component title, BasicOwnedTileEnt tile) {
		super(inventorySlotsIn, inv, title);
		this.ownedTile = tile;
	}

	/**
	 * 1.12 ClientProxy.resolvePlayerNameFromUUID: name of an online player, else the UUID
	 */
	public static String resolvePlayerNameFromUUID(UUID uuid) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.getConnection() != null) {
			PlayerInfo info = mc.getConnection().getPlayerInfo(uuid);
			if (info != null) {
				return info.getProfile().getName();
			}
		}
		if (mc.player != null && uuid.equals(mc.player.getUUID())) {
			return mc.player.getGameProfile().getName();
		}
		return uuid.toString();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		this.drawInventoryContainerName(g, ownedTile);

		int mx = mouseX - (this.width - this.imageWidth) / 2;
		int my = mouseY - (this.height - this.imageHeight) / 2;

		if (isInRect(mx, my, -22, heightSecurityButton, 20, 20)) {
			List<String> tooltip = new ArrayList<String>();
			String color = "";
			byte type = ownedTile.getSecurity();
			switch (type) {
			case 0:
				color += ChatFormatting.GREEN;
				break;
			case 1:
				color += ChatFormatting.YELLOW;
				break;
			case 2:
				color += ChatFormatting.GOLD;
				break;
			case 3:
				color += ChatFormatting.RED;
				break;
			}

			String own = TextUtil.trans("techguns.container.security.tooltip.unowned");
			UUID owner = ownedTile.getOwner();
			if (owner != null) {
				own = TextUtil.trans("techguns.container.security.tooltip.owner") + ": " + resolvePlayerNameFromUUID(owner);
			}

			tooltip.add(TextUtil.trans("techguns.container.security.owner") + ": " + own);
			tooltip.add(TextUtil.trans("techguns.container.security.tooltip") + ": " + color + TextUtil.trans("techguns.container.security.tooltip." + type));
			tooltip.add(TextUtil.trans("techguns.container.security.tooltip.description." + type));
			tooltip.add(TextUtil.trans("techguns.container.security.tooltip.descr2"));
			this.drawHoveringText(tooltip, mx, my);
		}
	}

	/**
	 * Sends the button click to the server tile entity (1.12 actionPerformed)
	 */
	protected void actionPerformed(int buttonId) {
		TGPackets.network.sendToServer(new PacketGuiButtonClick(this.ownedTile, buttonId));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		//draw security button background
		g.blit(security_texture, k - 22 - 5, l + heightSecurityButton - 5, 195, 0, 22 + 5, 30);
	}

	@Override
	protected void init() {
		super.init();
		this.addRenderableWidget(new GuiButtonSecurity(BUTTON_ID_SECURITY, this.leftPos - 22, this.topPos + heightSecurityButton, 20, 20, "", ownedTile, b -> this.actionPerformed(BUTTON_ID_SECURITY)));
	}
}
