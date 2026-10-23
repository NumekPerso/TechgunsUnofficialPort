package techguns.gui.player;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * Armor slot of the Techguns player inventory (1.12 gui.widgets.SlotArmor, same behaviour as the vanilla InventoryMenu armor slots)
 */
public class SlotArmor extends Slot {
	private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[] { InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
			InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET };

	private EquipmentSlot armortype;
	private Player ply;

	public SlotArmor(Container inv, int slot, int x, int y, EquipmentSlot armorType, Player ply) {
		super(inv, slot, x, y);
		this.armortype = armorType;
		this.ply = ply;
	}

	public SlotArmor(Container inv, int slot, int x, int y, int armorType, Player ply) {
		this(inv, slot, x, y, getArmorTypeFromInt(armorType), ply);
	}

	protected static EquipmentSlot getArmorTypeFromInt(int slot) {
		switch (slot) {
		case 0:
			return EquipmentSlot.HEAD;
		case 1:
			return EquipmentSlot.CHEST;
		case 2:
			return EquipmentSlot.LEGS;
		default:
			return EquipmentSlot.FEET;
		}
	}

	@Override
	public void setByPlayer(ItemStack stack) {
		if (Equipable.get(stack) != null) {
			this.ply.onEquipItem(this.armortype, this.getItem(), stack);
		}
		super.setByPlayer(stack);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.canEquip(armortype, ply);
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		ItemStack itemstack = this.getItem();
		return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(playerIn);
	}

	@Override
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[armortype.getIndex()]);
	}

}
