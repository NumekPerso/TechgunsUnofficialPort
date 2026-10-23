package techguns.api.guns;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum GunHandType {
	TWO_HANDED,
	ONE_HANDED,
	ONE_POINT_FIVE_HANDED;

	public MutableComponent getDisplayComponent() {
		ChatFormatting color;
		switch (this) {
		case ONE_HANDED:
			color = ChatFormatting.GREEN;
			break;
		case ONE_POINT_FIVE_HANDED:
			color = ChatFormatting.YELLOW;
			break;
		case TWO_HANDED:
		default:
			color = ChatFormatting.GOLD;
			break;
		}
		return Component.translatable("techguns.gunhandtype." + this.name().toLowerCase()).withStyle(color);
	}
}
