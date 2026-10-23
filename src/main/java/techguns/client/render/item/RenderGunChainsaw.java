package techguns.client.render.item;

import net.minecraft.world.item.ItemStack;
import techguns.client.models.ModelMultipart;
import techguns.items.guns.GenericGun;

public class RenderGunChainsaw extends RenderGunBase90 {

	protected static final float[] COLOR_HEAD_1 = { 0.9f, 0.55f, 1.0f, 1.0f };
	protected static final float[] COLOR_HEAD_2 = { 0.4f, 0.4f, 0.4f, 1.0f };

	public RenderGunChainsaw(ModelMultipart model, int parts) {
		super(model, parts);
	}

	@Override
	protected float[] getColorForPart(GenericGun gun, int part, ItemStack stack) {
		if (part == 1) {
			int i = gun.getMiningHeadLevel(stack);
			if (i == 1) {
				return COLOR_HEAD_1;
			} else if (i == 2) {
				return COLOR_HEAD_2;
			}
		}
		return null;
	}

}
