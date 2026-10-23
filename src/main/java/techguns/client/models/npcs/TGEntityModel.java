package techguns.client.models.npcs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import techguns.client.models.TGModelRenderer;

/**
 * Vanilla entity model wrapping a 1.12 style non humanoid model (ITGEntityModel): the 1.12 setRotationAngles animates the TG parts,
 * their poses are copied to ModelParts with the same cubes. Being a HierarchicalModel, the gib / death effects can split it in parts.
 */
public class TGEntityModel<T extends Entity> extends HierarchicalModel<T> {

	protected final ITGEntityModel model;
	protected final TGModelPartConverter converter = new TGModelPartConverter();
	protected final ModelPart root;

	public TGEntityModel(ITGEntityModel model) {
		this.model = model;
		Map<String, ModelPart> parts = new LinkedHashMap<>();
		List<TGModelRenderer> rendered = model.getRenderedParts();
		for (int i = 0; i < rendered.size(); i++) {
			parts.put("part" + i, this.converter.convert(rendered.get(i)));
		}
		this.root = TGModelPartConverter.root(parts);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 0.0625f, entity);
		this.converter.syncAll();
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		float offset = this.model.getRenderOffsetY();
		if (offset != 0f) {
			poseStack.pushPose();
			poseStack.translate(0f, offset, 0f);
			super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();
		} else {
			super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}
}
