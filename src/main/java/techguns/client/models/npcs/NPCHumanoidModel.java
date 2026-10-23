package techguns.client.models.npcs;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import techguns.api.guns.IGenericGun;
import techguns.client.models.armor.TGModelBiped;
import techguns.entities.npcs.SuperMutantBasic;

/**
 * Vanilla HumanoidModel built from a 1.12 style biped model (TGModelBiped): the parts keep the exact 1.12 geometry, the animation
 * is the vanilla humanoid one (same as the 1.12 ModelBiped.setRotationAngles), so vanilla layers (armor, held item) work.
 * ModelGenericNPC arm poses: a gun is held with the bow and arrow pose, other items with the item pose (1.12 ModelGenericNPC).
 */
public class NPCHumanoidModel<T extends LivingEntity> extends HumanoidModel<T> {

	protected final boolean npcArmPoses;

	/**
	 * Render scale of the whole model (1.12 ModelSuperMutant.render: f5*scale)
	 */
	protected float renderScale = 1.0f;

	public NPCHumanoidModel(TGModelBiped model) {
		super(buildRoot(model));
		this.npcArmPoses = model instanceof ModelGenericNPC && ((ModelGenericNPC) model).hasNPCArmPoses();
	}

	public static ModelPart buildRoot(TGModelBiped model) {
		TGModelPartConverter conv = new TGModelPartConverter();
		Map<String, ModelPart> parts = new LinkedHashMap<>();
		parts.put("head", conv.convert(model.bipedHead));
		parts.put("hat", conv.convert(model.bipedHeadwear));
		parts.put("body", conv.convert(model.bipedBody));
		parts.put("right_arm", conv.convert(model.bipedRightArm));
		parts.put("left_arm", conv.convert(model.bipedLeftArm));
		parts.put("right_leg", conv.convert(model.bipedRightLeg));
		parts.put("left_leg", conv.convert(model.bipedLeftLeg));
		return TGModelPartConverter.root(parts);
	}

	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
		if (this.npcArmPoses) {
			setNPCArmPoses(this, entity);
		}
		if (entity instanceof SuperMutantBasic) {
			this.renderScale = ((SuperMutantBasic) entity).getModelScale();
		} else {
			this.renderScale = 1.0f;
		}
	}

	/**
	 * 1.12 ModelGenericNPC.setRotationAngles arm poses
	 */
	public static void setNPCArmPoses(HumanoidModel<?> model, LivingEntity npc) {
		ItemStack held = npc.getMainHandItem();
		boolean left = npc instanceof Mob && ((Mob) npc).isLeftHanded() || npc.getMainArm() == HumanoidArm.LEFT;
		ArmPose pose;
		if (!held.isEmpty() && held.getItem() instanceof IGenericGun) {
			pose = ArmPose.BOW_AND_ARROW;
		} else if (!held.isEmpty()) {
			pose = ArmPose.ITEM;
		} else {
			pose = ArmPose.EMPTY;
		}
		if (left) {
			model.leftArmPose = pose;
			model.rightArmPose = ArmPose.EMPTY;
		} else {
			model.rightArmPose = pose;
			model.leftArmPose = ArmPose.EMPTY;
		}
	}

	public float getRenderScale() {
		return renderScale;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (this.renderScale != 1.0f) {
			poseStack.pushPose();
			poseStack.scale(this.renderScale, this.renderScale, this.renderScale);
			super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();
		} else {
			super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}
}
