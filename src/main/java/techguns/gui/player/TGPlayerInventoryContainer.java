package techguns.gui.player;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegisterEvent;
import techguns.Techguns;
import techguns.api.tginventory.TGSlotType;
import techguns.capabilities.TGExtendedPlayer;

@Mod.EventBusSubscriber(modid = Techguns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TGPlayerInventoryContainer extends AbstractContainerMenu {

	public static MenuType<TGPlayerInventoryContainer> TYPE;

	private static final int DEFAULT_INV_ARMOR_START = TGPlayerInventory.NUMSLOTS;
	private static final int DEFAULT_INV_START = DEFAULT_INV_ARMOR_START + 4;
	private static final int DEFAULT_INV_HOTBAR_START = DEFAULT_INV_START + 27;
	private static final int DEFAULT_INV_SHIELD = DEFAULT_INV_HOTBAR_START + 9;

	private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

	private static final int FOOD_SLOTS_COUNT = 3;
	private static final int AMMO_SLOTS_COUNT = 8;

	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.MENU)) {
			TYPE = new MenuType<>(TGPlayerInventoryContainer::new, FeatureFlags.DEFAULT_FLAGS);
			event.register(Registries.MENU, new ResourceLocation(Techguns.MODID, "tgplayerinventory"), () -> TYPE);
		}
	}

	/**
	 * Server: open the Techguns player inventory (1.12 ply.openGui(Techguns.instance, 0, ...))
	 */
	public static void openForPlayer(ServerPlayer ply) {
		NetworkHooks.openScreen(ply, new SimpleMenuProvider((id, inv, p) -> new TGPlayerInventoryContainer(id, inv), Component.translatable("techguns.extendedinventory")));
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	public TGPlayerInventoryContainer(int id, Inventory inv) {
		super(TYPE, id);
		Player player = inv.player;
		TGPlayerInventory tgplayerinv = TGExtendedPlayer.get(player).tg_inventory;

		//custom slots:
		this.addSlot(new SlotTG(tgplayerinv, TGPlayerInventory.SLOT_FACE, 77, 8, TGSlotType.FACESLOT));

		this.addSlot(new SlotTG(tgplayerinv, TGPlayerInventory.SLOT_BACK, 77, 26, TGSlotType.BACKSLOT));

		this.addSlot(new SlotTG(tgplayerinv, TGPlayerInventory.SLOT_HAND, 77, 44, TGSlotType.HANDSLOT));

		this.addSlot(new SlotFood(tgplayerinv, TGPlayerInventory.SLOTS_AUTOFOOD_START, 116, 24));
		this.addSlot(new SlotFood(tgplayerinv, TGPlayerInventory.SLOTS_AUTOFOOD_START + 1, 116 + 18, 24));
		this.addSlot(new SlotFood(tgplayerinv, TGPlayerInventory.SLOTS_AUTOFOOD_START + 2, 116 + 18 * 2, 24));

		this.addSlot(new SlotTG(tgplayerinv, TGPlayerInventory.SLOT_AUTOHEAL, 97, 24, TGSlotType.HEALSLOT));

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlot(new SlotTG(tgplayerinv, TGPlayerInventory.SLOTS_AMMO_START + (i * 4) + j, 98 + j * 18, 44 + i * 18, TGSlotType.AMMOSLOT));
			}
		}

		//Default Slots:

		//Armor
		for (int k = 0; k < 4; ++k) {
			EquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
			this.addSlot(new SlotArmor(inv, 36 + (3 - k), 8, 8 + k * 18, entityequipmentslot, player));
		}

		int i, j;
		//Inventory:
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
			}
		}

		//Hotbar:
		for (i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
		}

		this.addSlot(new Slot(inv, 40, 77, 62) {
			@Override
			public void setByPlayer(ItemStack stack) {
				if (net.minecraft.world.item.Equipable.get(stack) != null) {
					player.onEquipItem(EquipmentSlot.OFFHAND, this.getItem(), stack);
				}
				super.setByPlayer(stack);
			}

			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
			}
		});
	}

	/**
	 * Get the valid slot in the TG inventory tab
	 */
	private int[] getValidSlotForIteminInv(ItemStack stack) {
		int[] ret = new int[2];
		ret[0] = -1;
		ret[1] = 1;

		for (int i = 0; i < TGPlayerInventory.NUMSLOTS; ++i) {
			Slot s = this.slots.get(i);
			if (s.mayPlace(stack)) {
				ret[0] = i;

				if (s instanceof SlotTG) {
					SlotTG stg = (SlotTG) s;
					if (stg.getType() == TGSlotType.AMMOSLOT) {
						ret[1] = AMMO_SLOTS_COUNT;
					}
				} else if (s instanceof SlotFood) {
					ret[1] = FOOD_SLOTS_COUNT;
				}

				return ret;
			}
		}
		return ret;
	}

	private boolean hasTGSpecialSlot(ItemStack stack) {
		return this.getValidSlotForIteminInv(stack)[0] >= 0;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int id) {
		int o = TGPlayerInventory.NUMSLOTS - 5; //offset compared to Default inv, -5 craftmatrix + TG inventory size

		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(id);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			EquipmentSlot armorSlot = Mob.getEquipmentSlotForItem(itemstack);
			int armorSlotIndex = DEFAULT_INV_ARMOR_START + (3 - armorSlot.getIndex());

			if (id < TGPlayerInventory.NUMSLOTS) {
				//CLICKED IN TG INVENTORY
				if (!this.moveItemStackTo(itemstack1, DEFAULT_INV_START, DEFAULT_INV_START + 36, false)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(itemstack1, itemstack);
			}
			//Try merge Armor (1.12 used armorType.getIndex() directly, which maps the helmet to the boots slot)
			else if (armorSlot.getType() == EquipmentSlot.Type.ARMOR && (id < DEFAULT_INV_ARMOR_START || id >= DEFAULT_INV_START) && !this.slots.get(armorSlotIndex).hasItem()) {
				if (!this.moveItemStackTo(itemstack1, armorSlotIndex, armorSlotIndex + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((id >= DEFAULT_INV_START && id < DEFAULT_INV_START + 36) && hasTGSpecialSlot(itemstack)) {
				//Merge into tG inventory
				int slots[] = this.getValidSlotForIteminInv(itemstack1);
				int validslot = slots[0];
				int num = slots[1];
				if (!this.moveItemStackTo(itemstack1, validslot, validslot + num, false)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (id >= 9 + o && id < 36 + o) {
				if (!this.moveItemStackTo(itemstack1, 36 + o, 45 + o, false)) {
					return ItemStack.EMPTY;
				}
			} else if (id >= 36 + o && id < 45 + o) {
				if (!this.moveItemStackTo(itemstack1, 9 + o, 36 + o, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 9 + o, 45 + o, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

}
