package techguns.client.models.gibs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import techguns.Techguns;
import techguns.client.models.TGModelBase;

/**
 * Model of the body parts flying around after a gore death.
 *
 * 1.12 gib models wrapped the vanilla ModelBase of the mob (new ModelBiped(), ...) and rendered single ModelRenderers
 * with their rotation point set to 0. 1.20 vanilla models are baked ModelParts: the gib models wrap ModelParts (baked
 * from the vanilla layer definitions or taken from the renderer model) and render them with their position set to 0.
 * The pose is restored after rendering, so the parts of a live renderer model can be used too.
 */
public abstract class ModelGibs extends TGModelBase {

	public abstract void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float scale, int part);

	public abstract int getNumGibs();

	/**
	 * 1.12 part.setRotationPoint(0,0,0); part.render(scale)
	 */
	public static void renderPart(ModelPart part, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
		if (part == null) return;
		PartPose pose = part.storePose();
		part.setPos(0.0f, 0.0f, 0.0f);
		part.render(poseStack, buffer, packedLight, packedOverlay);
		part.loadPose(pose);
	}

	/**
	 * Renders only the cubes of a part (no children, no pose), like a 1.12 ModelRenderer without rotation point
	 */
	public static void renderPartCubesOnly(ModelPart part, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
		if (part == null || !part.visible) return;
		PoseStack.Pose pose = poseStack.last();
		for (ModelPart.Cube cube : getCubes(part)) {
			cube.compile(pose, buffer, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
		}
	}

	/**
	 * Direct children of a ModelPart (private in 1.20)
	 */
	public static List<ModelPart> getChildren(ModelPart part) {
		try {
			Map<String, ModelPart> children = ObfuscationReflectionHelper.getPrivateValue(ModelPart.class, part, "f_104213_");
			if (children != null) {
				return new ArrayList<>(children.values());
			}
		} catch (Exception e) {
			Techguns.LOGGER.error("Can't access ModelPart children", e);
		}
		return Collections.emptyList();
	}

	public static List<ModelPart.Cube> getCubes(ModelPart part) {
		try {
			List<ModelPart.Cube> cubes = ObfuscationReflectionHelper.getPrivateValue(ModelPart.class, part, "f_104212_");
			if (cubes != null) {
				return cubes;
			}
		} catch (Exception e) {
			Techguns.LOGGER.error("Can't access ModelPart cubes", e);
		}
		return Collections.emptyList();
	}

	/**
	 * Child of a part or null if it doesn't exist
	 */
	public static ModelPart getChildOrNull(ModelPart part, String name) {
		return part != null && part.hasChild(name) ? part.getChild(name) : null;
	}
}
