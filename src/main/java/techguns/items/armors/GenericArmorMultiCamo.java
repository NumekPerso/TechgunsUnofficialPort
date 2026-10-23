package techguns.items.armors;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.Techguns;
import techguns.util.TextUtil;

public class GenericArmorMultiCamo extends GenericArmor implements ICamoChangeable {
	protected static Random rnd = new Random();
	protected String[] textureNames;
	protected String camoNameSuffix = "";

	public GenericArmorMultiCamo(String unlocalizedName, TGArmorMaterial material, String[] textureNames, EquipmentSlot type) {
		this(Techguns.MODID, unlocalizedName, material, textureNames, type);
	}

	public GenericArmorMultiCamo(String modid, String unlocalizedName, TGArmorMaterial material, String[] textureNames, EquipmentSlot type) {
		super(modid, unlocalizedName, material, textureNames[0], type);
		this.textureNames = textureNames;
	}

	public GenericArmorMultiCamo setCamoNameSuffix(String s) {
		this.camoNameSuffix = s + ".";
		return this;
	}

	@Override
	public void appendHoverText(ItemStack item, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
		super.appendHoverText(item, worldIn, list, flagIn);
		if (Techguns.proxy.isShiftKeyDown()) {
			list.add(Component.literal(TextUtil.trans(Techguns.MODID + ".tooltip.currentcamo") + ": ").append(getCurrentCamoName(item)));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand handIn) {
		if (player.isShiftKeyDown()) {
			ItemStack item = player.getItemInHand(handIn);
			this.switchCamo(item);
			if (world.isClientSide) {
				player.displayClientMessage(Component.literal(TextUtil.trans("techguns.message.camoswitch") + " ").append(getCurrentCamoName(item)), false);
			}
			return InteractionResultHolder.success(item);
		} else {
			return super.use(world, player, handIn);
		}
	}

	@Override
	public int getCamoCount() {
		if (textureNames != null) {
			return textureNames.length;
		}
		return 0;
	}

	@Override
	public Component getCurrentCamoName(ItemStack item) {
		CompoundTag tags = item.getTag();
		byte camoID = -1;
		if (tags != null && tags.contains("camo")) {
			camoID = tags.getByte("camo");
		}
		if (camoID >= 0) {
			return Component.translatable(this.modid + ".item." + textureNames[0] + "." + camoNameSuffix + "camoname." + camoID);
		} else {
			return Component.translatable(Techguns.MODID + ".item.invalidcamo");
		}
	}

	public static int getRandomCamoIndexFor(GenericArmorMultiCamo type) {
		int count = type.getCamoCount();
		if (count <= 0) {
			return 0;
		}
		return rnd.nextInt(count);
	}

	public static ItemStack getNewWithCamo(Item item, int camo) {
		ItemStack armor = new ItemStack(item);
		armor.getOrCreateTag().putByte("camo", (byte) camo);
		return armor;
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level world, Player player) {
		super.onCraftedBy(stack, world, player);
		stack.getOrCreateTag().putByte("camo", (byte) 0);
	}

	protected static int getArmorLayer(EquipmentSlot slot) {
		return EquipmentSlot.LEGS == slot ? 2 : 1;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		GenericArmorMultiCamo armor = (GenericArmorMultiCamo) stack.getItem();

		int i = armor.getCurrentCamoIndex(stack);
		if (i >= 0 && i < this.textureNames.length) {
			return this.modid + ":textures/models/armor/" + this.textureNames[i] + (this.hasDoubleTexture() ? ("_layer_" + getArmorLayer(slot)) : "") + ".png";
		} else {
			return null;
		}
	}
}
