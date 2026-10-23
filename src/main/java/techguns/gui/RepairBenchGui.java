package techguns.gui;

import static techguns.gui.ButtonConstants.BUTTON_ID_SECURITY;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import techguns.gui.containers.RepairBenchContainer;
import techguns.gui.widgets.GuiButtonRepair;
import techguns.items.armors.GenericArmor;
import techguns.items.armors.GenericShield;
import techguns.tileentities.RepairBenchTileEnt;
import techguns.util.TextUtil;

public class RepairBenchGui extends OwnedTileEntGui<RepairBenchContainer> {
	public static final ResourceLocation texture = new ResourceLocation("techguns:textures/gui/repair_bench_gui.png");

	protected RepairBenchTileEnt tileent;
	protected Inventory invplayer;

	public RepairBenchGui(RepairBenchContainer container, Inventory player, Component title) {
		super(container, player, title, container.getRepairBench());
		this.tileent = container.getRepairBench();
		this.invplayer = player;
		this.tex = texture;
		this.heightSecurityButton = 10;
		this.showInventoryText = false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics g, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(g, mouseX, mouseY);
		int mx = mouseX - (this.width - this.imageWidth) / 2;
		int my = mouseY - (this.height - this.imageHeight) / 2;

		for (int i = 0; i < 4; i++) {
			if (isInRect(mx, my, 90 + i * 20, 39, 14, 14)) {
				ItemStack armor = this.invplayer.armor.get(3 - i);
				this.drawTooltipRepairMats(armor, mx, my);
			}
		}

		if (isInRect(mx, my, 65, 39, 14, 14)) {
			ItemStack offhand = this.invplayer.offhand.get(0);
			this.drawTooltipRepairMats(offhand, mx, my);
		}

		if (isInRect(mx, my, 12, 39, 14, 14)) {
			ItemStack stack = this.tileent.getInventory().getStackInSlot(9);
			this.drawTooltipRepairMats(stack, mx, my);
		}
	}

	private void drawTooltipRepairMats(ItemStack item, int mx, int my) {
		if (!item.isEmpty()) {

			List<ItemStack> mats = null;
			boolean canRepair = false;
			if (item.getItem() instanceof GenericArmor && ((GenericArmor) item.getItem()).canRepairOnRepairBench(item)) {
				canRepair = true;
				if (item.getDamageValue() > 0) {
					mats = ((GenericArmor) item.getItem()).getRepairMats(item);
				}
			} else if (item.getItem() instanceof GenericShield && ((GenericShield) item.getItem()).canRepairOnRepairBench(item)) {
				canRepair = true;
				if (item.getDamageValue() > 0) {
					mats = ((GenericShield) item.getItem()).getRepairMats(item);
				}
			}

			if (canRepair) {
				if (mats != null) {
					List<String> tooltips = new ArrayList<String>();
					tooltips.add(TextUtil.trans("techguns.repairBench.requiredMats"));
					for (int i = 1; i < mats.size() + 1; i++) {
						String prefix = "";
						if (RepairBenchTileEnt.canConsumeItem(this.tileent.getInventory(), mats.get(i - 1), 0, this.tileent.getInventory().getSlots()) > 0) {
							prefix = "§c";
						}
						tooltips.add(prefix + mats.get(i - 1).getCount() + "x " + mats.get(i - 1).getHoverName().getString());
					}

					this.drawHoveringText(tooltips, mx, my);

				} else {
					this.drawHoveringText(TextUtil.trans("techguns.repairBench.fullcondition"), mx, my);
				}
			} else {
				this.drawHoveringText(TextUtil.trans("techguns.repairBench.cantrepair"), mx, my);
			}

		} else {
			this.drawHoveringText(TextUtil.trans("techguns.repairBench.noarmor"), mx, my);
		}

	}

	@Override
	protected void init() {
		super.init();
		int id = BUTTON_ID_SECURITY + 1;
		this.addRepairButton(id++, this.leftPos + 90, this.topPos + 39);
		this.addRepairButton(id++, this.leftPos + 90 + 20, this.topPos + 39);
		this.addRepairButton(id++, this.leftPos + 90 + 40, this.topPos + 39);
		this.addRepairButton(id++, this.leftPos + 90 + 60, this.topPos + 39);

		//offhand
		this.addRepairButton(id++, this.leftPos + 65, this.topPos + 39);
		//general repair slot
		this.addRepairButton(id++, this.leftPos + 9, this.topPos + 39);
	}

	protected void addRepairButton(int id, int x, int y) {
		this.addRenderableWidget(new GuiButtonRepair(id, x, y, 14, 14, b -> this.actionPerformed(id)));
	}
}
