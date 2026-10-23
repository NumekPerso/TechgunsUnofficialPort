package techguns.client.render.item;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import techguns.client.models.ModelMultipart;

/**
 * RenderGunBase90 where one model part is rendered with alpha blending (1.12 models did enableBlendMode(ALPHA) in the middle of render()),
 * used by the nuclear death ray glow.
 */
public class RenderGunBase90Translucent extends RenderGunBase90 {

	protected final int translucentPart;

	public RenderGunBase90Translucent(ModelMultipart model, int parts, int translucentPart) {
		super(model, parts);
		this.translucentPart = translucentPart;
	}

	@Override
	protected RenderType getRenderTypeForPart(ResourceLocation tex, int part) {
		if (part == translucentPart) {
			return RenderType.entityTranslucent(tex);
		}
		return super.getRenderTypeForPart(tex, part);
	}
}
