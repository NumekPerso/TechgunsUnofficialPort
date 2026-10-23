package techguns.client.models.npcs;

import techguns.client.models.armor.TGModelBiped;

/**
 * 1.12 ModelGenericNPC: ModelBiped with 64x64 texture (64x32 for the armor models). The arm pose logic of 1.12 setRotationAngles
 * (gun = bow and arrow pose, other item = item pose) is in {@link NPCHumanoidModel#prepareMobModel}.
 */
public class ModelGenericNPC extends TGModelBiped {

	public ModelGenericNPC() {
		this(0.0F, false);
	}

	public ModelGenericNPC(float modelSize, boolean b) {
		super(modelSize, 0.0F, 64, b ? 32 : 64);
	}

	/**
	 * Models with the 1.12 ModelGenericNPC arm poses (ModelCyberDemon extends ModelBiped directly)
	 */
	public boolean hasNPCArmPoses() {
		return true;
	}
}
