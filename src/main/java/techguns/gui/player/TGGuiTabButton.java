package techguns.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import techguns.TGPackets;
import techguns.packets.PacketOpenPlayerGUI;

/**
 * Inventory tab button (merge of the 1.12 TGGuiTabButton and the Galacticraft API AbstractTab that 1.12 used for the tabs).
 * GUI_ID &lt; 0: vanilla inventory, otherwise the player gui with that id is opened by the server (0 = Techguns inventory).
 */
public class TGGuiTabButton extends AbstractButton {
	private static final ResourceLocation tabs_texture = new ResourceLocation("minecraft", "textures/gui/container/creative_inventory/tabs.png");

	public static final int TAB_WIDTH = 26;
	public static final int TAB_HEIGHT = 32;

	ItemStack iconItem;
	private final short GUI_ID;
	/**
	 * false for the tab of the current screen (1.12 GuiButton.enabled)
	 */
	public boolean tabEnabled;
	/**
	 * column of the tab, the first one uses the tab sprite with the left border
	 */
	public int column;

	public TGGuiTabButton(int column, int xPos, int yPos, boolean enabled, ItemStack iconItem, int guid) {
		super(xPos, yPos, TAB_WIDTH, TAB_HEIGHT - 4, Component.empty());
		this.column = column;
		this.tabEnabled = enabled;
		this.iconItem = iconItem;
		this.GUI_ID = (short) guid;
	}

	@Override
	public void onPress() {
		if (!this.tabEnabled) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (GUI_ID < 0) {
			//1.12: CPacketCloseWindow + display GuiInventory. LocalPlayer.closeContainer() would also set the screen to null:
			//the mouse gets grabbed (cursor centered) before the inventory screen releases it again
			mc.player.connection.send(new ServerboundContainerClosePacket(mc.player.containerMenu.containerId));
			mc.player.containerMenu = mc.player.inventoryMenu;
			mc.setScreen(new InventoryScreen(mc.player));
		} else {
			TGPackets.network.sendToServer(new PacketOpenPlayerGUI(GUI_ID));
		}
	}

	@Override
	public void playDownSound(SoundManager handler) {
		if (this.tabEnabled) {
			super.playDownSound(handler);
		}
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.enableBlend();
		int u = this.column == 0 ? 0 : TAB_WIDTH;
		if (this.tabEnabled) {
			//not selected: don't draw the bottom part, it would cover the gui border
			g.blit(tabs_texture, this.getX(), this.getY(), u, 0, TAB_WIDTH, 28);
		} else {
			//selected tab overlaps the gui border
			g.blit(tabs_texture, this.getX(), this.getY(), u, 32, TAB_WIDTH, TAB_HEIGHT);
		}
		g.pose().pushPose();
		g.pose().translate(0.0F, 0.0F, 100.0F);
		g.renderItem(this.iconItem, this.getX() + 5, this.getY() + 9);
		g.pose().popPose();
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}

}
