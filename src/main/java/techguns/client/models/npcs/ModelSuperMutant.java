package techguns.client.models.npcs;

import techguns.client.models.TGModelRenderer;

/**
 * ModelSupermutant - pWn3d
 * Created using Tabula 4.1.1
 * The 1.12 render scale (SuperMutantBasic.getModelScale) is applied by NPCHumanoidModel.
 */
public class ModelSuperMutant extends ModelGenericNPC {

	public TGModelRenderer rightarmUpper;
	public TGModelRenderer leftBoot;
	public TGModelRenderer chestarmorfront;
	public TGModelRenderer chestarmorback;
	public TGModelRenderer belt;
	public TGModelRenderer leftarm_upper;
	public TGModelRenderer rightBoot;

	public ModelSuperMutant() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.rightBoot = new TGModelRenderer(this, 0, 33);
		this.rightBoot.setRotationPoint(-0.6F, 6.0F, -0.7F);
		this.rightBoot.addBox(-2.0F, 0.0F, -2.0F, 5, 6, 5, 0.0F);
		this.bipedLeftArm = new TGModelRenderer(this, 40, 16);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		this.bipedRightLeg = new TGModelRenderer(this, 0, 16);
		this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
		this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.bipedBody = new TGModelRenderer(this, 16, 16);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		this.leftBoot = new TGModelRenderer(this, 0, 33);
		this.leftBoot.mirror = true;
		this.leftBoot.setRotationPoint(-0.4F, 6.0F, -0.7F);
		this.leftBoot.addBox(-2.0F, 0.0F, -2.0F, 5, 6, 5, 0.0F);
		this.bipedHeadwear = new TGModelRenderer(this, 32, 0);
		this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
		this.bipedRightArm = new TGModelRenderer(this, 40, 16);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		this.chestarmorfront = new TGModelRenderer(this, 21, 34);
		this.chestarmorfront.setRotationPoint(1.0F, 1.0F, -1.0F);
		this.chestarmorfront.addBox(-4.0F, 0.0F, -2.0F, 6, 7, 1, 0.0F);
		this.chestarmorback = new TGModelRenderer(this, 21, 44);
		this.chestarmorback.setRotationPoint(1.0F, 1.0F, 4.0F);
		this.chestarmorback.addBox(-4.0F, 0.0F, -2.0F, 6, 7, 1, 0.0F);
		this.belt = new TGModelRenderer(this, 0, 53);
		this.belt.setRotationPoint(-4.5F, 9.0F, -2.5F);
		this.belt.addBox(0.0F, 0.0F, 0.0F, 9, 4, 5, 0.0F);
		this.leftarm_upper = new TGModelRenderer(this, 40, 33);
		this.leftarm_upper.mirror = true;
		this.leftarm_upper.setRotationPoint(-0.1F, -0.2F, -0.5F);
		this.leftarm_upper.addBox(-1.0F, -2.0F, -2.0F, 5, 7, 5, 0.0F);
		this.bipedLeftLeg = new TGModelRenderer(this, 0, 16);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, -0.0F);
		this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.rightarmUpper = new TGModelRenderer(this, 40, 33);
		this.rightarmUpper.setRotationPoint(-0.9F, -0.2F, -0.5F);
		this.rightarmUpper.addBox(-3.0F, -2.0F, -2.0F, 5, 7, 5, 0.0F);
		this.bipedHead = new TGModelRenderer(this, 0, 0);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.bipedRightLeg.addChild(this.rightBoot);
		this.bipedLeftLeg.addChild(this.leftBoot);
		this.bipedBody.addChild(this.chestarmorfront);
		this.bipedBody.addChild(this.chestarmorback);
		this.bipedBody.addChild(this.belt);
		this.bipedLeftArm.addChild(this.leftarm_upper);
		this.bipedRightArm.addChild(this.rightarmUpper);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(TGModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
