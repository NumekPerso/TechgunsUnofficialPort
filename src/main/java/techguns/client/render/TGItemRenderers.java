package techguns.client.render;

/**
 * Registers the IItemRenderers of all Techguns items (port of 1.12 ClientProxy.registerItemRenderers).
 * The registrations are split in parts so they can be ported independently.
 */
public class TGItemRenderers {

	public static void init() {
		TGGunRenderersPart1.register();
		TGGunRenderersPart2.register();
	}
}
