package techguns.util;

import net.minecraft.locale.Language;

public class TextUtil {
	/**
	 * Translate a key with the currently loaded language (client side). On a dedicated server this returns the key.
	 */
	public static String trans(String key) {
		return Language.getInstance().getOrDefault(key);
	}

	public static String transTG(String key) {
		return trans("techguns." + key);
	}
}
