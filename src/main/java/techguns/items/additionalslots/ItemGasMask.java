package techguns.items.additionalslots;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import techguns.api.tginventory.TGSlotType;

public class ItemGasMask extends ItemTGSpecialSlot {

	public static final UUID UUID_RAD_RESIST_FACE = UUID.fromString("1B428AAE-2ECA-42C6-A14F-0A8B3F507276");

	protected float radresist = 0f;

	public ItemGasMask(String unlocalizedName, int camoCount, int dur) {
		super(unlocalizedName, TGSlotType.FACESLOT, camoCount, dur);
	}

	public ItemGasMask setRadresist(float radresist) {
		this.radresist = radresist;
		return this;
	}

	public float getRadresist() {
		return this.radresist;
	}

	@Override
	public void onPlayerTick(ItemStack item, PlayerTickEvent event) {
		if (event.player.hasEffect(MobEffects.POISON) && item.getDamageValue() < item.getMaxDamage()) {
			event.player.removeEffect(MobEffects.POISON);
			item.setDamageValue(item.getDamageValue() + 1);
		}
		if (event.player.hasEffect(MobEffects.WITHER) && item.getDamageValue() < item.getMaxDamage()) {
			event.player.removeEffect(MobEffects.WITHER);
			item.setDamageValue(item.getDamageValue() + 1);
		}
		//1.12 also applied the radiation resistance attribute modifier (UUID_RAD_RESIST_FACE) of radresist here,
		//done by radiation.RadiationEventHandler.updateFaceSlotRadResistance in the port
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		if (this.radresist > 0) {
			tooltip.add(Component.literal("RAD Resistance: " + this.radresist).withStyle(ChatFormatting.BLUE));
		}
	}

}
