package techguns.client.models.armor;

import techguns.client.models.TGModelRenderer;

public class ModelT4PowerArmorMk2 extends TGModelBiped {

	public TGModelRenderer RA03;
	public TGModelRenderer RA01;
	public TGModelRenderer RA04;
	public TGModelRenderer RA05;
	public TGModelRenderer RA06;
	public TGModelRenderer LA01;
	public TGModelRenderer LA03;
	public TGModelRenderer LA04;
	public TGModelRenderer LA05;
	public TGModelRenderer LA06;
	public TGModelRenderer H01;
	public TGModelRenderer H04;
	public TGModelRenderer H06;
	public TGModelRenderer H03;
	public TGModelRenderer H09;
	public TGModelRenderer H08;
	public TGModelRenderer H05;
	public TGModelRenderer H07;
	public TGModelRenderer H02;
	public TGModelRenderer H10;
	public TGModelRenderer H11;
	public TGModelRenderer H12;
	public TGModelRenderer B01;
	public TGModelRenderer B02;
	public TGModelRenderer B03;
	public TGModelRenderer B04;
	public TGModelRenderer B05;
	//public TGModelRenderer B09;
	public TGModelRenderer B07;
	public TGModelRenderer B06;
	//public TGModelRenderer B08;
	public TGModelRenderer B10;
	public TGModelRenderer B12;
	public TGModelRenderer B13;
	public TGModelRenderer B14;
	public TGModelRenderer P03;
	//public TGModelRenderer P01;
	public TGModelRenderer P02;
	public TGModelRenderer RL01;
	public TGModelRenderer RL02;
	public TGModelRenderer RL03;

	public TGModelRenderer LL01;
	public TGModelRenderer LL02;
	public TGModelRenderer LL03;

	public TGModelRenderer RB01;
	public TGModelRenderer RB02;
	public TGModelRenderer LB01;
	public TGModelRenderer LB02;

	public ModelT4PowerArmorMk2(int type) {
		this(type, 0.0f);
	}

	public ModelT4PowerArmorMk2(int type, float modelSize) {
		super(modelSize);

		this.textureWidth = 128;
		this.textureHeight = 64;

		this.bipedHead = new TGModelRenderer(this, 0, 0);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedHeadwear = new TGModelRenderer(this, 32, 0);
		this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedBody = new TGModelRenderer(this, 16, 16);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedRightArm = new TGModelRenderer(this, 40, 16);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.bipedLeftArm = new TGModelRenderer(this, 40, 16);
		// this.bipedLeftArm.mirror = false;
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.bipedRightLeg = new TGModelRenderer(this, 0, 16);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.bipedLeftLeg = new TGModelRenderer(this, 0, 16);
		// this.bipedLeftLeg.mirror = false;
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);

	    float ArmOffsetX = 5.0F;
	    float ArmOffsetY = -2.0F;
	    float LegOffsetX = 1.9F;
	    float LegOffsetY = -12.0F;
		
	    this.H01 = new TGModelRenderer(this, 0, 0);
        this.H01.setRotationPoint(-4.5F, -8.5F, -4.5F);
        this.H01.addBox(0.0F, 0.0F, 0.0F, 9, 9, 9, 0.0F);
        this.H02 = new TGModelRenderer(this, 0, 19);
        this.H02.setRotationPoint(-2.0F, -2.2F, -6.0F);
        this.H02.addBox(0.0F, 0.0F, 0.0F, 4, 3, 3, 0.0F);
        this.H03 = new TGModelRenderer(this, 0, 28);
        this.H03.setRotationPoint(2.0F, -2.0F, -5.5F);
        this.H03.addBox(0.0F, 0.0F, 0.0F, 3, 2, 2, 0.0F);
        this.H04 = new TGModelRenderer(this, 0, 28);
        this.H04.setRotationPoint(-2.0F, -2.0F, -5.5F);
        this.H04.addBox(-3.0F, 0.0F, 0.0F, 3, 2, 2, 0.0F);    
        this.H05 = new TGModelRenderer(this, 13, 27);
        this.H05.setRotationPoint(4.0F, -2.0F, -3.5F);
        this.H05.addBox(0.0F, 0.0F, 0.0F, 1, 2, 8, 0.0F);
	    this.H06 = new TGModelRenderer(this, 13, 27);
        this.H06.setRotationPoint(-5.0F, -2.0F, -3.5F);
        this.H06.addBox(0.0F, 0.0F, 0.0F, 1, 2, 8, 0.0F);
        this.H08 = new TGModelRenderer(this, 19, 19);
        this.H08.setRotationPoint(-4.5F, -2.0F, 4.0F);
        this.H08.addBox(0.0F, 0.0F, 0.0F, 9, 2, 1, 0.0F);
        this.H07 = new TGModelRenderer(this, 0, 33);
        this.H07.setRotationPoint(-1.5F, -4.2F, -6.0F);
        this.H07.addBox(0.0F, 0.0F, 0.0F, 3, 2, 2, 0.0F);
        this.H10 = new TGModelRenderer(this, 38, 0);
        this.H10.setRotationPoint(4.5F, -5.6F, -1.5F);
        this.H10.addBox(0.0F, 0.0F, 0.0F, 1, 6, 3, 0.0F);
        this.H11 = new TGModelRenderer(this, 38, 0);
        this.H11.mirror = true;
        this.H11.setRotationPoint(-5.5F, -5.6F, -1.5F);
        this.H11.addBox(0.0F, 0.0F, 0.0F, 1, 6, 3, 0.0F);
        this.H12 = new TGModelRenderer(this, 97, 15);
        this.H12.setRotationPoint(-2.0F, -9.5F, -2.0F);
        this.H12.addBox(0.0F, 0.0F, 0.0F, 4, 1, 7, 0.0F);
        this.H09 = new TGModelRenderer(this, 24, 23);
        this.H09.setRotationPoint(-2.0F, -8.5F, 4.2F);
        this.H09.addBox(0.0F, 0.0F, 0.0F, 4, 9, 1, 0.0F);

        
	    this.RA01 = new TGModelRenderer(this, 69, 38);
        this.RA01.setRotationPoint(-8.5F+ArmOffsetX, -0.5F+ArmOffsetY, -2.5F);
        this.RA01.addBox(0.0F, 0.0F, 0.0F, 5, 13, 5, 0.0F);
        this.RA03 = new TGModelRenderer(this, 68, 27);
        this.RA03.mirror = true;
        this.RA03.setRotationPoint(-9.0F+ArmOffsetX, -1.0F+ArmOffsetY, -3.0F);
        this.RA03.addBox(0.0F, 0.0F, 0.0F, 4, 4, 6, 0.0F);
        this.RA05 = new TGModelRenderer(this, 56, 38);
        this.RA05.setRotationPoint(-9.2F+ArmOffsetX, 6.5F+ArmOffsetY, -2.0F);
        this.RA05.addBox(0.0F, 0.0F, 0.0F, 1, 5, 4, 0.0F);
        this.RA06 = new TGModelRenderer(this, 46, 38);
        this.RA06.setRotationPoint(-8.0F+ArmOffsetX, 4.0F+ArmOffsetY, 2.0F);
        this.RA06.addBox(0.0F, 0.0F, 0.0F, 3, 4, 1, 0.0F);
        this.RA04 = new TGModelRenderer(this, 75, 12);
        this.RA04.setRotationPoint(-9.2F+ArmOffsetX, -3.0F+ArmOffsetY, -2.0F);
        this.RA04.addBox(0.0F, 0.0F, 0.0F, 1, 7, 4, 0.0F);
        
        this.LA03 = new TGModelRenderer(this, 68, 27);
        this.LA03.setRotationPoint(5.0F-ArmOffsetX, -1.0F+ArmOffsetY, -3.0F);
        this.LA03.addBox(0.0F, 0.0F, 0.0F, 4, 4, 6, 0.0F);
        this.LA01 = new TGModelRenderer(this, 69, 38);
        this.LA01.mirror = true;
        this.LA01.setRotationPoint(3.5F-ArmOffsetX, -0.5F+ArmOffsetY, -2.5F);
        this.LA01.addBox(0.0F, 0.0F, 0.0F, 5, 13, 5, 0.0F);
        this.LA06 = new TGModelRenderer(this, 46, 38);
        this.LA06.setRotationPoint(6.0F-ArmOffsetX, 5.0F+ArmOffsetY, 2.0F);
        this.LA06.addBox(-1.0F, -1.0F, 0.0F, 3, 4, 1, 0.0F);
        this.LA05 = new TGModelRenderer(this, 56, 38);
        this.LA05.mirror = true;
        this.LA05.setRotationPoint(9.7F-ArmOffsetX, 9.0F+ArmOffsetY, 0.0F);
        this.LA05.addBox(-1.5F, -2.5F, -2.0F, 1, 5, 4, 0.0F);
        this.LA04 = new TGModelRenderer(this, 75, 12);
        this.LA04.setRotationPoint(8.2F-ArmOffsetX, -3.0F+ArmOffsetY, -2.0F);
        this.LA04.addBox(0.0F, 0.0F, 0.0F, 1, 7, 4, 0.0F);
        
        this.B01 = new TGModelRenderer(this, 41, 19);
        this.B01.setRotationPoint(-0.5F, -0.5F, -0.5F);
        this.B01.addBox(-4.0F, 0.0F, -2.0F, 9, 6, 5, 0.0F);
        this.B02 = new TGModelRenderer(this, 48, 0);
        this.B02.setRotationPoint(-4.5F, 1.5F, -4.5F);
        this.B02.addBox(0.0F, 0.0F, 0.0F, 9, 4, 2, 0.0F);
        this.B03 = new TGModelRenderer(this, 71, 0);
        this.B03.setRotationPoint(-3.0F, 5.2F, -4.5F);
        this.B03.addBox(0.0F, 0.0F, 0.0F, 6, 7, 2, 0.0F);
        this.setRotateAngle(B03, 0.2792526803190927F, -0.0F, 0.0F);
        this.B04 = new TGModelRenderer(this, 48, 10);
        this.B04.setRotationPoint(4.5F, 1.5F, 4.0F);
        this.B04.addBox(-4.0F, 0.0F, -2.0F, 3, 5, 2, 0.0F);
        this.B05 = new TGModelRenderer(this, 48, 10);
        this.B05.setRotationPoint(0.5F, 1.5F, 4.0F);
        this.B05.addBox(-4.0F, 0.0F, -2.0F, 3, 5, 2, 0.0F);
        this.B06 = new TGModelRenderer(this, 60, 10);
        this.B06.setRotationPoint(-2.5F, 5.0F, 2.5F);
        this.B06.addBox(0.0F, 0.0F, 0.0F, 5, 5, 2, 0.0F);
        this.B07 = new TGModelRenderer(this, 50, 32);
        this.B07.setRotationPoint(-3.0F, 11.0F, 3.0F);
        this.B07.addBox(0.0F, -1.0F, -1.0F, 6, 2, 2, 0.0F);
        this.setRotateAngle(B07, 0.7853981633974483F, 0.0F, 0.0F);
	    this.B10 = new TGModelRenderer(this, 89, 27);
        this.B10.setRotationPoint(-1.0F, 0.8F, -4.8F);
        this.B10.addBox(0.0F, 0.0F, 0.0F, 2, 5, 3, 0.0F);
        this.B13 = new TGModelRenderer(this, 100, 24);
        this.B13.setRotationPoint(2.2F, 5.9F, -3.0F);
        this.B13.addBox(0.0F, 0.0F, 0.0F, 2, 3, 6, 0.0F);
        this.B14 = new TGModelRenderer(this, 100, 24);
        this.B14.mirror = true;
        this.B14.setRotationPoint(-4.2F, 5.9F, -3.0F);
        this.B14.addBox(0.0F, 0.0F, 0.0F, 2, 3, 6, 0.0F);
        this.B12 = new TGModelRenderer(this, 90, 36);
        this.B12.setRotationPoint(0.0F, 5.5F, -0.6F);
        this.B12.addBox(-4.0F, 0.0F, -2.0F, 8, 6, 5, 0.1F);
        
        this.RL01 = new TGModelRenderer(this, 0, 47);
        this.RL01.setRotationPoint(-2.5F+LegOffsetX, 11.5F+LegOffsetY, -0.5F);
        this.RL01.addBox(-2.0F, 0.0F, -2.0F, 5, 9, 5, 0.0F);
        this.RL02 = new TGModelRenderer(this, 21, 56);
        this.RL02.setRotationPoint(-2.3F+LegOffsetX, 15.5F+LegOffsetY, -3.0F);
        this.RL02.addBox(0.0F, 0.0F, 0.0F, 3, 3, 1, 0.0F);
        this.setRotateAngle(RL02, 0.0F, -0.0F, 0.7853981633974483F);
        this.RL03 = new TGModelRenderer(this, 30, 56);
        this.RL03.setRotationPoint(-4.8F+LegOffsetX, 17.5F+LegOffsetY, 0.0F);
        this.RL03.addBox(0.0F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(RL03, 0.7853981633974483F, -0.0F, 0.0F);
        
        this.LL02 = new TGModelRenderer(this, 21, 56);
        this.LL02.setRotationPoint(2.3F-LegOffsetX, 15.5F+LegOffsetY, -3.0F);
        this.LL02.addBox(0.0F, 0.0F, 0.0F, 3, 3, 1, 0.0F);
        this.setRotateAngle(LL02, 0.0F, -0.0F, 0.7853981633974483F);
        this.LL03 = new TGModelRenderer(this, 30, 56);
        this.LL03.mirror = true;
        this.LL03.setRotationPoint(3.8F-LegOffsetX, 17.5F+LegOffsetY, 0.0F);
        this.LL03.addBox(0.0F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(LL03, 0.7853981633974483F, -0.0F, 0.0F);
        this.LL01 = new TGModelRenderer(this, 0, 47);
        this.LL01.mirror = true;
        this.LL01.setRotationPoint(1.5F-LegOffsetX, 11.5F+LegOffsetY, -0.5F);
        this.LL01.addBox(-2.0F, 0.0F, -2.0F, 5, 9, 5, 0.0F);
  
        this.LB01 = new TGModelRenderer(this, 103, 0);
        this.LB01.setRotationPoint(-2.5F-LegOffsetX, 20.5F+LegOffsetY, -0.5F);
        this.LB01.addBox(2.0F, 0.0F, -2.0F, 5, 4, 5, 0.0F);
        this.LB02 = new TGModelRenderer(this, 103, 10);
        this.LB02.mirror = true;
        this.LB02.setRotationPoint(2.0F-LegOffsetX, 22.0F+LegOffsetY, -2.0F);
        this.LB02.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 2, 0.0F);
        
        this.RB01 = new TGModelRenderer(this, 103, 0);
        this.RB01.setRotationPoint(-2.5F+LegOffsetX, 20.5F+LegOffsetY, -0.5F);
        this.RB01.addBox(-2.0F, 0.0F, -2.0F, 5, 4, 5, 0.0F);
        this.RB02 = new TGModelRenderer(this, 103, 10);
        this.RB02.setRotationPoint(-2.0F+LegOffsetX, 22.0F+LegOffsetY, -2.0F);
        this.RB02.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 2, 0.0F);
      
     

       /* this.P01 = new TGModelRenderer(this, 40, 48);
        this.P01.setRotationPoint(-4.5F, 12.6F, -2.5F);
        this.P01.addBox(0.0F, 0.0F, 0.0F, 9, 2, 5, -0.3F);*/
      
        this.P03 = new TGModelRenderer(this, 21, 47);
        this.P03.setRotationPoint(-1.5F, 12.9F, -3.0F);
        this.P03.addBox(0.0F, 0.0F, 0.0F, 3, 2, 6, 0.0F);

        this.P02 = new TGModelRenderer(this, 0, 38);
        this.P02.setRotationPoint(-5.0F, 9.9F, -3.0F);
        this.P02.addBox(0.0F, 0.0F, 0.0F, 10, 3, 6, 0.0F);


		if (type == 0) {
			this.bipedLeftArm.addChild(LA01);
			//this.bipedLeftArm.addChild(LA02);
			this.bipedLeftArm.addChild(LA03);
			this.bipedLeftArm.addChild(LA04);
			this.bipedLeftArm.addChild(LA05);
			this.bipedLeftArm.addChild(LA06);

			this.bipedRightArm.addChild(RA01);
			//this.bipedRightArm.addChild(RA02);
			this.bipedRightArm.addChild(RA03);
			this.bipedRightArm.addChild(RA04);
			this.bipedRightArm.addChild(RA05);
			this.bipedRightArm.addChild(RA06);

			this.bipedHead.addChild(H01);
			this.bipedHead.addChild(H02);
			this.bipedHead.addChild(H03);
			this.bipedHead.addChild(H04);
			this.bipedHead.addChild(H05);
			this.bipedHead.addChild(H06);
			this.bipedHead.addChild(H07);
			this.bipedHead.addChild(H08);
			this.bipedHead.addChild(H09);
			this.bipedHead.addChild(H10);
			this.bipedHead.addChild(H11);
			this.bipedHead.addChild(H12);

			this.bipedBody.addChild(B01);
			this.bipedBody.addChild(B02);
			this.bipedBody.addChild(B03);
			this.bipedBody.addChild(B04);
			this.bipedBody.addChild(B05);
			this.bipedBody.addChild(B06);
			this.bipedBody.addChild(B07);
			//this.bipedBody.addChild(B08);
			//this.bipedBody.addChild(B09);
			this.bipedBody.addChild(B10);
			// this.bipedBody.addChild(B11);
			this.bipedBody.addChild(B12);
			this.bipedBody.addChild(B13);
			this.bipedBody.addChild(B14);

			this.bipedRightLeg.addChild(RB01);
			this.bipedRightLeg.addChild(RB02);

			this.bipedLeftLeg.addChild(LB01);
			this.bipedLeftLeg.addChild(LB02);

		} else {
			//this.bipedBody.addChild(P01);
			this.bipedBody.addChild(P02);
			this.bipedBody.addChild(P03);

			this.bipedRightLeg.addChild(RL01);
			this.bipedRightLeg.addChild(RL02);
			this.bipedRightLeg.addChild(RL03);
			//this.bipedRightLeg.addChild(RL04);
			//this.bipedRightLeg.addChild(RL05);

			this.bipedLeftLeg.addChild(LL01);
			this.bipedLeftLeg.addChild(LL02);
			this.bipedLeftLeg.addChild(LL03);
			//this.bipedLeftLeg.addChild(LL04);
			//this.bipedLeftLeg.addChild(LL05);
		}
	}

	private void setRotateAngle(TGModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
