package techguns.client.models.npcs;

import techguns.client.models.TGModelRenderer;
import techguns.client.models.armor.TGModelBiped;

/**
 * 1.12 ModelCyberDemon (extends ModelBiped: no NPC arm poses). The biped parts have no boxes, the visible parts are their children.
 * 1.12 set mirror / setTextureSize after addBox (no effect on the boxes), those calls are left out.
 */
public class ModelCyberDemon extends TGModelBiped {
	//fields

	TGModelRenderer head;
	TGModelRenderer body;
	TGModelRenderer rightArm;
	TGModelRenderer leftArm;
	TGModelRenderer rightLeg;
	TGModelRenderer leftLeg;

	TGModelRenderer h2;
	TGModelRenderer h3;
	TGModelRenderer h4;
	TGModelRenderer h5;
	TGModelRenderer h6;
	TGModelRenderer h7;
	TGModelRenderer h8;
	TGModelRenderer b2;
	TGModelRenderer ra2;
	TGModelRenderer la2;
	TGModelRenderer rl2;
	TGModelRenderer rl3;
	TGModelRenderer ll2;
	TGModelRenderer ll3;

	public ModelCyberDemon() {

		textureWidth = 64;
		textureHeight = 64;

		this.bipedHead = new TGModelRenderer(this, 0, 0);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedHeadwear = new TGModelRenderer(this, 32, 0);
		this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedBody = new TGModelRenderer(this, 16, 16);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedRightArm = new TGModelRenderer(this, 40, 16);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.bipedLeftArm = new TGModelRenderer(this, 40, 16);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.bipedRightLeg = new TGModelRenderer(this, 0, 16);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.bipedLeftLeg = new TGModelRenderer(this, 0, 16);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);

		h2 = new TGModelRenderer(this, 48, 0);
		h2.setTextureSize(64, 64);
		h2.addBox(-3F, -5F, -5F, 6, 5, 2);
		setRotation(h2, 0F, 0F, 0F);
		h3 = new TGModelRenderer(this, 52, 7);
		h3.setTextureSize(64, 64);
		h3.addBox(4F, -7F, -2F, 2, 4, 4);
		setRotation(h3, 0F, 0F, 0F);
		head = new TGModelRenderer(this, 0, 0);
		head.setTextureSize(64, 64);
		head.addBox(-4F, -8F, -3F, 8, 8, 7);
		setRotation(head, 0F, 0F, 0F);
		h4 = new TGModelRenderer(this, 30, 8);
		h4.setTextureSize(64, 64);
		h4.addBox(-1F, 0F, -5F, 2, 2, 5);
		h4.setRotationPoint(-7.5F, -5.5F, -1F);
		setRotation(h4, -0.4363323F, 0F, 0F);
		h5 = new TGModelRenderer(this, 52, 7);
		h5.setTextureSize(64, 64);
		h5.addBox(-6F, -7F, -2F, 2, 4, 4);
		setRotation(h5, 0F, 0F, 0F);
		h6 = new TGModelRenderer(this, 24, 0);
		h6.setTextureSize(64, 64);
		h6.addBox(0F, -1.5F, -3F, 4, 3, 3);
		h6.setRotationPoint(6F, -5F, 1.5F);
		setRotation(h6, 0F, 0.6981317F, 0F);
		h7 = new TGModelRenderer(this, 24, 0);
		h7.setTextureSize(64, 64);
		h7.addBox(-4F, -1.5F, -3F, 4, 3, 3);
		h7.setRotationPoint(-6F, -5F, 1.5F);
		setRotation(h7, 0F, -0.6981317F, 0F);
		h8 = new TGModelRenderer(this, 30, 8);
		h8.setTextureSize(64, 64);
		h8.addBox(-1F, 0F, -5F, 2, 2, 5);
		h8.setRotationPoint(7.5F, -5.5F, -1F);
		setRotation(h8, -0.4363323F, 0F, 0F);
		b2 = new TGModelRenderer(this, 0, 28);
		b2.setTextureSize(64, 64);
		b2.addBox(-4.5F, 7F, -2.5F, 9, 5, 5);
		setRotation(b2, 0F, 0F, 0F);
		body = new TGModelRenderer(this, 0, 15);
		body.setTextureSize(64, 64);
		body.addBox(-6F, 0F, -3F, 12, 7, 6);
		setRotation(body, 0F, 0F, 0F);

		rightArm = new TGModelRenderer(this, 44, 16);
		rightArm.setTextureSize(64, 64);
		rightArm.addBox(-6F, -2F, -2.5F, 5, 7, 5);
		setRotation(rightArm, 0F, 0F, 0F);
		ra2 = new TGModelRenderer(this, 48, 28);
		ra2.setTextureSize(64, 64);
		ra2.addBox(-5.5F, 3F, 0.5F, 4, 8, 4);
		setRotation(ra2, -0.5205006F, 0F, 0F);

		la2 = new TGModelRenderer(this, 48, 28);
		la2.setTextureSize(64, 64);
		la2.addBox(1.5F, 3F, 0.5F, 4, 8, 4);
		setRotation(la2, -0.5205006F, 0F, 0F);
		leftArm = new TGModelRenderer(this, 44, 16);
		leftArm.setTextureSize(64, 64);
		leftArm.addBox(1F, -2F, -2.5F, 5, 7, 5);
		setRotation(leftArm, 0F, 0F, 0F);

		rightLeg = new TGModelRenderer(this, 0, 38);
		rightLeg.setTextureSize(64, 64);
		rightLeg.addBox(-3.6F, -1F, -2.5F, 5, 7, 5);
		setRotation(rightLeg, -0.5235988F, 0F, 0F);
		rl2 = new TGModelRenderer(this, 20, 39);
		rl2.setTextureSize(64, 64);
		rl2.addBox(-3.1F, 3F, 1.5F, 4, 7, 4);
		setRotation(rl2, -0.5235988F, 0F, 0F);
		rl3 = new TGModelRenderer(this, 0, 50);
		rl3.setTextureSize(64, 64);
		rl3.addBox(-3.6F, 9F, -4F, 5, 3, 5);
		setRotation(rl3, 0F, 0F, 0F);

		leftLeg = new TGModelRenderer(this, 0, 38);
		leftLeg.setTextureSize(64, 64);
		leftLeg.addBox(-1.4F, -1F, -2.5F, 5, 7, 5);
		setRotation(leftLeg, -0.5235988F, 0F, 0F);
		ll2 = new TGModelRenderer(this, 20, 39);
		ll2.setTextureSize(64, 64);
		ll2.addBox(-0.9F, 3F, 1.5F, 4, 7, 4);
		setRotation(ll2, -0.5235988F, 0F, 0F);
		ll3 = new TGModelRenderer(this, 0, 50);
		ll3.setTextureSize(64, 64);
		ll3.addBox(-1.4F, 9F, -4F, 5, 3, 5);
		setRotation(ll3, 0F, 0F, 0F);

		this.bipedHead.addChild(head);
		this.bipedHead.addChild(h2);
		this.bipedHead.addChild(h3);
		this.bipedHead.addChild(h4);
		this.bipedHead.addChild(h5);
		this.bipedHead.addChild(h6);
		this.bipedHead.addChild(h7);
		this.bipedHead.addChild(h8);

		this.bipedBody.addChild(body);
		this.bipedBody.addChild(b2);

		this.bipedRightArm.addChild(rightArm);
		this.bipedRightArm.addChild(ra2);

		this.bipedLeftArm.addChild(leftArm);
		this.bipedLeftArm.addChild(la2);

		this.bipedRightLeg.addChild(rightLeg);
		this.bipedRightLeg.addChild(rl2);
		this.bipedRightLeg.addChild(rl3);

		this.bipedLeftLeg.addChild(leftLeg);
		this.bipedLeftLeg.addChild(ll2);
		this.bipedLeftLeg.addChild(ll3);

	}

}
