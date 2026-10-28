package techguns.gui.player;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.capabilities.TGExtendedPlayer;
import techguns.damagesystem.DamageSystem;
import techguns.gui.player.tabs.TGPlayerTab;
import techguns.gui.widgets.ITGSlotBackground;
import techguns.items.armors.GenericArmor;
import techguns.util.TextUtil;

public class TGPlayerInventoryGui extends AbstractContainerScreen<TGPlayerInventoryContainer> {

	public static ResourceLocation texture = new ResourceLocation(Techguns.MODID, "textures/gui/tgplayerinventory.png");

	private static final int MINOR_POISONING = techguns.TGRadiationSystem.MINOR_POISONING;
	private static final int SEVERE_POISONING = techguns.TGRadiationSystem.SEVERE_POISONING;
	private static final int LETHAL_POISONING = techguns.TGRadiationSystem.LETHAL_POISONING;

	private final TGPlayerInventory inventory;
	private float sizex;
	private float sizey;
	private TGExtendedPlayer props;

	private static final int togglebuttons_xpos = -8;

	public TGPlayerInventoryGui(TGPlayerInventoryContainer container, Inventory inv, Component title) {
		super(container, inv, title);
		this.props = TGExtendedPlayer.get(inv.player);
		this.inventory = props.tg_inventory;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	public static boolean isInRect(int mouseX, int mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
	}

	public static String trans(String key) {
		return TextUtil.trans("techguns." + key);
	}

	private List<Component> getTooltipForToggleButton(int index) {
		List<String> tooltip = new ArrayList<String>();

		boolean state = false;
		if (index == 0) {
			state = props.showTGHudElements;
			tooltip.add(trans("tgguitooltip.togglehud"));
			tooltip.add(trans("tgguitooltip.current") + ": " + (!state ? "§c" : "§a") + trans("tgguitooltip." + (state ? "on" : "off")));
			tooltip.add(trans("tgguitooltip.togglehud.tooltip1"));
			tooltip.add(trans("tgguitooltip.togglehud.tooltip2"));
		} else if (index == 1) {
			state = props.enableNightVision;
			tooltip.add(trans("tgguitooltip.togglenv"));
			tooltip.add(trans("tgguitooltip.current") + ": " + (!state ? "§c" : "§a") + trans("tgguitooltip." + (state ? "on" : "off")));
			tooltip.add(trans("tgguitooltip.togglenv.tooltip1"));
			tooltip.add(trans("tgguitooltip.togglenv.tooltip2"));
		} else if (index == 2) {
			state = props.enableSafemode;
			tooltip.add(trans("tgguitooltip.togglesafe"));
			tooltip.add(trans("tgguitooltip.current") + ": " + (!state ? "§c" : "§a") + trans("tgguitooltip." + (state ? "on" : "off")));
			tooltip.add(trans("tgguitooltip.togglesafe.tooltip1"));
			tooltip.add(trans("tgguitooltip.togglesafe.tooltip2"));
		} else if (index == 3) {
			state = props.enableStepAssist;
			tooltip.add(trans("tgguitooltip.togglestep"));
			tooltip.add(trans("tgguitooltip.current") + ": " + (!state ? "§c" : "§a") + trans("tgguitooltip." + (state ? "on" : "off")));
			tooltip.add(trans("tgguitooltip.togglestep.tooltip1"));
			tooltip.add(trans("tgguitooltip.togglestep.tooltip2"));
		} else if (index == 4) {
			state = props.enableJetpack;
			tooltip.add(trans("tgguitooltip.togglejetpack"));
			tooltip.add(trans("tgguitooltip.current") + ": " + (!state ? "§c" : "§a") + trans("tgguitooltip." + (state ? "on" : "off")));
			tooltip.add(trans("tgguitooltip.togglejetpack.tooltip1"));
			tooltip.add(trans("tgguitooltip.togglejetpack.tooltip2"));
		}

		return toComponents(tooltip);
	}

	private static List<Component> toComponents(List<String> lines) {
		List<Component> ret = new ArrayList<>();
		lines.forEach(s -> ret.add(Component.literal(s)));
		return ret;
	}

	private List<Component> getTooltipTotalArmorValues() {
		List<String> tooltip = new ArrayList<String>();

		tooltip.add(ChatFormatting.UNDERLINE + trans("tgguitooltip.totalarmorvalues"));

		Player ply = Minecraft.getInstance().player;
		tooltip.add(ChatFormatting.DARK_GRAY + trans("TGDamageType.PHYSICAL") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.PHYSICAL), DamageType.PHYSICAL));
		tooltip.add(ChatFormatting.GRAY + trans("TGDamageType.PROJECTILE") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.PROJECTILE), DamageType.PROJECTILE));
		tooltip.add(ChatFormatting.DARK_RED + trans("TGDamageType.EXPLOSION") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.EXPLOSION), DamageType.EXPLOSION));
		tooltip.add(ChatFormatting.DARK_AQUA + trans("TGDamageType.ENERGY") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.ENERGY), DamageType.ENERGY));
		tooltip.add(ChatFormatting.RED + trans("TGDamageType.FIRE") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.FIRE), DamageType.FIRE));
		tooltip.add(ChatFormatting.AQUA + trans("TGDamageType.ICE") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.ICE), DamageType.ICE));
		tooltip.add(ChatFormatting.YELLOW + trans("TGDamageType.LIGHTNING") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.LIGHTNING), DamageType.LIGHTNING));
		tooltip.add(ChatFormatting.DARK_GRAY + trans("TGDamageType.DARK") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.DARK), DamageType.DARK));
		tooltip.add(ChatFormatting.DARK_GREEN + trans("TGDamageType.POISON") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.POISON), DamageType.POISON));
		tooltip.add(ChatFormatting.GREEN + trans("TGDamageType.RADIATION") + ": " + GenericArmor.formatAV(DamageSystem.getTotalArmorAgainstType(ply, DamageType.RADIATION), DamageType.RADIATION));

		return toComponents(tooltip);
	}

	private List<Component> getTooltipRadiationBar() {
		List<String> tooltip = new ArrayList<String>();

		TGExtendedPlayer props = TGExtendedPlayer.get(Minecraft.getInstance().player);

		int displayedRad = 0;
		if (props != null) {
			displayedRad = props.radlevel;
		}

		tooltip.add(ChatFormatting.GREEN + "" + ChatFormatting.UNDERLINE + trans("tgguitooltip.currentrad"));
		tooltip.add(displayedRad + "/" + "1000");

		String potency = " " + TextUtil.trans("potion.potency.1");
		if (displayedRad < MINOR_POISONING) {
			tooltip.add(trans("tgguitooltip.radeffects") + ": " + ChatFormatting.GREEN + trans("tgguitooltip.radeffects.none"));
		} else {
			tooltip.add(trans("tgguitooltip.radeffects") + ":");

			if (displayedRad < SEVERE_POISONING) {
				tooltip.add(" " + TextUtil.trans(MobEffects.HUNGER.getDescriptionId()));
			} else {
				tooltip.add(" " + TextUtil.trans(MobEffects.HUNGER.getDescriptionId()) + potency);
				tooltip.add(" " + TextUtil.trans(MobEffects.DIG_SLOWDOWN.getDescriptionId()) + potency);
				tooltip.add(" " + TextUtil.trans(MobEffects.WEAKNESS.getDescriptionId()) + potency);

				if (displayedRad >= (LETHAL_POISONING)) {
					tooltip.add(" " + TextUtil.trans(MobEffects.CONFUSION.getDescriptionId()) + potency);
					tooltip.add(" " + TextUtil.trans(MobEffects.MOVEMENT_SLOWDOWN.getDescriptionId()) + potency);
					tooltip.add(" " + trans("gun.tooltip.damage"));
				}
			}
		}
		return toComponents(tooltip);
	}

	/**
	 * 1.12 drawGuiContainerForegroundLayer (only tooltips, no labels)
	 */
	@Override
	protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
	}

	protected void renderTGTooltips(GuiGraphics g, int mouseX, int mouseY) {
		int mx = mouseX - this.leftPos;
		int my = mouseY - this.topPos;

		for (int i = 0; i < 5; i++) {
			if (isInRect(mx, my, togglebuttons_xpos, 7 + i * 11, 11, 11)) {
				g.renderComponentTooltip(this.font, getTooltipForToggleButton(i), mouseX, mouseY);
			}
		}

		//Total Armor Tooltip
		if (isInRect(mx, my, 98, 7, 14, 14)) {
			g.renderComponentTooltip(this.font, this.getTooltipTotalArmorValues(), mouseX, mouseY);
		}

		//Radiation Bar Tooltip
		if (isInRect(mx, my, 115, 7, 54, 6)) {
			g.renderComponentTooltip(this.font, this.getTooltipRadiationBar(), mouseX, mouseY);
		}
	}

	@Override
	protected void renderBg(GuiGraphics g, float partialTickTime, int mouseX, int mouseY) {
		int k = this.leftPos;
		int l = this.topPos;
		g.blit(texture, k, l, 0, 0, this.imageWidth, this.imageHeight);

		//empty slot icons (1.12 Slot.getSlotTexture with sprites of the block atlas)
		for (Slot slot : this.menu.slots) {
			if (!slot.hasItem()) {
				ResourceLocation tex = null;
				if (slot instanceof SlotTG stg) {
					tex = stg.getEmptySlotTexture();
				} else if (slot instanceof SlotFood) {
					tex = SlotTG.FOODSLOT_TEX;
				}
				if (tex != null) {
					ITGSlotBackground.drawIcon(g, tex, k + slot.x, l + slot.y);
				}
			}
		}

		TGExtendedPlayer props = TGExtendedPlayer.get(Minecraft.getInstance().player);

		int displayedRad = 0;
		if (props != null) {
			displayedRad = props.radlevel;
		}

		if (displayedRad > 0) {
			int width = (int) Math.ceil(displayedRad / 1000.0d * 52d);
			g.blit(texture, k + 116, l + 8, 177, 0, width, 4);
		}

		InventoryScreen.renderEntityInInventoryFollowsMouse(g, k + 51, l + 75, 30, (float) (k + 51) - this.sizex, (float) (l + 75 - 50) - this.sizey, this.minecraft.player);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(g);
		super.render(g, mouseX, mouseY, partialTicks);
		this.renderTooltip(g, mouseX, mouseY);
		this.renderTGTooltips(g, mouseX, mouseY);
		this.sizex = (float) mouseX;
		this.sizey = (float) mouseY;
	}

	@Override
	protected void init() {
		super.init();

		//1.12: Galacticraft API for Tabs
		TGPlayerTab.addTabs(this.leftPos, this.topPos, true, this::addRenderableWidget);

		for (int x = 0; x < 5; x++) {
			this.addRenderableWidget(new TGGuiToggleButton(this.leftPos + togglebuttons_xpos, this.topPos + 7 + (x * 11), x));
		}
	}

}
