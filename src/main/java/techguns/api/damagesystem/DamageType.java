package techguns.api.damagesystem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum DamageType {
	PHYSICAL, //Melee hits
	PROJECTILE, //Arrows, bullets,..
	FIRE, //fire, flamethrower
	EXPLOSION, // Explosions like TNT, Rockets
	ENERGY, // Energy Weapons, Lasers, Magic
	POISON, //gas, slimy stuff
	UNRESISTABLE, //Things that should never get reduced with armor value: starve, falldmg, suffocate...
	ICE, //anything cold related
	LIGHTNING, //electricity, shock, lightning magic
	RADIATION, //
	DARK; // dark energy, black holes, dark magic

	public ChatFormatting getColor() {
		switch (this) {
		case PROJECTILE:
			return ChatFormatting.GRAY;
		case FIRE:
			return ChatFormatting.RED;
		case EXPLOSION:
			return ChatFormatting.DARK_RED;
		case ENERGY:
			return ChatFormatting.DARK_AQUA;
		case POISON:
			return ChatFormatting.DARK_GREEN;
		case UNRESISTABLE:
			return ChatFormatting.BLACK;
		case ICE:
			return ChatFormatting.AQUA;
		case LIGHTNING:
			return ChatFormatting.YELLOW;
		case RADIATION:
			return ChatFormatting.GREEN;
		case DARK:
			return ChatFormatting.BLACK;
		case PHYSICAL:
		default:
			return ChatFormatting.DARK_GRAY;
		}
	}

	public MutableComponent getDisplayComponent() {
		return Component.translatable("techguns.TGDamageType." + this.name()).withStyle(this.getColor());
	}
}
