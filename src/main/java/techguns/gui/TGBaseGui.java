package techguns.gui;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import techguns.gui.containers.TGBaseContainer;
import techguns.gui.widgets.ITGSlotBackground;
import techguns.tileentities.BasicInventoryTileEnt;
import techguns.util.TextUtil;

/**
 * Base screen of the Techguns machine GUIs (1.12 TGBaseGui extends GuiContainer). Client only.
 * The 1.12 method names are kept as hooks:
 * - drawGuiContainerBackgroundLayer: background, absolute coordinates (k = leftPos, l = topPos)
 * - drawGuiContainerForegroundLayer: labels, coordinates relative to the GUI, mouse coordinates are absolute
 * - drawHoveringText: tooltip with coordinates relative to the GUI (drawn after everything else)
 */
public abstract class TGBaseGui<T extends TGBaseContainer> extends AbstractContainerScreen<T> {
	protected ResourceLocation tex;

	protected boolean showInventoryText = true;

	protected List<Component> pendingTooltip = null;
	protected int pendingTooltipX;
	protected int pendingTooltipY;

	public TGBaseGui(T inventorySlotsIn, Inventory inv, Component title) {
		super(inventorySlotsIn, inv, title);
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
		this.pendingTooltip = null;
		this.renderBackground(g);
		super.render(g, mouseX, mouseY, partialTicks);
		this.renderTooltip(g, mouseX, mouseY);
		if (this.pendingTooltip != null && this.menu.getCarried().isEmpty()) {
			g.renderComponentTooltip(this.font, this.pendingTooltip, this.pendingTooltipX + this.leftPos, this.pendingTooltipY + this.topPos);
		}
	}

	public static boolean isInRect(int mouseX, int mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
	}

	public static String trans(String key) {
		return TextUtil.trans("techguns." + key);
	}

	/**
	 * Queue a tooltip (lines are plain strings), coordinates relative to the GUI like 1.12 drawHoveringText in the foreground layer
	 */
	public void drawHoveringText(List<String> lines, int mx, int my) {
		List<Component> list = new ArrayList<>();
		lines.forEach(s -> list.add(Component.literal(s)));
		this.drawHoveringTextComponents(list, mx, my);
	}

	public void drawHoveringText(String line, int mx, int my) {
		List<String> list = new ArrayList<>();
		list.add(line);
		this.drawHoveringText(list, mx, my);
	}

	public void drawHoveringTextComponents(List<Component> lines, int mx, int my) {
		this.pendingTooltip = lines;
		this.pendingTooltipX = mx;
		this.pendingTooltipY = my;
	}

	public void drawInventoryContainerName(GuiGraphics g, BasicInventoryTileEnt tileent) {
		int color = 4210752; //0xff101010;

		if (this.showInventoryText) {
			g.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, color, false);
		}

		Component s = tileent.getDisplayName();
		g.drawString(this.font, s, this.imageWidth / 2 - this.font.width(s) / 2, 6, color, false);
	}

	@Override
	protected final void renderBg(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		this.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);
		this.drawSlotBackgrounds(g);
	}

	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;
		g.blit(tex, k, l, 0, 0, this.imageWidth, this.imageHeight);
	}

	/**
	 * Draws the icon of empty slots (1.12 Slot.getSlotTexture)
	 */
	protected void drawSlotBackgrounds(GuiGraphics g) {
		for (Slot slot : this.menu.slots) {
			if (slot instanceof ITGSlotBackground bg && !slot.hasItem() && slot.isActive()) {
				ResourceLocation bgtex = bg.getBackgroundTexture();
				if (bgtex != null) {
					ITGSlotBackground.drawIcon(g, bgtex, this.leftPos + slot.x, this.topPos + slot.y);
				}
			}
		}
	}

	@Override
	protected final void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
		this.drawGuiContainerForegroundLayer(g, mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
	}

	/**
	 * Draws a part of a sprite of the block atlas, filling a tank from the bottom (1.12 drawFluidWithTesselator).
	 * Absolute coordinates.
	 */
	protected void drawFluidWithTesselator(GuiGraphics g, TextureAtlasSprite tex, int x, int y, int width, int height, int px) {
		int offset = 0;

		if (tex == null) {
			return;
		}

		int len = tex.contents().height();
		while (offset < px) {
			if (offset + len > px) {
				len = px - offset;
			}
			this.drawTexturedModelRectFromIconFluidTank(g, x, y + height - offset - len, tex, width, len);
			offset += tex.contents().height();
		}
	}

	protected void drawTexturedModelRectFromIconFluidTank(GuiGraphics g, int x, int y, TextureAtlasSprite icon, int w, int h) {
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		Matrix4f matrix = g.pose().last().pose();
		int iconHeight = icon.contents().height();
		float v0 = icon.getV0() + (icon.getV1() - icon.getV0()) * (float) (iconHeight - h) / (float) iconHeight;
		BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(matrix, (float) (x + 0), (float) (y + h), 0f).uv(icon.getU0(), icon.getV1()).endVertex();
		buffer.vertex(matrix, (float) (x + w), (float) (y + h), 0f).uv(icon.getU1(), icon.getV1()).endVertex();
		buffer.vertex(matrix, (float) (x + w), (float) (y + 0), 0f).uv(icon.getU1(), v0).endVertex();
		buffer.vertex(matrix, (float) (x + 0), (float) (y + 0), 0f).uv(icon.getU0(), v0).endVertex();
		Tesselator.getInstance().end();
	}

	/**
	 * Draws a textured rectangle of the current gui texture (256x256) with a color (0-255 values)
	 */
	public void drawTexturedModalRect(GuiGraphics g, ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height, int r, int gr, int b, int a) {
		RenderSystem.setShaderColor(r / 255f, gr / 255f, b / 255f, a / 255f);
		RenderSystem.enableBlend();
		g.blit(texture, x, y, textureX, textureY, width, height);
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}

}
