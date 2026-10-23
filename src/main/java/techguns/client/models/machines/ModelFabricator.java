package techguns.client.models.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import techguns.client.models.TGModelRenderer;
import techguns.client.render.TGRenderHelper;

public class ModelFabricator extends ModelMachine {

	  public TGModelRenderer Glass;
	    public TGModelRenderer basePlate;
	    public TGModelRenderer Corner1;
	    public TGModelRenderer Corner2;
	    public TGModelRenderer Corner3;
	    public TGModelRenderer Corner4;
	    public TGModelRenderer SidePlate1;
	    public TGModelRenderer SidePlate2;
	    public TGModelRenderer SidePlate3;
	    public TGModelRenderer SidePlate4;
	    public TGModelRenderer Controller;
	    public TGModelRenderer Connection1;
	    public TGModelRenderer Connection2;
	    public TGModelRenderer Connection3;
	    public TGModelRenderer Connection4;
	    public TGModelRenderer Connection5;
	    public TGModelRenderer Connection6;
	    public TGModelRenderer Connection7;
	    public TGModelRenderer SideBarTop1;
	    public TGModelRenderer SideBarTop2;
	    public TGModelRenderer SideBarTop3;
	    public TGModelRenderer SideBarTop4;
	    public TGModelRenderer innerPlate;
	    public TGModelRenderer MidPlate;
	    public TGModelRenderer Frame1;
	    public TGModelRenderer Frame2;
	    public TGModelRenderer Frame3;
	    public TGModelRenderer Frame4;
	    public TGModelRenderer FrameTop1;
	    public TGModelRenderer FrameTop2;
	    public TGModelRenderer FrameMove1;
	    public TGModelRenderer WorkingHead;
	    public TGModelRenderer WorkingDrill1;
	    public TGModelRenderer WorkingDrill2;
	    public TGModelRenderer WorkingLaser1;
	    public TGModelRenderer WorkingLaser2;
	    public TGModelRenderer WorkingTube1;
	    public TGModelRenderer WorkingTube2;
	    public TGModelRenderer WorkingLaserBeam;

	    public ModelFabricator() {
	        this.textureWidth = 128;
	        this.textureHeight = 128;
	        this.WorkingLaser1 = new TGModelRenderer(this, 14, 102);
	        this.WorkingLaser1.setRotationPoint(-6.7F, 2.5F, 6.5F);
	        this.WorkingLaser1.addBox(-0.5F, 0.0F, -0.5F, 1, 7, 1, 0.0F);
	        this.setRotation(WorkingLaser1, 0.0F, 0.7853981633974483F, 0.0F);
	        this.Corner3 = new TGModelRenderer(this, 0, 0);
	        this.Corner3.setRotationPoint(-24.0F, 8.0F, 21.0F);
	        this.Corner3.addBox(0.0F, 0.0F, 0.0F, 3, 14, 3, 0.0F);
	        this.Connection2 = new TGModelRenderer(this, 0, 21);
	        this.Connection2.setRotationPoint(-24.0F, 16.0F, 0.0F);
	        this.Connection2.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 1, 0.0F);
	        this.setRotation(Connection2, 0.0F, 1.5707963267948966F, 0.0F);
	        this.Corner4 = new TGModelRenderer(this, 0, 0);
	        this.Corner4.setRotationPoint(5.0F, 8.0F, 21.0F);
	        this.Corner4.addBox(0.0F, 0.0F, 0.0F, 3, 14, 3, 0.0F);
	        this.Glass = new TGModelRenderer(this, 8, 86);
	        this.Glass.setRotationPoint(-8.0F, -6.0F, 8.0F);
	        this.Glass.addBox(-15.0F, 0.0F, -15.0F, 30, 12, 30, 0.0F);
	        this.Corner1 = new TGModelRenderer(this, 0, 0);
	        this.Corner1.setRotationPoint(5.0F, 8.0F, -8.0F);
	        this.Corner1.addBox(0.0F, 0.0F, 0.0F, 3, 14, 3, 0.0F);
	        this.Connection7 = new TGModelRenderer(this, 0, 21);
	        this.Connection7.setRotationPoint(8.0F, 16.0F, 16.0F);
	        this.Connection7.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 1, 0.0F);
	        this.setRotation(Connection7, 0.0F, -1.5707963267948966F, 0.0F);
	        this.FrameMove1 = new TGModelRenderer(this, 0, 71);
	        this.FrameMove1.setRotationPoint(-8.0F, -5.0F, 8.0F);
	        this.FrameMove1.addBox(-10.0F, 0.0F, -2.0F, 20, 2, 4, 0.0F);
	        this.Frame4 = new TGModelRenderer(this, 18, 0);
	        this.Frame4.setRotationPoint(3.0F, -5.0F, 18.0F);
	        this.Frame4.addBox(-1.0F, 0.0F, -2.0F, 2, 16, 4, 0.0F);
	        this.setRotation(Frame4, 0.0F, 3.141592653589793F, 0.0F);
	        this.Frame2 = new TGModelRenderer(this, 18, 0);
	        this.Frame2.setRotationPoint(-19.0F, -5.0F, 18.0F);
	        this.Frame2.addBox(-1.0F, 0.0F, -2.0F, 2, 16, 4, 0.0F);
	        this.SidePlate2 = new TGModelRenderer(this, 0, 35);
	        this.SidePlate2.setRotationPoint(-8.0F, 8.0F, 23.0F);
	        this.SidePlate2.addBox(-13.0F, 0.0F, 0.0F, 26, 14, 3, 0.0F);
	        this.setRotation(SidePlate2, 0.0F, 3.141592653589793F, 0.0F);
	        this.SidePlate1 = new TGModelRenderer(this, 0, 35);
	        this.SidePlate1.setRotationPoint(-8.0F, 8.0F, -7.0F);
	        this.SidePlate1.addBox(-13.0F, 0.0F, 0.0F, 26, 14, 3, 0.0F);
	        this.Connection3 = new TGModelRenderer(this, 0, 21);
	        this.Connection3.setRotationPoint(-24.0F, 16.0F, 16.0F);
	        this.Connection3.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 1, 0.0F);
	        this.setRotation(Connection3, 0.0F, 1.5707963267948966F, 0.0F);
	        this.SideBarTop4 = new TGModelRenderer(this, 0, 62);
	        this.SideBarTop4.setRotationPoint(8.0F, 6.0F, 8.0F);
	        this.SideBarTop4.addBox(-12.0F, 0.0F, 0.0F, 24, 2, 4, 0.0F);
	        this.setRotation(SideBarTop4, 0.0F, -1.5707963267948966F, 0.0F);
	        this.WorkingLaser2 = new TGModelRenderer(this, 0, 102);
	        this.WorkingLaser2.setRotationPoint(-6.7F, -2.5F, 6.5F);
	        this.WorkingLaser2.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 2, 0.0F);
	        this.setRotation(WorkingLaser2, 0.0F, 0.7853981633974483F, 0.0F);
	        this.SideBarTop1 = new TGModelRenderer(this, 0, 53);
	        this.SideBarTop1.setRotationPoint(-8.0F, 6.0F, -8.0F);
	        this.SideBarTop1.addBox(-16.0F, 0.0F, 0.0F, 32, 2, 4, 0.0F);
	        this.SideBarTop2 = new TGModelRenderer(this, 0, 53);
	        this.SideBarTop2.setRotationPoint(-8.0F, 6.0F, 24.0F);
	        this.SideBarTop2.addBox(-16.0F, 0.0F, 0.0F, 32, 2, 4, 0.0F);
	        this.setRotation(SideBarTop2, 0.0F, 3.141592653589793F, 0.0F);
	        this.SidePlate3 = new TGModelRenderer(this, 0, 35);
	        this.SidePlate3.setRotationPoint(-23.0F, 8.0F, 8.0F);
	        this.SidePlate3.addBox(-13.0F, 0.0F, 0.0F, 26, 14, 3, 0.0F);
	        this.setRotation(SidePlate3, 0.0F, 1.5707963267948966F, 0.0F);
	        this.FrameTop2 = new TGModelRenderer(this, 92, 35);
	        this.FrameTop2.setRotationPoint(3.0F, -4.0F, 8.0F);
	        this.FrameTop2.addBox(-1.0F, -1.0F, -8.0F, 2, 2, 16, 0.0F);
	        this.setRotation(FrameTop2, 0.0F, 3.141592653589793F, 0.0F);
	        this.WorkingTube1 = new TGModelRenderer(this, 9, 77);
	        this.WorkingTube1.setRotationPoint(-6.7F, -2.5F, 9.5F);
	        this.WorkingTube1.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2, 0.0F);
	        this.setRotation(WorkingTube1, 0.0F, 0.7853981633974483F, 0.0F);
	        this.SidePlate4 = new TGModelRenderer(this, 0, 35);
	        this.SidePlate4.setRotationPoint(7.0F, 8.0F, 8.0F);
	        this.SidePlate4.addBox(-13.0F, 0.0F, 0.0F, 26, 14, 3, 0.0F);
	        this.setRotation(SidePlate4, 0.0F, -1.5707963267948966F, 0.0F);
	        this.SideBarTop3 = new TGModelRenderer(this, 0, 62);
	        this.SideBarTop3.setRotationPoint(-24.0F, 6.0F, 8.0F);
	        this.SideBarTop3.addBox(-12.0F, 0.0F, 0.0F, 24, 2, 4, 0.0F);
	        this.setRotation(SideBarTop3, 0.0F, 1.5707963267948966F, 0.0F);
	        this.Connection1 = new TGModelRenderer(this, 0, 21);
	        this.Connection1.setRotationPoint(-16.0F, 16.0F, -8.0F);
	        this.Connection1.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 1, 0.0F);
	        this.WorkingDrill2 = new TGModelRenderer(this, 0, 85);
	        this.WorkingDrill2.setRotationPoint(-9.3F, -3.5F, 8.0F);
	        this.WorkingDrill2.addBox(-0.5F, 0.0F, -0.5F, 1, 8, 1, 0.0F);
	        this.setRotation(WorkingDrill2, 0.0F, 0.7853981633974483F, 0.0F);
	        this.WorkingTube2 = new TGModelRenderer(this, 18, 78);
	        this.WorkingTube2.setRotationPoint(-6.7F, -3.5F, 9.5F);
	        this.WorkingTube2.addBox(-0.5F, 0.0F, -0.5F, 1, 9, 1, 0.0F);
	        this.setRotation(WorkingTube2, 0.0F, 0.7853981633974483F, 0.0F);
	        this.WorkingDrill1 = new TGModelRenderer(this, 0, 77);
	        this.WorkingDrill1.setRotationPoint(-9.3F, -2.5F, 8.0F);
	        this.WorkingDrill1.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.0F);
	        this.setRotation(WorkingDrill1, 0.0F, 0.7853981633974483F, 0.0F);
	        this.innerPlate = new TGModelRenderer(this, 32, 59);
	        this.innerPlate.setRotationPoint(-8.0F, 11.0F, 8.0F);
	        this.innerPlate.addBox(-12.0F, 0.0F, -12.0F, 24, 2, 24, 0.0F);
	        this.FrameTop1 = new TGModelRenderer(this, 92, 35);
	        this.FrameTop1.setRotationPoint(-19.0F, -4.0F, 8.0F);
	        this.FrameTop1.addBox(-1.0F, -1.0F, -8.0F, 2, 2, 16, 0.0F);
	        this.Connection5 = new TGModelRenderer(this, 0, 21);
	        this.Connection5.setRotationPoint(0.0F, 16.0F, 24.0F);
	        this.Connection5.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 1, 0.0F);
	        this.setRotation(Connection5, 0.0F, 3.141592653589793F, 0.0F);
	        this.Connection6 = new TGModelRenderer(this, 0, 21);
	        this.Connection6.setRotationPoint(8.0F, 17.0F, 0.0F);
	        this.Connection6.addBox(-4.0F, -5.0F, 0.0F, 8, 8, 1, 0.0F);
	        this.setRotation(Connection6, 0.0F, -1.5707963267948966F, 0.0F);
	        this.WorkingLaserBeam = new TGModelRenderer(this, 9, 102);
	        this.WorkingLaserBeam.setRotationPoint(-6.7F, 0.5F, 6.5F);
	        this.WorkingLaserBeam.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
	        this.setRotation(WorkingLaserBeam, 0.0F, 0.7853981633974483F, 0.0F);
	        this.WorkingHead = new TGModelRenderer(this, 0, 94);
	        this.WorkingHead.setRotationPoint(-8.0F, -4.5F, 8.0F);
	        this.WorkingHead.addBox(-3.0F, 0.0F, -3.0F, 6, 2, 6, 0.0F);
	        this.basePlate = new TGModelRenderer(this, 0, 0);
	        this.basePlate.setRotationPoint(-8.0F, 22.0F, 8.0F);
	        this.basePlate.addBox(-16.0F, 0.0F, -16.0F, 32, 2, 32, 0.0F);
	        this.Connection4 = new TGModelRenderer(this, 0, 21);
	        this.Connection4.setRotationPoint(-16.0F, 16.0F, 24.0F);
	        this.Connection4.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 1, 0.0F);
	        this.setRotation(Connection4, 0.0F, 3.141592653589793F, 0.0F);
	        this.Frame3 = new TGModelRenderer(this, 18, 0);
	        this.Frame3.setRotationPoint(3.0F, -5.0F, -2.0F);
	        this.Frame3.addBox(-1.0F, 0.0F, -2.0F, 2, 16, 4, 0.0F);
	        this.setRotation(Frame3, 0.0F, 3.141592653589793F, 0.0F);
	        this.Controller = new TGModelRenderer(this, 103, 0);
	        this.Controller.setRotationPoint(-4.0F, 12.0F, -7.0F);
	        this.Controller.addBox(0.0F, 0.0F, 0.0F, 8, 8, 3, 0.0F);
	        this.setRotation(Controller, -0.1308996938995747F, 0.0F, 0.0F);
	        this.MidPlate = new TGModelRenderer(this, 58, 36);
	        this.MidPlate.setRotationPoint(-8.0F, 10.0F, 8.0F);
	        this.MidPlate.addBox(-6.0F, 0.0F, -6.0F, 12, 1, 12, 0.0F);
	        this.Corner2 = new TGModelRenderer(this, 0, 0);
	        this.Corner2.setRotationPoint(-24.0F, 8.0F, -8.0F);
	        this.Corner2.addBox(0.0F, 0.0F, 0.0F, 3, 14, 3, 0.0F);
	        this.Frame1 = new TGModelRenderer(this, 18, 0);
	        this.Frame1.setRotationPoint(-19.0F, -5.0F, -2.0F);
	        this.Frame1.addBox(-1.0F, 0.0F, -2.0F, 2, 16, 4, 0.0F);
	    }

	    
	    
	    @Override
		public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale, float progress) {
	    	
			//unanimated
		        this.innerPlate.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.FrameTop1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Connection7.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Controller.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.basePlate.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Frame2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Frame4.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Connection1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.FrameTop2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.SideBarTop1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Frame3.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.SidePlate4.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Connection5.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Corner3.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.SidePlate3.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Corner4.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Connection6.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Corner1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Connection2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Connection3.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Frame1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.MidPlate.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Connection4.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.SidePlate2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.Corner2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.SidePlate1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.SideBarTop2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.SideBarTop4.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.SideBarTop3.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        
			//animate frame, part 1	GL11.glPushMatrix();
	        	poseStack.pushPose();
	        	poseStack.translate(0f, 0f, 0.25f*Math.sin(progress*Math.PI*4));
        	
		        this.FrameMove1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        
			//animate head, part 2
		        poseStack.translate(0.125f*Math.sin(progress*Math.PI*8),0f,0f);
        		
		        this.WorkingHead.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.WorkingDrill1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.WorkingTube1.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.WorkingLaser2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        this.WorkingLaserBeam.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        
		        if (progress<0.1f || progress >0.9f) {
		        	this.WorkingDrill2.render(poseStack, buffer, packedLight, packedOverlay, scale); 
		        	this.WorkingTube2.render(poseStack, buffer, packedLight, packedOverlay, scale);
		        } else if(progress>=0.1f && progress<0.3f) {
		        	float progScaled = (progress - 0.1f)*5.0f;
        			poseStack.pushPose();
        				float offsetX=-0.575f;
        				float offsetZ=0.5f;
        			
        				poseStack.translate(offsetX,0,offsetZ);
        				TGRenderHelper.rotate(poseStack, progScaled*360*8, 0, 1, 0);
        				poseStack.translate(-offsetX,0,-offsetZ);
        				double sin = Math.sin(progScaled*Math.PI);
        				poseStack.translate(0, 0.3d*sin*sin, 0);
        				this.WorkingDrill2.render(poseStack, buffer, packedLight, packedOverlay, scale); 
        			poseStack.popPose();
        			this.WorkingTube2.render(poseStack, buffer, packedLight, packedOverlay, scale);
        			
		        } else if (progress>=0.3f && progress <0.6f){
		        	 this.WorkingDrill2.render(poseStack, buffer, packedLight, packedOverlay, scale); 
		        	 
    				float progScaled = (progress - 0.3f)* (1.0f/0.3f);
    				poseStack.pushPose();
    					double sin = Math.sin(progScaled*Math.PI*4);
    					poseStack.translate(0, 0.3d*sin*sin, 0);
    					this.WorkingTube2.render(poseStack, buffer, packedLight, packedOverlay, scale);
					poseStack.popPose();
		        } else {
			        this.WorkingDrill2.render(poseStack, buffer, packedLight, packedOverlay, scale); 
			        this.WorkingLaser1.render(poseStack, buffer, packedLight, packedOverlay, scale);
			        this.WorkingTube2.render(poseStack, buffer, packedLight, packedOverlay, scale);
    			}
		        poseStack.popPose();
		        
	        //glass is rendered last by renderGlass (translucent render type)
	    }

	    /**
	     * 1.12 rendered the glass at the end of render(), it needs a translucent buffer in 1.20
	     */
	    public void renderGlass(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float scale) {
	    	this.Glass.render(poseStack, buffer, packedLight, packedOverlay, scale);
	    }
}
