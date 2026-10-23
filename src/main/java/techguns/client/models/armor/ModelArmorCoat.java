package techguns.client.models.armor;

import techguns.client.models.TGModelRenderer;

public class ModelArmorCoat extends TGModelBiped {

	public TGModelRenderer coatback;
	public TGModelRenderer coatLeftLeg;
	public TGModelRenderer coatRight;
	public TGModelRenderer coatLeft;
	public TGModelRenderer coatRightLeg;
	public TGModelRenderer leftShoulder;
	public TGModelRenderer rightShoulder;

	public ModelArmorCoat(int type, float scale) {
		super(scale, 0.0F, 64, 64);

		if (type == 1) {

			this.coatLeft = new TGModelRenderer(this, 50, 47);
			this.coatLeft.mirror = true;
			this.coatLeft.setRotationPoint(7.0F, -0.1F, -0.5F);
			this.coatLeft.addBox(-4.0F, 0.0F, -2.0F, 2, 12, 5, scale);
			this.bipedBody.addChild(coatLeft);

			this.coatback = new TGModelRenderer(this, 44, 35);
			this.coatback.setRotationPoint(-1.0F, -0.1F, 4.5F);
			this.coatback.addBox(-4.0F, 0.0F, -2.0F, 10, 12, 0, scale);
			this.bipedBody.addChild(coatback);

			this.coatRight = new TGModelRenderer(this, 50, 47);
			this.coatRight.setRotationPoint(-1.0F, -0.1F, -0.5F);
			this.coatRight.addBox(-4.0F, 0.0F, -2.0F, 2, 12, 5, scale);
			this.bipedBody.addChild(coatRight);

			this.leftShoulder = new TGModelRenderer(this, 0, 34);
			this.leftShoulder.mirror = true;
			this.leftShoulder.setRotationPoint(1.0F, -0.5F, -1.0F);
			this.leftShoulder.addBox(-2.0F, -2.5F, -2.0F, 5, 3, 6, scale);
			this.setRotateAngle(leftShoulder, 0.0F, 0.0F, 0.22689280275926282F);
			this.bipedLeftArm.addChild(leftShoulder);

			this.rightShoulder = new TGModelRenderer(this, 0, 34);
			this.rightShoulder.setRotationPoint(-1.0F, -0.5F, -1.0F);
			this.rightShoulder.addBox(-3.0F, -2.5F, -2.0F, 5, 3, 6, scale);
			this.setRotateAngle(rightShoulder, 0.0F, 0.0F, -0.22689280275926282F);
			this.bipedRightArm.addChild(rightShoulder);

		} else if (type == 2) {
			this.coatLeftLeg = new TGModelRenderer(this, 0, 48);
			this.coatLeftLeg.mirror = true;
			this.coatLeftLeg.setRotationPoint(0.1F, -0.1F, -0.5F);
			this.coatLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 5, 10, 5, scale);
			this.bipedLeftLeg.addChild(coatLeftLeg);

			this.coatRightLeg = new TGModelRenderer(this, 0, 48);
			this.coatRightLeg.setRotationPoint(-1.1F, -0.1F, -0.5F);
			this.coatRightLeg.addBox(-2.0F, 0.0F, -2.0F, 5, 10, 5, scale);
			this.bipedRightLeg.addChild(coatRightLeg);
		}
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
