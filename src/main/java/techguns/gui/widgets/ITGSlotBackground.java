package techguns.gui.widgets;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import techguns.Techguns;

/**
 * Slot with a background icon drawn while it is empty (1.12 Slot.getSlotTexture, sprites of textures/gui/emptyslots).
 * The icon texture (16x16 png) is drawn directly by TGBaseGui, so the empty slot textures don't need an atlas.
 */
public interface ITGSlotBackground {

	public static final ResourceLocation INGOTSLOT_TEX = emptySlot("emptyslot_ingot");
	public static final ResourceLocation INGOTDARKSLOT_TEX = emptySlot("emptyslot_ingot_dark");
	public static final ResourceLocation POWDERSLOT_TEX = emptySlot("emptyslot_powder");
	public static final ResourceLocation WIRESSLOT_TEX = emptySlot("emptyslot_wires");
	public static final ResourceLocation PLATESLOT_TEX = emptySlot("emptyslot_plate");
	public static final ResourceLocation AMMOSLOT_TEX = emptySlot("emptyslot_bullet");
	public static final ResourceLocation AMMOEMPTYSLOT_TEX = emptySlot("emptyslot_bullet_empty");
	public static final ResourceLocation BOTTLESLOT_TEX = emptySlot("emptyslot_bottle");
	public static final ResourceLocation TURRETARMORSLOT_TEX = emptySlot("emptyslot_turretarmor");
	public static final ResourceLocation TURRETGUNSLOT_TEX = emptySlot("emptyslot_turretgun");

	public static ResourceLocation emptySlot(String name) {
		return new ResourceLocation(Techguns.MODID, "textures/gui/emptyslots/" + name + ".png");
	}

	/**
	 * @return texture drawn in the empty slot, null for none
	 */
	@Nullable
	public ResourceLocation getBackgroundTexture();
}
