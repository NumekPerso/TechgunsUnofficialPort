package techguns.gui;

import static techguns.gui.ButtonConstants.BUTTON_ID_SECURITY;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import techguns.Techguns;
import techguns.capabilities.TGExtendedPlayer;
import techguns.gui.containers.CamoBenchContainer;
import techguns.gui.player.SlotTG;
import techguns.gui.player.TGPlayerInventory;
import techguns.gui.widgets.GuiButtonExt;
import techguns.items.armors.ICamoChangeable;
import techguns.tileentities.CamoBenchTileEnt;
import techguns.tileentities.operation.CamoBenchRecipes;
import techguns.tileentities.operation.CamoBenchRecipes.CamoBenchRecipe;
import techguns.util.TextUtil;

public class CamoBenchGui extends OwnedTileEntGui<CamoBenchContainer> {

	public static final ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/camo_bench_gui.png");

	protected CamoBenchTileEnt tileent;

	public CamoBenchGui(CamoBenchContainer container, Inventory ply, Component title) {
		super(container, ply, title, container.getCamoBench());
		this.tileent = container.getCamoBench();
		this.tex = texture;
		this.heightSecurityButton = 95 + 25;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(g, partialTicks, mouseX, mouseY);

		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;

		//security texture (from super.draw....)
		g.blit(security_texture, k - 22, l + 90, 195, 0, 22, 20);
		g.blit(security_texture, k - 22, l + 90 + 20, 195, 25, 22, 5);

		g.blit(texture, k - 45, l + 6, 178, 0, 45, 84);

		//draw offhand slotbg
		g.blit(texture, k - 18, l + 93, 7, 83, 18, 18);

		//empty slot icons of the Techguns slots
		for (Slot slot : this.menu.slots) {
			if (slot instanceof SlotTG stg && !slot.hasItem()) {
				ResourceLocation slottex = stg.getEmptySlotTexture();
				if (slottex != null) {
					g.blit(slottex, k + slot.x, l + slot.y, 0, 0, 16, 16, 16, 16);
				}
			}
		}

		int left = this.leftPos;
		int top = this.topPos;

		InventoryScreen.renderEntityInInventoryFollowsMouse(g, left - 20, top + 77, 30, (float) (left - 20) - mouseX, (float) (top + 77 - 50) - mouseY, this.minecraft.player);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);

		int x = 0;
		int y = 0;
		int color = 4210752; //0xff101010;

		Component s2 = Component.literal(TextUtil.trans("techguns.item.invalidcamo"));
		String s3 = "";

		ItemStack item = tileent.getItem();
		if (!item.isEmpty()) {
			if (item.getItem() instanceof ICamoChangeable) {
				ICamoChangeable camoitem = (ICamoChangeable) item.getItem();
				s2 = camoitem.getCurrentCamoName(item);
				s3 += (camoitem.getCurrentCamoIndex(item) + 1) + "/" + (camoitem.getCamoCount()) + ":";
			} else {
				CamoBenchRecipe r = CamoBenchRecipes.getRecipeFor(item.getItem());
				if (r != null) {
					int i = r.getCamoIndex(item) + 1;
					s3 += (i > 9 ? "" : " ") + i + "/" + r.getCamoCount();
					s2 = Component.empty();
				}
			}
		}

		g.drawString(this.font, s3, x + 10, y + 40, color, false);
		g.drawString(this.font, s2, x + 10, y + 50, color, false);

		for (int i = 0; i < 4; i++) {
			g.drawString(this.font, "" + getArmorCamo(3 - i), 104 + (i * 18), 38, color, false);
		}

		g.drawString(this.font, "" + getExtendedInvItemCamo(TGPlayerInventory.SLOT_FACE), 49, 38, color, false);
		g.drawString(this.font, "" + getExtendedInvItemCamo(TGPlayerInventory.SLOT_BACK), 68, 38, color, false);
		g.drawString(this.font, "" + getExtendedInvItemCamo(TGPlayerInventory.SLOT_HAND), 86, 38, color, false);
	}

	@Override
	protected void init() {
		super.init();

		int id = BUTTON_ID_SECURITY + 1;
		//id, x, y, width, height, text
		this.addCamoButton(id++, this.leftPos + 14 + 12, this.topPos + 59, 12, 12, ">");
		this.addCamoButton(id++, this.leftPos + 14, this.topPos + 59, 12, 12, "<");

		int posX = 102;
		int offsetX = 18;
		int offsetY = 59;

		for (int i = 0; i < 4; i++) {
			this.addCamoButton(id++, this.leftPos + posX + offsetX * i, this.topPos + offsetY, 10, 10, "+");
			this.addCamoButton(id++, this.leftPos + posX + offsetX * i, this.topPos + offsetY + 10, 10, 10, "-");
		}
	}

	protected void addCamoButton(int id, int x, int y, int w, int h, String text) {
		this.addRenderableWidget(new GuiButtonExt(id, x, y, w, h, text, b -> this.actionPerformed(id)));
	}

	private int getArmorCamo(int slot) {
		Player ply = Minecraft.getInstance().player;

		ItemStack armor = ply.getInventory().armor.get(slot);
		if (!armor.isEmpty() && armor.getItem() instanceof ICamoChangeable) {
			ICamoChangeable camoArmor = (ICamoChangeable) armor.getItem();
			return camoArmor.getCurrentCamoIndex(armor) + 1;
		}

		return 0;
	}

	private int getExtendedInvItemCamo(int slot) {
		Player ply = Minecraft.getInstance().player;

		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props != null) {

			ItemStack item = props.tg_inventory.inventory.get(slot);

			if (!item.isEmpty() && item.getItem() instanceof ICamoChangeable) {
				ICamoChangeable camoItem = (ICamoChangeable) item.getItem();
				return camoItem.getCurrentCamoIndex(item) + 1;
			}
		}
		return 0;
	}

}
