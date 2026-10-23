package techguns.client.armor;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import techguns.client.render.item.TileEntityItemRendererTGShield;

/**
 * Client item extensions of the Techguns shields: 3D shield renderer
 */
public class TGShieldClientExtensions implements IClientItemExtensions {

	public static final TGShieldClientExtensions INSTANCE = new TGShieldClientExtensions();

	@Override
	public BlockEntityWithoutLevelRenderer getCustomRenderer() {
		return TileEntityItemRendererTGShield.getInstance();
	}
}
