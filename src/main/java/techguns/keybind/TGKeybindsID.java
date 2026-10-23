package techguns.keybind;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * contains the keybind id constants, also needed server side
 */
public class TGKeybindsID {
	public static final byte TOGGLE_NIGHTVISION = 0;
	public static final byte TOGGLE_SAFEMODE = 1;
	/**
	 * Uses Jump default keybind, need id for packet;
	 */
	public static final byte JETPACK_BOOST_START = 2;
	/**
	 * Uses Jump default keybind, need id for packet;
	 */
	public static final byte JETPACK_BOOST_STOP = 3;
	/**
	 * used for jetpack accelaration, needs id for packet
	 */
	public static final byte JETPACK_FORWARD_START = 4;
	/**
	 * used for jetpack accelaration, needs id for packet
	 */
	public static final byte JETPACK_FORWARD_STOP = 5;
	public static final byte FORCE_RELOAD = 6;
	public static final byte TOGGLE_JETPACK = 7;
	public static final byte TOGGLE_STEP_ASSIST = 8;
	public static final byte TOGGLE_HUD = 9;

	private static String getLangKeyForID(int id) {
		switch (id) {
		case TOGGLE_NIGHTVISION:
			return "techguns.armorTooltip.nightvision";
		case TOGGLE_JETPACK:
			return "techguns.msg.enablejetpack";
		case TOGGLE_SAFEMODE:
			return "techguns.msg.safemode";
		case TOGGLE_STEP_ASSIST:
			return "techguns.armorTooltip.stepassist";
		default:
			return "UNKNOWN";
		}
	}

	/**
	 * Replaces the 1.12 PacketShowKeybindConfirmMessage, translation happens on the client since only keys are sent
	 */
	public static void showConfirmMessage(ServerPlayer ply, byte id, boolean state) {
		Component msg = Component.literal("[Techguns]: ").withStyle(ChatFormatting.YELLOW)
				.append(Component.translatable("techguns.msg.keybindtogglechange").withStyle(ChatFormatting.WHITE))
				.append(Component.literal(" [").withStyle(ChatFormatting.YELLOW))
				.append(Component.translatable(getLangKeyForID(id)).withStyle(ChatFormatting.YELLOW))
				.append(Component.literal("] ").withStyle(ChatFormatting.YELLOW))
				.append(Component.translatable("techguns.msg.keybindtogglechange2").withStyle(ChatFormatting.WHITE))
				.append(Component.literal(" [").withStyle(state ? ChatFormatting.GREEN : ChatFormatting.DARK_RED))
				.append(Component.translatable("techguns.container.info." + (state ? "on" : "off")).withStyle(state ? ChatFormatting.GREEN : ChatFormatting.DARK_RED))
				.append(Component.literal("]").withStyle(state ? ChatFormatting.GREEN : ChatFormatting.DARK_RED));
		ply.displayClientMessage(msg, true);
	}
}
