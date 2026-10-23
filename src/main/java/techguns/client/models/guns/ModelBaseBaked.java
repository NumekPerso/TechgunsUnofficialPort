package techguns.client.models.guns;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import techguns.client.models.ModelMultipart;
import techguns.client.models.TGObjModel;

/**
 * Model of an OBJ file (1.12: baked Forge OBJ model rendered with RenderItem.renderModel). The texture comes from the item renderer
 * (the gun texture), the OBJ files use flip-v like the 1.12 blockstates.
 */
public class ModelBaseBaked extends ModelMultipart {

	protected List<TGObjModel> objModels = new ArrayList<>();

	/**
	 * @param objLocations
	 *            locations of the OBJ files, e.g. techguns:models/item/gaussrifle.obj
	 */
	public ModelBaseBaked(ResourceLocation... objLocations) {
		for (ResourceLocation loc : objLocations) {
			this.objModels.add(new TGObjModel(loc, true));
		}
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, int ammoLeft, float reloadProgress, ItemDisplayContext transformType, int part, float fireProgress, float chargeProgress) {
		objModels.get(0).render(poseStack, buffer, packedLight, packedOverlay);
	}

}
