package techguns.client.render.item;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.model.geom.ModelPart;
import techguns.client.models.TGModelRenderer;
import techguns.client.models.armor.TGModelBiped;

/**
 * 3D inventory rendering of the power armors (1.12 RenderArmorItem)
 */
public class RenderArmorItem extends RenderItemBase {

	protected TGModelBiped modelBiped;
	protected EquipmentSlot armorSlot;

	public RenderArmorItem(TGModelBiped model, ResourceLocation texture, EquipmentSlot armorSlot) {
		super(null, texture);
		this.modelBiped = model;
		this.armorSlot = armorSlot;

		//gui, item frame and ground: fitted to the model size (renderItem)
		this.scale_thirdp = 1.0f;
		this.scale_ego = 1.0f;

		this.translateType[0][1] = 0.1f;
		this.translateType[1][1] = 0.04f;
	}

	/**
	 * Bounding box of the visible parts in model pixels (minX, minY, minZ, maxX, maxY, maxZ), computed once
	 */
	protected float[] guiBounds = null;

	@Override
	public void renderItem(ItemDisplayContext transform, ItemStack stack, @Nullable LivingEntity elb, boolean leftHanded, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		poseStack.pushPose();

		this.modelBiped.setDefaultPose();
		this.modelBiped.setVisibleForSlot(this.armorSlot, true);

		if (this.guiBounds == null) {
			this.guiBounds = computeBounds();
		}
		float[] b = this.guiBounds;
		float maxDim = Math.max(b[3] - b[0], Math.max(b[4] - b[1], b[5] - b[2]));

		if (ItemDisplayContext.GUI == transform || ItemDisplayContext.FIXED == transform || ItemDisplayContext.GROUND == transform) {
			//The 1.12 fixed scale/translation made the bulky power armors overflow the slot / item frame and float above
			//the ground: fit the model (gui 0.95 block, item frame 0.75, ground 1 block before the 0.5 ground scale of the item model)
			float size = transform == ItemDisplayContext.GUI ? 0.95f : (transform == ItemDisplayContext.FIXED ? 0.75f : 1.0f);
			if (transform != ItemDisplayContext.GROUND) {
				poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
			}
			float s = maxDim > 0f ? size / (maxDim / 16.0f) : 1.0f;
			poseStack.scale(s, s, s);
			this.setBaseRotation(poseStack, transform);
		} else {
			this.applyTranslation(poseStack, transform);
			this.setBaseScale(poseStack, elb, transform);
			this.setBaseRotation(poseStack, transform);
		}
		//center of the model in the hand / slot: the 1.12 base translation was a fixed height per slot, the models were
		//far from the hand (helmet 0.8 block above it)
		poseStack.translate(-(b[0] + b[3]) * 0.5f / 16.0f, -(b[1] + b[4]) * 0.5f / 16.0f, -(b[2] + b[5]) * 0.5f / 16.0f);

		VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(this.texture));
		this.modelBiped.render(poseStack, vc, packedLight, packedOverlay, 1f, 1f, 1f, 1f, false, SCALE);

		poseStack.popPose();
	}

	protected float[] computeBounds() {
		float[] b = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };
		TGModelRenderer[] parts = { modelBiped.bipedHead, modelBiped.bipedHeadwear, modelBiped.bipedBody, modelBiped.bipedRightArm, modelBiped.bipedLeftArm, modelBiped.bipedRightLeg, modelBiped.bipedLeftLeg };
		for (TGModelRenderer part : parts) {
			addBounds(part, 0f, 0f, 0f, b);
		}
		if (b[0] > b[3]) {
			return new float[] { -8f, -8f, -8f, 8f, 8f, 8f };
		}
		return b;
	}

	/**
	 * Rotations are ignored, the box is only used to scale and center the GUI icon
	 */
	protected static void addBounds(TGModelRenderer part, float ox, float oy, float oz, float[] b) {
		if (part == null || part.isHidden || !part.showModel) {
			return;
		}
		float x = ox + part.offsetX * 16f + part.rotationPointX;
		float y = oy + part.offsetY * 16f + part.rotationPointY;
		float z = oz + part.offsetZ * 16f + part.rotationPointZ;
		for (ModelPart.Cube c : part.cubeList) {
			b[0] = Math.min(b[0], x + c.minX);
			b[1] = Math.min(b[1], y + c.minY);
			b[2] = Math.min(b[2], z + c.minZ);
			b[3] = Math.max(b[3], x + c.maxX);
			b[4] = Math.max(b[4], y + c.maxY);
			b[5] = Math.max(b[5], z + c.maxZ);
		}
		if (part.childModels != null) {
			for (TGModelRenderer child : part.childModels) {
				addBounds(child, x, y, z, b);
			}
		}
	}
}
