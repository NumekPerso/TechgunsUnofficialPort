package techguns.client.models.gibs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

/**
 * 1.12 defined its own boxes for the 128x128 horse textures of 1.12. 1.20 horse textures use the 64x64 layout of the
 * vanilla HorseModel, so the gibs are the same body parts taken from the baked vanilla horse layer, rendered without
 * their children (saddle, ears, ...) and without rotation point like the 1.12 boxes.
 */
public class ModelGibsHorse extends ModelGibs {

	private final ModelPart head;
	private final ModelPart upperMouth;
	private final ModelPart neck;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart mane;
	private final ModelPart backLeftLeg;

	private final ModelPart backRightLeg;

	private final ModelPart frontLeftLeg;

	private final ModelPart frontRightLeg;


	protected ModelPart[] gibs;

	/**
	 * @param root baked root of the vanilla horse layer (ModelLayers.HORSE / DONKEY / MULE)
	 */
	public ModelGibsHorse(ModelPart root)
	{
		this.body = getChildOrNull(root, "body");
		this.tail = getChildOrNull(this.body, "tail");
		this.neck = getChildOrNull(root, "head_parts");
		this.head = getChildOrNull(this.neck, "head");
		this.upperMouth = getChildOrNull(this.neck, "upper_mouth");
		this.mane = getChildOrNull(this.neck, "mane");
		this.backLeftLeg = getChildOrNull(root, "left_hind_leg");
		this.backRightLeg = getChildOrNull(root, "right_hind_leg");
		this.frontLeftLeg = getChildOrNull(root, "left_front_leg");
		this.frontRightLeg = getChildOrNull(root, "right_front_leg");

		this.gibs = new ModelPart[] {
				this.body,
				this.tail,
				this.mane,
				this.backLeftLeg,
				this.backRightLeg,
				this.frontLeftLeg,
				this.frontRightLeg,
				this.head,
				this.upperMouth,
				this.neck
		};
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Entity entityIn, float scale, int part) {
		renderPartCubesOnly(this.gibs[part], poseStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public int getNumGibs() {
		return this.gibs.length;
	}

}
