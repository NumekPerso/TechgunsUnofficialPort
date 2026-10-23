package techguns.client.models.gibs;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

/**
 * Every top level part of a model is a gib (1.12: all boxes of the boxList which are not children and not hidden)
 */
public class ModelGibsGeneric extends ModelGibs{

	List<ModelPart> gibs = new ArrayList<ModelPart>();

	/**
	 * @param root baked root part of a model, its direct children are the gibs
	 */
	public ModelGibsGeneric(ModelPart root) {
		this(getChildren(root));
	}

	/**
	 * @param parts top level parts of the model
	 */
	public ModelGibsGeneric(Iterable<ModelPart> parts) {
		for (ModelPart box : parts) {
			if (box.visible) {
				gibs.add(box);
			}
		}
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float scale, int part) {
		if (part >= 0 && part < gibs.size()) {
			renderPart(gibs.get(part), poseStack, buffer, packedLight, packedOverlay);
		}
	}

	@Override
	public int getNumGibs() {
		return gibs.size();
	}

}
