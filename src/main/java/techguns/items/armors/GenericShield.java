package techguns.items.armors;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import techguns.TGArmors;
import techguns.TGItems;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.ShieldStats;
import techguns.util.TextUtil;

/**
 * Port of the 1.12 GenericShield. The blocked damage of Techguns damage sources depends on the damage type (ShieldStats), see
 * TGArmorEventHandler.onShieldBlock. Rendered by TileEntityItemRendererTGShield.
 */
public class GenericShield extends ShieldItem implements ICamoChangeable, TGItems.ITGRegistryNamed {

	protected int camocount = 1;

	@Nullable
	protected ResourceLocation repairMat = null;
	protected int repairMatCount = 0;

	protected static DecimalFormat formatReduction = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	protected final String modid;
	protected final String name;

	public GenericShield(String modid, String name, int durability, boolean addToList, int camocount) {
		super(new Item.Properties().durability(durability));
		this.modid = modid;
		this.name = name;
		this.camocount = Math.max(camocount, 1);
		if (addToList) {
			TGArmors.shields.add(this);
		}
		TGItems.ITEMLIST.add(this);
	}

	public GenericShield(String modid, String name, int durability, boolean addToList) {
		this(modid, name, durability, addToList, 1);
	}

	public GenericShield(String name, int durability) {
		this(Techguns.MODID, name, durability, true, 1);
	}

	public GenericShield(String name, int durability, int camocount) {
		this(Techguns.MODID, name, durability, true, camocount);
	}

	@Override
	public ResourceLocation getTGRegistryName() {
		return new ResourceLocation(this.modid, this.name);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(techguns.client.armor.TGShieldClientExtensions.INSTANCE);
	}

	/**
	 * Repair material (1.12 ore dictionary entry, now an item registry name) and amount for the repair bench
	 */
	public GenericShield setRepairMat(ResourceLocation mat, int amount) {
		this.repairMat = mat;
		this.repairMatCount = amount;
		return this;
	}

	public ArrayList<ItemStack> getRepairMats(ItemStack item) {
		ArrayList<ItemStack> mats = new ArrayList<ItemStack>();

		if (item.getDamageValue() > 0) {
			GenericShield armor = (GenericShield) item.getItem();

			float dmgpercent = (item.getDamageValue() * 1.0f) / ((item.getMaxDamage() - 1) * 1.0f);

			int count = (int) Math.ceil(armor.repairMatCount * dmgpercent);

			if (armor.repairMat != null && count > 0) {
				Item it = ForgeRegistries.ITEMS.getValue(armor.repairMat);
				if (it != null) {
					mats.add(new ItemStack(it, count));
				}
			}
		}
		return mats;
	}

	public boolean canRepairOnRepairBench(ItemStack item) {
		GenericShield shield = (GenericShield) item.getItem();
		return shield.repairMatCount > 0;
	}

	/**
	 * same as in Item (no banner color variants)
	 */
	@Override
	public String getDescriptionId(ItemStack stack) {
		return this.getDescriptionId();
	}

	@Override
	public int getCamoCount() {
		return this.camocount;
	}

	@Override
	public Component getCurrentCamoName(ItemStack item) {
		CompoundTag tags = item.getTag();
		byte camoID = 0;
		if (tags != null && tags.contains("camo")) {
			camoID = tags.getByte("camo");
		}
		if (camoID > 0) {
			return Component.translatable(this.getDescriptionId() + ".camoname." + camoID);
		} else {
			return Component.translatable("techguns.item.defaultcamo");
		}
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		if (this.repairMat != null && !repair.isEmpty() && this.repairMat.equals(ForgeRegistries.ITEMS.getKey(repair.getItem()))) {
			return true;
		}
		return super.isValidRepairItem(toRepair, repair);
	}

	protected static void addLine(List<Component> list, String text) {
		list.add(Component.literal(text));
	}

	@Override
	public void appendHoverText(ItemStack item, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
		super.appendHoverText(item, worldIn, list, flagIn);

		if (Techguns.proxy.isShiftKeyDown()) {

			addLine(list, trans("armorTooltip.durability") + ": " + (item.getMaxDamage() - item.getDamageValue()) + "/" + (item.getMaxDamage()));

			ShieldStats s = ShieldStats.getStats(item, null);
			addLine(list, ChatFormatting.BLUE + trans("armorTooltip.resistances") + ":");

			addLine(list, ChatFormatting.DARK_GRAY + " AR: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.PHYSICAL)) + "%");
			addLine(list, ChatFormatting.GRAY + " PR: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.PROJECTILE)) + "%");
			addLine(list, ChatFormatting.DARK_RED + " EX: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.EXPLOSION)) + "%");
			addLine(list, ChatFormatting.DARK_AQUA + " E: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.ENERGY)) + "%");

			addLine(list, ChatFormatting.RED + " F: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.FIRE)) + "%");
			addLine(list, ChatFormatting.AQUA + " I: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.ICE)) + "%");
			addLine(list, ChatFormatting.YELLOW + " L: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.LIGHTNING)) + "%");
			addLine(list, ChatFormatting.DARK_GREEN + " P: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.POISON)) + "%");
			addLine(list, ChatFormatting.DARK_GRAY + " D: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.DARK)) + "%");
			addLine(list, ChatFormatting.GREEN + " RAD: " + formatReduction.format(s.getReductionPercentAgainstType(DamageType.RADIATION)) + "%");

		} else {
			addLine(list, TextUtil.trans("techguns.gun.tooltip.shift1") + " " + ChatFormatting.GREEN + TextUtil.trans("techguns.gun.tooltip.shift2") + " " + ChatFormatting.GRAY + TextUtil.trans("techguns.gun.tooltip.shift3"));
		}
	}

	protected String trans(String text) {
		return TextUtil.trans(Techguns.MODID + "." + text);
	}
}
