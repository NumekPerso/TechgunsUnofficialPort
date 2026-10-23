package techguns.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.TGItems;
import techguns.Techguns;
import techguns.api.tginventory.TGSlotType;
import techguns.items.armors.TGArmorBonus;
import techguns.util.TextUtil;

/**
 * Replaces the non ammo variants of the 1.12 GenericItemShared (metadata based shared item): crafting materials, gun parts,
 * machine/turret/ore drill/armor upgrade items, oxygen mask, working gloves. One item per 1.12 variant, the registry name is
 * the 1.12 variant name.
 *
 * getSlot / getBonus have the same signature as the 1.12 ITGSpecialSlot methods.
 */
public class TGSharedItem extends GenericItem implements techguns.api.tginventory.ITGSpecialSlot {

	protected final TGSlotType slottype;

	/**
	 * Mining level bonus shown in the tooltip of ore drill heads (1.12 compared the item damage with TGItems.OREDRILLHEAD_*)
	 */
	protected int oreDrillMiningLevel = 0;

	public TGSharedItem(String name, TGSlotType slottype, int maxStackSize) {
		super(name, true, new Properties().stacksTo(maxStackSize));
		this.slottype = slottype;
	}

	public TGSharedItem(String name, TGSlotType slottype) {
		this(name, slottype, 64);
	}

	public TGSharedItem(String name) {
		this(name, TGSlotType.NORMAL);
	}

	public TGSharedItem setOreDrillMiningLevel(int level) {
		this.oreDrillMiningLevel = level;
		return this;
	}

	public int getOreDrillMiningLevel() {
		return oreDrillMiningLevel;
	}

	/**
	 * The slot type this item belongs (1.12 ITGSpecialSlot)
	 */
	public TGSlotType getSlot(ItemStack item) {
		return this.slottype;
	}

	/**
	 * 1.12 ITGSpecialSlot.getBonus
	 */
	public float getBonus(TGArmorBonus type, ItemStack stack, boolean consume, @Nullable Player player) {
		if (this == TGItems.OXYGEN_MASK) {
			if (type == TGArmorBonus.OXYGEN_GEAR) {
				return 1.0f;
			}
		} else if (this == TGItems.WORKING_GLOVES) {
			if (type == TGArmorBonus.BREAKSPEED) {
				return 0.10f;
			}
		}
		return 0f;
	}

	private float getBonus(TGArmorBonus type, ItemStack stack) {
		return getBonus(type, stack, false, null);
	}

	/**
	 * Shows the enchantment of the upgrade bench recipe of an armor upgrade item
	 */
	protected void addArmorEnchantTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		techguns.tileentities.operation.UpgradeBenchRecipes.UpgradeBenchRecipe rec = techguns.tileentities.operation.UpgradeBenchRecipes.getUpgradeRecipeForUpgradeItem(stack);
		if (rec != null) {
			net.minecraft.world.item.enchantment.Enchantment ench = rec.getEnch();
			tooltip.add(ench.getFullname(rec.getLevel()).copy().withStyle(ChatFormatting.AQUA));

			if (flagIn.isAdvanced() && ench.category != null) {
				tooltip.add(Component.literal(" " + ench.category.toString()));
			}
		}
	}

	public static boolean isMachineUpgrade(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == TGItems.MACHINE_UPGRADE_STACK;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (this.getSlot(stack) != TGSlotType.NORMAL) {
			addLine(tooltip, ChatFormatting.GRAY + TextUtil.trans(Techguns.MODID + ".tooltip.slot") + ": " + this.getSlot(stack));
			if (this.getSlot(stack) == TGSlotType.ARMOR_UPGRADE) {
				this.addArmorEnchantTooltip(stack, worldIn, tooltip, flagIn);
			}
		}
		if (this.getBonus(TGArmorBonus.EXTRA_HEART, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.healthbonus") + ": +" + (int) this.getBonus(TGArmorBonus.EXTRA_HEART, stack) + " " + trans("armorTooltip.hearts"));
		} else if (this.getBonus(TGArmorBonus.SPEED, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.movespeed") + ": +" + this.getBonus(TGArmorBonus.SPEED, stack) * 100.0f + "%");
		} else if (this.getBonus(TGArmorBonus.JUMP, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.jumpheight") + ": +" + this.getBonus(TGArmorBonus.JUMP, stack));
		}
		if (this.getBonus(TGArmorBonus.FALLDMG, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.falldamage") + ": -" + this.getBonus(TGArmorBonus.FALLDMG, stack) * 100.0f + "%");
		}
		if (this.getBonus(TGArmorBonus.FREEHEIGHT, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.fallheight") + ": -" + this.getBonus(TGArmorBonus.FREEHEIGHT, stack));
		}
		if (this.getBonus(TGArmorBonus.BREAKSPEED, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.miningspeed") + ": +" + this.getBonus(TGArmorBonus.BREAKSPEED, stack) * 100.0f + "%");
		}
		if (this.getBonus(TGArmorBonus.BREAKSPEED_WATER, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.underwatermining") + ": +" + this.getBonus(TGArmorBonus.BREAKSPEED_WATER, stack) * 100.0f + "%");
		}
		if (this.getBonus(TGArmorBonus.KNOCKBACK_RESISTANCE, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.knockbackresistance") + ": +" + this.getBonus(TGArmorBonus.KNOCKBACK_RESISTANCE, stack) * 100.0f + "%");
		}
		if (this.getBonus(TGArmorBonus.NIGHTVISION, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.nightvision"));
		}
		if (this.getBonus(TGArmorBonus.STEPASSIST, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.stepassist"));
		}
		if (this.getBonus(TGArmorBonus.OXYGEN_GEAR, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.oxygengear"));
		}
		if (this.getBonus(TGArmorBonus.COOLING_SYSTEM, stack) > 0.0f) {
			addLine(tooltip, trans("armorTooltip.coolingsystem"));
		}
		if (this.oreDrillMiningLevel > 0) {
			addLine(tooltip, trans("oredrill.mininglevel") + " +" + this.oreDrillMiningLevel);
		}
	}

	protected static void addLine(List<Component> tooltip, String text) {
		tooltip.add(Component.literal(text));
	}

	private static String trans(String text) {
		return TextUtil.trans(Techguns.MODID + "." + text);
	}
}
