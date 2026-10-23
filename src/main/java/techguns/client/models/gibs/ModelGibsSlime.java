package techguns.client.models.gibs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.entity.Entity;
import techguns.client.models.TGModelRenderer;

/**
 * Invisible gibs (only the blood trail particles are visible)
 */
public class ModelGibsSlime extends ModelGibs {

	public TGModelRenderer[] gibs;

	public ModelGibsSlime() {
		super();
		gibs = new TGModelRenderer[] {
				addGib(),
				addGib(),
				addGib(),
				addGib(),
				addGib(),
				addGib(),
				addGib(),
				addGib()
		};
	}

	protected TGModelRenderer addGib() {
		TGModelRenderer r = new TGModelRenderer(this,0,0);
		r.addBox(0, 0, 0, 0, 0, 0);
		r.setRotationPoint(0, 0, 0);
		return r;
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float scale, int part) {
		gibs[part].render(poseStack, buffer, packedLight, packedOverlay, scale);
	}

	@Override
	public int getNumGibs() {
		return gibs.length;
	}

}
