package techguns.client.models.npcs;

import java.util.List;

import net.minecraft.world.entity.Entity;
import techguns.client.models.TGModelRenderer;

/**
 * 1.12 style non humanoid entity model (ModelBase with setRotationAngles + render of top level parts), wrapped by TGEntityModel
 */
public interface ITGEntityModel {

	/**
	 * Top level parts rendered by the 1.12 render() method, in that order
	 */
	List<TGModelRenderer> getRenderedParts();

	void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn);

	/**
	 * Translation (blocks) applied before rendering the parts (1.12 render() GlStateManager.translate)
	 */
	default float getRenderOffsetY() {
		return 0f;
	}
}
