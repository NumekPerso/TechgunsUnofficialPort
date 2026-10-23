package techguns.client.render.tileentities;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Client extension of machine items rendered by a block entity renderer
 */
public class TGMachineItemClientExtensions implements IClientItemExtensions {

	public static final TGMachineItemClientExtensions INSTANCE = new TGMachineItemClientExtensions();

	@Override
	public BlockEntityWithoutLevelRenderer getCustomRenderer() {
		return TGMachineItemRenderer.getInstance();
	}
}
