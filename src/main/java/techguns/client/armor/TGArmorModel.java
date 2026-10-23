package techguns.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import techguns.client.models.armor.TGModelBiped;

/**
 * Armor model returned by IClientItemExtensions.getGenericArmorModel: renders a 1.12 style TGModelBiped with the part poses
 * of the vanilla armor model it replaces (which got the poses of the entity model from HumanoidArmorLayer).
 */
public class TGArmorModel extends Model {

	protected final TGModelBiped biped;

	protected HumanoidModel<?> base;
	protected EquipmentSlot slot = EquipmentSlot.HEAD;
	protected boolean showHeadwear = true;
	protected boolean young = false;

	public TGModelBiped getBiped() {
		return this.biped;
	}

	public TGArmorModel(TGModelBiped biped) {
		super(RenderType::armorCutoutNoCull);
		this.biped = biped;
	}

	/**
	 * Set before each render (called from getGenericArmorModel right before the armor layer renders this model)
	 */
	public TGArmorModel setup(HumanoidModel<?> base, EquipmentSlot slot, boolean showHeadwear) {
		this.base = base;
		this.slot = slot;
		this.showHeadwear = showHeadwear;
		this.young = base.young;
		return this;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (this.base != null) {
			this.biped.setPosesFrom(this.base);
		}
		this.biped.setVisibleForSlot(this.slot, this.showHeadwear);
		this.biped.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha, this.young, 0.0625f);
	}
}
