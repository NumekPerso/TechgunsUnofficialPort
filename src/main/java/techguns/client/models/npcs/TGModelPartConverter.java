package techguns.client.models.npcs;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.model.geom.ModelPart;
import techguns.client.models.TGModelRenderer;

/**
 * Builds vanilla ModelParts from the 1.12 style TGModelRenderer parts of the NPC models: the cubes are the same objects (exact
 * 1.12 geometry and UV, see TGModelRenderer), so the NPC models can be used by the vanilla entity renderers and layers (armor,
 * held items) and by the death effects / gibs, which all work on ModelParts. Both use the same Z, Y, X rotation order.
 */
public class TGModelPartConverter {

	protected final IdentityHashMap<TGModelRenderer, ModelPart> parts = new IdentityHashMap<>();

	/**
	 * Converts a part and its children (children are named "child0", "child1", ...)
	 */
	public ModelPart convert(TGModelRenderer r) {
		Map<String, ModelPart> children = new LinkedHashMap<>();
		if (r.childModels != null) {
			for (int i = 0; i < r.childModels.size(); i++) {
				children.put("child" + i, this.convert(r.childModels.get(i)));
			}
		}
		ModelPart part = new ModelPart(new ArrayList<>(r.cubeList), children);
		this.parts.put(r, part);
		sync(r, part);
		return part;
	}

	/**
	 * Root part without cubes holding the given named parts
	 */
	public static ModelPart root(Map<String, ModelPart> children) {
		return new ModelPart(List.of(), children);
	}

	public ModelPart get(TGModelRenderer r) {
		return this.parts.get(r);
	}

	/**
	 * Copies the poses (rotation point, offset, angles, visibility) of all converted TG parts to their ModelParts
	 */
	public void syncAll() {
		this.parts.forEach(TGModelPartConverter::sync);
	}

	public static void sync(TGModelRenderer r, ModelPart p) {
		//1.12 offset is applied before the rotation point translation, in blocks
		p.setPos(r.rotationPointX + r.offsetX * 16f, r.rotationPointY + r.offsetY * 16f, r.rotationPointZ + r.offsetZ * 16f);
		p.setRotation(r.rotateAngleX, r.rotateAngleY, r.rotateAngleZ);
		p.visible = r.showModel && !r.isHidden;
	}
}
