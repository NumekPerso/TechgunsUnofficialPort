package techguns.gui.player;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import techguns.Techguns;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;

/**
 * Slot for the Techguns player inventory (1.12 gui.widgets.SlotTG, limited to the player slot types).
 * The 1.12 empty slot icons were block atlas sprites, here they are plain textures drawn by the screen
 * ({@link #getEmptySlotTexture()}), so no atlas changes are needed.
 */
public class SlotTG extends Slot {
	private final TGSlotType type;
	public static final ResourceLocation BACKSLOT_TEX = tex("emptyslot_back");
	public static final ResourceLocation FACESLOT_TEX = tex("emptyslot_face");
	public static final ResourceLocation HANDSLOT_TEX = tex("emptyslot_hand");
	public static final ResourceLocation AMMOSLOT_TEX = tex("emptyslot_bullet");
	public static final ResourceLocation FOODSLOT_TEX = tex("emptyslot_food");
	public static final ResourceLocation HEALSLOT_TEX = tex("emptyslot_heal");

	protected static ResourceLocation tex(String name) {
		return new ResourceLocation(Techguns.MODID, "textures/gui/emptyslots/" + name + ".png");
	}

	public SlotTG(Container inv, int index, int posx, int posy, TGSlotType type) {
		super(inv, index, posx, posy);
		this.type = type;
	}

	@Override
	public boolean mayPlace(ItemStack itemstack) {
		if (!itemstack.isEmpty() && itemstack.getItem() instanceof ITGSpecialSlot) {
			ITGSpecialSlot slot = (ITGSpecialSlot) itemstack.getItem();
			return slot.getSlot(itemstack) == type;
		}
		return TGSlotType.NORMAL == type;
	}

	public TGSlotType getType() {
		return type;
	}

	/**
	 * 16x16 texture drawn by the screen when the slot is empty
	 */
	@Nullable
	public ResourceLocation getEmptySlotTexture() {
		switch (type) {
		case AMMOSLOT:
			return AMMOSLOT_TEX;
		case BACKSLOT:
			return BACKSLOT_TEX;
		case FACESLOT:
			return FACESLOT_TEX;
		case FOODSLOT:
			return FOODSLOT_TEX;
		case HANDSLOT:
			return HANDSLOT_TEX;
		case HEALSLOT:
			return HEALSLOT_TEX;
		default:
			break;
		}
		return null;
	}

}
